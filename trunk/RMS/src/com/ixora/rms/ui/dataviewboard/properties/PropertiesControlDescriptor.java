/*
 * Created on 18-Mar-2005
 */
package com.ixora.rms.ui.dataviewboard.properties;

import com.ixora.common.ui.filter.RowFilter;
import com.ixora.rms.ui.dataviewboard.utils.TableBasedControlDescriptor;

/**
 * Decriptor for properties controls.
 * @author Daniel Moraru
 */
public final class PropertiesControlDescriptor extends TableBasedControlDescriptor {
	private static final long serialVersionUID = -6929700130720202750L;

	/**
	 * XML constructor.
	 */
	public PropertiesControlDescriptor() {
		super();
	}

	/**
	 * @param sortedColIdx
	 * @param sortedDesc
	 * @param filter
	 */
	public PropertiesControlDescriptor(int sortedColIdx, boolean sortedDesc, RowFilter filter) {
		super(sortedColIdx, sortedDesc, filter);
	}
}
