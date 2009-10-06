#define _WIN32_DCOM
#define FORCE_UNICODE
#define UNICODE
#define _UNICODE
#include <iostream>
using namespace std;
#include <comdef.h>
#include <Wbemidl.h>
#include <tchar.h>
#include <jni.h>
#include <list>

# pragma comment(lib, "wbemuuid.lib")


#define JAVA_PREFIX(fncname) Java_com_ixora_rms_agents_wmi_WMIRootEntity_##fncname

extern "C" {
	JNIEXPORT jint JNICALL JAVA_PREFIX(ConnectServer)
		(JNIEnv *env, jclass clazz, jstring serverAndNamespace, jstring userName, jstring password);

	JNIEXPORT jobjectArray JNICALL JAVA_PREFIX(GetClassInstances)
		(JNIEnv *env, jclass clazz, jint nativeHandle, jstring className);

	JNIEXPORT jobjectArray JNICALL JAVA_PREFIX(GetAssociatedInstances)
		(JNIEnv *env, jclass clazz, jint nativeHandle, jstring className,
		 jstring escapedInstanceName);

	JNIEXPORT jobjectArray JNICALL JAVA_PREFIX(GetClassProperties)
		(JNIEnv *env, jclass clazz, jint nativeHandle, jstring className);

	JNIEXPORT jobjectArray JNICALL JAVA_PREFIX(GetCounterValues)
		(JNIEnv *env, jclass clazz, jint nativeHandle, jstring className,
		jstring instanceName, jobjectArray counterNames);

	JNIEXPORT void JNICALL JAVA_PREFIX(CloseHandle)
		(JNIEnv *env, jclass clazz, jint nativeHandle);
}

void throwException(JNIEnv* env, char* szMsg)
{
	jclass excClazz = env->FindClass("com/ixora/rms/agents/wmi/exceptions/WMIAgentException");
	env->ThrowNew(excClazz, szMsg);
	throw "internal";
}

typedef struct
{
	jstring name;
	LONG type;
} SNativeProperty;

typedef struct
{
	jstring className;
	jstring instanceName;
} SNativeRelationship;

jobject makeNativeProperty(JNIEnv *env, jstring& name, jstring& description, int cimType)
{
	jclass clazz = env->FindClass("com/ixora/rms/agents/wmi/NativeWMIProperty");
	jmethodID ctor = env->GetMethodID(clazz, "<init>",
		"(Ljava/lang/String;Ljava/lang/String;I)V");

	int counterType = 0; // TYPE_DOUBLE
	switch (cimType)
	{
		case CIM_BOOLEAN: // 0xB
		case CIM_EMPTY: // 0x0
		case CIM_SINT8: // 0x10
		case CIM_UINT8: // 0x11
		case CIM_CHAR16: // 0x67
		case CIM_SINT16: // 0x2
		case CIM_UINT16: // 0x12
		case CIM_SINT32: // 0x3
		case CIM_UINT32: // 0x13
		case CIM_REAL32: // 0x4
			counterType = 0; break; // DOUBLE

		case CIM_SINT64: // 0x14
		case CIM_UINT64: // 0x15
		case CIM_REAL64: // 0x5
		case CIM_DATETIME: // 0x65
			counterType = 1; break; // LONG

		case CIM_STRING: // 0x8
			counterType = 2; break; // STRING

		// Other values
		case CIM_FLAG_ARRAY: // 0x2000
		case CIM_ILLEGAL : // 0xFFF
		case CIM_REFERENCE: // 0x66
		case CIM_OBJECT: // 0xD
			break;
	}

	return env->NewObject(clazz,
		ctor,
		name,
		description,
		counterType);
}

jobject makeNativeRelationship(JNIEnv *env, jstring& className, jstring& instanceName)
{
	jclass clazz = env->FindClass("com/ixora/rms/agents/wmi/NativeWMIRelationship");
	jmethodID ctor = env->GetMethodID(clazz, "<init>",
		"(Ljava/lang/String;Ljava/lang/String;)V");

	return env->NewObject(clazz,
		ctor,
		className,
		instanceName);
}

