package com.ixora.rms;

import java.io.Serializable;

import com.ixora.common.logging.AppLogger;
import com.ixora.common.logging.AppLoggerFactory;


/**
 * The header of an EntityDataBufferImpl. Contains the descriptions of
 * the counters sent in the data buffer.<br>
 * @author Daniel Moraru
 */
public final class RecordDefinition implements Serializable {
	private static final long serialVersionUID = 1441931236999395287L;
	/** Logger */
	private static final AppLogger sLogger = AppLoggerFactory.getLogger(RecordDefinition.class);
	/** Counter ids */
	private CounterId[] ids;
	/**
	 * The descriptor of the entity owning this record defintion.
	 */
	private EntityDescriptor descriptor;

	/**
	 * Constructor for RecordDefinition.
	 */
	public RecordDefinition() {
		super();
	}

	/**
	 * @return the counter ids
	 */
	public CounterId[] getFields() {
		return ids;
	}

	/**
	 * @return the discrete flags
	 */
	public EntityDescriptor getEntityDescriptor() {
		return this.descriptor;
	}

	/**
	 * Sets counter ids.
	 * @param fields
	 */
	public void setFields(CounterId[] fields) {
		this.ids = fields;
	}

	/**
	 * Sets counter types.
	 * @param types
	 */
	public void setEntityDescriptor(EntityDescriptor ed) {
		this.descriptor = ed;
	}

	/**
	 * Returns the index of the counter with the specified name
	 * @param counterId
	 * @return
	 */
	public int indexOf(CounterId counterId) {
		for (int i = 0; i < ids.length; i++) {
			if (counterId.equals(ids[i])) {
				return i;
			}
		}
		if(sLogger.isTraceEnabled()) {
			sLogger.error("Counter " + counterId + " was not found in the definition");
		}
 		return -1;
	}
}
