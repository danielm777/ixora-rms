/*
 * Created on 21-Nov-2004
 */
package com.ixora.rms.ui.dataviewboard.charts.legend;

import java.io.File;
import java.io.IOException;

import com.ixora.rms.ui.dataviewboard.legend.Legend;
import com.ixora.rms.ui.dataviewboard.legend.LegendEntry;
import com.ixora.rms.ui.dataviewboard.legend.LegendItemInfo;
import com.ixora.rms.ui.dataviewboard.legend.LegendPanelDetailed;

/**
 * @author Daniel Moraru
 */
public final class ChartLegendPanelDetailed extends LegendPanelDetailed {
	private static final long serialVersionUID = -3371538579554441993L;

	/**
	 * Constructor.
	 * @param legend
	 */
	public ChartLegendPanelDetailed(Legend legend) {
		super(legend);
	}


	/**
	 * @see com.ixora.rms.ui.dataviewboard.legend.LegendPanelDetailed#getLegendEntry(com.ixora.rms.ui.dataviewboard.legend.LegendCounterInfo)
	 */
	protected LegendEntry getLegendEntry(LegendItemInfo ci) {
		return new ChartLegendEntry((ChartLegendItemInfo)ci);
	}

	/**
	 * @see com.ixora.rms.ui.exporter.HTMLProvider#toHTML(java.lang.StringBuilder, java.io.File)
	 */
	public void toHTML(StringBuilder buff, File root) throws IOException {
		super.toHTML(buff, root);
		buff.append("<table class='legend'>");
		for(LegendEntry entry : this.legendEntries) {
			entry.toHTML(buff, root);
		}
		buff.append("</table>");
	}


}
