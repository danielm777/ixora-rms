// perfmon.cpp : Defines the entry point for the console application.
//

#define WIN32_LEAN_AND_MEAN		// Exclude rarely-used stuff from Windows headers
#define FORCE_UNICODE
#define UNICODE
#define _UNICODE

#include <windows.h>
#include <stdlib.h>
#include "include\pdh.h"
#include "include\pdhmsg.h"
#include <tchar.h>
#include <jni.h>
#include <Lm.h>
#include <string>

/* FOR INFO CHECK THIS OUT: http://msdn2.microsoft.com/en-us/library/aa373219.aspx */

#define JAVA_PREFIX(fncname) Java_com_ixora_rms_agents_windows_WinPerfRootEntity_##fncname

extern "C"
{
	JNIEXPORT jint JNICALL JAVA_PREFIX(ResetQuery)(JNIEnv *, jclass, jintArray, jint);

	JNIEXPORT jintArray JNICALL JAVA_PREFIX(SetCounters)(JNIEnv *, jclass, jstring, jint, jobjectArray);

	JNIEXPORT jobjectArray JNICALL JAVA_PREFIX(EnumEntities)(JNIEnv *, jclass, jstring, jstring, jstring, jstring);

	JNIEXPORT jobjectArray JNICALL JAVA_PREFIX(CollectQueryData)(JNIEnv *, jclass, jint, jintArray, jintArray);
}


void throwException(JNIEnv* env, char* szMsg)
{
	jclass excClazz = env->FindClass("com/ixora/rms/agents/windows/exception/WinPerfException");
	env->ThrowNew(excClazz, szMsg);
	throw "internal";
}

void throwJavaExceptionIfNeeded(JNIEnv *env) {
		// If this is a Java exception we threw, just let it through
		// Otherwise it's an access violation or something: convert to Java exception
		if (!env->ExceptionOccurred())
		{
			jclass excClazz = env->FindClass("com/ixora/rms/agents/windows/exception/WinPerfException");
			env->ThrowNew(excClazz, "windows.error.internal_agent_error");
		}
}

#define MAX_INSTANCE_NAME	2048

jobject makeNativeObject(JNIEnv *env, const TCHAR* szName, const TCHAR* szDescription,
						 const TCHAR* szTotalName,
						 jobjectArray instances, jobjectArray counters)
{
	jclass clazz = env->FindClass("com/ixora/rms/agents/windows/NativeObject");
	jmethodID ctor = env->GetMethodID(clazz, "<init>",
		"(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;[Lcom/ixora/rms/agents/windows/NativeInstance;[Lcom/ixora/rms/agents/windows/NativeCounter;)V");

	if (!szName) szName = _T("");
	if (!szDescription) szDescription = _T("");

	return env->NewObject(clazz, ctor,
		env->NewString((jchar*)szName, _tcslen(szName)),
		env->NewString((jchar*)szDescription, _tcslen(szDescription)),
		szTotalName ? env->NewString((jchar*)szTotalName, _tcslen(szTotalName)) : NULL,
		instances,
		counters);
}

jobject makeNativeInstance(JNIEnv *env, int id, const TCHAR* szName)
{
	jclass clazz = env->FindClass("com/ixora/rms/agents/windows/NativeInstance");
	jmethodID ctor = env->GetMethodID(clazz, "<init>",
		"(ILjava/lang/String;)V");

	if (!szName) szName = _T("");

	return env->NewObject(clazz,
		ctor,
		id,
		env->NewString((jchar*)szName, _tcslen(szName)));
}