jobject makeCounterValueDouble(JNIEnv *env, double value)
{
	jclass clazz = env->FindClass("com/ixora/rms/data/CounterValueDouble");
	jmethodID ctor = env->GetMethodID(clazz, "<init>", "(D)V");

	return env->NewObject(clazz, ctor, value);
}

jobject makeCounterValueString(JNIEnv *env, const TCHAR* value)
{
	jclass clazz = env->FindClass("com/ixora/rms/data/CounterValueString");
	jmethodID ctor = env->GetMethodID(clazz, "<init>", "(Ljava/lang/String;)V");

	return env->NewObject(clazz, ctor, env->NewString((jchar*)value, _tcslen(value)));
}

jobject makeCounterValue(JNIEnv *env, VARIANT& varValue)
{
	switch (varValue.vt)
	{
		case VT_I1:
		case VT_UI1:
			return makeCounterValueDouble(env, varValue.boolVal);
		case VT_I2:
		case VT_UI2:
			return makeCounterValueDouble(env, varValue.intVal);
		case VT_I4:
		case VT_UI4:
			return makeCounterValueDouble(env, varValue.intVal);
		case VT_R4:
			return makeCounterValueDouble(env, varValue.fltVal);
		case VT_R8:
			return makeCounterValueDouble(env, varValue.dblVal);
		case VT_BSTR:
			return makeCounterValueString(env, varValue.bstrVal);
		case VT_NULL:
			return makeCounterValueString(env, L"");
	}

	return makeCounterValueDouble(env, varValue.intVal);
}

// ==================================================================================

