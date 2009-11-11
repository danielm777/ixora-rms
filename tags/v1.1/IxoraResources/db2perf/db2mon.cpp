// db2perf.cpp : Defines the entry point for the console application.
//

#include "snapshot.h"
#include "db2mon.h"

/**************************************************************/

#define JAVA_PREFIX(fncname) Java_com_ixora_rms_agents_db2_DB2RootEntity_##fncname

extern "C" {
	/*
	 * Class:     com_ixora_rms_agents_db2_DB2RootEntity
	 * Method:    Attach
	 * Signature: (Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V
	 */
	JNIEXPORT jint JNICALL JAVA_PREFIX(Attach)
	  (JNIEnv *, jclass, jstring, jstring, jstring);

	/*
	 * Class:     com_ixora_rms_agents_db2_DB2RootEntity
	 * Method:    Detach
	 * Signature: (Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V
	 */
	JNIEXPORT void JNICALL JAVA_PREFIX(Detach)
	  (JNIEnv *, jclass, jint);

	/*
	 * Class:     com_ixora_rms_agents_db2_DB2RootEntity
	 * Method:    EnableMonitors
	 */
	JNIEXPORT void JNICALL JAVA_PREFIX(EnableMonitors)
	  (JNIEnv *env, jclass clazz, jint handle, jboolean aggAllNodes, jboolean monitorUOW,
	  jboolean monitorStatement,
	  jboolean monitorTable, jboolean monitorBP, jboolean monitorLock,
	  jboolean monitorSort, jboolean monitorTimestamp);

	/*
	 * Class:     com_ixora_rms_agents_db2_DB2RootEntity
	 * Method:    EnumSystemEntities
	 * Signature: ()Lcom/ixora/rms/agents/db2/DB2RootEntity$NativeEntity;
	 */
	JNIEXPORT jobject JNICALL JAVA_PREFIX(EnumSystemEntities)
	  (JNIEnv *, jclass, jint);

	/*
	 * Class:     com_ixora_rms_agents_db2_DB2RootEntity
	 * Method:    EnumDatabaseEntities
	 * Signature: (Ljava/lang/String;)Lcom/ixora/rms/agents/db2/DB2RootEntity$NativeEntity;
	 */
	JNIEXPORT jobject JNICALL JAVA_PREFIX(EnumDatabaseEntities)
	  (JNIEnv *, jclass, jint, jstring);

}

#define APICHECK(env, thesqlca) apiCheckAndThrow(env, sqlca)

/** Throws the appropriate DB2Exception for this error */
void apiCheckAndThrow(JNIEnv* env, sqlca& thesqlca)
{
	if (thesqlca.sqlcode < 0)
	{
		char errorMsg[1024];
		sqlaintp(errorMsg, 1024, 80, &thesqlca);

		jclass excClazz = env->FindClass("com/ixora/rms/agents/db2/DB2NativeException");
		env->ThrowNew(excClazz, errorMsg);
		throw "internal";
	}
}

/**************************************************************/

DB2Mon::DB2Mon()
{
}

void DB2Mon::attach(JNIEnv* env, char* user, char* password, char* instance)
{
	sqlca	sqlca;
	sqleatin(instance, user, password, &sqlca);
	APICHECK(env, sqlca);
}

void DB2Mon::detach(JNIEnv* env)
{
	sqlca	sqlca;
	sqledtin(&sqlca);
	APICHECK(env, sqlca);
}

