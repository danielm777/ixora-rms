/*
 * Created on 31-Dec-2003
 */
package com.ixora.common.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.border.BevelBorder;

/**
 * A thin bevel border.
 * @author Daniel Moraru (initial internet source)
 */
public final class ThinBevelBorder extends BevelBorder {
	/**
	 * Constructor.
	 * @param bevelType
	 */
	public ThinBevelBorder(int bevelType) {
		super(bevelType);
	}

	/**
	 * @see javax.swing.border.Border#getBorderInsets(java.awt.Component)
	 */
	public Insets getBorderInsets(Component c) {
		return new Insets(2, 2, 2, 2);
	}

	/**
	 * @see javax.swing.border.BevelBorder#paintRaisedBevel(java.awt.Component, java.awt.Graphics, int, int, int, int)
	 */
	protected void paintRaisedBevel(
			Component c,
			Graphics g,
			int x,
			int y,
			int width,
			int height) {
		Color oldColor = g.getColor();
		int h = height;
		int w = width;
		g.translate(x, y);
		g.setColor(getHighlightOuterColor(c));
		g.drawLine(0, 0, 0, h-1);
		g.drawLine(1, 0, w-1, 0);
		g.setColor(getShadowOuterColor(c));
		g.drawLine(1, h-1, w-1, h-1);
		g.drawLine(w-1, 1, w-1, h-2);
		g.translate(-x, -y);
		g.setColor(oldColor);
	}

	/**
	 * @see javax.swing.border.BevelBorder#paintLoweredBevel(java.awt.Component, java.awt.Graphics, int, int, int, int)
	 */
	protected void paintLoweredBevel(
			Component c,
			Graphics g,
			int x,
			int y,
			int width,
			int height) {
		Color oldColor = g.getColor();
		int h = height;
		int w = width;
		g.translate(x, y);
		g.setColor(getShadowInnerColor(c));
		g.drawLine(0, 0, 0, h-1);
		g.drawLine(1, 0, w-1, 0);
		g.setColor(getHighlightInnerColor(c));
		g.drawLine(1, h-1, w-1, h-1);
		g.drawLine(w-1, 1, w-1, h-2);
		g.translate(-x, -y);
		g.setColor(oldColor);
	}
}
