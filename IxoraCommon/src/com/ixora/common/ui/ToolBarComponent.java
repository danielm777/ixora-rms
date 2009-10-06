/*
 * Created on Feb 8, 2004
 */
package com.ixora.common.ui;

import java.awt.Insets;

import javax.swing.Icon;
import javax.swing.border.Border;

/**
 * Interface that sould be implemented by all components
 * that are to be hosted inside a tool bar.
 * @author Daniel Moraru
 */
public interface ToolBarComponent {
	/**
	 * Sets the text for the main button.
	 * @param text
	 */
	void setText(String text);
	/**
	 * Sets the text for the main button.
	 * @param text
	 */
	void setIcon(Icon icon);
	/**
	 * Sets the mnemonic for the main button.
	 * @param m
	 */
	void setMnemonic(int m);
	/**
	 * Sets the margin for this component.
	 * @param insets
	 */
	void setMargin(Insets insets);
	/**
	 * Sets the rollover flag.
	 * @param rollover
	 */
	void setRolloverEnabled(boolean rollover);
	/**
	 * Not intended to be used directly but by JToolBar containers
	 */
	void setBorder(Border border);
}