void DB2Mon::enableMonitors(JNIEnv* env,
		jboolean aggAllNodes, jboolean monitorUOW,
		jboolean monitorStatement, jboolean monitorTable,
		jboolean monitorBP, jboolean monitorLock,
		jboolean monitorSort, jboolean monitorTimestamp)
{
	int		rc = 0;
	sqlca	sqlca;
	sqluint32 outputFormat;

	m_aggAllNodes = aggAllNodes;

	db2MonitorSwitchesData	switchesData;
	sqlm_recording_group	switchesList[SQLM_NUM_GROUPS];

	// Call the db2MonitorSwitches API to update the monitor switch settings
	// first, set the values of the sqlm_recording_group structure
	switchesList[SQLM_UOW_SW].input_state = monitorUOW ? SQLM_ON : SQLM_OFF;
	switchesList[SQLM_STATEMENT_SW].input_state = monitorStatement ? SQLM_ON : SQLM_OFF;
	switchesList[SQLM_TABLE_SW].input_state = monitorTable ? SQLM_ON : SQLM_OFF;
	switchesList[SQLM_BUFFER_POOL_SW].input_state = monitorBP ? SQLM_ON : SQLM_OFF;
	switchesList[SQLM_LOCK_SW].input_state = monitorLock ? SQLM_ON : SQLM_OFF;
	switchesList[SQLM_SORT_SW].input_state = monitorSort ? SQLM_ON : SQLM_OFF;
	switchesList[SQLM_TIMESTAMP_SW].input_state = monitorTimestamp ? SQLM_ON : SQLM_OFF;

	// second, set the values of the db2MonitorSwitchesData structure
	switchesData.piGroupStates = switchesList;
	switchesData.poBuffer = NULL;
	switchesData.iVersion = SQLM_CURRENT_VERSION;
	switchesData.iBufferSize = 0;
	switchesData.iReturnData = 0;
	switchesData.iNodeNumber = m_aggAllNodes ? SQLM_ALL_NODES : SQLM_CURRENT_NODE;
	switchesData.poOutputFormat = &outputFormat;

	// third, call the db2MonitorSwitches API
	db2MonitorSwitches(db2Version810, &switchesData, &sqlca);
	APICHECK(env, sqlca);
}

void DB2Mon::disableMonitors(JNIEnv* env)
{
	int		rc = 0;
	sqlca	sqlca;
	sqluint32 outputFormat;

	db2MonitorSwitchesData	switchesData;
	sqlm_recording_group	switchesList[SQLM_NUM_GROUPS];

	// Call the db2MonitorSwitches API to update the monitor switch settings
	// first, set the values of the sqlm_recording_group structure
	switchesList[SQLM_UOW_SW].input_state = SQLM_OFF;
	switchesList[SQLM_STATEMENT_SW].input_state = SQLM_OFF;
	switchesList[SQLM_TABLE_SW].input_state = SQLM_OFF;
	switchesList[SQLM_BUFFER_POOL_SW].input_state = SQLM_OFF;
	switchesList[SQLM_LOCK_SW].input_state = SQLM_OFF;
	switchesList[SQLM_SORT_SW].input_state = SQLM_OFF;
	switchesList[SQLM_TIMESTAMP_SW].input_state = SQLM_OFF;

	// second, set the values of the db2MonitorSwitchesData structure
	switchesData.piGroupStates = switchesList;
	switchesData.poBuffer = NULL;
	switchesData.iVersion = SQLM_CURRENT_VERSION;
	switchesData.iBufferSize = 0;
	switchesData.iReturnData = 0;
	switchesData.iNodeNumber = m_aggAllNodes ? SQLM_ALL_NODES : SQLM_CURRENT_NODE;
	switchesData.poOutputFormat = &outputFormat;

	// third, call the db2MonitorSwitches API
	db2MonitorSwitches(db2Version810, &switchesData, &sqlca);
	APICHECK(env, sqlca);
}