JNIEXPORT jint JNICALL JAVA_PREFIX(ConnectServer)
	(JNIEnv *env, jclass clazz, jstring serverAndNamespace, jstring userName, jstring password)
{
	try
	{
		HRESULT hres;

		// Extract parameters
		const wchar_t *wszServerAndNamespace = NULL;
		if (serverAndNamespace != NULL)
			wszServerAndNamespace = (wchar_t *)env->GetStringChars(serverAndNamespace, 0);

		const wchar_t *wszUserName = NULL;
		if (userName != NULL)
			wszUserName = (wchar_t *)env->GetStringChars(userName, 0);

		const wchar_t *wszPassword = NULL;
		if (password != NULL)
			wszPassword = (wchar_t *)env->GetStringChars(password, 0);

		// Step 1: --------------------------------------------------
		// Initialize COM. ------------------------------------------

		hres =  CoInitializeEx(0, COINIT_MULTITHREADED);
		if (FAILED(hres))
			throwException(env, "wmi.error.internal_error");

		// Step 2: --------------------------------------------------
		// Set general COM security levels --------------------------
		// Note: If you are using Windows 2000, you need to specify -
		// the default authentication credentials for a user by using
		// a SOLE_AUTHENTICATION_LIST structure in the pAuthList ----
		// parameter of CoInitializeSecurity ------------------------

		hres =  CoInitializeSecurity(
			NULL,
			-1,                          // COM authentication
			NULL,                        // Authentication services
			NULL,                        // Reserved
			RPC_C_AUTHN_LEVEL_DEFAULT,   // Default authentication
			RPC_C_IMP_LEVEL_IMPERSONATE, // Default Impersonation
			NULL,                        // Authentication info
			EOAC_NONE,                   // Additional capabilities
			NULL                         // Reserved
			);

		// Step 3: ---------------------------------------------------
		// Obtain the initial locator to WMI -------------------------

		IWbemLocator *pLoc = NULL;

		hres = CoCreateInstance(
			CLSID_WbemLocator,
			0,
			CLSCTX_INPROC_SERVER,
			IID_IWbemLocator, (LPVOID *) &pLoc);

		if (FAILED(hres))
			throwException(env, "wmi.error.wmi_not_initialized");

		// Step 4: -----------------------------------------------------
		// Connect to WMI through the IWbemLocator::ConnectServer method

		IWbemServices *pSvc = NULL;

		// Connect to the root\cimv2 namespace with
		// the current user and obtain pointer pSvc
		// to make IWbemServices calls.
		hres = pLoc->ConnectServer(
			 (wchar_t* const)wszServerAndNamespace, // Object path of WMI namespace
			 (wchar_t* const)(wcslen(wszUserName) > 0 ? wszUserName : NULL), // User name. NULL = current user
			 (wchar_t* const)(wcslen(wszPassword) > 0 ? wszPassword : NULL), // User password. NULL = current
			 0,                       // Locale. NULL indicates current
			 NULL,                    // Security flags.
			 0,                       // Authority (e.g. Kerberos)
			 0,                       // Context object
			 &pSvc                    // pointer to IWbemServices proxy
			 );

		if (FAILED(hres))
		{
			pLoc->Release();
			throwException(env, "wmi.error.could_not_connect");
		}

		cout << "Connected to ROOT\\CIMV2 WMI namespace" << endl;


		// Step 5: --------------------------------------------------
		// Set security levels on the proxy -------------------------

		hres = CoSetProxyBlanket(
		   pSvc,                        // Indicates the proxy to set
		   RPC_C_AUTHN_WINNT,           // RPC_C_AUTHN_xxx
		   RPC_C_AUTHZ_NONE,            // RPC_C_AUTHZ_xxx
		   NULL,                        // Server principal name
		   RPC_C_AUTHN_LEVEL_CALL,      // RPC_C_AUTHN_LEVEL_xxx
		   RPC_C_IMP_LEVEL_IMPERSONATE, // RPC_C_IMP_LEVEL_xxx
		   NULL,                        // client identity
		   EOAC_NONE                    // proxy capabilities
		);

		if (FAILED(hres))
		{
			pSvc->Release();
			pLoc->Release();
			throwException(env, "wmi.error.internal_error");
		}

		// Free stuff
		if (serverAndNamespace != NULL)
			env->ReleaseStringChars(serverAndNamespace, (jchar*)wszServerAndNamespace);

		if (userName != NULL)
			env->ReleaseStringChars(userName, (jchar*)wszUserName);

		if (password != NULL)
			env->ReleaseStringChars(password, (jchar*)wszPassword);

		// Return the pointer to IWbemServices, it will be passed back to us later
		return (DWORD)pSvc;
	}
	catch (...)
	{
		// If this is a Java exception we threw, just let it through
		// Otherwise it's an access violation or something: convert to Java exception
		if (!env->ExceptionOccurred())
		{
			jclass excClazz = env->FindClass("com/ixora/rms/agents/wmi/exceptions/WMIAgentException");
			env->ThrowNew(excClazz, "wmi.error.internal_error");
		}

		return 0;
	}
}

JNIEXPORT jobjectArray JNICALL JAVA_PREFIX(GetClassInstances)
	(JNIEnv *env, jclass clazz, jint nativeHandle, jstring className)
{
	try
	{
		HRESULT hres;

		// The handle is actually the IWbemServices pointer
		IWbemServices *pSvc = (IWbemServices *)nativeHandle;

		// Extract parameters
		const wchar_t *wszClassName = NULL;
		if (className != NULL)
			wszClassName = (wchar_t *)env->GetStringChars(className, 0);

		// Enumerate instances of the specified class
		IEnumWbemClassObject* pEnumerator = NULL;
		hres = pSvc->ExecQuery(
			bstr_t("WQL"),
			bstr_t("SELECT Name FROM ") + _bstr_t(wszClassName),
			WBEM_FLAG_FORWARD_ONLY | WBEM_FLAG_RETURN_IMMEDIATELY,
			NULL,
			&pEnumerator);

		if (FAILED(hres))
			throwException(env, "wmi.error.internal_error");

		// Get the data from the query

		IWbemClassObject *pclsObj = NULL;
		ULONG uReturn = 0;

		std::list<jstring>	listInstances;

		// Limit maximum results to 1000 to avoid huge delays
		while (pEnumerator && listInstances.size() < 1000)
		{
			HRESULT hr = pEnumerator->Next(WBEM_INFINITE, 1,
				&pclsObj, &uReturn);

			if(0 == uReturn)
			{
				break;
			}

			VARIANT vtProp;
			VariantInit(&vtProp);

			// Get the value of the Name property
			hr = pclsObj->Get(L"Name", 0, &vtProp, 0, 0);

			// Put it in the ouput vector
			if (vtProp.vt == VT_BSTR)
				listInstances.push_back(
					env->NewString((jchar *)vtProp.bstrVal, wcslen(vtProp.bstrVal)));

			VariantClear(&vtProp);

			if (pclsObj)
				pclsObj->Release();
		}

		// Make output vector from the list of instance names
		jobjectArray arrayObjects = env->NewObjectArray(listInstances.size(),
			env->FindClass("java/lang/String"), NULL);

		int i = 0;
		for (std::list<jstring>::iterator it = listInstances.begin();
			it != listInstances.end(); ++it, ++i)
		{
			env->SetObjectArrayElement(arrayObjects, i, *it);
		}


		// Cleanup

		if (pEnumerator)
			pEnumerator->Release();

		if (className != NULL)
			env->ReleaseStringChars(className, (jchar*)wszClassName);

		return arrayObjects;
	}
	catch (...)
	{
		// If this is a Java exception we threw, just let it through
		// Otherwise it's an access violation or something: convert to Java exception
		if (!env->ExceptionOccurred())
		{
			jclass excClazz = env->FindClass("com/ixora/rms/agents/wmi/exceptions/WMIAgentException");
			env->ThrowNew(excClazz, "wmi.error.internal_error");
		}

		return NULL;
	}
}

