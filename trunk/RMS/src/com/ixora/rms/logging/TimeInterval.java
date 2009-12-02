package com.ixora.rms.logging;

import java.util.Date;

import com.ixora.rms.exception.RMSException;
import com.ixora.rms.messages.Msg;

/**
 * @author Daniel Moraru
 */
public class TimeInterval {
	private long timeStart;
	private long timeEnd;
	
	public TimeInterval(long start, long end) throws RMSException {
		super();
		if(start >= end) {
			throw new RMSException(Msg.ERROR_TIMESTAMP_MUST_BE_SMALLER_OR_EQUAL, new String[]{
					String.valueOf(start),
					String.valueOf(end)
			});					
		}
	}

	public TimeInterval(long start, long end, TimeInterval limits) throws RMSException {
		this(start, end);
		if(start < limits.getStart()) {
			throw new RMSException(Msg.ERROR_TIMESTAMP_MUST_BE_GREATER_OR_EQUAL, 
					new String[]{String.valueOf(start), 
					new Date(limits.getStart()).toString()});
		}
		if(end > limits.getEnd()) {
			throw new RMSException(Msg.ERROR_TIMESTAMP_MUST_BE_SMALLER_OR_EQUAL, 
					new String[]{String.valueOf(end), 
					new Date(limits.getEnd()).toString()});
		}
	}

	public long getStart() {
		return timeStart;
	}

	public long getEnd() {
		return timeEnd;
	}
}
