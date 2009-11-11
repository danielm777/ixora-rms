/*
 * Created on 03-Mar-2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.ixora.rms.agents.websphere.messages;

/**
 * Messages for WebSphere agent.
 * @author Daniel Moraru
 */
public interface Msg {
// agent name
	public static final String WEBSPHEREAGENT_NAME = "agents.websphere";

// configuration entries
	public static final String WEBSPHEREAGENT_CONNECTORTYPE
		= "websphereagent.connector_type";

	public static final String WEBSPHEREAGENT_PROXY_RMI_PORT
		= "websphereagent.proxy_rmi_port";
	public static final String WEBSPHEREAGENT_SECURITYENABLED
		= "websphereagent.security_enabled";
	public static final String WEBSPHEREAGENT_WASHOME
		= "websphereagent.was_home";
	public static final String WEBSPHEREAGENT_WASCLASSPATH
		= "websphereagent.was_classpath";
	public static final String WEBSPHEREAGENT_TRUSTSTORE
		= "websphereagent.trust_store";
	public static final String WEBSPHEREAGENT_KEYSTORE
		= "websphereagent.key_store";
	public static final String WEBSPHEREAGENT_TRUSTSTOREPASSWORD
		= "websphereagent.trust_store_password";
	public static final String WEBSPHEREAGENT_KEYSTOREPASSWORD
		= "websphereagent.key_store_password";
	public static final String WEBSPHEREAGENT_SOAPPROPS
		= "websphereagent.soap_props";
    public static final String WEBSPHEREAGENT_WAS_HOST
        = "websphereagent.was_host";
	public static final String WEBSPHEREAGENT_WAS_PROFILE
		= "websphereagent.was_profile";
	public static final String WEBSPHEREAGENT_SASPROPS
		= "websphereagent.sas_props";

// errors
	public static final String WEBSPHEREAGENT_ERROR_CONNECTED_TO_WRONG_PROXY =
		"websphereagent.error.connected_to_wrong_proxy";
    public static final String WEBSPHEREAGENT_ERROR_NOT_INSTALLED_ON_HOST =
        "websphereagent.error.websphere_no_installed_on_host";
    public static final String WEBSPHEREAGENT_ERROR_SERVER_FAILED_TO_RETURN_DATA =
        "websphereagent.error.server_failed_to_return_data";
}