JNIEXPORT jobjectArray JNICALL JAVA_PREFIX(GetAssociatedInstances)
	(JNIEnv *env, jclass clazz, jint nativeHandle, jstring className,
	jstring instanceName)
{
	try
	{
		HRESULT hres;

		// The handle is actually the IWbemServices pointer
		IWbemServices *pSvc = (IWbemServices *)nativeHandle;

		// Extract parameters
		const wchar_t *wszClassName = NULL;
		if (className != NULL)
			wszClassName = (wchar_t *)env->GetStringChars(className, 0);

		const wchar_t *wszInstanceName = NULL;
		if (instanceName != NULL)
			wszInstanceName = (wchar_t *)env->GetStringChars(instanceName, 0);


		// Enumerate instances of the specified class
		_bstr_t bstrQuery = "ASSOCIATORS OF {";
		bstrQuery += wszClassName;
		bstrQuery += "='";
		bstrQuery += wszInstanceName;
		bstrQuery += "'}";

		IEnumWbemClassObject* pEnumerator = NULL;
		hres = pSvc->ExecQuery(
			bstr_t("WQL"),
			bstrQuery,
			WBEM_FLAG_FORWARD_ONLY | WBEM_FLAG_RETURN_IMMEDIATELY,
			NULL,
			&pEnumerator);

		if (FAILED(hres))
			throwException(env, "wmi.error.internal_error");

		// Get the data from the query

 		IWbemClassObject *pclsObj = NULL;
		ULONG uReturn = 0;

		std::list<SNativeRelationship>	listInstances;

		// Limit maximum results to 1000 to avoid huge delays
		while (pEnumerator && listInstances.size() < 1000)
		{
			HRESULT hr = pEnumerator->Next(WBEM_INFINITE, 1,
				&pclsObj, &uReturn);

			if(0 == uReturn)
			{
				break;
			}

			VARIANT vtProp;
			SNativeRelationship	rel;

			// Get the value of the Name property
			VariantInit(&vtProp);
			hr = pclsObj->Get(L"Name", 0, &vtProp, 0, 0);
			rel.instanceName = env->NewString((jchar*)vtProp.bstrVal, wcslen(vtProp.bstrVal));
			VariantClear(&vtProp);

			// Get the value of the Class property
			hr = pclsObj->Get(L"__Class", 0, &vtProp, 0, 0);
			rel.className = env->NewString((jchar*)vtProp.bstrVal, wcslen(vtProp.bstrVal));
			VariantClear(&vtProp);

			// Put it in the output vector
			listInstances.push_back(rel);

			if (pclsObj)
				pclsObj->Release();
		}

		// Make output vector from the list of instance names
		jobjectArray arrayObjects = env->NewObjectArray(listInstances.size(),
			env->FindClass("java/lang/Object"), NULL);

		int i = 0;
		for (std::list<SNativeRelationship>::iterator it = listInstances.begin();
			it != listInstances.end(); ++it, ++i)
		{
			env->SetObjectArrayElement(arrayObjects, i,
				makeNativeRelationship(env, it->className, it->instanceName));
		}


		// Cleanup

		if (pEnumerator)
			pEnumerator->Release();

		if (className != NULL)
			env->ReleaseStringChars(className, (jchar*)wszClassName);

		if (instanceName != NULL)
			env->ReleaseStringChars(className, (jchar*)wszInstanceName);

		return arrayObjects;
	}
	catch (...)
	{
		// If this is a Java exception we threw, just let it through
		// Otherwise it's an access violation or something: convert to Java exception
		if (!env->ExceptionOccurred())
		{
			jclass excClazz = env->FindClass("com/ixora/rms/agents/wmi/exceptions/WMIAgentException");
			env->ThrowNew(excClazz, "wmi.error.internal_error");
		}

		return NULL;
	}
}

