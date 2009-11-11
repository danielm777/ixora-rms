/*
 * Created on 31-Dec-2003
 */
package com.ixora.rms.ui.logchooser;

import com.ixora.rms.logging.LogRepositoryInfo;

/**
 * LogChooserHandler.
 * @author Daniel Moraru
 */
public interface LogChooserHandler {
	/**
	 * @param last the last repository used, it can be null
	 * @return info on the selected log
	 */
	LogRepositoryInfo getLogInfoForRead(LogRepositoryInfo last);
	/**
	 * @param last the last repository used, it can be null
	 * @return info on the selected log
	 */
	LogRepositoryInfo getLogInfoForWrite(LogRepositoryInfo last);
}