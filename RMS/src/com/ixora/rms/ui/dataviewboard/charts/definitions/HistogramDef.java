/*
 * Created on 16-Jan-2005
 */
package com.ixora.rms.ui.dataviewboard.charts.definitions;

import com.ixora.common.xml.XMLAttributeDouble;
import com.ixora.common.xml.XMLAttributeInt;
import com.ixora.common.xml.XMLTag;

/**
 * HistogramDef.
 * @author Daniel Moraru
 */
public class HistogramDef extends XMLTag {
    protected XMLAttributeDouble low = new XMLAttributeDouble("low", true);
    protected XMLAttributeDouble high = new XMLAttributeDouble("high", true);
    protected XMLAttributeInt buckets = new XMLAttributeInt("buckets", true);

    /**
     * Constructs an empty object, ready to be loaded from XML
     */
    public HistogramDef() {
        super();
    }

    /**
     * Constructs an object from given values (as opposed to read it
     * from XML)
     * @param id resource to be plotted on this axis
     */
    public HistogramDef(double low, double high, int buckets) {
        this.low.setValue(low);
        this.high.setValue(high);
        this.buckets.setValue(buckets);
    }

    /**
     * @return
     */
    public double getLow() {
        return low.getDouble().doubleValue();
    }

    /**
     * @return
     */
    public double getHigh() {
        return high.getDouble().doubleValue();
    }

    /**
     * @return
     */
    public int getBuckets() {
        return buckets.getInteger().intValue();
    }
}
