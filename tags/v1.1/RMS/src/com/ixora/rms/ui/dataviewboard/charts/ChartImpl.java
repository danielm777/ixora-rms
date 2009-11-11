/*
 * Created on 16-Jan-2005
 */
package com.ixora.rms.ui.dataviewboard.charts;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.AbstractRenderer;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.xy.XYDataset;

import com.ixora.rms.dataengine.RealizedQuery;
import com.ixora.rms.dataengine.QueryResult;
import com.ixora.rms.dataengine.Style;
import com.ixora.rms.exception.QueryException;
import com.ixora.rms.ui.dataviewboard.charts.definitions.ChartDef;
import com.ixora.rms.ui.dataviewboard.charts.definitions.RendererDef;
import com.ixora.rms.ui.dataviewboard.charts.exception.ChartException;
import com.ixora.rms.ui.dataviewboard.charts.exception.ChartMultipleRenderersException;

/**
 * ChartImpl
 * Constructs a JFreeChart Plot object, based on definitions given
 * by ChartDef and QueryDef (encapsulated in a ChartDef)
 */
public class ChartImpl {
	private List<RendererImpl>	renderers = new LinkedList<RendererImpl>();

	/**
	 * Creates a chart implementation from the given definition. If the definition
	 * contains no renderers, one will be created based on the type of results
	 * returned by the query. Assumes that the query provides at least two results,
	 * the renderer will plot the first result on the domain axis, and the others
	 * on the range axis.
	 * Renderer created will be of type:
	 *   <li><b>Bar</b> if domain is STRING</li>
	 *   <li><b>StandardXYItem</b> if domain is DATE or NUMBER</li>
	 *
	 * @param context used to complete relative ResourceIDs
	 * @param chartDef defines details of the chart (name,
	 * description, renderers and so on)
	 * @param maxItemCount for XY series
	 */
	public ChartImpl(ChartDef chartDef, RealizedQuery realizedQuery, int maxItemCount) {
		super();
		realize(chartDef, realizedQuery, maxItemCount);
	}

	/**
	 * Creates a chart implementation from the given definition. If the definition
	 * contains no renderers, one will be created based on the type of results
	 * returned by the query.
	 * @param chartDef defines details of the chart (name,
	 * description, renderers and so on)
	 * @param realizedQuery query which provides data for the chart
	 * @param maxItemCount
	 */
	private void realize(ChartDef chartDef, RealizedQuery realizedQuery, int maxItemCount) {
		// Create implementations for renderers
		for (RendererDef rd : chartDef.getRenderers()) {
			renderers.add(new RendererImpl(realizedQuery, rd, maxItemCount));
		}

		// If no renderers are defined, create one based on query results
		// Queries will typically be of two types:
		// 1. counters to be plotted on time series
		// 2. counters to be grouped by entity
		//
		// Assumption: the query will have at least two results
		//
		if (renderers.size() < 1 &&
		        realizedQuery.getQueryResults().size() > 1) {
			Iterator<QueryResult> it = realizedQuery.getQueryResults().iterator();
			QueryResult queryResult = it.next();

			// Domain is the first query result; all other results are ranges
			String			domain = queryResult.getStyle().getID();
			List<String>	ranges = new LinkedList<String>();
			while (it.hasNext()) {
				queryResult = it.next();
				ranges.add(queryResult.getStyle().getID());
			}

			// Create a time series (LineAndShape renderer)
			if (Style.TYPE_DATE.equals(queryResult.getStyle().getType())) {
				renderers.add(new RendererImpl(
						realizedQuery,
						new RendererDef(ChartStyle.XY_LINE.getRenderer(), domain, ranges),
						maxItemCount));
			}
			// Create a bar chart (Bar renderer)
			else if (Style.TYPE_STRING.equals(queryResult.getStyle().getType())) {
				renderers.add(new RendererImpl(
						realizedQuery,
						new RendererDef(ChartStyle.BAR_2D.getRenderer(), domain, ranges),
						maxItemCount));
			}
			// Generic XY renderer
			else {
				renderers.add(new RendererImpl(
						realizedQuery,
						new RendererDef(ChartStyle.XY_LINE.getRenderer(), domain, ranges),
						maxItemCount));
			}
		}
	}

	/**
	 * Helper to create a JFreeChart Plot object based on the definition
	 * of this chart and filling a list with datasets created for it.
	 *
	 * @param datasets list of RMSDatasets
	 */
	public Plot createPlot(List<RMSDataset> datasets)
		throws ChartException, QueryException {

		Plot tmpPlot = null;

		if (renderers.size() == 1) {

			// Just one renderer on one plot. Everything is
			// already initialized
			RendererImpl r = renderers.get(0);
			tmpPlot = r.createPlot(null);

			// Add to the outer list
			datasets.add(r.getDataset());
		} else if (renderers.size() > 1) {

			// Category and XY are the only Plots that support more renderers
			CategoryPlot	ctPlot = null;
			XYPlot			xyPlot = null;

			// Create the first renderer
			Iterator<RendererImpl> 	it = renderers.iterator();
			RendererImpl r = (RendererImpl) it.next();
			tmpPlot = r.createPlot(null);
			datasets.add(r.getDataset());

			// Multiple renderers on the same plot
			if (!(tmpPlot instanceof CategoryPlot) &&
				!(tmpPlot instanceof XYPlot))
				throw new ChartMultipleRenderersException();

			// One of these must be non null
			if (tmpPlot instanceof CategoryPlot)
				ctPlot = (CategoryPlot) tmpPlot;
			if (tmpPlot instanceof XYPlot)
				xyPlot = (XYPlot) tmpPlot;

			// Add renderer and dataset as secondary things on the same plot
			int i = 1;
			while (it.hasNext()) {

				r = (RendererImpl) it.next();

				// Initialize renderer and dataset based on the same plot
				r.createPlot(tmpPlot);
				AbstractRenderer renderer = r.getRenderer();
				RMSDataset dataset = r.getDataset();

				// Add to the outer list
				datasets.add(dataset);

				if (ctPlot != null) {
					ctPlot.setRenderer(i, (CategoryItemRenderer)renderer);
					ctPlot.setDataset(i, (CategoryDataset)dataset);
				} else {
					xyPlot.setRenderer(i, (XYItemRenderer)renderer);
					xyPlot.setDataset(i, (XYDataset)dataset);
				}

				i++;
			}
		}

		return tmpPlot;
	}

}
