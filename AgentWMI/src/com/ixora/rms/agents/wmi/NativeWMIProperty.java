/*
 * Created on 20-Aug-2005
 */
package com.ixora.rms.agents.wmi;

import com.ixora.rms.CounterType;

/**
 * NativeWMIProperty
 * Constructed by native code: contains the name and the type of
 * a property of a WMI class.
 */
public class NativeWMIProperty {
	private String fName;
	private String fDescription;
	private CounterType fType;

	/**
	 * @param name
	 * @param type
	 */
	public NativeWMIProperty(String name, String description, int type) {
		super();
		fName = name;
		fDescription = description;
		fType = CounterType.resolve(type);
	}

	/**
	 * @return Returns the fName.
	 */
	public String getName() {
		return fName;
	}

	/**
	 * @return Returns the fType.
	 */
	public CounterType getType() {
		return fType;
	}

	/**
	 * @return Returns the fDescription.
	 */
	public String getDescription() {
		return fDescription;
	}
}
