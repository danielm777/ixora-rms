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
public final class RMSTimeseriesToolTipGenerator extends StandardXYToolTipGenerator {
	private static final long serialVersionUID = 4268487106372923338L;

	/**
     * Constructor.
     */
    public RMSTimeseriesToolTipGenerator() {
        super(DEFAULT_TOOL_TIP_FORMAT, DateFormat.getInstance(), NumberFormat.getInstance());
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
