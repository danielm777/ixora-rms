
#include "snapshot.h"
#include "db2mon.h"

// special character used by Java code to split an entity path into subentities
#define SEP std::string("`")

jobject makeCounterValueDouble(JNIEnv *env, double value)
{
	jclass clazz = env->FindClass("com/ixora/rms/data/CounterValueDouble");
	jmethodID ctor = env->GetMethodID(clazz, "<init>", "(D)V");

	return env->NewObject(clazz, ctor, value);
}

jobject makeCounterValueString(JNIEnv *env, const char* value, int len)
{
	jclass clazz = env->FindClass("com/ixora/rms/data/CounterValueString");
	jmethodID ctor = env->GetMethodID(clazz, "<init>", "(Ljava/lang/String;)V");

	// Convert to unicode, using a self-cleaning buffer
	a2w			objValue(value, len);
	wchar_t*	wszValue = objValue.wget();

	return env->NewObject(clazz, ctor, env->NewString((const jchar*)wszValue, len));
}

// Creates a new counter and adds it as the child of the specified entity
void addCounterToEntity(JNIEnv *env, jobject parentEntity, const char* name, jobject counterValue)
{
	// Ignore unknown counters
	if (name == NULL)
		return;

	// Convert to unicode, using a self-cleaning buffer
	a2w			objName(name);
	wchar_t*	wszName = objName.wget();

	// Find NativeCounter's constructor
	jclass clazz = env->FindClass("com/ixora/rms/agents/db2/DB2RootEntity$NativeCounter");
	jmethodID ctor = env->GetMethodID(clazz, "<init>",
		"(Ljava/lang/String;Lcom/ixora/rms/data/CounterValue;)V");

	// Create the NativeCounter
	jobject counter = env->NewObject(clazz, ctor,
		env->NewString((const jchar*)wszName, _tcslen(wszName)), counterValue);

	// Find the add method of the NativeEntity object
	clazz = env->FindClass("com/ixora/rms/agents/db2/DB2RootEntity$NativeEntity");
	jmethodID addCounter = env->GetMethodID(clazz, "addCounter",
		"(Lcom/ixora/rms/agents/db2/DB2RootEntity$NativeCounter;)V");

	// Use the add method to add the new counter
	env->CallVoidMethod(parentEntity, addCounter, counter);
}

// Creates a new entity and adds it as the child of the specified one
// Returns the new entity
jobject addEntityToEntity(JNIEnv *env, jobject parentEntity, const char* name)
{
	// Ignore unknown entities
	if (name == NULL)
		return parentEntity;

	// Convert to unicode, using a self-cleaning buffer
	a2w			objName(name);
	wchar_t*	wszName = objName.wget();

	// Find NativeEntity's constructor
	jclass clazz = env->FindClass("com/ixora/rms/agents/db2/DB2RootEntity$NativeEntity");
	jmethodID ctor = env->GetMethodID(clazz, "<init>",
		"(Ljava/lang/String;)V");

	// Create the NativeEntity
	jobject entity = env->NewObject(clazz, ctor, env->NewString((const jchar*)wszName, _tcslen(wszName)));

	if (parentEntity != NULL)
	{
		// Find the add method of the NativeEntity object
		jmethodID addEntity = env->GetMethodID(clazz, "addEntity",
			"(Lcom/ixora/rms/agents/db2/DB2RootEntity$NativeEntity;)V");

		// Use the add method to add the new Entity
		env->CallVoidMethod(parentEntity, addEntity, entity);
	}

	return entity;
}

/**************************************************************/

Snapshot::Snapshot(JNIEnv* env)
{
	initElementNames();

	// The root entity is not visible to the end user
	rootEntity = addEntityToEntity(env, NULL, "root");

	// System entity is actually the root SQLM_ELM_COLLECTED
	systemEntity = NULL;
}