JNIEXPORT jobjectArray JNICALL JAVA_PREFIX(GetClassProperties)
	(JNIEnv *env, jclass clazz, jint nativeHandle, jstring className)
{
	try
	{
		HRESULT hres;

		// The handle is actually the IWbemServices pointer
		IWbemServices *pSvc = (IWbemServices *)nativeHandle;

		// Extract parameters
		const wchar_t *wszClassName = NULL;
		if (className != NULL)
			wszClassName = (wchar_t *)env->GetStringChars(className, 0);

		std::list<SNativeProperty>	listProperties;

		// Call IWbemServices->GetObject and then IWbemClassObject::BeginEnumeration
		IWbemClassObject *pObj = NULL;
		hres = pSvc->GetObject((wchar_t* const)wszClassName, 0, 0, &pObj, 0);
		if (hres != WBEM_E_NOT_FOUND)
		{
			if (FAILED(hres))
				throwException(env, "wmi.error.internal_error");

			hres = pObj->BeginEnumeration(0);

			// Get the name of each property and put it in a list
			while (pObj)
			{
				BSTR bstrPropertyName;
				LONG cimType;
				LONG flavour;

				hres = pObj->Next(0, &bstrPropertyName, NULL, &cimType, &flavour);
				if (FAILED(hres) || bstrPropertyName == NULL)
					break;

				// Skip system properties
				if (!(flavour & WBEM_FLAVOR_ORIGIN_SYSTEM))
				{
					SNativeProperty nativeProperty;
					nativeProperty.name = env->NewString((jchar*)bstrPropertyName, wcslen(bstrPropertyName));
					nativeProperty.type = cimType;

					listProperties.push_back(nativeProperty);
				}

				SysFreeString(bstrPropertyName);
			}

			hres = pObj->EndEnumeration();
		}

		// Make output vector from the list of instance names
		jobjectArray arrayObjects = env->NewObjectArray(listProperties.size(),
			env->FindClass("com/ixora/rms/agents/wmi/NativeWMIProperty"), NULL);

		int i = 0;
		for (std::list<SNativeProperty>::iterator it = listProperties.begin();
			it != listProperties.end(); ++it, ++i)
		{
			env->SetObjectArrayElement(arrayObjects, i,
				makeNativeProperty(env, it->name, it->name, it->type));
		}


		// Cleanup

		if (className != NULL)
			env->ReleaseStringChars(className, (jchar*)wszClassName);

		return arrayObjects;
	}
	catch (...)
	{
		// If this is a Java exception we threw, just let it through
		// Otherwise it's an access violation or something: convert to Java exception
		if (!env->ExceptionOccurred())
		{
			jclass excClazz = env->FindClass("com/ixora/rms/agents/wmi/exceptions/WMIAgentException");
			env->ThrowNew(excClazz, "wmi.error.internal_error");
		}

		return NULL;
	}
}

