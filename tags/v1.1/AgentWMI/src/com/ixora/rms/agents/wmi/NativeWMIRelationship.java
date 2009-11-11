/*
 * Created on 21-Aug-2005
 */
package com.ixora.rms.agents.wmi;

/**
 * NativeWMIRelationship
 * Created by native code when returning instances (and their classes)
 * related to a given instance
 */
public class NativeWMIRelationship {
	private String fClass;
	private String fInstance;

	/**
	 * @param class1
	 * @param instance
	 */
	public NativeWMIRelationship(String clazz, String instance) {
		super();
		fClass = clazz;
		fInstance = instance;
	}
	/**
	 * @return Returns the Class.
	 */
	public String getWMIClass() {
		return fClass;
	}
	/**
	 * @return Returns the Instance.
	 */
	public String getWMIInstance() {
		return fInstance;
	}
}
