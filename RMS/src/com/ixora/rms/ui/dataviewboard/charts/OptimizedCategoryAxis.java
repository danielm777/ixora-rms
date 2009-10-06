/*
 * Created on 02-Jan-2005
 */
package com.ixora.rms.ui.dataviewboard.charts;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

import org.jfree.chart.axis.AxisState;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.ui.RectangleEdge;

/**
 * OptimizedCategoryAxis
 * Holds caches for various data, to avoid to much recalculation
 */
public class OptimizedCategoryAxis extends CategoryAxis {
    /** Cache for tickmarks */
    private List listCachedTicks;

    /**
     * Clears all caches, usually called when data was added to chart
     */
    public void configure() {
        listCachedTicks = null;
        super.configure();
    }

    /**
     * Holds a cache of tickmarks to avoid recalculating them
     * for every draw cycle.
     * @see org.jfree.chart.axis.Axis#refreshTicks(java.awt.Graphics2D, org.jfree.chart.axis.AxisState, java.awt.geom.Rectangle2D, java.awt.geom.Rectangle2D, org.jfree.ui.RectangleEdge)
     */
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
