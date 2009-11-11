/*
 * Created on 30-Sep-2004
 */
package com.ixora.common.typedproperties.ui;

import javax.swing.text.NumberFormatter;

/**
 * @author Daniel Moraru
 */
final class CellComponentFloat extends CellComponentNumber<Float> {
	private static final long serialVersionUID = -2441968747654810610L;

	/**
     * Constructor.
     */
    public CellComponentFloat() {
        super();
    }

    /**
     * @see com.ixora.common.typedproperties.ui.CellComponentNumber#createFormatter()
     */
    protected NumberFormatter createFormatter() {
    	NumberFormatter nf = new AllowNullNumberFormatter();
    	nf.setAllowsInvalid(true);
    	//nf.setCommitsOnValidEdit(true);
    	nf.setValueClass(Float.class);
    	return nf;
    }
}
