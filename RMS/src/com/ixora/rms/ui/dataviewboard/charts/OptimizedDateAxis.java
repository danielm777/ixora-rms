/*
 * Created on 02-Jan-2005
 */
package com.ixora.rms.ui.dataviewboard.charts;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

import org.jfree.chart.axis.AxisState;
import org.jfree.chart.axis.DateAxis;
import org.jfree.data.time.DateRange;
import org.jfree.ui.RectangleEdge;

/**
 * OptimizedDateAxis
 * Holds caches for various data, to avoid recalculating stuff.
 */
public class OptimizedDateAxis extends DateAxis {
	private static final long serialVersionUID = 4250417742480432891L;
	/** Cache for tickmarks */
    @SuppressWarnings("unchecked")
	private List	listCachedTicks;
    /** Cache for valueToJava2D */
    private Rectangle2D lastArea;
    private double	lastP;
    private double	lastQ;
    private double	lastAxisMin;
    private double	lastAxisMax;

    /** Cache for max tick labels */
    private double lastMaximumTickLabelWidth;
    private double lastMaximumTickLabelHeight;

    /**
     * Caches values if area requested is the same as last
     * @see org.jfree.chart.axis.ValueAxis#valueToJava2D(double, java.awt.geom.Rectangle2D, org.jfree.ui.RectangleEdge)
     */
    public double valueToJava2D(double value, Rectangle2D area, RectangleEdge edge) {

        //return super.valueToJava2D(value, area, edge);

        if (lastArea != area) {
            lastArea = area;

	        DateRange range = (DateRange) getRange();
	        lastAxisMin = range.getLowerDate().getTime();
	        lastAxisMax = range.getUpperDate().getTime();

	        if (RectangleEdge.isTopOrBottom(edge)) {
	            double min = area.getX();
	            double max = area.getMaxX();
	            if (isInverted()) {
	                lastP = max;
	                lastQ = (min - max) / (lastAxisMax - lastAxisMin);
	            } else {
	                lastP = min;
	                lastQ = (max - min) / (lastAxisMax - lastAxisMin);
	            }
	        }
	        else if (RectangleEdge.isLeftOrRight(edge)) {
	            double min = area.getMinY();
	            double max = area.getMaxY();
	            if (isInverted()) {
	                lastP = min;
	                lastQ = (max - min) / (lastAxisMax - lastAxisMin);
	            } else {
	                lastP = max;
	                lastQ = (max - min) / (lastAxisMax - lastAxisMin);
	            }
	        }
        }

        return lastP + (value - lastAxisMin) * lastQ;
    }


    /**
     * Caches last maximum tick label width, until configure is called
     * @see org.jfree.chart.axis.ValueAxis#findMaximumTickLabelWidth(java.util.List, java.awt.Graphics2D, java.awt.geom.Rectangle2D, boolean)
     */
    @SuppressWarnings("unchecked")
	protected double findMaximumTickLabelWidth(List ticks,
            Graphics2D g2,
            Rectangle2D drawArea,
            boolean vertical) {
        if (lastMaximumTickLabelWidth == 0) {
            lastMaximumTickLabelWidth = super.findMaximumTickLabelWidth(
                    ticks, g2,  drawArea, vertical);
        }
        return lastMaximumTickLabelWidth;
    }

    /**
     * Caches last maximum tick label height, until configure is called
     * @see org.jfree.chart.axis.ValueAxis#findMaximumTickLabelWidth(java.util.List, java.awt.Graphics2D, java.awt.geom.Rectangle2D, boolean)
     */
    @SuppressWarnings("unchecked")
	protected double findMaximumTickLabelHeight(List ticks,
            Graphics2D g2,
            Rectangle2D drawArea,
            boolean vertical) {
        if (lastMaximumTickLabelHeight == 0) {
            lastMaximumTickLabelHeight = super.findMaximumTickLabelHeight(
                    ticks, g2,  drawArea, vertical);
        }
        return lastMaximumTickLabelHeight;
    }

    /**
     * Clears all caches, usually called when data was added to chart
     */
    public void configure() {
        listCachedTicks = null;
        lastArea = null;
        lastMaximumTickLabelWidth = 0;
        lastMaximumTickLabelHeight = 0;
        super.configure();
    }

    /**
     * Holds a cache of tickmarks to avoid recalculating them
     * for every draw cycle.
     * @see org.jfree.chart.axis.Axis#refreshTicks(java.awt.Graphics2D, org.jfree.chart.axis.AxisState, java.awt.geom.Rectangle2D, java.awt.geom.Rectangle2D, org.jfree.ui.RectangleEdge)
     */
    @SuppressWarnings("unchecked")
	public List refreshTicks(Graphics2D g2,
            AxisState state,
            Rectangle2D plotArea,
            Rectangle2D dataArea,
            RectangleEdge edge) {

		if (listCachedTicks == null) {
		    listCachedTicks = super.refreshTicks(g2, state, plotArea, dataArea, edge);
		}
	    return listCachedTicks;
    }

}
