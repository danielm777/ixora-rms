/**
 * 18-Mar-2006
 */
package com.ixora.rms.logging;


/**
 * @author Daniel Moraru
 */
public class DataLogCompareAndReplayConfiguration {
	public static class LogRepositoryReplayConfig {
		private long fTimestampBegin;
		private long fTimestampEnd;
		private LogRepositoryInfo fLogRepository;
		
		public LogRepositoryReplayConfig(LogRepositoryInfo logRepository, long timeBegin, long timeEnd) {
			super();
			fLogRepository = logRepository;
			fTimestampBegin = timeBegin;
			fTimestampEnd = timeEnd;
		}
		public long getTimeBegin() {
			return fTimestampBegin;
		}
		public long getTimeEnd() {
			return fTimestampEnd;
		}
		public LogRepositoryInfo getLogRepository() {
			return fLogRepository;
		}		
		public void setTimestampBegin(long timestampBegin) {
			fTimestampBegin = timestampBegin;
		}
		public void setTimestampEnd(long timestampEnd) {
			fTimestampEnd = timestampEnd;
		}		
	}
	
	private LogRepositoryReplayConfig fLogOne;
	private LogRepositoryReplayConfig fLogTwo;
	private int fAggregationStep;

	public DataLogCompareAndReplayConfiguration(LogRepositoryReplayConfig f1, LogRepositoryReplayConfig f2, int aggStep) {
		super();
		fLogOne = f1;
		fLogTwo = f2;
		fAggregationStep = aggStep;
	}

	public int getAggregationStep() {
		return fAggregationStep;
	}

	public LogRepositoryReplayConfig getLogOne() {
		return fLogOne;
	}

	public LogRepositoryReplayConfig getLogTwo() {
		return fLogTwo;
	}
}
