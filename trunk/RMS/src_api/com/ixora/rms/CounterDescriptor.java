/*
 * Created on 25-Jul-2004
 */
package com.ixora.rms;

/**
 * CounterDescriptor.
 * @author Daniel Moraru
 */
public interface CounterDescriptor extends MonitoringDescriptor {
	/** The id of the timestamp */
	public static final CounterId TIMESTAMP_ID = new CounterId("#time#");

	/**
	 * @return the counter id
	 */
	CounterId getId();

	/**
	 * @return this counter's data type
	 */
	CounterType getType();

	/**
	 * @return if counter has discreet (non-continuous) values
	 */
	boolean isDiscreet();

	/**
	 * @return the name of the class for the viewboard to use when plotting
	 * this counter; if null is up to the framework to decide; it must be used
	 * only by counters that require specialized view boards.
	 */
	String getViewboardClassName();
}