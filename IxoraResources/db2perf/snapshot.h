#ifndef __SNAPSHOT_H__
#define __SNAPSHOT_H__

#ifndef _UNICODE
#define _UNICODE
#endif
#ifndef UNICODE
#define UNICODE
#endif

#include <stddef.h>
#include <stdlib.h>
#include <tchar.h>
#include <string.h>
#include <string>
#include "db2includes/sql.h"
#include "db2includes/db2ApiDf.h"
#include <jni.h>

#ifdef WIN32
	typedef unsigned __int64	uint64;
	typedef   signed __int64	int64;
#else
	typedef unsigned long long	uint64;
	typedef   signed long long	int64;
#endif

class MemObj
{
	char* p;
public:
	MemObj(int bytes, bool zeroFill = true)
	{
		p = (char*)malloc(bytes);
		if (zeroFill)
			memset(p, 0, bytes);
	}

	~MemObj()
	{
		free(p);
	}

	char* get() { return p; }

	void resize(int bytes, bool zeroFill = true)
	{
		free(p);
		p = (char*)malloc(bytes);
		if (zeroFill)
			memset(p, 0, bytes);
	}
};

class a2w : public MemObj
{
public:
	a2w(const char* value, int len) : MemObj(2*(len + 1))
	{
		wchar_t*	buffer = wget();
		mbstowcs(buffer, value, len);
		buffer[len] = 0;
	}

	a2w(const char* value) : MemObj(2*(strlen(value) + 1))
	{
		int			len = strlen(value);
		wchar_t*	buffer = wget();
		mbstowcs(buffer, value, len);
		buffer[len] = 0;
	}

	wchar_t* wget() { return (wchar_t*)get(); }
};

class w2a : public MemObj
{
public:
	w2a(const wchar_t* value, int len) : MemObj(len + 1)
	{
		char*	buffer = get();
		wcstombs(buffer, value, len);
		buffer[len] = 0;
	}

	w2a(const wchar_t* value) : MemObj(_tcslen(value) + 1)
	{
		int		len = _tcslen(value);
		char*	buffer = get();
		wcstombs(buffer, value, len);
		buffer[len] = 0;
	}
};

/**************************************************************/

#define SNAPSHOT_BUFFER_UNIT_SZ (10*1024)

class Snapshot
{
    // array for the defined names of all monitor elements (from sqlmon.h)
    char **elementName;

	jobject	rootEntity;
	jobject	systemEntity; // top entity, downgraded one level

	sqlm_header_info * recurseParseMonitorStream(JNIEnv *env, jobject parentEntity, char * pStart, char * pEnd);
	jobject createEntity(JNIEnv *env, jobject entity, sqlm_header_info* pHeader, bool* pbParseChildren);
	void createCounter(JNIEnv *env, jobject entity, sqlm_header_info* pHeader);

	void initElementNames();
	char* getElementName(int index);
	char* getMemPoolName(int code);

	jobject createTimestampEntity(JNIEnv *env, jobject entity, sqlm_header_info* pHeader, bool* pbParseChildren);
	jobject createSQLQueryEntity(JNIEnv *env, jobject entity, sqlm_header_info* pHeader, bool* pbParseChildren);
	jobject createTablespaceEntity(JNIEnv *env, jobject entity, sqlm_header_info* pHeader, bool* pbParseChildren);
	jobject createApplicationEntity(JNIEnv *env, jobject entity, sqlm_header_info* pHeader, bool* pbParseChildren);
	jobject createTableEntity(JNIEnv *env, jobject entity, sqlm_header_info* pHeader, bool* pbParseChildren);
	jobject createBufferpoolEntity(JNIEnv *env, jobject entity, sqlm_header_info* pHeader, bool* pbParseChildren);
	jobject createBufferpoolNodeEntity(JNIEnv *env, jobject entity, sqlm_header_info* pHeader, bool* pbParseChildren);
	jobject createTablespaceContainerEntity(JNIEnv *env, jobject entity, sqlm_header_info* pHeader, bool* pbParseChildren);
	jobject createRootEntity(JNIEnv *env, jobject entity, sqlm_header_info* pHeader, bool* pbParseChildren);
	jobject createAgentEntity(JNIEnv *env, jobject entity, sqlm_header_info* pHeader, bool* pbParseChildren);
	jobject createMemoryPoolEntity(JNIEnv *env, jobject entity, sqlm_header_info* pHeader, bool* pbParseChildren);
	jobject createDatabaseEntity(JNIEnv *env, jobject entity, sqlm_header_info* pHeader, bool* pbParseChildren);
	jobject createApplLockEntity(JNIEnv *env, jobject entity, sqlm_header_info* pHeader, bool* pbParseChildren);

	sqlm_header_info* getChildStructure(sqlm_header_info* pHeader, int structID);
	std::string getElementValueString(sqlm_header_info* pHeader, int elemID);
	int getElementValueInt(sqlm_header_info* pHeader, int elemID);
	short getElementValueShort(sqlm_header_info* pHeader, int elemID);

public:
	Snapshot(JNIEnv* env);

	void initialize(JNIEnv* env, char* buffer)
	{
		recurseParseMonitorStream(env, rootEntity, buffer, NULL);
	}

	jobject getRootEntity() { return rootEntity; }
};

#endif // __SNAPSHOT_H__
