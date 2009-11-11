package com.ixora.common.ui;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

/*
 * Created on 09-Dec-03
 */
/**
 * Based on Swing samples.
 * @author Daniel Moraru
 */
public final class LookAndFeelSwitch implements PropertyChangeListener {
	/** Component to change */
	private JComponent componentToSwitch;

	/**
	 * Constructor for LookAndFeelSwitch.
	 * @param c
	 */
	public LookAndFeelSwitch(JComponent c) {
		componentToSwitch = c;
	}

	/**
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent e) {
		String name = e.getPropertyName();
		if (name.equals("lookAndFeel")) {
			SwingUtilities.updateComponentTreeUI(componentToSwitch);
			componentToSwitch.invalidate();
			componentToSwitch.validate();
			componentToSwitch.repaint();
		}
	}
}
