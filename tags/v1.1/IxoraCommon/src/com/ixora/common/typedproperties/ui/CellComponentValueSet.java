/*
 * Created on 02-Oct-2004
 */
package com.ixora.common.typedproperties.ui;

import com.ixora.common.ui.UIConfiguration;

/**
 * @author Daniel Moraru
 */
public class CellComponentValueSet<T> extends CellComponentExtended<T> {
	private static final long serialVersionUID = -4955065064519229522L;

	/**
     * Constructor.
     * @param display
     */
    public CellComponentValueSet(CellComponent<T> display) {
        super(display, null, UIConfiguration.getIcon("arrowd.gif"));
    }
}
