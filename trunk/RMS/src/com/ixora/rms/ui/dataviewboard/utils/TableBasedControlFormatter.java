/**
 * 19-Feb-2006
 */
package com.ixora.rms.ui.dataviewboard.utils;

import java.awt.Color;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.Format;
import java.text.NumberFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.ixora.common.ui.UIUtils;
import com.ixora.common.utils.Utils;

/**
 * @author Daniel Moraru
 */
public abstract class TableBasedControlFormatter extends AbstractValueFormatter {
	public class FormattingData {
		public Color fBackgroundColor;
		public String fText;
		public String fToolTip;
	}

	/** Original background color */
    protected Color fOriginalBackColor;
    /** Color to use for row stripping */
    protected Color fStripeColor;
    /** Cached value of the max length of a line in the tooltip */
    protected int fMaxLineLengthForToolTips;
    /** Date formats; one for each column; some are null */
	protected List<DateFormat> fColumnDateFormats;
    /** List of column formats; one for each column; some are null */
    protected List<NumberFormat> fColumnNumberFormats;
    /** The only instance of formmatting data */
	protected FormattingData fFormattingData;

	/**
	 * @param backgroundColor
	 * @param stripe
	 * @param maxToolTipLength
	 * @param defaultNumberFormat
	 */
	public TableBasedControlFormatter(
			Color backgroundColor, Color stripe,
			int maxToolTipLength,
			String defaultNumberFormat) {
		super(defaultNumberFormat);
		fOriginalBackColor = backgroundColor;
        fStripeColor = stripe;
        fMaxLineLengthForToolTips = maxToolTipLength;
		fFormattingData = new FormattingData();
        fColumnNumberFormats = new LinkedList<NumberFormat>();
        fColumnDateFormats = new LinkedList<DateFormat>();
        fListeners = new LinkedList<Listener>();
	}

	/**
	 * @param val
	 * @param row
	 * @param col
	 * @param bgColor the original background color
	 */
	public FormattingData format(Object val, int row,
			int col, Color bgColor) {
		fFormattingData.fText = String.valueOf(getTextForValue(val, col));
        String toolTipText = fFormattingData.fText;
        if(toolTipText.length() < fMaxLineLengthForToolTips) {
        	fFormattingData.fToolTip = toolTipText;
        } else {
        	fFormattingData.fToolTip = UIUtils.getMultilineHtmlText(toolTipText, fMaxLineLengthForToolTips);
        }
        if(this.fOriginalBackColor == null) {
        	// save original background color
        	this.fOriginalBackColor = bgColor;
        }
        if(this.fStripeColor == null) {
        	this.fStripeColor = fOriginalBackColor;
        }
       	fFormattingData.fBackgroundColor = (row % 2 == 0 ? fOriginalBackColor : fStripeColor);
		return fFormattingData;
	}

    /**
     * Sets the color to use for row striping
     * @param stripe
     */
    public void setRowStripeColor(Color stripe) {
    	this.fStripeColor = stripe;
    	fireFormattingChanged();
    }

    /**
	 * @see com.ixora.rms.ui.dataviewboard.utils.AbstractValueFormatter#setDefaultNumberFormat(java.lang.String)
	 */
    public void setDefaultNumberFormat(String dnf) {
    	NumberFormat newf = new DecimalFormat(dnf);
    	int idx = 0;
    	for(int i = 0; i < fColumnNumberFormats.size(); i++) {
    		Format f = fColumnNumberFormats.get(i);
    		// change only the columns who have the default formatter
    		if(f == this.fDefaultNumericFormat) {
    			this.fColumnNumberFormats.set(i, newf);
    		}
		}
    	this.fDefaultNumericFormat = newf;
    	fireFormattingChanged();
    }

    /**
     *
     */
    public void reset() {
        fColumnNumberFormats.clear();
    	fColumnDateFormats.clear();
    }

	/**
	 * @param idx
	 * @return
	 */
	public DateFormat getDateFormatForColumn(int idx) {
		return fColumnDateFormats.get(idx);
	}

    /**
     * @param numberFormats
     * @param dateFormats
     */
    public void setFormatsForColumns(
    		List<NumberFormat> numberFormats,
    		List<DateFormat> dateFormats) {
    	fColumnDateFormats = dateFormats;
    	fColumnNumberFormats = numberFormats;
    }

    /**
     * @param idx
     * @param format
     */
    public void setDateFormatForColumn(int idx, DateFormat format) {
    	fColumnDateFormats.set(idx, format);
    }

    /**
     * @param idx
     * @param format
     */
    public void setNumberFormatForColumn(int idx, NumberFormat format) {
    	fColumnNumberFormats.set(idx, format);
    }

    /**
     * @param value
     * @param col
     * @return
     */
    protected String getTextForValue(Object value, int col) {
        if(value instanceof Date) {
        	if(!Utils.isEmptyCollection(fColumnDateFormats)) {
        		DateFormat format = fColumnDateFormats.get(col);
        		if(format != null) {
        			return format.format(value);
        		}
        	} else {
        		return fDefaultDateFormat.format(value);
        	}
        } else if(value instanceof NumericValueDeltaHandler) {
        	NumericValueDeltaHandler ndh = (NumericValueDeltaHandler)value;
        	double val = ndh.getValue().getDouble();
        	if(!Utils.isEmptyCollection(fColumnNumberFormats)) {
	        	Format format = fColumnNumberFormats.get(col);
	        	if(format != null) {
	        		return format.format(val);
	        	}
        	} else {
        		return fDefaultNumericFormat.format(val);
        	}
        	return String.valueOf(val);
        }
        return String.valueOf(value);
    }
}