void Snapshot::createCounter(JNIEnv *env, jobject entity, sqlm_header_info* pHeader)
{
	// Pointer to the data which appears immediately after the header
	char*	pData = (char*)pHeader + sizeof(sqlm_header_info);
	char*	pElementName = getElementName(pHeader->element);

	// Ignore unknown counters
	if (pElementName == NULL)
		return;

	jobject	counterValue = NULL;

	if (pHeader->type == SQLM_TYPE_U32BIT)
	{
		unsigned int i = *(unsigned int*)pData;
		counterValue = makeCounterValueDouble(env, i);
	}
	else if (pHeader->type == SQLM_TYPE_32BIT)
	{
		signed int i = *(signed int*)pData;
		counterValue = makeCounterValueDouble(env, i);
	}
	else if (pHeader->type == SQLM_TYPE_STRING)
	{
		counterValue = makeCounterValueString(env, pData, pHeader->size);
	}
	else if (pHeader->type == SQLM_TYPE_U16BIT)
	{
		unsigned int i = *(unsigned short*)pData;
		counterValue = makeCounterValueDouble(env, i);
	}
	else if (pHeader->type == SQLM_TYPE_16BIT)
	{
		signed int i = *(signed short*)pData;
		counterValue = makeCounterValueDouble(env, i);
	}
	else if (pHeader->type == SQLM_TYPE_8BIT)
	{
		signed char i = *(signed char*)pData;
		counterValue = makeCounterValueDouble(env, i);
	}
	else if (pHeader->type == SQLM_TYPE_U8BIT)
	{
		unsigned int i = *(unsigned char*)pData;
		counterValue = makeCounterValueDouble(env, i);
	}
	else if (pHeader->type == SQLM_TYPE_64BIT)
	{
		int64 i = *(int64*)pData;
		counterValue = makeCounterValueDouble(env, (double)i);
	}
	else if (pHeader->type == SQLM_TYPE_U64BIT)
	{
		uint64 i = *(uint64*)pData;
		counterValue = makeCounterValueDouble(env, (double)i);
	}
	else // unknown (binary?) counter
	{
		counterValue = makeCounterValueString(env, "????", 4);
	}

	addCounterToEntity(env, entity, pElementName, counterValue);
}

// An group timestamp has two components (seconds + microseconds). Don't
// create an entity out of it; rather create one counter in milliseconds.
jobject Snapshot::createTimestampEntity(JNIEnv *env, jobject entity, sqlm_header_info* pHeader, bool* pbParseChildren)
{
	char* pElementName = getElementName(pHeader->element);

	int seconds = getElementValueInt(pHeader, SQLM_ELM_SECONDS);
	int microseconds = getElementValueInt(pHeader, SQLM_ELM_MICROSEC);
	uint64 milliseconds = seconds * 1000 + microseconds / 1000;

	jobject counterValue = makeCounterValueDouble(env, (double)milliseconds);
	addCounterToEntity(env, entity, pElementName, counterValue);

	*pbParseChildren = false;
	return entity;
}

// Create a normal entity, but set its name to the value of the sql query text
jobject Snapshot::createSQLQueryEntity(JNIEnv *env, jobject entity, sqlm_header_info* pHeader, bool* pbParseChildren)
{
	std::string strName = getElementValueString(pHeader, SQLM_ELM_STMT_TEXT);

	*pbParseChildren = true;
	return addEntityToEntity(env, entity, strName.c_str());
}

// Create a normal entity, but set its name to the value of the tablespace name
jobject Snapshot::createTablespaceEntity(JNIEnv *env, jobject entity, sqlm_header_info* pHeader, bool* pbParseChildren)
{
	std::string strTSName = getElementValueString(pHeader, SQLM_ELM_TABLESPACE_NAME);
	std::string strDatabaseName = getElementValueString(pHeader, SQLM_ELM_DB_NAME);
	std::string strName;
	if (strDatabaseName.size() > 0)
		strName = strDatabaseName + SEP + std::string("tablespace_list") + SEP + strTSName;
	else
		strName = strTSName;

	*pbParseChildren = true;
	return addEntityToEntity(env, entity, strName.c_str());
}

// Create a normal entity, but set its name to the value of the tablespace container name
jobject Snapshot::createTablespaceContainerEntity(JNIEnv *env, jobject entity, sqlm_header_info* pHeader, bool* pbParseChildren)
{
	std::string strName = getElementValueString(pHeader, SQLM_ELM_CONTAINER_NAME);

	*pbParseChildren = true;
	return addEntityToEntity(env, entity, strName.c_str());
}

