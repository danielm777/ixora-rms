/*
 * Created on 30-Sep-2004
 */
package com.ixora.common.typedproperties.ui;

import java.awt.Color;
import java.awt.LayoutManager;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.border.Border;

import com.ixora.common.typedproperties.PropertyEntry;


public abstract class CellComponent<T> extends JPanel implements PropertyEntryCellRenderer<T> {
	private static final long serialVersionUID = 8266669643523802263L;
	/** This border must be used by all display components in subclasses */
    protected static final Border BORDER = BorderFactory.createEmptyBorder(1, 1, 1, 1);
    /** Background color that must be used by all display components */
    protected static final Color BACKGROUND = Color.WHITE;
    /** Subclasses must set the delegate component if they have any */
    protected JComponent delegate;

    /**
     * Constructor.
     * @param lm
     */
    protected CellComponent(LayoutManager lm) {
		super(lm);
		setBorder(null);
    }

    /**
     * @see com.ixora.common.typedproperties.ui.PropertyEntryCellRenderer#render(com.ixora.common.app.typedproperties.PropertyEntry)
     */
    public void render(PropertyEntry<T> e) {
        render(e, e.getValue());
    }

	/**
	 * @see javax.swing.JComponent#setBackground(java.awt.Color)
	 */
	public void setBackground(Color bg) {
		if(delegate == null) {
			super.setBackground(bg);
		} else {
			delegate.setBackground(bg);
		}
	}

	/**
	 * @see javax.swing.JComponent#setForeground(java.awt.Color)
	 */
	public void setForeground(Color fg) {
		if(delegate == null) {
			super.setForeground(fg);
		} else {
			delegate.setForeground(fg);
		}
	}

	/**
	 * @see java.awt.Component#getBackground()
	 */
	public Color getBackground() {
		return delegate == null ? super.getBackground() : this.delegate.getBackground();
	}

	/**
	 * @see java.awt.Component#getForeground()
	 */
	public Color getForeground() {
		return delegate == null ? super.getForeground() : this.delegate.getForeground();
	}
}