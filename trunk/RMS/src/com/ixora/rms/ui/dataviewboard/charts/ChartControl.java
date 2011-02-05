/*
 * Created on Aug 17, 2004
 */
package com.ixora.rms.ui.dataviewboard.charts;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.border.TitledBorder;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.Legend;
import org.jfree.chart.LegendItem;
import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.CategoryItemEntity;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.entity.PieSectionEntity;
import org.jfree.chart.entity.XYItemEntity;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.text.TextUtilities;
import org.jfree.ui.RectangleInsets;

import com.ixora.common.ComponentConfiguration;
import com.ixora.common.ConfigurationMgr;
import com.ixora.common.MessageRepository;
import com.ixora.common.ui.UIFactoryMgr;
import com.ixora.common.utils.Utils;
import com.ixora.rms.RMSComponent;
import com.ixora.rms.RMSConfigurationConstants;
import com.ixora.rms.ResourceId;
import com.ixora.rms.dataengine.external.QueryData;
import com.ixora.rms.exception.QueryException;
import com.ixora.rms.repository.DataView;
import com.ixora.rms.ui.dataviewboard.DataViewControl;
import com.ixora.rms.ui.dataviewboard.DataViewControlContext;
import com.ixora.rms.ui.dataviewboard.DataViewControlDescriptor;
import com.ixora.rms.ui.dataviewboard.charts.definitions.ChartDef;
import com.ixora.rms.ui.dataviewboard.charts.exception.ChartException;
import com.ixora.rms.ui.dataviewboard.charts.legend.ChartLegendItemInfo;
import com.ixora.rms.ui.dataviewboard.charts.legend.ChartLegendPanelDetailed;
import com.ixora.rms.ui.dataviewboard.charts.legend.ChartLegendPanelSimple;
import com.ixora.rms.ui.dataviewboard.charts.messages.Msg;
import com.ixora.rms.ui.dataviewboard.exception.FailedToCreateControl;

/**
 * ChartControl
 * @author Cristian Costache
 * @author Daniel Moraru
 */
