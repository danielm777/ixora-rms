/*
 * Created on Feb 21, 2004
 */
package com.ixora.rms.ui.dataviewboard.legend;

import java.awt.Color;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.ixora.common.ui.UIConfiguration;
import com.ixora.common.ui.UIFactoryMgr;
import com.ixora.rms.ui.exporter.HTMLProvider;

/**
 * @author Daniel Moraru
 */
public class LegendEntry extends JPanel implements HTMLProvider {
	private static final long serialVersionUID = 9113650693735622102L;
	/** Padding */
	protected static final int PADDING = UIConfiguration.getPanelPadding();
	/** Number formatter */
	protected static final DecimalFormat formatter =
		new DecimalFormat("#.###");
	/** Text label */
	protected JLabel jLabelText;
	/** Value label */
	protected JLabel jLabelValue;
	/** Original color */
	private Color originalColor;

	/**
	 * Constructor.
	 */
	public LegendEntry() {
		super();
	}

	/**
	 * Constructor.
	 * @param ci
	 */
	public LegendEntry(LegendItemInfo ci) {
		super();
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		setTextLabel(ci.getItemName());
		setName(ci.getItemName());
		addPadding();
		setValueLabel();
	}

	/**
	 * Sets the text for this legend entry.
	 * @param text
	 */
	public void setText(String text) {
		this.jLabelText.setText(text);
	}

	/**
	 * Sets the value.
	 * @param val
	 */
	public void setValue(Number val) {
		jLabelValue.setText("[" + formatter.format(val) + "]");
	}

	/**
	 * @return the text
	 */
	public String getText() {
		return jLabelText.getText();
	}

	/**
	 * Highlights or resets this entry.
	 * @param h
	 */
	public void highlight(boolean h) {
		if(h) {
			jLabelText.setForeground(Color.WHITE);
		} else {
			jLabelText.setForeground(originalColor);
		}
	}

	/**
	 * @return whether or not this entry is highlighted
	 */
	public boolean isHighlighted() {
		return jLabelText.getForeground() == Color.WHITE ? true : false;
	}


	/**
	 * @see java.awt.Component#getPreferredSize()
	 */
	public Dimension getPreferredSize() {
		Dimension d = super.getPreferredSize();
		Dimension dl = jLabelText.getPreferredSize();
		return new Dimension(
				d.width,
				Math.max(d.height, dl.height));
	}

	/**
	 * @see java.awt.Component#getMaximumSize()
	 */
	public Dimension getMaximumSize() {
		Dimension d = super.getMaximumSize();
		Dimension dl = jLabelText.getPreferredSize();
		return new Dimension(d.width, dl.height);
	}

	/**
	 * @see java.awt.Component#getMinimumSize()
	 */
	public Dimension getMinimumSize() {
		Dimension d = super.getMinimumSize();
		Dimension dl = jLabelText.getMinimumSize();
		return new Dimension(
				d.width,
				Math.max(d.height, dl.height));
	}

	/**
	 * Adds padding.
	 */
	protected void addPadding() {
		add(Box.createHorizontalStrut(PADDING));
	}

	/**
	 * Adds padding.
	 * @param idx
	 */
	protected void addPadding(int idx) {
		add(Box.createHorizontalStrut(PADDING), idx);
	}

	/**
	 * Sets the value label.
	 */
	protected void setValueLabel() {
		add(this.jLabelValue = UIFactoryMgr.createLabel(""));
		originalColor = this.jLabelText.getForeground();
	}

	/**
	 * Sets the text label.
	 * @param text
	 */
	protected void setTextLabel(String text) {
		add(this.jLabelText = UIFactoryMgr.createLabel(text));
	}

	/**
	 * @see com.ixora.rms.ui.exporter.HTMLProvider#toHTML(java.lang.StringBuilder, java.io.File)
	 */
	public void toHTML(StringBuilder buff, File root) throws IOException {
	}
}
