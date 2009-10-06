/**
 * 18-Mar-2006
 */
package com.ixora.rms.logging;

/**
 * @author Daniel Moraru
 */
public class DataLogReplayConfiguration {
	private long fTimeBegin;
	private long fTimeEnd;
	private int fAggregationStep;

	/**
	 * @param timeBegin
	 * @param timeEnd
	 * @param aggStep
	 */
	public DataLogReplayConfiguration(long timeBegin, long timeEnd, int aggStep) {
		super();
		fTimeBegin = timeBegin;
		fTimeEnd = timeEnd;
		fAggregationStep = aggStep;
	}

	/**
	 * @return
	 */
	public int getAggregationStep() {
		return fAggregationStep;
	}

	/**
	 * @return.
	 */
	public long getTimeBegin() {
		return fTimeBegin;
	}

	/**
	 * @return
	 */
	public long getTimeEnd() {
		return fTimeEnd;
	}
}
