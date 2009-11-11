/**
 * 11-Feb-2006
 */
package com.ixora.rms.agents.logfile;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Daniel Moraru
 */
public class LogParserScriptContextImpl implements LogParserScriptContext {
	/** Map used by the log parser scripts to store data between invocations */
	private Map<String, Object> fScriptData;

	/**
	 *
	 */
	public LogParserScriptContextImpl() {
		super();
		fScriptData = new HashMap<String, Object>();
	}

	/**
	 * @see com.ixora.rms.agents.logfile.LogParserScriptContext#getScriptData(java.lang.String)
	 */
	public Object getScriptData(String id) {
		return fScriptData.get(id);
	}

	/**
	 * @see com.ixora.rms.agents.logfile.LogParserScriptContext#setScriptData(java.lang.String, java.lang.Object)
	 */
	public void setScriptData(String id, Object data) {
		fScriptData.put(id, data);
	}
}
