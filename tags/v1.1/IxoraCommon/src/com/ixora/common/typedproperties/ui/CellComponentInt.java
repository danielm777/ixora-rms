/*
 * Created on 30-Sep-2004
 */
package com.ixora.common.typedproperties.ui;

import javax.swing.text.NumberFormatter;

/**
 * @author Daniel Moraru
 */
final class CellComponentInt extends CellComponentNumber<Integer> {
	private static final long serialVersionUID = -2164101641590586808L;

	/**
     * Constructor.
     */
    public CellComponentInt() {
        super();
    }

    /**
     * @see com.ixora.common.typedproperties.ui.CellComponentNumber#createFormatter()
     */
    protected NumberFormatter createFormatter() {
    	NumberFormatter nf = new AllowNullNumberFormatter();
    	nf.setAllowsInvalid(true);
    	//nf.setCommitsOnValidEdit(true);
    	nf.setValueClass(Integer.class);
    	return nf;
    }
}
