/**
 * 14-Jul-2005
 */
package com.ixora.rms;

/**
 * @author Daniel Moraru
 */
public interface DataSinkTrimmed extends DataSink {
	/**
	 * @param rdCache
	 */
	void setRecordDefinitionCache(RecordDefinitionCache rdCache);
}
