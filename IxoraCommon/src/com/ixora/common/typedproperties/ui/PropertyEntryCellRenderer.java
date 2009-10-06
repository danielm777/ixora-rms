/*
 * Created on 02-Oct-2004
 */
package com.ixora.common.typedproperties.ui;

import com.ixora.common.typedproperties.PropertyEntry;

/**
 * @author Daniel Moraru
 */
public interface PropertyEntryCellRenderer {
    /**
     * Implementations must render here the value of the given property.
     * @param e
     */
    void render(PropertyEntry e);

    /**
     * Implementations must render here the given value of the given property.
     * @param e
     */
    void render(PropertyEntry e, Object value);
}
