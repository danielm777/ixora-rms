/*
 * Created on 17-Jan-2005
 */
package com.ixora.rms.ui.dataviewboard.charts;

import java.awt.BasicStroke;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.Axis;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
import org.jfree.chart.labels.StandardPieItemLabelGenerator;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.MultiplePiePlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.AbstractRenderer;
import org.jfree.chart.renderer.category.AbstractCategoryItemRenderer;
import org.jfree.chart.renderer.category.AreaRenderer;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.BarRenderer3D;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.renderer.category.StackedAreaRenderer;
import org.jfree.chart.renderer.category.StackedBarRenderer;
import org.jfree.chart.renderer.category.StackedBarRenderer3D;
import org.jfree.chart.renderer.xy.AbstractXYItemRenderer;
import org.jfree.chart.renderer.xy.StackedXYAreaRenderer;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYAreaRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.xy.XYDataset;

import com.ixora.common.exception.AppRuntimeException;
import com.ixora.rms.dataengine.RealizedQuery;
import com.ixora.rms.dataengine.QueryResult;
import com.ixora.rms.dataengine.Style;
import com.ixora.rms.exception.QueryException;
import com.ixora.rms.exception.QueryIsEmptyException;
import com.ixora.rms.ui.dataviewboard.charts.definitions.RendererDef;
import com.ixora.rms.ui.dataviewboard.charts.exception.ChartCannotMixRenderersException;
import com.ixora.rms.ui.dataviewboard.charts.exception.ChartException;
import com.ixora.rms.ui.dataviewboard.charts.exception.ChartUnexpectedDatasetException;
import com.ixora.rms.ui.dataviewboard.charts.exception.ChartUnexpectedRendererException;

/**
 * RendererImpl
 */
public class RendererImpl {
	/** Actual renderer object and its source dataset */
	private AbstractRenderer fRenderer;
	private RMSDataset fDataset;
	/** Definitions that this implementation is based on */
	private RealizedQuery fCube;
	private RendererDef fRendererDef;
	/** Style of the first counter on the Y axis, to determine axis properties */
	private Style fRangeStyle;
	/** Max number of item for a XYSeries */
	private int fMaxItemCount;

	public RendererImpl(RealizedQuery cube, RendererDef rDef, int maxItem) {
		fCube = cube;
		fRendererDef = rDef;
		fMaxItemCount = maxItem;
	}

	public AbstractRenderer getRenderer() {
		return fRenderer;
	}

	public RMSDataset getDataset() {
		return fDataset;
	}

