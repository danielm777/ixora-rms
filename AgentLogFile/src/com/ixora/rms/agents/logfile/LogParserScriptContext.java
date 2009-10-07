/**
 * 16-Mar-2006
 */
package com.ixora.rms.agents.logfile;

/**
 * Interface made available to the log parser scripts. The most common usage
 * of this class is to cache objects like <code>DateFormat</code>s
 * to be available during subsequent invocations of the parser script.
 * @author Daniel Moraru
 */
public interface LogParserScriptContext {

	/**
	 * Retrieves an object which was previously registered using the given <code>id</code>.
	 * @param id
	 * @return
	 */
	public Object getScriptData(String id);

	/**
	 * Stores the <code>data</code> object under the given <code>id</code>.
	 * @param id
	 * @param data
	 */
	public void setScriptData(String id, Object data);
}