jobject makeNativeCounter(JNIEnv *env, const TCHAR* szName, const TCHAR* szDescription, int type)
{
	jclass clazz = env->FindClass("com/ixora/rms/agents/windows/NativeCounter");
	jmethodID ctor = env->GetMethodID(clazz, "<init>",
		"(Ljava/lang/String;Ljava/lang/String;I)V");

	if (!szName) szName = _T("");
	if (!szDescription) szDescription = _T("");

	return env->NewObject(clazz,
		ctor,
		env->NewString((jchar*)szName, _tcslen(szName)),
		env->NewString((jchar*)szDescription, _tcslen(szDescription)),
		type);
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




static const int TOTALBYTES = 200 * 1024;
static const int BYTEINCREMENT = 100 * 1024;
// Reads details for all objects, instances and counters
class PerformanceInfo
{
	TCHAR* lpNameStrings;
	TCHAR* lpHelpStrings;
	TCHAR**lpNamesArray;
	PPERF_DATA_BLOCK PerfData;
	int		names;

public:
	~PerformanceInfo()
	{
		if (lpNameStrings) free(lpNameStrings);
		if (lpHelpStrings) free(lpHelpStrings);
		if (lpNamesArray) free(lpNamesArray);
		if (PerfData) free(PerfData);
	}

	PPERF_OBJECT_TYPE FirstObject()
	{ return (PPERF_OBJECT_TYPE)((PBYTE)PerfData + PerfData->HeaderLength); }

	PPERF_OBJECT_TYPE NextObject(PPERF_OBJECT_TYPE PerfObj)
	{ return (PPERF_OBJECT_TYPE)((PBYTE)PerfObj + PerfObj->TotalByteLength); }

	PPERF_INSTANCE_DEFINITION FirstInstance(PPERF_OBJECT_TYPE PerfObj)
	{ return (PPERF_INSTANCE_DEFINITION)((PBYTE)PerfObj + PerfObj->DefinitionLength); }

	PPERF_INSTANCE_DEFINITION NextInstance(PPERF_INSTANCE_DEFINITION PerfInst)
	{
		PPERF_COUNTER_BLOCK PerfCntrBlk;
		PerfCntrBlk = (PPERF_COUNTER_BLOCK)((PBYTE)PerfInst + PerfInst->ByteLength);
		return (PPERF_INSTANCE_DEFINITION)((PBYTE)PerfCntrBlk + PerfCntrBlk->ByteLength);
	}

	PPERF_COUNTER_DEFINITION FirstCounter(PPERF_OBJECT_TYPE PerfObj)
	{ return (PPERF_COUNTER_DEFINITION) ((PBYTE)PerfObj + PerfObj->HeaderLength); }

	PPERF_COUNTER_DEFINITION NextCounter(PPERF_COUNTER_DEFINITION PerfCntr)
	{ return (PPERF_COUNTER_DEFINITION)((PBYTE)PerfCntr + PerfCntr->ByteLength); }

	PerformanceInfo(JNIEnv *env, const TCHAR* szMachineName,
		const TCHAR* szDomain, const TCHAR* szUserName, const TCHAR* szPassword)
	{
		lpNameStrings = NULL;
		lpNamesArray = NULL;
		PerfData = NULL;

		HKEY hKeyPerflib;		// handle to registry key
		HKEY hKeyPerflib009;	// handle to registry key
		DWORD dwMaxValueLen = 0;// maximum size of key values
		DWORD dwBuffer = 0;		// bytes to allocate for buffers
		DWORD dwBufferSize = 0;	// size of dwBuffer
		TCHAR* lpCurrentString;	// pointer for enumerating data strings
		DWORD dwCounter;		// current counter index

		bool remoteMachine = false;
		HKEY hHKLM = NULL;
		if (szMachineName == NULL) {
			hHKLM = HKEY_LOCAL_MACHINE;
		} else {
			remoteMachine = true;
			// Connect to network machine
			TCHAR szServerResource[MAX_PATH];
			_tcscpy(szServerResource, szMachineName);
			_tcscat(szServerResource, _T("\\IPC$"));
			USE_INFO_2 u;
			memset(&u, 0, sizeof(u));
			u.ui2_local = NULL;
			u.ui2_asg_type = USE_IPC;
			u.ui2_remote = (TCHAR*)szServerResource;
			u.ui2_password = (TCHAR*)szPassword;
			u.ui2_username = (TCHAR*)szUserName;
			u.ui2_domainname = (TCHAR*)szDomain;
			DWORD errParam = 0;
			NET_API_STATUS retStat = NetUseAdd(NULL, 2, (BYTE *) &u, &errParam);

			// If this failed, we still try the following:

			// Connect network registry on HKEY_LOCAL_MACHINE
			if (ERROR_SUCCESS != RegConnectRegistry(szMachineName, HKEY_LOCAL_MACHINE, &hHKLM)) {
				throwException(env, "windows.error.remote_connect_failed");
			}
		}

		// Get the number of Counter items.
		RegOpenKeyEx(hHKLM,
			_T("SOFTWARE\\Microsoft\\Windows NT\\CurrentVersion\\Perflib"),
			0, KEY_READ, &hKeyPerflib);

		dwBufferSize = sizeof(dwBuffer);
		RegQueryValueEx( hKeyPerflib, _T("Last Counter"), NULL, NULL, (LPBYTE) &dwBuffer, &dwBufferSize );
		RegCloseKey( hKeyPerflib );

		// Allocate memory for the counter names and descriptions array.
		names = 2*(dwBuffer+1); // two strings for each counter: name and description
		lpNamesArray = (TCHAR**)malloc(names * sizeof(TCHAR*));
		for (int kk = 0; kk < names; kk++)
			lpNamesArray[kk] = NULL;

		// Open the key containing the counter and object names.
		RegOpenKeyEx( hHKLM,
			_T("SOFTWARE\\Microsoft\\Windows NT\\CurrentVersion\\Perflib\\009"),
			0, KEY_READ, &hKeyPerflib009);

		// Get the size of the largest value in the key (Counter or Help).
		RegQueryInfoKey( hKeyPerflib009, NULL, NULL, NULL, NULL, NULL, NULL, NULL,
			NULL, &dwMaxValueLen, NULL, NULL);

		// Allocate memory for the counter and object names.
		dwBuffer = dwMaxValueLen + 2;
		lpNameStrings = (TCHAR*)malloc( dwBuffer * sizeof(TCHAR) );

		// Load Counter names into the lpNamesArray
		LONG ret = RegQueryValueEx(hKeyPerflib009, _T("Counter"), NULL, NULL, (BYTE*)lpNameStrings, &dwBuffer);
		for( lpCurrentString = lpNameStrings; *lpCurrentString;
			lpCurrentString += (lstrlen(lpCurrentString)+1) )
		{
			dwCounter = _wtol( lpCurrentString );
			lpCurrentString += (lstrlen(lpCurrentString)+1);
			lpNamesArray[dwCounter] = (TCHAR*) lpCurrentString;
		}

		// Load Help descriptions into the lpNamesArray
		dwBuffer = dwMaxValueLen + 2;
		lpHelpStrings = (TCHAR*)malloc( dwBuffer * sizeof(TCHAR) );

		ret = RegQueryValueEx(hKeyPerflib009, _T("Help"), NULL, NULL, (BYTE*)lpHelpStrings, &dwBuffer);
		for( lpCurrentString = lpHelpStrings; *lpCurrentString;
			lpCurrentString += (lstrlen(lpCurrentString)+1) )
		{
			dwCounter = _wtol( lpCurrentString );
			lpCurrentString += (lstrlen(lpCurrentString)+1);
			lpNamesArray[dwCounter] = (TCHAR*) lpCurrentString;
		}

		// Read the actual performance data
		DWORD BufferSize = TOTALBYTES;

		// Allocate the buffer for the performance data and read.
		PerfData = (PPERF_DATA_BLOCK) malloc( BufferSize );

		HKEY hKeyPerfData = HKEY_PERFORMANCE_DATA; // for local
		if(remoteMachine) {
			// Connect network registry on the HKEY_PERFORMANCE_DATA key
			if (ERROR_SUCCESS != RegConnectRegistry(szMachineName, HKEY_PERFORMANCE_DATA, &hKeyPerfData)) {
				throwException(env, "windows.error.remote_connect_failed");
			}
		}

		while( RegQueryValueEx(hKeyPerfData, _T("Global"), NULL, NULL,
			(LPBYTE) PerfData, &BufferSize ) == ERROR_MORE_DATA)
		{
			BufferSize += BYTEINCREMENT;
			PerfData = (PPERF_DATA_BLOCK) realloc(PerfData, BufferSize);
		}

		RegCloseKey(hKeyPerflib009);
		RegCloseKey(hKeyPerfData);
		RegCloseKey(hHKLM);
	}

	TCHAR* GetInstanceName(int idxObject, int idxInstance)
	{
		if (idxObject == 0)
			return NULL;

		int i;

		PPERF_OBJECT_TYPE PerfObj = FirstObject();
		for (i=0; i < PerfData->NumObjectTypes; i++)
		{
			if (PerfObj->ObjectNameTitleIndex == idxObject)
				break;
			PerfObj = NextObject(PerfObj);
		}

		PPERF_INSTANCE_DEFINITION PerfInst = FirstInstance(PerfObj);
		for (i=0; i < idxInstance; i++)
			PerfInst = NextInstance(PerfInst);

		return (TCHAR*)((PBYTE)PerfInst + PerfInst->NameOffset);
	}

	int GetInstanceIndex(PPERF_OBJECT_TYPE PerfObj, int maxIndex, TCHAR* instanceName)
	{
		int count = 0;

		PPERF_INSTANCE_DEFINITION PerfInst = FirstInstance(PerfObj);
		for (int i=0; i < maxIndex; i++)
		{
			TCHAR curInName[MAX_INSTANCE_NAME];
			TCHAR* in = (TCHAR*)((PBYTE)PerfInst + PerfInst->NameOffset);

			// This instance may be a child of another instance: change the name
			if (PerfInst->ParentObjectTitleIndex != 0)
			{
				_stprintf(curInName, _T("%s/%s"),
					GetInstanceName(PerfInst->ParentObjectTitleIndex, PerfInst->ParentObjectInstance),
					in);
			}
			else
			{
				_stprintf(curInName, _T("%s"), in);
			}

			// Compare names
			if (_tcscmp(instanceName, curInName) == 0)
				count++;

			PerfInst = NextInstance(PerfInst);
		}

		return count == 0 ? -1 : count;
	}

	const TCHAR* GetName(int idx)
	{
		if (idx >= 0 && idx < names)
			return lpNamesArray[idx];
		else
			return NULL;
	}

	jobjectArray makeObjectArray(JNIEnv* env)
	{
		DWORD i, j, k;

		// Get the first object type.
		PPERF_OBJECT_TYPE PerfObj = FirstObject();

		// Create an array of NativeObjects with PerfData->NumObjectTypes items
		jobjectArray arrayObjects = env->NewObjectArray(PerfData->NumObjectTypes,
			env->FindClass("com/ixora/rms/agents/windows/NativeObject"), NULL);
		for (i=0; i < PerfData->NumObjectTypes; i++)
		{
			// Create an array of NativeCounters with PerfObj->NumCounters items
			jobjectArray arrayCounters = env->NewObjectArray(PerfObj->NumCounters,
				env->FindClass("com/ixora/rms/agents/windows/NativeCounter"), NULL);

			// Retrieve all counters
			PPERF_COUNTER_DEFINITION PerfCntr = FirstCounter(PerfObj);
			for (j=0; j < PerfObj->NumCounters; j++)
			{
				// Ignore unknown (noname) counters
				if (GetName(PerfCntr->CounterNameTitleIndex))
				{
					env->SetObjectArrayElement(arrayCounters, j,
						makeNativeCounter(env,
							GetName(PerfCntr->CounterNameTitleIndex),
							GetName(PerfCntr->CounterHelpTitleIndex),
							PerfCntr->CounterType));
				}

				PerfCntr = NextCounter(PerfCntr);
			}

			// Retrieve all instances
			TCHAR*			totalName = NULL;
			int				totalIndex = -1;
			jobjectArray	arrayInstances = NULL;

			if (PerfObj->NumInstances > 0)
			{
				// Search for the _Total instance
				PPERF_INSTANCE_DEFINITION PerfInst = FirstInstance(PerfObj);
				for (k=0; k < PerfObj->NumInstances; k++)
				{
					TCHAR* instanceName = (TCHAR*)((PBYTE)PerfInst + PerfInst->NameOffset);

					if (_tcsstr(instanceName, _T("_Total")) || _tcsstr(instanceName, _T("_Global_")))
					{
						totalName = instanceName;
						totalIndex = k;
						break;
					}

					PerfInst = NextInstance(PerfInst);
				}

				// Create an NativeInstance array with all instances except _Total
				int numInstances = PerfObj->NumInstances;
				if (totalIndex != -1)
					numInstances--;

				if (numInstances > 0)
				{
					arrayInstances = env->NewObjectArray(numInstances,
						env->FindClass("com/ixora/rms/agents/windows/NativeInstance"), NULL);

					PPERF_INSTANCE_DEFINITION PerfInst = FirstInstance(PerfObj);
					int idx = 0;
					for (k=0; k < PerfObj->NumInstances; k++)
					{
						if (k != totalIndex)
						{
							TCHAR instanceName[MAX_INSTANCE_NAME];
							TCHAR* in = (TCHAR*)((PBYTE)PerfInst + PerfInst->NameOffset);

							// This instance may be a child of another instance: change the name
							if (PerfInst->ParentObjectTitleIndex != 0)
							{
								_stprintf(instanceName, _T("%s/%s"),
									GetInstanceName(PerfInst->ParentObjectTitleIndex, PerfInst->ParentObjectInstance),
									in);
							}
							else
							{
								_stprintf(instanceName, _T("%s"), in);
							}

							// Get the index of the instance (for instances with duplicate names)
							int instanceIndex = GetInstanceIndex(PerfObj, k, instanceName);
							env->SetObjectArrayElement(arrayInstances, idx++,
								makeNativeInstance(env, instanceIndex, instanceName));
						}

						PerfInst = NextInstance(PerfInst);
					}
				}
			}
			else
			{
				// Make a difference between no instances and no _Total instance
				totalName = _T("");
			}

			// Create the object with the list of instances and counters
			// Ignore unknown(noname) objects
			if (GetName(PerfObj->ObjectNameTitleIndex))
			{
				env->SetObjectArrayElement(arrayObjects, i,
					makeNativeObject(env,
						GetName(PerfObj->ObjectNameTitleIndex),
						GetName(PerfObj->ObjectHelpTitleIndex),
						totalName,
						arrayInstances, arrayCounters));
			}

			PerfObj = NextObject( PerfObj );
		}

		return arrayObjects;
	}

};

// Opens a new query and returns its handle. Also removes old counters and
// closes the old query, if any
JNIEXPORT jint JNICALL JAVA_PREFIX(ResetQuery)
  (JNIEnv *env, jclass clazz, jintArray nativeHandles, jint nativeQuery)
{
	try
	{
		PDH_STATUS  status;

		// Remove the old handles, if any
		if (nativeHandles != NULL)
		{
			jsize cntCount = env->GetArrayLength(nativeHandles);
			jint* hCounters = env->GetIntArrayElements(nativeHandles, NULL);
			for (int i = 0; i < cntCount; i++)
				PdhRemoveCounter((HCOUNTER)hCounters[i]);
			env->ReleaseIntArrayElements(nativeHandles, hCounters, 0);
		}

		// Close the old query, if any
		if((void*)nativeQuery != NULL) {
			PdhCloseQuery((HQUERY)nativeQuery);
		}

		// Create the new query
		HQUERY hQuery;
		status = PdhOpenQuery(NULL, 0, &hQuery);

		return (jint)hQuery;
	}
	catch (...)
	{
		throwJavaExceptionIfNeeded(env);
		return 0;
	}
}

JNIEXPORT jobjectArray JNICALL JAVA_PREFIX(EnumEntities)
  (JNIEnv *env, jclass clazz, jstring machine, jstring userName, jstring domain, jstring password)
{
	try
	{
		const TCHAR *szMachineName = NULL;
		if (machine != NULL)
			szMachineName = (const TCHAR*)env->GetStringChars(machine, 0);

		const TCHAR *szDomain = NULL;
		if (domain != NULL)
			szDomain = (const TCHAR*)env->GetStringChars(domain, 0);

		const TCHAR *szUserName = NULL;
		if (userName != NULL)
			szUserName = (const TCHAR*)env->GetStringChars(userName, 0);

		const TCHAR *szPassword = NULL;
		if (password != NULL)
			szPassword = (const TCHAR*)env->GetStringChars(password, 0);

		PerformanceInfo pi(env, szMachineName, szDomain, szUserName, szPassword);
		jobjectArray retVal = pi.makeObjectArray(env);

		if (machine != NULL)
			env->ReleaseStringChars(machine, (jchar*)szMachineName);

		if (domain != NULL)
			env->ReleaseStringChars(domain, (jchar*)szDomain);

		if (userName != NULL)
			env->ReleaseStringChars(userName, (jchar*)szUserName);

		if (password != NULL)
			env->ReleaseStringChars(password, (jchar*)szPassword);

		return retVal;
	}
	catch (...)
	{
		throwJavaExceptionIfNeeded(env);
		return NULL;
	}
}

JNIEXPORT jintArray JNICALL JAVA_PREFIX(SetCounters)
  (JNIEnv *env, jclass clazz, jstring machine, jint nativeQuery, jobjectArray counters)
{
	try
	{
		PDH_STATUS  status;

		// Return immediately if no counters
		if (counters == NULL)
			return NULL;
		jsize cntCount = env->GetArrayLength(counters);
		if (cntCount < 1)
			return NULL;

		// TODO: machine name is ignored for the moment
		const TCHAR *szMachineName = NULL;
		if (machine != NULL)
			szMachineName = (const TCHAR*)env->GetStringChars(machine, 0);


		// Extract all counter names from the array and add them to PDH
		jintArray retVal = env->NewIntArray(cntCount);
		jint* hCounters = env->GetIntArrayElements(retVal, NULL);

		for (int i = 0; i < cntCount; i++)
		{
			// Get counter name
			jstring obj = (jstring)env->GetObjectArrayElement(counters, i);
			const TCHAR* counterName = (const TCHAR*)env->GetStringChars(obj, 0);

			// Add it to PDH query
			status = PdhAddCounter((HQUERY)nativeQuery, counterName, 0, (HCOUNTER*)&hCounters[i]);

			env->ReleaseStringChars(obj, (jchar*)counterName);
		}

		env->ReleaseIntArrayElements(retVal, hCounters, 0);


		// Make a first call to collect initial data (required for rate counters)
		PdhCollectQueryData((HQUERY)nativeQuery);

		if (machine != NULL)
			env->ReleaseStringChars(machine, (jchar*)szMachineName);

		return retVal;
	}
	catch (...)
	{
		throwJavaExceptionIfNeeded(env);
		return NULL;
	}
}

JNIEXPORT jobjectArray JNICALL JAVA_PREFIX(CollectQueryData)
  (JNIEnv *env, jclass clazz, jint nativeQuery, jintArray nativeHandles, jintArray nativeFormats)

{
	try
	{
		PDH_STATUS  status;

		// Return immediately if no counters
		if (nativeHandles == NULL)
			return NULL;
		jsize cntCount = env->GetArrayLength(nativeHandles);
		if (cntCount < 1)
			return NULL;

		// Get Java arrays into native arrays
		jint* hCounters = env->GetIntArrayElements(nativeHandles, NULL);
		jint* fmtCounters = env->GetIntArrayElements(nativeFormats, NULL);

		// Get the data
		jobjectArray retVal = env->NewObjectArray(cntCount, env->FindClass("java/lang/Object"), NULL);
		PdhCollectQueryData((HQUERY)nativeQuery);
		for (int i = 0; i < cntCount; i++)
		{
			PDH_FMT_COUNTERVALUE	val;
			status = PdhGetFormattedCounterValue((HCOUNTER)hCounters[i], fmtCounters[i], NULL, &val);

			jobject objVal;

			switch (fmtCounters[i])
			{
				case PDH_FMT_UNICODE :
				{
					objVal = makeCounterValueString(env, val.WideStringValue);
					break;
				}
				case PDH_FMT_LONG :
				{
					objVal = makeCounterValueDouble(env, val.longValue);
					break;
				}
				case PDH_FMT_DOUBLE :
				{
					objVal = makeCounterValueDouble(env, val.doubleValue);
					break;
				}
				case PDH_FMT_LARGE : // TODO: possible loss of data?
				{
					objVal = makeCounterValueDouble(env, val.largeValue);
					break;
				}
				default: // Integer
				{
					objVal = makeCounterValueDouble(env, val.longValue);
					break;
				}
			}

			env->SetObjectArrayElement(retVal, i, objVal);
		}

		env->ReleaseIntArrayElements(nativeFormats, fmtCounters, JNI_ABORT);
		env->ReleaseIntArrayElements(nativeHandles, hCounters, JNI_ABORT);

		return retVal;
	}
	catch (...)
	{
		throwJavaExceptionIfNeeded(env);
		return NULL;
	}
}