void DB2Mon::getDatabaseSnapshot(JNIEnv* env, char* dbName, Snapshot& snap)
{
	// Allocate the sqlma (monitor area) structure. This tells DB2 which objects
	// we want to monitor.
	unsigned int obj_num = 16;    // # of objects to monitor
	unsigned int ma_sz = SQLMASIZE(obj_num);
	MemObj	ptrSQLMA(ma_sz);
	sqlma *ma_ptr = (sqlma *) ptrSQLMA.get();
	ma_ptr->obj_num = obj_num;
	ma_ptr->obj_var[0].obj_type = SQLMA_DBASE;
	strncpy((char *)ma_ptr->obj_var[0].object, dbName, SQLM_OBJECT_SZ);
	ma_ptr->obj_var[1].obj_type = SQLMA_DBASE_APPLS;
	strncpy((char *)ma_ptr->obj_var[1].object, dbName, SQLM_OBJECT_SZ);
	ma_ptr->obj_var[2].obj_type = SQLMA_DBASE_TABLESPACES;
	strncpy((char *)ma_ptr->obj_var[2].object, dbName, SQLM_OBJECT_SZ);
	ma_ptr->obj_var[3].obj_type = SQLMA_DBASE_LOCKS;
	strncpy((char *)ma_ptr->obj_var[3].object, dbName, SQLM_OBJECT_SZ);
	ma_ptr->obj_var[4].obj_type = SQLMA_DBASE_BUFFERPOOLS;
	strncpy((char *)ma_ptr->obj_var[4].object, dbName, SQLM_OBJECT_SZ);
	ma_ptr->obj_var[5].obj_type = SQLMA_DBASE_TABLES;
	strncpy((char *)ma_ptr->obj_var[5].object, dbName, SQLM_OBJECT_SZ);
	ma_ptr->obj_var[6].obj_type = SQLMA_DYNAMIC_SQL;
	strncpy((char *)ma_ptr->obj_var[6].object, dbName, SQLM_OBJECT_SZ);

	ma_ptr->obj_var[7].obj_type = SQLMA_DCS_DBASE_APPLS;
	strncpy((char *)ma_ptr->obj_var[7].object, dbName, SQLM_OBJECT_SZ);

	ma_ptr->obj_var[8].obj_type = SQLMA_DBASE_APPLS_REMOTE;
	strncpy((char *)ma_ptr->obj_var[8].object, dbName, SQLM_OBJECT_SZ);

	// include generic ones as well
	ma_ptr->obj_var[ 9].obj_type = SQLMA_DBASE_ALL;
	ma_ptr->obj_var[10].obj_type = SQLMA_BUFFERPOOLS_ALL;
	ma_ptr->obj_var[11].obj_type = SQLMA_DBASE_REMOTE_ALL;
	ma_ptr->obj_var[12].obj_type = SQLMA_DCS_DBASE_ALL;
	ma_ptr->obj_var[13].obj_type = SQLMA_APPL_ALL;
	ma_ptr->obj_var[14].obj_type = SQLMA_DCS_APPL_ALL;
	ma_ptr->obj_var[15].obj_type = SQLMA_APPL_REMOTE_ALL;
	//ma_ptr->obj_var[16].obj_type = SQLMA_APPLINFO_ALL;
	//ma_ptr->obj_var[17].obj_type = SQLMA_DCS_APPLINFO_ALL;

	getSnapshot(env, ma_ptr, snap);
}

void DB2Mon::getSystemSnapshot(JNIEnv* env, Snapshot& snap)
{
	// Allocate the sqlma (monitor area) structure. This tells DB2 which objects
	// we want to monitor.
	unsigned int obj_num = 7;    // # of objects to monitor
	unsigned int ma_sz = SQLMASIZE(obj_num);
	MemObj	ptrSQLMA(ma_sz);
	sqlma *ma_ptr = (sqlma *) ptrSQLMA.get();
	ma_ptr->obj_num = obj_num;
	ma_ptr->obj_var[0].obj_type = SQLMA_DBASE_ALL;
	ma_ptr->obj_var[1].obj_type = SQLMA_BUFFERPOOLS_ALL;
	ma_ptr->obj_var[2].obj_type = SQLMA_DBASE_REMOTE_ALL;
	ma_ptr->obj_var[3].obj_type = SQLMA_DCS_DBASE_ALL;
	ma_ptr->obj_var[4].obj_type = SQLMA_APPL_ALL;
	ma_ptr->obj_var[5].obj_type = SQLMA_DCS_APPL_ALL;
	ma_ptr->obj_var[6].obj_type = SQLMA_APPL_REMOTE_ALL;
	//ma_ptr->obj_var[7].obj_type = SQLMA_APPLINFO_ALL;
	//ma_ptr->obj_var[8].obj_type = SQLMA_DCS_APPLINFO_ALL;


	getSnapshot(env, ma_ptr, snap);
}