// Create a normal entity, but set its name to the value of the application name
jobject Snapshot::createApplicationEntity(JNIEnv *env, jobject entity, sqlm_header_info* pHeader, bool* pbParseChildren)
{
	std::string strName = "appl";
	sqlm_header_info* pApplInfo = getChildStructure(pHeader, SQLM_ELM_APPL_INFO);
	if (pApplInfo)
	{
		std::string strApplicationName = getElementValueString(pApplInfo, SQLM_ELM_APPL_NAME);
		std::string strDatabaseName = getElementValueString(pApplInfo, SQLM_ELM_DB_NAME);
		int agentID = getElementValueInt(pApplInfo, SQLM_ELM_AGENT_ID);
		char szAgentID[1024]; sprintf(szAgentID, "%d", agentID);

		strName = /* std::string("dbase") + SEP + */ strDatabaseName + SEP + std::string("appl") + SEP +
			strApplicationName + std::string(" ") + szAgentID;
	}

	*pbParseChildren = true;

	return addEntityToEntity(env, entity, strName.c_str());
}

// Create a normal entity, but set its name to the value of the application
jobject Snapshot::createApplLockEntity(JNIEnv *env, jobject entity, sqlm_header_info* pHeader, bool* pbParseChildren)
{
	std::string strApplicationName = getElementValueString(pHeader, SQLM_ELM_APPL_NAME);
	int agentID = getElementValueInt(pHeader, SQLM_ELM_AGENT_ID);
	char szAgentID[1024]; sprintf(szAgentID, "%d", agentID);
	std::string strName = strApplicationName + std::string(" ") + szAgentID;

	*pbParseChildren = true;
	return addEntityToEntity(env, entity, strName.c_str());
}

// Create a normal entity, but set its name to the value of the table name
jobject Snapshot::createTableEntity(JNIEnv *env, jobject entity, sqlm_header_info* pHeader, bool* pbParseChildren)
{
	std::string strName = getElementValueString(pHeader, SQLM_ELM_TABLE_NAME);

	*pbParseChildren = true;
	return addEntityToEntity(env, entity, strName.c_str());
}

// Create a normal entity, but set its name to the value of the bufferpool name
jobject Snapshot::createBufferpoolEntity(JNIEnv *env, jobject entity, sqlm_header_info* pHeader, bool* pbParseChildren)
{
	std::string strBufferpoolName = getElementValueString(pHeader, SQLM_ELM_BP_NAME);
	std::string strDatabaseName = getElementValueString(pHeader, SQLM_ELM_DB_NAME);
	std::string strName = /* std::string("dbase") + SEP + */ strDatabaseName +
		SEP + std::string("bp_list") + SEP + strBufferpoolName;

	*pbParseChildren = true;

	return addEntityToEntity(env, entity, strName.c_str());
}

// Create a normal entity, but set its name to the value of the node number
jobject Snapshot::createBufferpoolNodeEntity(JNIEnv *env, jobject entity, sqlm_header_info* pHeader, bool* pbParseChildren)
{
	char szNodeName[1024];
	int node = getElementValueShort(pHeader, SQLM_ELM_NODE_NUMBER);
	sprintf(szNodeName, "Node %d", node);

	*pbParseChildren = true;
	return addEntityToEntity(env, entity, szNodeName);
}

// Create a normal entity, but set its name to the value of the node number
jobject Snapshot::createRootEntity(JNIEnv *env, jobject entity, sqlm_header_info* pHeader, bool* pbParseChildren)
{
	// Pointer to the data which appears immediately after the header
	char* pData = (char*)pHeader + sizeof(sqlm_header_info);
	char* pElementName = getElementName(pHeader->element);

	// Remember this System entity
	systemEntity = addEntityToEntity(env, entity, pElementName);

	// Return the same parent=> all the children of the System entity will surface
	*pbParseChildren = true;
	return entity;
}

// Create a normal entity, but set its name to the value of the agent id
jobject Snapshot::createAgentEntity(JNIEnv *env, jobject entity, sqlm_header_info* pHeader, bool* pbParseChildren)
{
	char szName[1024];
	int agentID = getElementValueInt(pHeader, SQLM_ELM_AGENT_PID);
	sprintf(szName, "Agent %d", agentID);

	*pbParseChildren = true;
	return addEntityToEntity(env, entity, szName);
}

// Create a normal entity, but set its name to the value of the memory pool type
jobject Snapshot::createMemoryPoolEntity(JNIEnv *env, jobject entity, sqlm_header_info* pHeader, bool* pbParseChildren)
{
	int poolID = getElementValueInt(pHeader, SQLM_ELM_POOL_ID);
	char* szName = getMemPoolName(poolID);

	*pbParseChildren = true;
	return addEntityToEntity(env, entity, szName);
}

