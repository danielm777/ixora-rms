/*
 * Created on 02-Jan-2005
 */
package com.ixora.rms.ui.dataviewboard.charts;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

import org.jfree.chart.axis.AxisState;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.data.Range;
import org.jfree.ui.RectangleEdge;

/**
 * OptimizedNumberAxis
 * Holds caches for various data, to avoid to much recalculation
 */
public class OptimizedNumberAxis extends NumberAxis {
	private static final long serialVersionUID = 711522303697802120L;
	/** Cache for tickmarks */
    @SuppressWarnings("unchecked")
	private List	listCachedTicks;
    /** Cache for valueToJava2D*/
    private Rectangle2D lastArea;
    private double lastAxisMin;
    private double lastAxisMax;
    private double lastP;
    private double lastQ;
    /** Cache for max tick labels */
    private double lastMaximumTickLabelWidth;
    private double lastMaximumTickLabelHeight;

    /**
     * Caches values if area requested is the same as last
     * @see org.jfree.chart.axis.ValueAxis#valueToJava2D(double, java.awt.geom.Rectangle2D, org.jfree.ui.RectangleEdge)
     */
    public double valueToJava2D(double value, Rectangle2D area, RectangleEdge edge) {
        // Store values in cache
        if (lastArea != area) {
            lastArea = area;

            Range range = getRange();
            lastAxisMin = range.getLowerBound();
            lastAxisMax = range.getUpperBound();

            double min = 0;
            double max = 0;
            if (RectangleEdge.isTopOrBottom(edge)) {
                min = area.getX();
                max = area.getMaxX();
            }
            else if (RectangleEdge.isLeftOrRight(edge)) {
                max = area.getMinY();
                min = area.getMaxY();
            }
            if (isInverted()) {
                lastP = max;
                lastQ = -(max - min) / (lastAxisMax - lastAxisMin);
            } else {
                lastP = min;
                lastQ = (max - min) / (lastAxisMax - lastAxisMin);
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
     * @see org.jfree.chart.axis.ValueAxis#findMaximumTickLabelHeight(java.util.List, java.awt.Graphics2D, java.awt.geom.Rectangle2D, boolean)
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