void DB2Mon::getSnapshot(JNIEnv* env, sqlma *ma_ptr, Snapshot& snap)
{
	int		rc = 0;  // return code
	sqlca	sqlca;
	sqluint32 outputFormat;

	// Determine the size of the snapshot
	db2GetSnapshotSizeData getSnapshotSizeParam;
	sqluint32 buffer_sz;  // estimated buffer size

	getSnapshotSizeParam.piSqlmaData = ma_ptr;
	getSnapshotSizeParam.poBufferSize = &buffer_sz;
	getSnapshotSizeParam.iVersion = SQLM_CURRENT_VERSION;
	getSnapshotSizeParam.iNodeNumber = m_aggAllNodes ? SQLM_ALL_NODES : SQLM_CURRENT_NODE;
	getSnapshotSizeParam.iSnapshotClass = SQLM_CLASS_DEFAULT;

	rc = db2GetSnapshotSize(db2Version810, &getSnapshotSizeParam, &sqlca);
	APICHECK(env, sqlca);

	// Allocate memory to a buffer to hold snapshot monitor data.
	// Add about 10k to make sure we avoid the reallocation below
	buffer_sz = buffer_sz + SNAPSHOT_BUFFER_UNIT_SZ;
	MemObj	ptrBuffer(buffer_sz);
	char * buffer_ptr = ptrBuffer.get();
	sqlm_collected collected;  // returned sqlm_collected structure
	memset(&collected, '\0', sizeof(struct sqlm_collected));

	db2GetSnapshotData getSnapshotParam;
	getSnapshotParam.piSqlmaData = ma_ptr;
	getSnapshotParam.poCollectedData = &collected;
	getSnapshotParam.iBufferSize = buffer_sz;
	getSnapshotParam.poBuffer = buffer_ptr;
	getSnapshotParam.iVersion = SQLM_CURRENT_VERSION;
	getSnapshotParam.iStoreResult = 0;
	getSnapshotParam.iNodeNumber = m_aggAllNodes ? SQLM_ALL_NODES : SQLM_CURRENT_NODE;
	getSnapshotParam.poOutputFormat = &outputFormat;
	getSnapshotParam.iSnapshotClass = SQLM_CLASS_DEFAULT;

	rc = db2GetSnapshot(db2Version810, &getSnapshotParam, &sqlca);
	APICHECK(env, sqlca);

	// If buffer was too small enlarge it and repeat until succeeded
	while (sqlca.sqlcode == 1606)
	{
		buffer_sz = buffer_sz + SNAPSHOT_BUFFER_UNIT_SZ;
		ptrBuffer.resize(buffer_sz);
		buffer_ptr = ptrBuffer.get();

		getSnapshotParam.iBufferSize = buffer_sz;
		getSnapshotParam.poBuffer = buffer_ptr;

		rc = db2GetSnapshot(db2Version810, &getSnapshotParam, &sqlca);
		APICHECK(env, sqlca);
	}

	// Fill the snapshot object from the memory buffer
	snap.initialize(env, buffer_ptr);
}


/***********************************************************************/

JNIEXPORT jint JNICALL JAVA_PREFIX(Attach)
  (JNIEnv *env, jclass clazz, jstring user, jstring password, jstring instance)
{
	try
	{
		DB2Mon*	db2Mon = new DB2Mon();

		const TCHAR* wszUser = (const TCHAR*)env->GetStringChars(user, 0);
		const TCHAR* wszPassword = (const TCHAR*)env->GetStringChars(password, 0);
		const TCHAR* wszInstance = (const TCHAR*)env->GetStringChars(instance, 0);

		w2a	objUser(wszUser); char* szUser = objUser.get();
		w2a	objPassword(wszPassword); char* szPassword = objPassword.get();
		w2a	objInstance(wszInstance); char* szInstance = objInstance.get();

		db2Mon->attach(env, szUser, szPassword, szInstance);

		env->ReleaseStringChars(user, (const jchar*)wszUser);
		env->ReleaseStringChars(password, (const jchar*)wszPassword);
		env->ReleaseStringChars(instance, (const jchar*)wszInstance);

		return (jint)db2Mon;
	}
	catch (...)
	{
		// If this is a Java exception we threw, just let it through
		// Otherwise it's an access violation or something: convert to Java exception
		if (!env->ExceptionOccurred())
		{
			jclass excClazz = env->FindClass("com/ixora/rms/agents/db2/DB2AgentException");
			env->ThrowNew(excClazz, "db2.error.internal_agent_error");
		}

		return 0;
	}
}