// Create a normal entity, but set its name to the value of the database id
jobject Snapshot::createDatabaseEntity(JNIEnv *env, jobject entity, sqlm_header_info* pHeader, bool* pbParseChildren)
{
	std::string strDatabaseName = getElementValueString(pHeader, SQLM_ELM_DB_NAME);
	std::string strName = /* std::string("dbase") + SEP + */ strDatabaseName;

	*pbParseChildren = true;
	return addEntityToEntity(env, entity, strName.c_str());
}

jobject Snapshot::createEntity(JNIEnv *env, jobject entity, sqlm_header_info* pHeader, bool* pbParseChildren)
{

	// Deal with timestamps: don't create entities
	if (pHeader->element >= SQLM_MIN_TIME_STAMP && pHeader->element <= SQLM_MAX_TIME_STAMP ||
		pHeader->element == SQLM_ELM_STMT_USR_CPU_TIME ||
		pHeader->element == SQLM_ELM_STMT_SYS_CPU_TIME ||
		pHeader->element == SQLM_ELM_TOTAL_SYS_CPU_TIME ||
		pHeader->element == SQLM_ELM_TOTAL_USR_CPU_TIME ||
		pHeader->element == SQLM_ELM_STMT_ELAPSED_TIME ||
		pHeader->element == SQLM_ELM_UOW_ELAPSED_TIME)
	{
		return createTimestampEntity(env, entity, pHeader, pbParseChildren);
	}
	// deal with databases
	else if (pHeader->element == SQLM_ELM_DBASE)
	{
		return createDatabaseEntity(env, entity, pHeader, pbParseChildren);
	}
	// deal with sql queries
	else if (pHeader->element == SQLM_ELM_DYNSQL)
	{
		return createSQLQueryEntity(env, entity, pHeader, pbParseChildren);
	}
	// deal with tablespace names
	else if (pHeader->element == SQLM_ELM_TABLESPACE)
	{
		return createTablespaceEntity(env, entity, pHeader, pbParseChildren);
	}
	// deal with tablespace containers
	else if (pHeader->element == SQLM_ELM_TABLESPACE_CONTAINER)
	{
		return createTablespaceContainerEntity(env, entity, pHeader, pbParseChildren);
	}
	// deal with memory pools
	else if (pHeader->element == SQLM_ELM_MEMORY_POOL)
	{
		return createMemoryPoolEntity(env, entity, pHeader, pbParseChildren);
	}
	// deal with applications
	else if (pHeader->element == SQLM_ELM_APPL)
	{
		return createApplicationEntity(env, entity, pHeader, pbParseChildren);
	}
	// deal with tables
	else if (pHeader->element == SQLM_ELM_TABLE)
	{
		return createTableEntity(env, entity, pHeader, pbParseChildren);
	}
	// deal with bufferpools
	else if (pHeader->element == SQLM_ELM_BUFFERPOOL)
	{
		return createBufferpoolEntity(env, entity, pHeader, pbParseChildren);
	}
	// deal with bufferpool nodes
	else if (pHeader->element == SQLM_ELM_BUFFERPOOL_NODEINFO)
	{
		return createBufferpoolNodeEntity(env, entity, pHeader, pbParseChildren);
	}
	// deal with application locks
	else if (pHeader->element == SQLM_ELM_APPL_LOCK_LIST)
	{
		return createApplLockEntity(env, entity, pHeader, pbParseChildren);
	}
	// deal with switches nodes: skip them
	else if (pHeader->element == SQLM_ELM_SWITCH_LIST)
	{
		*pbParseChildren = false;
		return entity;
	}
	else if (pHeader->element == SQLM_ELM_COLLECTED)
	{
		return createRootEntity(env, entity, pHeader, pbParseChildren);
	}
	else if (pHeader->element == SQLM_ELM_AGENT)
	{
		return createAgentEntity(env, entity, pHeader, pbParseChildren);
	}
	else // create a normal entity
	{
		// Pointer to the data which appears immediately after the header
		char* pData = (char*)pHeader + sizeof(sqlm_header_info);
		char*	pElementName = getElementName(pHeader->element);
		if (pElementName != NULL)
		{
			std::string strElementName = std::string(pElementName);

			// If there is a DB_NAME child element move this under the database
			std::string strDatabaseName = getElementValueString(pHeader, SQLM_ELM_DB_NAME);
			std::string strName = strElementName;
			if (rootEntity == entity && strDatabaseName.size() > 0)
				strName = strDatabaseName + SEP + strElementName;

			*pbParseChildren = true;
			return addEntityToEntity(env, entity, strName.c_str());
		}
		else
			return entity;
	}
}

