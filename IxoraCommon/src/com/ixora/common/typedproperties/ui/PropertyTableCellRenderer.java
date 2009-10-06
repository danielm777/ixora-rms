/*
 * Created on 30-Sep-2004
 */
package com.ixora.common.typedproperties.ui;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import com.ixora.common.typedproperties.PropertyEntry;

/**
 * Table cell renderer.
 */
public final class PropertyTableCellRenderer implements TableCellRenderer {
    /** Renderers */
    private CellRenderers renderers;
    /** Component name */
    private String componentName;

    /**
     * Constructor.
     * @param r
     */
	public PropertyTableCellRenderer() {
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
	 * @see javax.swing.table.TableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
	 */
	public Component getTableCellRendererComponent(JTable table,
			Object value, boolean isSelected, boolean hasFocus, int row,
			int column) {
		if(value instanceof PropertyEntry) {
			PropertyEntry pe = (PropertyEntry)value;
		    Object set = pe.getValueSet();
	        int type = pe.getType();
	        CellComponent ret = null;
	        if(set == null) {
	            ret = renderers.getRendererExtended(type);
	        } else {
	            ret = renderers.getRendererValueSet(type);
	        }
	        if(ret != null) {
	        	if(ret instanceof CellComponentExtended) {
	        		((CellComponentExtended)ret).setComponentName(componentName);
	        	}
	        	if(ret instanceof CellComponentObject) {
	        		((CellComponentObject)ret).setComponentName(componentName);
	        	}
	            ret.render(pe);
	            prepareRenderer(ret, table, isSelected, hasFocus);
	            return ret;
	        }
		}
		return null;
	}

	/**
	 * @param cc
	 * @param table
	 * @param isSelected
	 * @param hasFocus
	 */
	private void prepareRenderer(CellComponent cc, JTable table, boolean isSelected, boolean hasFocus) {
		if(isSelected) {
			cc.setForeground(table.getSelectionForeground());
			cc.setBackground(table.getSelectionBackground());
		} else {
			cc.setForeground(table.getForeground());
			cc.setBackground(table.getBackground());
		}
	}
}