JNIEXPORT void JNICALL JAVA_PREFIX(Detach)
  (JNIEnv *env, jclass clazz, jint handle)
{
	try
	{
		DB2Mon*	db2Mon = (DB2Mon*)handle;
		db2Mon->disableMonitors(env);
		db2Mon->detach(env);
	}
	catch (...)
	{
		// If this is a Java exception we threw, just let it through
		// Otherwise it's an access violation or something: convert to Java exception
		if (!env->ExceptionOccurred())
		{
			jclass excClazz = env->FindClass("com/ixora/rms/agents/db2/DB2AgentException");
			env->ThrowNew(excClazz, "db2.error.internal_agent_error");
		}
	}
}

JNIEXPORT void JNICALL JAVA_PREFIX(EnableMonitors)
  (JNIEnv *env, jclass clazz, jint handle, jboolean aggAllNodes, jboolean monitorUOW,
  jboolean monitorStatement,
  jboolean monitorTable, jboolean monitorBP, jboolean monitorLock,
  jboolean monitorSort, jboolean monitorTimestamp)
{
	try
	{
		DB2Mon*	db2Mon = (DB2Mon*)handle;
		db2Mon->enableMonitors(env, aggAllNodes, monitorUOW, monitorStatement,
			monitorTable, monitorBP, monitorLock,
			monitorSort, monitorTimestamp);
	}
	catch (...)
	{
		// If this is a Java exception we threw, just let it through
		// Otherwise it's an access violation or something: convert to Java exception
		if (!env->ExceptionOccurred())
		{
			jclass excClazz = env->FindClass("com/ixora/rms/agents/db2/DB2AgentException");
			env->ThrowNew(excClazz, "db2.error.internal_agent_error");
		}
	}
}


JNIEXPORT jobject JNICALL JAVA_PREFIX(EnumSystemEntities)
  (JNIEnv *env, jclass clazz, jint handle)
{
	try
	{
		DB2Mon*	db2Mon = (DB2Mon*)handle;

		Snapshot	snap(env);
		db2Mon->getSystemSnapshot(env, snap);

		return snap.getRootEntity();
	}
	catch (...)
	{
		// If this is a Java exception we threw, just let it through
		// Otherwise it's an access violation or something: convert to Java exception
		if (!env->ExceptionOccurred())
		{
			jclass excClazz = env->FindClass("com/ixora/rms/agents/db2/DB2AgentException");
			env->ThrowNew(excClazz, "db2.error.internal_agent_error");
		}

		return NULL;
	}
}

JNIEXPORT jobject JNICALL JAVA_PREFIX(EnumDatabaseEntities)
  (JNIEnv *env, jclass clazz, jint handle, jstring dbName)
{
	try
	{
		DB2Mon*	db2Mon = (DB2Mon*)handle;
		const TCHAR* wszDBName = (const TCHAR*)env->GetStringChars(dbName, 0);
		w2a	objDBName(wszDBName); char* szDBName = objDBName.get();

		Snapshot	snap(env);
		db2Mon->getDatabaseSnapshot(env, szDBName, snap);

		env->ReleaseStringChars(dbName, (const jchar*)wszDBName);

		return snap.getRootEntity();
	}
	catch (...)
	{
		// If this is a Java exception we threw, just let it through
		// Otherwise it's an access violation or something: convert to Java exception
		if (!env->ExceptionOccurred())
		{
			jclass excClazz = env->FindClass("com/ixora/rms/agents/db2/DB2AgentException");
			env->ThrowNew(excClazz, "db2.error.internal_agent_error");
		}

		return NULL;
	}
}