/**
 * Looks for a child structure with the specified elemID and returns a pointer
 * to its header.
 */
sqlm_header_info* Snapshot::getChildStructure(sqlm_header_info* pHeader, int structID)
{
	// Pointer to the data which appears immediately after the header
	char* pData = (char*)pHeader + sizeof(sqlm_header_info);
	char* pAfterQuery = (char*)pData + pHeader->size;

	// point immediately after the header (point to first counter)
	pHeader = (sqlm_header_info*)(pData);
	pData = (char*)pHeader + sizeof(sqlm_header_info);
	do
	{
		if (pHeader->element == structID)
			return pHeader;

		// advance to next counter
		pHeader = (sqlm_header_info*)(pData + pHeader->size);
		pData = (char*)pHeader + sizeof(sqlm_header_info);
	} while ((void*)pHeader <= (void*)pAfterQuery);

	return NULL;
}

/**
 * Looks for a child element with the specified elemID and returns its value
 * as a string.
 */
std::string Snapshot::getElementValueString(sqlm_header_info* pHeader, int elemID)
{
	sqlm_header_info* pElement = getChildStructure(pHeader, elemID);
	if (pElement)
	{
		char* pData = (char*)pElement + sizeof(sqlm_header_info);
		std::string strElement(pData, pElement->size);
		strElement.erase(strElement.find_last_not_of(" \t\r\n") + 1); // trim right
		return strElement;
	}

	return "";
}

/**
 * Looks for a child element with the specified elemID and returns its value
 * as a long int.
 */
int Snapshot::getElementValueInt(sqlm_header_info* pHeader, int elemID)
{
	sqlm_header_info* pElement = getChildStructure(pHeader, elemID);
	if (pElement)
	{
		char* pData = (char*)pElement + sizeof(sqlm_header_info);
		return *(int*)pData;
	}

	return 0;
}

/**
 * Looks for a child element with the specified elemID and returns its value
 * as a short int.
 */
short Snapshot::getElementValueShort(sqlm_header_info* pHeader, int elemID)
{
	sqlm_header_info* pElement = getChildStructure(pHeader, elemID);
	if (pElement)
	{
		char* pData = (char*)pElement + sizeof(sqlm_header_info);
		return *(short*)pData;
	}

	return 0;
}

sqlm_header_info * Snapshot::recurseParseMonitorStream(
	JNIEnv *env, jobject parentEntity, char * pStart, char * pEnd)
{
	sqlm_header_info * pHeader = (sqlm_header_info *)pStart;
	char * pData;

	// "pEnd" is NULL only when called at the "SQLM_ELM_COLLECTED" level, so
	// because this is the beginning of the monitor data stream, calculate
	// the memory location where the monitor data stream buffer ends
	if (!pEnd)
		pEnd = pStart +           // start of monitor stream
		pHeader->size +           // add size in the "collected" header
		sizeof(sqlm_header_info); // add size of header itself

	// Parse and print the data for the current logical grouping
	// Elements in the current logical grouping will be parsed until "pEnd"
	while ((char*)pHeader < pEnd)
	{
		// Pointer to the data which appears immediately after the header
		pData = (char*)pHeader + sizeof(sqlm_header_info);

		// determine if the current unit of data is a nested logical grouping
		if (pHeader->type == SQLM_TYPE_HEADER)
		{
			// Create an entity from this group. For timestamp-type groups
			// no new entity is created: same is returned
			bool	bParseChildren = true;
			jobject newEntity = createEntity(env, parentEntity, pHeader, &bParseChildren);

			// Call recurseParseMonitorStream recursively to parse this nested logical grouping
			if (bParseChildren)
				pHeader = recurseParseMonitorStream(env, newEntity, pData, pData + (pHeader->size));
			else
				pHeader = (sqlm_header_info *)(pData + pHeader->size);
		}
		else
		{
			// If this is a top level counter add it to the system entity. DB2 monitoring
			// information has a (rather useless) SQLM_ELM_COLLECTED root which we call
			// System entity and downgrade to the same level as its children.
			if (rootEntity == parentEntity)
				createCounter(env, systemEntity, pHeader);
			else
				createCounter(env, parentEntity, pHeader);

			// increment past the data to the next header
			pHeader = (sqlm_header_info *)(pData + pHeader->size);
		}
	}

	// Return the current memory location once the current logical grouping
	// has been parsed
	return pHeader;
}
