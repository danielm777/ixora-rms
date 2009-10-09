/*
 * Created on 01-Oct-2004
 */
package com.ixora.common.typedproperties.ui;

import java.awt.Component;

import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;

import com.ixora.common.typedproperties.PropertyEntry;

/**
 * Cell renderer for the list that displays a set of values for
 * a property.
 * @author Daniel Moraru
 */
final class ValueSetListCellRenderer implements ListCellRenderer {
    /** Current entry for which possibe values are displayed */
    private PropertyEntry<?> currentEntry;
    /** Renderers */
    private CellRenderers renderers;
    /** Component name */
    private String componentName;

    /**
     * Constructor.
     * @param renderers
     */
    public ValueSetListCellRenderer() {
        super();
        this.renderers = new CellRenderers();
    }

	/**
	 * Sets the component.
	 * @param component
	 */
	public void setComponentName(String component) {
	    this.componentName = component;
	}

    /**
     * @param pe
     */
    public void setPropertyEntry(PropertyEntry<?> pe) {
        this.currentEntry = pe;
    }

    /**
     * @see javax.swing.ListCellRenderer#getListCellRendererComponent(javax.swing.JList, java.lang.Object, int, boolean, boolean)
     */
    @SuppressWarnings("unchecked")
	public Component getListCellRendererComponent(JList list, Object value,
            int index, boolean isSelected, boolean cellHasFocus) {
        int type = currentEntry.getType();
        CellComponent ret = renderers.getRenderer(type);
        if(ret instanceof CellComponentObject) {
        	((CellComponentObject)ret).setComponentName(this.componentName);
        }
        if(ret instanceof CellComponentExtended) {
        	((CellComponentExtended<?>)ret).setComponentName(this.componentName);
        }
        if(ret != null) {
        	if(isSelected) {
        	    // as the list imitates a popup menu
        	    // any selected item must be shown as having the focus
        	    cellHasFocus = true;
        	    ret.setBackground(list.getSelectionBackground());
        	    ret.setForeground(list.getSelectionForeground());
        	}
        	else {
        	    ret.setBackground(list.getBackground());
        	    ret.setForeground(list.getForeground());
        	}
            ret.setBorder((cellHasFocus)
                    ? UIManager.getBorder("List.focusCellHighlightBorder")
                    : CellComponent.BORDER);
            ret.render(currentEntry, value);
            return ret;
        }
        return null;
    }
}
