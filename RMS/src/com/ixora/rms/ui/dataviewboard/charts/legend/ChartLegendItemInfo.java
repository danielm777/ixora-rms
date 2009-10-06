/*
 * Created on 21-Nov-2004
 */
package com.ixora.rms.ui.dataviewboard.charts.legend;

import java.awt.Paint;

import com.ixora.rms.ui.dataviewboard.legend.LegendItemInfo;

/**
 * @author Daniel Moraru
 */
public final class ChartLegendItemInfo extends LegendItemInfo {
	/** Paint used for this counter */
	private Paint paint;

	/**
	 * Constructor.
	 * @param paint
	 * @param name
	 * @param desc
	 */
	public ChartLegendItemInfo(Paint paint, String name, String desc) {
		super(name, desc);
		this.paint = paint;
	}
	/**
	 * @return
	 */
	public Paint getPaint() {
		return paint;
	}
}
