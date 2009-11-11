package com.ixora.rms.data;


/**
 * Counter that holds as a value a serializable object.
 * The value object should be immutable.
 * @author Daniel Moraru
 */
public class CounterValueObject implements CounterValue {
	private static final long serialVersionUID = 7393913526544206336L;
	/** Value object */
	private ValueObject value;

	/**
	 * @param value
	 */
	public CounterValueObject(ValueObject value)	{
		this.value = value;
	}

	/**
	 * @return
	 */
	public ValueObject getValue() {
		return value;
	}

	/**
	 * @param value
	 */
	public void setValue(ValueObject value) {
		this.value = value;
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
