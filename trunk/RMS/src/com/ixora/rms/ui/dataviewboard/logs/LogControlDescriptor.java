/**
 * 07-Feb-2006
 */
package com.ixora.rms.ui.dataviewboard.logs;

import com.ixora.common.ui.filter.RowFilter;
import com.ixora.rms.ui.dataviewboard.utils.TableBasedControlDescriptor;

/**
 * @author Daniel Moraru
 */
public class LogControlDescriptor extends TableBasedControlDescriptor {
	private static final long serialVersionUID = 2912272594569140361L;
	/**
	 * XML constructor.
	 */
	public LogControlDescriptor() {
		super();
	}
	/**
	 * @param sortedColIdx
	 * @param sortedDesc
	 * @param filter
	 */
	public LogControlDescriptor(int sortedColIdx, boolean sortedDesc, RowFilter filter) {
		super(sortedColIdx, sortedDesc, filter);
	}
}
