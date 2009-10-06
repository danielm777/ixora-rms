/**
 * 12-Jul-2005
 */
package com.ixora.rms;

import java.io.Serializable;

import com.ixora.rms.data.CounterValue;

/**
 * @author Daniel Moraru
 */
//NOTE: this class needs setters and getters for all members
//at it will be persisted and restored for data logging...
public interface EntityDataBuffer extends Serializable {
	/**
	 * @return the data buffer
	 */
	CounterValue[][] getBuffer();

	/**
	 * @return the record definition
	 */
	RecordDefinition getDefinition();

	/**
	 * @return the entity ID
	 */
	EntityId getEntityId();

	/**
	 * Sets the buffer
	 * @param buff
	 */
	void setBuffer(CounterValue[][] buff);

	/**
	 * Sets the record definition.
	 * @param definition
	 */
	void setDefinition(RecordDefinition definition);

	/**
	 * Sets the entity ID.
	 * @param id
	 */
	void setEntityId(EntityId id);

	/**
	 * Returns an array with the values of the specified counter.
	 * @param counterId
	 * @return
	 */
	CounterValue[] getCounterValues(CounterId counterId);

	/**
	 * @return the timestamp.
	 */
	long getTimestamp();

	/**
	 * @param timestamp the timestamp
	 */
	void setTimestamp(long timestamp);

	/**
	 * Applies the time delta to this buffer.
	 * @param delta
	 * @param def
	 */
	void applyTimeDelta(long delta, RecordDefinition def);

}