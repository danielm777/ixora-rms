/**
 * 12-Feb-2006
 */
package com.ixora.rms.agents.logfile;

import com.ixora.rms.agents.impl.logfile.LogRecord;

/**
 * The only method in this interface must be implemented by log parsing scripts.
 * @author Daniel Moraru
 */
public interface LogParserScript {
	/**
	 * @param context
	 * @param lines
	 * @return
	 */
	LogRecord parse(LogParserScriptContextImpl context, String[] lines);
}
