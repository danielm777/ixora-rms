/*
 * Created on 31-Dec-2003
 */
package com.ixora.rms.ui.logchooser;

import com.ixora.rms.logging.LogRepositoryInfo;

/**
 * LogChooser to get log repository details to
 * log data to or to play data from.
 * @author Daniel Moraru
 */
public interface LogChooser {
	/**
	 * @return info on the selected log
	 */
	LogRepositoryInfo getLogInfoForRead();
	/**
	 * @return info on the selected log
	 */
	LogRepositoryInfo getLogInfoForWrite();
}