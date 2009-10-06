/*
 * Created on Jan 24, 2004
 */
package com.ixora.common.ui.forms;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;

import com.ixora.common.ui.SpringUtilities;
import com.ixora.common.ui.UIConfiguration;
import com.ixora.common.ui.UIFactoryMgr;
import com.ixora.common.ui.UIUtils;
import com.ixora.common.utils.Utils;

/**
 * A panel that contains name and component pairs.
 * @author Daniel Moraru
 */
public class FormPanel extends JPanel {
	// the orientation of name/value pairs
	public static final int HORIZONTAL = 0;
	public static final int VERTICAL1 = 1;
	public static final int VERTICAL2 = 2;
	/** Space between rows */
	private static final int SPACE = UIConfiguration.getPanelPadding();
	/** Orientation */
	private int orientation;
	/** Vertical alignment of the label's text */
	private int labelTextPosition;

	/**
	 * Constructor.
	 */
	public FormPanel() {
		setLayout(new SpringLayout());
		orientation = VERTICAL1;
		labelTextPosition = SwingConstants.CENTER;
	}

	/**
	 * Constructor.
	 * @param orientation the orientation of name/value pairs
	 */
	public FormPanel(int orientation) {
		this(orientation, SwingConstants.CENTER);
	}

	/**
	 * Constructor.
	 * @param orientation the orientation of name/value pairs
	 * @param labelTextPosition the vertical alignment of the label text, one of swing's position constants
	 */
	public FormPanel(int orientation, int labelTextPosition) {
		setLayout(new SpringLayout());
		if(orientation != HORIZONTAL && orientation != VERTICAL1 && orientation != VERTICAL2) {
			throw new IllegalArgumentException("invalid orientation");
		}
		this.orientation = orientation;
		this.labelTextPosition = labelTextPosition;
	}

    /**
     * Sets the name/component pairs.
     * @param names
     * @param values
     */
    public void setPairs(String[] names, Component[] values) {
        removeAll();
        addPairs(names, values);
    }

	/**
	 * Adds name, component pairs.
	 * @param names
	 * @param values
	 */
	public void addPairs(String[] names, Component[] values) {
		addPairs(names, null, values);
	}

	/**
	 * Adds name, component pairs.
	 * @param names
	 * @param tooltips
	 * @param values
	 */
	public void addPairs(String[] names, String[] tooltips, Component[] values) {
		if(names == null || values == null) {
			throw new IllegalArgumentException("null params");
		}
		if(names.length != values.length) {
			throw new IllegalArgumentException("names/values number mismatch");
		}
		if(tooltips != null) {
			if(tooltips.length != names.length) {
				throw new IllegalArgumentException("names and tooltips must have the same size");
			}
		}
		JLabel label;
		Component comp;
		String name;
		for(int i = 0; i < names.length; i++) {
			name = names[i];
			label = UIFactoryMgr.createLabel(name);
			comp = values[i];
			label.setName(name);
			if(tooltips != null) {
				String tooltip = tooltips[i];
				if(!Utils.isEmptyString(tooltip)) {
					label.setToolTipText(
							UIUtils.getMultilineHtmlText(tooltip,
							UIConfiguration.getMaximumLineLengthForToolTipText()));
				}
			}
			label.setLabelFor(comp);
			label.setVerticalAlignment(this.labelTextPosition);
			add(label);
			add(comp);
		}
		int pairs = getComponentCount() / 2;
		// lay out the panel
		if(this.orientation == VERTICAL1) {
			SpringUtilities.makeCompactGrid(
					this,
					pairs, 2, //rows, cols
					SPACE, SPACE,        //initX, initY
					SPACE, SPACE);       //xPad, yPad
		} else if(this.orientation == VERTICAL2) {
			SpringUtilities.makeCompactGrid(
					this,
					pairs * 2, 1, //rows, cols
					SPACE, SPACE,        //initX, initY
					SPACE, SPACE);       //xPad, yPad
		} else {
			SpringUtilities.makeCompactGrid(
					this,
					1, pairs * 2, //rows, cols
					SPACE, SPACE,        //initX, initY
					SPACE, SPACE);       //xPad, yPad
		}
	}

	/**
	 * @param name
	 * @return the component registered with the given name
	 */
	public Component getComponentWithName(String name) {
		int c = getComponentCount();
		Component co;
		for(int i = 0; i < c; i++) {
			co = getComponent(i);
			if(name.equals(co.getName())) {
				return ((JLabel)co).getLabelFor();
			}
		}
		return null;
	}

	/**
	 * @param name
	 * @return the label registered with the given name
	 */
	public JLabel getLabelWithName(String name) {
		int c = getComponentCount();
		Component co;
		for(int i = 0; i < c; i++) {
			co = getComponent(i);
			if(name.equals(co.getName())) {
				return (JLabel)co;
			}
		}
		return null;
	}

	/**
	 * Shows/hides the pair identified by <code>entry</code>.
	 * @param entry
	 * @param visible
	 */
	public void setVisible(String entry, boolean visible) {
		int c = getComponentCount();
		Component co;
		for(int i = 0; i < c; i++) {
			co = getComponent(i);
			if(entry.equals(co.getName())) {
				co.setVisible(visible);
				((JLabel)co).getLabelFor().setVisible(visible);
			}
		}
	}

	/**
	 * Disables/enables the given pair.
	 * @param entry
	 * @param enabled
	 */
	public void setEnabled(String entry, boolean enabled) {
		int c = getComponentCount();
		Component co;
		for(int i = 0; i < c; i++) {
			co = getComponent(i);
			if(entry.equals(co.getName())) {
				co.setEnabled(enabled);
				((JLabel)co).getLabelFor().setEnabled(enabled);
			}
		}
	}

	/**
	 * @see java.awt.Component#setEnabled(boolean)
	 */
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		int c = getComponentCount();
		Component co;
		for(int i = 0; i < c; i++) {
			co = getComponent(i);
			co.setEnabled(enabled);
		}
	}
}
