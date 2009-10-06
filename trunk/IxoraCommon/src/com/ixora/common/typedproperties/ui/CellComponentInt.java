/*
 * Created on 30-Sep-2004
 */
package com.ixora.common.typedproperties.ui;

import javax.swing.text.NumberFormatter;

/**
 * @author Daniel Moraru
 */
final class CellComponentInt extends CellComponentNumber {

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
