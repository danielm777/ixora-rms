/*
 * Created on 30-Sep-2004
 */
package com.ixora.common.typedproperties.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.ImageIcon;
import javax.swing.JButton;

import com.ixora.common.typedproperties.PropertyEntry;

/**
 * Cell renderer used to render entries that require an extended editor.
 * @author Daniel Moraru
 */
public class CellComponentExtended<T> extends CellComponent<T> {
	private static final long serialVersionUID = -2339237286173102099L;
	/** Underlying component */
    protected CellComponent<T> display;
    /** Button that launches the external editor */
	protected JButton button;
	/** Component name */
	protected String componentName;

	/**
	 * Constructor.
	 * @param display the component that knows to display
	 * value of a certain type
	 */
	protected CellComponentExtended(CellComponent<T> display) {
	    super(new BorderLayout());
		button = new JButton("...");
		button.setPreferredSize(new Dimension(16, 16));
		this.display = display;
		this.delegate = display;
		add(display, BorderLayout.CENTER);
	    add(button, BorderLayout.EAST);
		setMinimumSize(new Dimension(32, 16));
	}

	/**
	 * Constructor.
	 * @param display the component that knows to display
	 * value of a certain type
	 * @param buttonText
	 */
	protected CellComponentExtended(
	        CellComponent<T> display,
	        String buttonText,
	        ImageIcon buttonIcon) {
	    super(new BorderLayout());
		button = new JButton(buttonText, buttonIcon);
		button.setPreferredSize(new Dimension(16, 16));
		this.display = display;
		this.delegate = display;
		add(display, BorderLayout.CENTER);
	    add(button, BorderLayout.EAST);
		setMinimumSize(new Dimension(32, 16));
	}

	/**
	 * @return the button that displays the extended editor
	 */
	JButton getButton() {
		return button;
	}

	/**
	 * @return the component used to display the value
	 */
	CellComponent<T> getDisplay() {
	    return display;
	}

    /**
     * @see com.ixora.common.typedproperties.ui.CellComponent#render(com.ixora.common.app.typedproperties.PropertyEntry, java.lang.Object)
     */
    public void render(PropertyEntry<T> e, T value) {
    	if(this.display instanceof CellComponentObject) {
    		((CellComponentObject)this.display).setComponentName(componentName);
    	}
        this.display.render(e, value);
    }

	/**
	 * Sets the component name.
	 * @param component
	 */
	public void setComponentName(String component) {
	    this.componentName = component;
	}
}