	public Plot createPlot(Plot plotDefault)
		throws ChartException, QueryException
	{
		String type = fRendererDef.getType();
		if (type.equalsIgnoreCase(ChartStyle.CATEGORY_AREA.getRenderer()))
			return createAreaPlot(plotDefault);
		if (type.equalsIgnoreCase(ChartStyle.BAR_2D.getRenderer()))
			return createBarPlot(plotDefault);
		if (type.equalsIgnoreCase(ChartStyle.BAR_3D.getRenderer()))
			return createBar3DPlot(plotDefault);
		if (type.equalsIgnoreCase("BoxAndWhisker"))
			return createBoxAndWhiskerPlot(plotDefault);
		if (type.equalsIgnoreCase("Candlestick"))
			return createCandlestickPlot(plotDefault);
		if (type.equalsIgnoreCase("CategoryStep"))
			return createCategoryStepPlot(plotDefault);
		if (type.equalsIgnoreCase("ClusteredXYBar"))
			return createClusteredXYBarPlot(plotDefault);
		if (type.equalsIgnoreCase("CyclicXYItem"))
			return createCyclicXYItemPlot(plotDefault);
		if (type.equalsIgnoreCase("DefaultCategoryItem"))
			return createDefaultCategoryItemPlot(plotDefault);
		if (type.equalsIgnoreCase("DefaultPolarItem"))
			return createDefaultPolarItemPlot(plotDefault);
		if (type.equalsIgnoreCase("DefaultXYItem"))
			return createDefaultXYItemPlot(plotDefault);
		if (type.equalsIgnoreCase("Gantt"))
			return createGanttPlot(plotDefault);
		if (type.equalsIgnoreCase("GroupedStackedBar"))
			return createGroupedStackedBarPlot(plotDefault);
		if (type.equalsIgnoreCase("HighLow"))
			return createHighLowPlot(plotDefault);
		if (type.equalsIgnoreCase("IntervalBar"))
			return createIntervalBarPlot(plotDefault);
		if (type.equalsIgnoreCase("LayeredBar"))
			return createLayeredBarPlot(plotDefault);
		if (type.equalsIgnoreCase("Level"))
			return createLevelPlot(plotDefault);
		if (type.equalsIgnoreCase(ChartStyle.CATEGORY_LINE.getRenderer()))
			return createLineAndShapePlot(plotDefault);
		if (type.equalsIgnoreCase("MinMaxCategory"))
			return createMinMaxCategoryPlot(plotDefault);
		if (type.equalsIgnoreCase("Signal"))
			return createSignalPlot(plotDefault);
		if (type.equalsIgnoreCase(ChartStyle.CATEGORY_STACKED_AREA.getRenderer()))
			return createStackedAreaPlot(plotDefault);
		if (type.equalsIgnoreCase(ChartStyle.STACKED_BAR_2D.getRenderer()))
			return createStackedBarPlot(plotDefault);
		if (type.equalsIgnoreCase(ChartStyle.STACKED_BAR_3D.getRenderer()))
			return createStackedBar3DPlot(plotDefault);
		if (type.equalsIgnoreCase(ChartStyle.STACKED_XY_AREA.getRenderer())) // (2)
			return createStackedXYAreaPlot(plotDefault);
		if (type.equalsIgnoreCase("StackedXYBar"))
			return createStackedXYBarPlot(plotDefault);
		if (type.equalsIgnoreCase(ChartStyle.XY_LINE.getRenderer()))
			return createStandardXYItemPlot(plotDefault);
		if (type.equalsIgnoreCase("StatisticalBar"))
			return createStatisticalBarPlot(plotDefault);
		if (type.equalsIgnoreCase("WaferMap"))
			return createWaferMapPlot(plotDefault);
		if (type.equalsIgnoreCase("WaterfallBar"))
			return createWaterfallBarPlot(plotDefault);
		if (type.equalsIgnoreCase("WindItem"))
			return createWindItemPlot(plotDefault);
		if (type.equalsIgnoreCase(ChartStyle.XY_AREA.getRenderer())) // (2)
			return createXYAreaPlot(plotDefault);
		if (type.equalsIgnoreCase("XYBar"))
			return createXYBarPlot(plotDefault);
		if (type.equalsIgnoreCase("XYBoxAndWhisker"))
			return createXYBoxAndWhiskerPlot(plotDefault);
		if (type.equalsIgnoreCase("XYBubble"))
			return createXYBubblePlot(plotDefault);
		if (type.equalsIgnoreCase("XYDifference"))
			return createXYDifferencePlot(plotDefault);
		if (type.equalsIgnoreCase("XYDot"))
			return createXYDotPlot(plotDefault);
		if (type.equalsIgnoreCase("XYLineAndShape"))
			return createXYLineAndShapePlot(plotDefault);
		if (type.equalsIgnoreCase("XYStepArea"))
			return createXYStepAreaPlot(plotDefault);
		if (type.equalsIgnoreCase("XYStep"))
			return createXYStepPlot(plotDefault);
		if (type.equalsIgnoreCase("YInterval"))
			return createYIntervalPlot(plotDefault);
		if (type.equalsIgnoreCase("PiePlot"))
			return createPiePlot(plotDefault);
		if (type.equalsIgnoreCase("PiePlot3D"))
			return createPiePlot3D(plotDefault);

		throw new ChartUnexpectedRendererException(type);
	}

