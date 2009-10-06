/*
 * Created on 09-Jun-2004
 */
package com.ixora.rms.repository.messages;

/**
 * @author Daniel Moraru
 */
public interface Msg {
	public static final String REPOSITORY_ERROR_AGENT_ALREADY_INSTALLED =
		"repository.error.agent_already_installed";
	public static final String REPOSITORY_ERROR_FAILED_TO_UNINSTALL_AGENT =
		"repository.error.failed_to_uninstall_agent";
	public static final String REPOSITORY_FAILED_TO_SAVE_REPOSITORY =
		"repository.failed_to_save_repository";

	public static final String AGENT_CATEGORY_MISCELLANEOUS = "miscellaneous";
	public static final String AGENT_CATEGORY_OPERATING_SYSTEMS = "operatingSystems";
	public static final String AGENT_CATEGORY_APP_SERVERS = "appServers";
	public static final String AGENT_CATEGORY_WEB_SERVERS = "webServers";
	public static final String AGENT_CATEGORY_DATABASES = "databases";
	public static final String AGENT_CATEGORY_NETWORK = "network";
	public static final String AGENT_CATEGORY_APPLICATIONS = "applications";
}
