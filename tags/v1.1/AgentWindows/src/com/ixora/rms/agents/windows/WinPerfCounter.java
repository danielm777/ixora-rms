/*
 * Created on Sep 1, 2004
 */
package com.ixora.rms.agents.windows;

import com.ixora.rms.CounterType;
import com.ixora.rms.agents.impl.Counter;

/**
 * WinPerfCounter
 */

public class WinPerfCounter extends Counter
{
	private int formatType = WinPerfConstants.PDH_FMT_LONG;
	private String nativePath;

	public WinPerfCounter(NativeCounter nativeCounter, String nativePath)
	{
		super(nativeCounter.name, nativeCounter.description);
		this.nativePath = nativePath;

		switch (nativeCounter.type)
		{
		// int
			case WinPerfConstants.PERF_COUNTER_RAWCOUNT_HEX :
				fDiscreet = false;
				fType = CounterType.LONG;
				this.formatType = WinPerfConstants.PDH_FMT_LONG;
				break;

		// float
			case WinPerfConstants.PERF_COUNTER_COUNTER :
			case WinPerfConstants.PERF_COUNTER_QUEUELEN_TYPE :
			case WinPerfConstants.PERF_COUNTER_RAWCOUNT :
			case WinPerfConstants.PERF_SAMPLE_FRACTION :
			case WinPerfConstants.PERF_SAMPLE_COUNTER :
			case WinPerfConstants.PERF_AVERAGE_TIMER :
			case WinPerfConstants.PERF_RAW_FRACTION :
			case WinPerfConstants.PERF_COUNTER_DELTA :
				fDiscreet = false;
				fType = CounterType.DOUBLE;
				this.formatType = WinPerfConstants.PDH_FMT_DOUBLE;
				break;

		// long
			case WinPerfConstants.PERF_COUNTER_LARGE_RAWCOUNT_HEX :
				fDiscreet = false;
				fType = CounterType.LONG;
				this.formatType = WinPerfConstants.PDH_FMT_LARGE;
				break;

		// double
			case WinPerfConstants.PERF_COUNTER_TIMER :
			case WinPerfConstants.PERF_COUNTER_LARGE_QUEUELEN_TYPE :
			case WinPerfConstants.PERF_COUNTER_BULK_COUNT :
			case WinPerfConstants.PERF_COUNTER_LARGE_RAWCOUNT :
			case WinPerfConstants.PERF_COUNTER_TIMER_INV :
			case WinPerfConstants.PERF_100NSEC_TIMER :
			case WinPerfConstants.PERF_100NSEC_TIMER_INV :
			case WinPerfConstants.PERF_COUNTER_MULTI_TIMER :
			case WinPerfConstants.PERF_COUNTER_MULTI_TIMER_INV :
			case WinPerfConstants.PERF_100NSEC_MULTI_TIMER :
			case WinPerfConstants.PERF_100NSEC_MULTI_TIMER_INV :
			case WinPerfConstants.PERF_ELAPSED_TIME :
			case WinPerfConstants.PERF_COUNTER_LARGE_DELTA :
				fDiscreet = false;
				fType = CounterType.DOUBLE;
				this.formatType = WinPerfConstants.PDH_FMT_DOUBLE;
				break;

		// Text = string
			case WinPerfConstants.PERF_COUNTER_TEXT :
				fDiscreet = true;
				fType = CounterType.STRING;
				this.formatType = WinPerfConstants.PDH_FMT_UNICODE;
				break;


		//	don't display
//			case WinPerfConstants.PERF_COUNTER_NODATA :
//			case WinPerfConstants.PERF_SAMPLE_BASE :
//			case WinPerfConstants.PERF_AVERAGE_BASE :
//			case WinPerfConstants.PERF_AVERAGE_BULK :
//			case WinPerfConstants.PERF_COUNTER_MULTI_BASE :
//			case WinPerfConstants.PERF_RAW_BASE :
//			case WinPerfConstants.PERF_COUNTER_HISTOGRAM_TYPE :
		}
	}


	/**
	 * @return Returns the formatType.
	 */
	public int getFormatType() {
		return formatType;
	}

	/**
	 * @return Returns the nativePath.
	 */
	public String getNativePath() {
		return nativePath;
	}
}
