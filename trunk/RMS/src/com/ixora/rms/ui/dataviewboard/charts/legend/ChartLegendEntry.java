/*
 * Created on 21-Nov-2004
 */
package com.ixora.rms.ui.dataviewboard.charts.legend;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Paint;
import java.io.File;
import java.io.IOException;

import com.ixora.common.utils.Utils;
import com.ixora.rms.ui.dataviewboard.legend.LegendEntry;

/**
 * @author Daniel Moraru
 */
public class ChartLegendEntry extends LegendEntry {
	/** Bullet */
	protected LegendBullet bullet;

	/**
	 * Constructor.
	 */
	public ChartLegendEntry() {
		super();
	}

	/**
	 * Constructor.
	 * @param ci
	 */
	public ChartLegendEntry(ChartLegendItemInfo ci) {
		super(ci);
		setBullet(ci.getPaint());
	}

	/**
	 * @param c
	 */
	protected void setBullet(Paint p) {
		add(this.bullet = new LegendBullet(p), 0);
		addPadding(1);
	}

	/**
	 * @see java.awt.Component#getPreferredSize()
	 */
	public Dimension getPreferredSize() {
		Dimension d = super.getPreferredSize();
		Dimension db = bullet.getPreferredSize();
		return new Dimension(
				d.width,
				Math.max(db.height, d.height));
	}

	/**
	 * @see java.awt.Component#getMaximumSize()
	 */
	public Dimension getMaximumSize() {
		Dimension d = super.getMaximumSize();
		Dimension db = bullet.getMaximumSize();
		return new Dimension(
				d.width,
				Math.max(db.height, d.height));
	}

	/**
	 * @see java.awt.Component#getMinimumSize()
	 */
	public Dimension getMinimumSize() {
		Dimension d = super.getMinimumSize();
		Dimension db = bullet.getMinimumSize();
		return new Dimension(
				db.width,
				Math.max(db.height, d.height));
	}

	/**
	 * @see com.ixora.rms.ui.exporter.HTMLProvider#toHTML(java.lang.StringBuilder, java.io.File)
	 */
	public void toHTML(StringBuilder buff, File root) throws IOException {
		super.toHTML(buff, root);
		String item = jLabelText.getText();
		Color color = bullet.getBulletColor();
		buff.append("<tr>");
		buff.append("<td><font color='#").append(Utils.getWebColor(color)).append("'>");
		buff.append(item);
		buff.append("</font></td>");
		buff.append("</tr>");
	}
}
