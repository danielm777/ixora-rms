#ifndef __DB2MON_H__
#define __DB2MON_H__

#include "snapshot.h"

class DB2Mon
{
	void getSnapshot(JNIEnv* env, sqlma* ma_ptr, Snapshot& snap);
	jboolean m_aggAllNodes; // collect data from local node, or from all nodes
public:
	DB2Mon();

	void attach(JNIEnv* env, char* user, char* password, char* node);
	void detach(JNIEnv* env);

	void enableMonitors(JNIEnv* env,
			jboolean aggAllNodes, jboolean monitorUOW,
			jboolean monitorStatement, jboolean monitorTable,
			jboolean monitorBP, jboolean monitorLock,
			jboolean monitorSort, jboolean monitorTimestamp);
	void disableMonitors(JNIEnv* env);

	void getDatabaseSnapshot(JNIEnv* env, char* dbName, Snapshot& snap);
	void getSystemSnapshot(JNIEnv* env, Snapshot& snap);
};


#endif // __DB2MON_H__
