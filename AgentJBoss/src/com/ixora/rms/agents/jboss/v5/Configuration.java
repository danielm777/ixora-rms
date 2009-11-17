/*
 * Created on Jan 25, 2004
 */
package com.ixora.rms.agents.jboss.v5;


/**
 * Configuration for JBoss agent v5+. Sice v5.0 JBoss introduced support for JSR160.
 * @author Daniel Moraru
 */
public class Configuration extends com.ixora.rms.agents.jmxjsr160.Configuration {
	private static final long serialVersionUID = -2610563586198041607L;

	/**
	 * Empty constructor. Required to allow the associated panel
	 * to create an empty configuration instance for editing.
	 */
	public Configuration() {
		super();
		setString(JMX_CONNECTION_STRING, "service:jmx:rmi://{host}/jndi/rmi://{host}:1090/jmxconnector");
	}
}