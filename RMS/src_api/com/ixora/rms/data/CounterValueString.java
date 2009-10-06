package com.ixora.rms.data;

import java.io.UnsupportedEncodingException;

/**
 * CounterValueString
 * @author Daniel Moraru
 */
public class CounterValueString implements CounterValue {
	private String value;

	/**
	 * @param value
	 */
	public CounterValueString(String value) {
		initValue(value);
	}

	/**
	 * @param bytes
	 * @param encoding
	 */
	public CounterValueString(byte[] bytes, String encoding) {
		try {
			initValue(new String(bytes, encoding));
		} catch (UnsupportedEncodingException e) {
			// shouldn't happen
			throw new RuntimeException(e);
		}
	}

	/**
	 * @param val
	 */
	private void initValue(String val) {
		String tmp = val.trim();
		this.value = tmp.length() == 0 ? "-" : tmp;
	}

	/**
	 * @return
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value
	 */
	public void setValue(String value) {
		initValue(value);
	}

	/**
	 * @see com.ixora.rms.data.CounterValue#getDouble()
	 */
	public double getDouble() {
		return 0;
	}

	/**
	 * @see com.ixora.rms.data.CounterValue#toString()
	 */
	public String toString() {
		return value;
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
