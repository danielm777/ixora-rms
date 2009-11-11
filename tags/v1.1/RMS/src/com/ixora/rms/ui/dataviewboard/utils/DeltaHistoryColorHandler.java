/*
 * Created on Nov 6, 2004
 */
package com.ixora.rms.ui.dataviewboard.utils;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;

/**
 * Handles the colors needed to represent the delta history.
 * @author Daniel Moraru
 */
public class DeltaHistoryColorHandler {
    /** List of colors for delta history scale */
    private List<Color> deltaHistoryColors;
    /** The depth of the delta history */
    private int deltaHistoryDepth;
    /** Up color */
    private Color upColor;
    /** Down color */
    private Color downColor;
    /** The color used for zero delta */
    private Color neutralColor;


    /**
     * Constructor.
     * @param neutralColor the color to return for a zero delta
     * @param up
     * @param down
     * @param deltaHistorySize
     */
    public DeltaHistoryColorHandler(Color neutral, Color up, Color down, int deltaHistorySize) {
        super();
        configure(neutral, up, down, deltaHistorySize);
    }

    /**
     * Sets new parameters.
     * @param neutralColor the color to return when delta history is 0
     * @param up
     * @param down
     * @param dh
     */
    public void configure(Color neutral, Color up, Color down, int dh) {
    	this.neutralColor = neutral;
        this.deltaHistoryDepth = dh;
        this.upColor = up;
        this.downColor = down;
        int size = dh * 2 + 1;
        Color[] tmp = new Color[size];
        int i = 0;
        Color last = down;
        for(; i < dh; i++) {
            tmp[i] = last;
            last = last.brighter();
        }
        tmp[i] = neutral;
        last = up;
        for(i = size - 1; i > dh; i--) {
            tmp[i] = last;
            last = last.brighter();
        }
        this.deltaHistoryColors = Arrays.asList(tmp);
    }

    /**
     * @param deltaHistoryValue
     * @return
     */
    public Color getColorForDeltaHistory(int deltaHistoryValue) {
        // protect against mismatch between the delta history value
        // and the value used to set up this class, they must be in synch
        // but since it's an external factor double check
        if(deltaHistoryDepth == 0 || deltaHistoryValue > deltaHistoryDepth) {
            // no history depth or invalid history value
            if(deltaHistoryValue == 0) {
                return neutralColor;
            }
            if(deltaHistoryValue < 0) {
                return downColor;
            } else {
                return upColor;
            }
        }
        if(deltaHistoryValue == 0) {
        	return neutralColor;
        }
        // the sum used to calculate the index is guarranteed to be within bounds
        // because the dataHistoryValue has already been validated above
        return (Color)this.deltaHistoryColors.get(deltaHistoryValue + deltaHistoryDepth);
    }

	/**
	 * @return the deltaHistoryDepth.
	 */
	public int getDeltaHistoryDepth() {
		return deltaHistoryDepth;
	}
	/**
	 * @return the downColor.
	 */
	public Color getDownColor() {
		return downColor;
	}
	/**
	 * @return the neutralColor.
	 */
	public Color getNeutralColor() {
		return neutralColor;
	}
	/**
	 * @return the upColor.
	 */
	public Color getUpColor() {
		return upColor;
	}
}
