/**
 * 18-Mar-2006
 */
package com.ixora.rms.logging;

import com.ixora.rms.exception.RMSException;
import com.ixora.rms.messages.Msg;


/**
 * @author Daniel Moraru
 */
public class DataLogCompareAndReplayConfiguration {
	public static class LogRepositoryReplayConfig {
		private TimeInterval fTimeInterval;
		private LogRepositoryInfo fLogRepository;
		
		public LogRepositoryReplayConfig(LogRepositoryInfo logRepository, TimeInterval timeInterval) {
			super();
			fLogRepository = logRepository;
			fTimeInterval = timeInterval;
		}
		public TimeInterval getTimeInterval() {
			return fTimeInterval;
		}
		public LogRepositoryInfo getLogRepository() {
			return fLogRepository;
		}		
	}
	
	private LogRepositoryReplayConfig fLogOne;
	private LogRepositoryReplayConfig fLogTwo;
	private int fAggregationStep;

	public DataLogCompareAndReplayConfiguration(LogRepositoryReplayConfig f1, int aggStep) throws RMSException {
		super();
		fLogOne = f1;
		fAggregationStep = aggStep;
		validate(false);
	}
	
	public DataLogCompareAndReplayConfiguration(LogRepositoryReplayConfig f1, LogRepositoryReplayConfig f2, int aggStep) throws RMSException {
		super();
		fLogOne = f1;
		fLogTwo = f2;
		fAggregationStep = aggStep;
		validate(true);
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
	
	private void validate(boolean forComparison) throws RMSException {
		if(fLogOne == null) {
			throw new RMSException(Msg.ERROR_LOG_ONE_IS_MISSING, true);
		}
		if(forComparison && fLogTwo == null) {
			throw new RMSException(Msg.ERROR_LOG_TWO_IS_MISSING, true);
		}
		if(forComparison) {
			// check that files are not the same
			if(fLogOne.getLogRepository().getRepositoryName()
					.equals(fLogTwo.getLogRepository().getRepositoryName())) {
				throw new RMSException(Msg.ERROR_LOGS_FOR_COMPARISON_ARE_THE_SAME, 
					new String[]{fLogOne.getLogRepository().getRepositoryName()});
			}
		}
	}	
}
