/*
 * Created on Nov 7, 2004
 */
package com.ixora.rms.ui.dataviewboard.tables;

import java.util.List;

import com.ixora.rms.dataengine.definitions.QueryDef;
import com.ixora.rms.ui.dataviewboard.charts.definitions.ChartDef;
import com.ixora.rms.ui.dataviewboard.charts.definitions.RendererDef;

/**
 * QuickChartDataViewInfo constructed by the table control to build
 * charts.
 * @author Daniel Moraru
 */
public final class QuickChartDataView extends ChartDef {
	private static final long serialVersionUID = 6802975848614450997L;

	/**
	 * Default constructor to support XML.
	 */
	public QuickChartDataView() {
		super();
	}
	/**
	 * Constructor.
	 * @param name
	 * @param description
	 * @param query
	 * @param listRenderers
	 */
	public QuickChartDataView(String name, String description,
    		QueryDef query, List<RendererDef> listRenderers, String author) {
		super(name, description, query, listRenderers, author);
	}

	/**
	 * @see com.ixora.rms.client.model.DataViewInfo#getSource()
	 */
	public int getSource() {
		return DATAVIEW_SOURCE_USER;
	}
}
