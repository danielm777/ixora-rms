/*
 * Created on 30-May-2005
 */
package com.ixora.rms.ui.dataviewboard.charts;

import java.text.DateFormat;
import java.text.NumberFormat;

import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.data.xy.XYDataset;

import com.ixora.common.ui.UIConfiguration;
import com.ixora.common.ui.UIUtils;

/**
 * RMSXYToolTipGenerator
 */
public final class RMSXYToolTipGenerator extends StandardXYToolTipGenerator {
	private static final long serialVersionUID = 7564261268369537895L;


	/**
     * Constructor.
     */
    public RMSXYToolTipGenerator() {
        super();
    }


    /**
	 * @param formatString
	 * @param xFormat
	 * @param yFormat
	 */
	public RMSXYToolTipGenerator(String formatString, DateFormat xFormat, DateFormat yFormat) {
		super(formatString, xFormat, yFormat);
	}


	/**
	 * @param formatString
	 * @param xFormat
	 * @param yFormat
	 */
	public RMSXYToolTipGenerator(String formatString, DateFormat xFormat, NumberFormat yFormat) {
		super(formatString, xFormat, yFormat);
	}


	/**
	 * @param formatString
	 * @param xFormat
	 * @param yFormat
	 */
	public RMSXYToolTipGenerator(String formatString, NumberFormat xFormat, NumberFormat yFormat) {
		super(formatString, xFormat, yFormat);
	}


	/**
     * @see org.jfree.chart.labels.StandardXYToolTipGenerator#generateToolTip(org.jfree.data.xy.XYDataset, int, int)
     */
    public String generateToolTip(XYDataset dataset, int series, int item) {
        return UIUtils.getMultilineHtmlText(
                super.generateToolTip(dataset, series, item),
                UIConfiguration.getMaximumLineLengthForToolTipText());
    }
}
