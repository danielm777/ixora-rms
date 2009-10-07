/*
 * Created on Jan 25, 2004
 */
package com.ixora.rms.agents.websphere.v61;

/**
 * Configuration for WebSphere agent v6.1.
 * @author Daniel Moraru
 */
public class Configuration extends com.ixora.rms.agents.websphere.v60.Configuration {

	/**
	 * Empty constructor. Required to allow the associated panel
	 * to create an empty configuration instance for editing.
	 */
	public Configuration() {
		super();
	}

	/**
	 * @see com.ixora.rms.agents.websphere.v50.Configuration#setup()
	 */
	protected void setup() {
        setProperty(WAS_PROFILE, TYPE_STRING, true, true);
		setProperty(WAS_HOST, TYPE_STRING, true, false);
		setProperty(CONNECTOR_TYPE, TYPE_STRING, true, new String[] {CONNECTOR_RMI, CONNECTOR_SOAP});
		setProperty(WAS_HOME, TYPE_STRING, true);
		setProperty(WAS_CLASSPATH, TYPE_STRING, true);
		setProperty(SECURITY_ENABLED, TYPE_BOOLEAN, true);
		setProperty(TRUST_STORE_PASSWORD, TYPE_STRING, true);
		setProperty(KEY_STORE_PASSWORD, TYPE_STRING, true);
		setProperty(TRUST_STORE, TYPE_STRING, true);
		setProperty(KEY_STORE, TYPE_STRING, true);
		setProperty(SOAP_PROPS, TYPE_STRING, true);
		setProperty(SAS_PROPS, TYPE_STRING, true);
		setProperty(PROXY_RMI_PORT, TYPE_INTEGER, true);

		setInt(PORT, 8880);
		setInt(PROXY_RMI_PORT, 9998);
		setString(WAS_PROFILE, "AppSrv01");
		setString(CONNECTOR_TYPE, CONNECTOR_SOAP);
		setString(WAS_HOME, "C:/IBM/WebSphere/AppServer");
		setBoolean(SECURITY_ENABLED, false);
		setString(TRUST_STORE_PASSWORD, "WebAS");
		setString(KEY_STORE_PASSWORD, "WebAS");
		setString(TRUST_STORE, "/profiles/{profile}/etc/DummyClientTrustFile.jks");
		setString(KEY_STORE, "/profiles/{profile}/etc/DummyClientKeyFile.jks");
		setString(SOAP_PROPS, "/profiles/{profile}/properties/soap.client.props");
		setString(SAS_PROPS, "/profiles/{profile}/properties/sas.client.props");
		setString(WAS_CLASSPATH, "/profiles/{profile},/properties,/etc/(.*\\.jar),/lib/(.*\\.jar),/java/jre/lib/ext/(.*\\.jar),/runtimes/(.*\\.jar),/plugins/(.*\\.jar)");
	}
}