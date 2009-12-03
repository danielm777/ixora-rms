package com.ixora.rms.logging;

import java.util.Date;

import com.ixora.rms.exception.RMSException;
import com.ixora.rms.messages.Msg;

/**
 * @author Daniel Moraru
 */
public class BoundedTimeInterval {
	private long fStart;
	private long fEnd;
	
	public BoundedTimeInterval(long start, long end) throws RMSException {
		super();
		if(start == 0 || end == 0) {
			// this is for the programmer
			throw new IllegalArgumentException("Start and end time stamps must be set to non-zero values");
		}
		if(start >= end) {
			// this is for user
			throw new RMSException(Msg.ERROR_TIMESTAMP_MUST_BE_SMALLER_OR_EQUAL, new String[]{
					String.valueOf(start),
					String.valueOf(end)
			});					
		}
		fStart = start;
		fEnd = end;
	}

	public BoundedTimeInterval(long start, long end, BoundedTimeInterval limits) throws RMSException {
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
		return fStart;
	}

	public long getEnd() {
		return fEnd;
	}
}
