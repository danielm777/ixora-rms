/**
 * 18-Feb-2006
 */
package com.ixora.rms.ui.dataviewboard.tables;

import java.awt.Color;

import com.ixora.common.ui.UIUtils;
import com.ixora.rms.ui.dataviewboard.utils.DeltaHistoryColorHandler;
import com.ixora.rms.ui.dataviewboard.utils.NumericValueDeltaHandler;
import com.ixora.rms.ui.dataviewboard.utils.TableBasedControlFormatter;

/**
 * This class is responsible for formatting table cells.
 * Used by the Swing table cell renderer, the HTML generator and any other
 * type of user interface control...
 * @author Daniel Moraru
 */
public class TableControlFormatter extends TableBasedControlFormatter {
    /** Delta history handler */
    private DeltaHistoryColorHandler fDeltaHandler;

	/**
	 * @param backgroundColor
	 * @param stripe
	 * @param up
	 * @param down
	 * @param deltaHistorySize
	 * @param maxToolTipLength
	 * @param defaultNumberFormat
	 */
	public TableControlFormatter(Color backgroundColor, Color stripe,
			Color up, Color down,
			int deltaHistorySize,
			int maxToolTipLength,
			String defaultNumberFormat) {
		super(backgroundColor, stripe, maxToolTipLength, defaultNumberFormat);
        fDeltaHandler = new DeltaHistoryColorHandler(backgroundColor, up, down, deltaHistorySize);
	}


	/**
	 * @see com.ixora.rms.ui.dataviewboard.utils.TableBasedControlFormatter#format(java.lang.Object, int, int, java.awt.Color)
	 */
	public FormattingData format(Object val, int row, int col, Color bgColor) {
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
        	// and recalculate delta scale colors
        	fDeltaHandler.configure(this.fOriginalBackColor,
        			fDeltaHandler.getUpColor(), fDeltaHandler.getDownColor(),
					fDeltaHandler.getDeltaHistoryDepth());
        }
        if(this.fStripeColor == null) {
        	this.fStripeColor = fOriginalBackColor;
        }
        if(val instanceof NumericValueDeltaHandler) {
        	NumericValueDeltaHandler dn = (NumericValueDeltaHandler)val;
        	int delta = dn.getDeltaHistory();
        	if(delta == 0) {
        		fFormattingData.fBackgroundColor = (row % 2 == 0 ? fOriginalBackColor : fStripeColor);
        	} else {
        		fFormattingData.fBackgroundColor = fDeltaHandler.getColorForDeltaHistory(delta);
        	}
        } else {
        	fFormattingData.fBackgroundColor = (row % 2 == 0 ? fOriginalBackColor : fStripeColor);
        }
		return fFormattingData;
	}

    /**
     * @param deltaHistorySize the deltaHistorySize to set.
     */
    public void setDeltaHistorySize(int deltaHistorySize) {
    	fDeltaHandler.configure(
    			this.fOriginalBackColor,
    			fDeltaHandler.getUpColor(), fDeltaHandler.getDownColor(), fDeltaHandler.getDeltaHistoryDepth());
    	fireFormattingChanged();
    }

	/**
     * @param up
     * @param down
     * @param deltaHistoryDepth
     */
    public void setColorsForHistoryScale(Color up, Color down, int deltaHistoryDepth) {
        fDeltaHandler.configure(this.fOriginalBackColor, up, down, deltaHistoryDepth);
        fireFormattingChanged();
    }

	/**
	 * @return
	 */
	public DeltaHistoryColorHandler getDeltaHandler() {
		return fDeltaHandler;
	}
}