	/** Helper to create a Category plot */
	private Plot createCommonCategoryPlot(
			Plot plotDefault,
			AbstractCategoryItemRenderer newRenderer,
			RMSDataset newDataset,
			Class<?> newDomainAxisClass,
			Class<?> newRangeAxisClass)
		throws ChartException, QueryException
	{
		if (!(newDataset instanceof CategoryDataset))
			throw new ChartUnexpectedDatasetException(newDataset.getClass().toString(), "CategoryDataset");

		// Create renderer and dataset
		fRenderer = newRenderer;
		fDataset = newDataset;

		// Create plot: category
		if (plotDefault == null) {
			CategoryPlot p = new CategoryPlot();

			try {
				// Create domain and range axes: always Category & Number Axis
				CategoryAxis x = (CategoryAxis)newDomainAxisClass.newInstance();
				NumberAxis y = (NumberAxis)newRangeAxisClass.newInstance();

				// Initialize range if a style exists
				if (fRangeStyle != null) {
					if (fRangeStyle.getMAX() != null)
						y.setUpperBound(fRangeStyle.getMAX().doubleValue());
					if (fRangeStyle.getMIN() != null)
						y.setLowerBound(fRangeStyle.getMIN().doubleValue());
				}

				p.setRangeCrosshairStroke(new BasicStroke(1));

				p.setDomainAxis(x);
				p.setRangeAxis(y);
			} catch (Exception e) {
				throw new AppRuntimeException(e);
			}

			// Attach data to the plot
			p.setDataset((CategoryDataset)fDataset);
			p.setRenderer((CategoryItemRenderer)fRenderer);

			// Enable tooltips
			newRenderer.setToolTipGenerator(new StandardCategoryToolTipGenerator());

			return p;
		}

		return plotDefault;
	}

	/** Helper to create a XY plot */
	private Plot createCommonXYPlot(
			Plot plotDefault,
			AbstractXYItemRenderer newRenderer,
			RMSDataset newDataset,
			Class<?> newDomainAxisClass,
			Class<?> newRangeAxisClass)
		throws ChartException, QueryException
	{
		if (!(newDataset instanceof XYDataset))
			throw new ChartUnexpectedDatasetException(newDataset.getClass().toString(), "XYDataset");

		// Create renderer and dataset
		fRenderer = newRenderer;
		fDataset = newDataset;

		// Create plot: XY
		if (plotDefault == null)
		{
			XYPlot p = new XYPlot();

			try {
				// Create domain and range axes: always Category & Number Axis
				ValueAxis x = (ValueAxis)newDomainAxisClass.newInstance();
				ValueAxis y = (ValueAxis)newRangeAxisClass.newInstance();

				// Initialize range if a style exists
				if (fRangeStyle != null)
				{
					if (fRangeStyle.getMAX() != null)
						y.setUpperBound(fRangeStyle.getMAX().doubleValue());
					if (fRangeStyle.getMIN() != null)
						y.setLowerBound(fRangeStyle.getMIN().doubleValue());
				}

				p.setDomainAxis(x);
				p.setRangeAxis(y);

				// Enable tooltips with the appropriate formatter (date or number)
				if (x instanceof DateAxis) {
					newRenderer.setToolTipGenerator(new RMSXYToolTipGenerator(
							StandardXYToolTipGenerator.DEFAULT_TOOL_TIP_FORMAT,
							DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.LONG), NumberFormat.getInstance()));
				} else {
					newRenderer.setToolTipGenerator(new RMSXYToolTipGenerator(
							StandardXYToolTipGenerator.DEFAULT_TOOL_TIP_FORMAT,
							NumberFormat.getInstance(), NumberFormat.getInstance()));
				}

			} catch (Exception e) {
				throw new AppRuntimeException(e);
			}

			// Attach data to the plot
			p.setDataset((XYDataset)fDataset);
			p.setRenderer((XYItemRenderer)fRenderer);

			return p;
		}

