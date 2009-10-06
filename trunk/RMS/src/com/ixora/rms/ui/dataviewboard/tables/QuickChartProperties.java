/*
 * Created on Nov 8, 2004
 */
package com.ixora.rms.ui.dataviewboard.tables;

import com.ixora.rms.ui.dataviewboard.charts.ChartStyle;

/**
 * @author Daniel Moraru
 */
public final class QuickChartProperties {
	private ChartStyle style;
	private String[] items;

	/**
	 * Constructor.
	 * @param style
	 * @param items
	 */
	public QuickChartProperties(ChartStyle style, String[] items) {
		super();
		this.style = style;
		this.items = items;
	}

	/**
	 * @return the items.
	 */
	public String[] getItems() {
		return items;
	}

	/**
	 * @return the style.
	 */
	public ChartStyle getStyle() {
		return style;
	}
}
