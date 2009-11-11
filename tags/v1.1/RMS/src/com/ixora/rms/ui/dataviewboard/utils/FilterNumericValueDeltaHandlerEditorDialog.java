/**
 * 19-Feb-2006
 */
package com.ixora.rms.ui.dataviewboard.utils;

import java.awt.Dialog;
import java.awt.Frame;

import com.ixora.common.ui.filter.Filter;
import com.ixora.common.ui.filter.FilterEditorDialogNumber;

/**
 * @author Daniel Moraru
 */
public class FilterNumericValueDeltaHandlerEditorDialog extends
		FilterEditorDialogNumber {
	private static final long serialVersionUID = 1995549812397606069L;

	/**
	 * @param parent
	 */
	public FilterNumericValueDeltaHandlerEditorDialog(Frame parent) {
		super(parent);
	}

	/**
	 * @param parent
	 */
	public FilterNumericValueDeltaHandlerEditorDialog(Dialog parent) {
		super(parent);
	}

	/**
	 * @see com.ixora.common.ui.filter.FilterEditorDialogNumber#createFilter(java.lang.Number, java.lang.Number)
	 */
	protected Filter createFilter(Number low, Number high) {
		return new FilterNumericValueDeltaHandler(low, high);
	}
}
