package com.ixora.rms.data;

import java.io.Serializable;

/**
 * Counter value.
 * @author Daniel Moraru
 */
public interface CounterValue extends Serializable, Cloneable {

    /**
     * Return the value of a counter as a number
     * Note: this method will be removed
     */
    double getDouble();

    /**
     * Return the value of a counter as a string
     */
	String toString();

    /**
     * @see java.lang.Object#clone()
     */
    public Object clone();
}