JNIEXPORT jobjectArray JNICALL JAVA_PREFIX(GetCounterValues)
	(JNIEnv *env, jclass clazz, jint nativeHandle, jstring className,
	jstring instanceName, jobjectArray counterNames)
{
	try
	{
		HRESULT hres;

		// The handle is actually the IWbemServices pointer
		IWbemServices *pSvc = (IWbemServices *)nativeHandle;

		// Extract parameters
		const wchar_t *wszClassName = NULL;
		if (className != NULL)
			wszClassName = (wchar_t *)env->GetStringChars(className, 0);

		const wchar_t *wszInstanceName = NULL;
		if (instanceName != NULL)
			wszInstanceName = (wchar_t *)env->GetStringChars(instanceName, 0);

		// Make the output array as long as the counter names array
		jsize cntCount = env->GetArrayLength(counterNames);
		jobjectArray arrayObjects = env->NewObjectArray(cntCount,
			env->FindClass("java/lang/Object"), NULL);

		_bstr_t bstrQuery = "SELECT * FROM ";
		bstrQuery += wszClassName;
		bstrQuery += " WHERE Name='";
		bstrQuery += wszInstanceName;
		bstrQuery += "'";

		IEnumWbemClassObject* pEnumerator = NULL;
		hres = pSvc->ExecQuery(
			bstr_t("WQL"),
			bstrQuery,
			WBEM_FLAG_FORWARD_ONLY | WBEM_FLAG_RETURN_IMMEDIATELY,
			NULL,
			&pEnumerator);

		if (FAILED(hres))
			throwException(env, "wmi.error.internal_error");

		// Get the data from the query

 		IWbemClassObject *pObj = NULL;
		ULONG uReturn = 0;
		hres = pEnumerator->Next(WBEM_INFINITE, 1, &pObj, &uReturn);

		if (0 != uReturn)
		{
			// Get the value of each property
			for (int i=0; i<cntCount; i++)
			{
				VARIANT vtProp;
				VariantInit(&vtProp);

				// Convert counter name from jstring to wchar_t
				jstring jcounterName = (jstring)env->GetObjectArrayElement(counterNames, i);
				const wchar_t *wszCounterName = NULL;
				wszCounterName = (wchar_t *)env->GetStringChars(jcounterName, 0);

				// Get the property value
				hres = pObj->Get(wszCounterName, 0, &vtProp, 0, 0);
				env->SetObjectArrayElement(arrayObjects, i, makeCounterValue(env, vtProp));

				// Cleanup temporary variables
				env->ReleaseStringChars(jcounterName, (jchar*)wszCounterName);
				VariantClear(&vtProp);
			}
		}

		// Cleanup

		if (pObj)
			pObj->Release();

		if (className != NULL)
			env->ReleaseStringChars(className, (jchar*)wszClassName);

		if (instanceName != NULL)
			env->ReleaseStringChars(className, (jchar*)wszInstanceName);

		return arrayObjects;

	}
	catch (...)
	{
		// If this is a Java exception we threw, just let it through
		// Otherwise it's an access violation or something: convert to Java exception
		if (!env->ExceptionOccurred())
		{
			jclass excClazz = env->FindClass("com/ixora/rms/agents/wmi/exceptions/WMIAgentException");
			env->ThrowNew(excClazz, "wmi.error.internal_error");
		}

		return NULL;
	}
}

JNIEXPORT void JNICALL JAVA_PREFIX(CloseHandle)
	(JNIEnv *env, jclass clazz, jint nativeHandle)
{
	try
	{
		// The handle is actually the IWbemServices pointer
		IWbemServices *pSvc = (IWbemServices *)nativeHandle;
		if (pSvc)
			pSvc->Release();
	}
	catch (...)
	{
		// If this is a Java exception we threw, just let it through
		// Otherwise it's an access violation or something: convert to Java exception
		if (!env->ExceptionOccurred())
		{
			jclass excClazz = env->FindClass("com/ixora/rms/agents/wmi/exceptions/WMIAgentException");
			env->ThrowNew(excClazz, "wmi.error.internal_error");
		}
	}
}
