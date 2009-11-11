/*
 * Created on 31-Dec-2003
 */
package com.ixora.rms.ui.dataviewboard.exception;

import com.ixora.rms.exception.RMSException;

/**
 * FailedToPlotView.
 * @author Daniel Moraru
 */
public class FailedToPlotView extends RMSException {
	private static final long serialVersionUID = -7035034839581418788L;

	/**
	 * @param e
	 */
	public FailedToPlotView(Throwable e) {
		super(e);
	}

	/**
	 * @param s
	 * @param e
	 */
	public FailedToPlotView(String s, Throwable e) {
		super(s, e);
	}

	/**
	 * @param s
	 */
	public FailedToPlotView(String s) {
		super(s);
	}

	/**
	 * @param s
	 * @param s
	 * @param needTranslation
	 */
	public FailedToPlotView(String s, boolean needTranslation) {
		super(s, needTranslation);
	}

	/**
	 * @param component
	 * @param s
	 * @param needTranslation
	 */
	public FailedToPlotView(String component, String s, boolean needTranslation) {
		super(component, s, needTranslation);
	}

	/**
	 * @param component
	 * @param s
	 * @param tokens
	 */
	public FailedToPlotView(
	        String component, String s, String[] tokens) {
		super(component, s, tokens);
	}
}
