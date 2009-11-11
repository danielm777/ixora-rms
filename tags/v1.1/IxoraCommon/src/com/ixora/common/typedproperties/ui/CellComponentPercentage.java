/*
 * Created on 30-Sep-2004
 */
package com.ixora.common.typedproperties.ui;

import javax.swing.text.NumberFormatter;

/**
 * @author Daniel Moraru
 */
final class CellComponentPercentage extends CellComponentNumber<Float> {
	private static final long serialVersionUID = -3951507640125969906L;

	/**
     * Constructor.
     */
    public CellComponentPercentage() {
        super();
    }

    /**
     * @see com.ixora.common.typedproperties.ui.CellComponentNumber#createFormatter()
     */
    protected NumberFormatter createFormatter() {
    	NumberFormatter nf = new AllowNullNumberFormatter();
    	//nf.setCommitsOnValidEdit(true);
    	nf.setAllowsInvalid(true);
    	nf.setValueClass(Float.class);
    	return nf;
    }
}
