/*
 * Created on 10-Dec-03
 */
package com.ixora.rms;

import com.ixora.common.logging.AppLogger;
import com.ixora.common.logging.AppLoggerFactory;
import com.ixora.rms.data.CounterValue;

/**
 * Monitor data buffer for an entity.
 * @author Daniel Moraru
 */
public final class EntityDataBufferImpl implements EntityDataBuffer {
	/** Logger */
	private static final AppLogger sLogger = AppLoggerFactory.getLogger(EntityDataBufferImpl.class);
	/**
	 * Record definition. Null if it hasn't changed
	 * since the last sample.
	 */
	private RecordDefinition fDefinition;
	/**
	 * Monitored entity.
	 */
	private EntityId fEntity;
	/**
	 * Data buffers. Multiple samples per counter
	 * are allowed.
	 */
	private CounterValue[][] fBuffer;
	/** The timestamp of the snapshot */
	private long fTimestamp;


	/**
	 *
	 */
	public EntityDataBufferImpl() {
		super();
	}

	/**
	 * @param rd
	 * @param eid
	 * @param buff
	 * @param time
	 */
	public EntityDataBufferImpl(RecordDefinition rd, EntityId eid, CounterValue[][] buff, long time) {
		this.fDefinition = rd;
		this.fEntity = eid;
		this.fBuffer = buff;
		this.fTimestamp = time;
	}

	/**
	 * @see com.ixora.rms.EntityDataBuffer#getBuffer()
	 */
	public CounterValue[][] getBuffer() {
		return fBuffer;
	}

	/**
	 * @see com.ixora.rms.EntityDataBuffer#getDefinition()
	 */
	public RecordDefinition getDefinition() {
		return fDefinition;
	}

	/**
	 * @see com.ixora.rms.EntityDataBuffer#getEntityId()
	 */
	public EntityId getEntityId() {
		return fEntity;
	}

	/**
	 * @see com.ixora.rms.EntityDataBuffer#setBuffer(com.ixora.rms.data.CounterValue[][])
	 */
	public void setBuffer(CounterValue[][] buff) {
		fBuffer = buff;
	}

	/**
	 * @see com.ixora.rms.EntityDataBuffer#setDefinition(com.ixora.rms.RecordDefinition)
	 */
	public void setDefinition(RecordDefinition definition) {
		this.fDefinition = definition;
	}

	/**
	 * @see com.ixora.rms.EntityDataBuffer#setEntityId(com.ixora.rms.EntityId)
	 */
	public void setEntityId(EntityId id) {
		fEntity = id;
	}

	/**
	 * @see com.ixora.rms.EntityDataBuffer#getCounterValues(com.ixora.rms.CounterId)
	 */
	public CounterValue[] getCounterValues(CounterId counterId) {
		int fieldIndex = fDefinition.indexOf(counterId);
		if (fieldIndex == -1) {
			// shouldn't happen
			if(sLogger.isTraceEnabled()) {
				sLogger.error("No value was found in the buffer for counter: " + counterId);
			}
			return null;
		}
		return fBuffer[fieldIndex];
	}

	/**
	 * @see com.ixora.rms.EntityDataBuffer#getTimestamp()
	 */
	public long getTimestamp() {
		return fTimestamp;
	}

	/**
	 * @see com.ixora.rms.EntityDataBuffer#setTimestamp(long)
	 */
	public void setTimestamp(long timestamp) {
		fTimestamp = timestamp;
	}

	/**
	 * @see com.ixora.rms.EntityDataBuffer#applyTimeDelta(long, com.ixora.rms.RecordDefinition)
	 */
	public void applyTimeDelta(long delta, RecordDefinition def) {
		this.fTimestamp = this.fTimestamp - delta;
	}

	/**
	 * Debug only.
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		String retVal = new String();
		CounterId[] fields = fDefinition.getFields();
		retVal += "\n----" + fEntity.toString() + "----\n";
		for (int i = 0; i < fields.length; i++)	{
			retVal += fields[i].toString() + ": ";
			for (int j = 0; j < fBuffer[i].length; j++) {
				if (fBuffer[i][j] != null)
					retVal += fBuffer[i][j].toString() + "\t";
				else
					retVal += "<null>\t";
			}
			retVal += "\n";
		}
		retVal += "---------------------------------------------------\n";
		return retVal;
	}
}