		return plotDefault;
	}

	/** Helper to create the appropriate plot based on resource types */
	private Plot createAnyPlot(Plot plotDefault, AbstractRenderer newRenderer)
		throws ChartException, QueryException {

		List<QueryResult>	qrList = fCube.getQueryResults();
		if (qrList.size() == 0)
			throw new QueryIsEmptyException();

		// Get the style of the first range
		if (fRendererDef.getRanges().size() > 0) {
			String rID = fRendererDef.getRanges().get(0).getId();
			fRangeStyle = fCube.getQueryResult(rID).getStyle();
		}

		// If the plot was already created just use it
		if (plotDefault != null)
		{
			if (plotDefault instanceof CategoryPlot) {
				if (!(newRenderer instanceof AbstractCategoryItemRenderer))
					throw new ChartCannotMixRenderersException();

				return createCommonCategoryPlot(
						plotDefault,
						(AbstractCategoryItemRenderer)newRenderer,
						new RMSDefaultCategoryDataset(
								fRendererDef.getDomain().getId(),
								fRendererDef.getRangesIDList()),
						null, null);
			} else {
				if (!(newRenderer instanceof AbstractXYItemRenderer))
					throw new ChartCannotMixRenderersException();

				return createCommonXYPlot(
						plotDefault,
						(AbstractXYItemRenderer)newRenderer,
						new RMSXYSeriesCollection(
								fRendererDef.getDomain().getId(),
								fRendererDef.getRangesIDList(),
								fMaxItemCount),
						null, null);
			}
		}

		// Create the appropriate domain axis based on QR's type
		Style domainStyle = fCube.getQueryResult(fRendererDef.getDomain().getId()).getStyle();
		String type = domainStyle.getType();

		Plot newPlot = null;

		if (type == null)
			type = Style.TYPE_NUMBER;

		// For strings create a Category plot with a
		if (type.equalsIgnoreCase(Style.TYPE_STRING))
		{
			if (!(newRenderer instanceof AbstractCategoryItemRenderer))
				throw new ChartUnexpectedRendererException(fRendererDef.getType(), "Category", "STRING");

			newPlot = createCommonCategoryPlot(
					plotDefault,
					(AbstractCategoryItemRenderer)newRenderer,
					new RMSDefaultCategoryDataset(fRendererDef.getDomain().getId(), fRendererDef.getRangesIDList()),
					OptimizedCategoryAxis.class,
					OptimizedNumberAxis.class);
		}
		// For dates create a XY plot with a DateAxis
		else if (type.equalsIgnoreCase(Style.TYPE_DATE))
		{
			newPlot = createCommonXYPlot(
					plotDefault,
					(AbstractXYItemRenderer)newRenderer,
					new RMSXYSeriesCollection(
							fRendererDef.getDomain().getId(),
							fRendererDef.getRangesIDList(),
							fMaxItemCount),
					OptimizedDateAxis.class,
					OptimizedNumberAxis.class);
		}
		// Numbers can be plotted on both XY and Domain plots
		else if (type.equalsIgnoreCase(Style.TYPE_NUMBER))
		{
			if (newRenderer instanceof AbstractXYItemRenderer) {
				newPlot = createCommonXYPlot(
						plotDefault,
						(AbstractXYItemRenderer)newRenderer,
						new RMSXYSeriesCollection(
								fRendererDef.getDomain().getId(),
								fRendererDef.getRangesIDList(),
								fMaxItemCount),
						OptimizedNumberAxis.class,
						OptimizedNumberAxis.class);
			} else {
				newPlot = createCommonCategoryPlot(
						plotDefault,
						(AbstractCategoryItemRenderer)newRenderer,
						new RMSDefaultCategoryDataset(fRendererDef.getDomain().getId(), fRendererDef.getRangesIDList()),
						OptimizedCategoryAxis.class,
						OptimizedNumberAxis.class);
			}
		}
		else
		{
			// Unknown type
			newPlot = createCommonXYPlot(
					plotDefault,
					(AbstractXYItemRenderer)newRenderer,
					new RMSDefaultCategoryDataset(fRendererDef.getDomain().getId(), fRendererDef.getRangesIDList()),
					OptimizedNumberAxis.class,
					OptimizedNumberAxis.class);
		}

		// Override the default formatting for axis labels
		String domainFormat = domainStyle != null ? domainStyle.getFormat() : null;
		String rangeFormat = fRangeStyle != null ? fRangeStyle.getFormat() : null;
		setPlotAxisFormatting(newPlot, domainFormat, rangeFormat);

		return newPlot;
	}

	private void setPlotAxisFormatting(Plot plot, String domainFormat, String rangeFormat)
	{
	    Axis domainAxis = null;
	    Axis rangeAxis = null;
	    if (plot instanceof XYPlot)
	    {
	        XYPlot	xyPlot = (XYPlot)plot;
	        domainAxis = xyPlot.getDomainAxis();
	        rangeAxis = xyPlot.getRangeAxis();
	    }
	    if (plot instanceof CategoryPlot)
	    {
	        CategoryPlot	catPlot = (CategoryPlot)plot;
	        domainAxis = catPlot.getDomainAxis();
	        rangeAxis = catPlot.getRangeAxis();
	    }

	    // Set format for domain axis
		if (domainFormat != null && domainFormat.length() > 0)
		{
		    if (domainAxis instanceof DateAxis)
		    {
		        DateAxis dateAxis = (DateAxis)domainAxis;
		        dateAxis.setDateFormatOverride(new SimpleDateFormat(domainFormat));
		    }
		    if (domainAxis instanceof NumberAxis)
		    {
		        NumberAxis numberAxis = (NumberAxis)domainAxis;
		        numberAxis.setNumberFormatOverride(new DecimalFormat(domainFormat));
		    }
		}

		// Set format for range axis
		if (rangeFormat != null && rangeFormat.length() > 0)
		{
		    if (rangeAxis instanceof DateAxis)
		    {
		        DateAxis dateAxis = (DateAxis)rangeAxis;
		        dateAxis.setDateFormatOverride(new SimpleDateFormat(rangeFormat));
		    }
		    if (rangeAxis instanceof NumberAxis)
		    {
		        NumberAxis numberAxis = (NumberAxis)rangeAxis;
		        numberAxis.setNumberFormatOverride(new DecimalFormat(rangeFormat));
		    }
		}
	}

	private Plot createAreaPlot(Plot plotDefault)  throws ChartException, QueryException
	{
		return createAnyPlot(plotDefault, new AreaRenderer());
	}
	private Plot createBarPlot(Plot plotDefault) throws ChartException, QueryException
	{
		return createAnyPlot(plotDefault, new BarRenderer());
	}
	private Plot createBar3DPlot(Plot plotDefault) throws ChartException, QueryException
	{
        BarRenderer3D ren = new BarRenderer3D();
        ren.setDrawBarOutline(false);
		return createAnyPlot(plotDefault, ren);
	}
	private Plot createBoxAndWhiskerPlot(Plot plotDefault) throws ChartException, QueryException
	{
		return null;
	}
	private Plot createCandlestickPlot(Plot plotDefault) throws ChartException, QueryException
	{
		return null;
	}
	private Plot createCategoryStepPlot(Plot plotDefault) throws ChartException, QueryException
	{
		return null;
	}
	private Plot createClusteredXYBarPlot(Plot plotDefault) throws ChartException, QueryException
	{
		return null;
	}
	private Plot createCyclicXYItemPlot(Plot plotDefault) throws ChartException, QueryException
	{
		return null;
	}
	private Plot createDefaultCategoryItemPlot(Plot plotDefault) throws ChartException, QueryException
	{
		return null;
	}
	private Plot createDefaultPolarItemPlot(Plot plotDefault) throws ChartException, QueryException
	{
		return null;
	}
	private Plot createDefaultXYItemPlot(Plot plotDefault) throws ChartException, QueryException
	{
		return null;
	}
	private Plot createGanttPlot(Plot plotDefault) throws ChartException, QueryException
	{
		return null;
	}
	private Plot createGroupedStackedBarPlot(Plot plotDefault) throws ChartException, QueryException
	{
		return null;
	}
	private Plot createHighLowPlot(Plot plotDefault) throws ChartException, QueryException
	{
		return null;
	}
	private Plot createIntervalBarPlot(Plot plotDefault) throws ChartException, QueryException
	{
		return null;
	}
	private Plot createLayeredBarPlot(Plot plotDefault) throws ChartException, QueryException
	{
		return null;
	}
	private Plot createLevelPlot(Plot plotDefault) throws ChartException, QueryException
	{
		return null;
	}
	private Plot createLineAndShapePlot(Plot plotDefault) throws ChartException, QueryException
	{
		LineAndShapeRenderer r = new LineAndShapeRenderer(true, true);
		return createAnyPlot(plotDefault, r);
	}
	private Plot createMinMaxCategoryPlot(Plot plotDefault)
	{
		return null;
	}
	private Plot createSignalPlot(Plot plotDefault) throws ChartException, QueryException
	{
		return null;
	}
	private Plot createStackedAreaPlot(Plot plotDefault) throws ChartException, QueryException
	{
		return createAnyPlot(plotDefault, new StackedAreaRenderer());
	}
	private Plot createStackedBarPlot(Plot plotDefault) throws ChartException, QueryException
	{
		return createAnyPlot(plotDefault, new StackedBarRenderer());
	}
	private Plot createStackedBar3DPlot(Plot plotDefault) throws ChartException, QueryException
	{
        StackedBarRenderer3D ren = new StackedBarRenderer3D();
        ren.setDrawBarOutline(false);
		return createAnyPlot(plotDefault, ren);
	}
	private Plot createStackedXYAreaPlot(Plot plotDefault) throws ChartException, QueryException
	{
		return createAnyPlot(plotDefault, new StackedXYAreaRenderer());
	}
	private Plot createStackedXYBarPlot(Plot plotDefault) throws ChartException, QueryException
	{
		return null;
	}
	private Plot createStandardXYItemPlot(Plot plotDefault) throws ChartException, QueryException
	{
		StandardXYItemRenderer r = new StandardXYItemRenderer(StandardXYItemRenderer.LINES);
        return createAnyPlot(plotDefault, r);
	}
	private Plot createStatisticalBarPlot(Plot plotDefault) throws ChartException, QueryException
	{
		return null;
	}
	private Plot createWaferMapPlot(Plot plotDefault) throws ChartException, QueryException
	{
		return null;
	}
	private Plot createWaterfallBarPlot(Plot plotDefault) throws ChartException, QueryException
	{
		return null;
	}
	private Plot createWindItemPlot(Plot plotDefault) throws ChartException, QueryException
	{
		return null;
	}
	private Plot createXYAreaPlot(Plot plotDefault) throws ChartException, QueryException
	{
		return createAnyPlot(plotDefault, new XYAreaRenderer());
	}
	private Plot createXYBarPlot(Plot plotDefault) throws ChartException, QueryException
	{
		return null;
	}
	private Plot createXYBoxAndWhiskerPlot(Plot plotDefault) throws ChartException, QueryException
	{
		return null;
	}
	private Plot createXYBubblePlot(Plot plotDefault) throws ChartException, QueryException
	{
		return null;
	}
	private Plot createXYDifferencePlot(Plot plotDefault) throws ChartException, QueryException
	{
		return null;
	}
	private Plot createXYDotPlot(Plot plotDefault) throws ChartException, QueryException
	{
		return null;
	}
	private Plot createXYLineAndShapePlot(Plot plotDefault) throws ChartException, QueryException
	{
		XYLineAndShapeRenderer r = new XYLineAndShapeRenderer();
		r.setDefaultShapesVisible(false);
		return createAnyPlot(plotDefault, r);
	}
	private Plot createXYStepAreaPlot(Plot plotDefault) throws ChartException, QueryException
	{
		return null;
	}
	private Plot createXYStepPlot(Plot plotDefault) throws ChartException, QueryException
	{
		return null;
	}
	private Plot createYIntervalPlot(Plot plotDefault) throws ChartException, QueryException
	{
		return null;
	}
	private Plot createPiePlot(Plot plotDefault) throws ChartException, QueryException
	{
		// Create pie dataset: a regular CategoryDataset
		RMSDefaultCategoryDataset catDataset = new RMSDefaultCategoryDataset(
				fRendererDef.getDomain().getId(), fRendererDef.getRangesIDList());

		fDataset = catDataset;
		fRenderer = null;

		// Create the internal plot (PiePlot)
		PiePlot internalPiePlot = new PiePlot(null);
		internalPiePlot.setToolTipGenerator(new StandardPieItemLabelGenerator());
		JFreeChart pieChart = new JFreeChart(internalPiePlot);

		// Create a multiple pie plot and link it to the chart
		MultiplePiePlot piePlot = new MultiplePiePlot(catDataset);
		piePlot.setPieChart(pieChart);

		return piePlot;
	}
	private Plot createPiePlot3D(Plot plotDefault) throws ChartException, QueryException
	{
		// Create pie dataset: a regular CategoryDataset
		RMSDefaultCategoryDataset catDataset = new RMSDefaultCategoryDataset(
				fRendererDef.getDomain().getId(), fRendererDef.getRangesIDList());

		fDataset = catDataset;
		fRenderer = null;

		// Create the internal plot (PiePlot3D)
		PiePlot internalPiePlot = new PiePlot3D(null);
		internalPiePlot.setToolTipGenerator(new StandardPieItemLabelGenerator());
		JFreeChart pieChart = new JFreeChart(internalPiePlot);

		// Create a multiple pie plot and link it to the chart
		MultiplePiePlot piePlot = new MultiplePiePlot(catDataset);
		piePlot.setPieChart(pieChart);

		return piePlot;
	}
}
