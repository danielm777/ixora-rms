/*
 * Created on 30-Sep-2004
 */
package com.ixora.common.typedproperties.ui;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JTextField;

import com.ixora.common.MessageRepository;
import com.ixora.common.typedproperties.PropertyEntry;
import com.ixora.common.ui.UIFactoryMgr;

/**
 * Renderer for properties whose values have a meaningful
 * text returned by their <code>toString()</code> method.
 * @author Daniel Moraru
 */
class CellComponentObject extends CellComponent {
    protected JTextField field;
    /** Component name */
    protected String componentName;

    /**
     * Constructor.
     */
    public CellComponentObject() {
        super(new BorderLayout());
        field = createTextField();
        delegate = field;
        field.setEditable(false);
        field.setBorder(BORDER);
        field.setBackground(BACKGROUND);
        add(field, BorderLayout.CENTER);
    }

    /**
     * @return
     */
    protected JTextField createTextField() {
    	return UIFactoryMgr.createTextField();
    }

    /**
     * @see com.ixora.common.typedproperties.ui.CellComponent#render(com.ixora.common.app.typedproperties.PropertyEntry, java.lang.Object)
     */
    public void render(PropertyEntry e, Object value) {
        if(value == null) {
            field.setText("");
        } else {
        	if(e.needsTranslation()) {
        		field.setText(MessageRepository.get(componentName, value.toString()));
        	} else {
        		field.setText(value.toString());
        	}
        }
    }

    /**
     * @see java.awt.Component#setBackground(java.awt.Color)
     */
    public void setBackground(Color bg) {
        if(field != null) {
            field.setBackground(bg);
        }
    }

    /**
     * @see java.awt.Component#setForeground(java.awt.Color)
     */
    public void setForeground(Color fg) {
        if(field != null) {
            field.setForeground(fg);
        }
    }

	/**
	 * Sets the component.
	 * @param component
	 */
	public void setComponentName(String component) {
	    this.componentName = component;
	}
}
