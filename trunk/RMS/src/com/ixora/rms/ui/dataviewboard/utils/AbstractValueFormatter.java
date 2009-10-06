/**
 * 20-Feb-2006
 */
package com.ixora.rms.ui.dataviewboard.utils;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Daniel Moraru
 */
public abstract class AbstractValueFormatter {
	/**
	 * Listener.
	 */
	public interface Listener {
		/**
		 * Invoked when the formatting has changed.
		 */
		void formattingChanged();
	}

    /** Default format */
    protected NumberFormat fDefaultNumericFormat;
    /** Default date format */
    protected DateFormat fDefaultDateFormat;
	/** Listeners */
	protected List<Listener> fListeners;


	protected AbstractValueFormatter(String defaultNumberFormat) {
		super();
		fListeners = new LinkedList<Listener>();
        fDefaultDateFormat = DateFormat.getDateTimeInstance();
        if(defaultNumberFormat != null) {
        	this.fDefaultNumericFormat = new DecimalFormat(defaultNumberFormat);
        } else {
        	this.fDefaultNumericFormat = NumberFormat.getInstance();
        }
	}

	/**
	 * Adds a formatter listener.
	 */
	public void addListener(Listener listener) {
		if(listener != null && !fListeners.contains(listener)) {
			fListeners.add(listener);
		}
	}

	/**
	 * Removes the formatter listener.
	 */
	public void removeListener(Listener listener) {
		fListeners.remove(listener);
	}

	/**
	 * Sets the default number format.
	 * @param dnf
	 */
	public void setDefaultNumberFormat(String dnf) {
    	this.fDefaultNumericFormat = new DecimalFormat(dnf);
    	fireFormattingChanged();
	}

	/**
	 * @return the default date format
	 */
	public DateFormat getDefaultDateFormat() {
		return fDefaultDateFormat;
	}

	/**
	 * @return the default number format
	 */
	public NumberFormat getDefaultNumberFormat() {
		return fDefaultNumericFormat;
	}

    /**
	 *
	 */
	protected void fireFormattingChanged() {
		for(Listener list : fListeners) {
			list.formattingChanged();
		}
	}
}