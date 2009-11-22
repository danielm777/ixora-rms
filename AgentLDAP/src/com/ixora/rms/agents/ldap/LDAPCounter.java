package com.ixora.rms.agents.ldap;

import com.ixora.rms.CounterType;
import com.ixora.rms.agents.impl.Counter;

/**
 * LDAPCounter
 * Holds values of a LDAP attribute
 * @author Daniel Moraru
 */
public class LDAPCounter extends Counter {
	private static final long serialVersionUID = 1864059704835005117L;

	/**
	 * @param id
	 * @param description
	 * @param type
	 */
	public LDAPCounter(String id, String description, CounterType type) {
		super(id, description, type);
	}
}