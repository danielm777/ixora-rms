/*
 * Created on Jan 25, 2004
 */
package com.ixora.rms.agents.websphere.v50;

import java.util.StringTokenizer;

import com.ixora.rms.agents.utils.AuthenticationAndPortConfiguration;
import com.ixora.rms.agents.websphere.messages.Msg;

/**
 * Configuration for WebSphere agent.
 * @author Daniel Moraru
 */
public class Configuration extends AuthenticationAndPortConfiguration {
	// connector types
	public static final String CONNECTOR_RMI = "RMI";
	public static final String CONNECTOR_SOAP = "SOAP";

	// value keys
	public static final String PROXY_RMI_PORT = Msg.WEBSPHEREAGENT_PROXY_RMI_PORT;
	public static final String CONNECTOR_TYPE = Msg.WEBSPHEREAGENT_CONNECTORTYPE;
	public static final String SECURITY_ENABLED = Msg.WEBSPHEREAGENT_SECURITYENABLED;
	public static final String TRUST_STORE = Msg.WEBSPHEREAGENT_TRUSTSTORE;
	public static final String KEY_STORE = Msg.WEBSPHEREAGENT_KEYSTORE;
	public static final String TRUST_STORE_PASSWORD = Msg.WEBSPHEREAGENT_TRUSTSTOREPASSWORD;
	public static final String KEY_STORE_PASSWORD = Msg.WEBSPHEREAGENT_KEYSTOREPASSWORD;
	public static final String WAS_HOME = Msg.WEBSPHEREAGENT_WASHOME;
	public static final String WAS_CLASSPATH = Msg.WEBSPHEREAGENT_WASCLASSPATH;
	public static final String SOAP_PROPS = Msg.WEBSPHEREAGENT_SOAPPROPS;
	public static final String SAS_PROPS = Msg.WEBSPHEREAGENT_SASPROPS;
    public static final String WAS_HOST = Msg.WEBSPHEREAGENT_WAS_HOST;

	/**
	 * Empty constructor. Required to allow the associated panel
	 * to create an empty configuration instance for editing.
	 */
	public Configuration() {
		super();
		setup();
	}

	/**
	 * Sets up this configuration object.
	 */
	protected void setup() {
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
		setString(CONNECTOR_TYPE, CONNECTOR_SOAP);
		setBoolean(SECURITY_ENABLED, false);
		setString(TRUST_STORE, "/etc/DummyClientTrustFile.jks");
		setString(KEY_STORE, "/etc/DummyClientKeyFile.jks");
		setString(TRUST_STORE_PASSWORD, "WebAS");
		setString(KEY_STORE_PASSWORD, "WebAS");
		setString(SOAP_PROPS, "/properties/soap.client.props");
		setString(SAS_PROPS, "/properties/sas.client.props");
		setString(WAS_HOME, "C:/IBM/WebSphere/AppServer");
		setString(WAS_CLASSPATH, "/properties/,/lib/(.*\\.jar),/java/jre/lib/ext/(.*\\.jar)");
	}

    /**
     * @see com.ixora.rms.agents.AgentCustomConfiguration#getClasspath()
     */
    public String getClasspath() {
        String wasHome = getString(WAS_HOME);
        String tmp = getString(WAS_CLASSPATH);
        if(tmp == null) {
            return null;
        }
        StringTokenizer tok = new StringTokenizer(tmp, ",");
        StringBuffer buff = new StringBuffer();
        while(tok.hasMoreTokens()) {
            buff.append(wasHome);
            buff.append(tok.nextToken());
            if(tok.hasMoreTokens()) {
                buff.append(',');
            }
        }
        return buff.toString();
    }
}