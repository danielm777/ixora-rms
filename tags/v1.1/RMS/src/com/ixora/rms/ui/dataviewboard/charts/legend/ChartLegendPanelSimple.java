/*
 * Created on 21-Nov-2004
 */
package com.ixora.rms.ui.dataviewboard.charts.legend;

import com.ixora.rms.ui.dataviewboard.legend.Legend;
import com.ixora.rms.ui.dataviewboard.legend.LegendEntry;
import com.ixora.rms.ui.dataviewboard.legend.LegendItemInfo;
import com.ixora.rms.ui.dataviewboard.legend.LegendPanelSimple;

/**
 * @author Daniel Moraru
 */
public final class ChartLegendPanelSimple extends LegendPanelSimple {
	private static final long serialVersionUID = 9024225902174339640L;

	/**
	 * Constructor.
	 * @param legend
	 */
	public ChartLegendPanelSimple(Legend legend) {
		super(legend);
	}

	/**
	 * @see com.ixora.rms.ui.dataviewboard.legend.LegendPanelSimple#getLegendEntry(com.ixora.rms.ui.dataviewboard.legend.LegendCounterInfo)
	 */
	protected LegendEntry getLegendEntry(LegendItemInfo ci) {
		return new ChartLegendEntry((ChartLegendItemInfo)ci);
	}
}
