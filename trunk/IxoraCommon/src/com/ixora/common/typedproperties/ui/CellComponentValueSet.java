/*
 * Created on 02-Oct-2004
 */
package com.ixora.common.typedproperties.ui;

import com.ixora.common.ui.UIConfiguration;

/**
 * @author Daniel Moraru
 */
public class CellComponentValueSet extends CellComponentExtended {

    /**
     * Constructor.
     * @param display
     */
    public CellComponentValueSet(CellComponent display) {
        super(display, null, UIConfiguration.getIcon("arrowd.gif"));
    }
}