public final class ChartControl extends DataViewControl
	implements Observer {
	private static final long serialVersionUID = -2337319043884376057L;
	private JFreeChart fChart;
	private ChartPanel fChartPanel;
	private List<RMSDataset> fDatasets;
	private ComponentConfiguration fConfig;
	private Legend fOldLegend;
	private TextTitle fTitle;
	@SuppressWarnings("unchecked")
	private List fSubtitles;
    private TitledBorder fTitledBorder;

	/** Visual detail levels */
    private static final String CHART_DETAILLEVEL_HIGHEST =
        "chartsboard.detaillevel.highest";
    private static final String CHART_DETAILLEVEL_LOWEST =
        "chartsboard.detaillevel.lowest";

    /** Event handler */
    private EventHandler fEventHandler;

	/**
	 * Event handler.
	 */
	private final class EventHandler extends MouseAdapter {
		/**
		 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
		 */
		public void mouseEntered(MouseEvent e) {
			fireControlInFocus();
		}
	}

	/**
	 * Constructor.
	 * @param owner
	 * @param listener
	 * @param context
	 * @param locator
	 * @param resourceContext
	 * @param dvi
	 * @param maxItemCount
	 * @throws ChartException
	 * @throws QueryException
	 * @throws FailedToCreateControl
	 */
	public ChartControl(
			ChartsBoard owner,
			Listener listener,
			DataViewControlContext context,
			ResourceId resourceContext, DataView dv)
		throws ChartException, QueryException, FailedToCreateControl {
	    super(owner, listener, context, resourceContext, dv);
	    fDatasets = new LinkedList<RMSDataset>();

	    // Create chart implementation based on definition
	    ChartDef chartDef = (ChartDef)dv;
	    ChartImpl chartImpl = new ChartImpl(chartDef, getRealizedQuery(), getMaximumItemCount());

		// Create the chart
		Plot plot = chartImpl.createPlot(fDatasets);
	    plot.setInsets(new Insets(1, 1, 1, 1));
		plot.setNoDataMessage(MessageRepository.get(
		        ChartsBoardComponent.NAME,
		        Msg.RMS_CHARTSBOARD_NO_DATA_AVAILABLE));
		fChart = new JFreeChart(chartDef.getName(),
				JFreeChart.DEFAULT_TITLE_FONT,
				plot, true);
		fOldLegend = fChart.getLegend();

		// Set and remember title and subtitle
		String subt = chartDef.getDescription();
		if (subt != null) {
			fChart.addSubtitle(new TextTitle(subt));
		}
		fTitle = fChart.getTitle();
		fSubtitles = fChart.getSubtitles();

		// Put the chart on its favourite panel
		fChartPanel = new ChartPanel(fChart,
				ChartPanel.DEFAULT_WIDTH,
				ChartPanel.DEFAULT_HEIGHT,
				ChartPanel.DEFAULT_MINIMUM_DRAW_WIDTH,
				ChartPanel.DEFAULT_MINIMUM_DRAW_HEIGHT,
				ChartPanel.DEFAULT_MAXIMUM_DRAW_WIDTH,
				ChartPanel.DEFAULT_MAXIMUM_DRAW_HEIGHT,
				false, // use buffer
				false, // menu - properties
				false, // menu - save
				false, // menu - print
				false, // menu - zoom
				true // tooltips
				);
		// Set tooltip delays to always show tooltips
		fChartPanel.setInitialDelay(0);
		fChartPanel.setDismissDelay(1000*1000);// 1000 seconds
		fChartPanel.setReshowDelay(0);

		getDisplayPanel().setLayout(new BorderLayout());
		getDisplayPanel().add(fChartPanel, BorderLayout.CENTER);

		this.fEventHandler = new EventHandler();
		fChartPanel.addMouseListener(fEventHandler);
		fChartPanel.addMouseListener(fPopupEventHandler);
		addMouseListener(fPopupEventHandler);
		addMouseListener(fEventHandler);

		// Generic renderer optimizations
		TextUtilities.setUseFontMetricsGetStringBounds(true);

		// Add configuration observer
		fConfig = ConfigurationMgr.get(ChartsBoardComponent.NAME);
		fConfig.addObserver(this);
		ConfigurationMgr.get(RMSComponent.NAME).addObserver(this);

		// Stuff may be set to on/off
		setAsConfigured();

		// rebuild the legend
		buildLegend();
	}

	/**
	 * @return
	 */
	private int getMaximumItemCount() {
		// get history size in minutes
		int historySize = ConfigurationMgr.getInt(ChartsBoardComponent.NAME, ChartsBoardConfigurationConstants.CHARTSBOARD_HISTORY_SIZE);
		// get sampling interval in seconds
		int samplingInterval = ConfigurationMgr.get(RMSComponent.NAME).getInt(RMSConfigurationConstants.AGENT_CONFIGURATION_SAMPLING_INERVAL);
		return Math.max(5, (historySize * 60)/samplingInterval);
	}

	/**
	 * Gets the value of the item under mouse cursor
	 * @return
	 */
	public Number valueFromPoint(int viewX, int viewY) {
	    ChartEntity ce = fChartPanel.getEntityForPoint(viewX, viewY);

	    Number value = null;
	    if (ce instanceof XYItemEntity) {
	        XYItemEntity xyItem = (XYItemEntity)ce;
	        int item = xyItem.getItem();
	        int series = xyItem.getSeriesIndex();
	        XYDataset ds = xyItem.getDataset();

	        value = new Double(ds.getYValue(series, item));

	    } else if (ce instanceof CategoryItemEntity) {
	        CategoryItemEntity catItem = (CategoryItemEntity)ce;
	        int item = catItem.getCategoryIndex();
	        int series = catItem.getCategoryIndex();
	        CategoryDataset ds = catItem.getDataset();

	        value = ds.getValue(item, series);

	    } else if (ce instanceof PieSectionEntity) {
	        PieSectionEntity pieItem = (PieSectionEntity)ce;
	        int section = pieItem.getSectionIndex();
	        PieDataset ds = pieItem.getDataset();

	        value = ds.getValue(section);
	    }

	    return value;
	}

	/**
	 * Builds the legend.
	 */
	protected void buildLegend() {
		fLegend = new com.ixora.rms.ui.dataviewboard.legend.Legend(createHmlDescription());
		fLegendPanelSimple = new ChartLegendPanelSimple(fLegend);
		fLegendPanelDetailed = new ChartLegendPanelDetailed(fLegend);
        fTitledBorder = UIFactoryMgr.createTitledBorder(getTranslatedViewName());
        fTitledBorder.setTitleFont(fTitledBorder.getTitleFont().deriveFont(Font.BOLD));
        getDisplayPanel().setBorder(fTitledBorder);
		setToolTipText(createHmlDescriptionFormatted());
	}

	/**
	 * @see com.ixora.rms.ui.dataviewboard.DataViewControl#cleanup()
	 */
	protected void cleanup() {
		super.cleanup();
	    ConfigurationMgr.get(ChartsBoardComponent.NAME).deleteObserver(this);
	    ConfigurationMgr.get(RMSComponent.NAME).deleteObserver(this);
	}

	/** Shows/hides the legend according to config */
	private void showLegend() {
		if (fConfig.getBoolean(ChartsBoardConfigurationConstants.CHARTSBOARD_LEGEND)) {
			fChart.setLegend(fOldLegend);
		} else { // remove legend
			if (fChart.getLegend() != null) {
				fOldLegend = fChart.getLegend();
				fChart.setLegend(null);
			}
		}
	}

	/** Shows/hides titles and subtitles according to config */
	@SuppressWarnings("unchecked")
	private void showTitles() {
		// title
		if (fConfig.getBoolean(ChartsBoardConfigurationConstants.CHARTSBOARD_SHOWTITLE)) {
			fChart.setTitle(fTitle);
		} else {
			if (fChart.getTitle() != null) {
				fTitle = fChart.getTitle();
				fChart.setTitle((TextTitle)null);
			}
		}

		// subtitle
		if (fConfig.getBoolean(ChartsBoardConfigurationConstants.CHARTSBOARD_SHOWSUBTITLE)) {
			fChart.setSubtitles(fSubtitles);
		} else {
			if (fChart.getSubtitles().size() > 0) {
				fSubtitles = fChart.getSubtitles();
				fChart.setSubtitles(new LinkedList());
			}
		}
	}

	/** Configures renderer as per the global settings */
	private void setRendererConfig() {
		boolean bMarkers = fConfig.getBoolean(
				ChartsBoardConfigurationConstants.CHARTSBOARD_DRAWMARKERS);

		ValueAxis rangeAxis = null;
        ValueAxis domainAxis = null;
		Plot plot = fChart.getPlot();

        // TODO add to config
        plot.setBackgroundPaint(Color.WHITE);//new Color(235, 235, 235));

		if (plot instanceof CategoryPlot) {
		    CategoryPlot catPlot = (CategoryPlot)plot;
			CategoryItemRenderer renderer = catPlot.getRenderer();
			if (renderer instanceof LineAndShapeRenderer) {
				    ((LineAndShapeRenderer)renderer).setShapesVisible(bMarkers);
            }
            renderer.setToolTipGenerator(new RMSCategoryToolTipGenerator());
			catPlot.setRangeGridlinePaint(fConfig.getColor(
			        ChartsBoardConfigurationConstants.CHARTSBOARD_GRID_LINE_COLOR));
			catPlot.setDomainGridlinePaint(fConfig.getColor(
			        ChartsBoardConfigurationConstants.CHARTSBOARD_GRID_LINE_COLOR));
			rangeAxis = catPlot.getRangeAxis();

            renderer.setStroke(new BasicStroke(1.3f,
                    BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            // set series color
            renderer.setSeriesPaint(0, Color.GREEN.darker().darker());
            renderer.setSeriesPaint(1, Color.RED.darker().darker());
            renderer.setSeriesPaint(2, Color.ORANGE);
            renderer.setSeriesPaint(3, Color.BLUE.darker());
            renderer.setSeriesPaint(4, Color.CYAN.darker());
            renderer.setSeriesPaint(5, Color.MAGENTA.darker());
            renderer.setSeriesPaint(6, Color.PINK);
		}
		else if (plot instanceof XYPlot) {
		    XYPlot xyPlot = (XYPlot)plot;
			XYItemRenderer renderer = xyPlot.getRenderer();
			if (renderer instanceof XYLineAndShapeRenderer) {
                XYLineAndShapeRenderer lineAndShapeRenderer = (XYLineAndShapeRenderer)renderer;
                lineAndShapeRenderer.setToolTipGenerator(new RMSXYToolTipGenerator());
                lineAndShapeRenderer.setDefaultShapesVisible(bMarkers);
                lineAndShapeRenderer.setDefaultShapesFilled(false);
                lineAndShapeRenderer.setDrawSeriesLineAsPath(true);
            }
			if (renderer instanceof StandardXYItemRenderer) {
                StandardXYItemRenderer xyItemRenderer = (StandardXYItemRenderer)renderer;
                xyItemRenderer.setToolTipGenerator(new RMSTimeseriesToolTipGenerator());
                xyItemRenderer.setPlotShapes(bMarkers);
                xyItemRenderer.setDefaultShapesFilled(false);
                xyItemRenderer.setDrawSeriesLineAsPath(true);
            }
			xyPlot.setRangeGridlinePaint(fConfig.getColor(
			        ChartsBoardConfigurationConstants.CHARTSBOARD_GRID_LINE_COLOR));
			xyPlot.setDomainGridlinePaint(fConfig.getColor(
			        ChartsBoardConfigurationConstants.CHARTSBOARD_GRID_LINE_COLOR));

            renderer.setStroke(new BasicStroke(1.3f,
                    BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            // set series color
            renderer.setSeriesPaint(0, Color.GREEN.darker().darker());
            renderer.setSeriesPaint(1, Color.RED.darker().darker());
            renderer.setSeriesPaint(2, Color.ORANGE);
            renderer.setSeriesPaint(3, Color.BLUE.darker());
            renderer.setSeriesPaint(4, Color.CYAN.darker());
            renderer.setSeriesPaint(5, Color.MAGENTA.darker());
            renderer.setSeriesPaint(6, Color.PINK);

			rangeAxis = xyPlot.getRangeAxis();
            domainAxis = xyPlot.getDomainAxis();

            XYDataset xyDataset = xyPlot.getDataset();
            if(xyDataset instanceof RMSXYSeriesCollection) {
            	((RMSXYSeriesCollection)xyDataset).setMaximumItemCount(getMaximumItemCount());
            }
		}
		plot.setForegroundAlpha(fConfig.getFloat(
				ChartsBoardConfigurationConstants.CHARTSBOARD_ALPHA));
		fChart.setAntiAlias(fConfig.getBoolean(
				ChartsBoardConfigurationConstants.CHARTSBOARD_ANTIALIAS));

		// Set the default formatting for range (#.##)
		String defaultRangeFormat = fConfig.getString(
				ChartsBoardConfigurationConstants.CHARTSBOARD_DEFAULTNUMBERFORMAT);
	    if (rangeAxis instanceof NumberAxis) {
	        NumberAxis numberAxis = (NumberAxis)rangeAxis;
	        if (numberAxis.getNumberFormatOverride() == null) {
	        	numberAxis.setNumberFormatOverride(new DecimalFormat(defaultRangeFormat));
	        }
	    }

        // set up axis
        if(rangeAxis != null) {
            rangeAxis.setTickMarkOutsideLength(3f);
            rangeAxis.setTickMarkPaint(Color.RED.darker().darker());
        }
        if(domainAxis != null) {
            domainAxis.setAutoRange(true);
            domainAxis.setFixedAutoRange(fConfig.getInt(
                    ChartsBoardConfigurationConstants.CHARTSBOARD_HISTORY_SIZE) * 60000);
            domainAxis.setTickMarkOutsideLength(3f);
            domainAxis.setTickMarkPaint(Color.RED.darker().darker());
        }

	}

	/** Sets all chart parameters as configured */
	private void setAsConfigured() {
		showLegend();
		showTitles();
		setRendererConfig();
		setDetailLevel();
	}

	/**
	 * Fills the internal legend with details about this control.
	 */
	@SuppressWarnings("unchecked")
	private void updateLegend()
	{
	    Plot p = fChart.getPlot();
	    LegendItemCollection lic = p.getLegendItems();

	    // Convert from jfreechart legend items to our own legend items
	    int idx = 0;
	    ChartLegendItemInfo[] legendItems = new ChartLegendItemInfo[lic.getItemCount()];
	    for (Iterator it = lic.iterator(); it.hasNext();)
	    {
            LegendItem li = (LegendItem) it.next();
            // Note: jfreechart's LegendItem also has description for items,
            // but it's unused at the moment
            Paint paint = li.getLinePaint();
            if (!li.isLineVisible()) {
            	paint = li.getFillPaint();
            }
            legendItems[idx] = new ChartLegendItemInfo(paint, li.getLabel(), li.getLabel());
            idx++;
        }
	    fLegend.setInfo(legendItems);
	}

	/**
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	public void update(Observable o, Object arg) {
		setAsConfigured();
	}

	/**
	 * @see com.ixora.rms.ui.dataviewboard.DataViewControl#handleDataAvailable(com.ixora.rms.dataengine.DataQueryExecutor.Data)
	 */
	protected void handleDataAvailable(QueryData data) {
		for(Iterator<RMSDataset> it = fDatasets.iterator(); it.hasNext();) {
			RMSDataset d = it.next();
			// inspectData returns true if a new series has been added
			if(d.inspectData(data)) {
				updateLegend();
			}
		}
	}

	/**
	 * @see com.ixora.rms.ui.dataviewboard.DataViewControl#handleExpired()
	 */
	protected void handleExpired() {
        fTitledBorder.setTitle("[Expired]" + fTitledBorder.getTitle());
        fTitledBorder.setTitleColor(Color.RED);
        revalidate();
        repaint();
	}

	/**
	 * Balances visual settings between esthetics / performance
	 */
	private void setDetailLevel() {
		String detailLevel = fConfig.getString(
				ChartsBoardConfigurationConstants.CHARTSBOARD_DETAIL_LEVEL);

		Plot	plot = fChart.getPlot();

		// Always turn off category labels and axes
	    if (plot instanceof CategoryPlot) {
			CategoryPlot	catPlot = (CategoryPlot)plot;
			catPlot.getDomainAxis().setTickLabelsVisible(false);
	    } else if (plot instanceof CategoryPlot) {
	    	//XYPlot xyPlot = (XYPlot)plot;
	    }

		if (detailLevel.equals(CHART_DETAILLEVEL_LOWEST))
		{
		    if (plot instanceof XYPlot)
		    {
				XYPlot	xyPlot = (XYPlot)plot;
				xyPlot.setRangeCrosshairVisible(false);
				xyPlot.setRangeGridlinesVisible(false);
				xyPlot.setDomainCrosshairVisible(false);
				xyPlot.setDomainGridlinesVisible(false);
				xyPlot.getRangeAxis().setTickLabelsVisible(true);
				xyPlot.getDomainAxis().setTickLabelsVisible(false);
		        xyPlot.setAxisOffset(new RectangleInsets(1.0, 1.0, 1.0, 3.0));
				xyPlot.getRangeAxis().setAxisLineVisible(false);
				xyPlot.getDomainAxis().setAxisLineVisible(false);
				xyPlot.getRangeAxis().setTickMarksVisible(false);
		        xyPlot.getDomainAxis().setTickMarksVisible(false);
		    }
		    if (plot instanceof CategoryPlot)
		    {
		        CategoryPlot	catPlot = (CategoryPlot)plot;
		        catPlot.setRangeCrosshairVisible(false);
		        catPlot.setRangeGridlinesVisible(false);
		        catPlot.setDomainGridlinesVisible(false);
		        catPlot.getRangeAxis().setTickLabelsVisible(true);
		        catPlot.getDomainAxis().setTickLabelsVisible(false);
		        catPlot.setAxisOffset(new RectangleInsets(1.0, 1.0, 1.0, 3.0));
				catPlot.getRangeAxis().setAxisLineVisible(false);
				catPlot.getDomainAxis().setAxisLineVisible(false);
				catPlot.getRangeAxis().setTickMarksVisible(false);
		        catPlot.getDomainAxis().setTickMarksVisible(false);
		    }
		}
		else if (detailLevel.equals(CHART_DETAILLEVEL_HIGHEST))
		{
		    if (plot instanceof XYPlot)
		    {
				XYPlot	xyPlot = (XYPlot)plot;
				xyPlot.setRangeCrosshairVisible(false);
				xyPlot.setRangeGridlinesVisible(true);
				xyPlot.setDomainCrosshairVisible(false);
				xyPlot.setDomainGridlinesVisible(true);
				xyPlot.getRangeAxis().setTickLabelsVisible(true);
				xyPlot.getDomainAxis().setTickLabelsVisible(true);
		        xyPlot.setAxisOffset(new RectangleInsets(3.0, 1.0, 1.0, 3.0));
				xyPlot.getRangeAxis().setAxisLineVisible(true);
				xyPlot.getDomainAxis().setAxisLineVisible(true);
				xyPlot.getRangeAxis().setTickMarksVisible(true);
		        xyPlot.getDomainAxis().setTickMarksVisible(true);
		    }
		    if (plot instanceof CategoryPlot)
		    {
		        CategoryPlot	catPlot = (CategoryPlot)plot;
		        catPlot.setRangeCrosshairVisible(false);
		        catPlot.setRangeGridlinesVisible(true);
		        catPlot.setDomainGridlinesVisible(false);
		        catPlot.getRangeAxis().setTickLabelsVisible(true);
		        catPlot.getDomainAxis().setTickLabelsVisible(false);
		        catPlot.setAxisOffset(new RectangleInsets(3.0, 1.0, 1.0, 3.0));
				catPlot.getRangeAxis().setAxisLineVisible(true);
				catPlot.getDomainAxis().setAxisLineVisible(false);
				catPlot.getRangeAxis().setTickMarksVisible(true);
		        catPlot.getDomainAxis().setTickMarksVisible(false);
		    }
		}
	}

	/**
	 * @see com.ixora.rms.ui.dataviewboard.DataViewControl#getDescriptor()
	 */
	public DataViewControlDescriptor getDescriptor() {
		ChartControlDescriptor desc = new ChartControlDescriptor();
		prepareDescriptor(desc);
		return desc;
	}

	/**
	 * @see com.ixora.rms.ui.dataviewboard.DataViewControl#setUpFromDescriptor(com.ixora.rms.ui.dataviewboard.DataViewControlDescriptor)
	 */
	public void setUpFromDescriptor(DataViewControlDescriptor desc) {
	}

	/**
	 * @see com.ixora.rms.ui.dataviewboard.DataViewControl#reset()
	 */
	protected void reset() {
		for(Iterator<RMSDataset> it = fDatasets.iterator(); it.hasNext();) {
			RMSDataset d = it.next();
			d.reset();
		}
	}

	/**
	 * @throws IOException
	 * @see com.ixora.rms.exporter.html.HTMLProvider#toHTML(java.lang.StringBuilder, java.io.File)
	 */
	@SuppressWarnings("unchecked")
	public void toHTML(StringBuilder buff, File root) throws IOException {
		super.toHTML(buff, root);
		String imgName = String.valueOf(Utils.getRandomInt(1000, 900000)) + ".png";
		ChartUtilities.saveChartAsPNG(new File(root, imgName), this.fChart, 300, 170);
		buff.append("</p><table class='chartAndLegendTable'><tr><td><div class='chart'><img src='").append(imgName).append("'>");
		buff.append("</div></td></tr><tr><td>");
		fLegendPanelDetailed.toHTML(buff, root);
		buff.append("</td></tr><tr><td>");
		// print names for items in series if necessary
		// (usually required by bar charts)
		Plot plot = this.fChart.getPlot();
		if(plot instanceof CategoryPlot) {
			CategoryPlot catPlot = (CategoryPlot)plot;
			List lst = catPlot.getCategories();
			buff.append("<b>Categories</b><br>");
			if(!Utils.isEmptyCollection(lst)) {
				for(Object catObj : lst) {
					buff.append(catObj.toString()).append("<br>");
				}
			}
		}
		buff.append("</td></tr></table>");
	}
}
