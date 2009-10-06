package com.ixora.rms.data;

/**
 * CounterValueDouble
 * @author Daniel Moraru
 */
public class CounterValueDouble implements CounterValue {
	/** Value */
	private double value;

	/**
	 * @param value
	 */
	public CounterValueDouble(double value) {
		this.value = value;
	}

	/**
	 * @return
	 */
	public double getValue() {
		return value;
	}

	/**
	 * @param value
	 */
	public void setValue(double value) {
		this.value = value;
	}

	/**
	 * @see com.ixora.rms.data.CounterValue#getDouble()
	 */
	public double getDouble() {
		return value;
	}

	/**
	 * @see com.ixora.rms.data.CounterValue#toString()
	 */
	public String toString() {
		return String.valueOf(value);
	}

	/**
	 * @see java.lang.Object#clone()
	 */
	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			// will not happen
			return null;
		}
	}
}
