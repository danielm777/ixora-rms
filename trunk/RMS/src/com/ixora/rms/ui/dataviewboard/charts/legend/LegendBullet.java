/*
 * Created on Feb 21, 2004
 */
package com.ixora.rms.ui.dataviewboard.charts.legend;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Paint;

import javax.swing.JComponent;

/**
 * @author Daniel Moraru
 */
final class LegendBullet extends JComponent {;
	/** Bullet color */
	private Paint paint;

	/**
	 * Constructor.
	 * @param color
	 */
	public LegendBullet(Paint paint) {
		super();
		this.paint = paint;
	}

	/**
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	protected void paintComponent(Graphics g) {
		// paint bullet as a square
		g = g.create();
		// TODO revisit
		if(this.paint instanceof Color) {
			g.setColor((Color)this.paint);
		}
		g.fillRect(0, 0, getWidth(), getHeight());
	}

	/**
	 * @see java.awt.Component#getMinimumSize()
	 */
	public Dimension getMinimumSize() {
		return new Dimension(5, 5);
	}

	/**
	 * @see java.awt.Component#getMaximumSize()
	 */
	public Dimension getMaximumSize() {
		return new Dimension(5, 5);
	}

	/**
	 * @see java.awt.Component#getPreferredSize()
	 */
	public Dimension getPreferredSize() {
		return new Dimension(5, 5);
	}

	/**
	 * @see java.awt.Component#isOpaque()
	 */
	public boolean isOpaque() {
		return true;
	}

	/**
	 * @return
	 */
	public Color getBulletColor() {
		return this.paint instanceof Color ? (Color)this.paint : null;
	}
}
