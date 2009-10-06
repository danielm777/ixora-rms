/*
 * Created on 17-Jun-2004
 */
package com.ixora.common.update.multinode;

import java.io.Serializable;

import com.ixora.common.update.UpdatePartDescriptor;

/**
 * @author Daniel Moraru
 */
public final class UpdatePart implements Serializable {
	/** Descriptor */
	private UpdatePartDescriptor descriptor;
	/** Data */
	private byte[] data;

	/**
	 * Constructor.
	 * @param desc
	 * @param data
	 */
	public UpdatePart(UpdatePartDescriptor desc, byte[] data) {
		super();
		this.descriptor = desc;
		this.data = data;
	}

	/**
	 * @return Returns the data.
	 */
	public byte[] getData() {
		return data;
	}

	/**
	 * @return Returns the descriptor.
	 */
	public UpdatePartDescriptor getDescriptor() {
		return descriptor;
	}
}
