/**
 * 21-Mar-2006
 */
package com.ixora.rms.batch;

import java.io.Serializable;

import com.ixora.rms.logging.LogRepositoryInfo;

/**
 * @author Daniel Moraru
 */
public class BatchSessionConfiguration implements Serializable {
	private String fMonitoringSessionName;
	private LogRepositoryInfo fLogRepositoryInfo;

	/**
	 * @param monitoringSessionName
	 * @param logRepInfo
	 */
	public BatchSessionConfiguration(String monitoringSessionName, LogRepositoryInfo logRepInfo) {
		super();
		fMonitoringSessionName = monitoringSessionName;
		fLogRepositoryInfo = logRepInfo;
	}

	/**
	 * @return fLogRepositoryInfo.
	 */
	public LogRepositoryInfo getLogRepositoryInfo() {
		return fLogRepositoryInfo;
	}

	/**
	 * @return fMonitoringSessionName.
	 */
	public String getMonitoringSessionName() {
		return fMonitoringSessionName;
	}
}
