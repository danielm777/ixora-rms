/*
 * Created on 28-Dec-2004
 */
package com.ixora.rms.providers.messages;

/**
 * @author Daniel Moraru
 */
public interface Msg {
	public static final String RMS_ENUM_PROVIDERSTATE_READY =
		"providers.enum.state.ready";
	public static final String RMS_ENUM_PROVIDERSTATE_STARTED =
		"providers.enum.state.started";
	public static final String RMS_ENUM_PROVIDERSTATE_FINISHED =
		"providers.enum.state.finished";
	public static final String RMS_ENUM_PROVIDERSTATE_STOPPED =
		"providers.enum.state.stopped";
	public static final String RMS_ENUM_PROVIDERSTATE_ERROR =
		"providers.enum.state.error";
	public static final String RMS_ENUM_PROVIDERSTATE_UNKNOWN =
		"providers.enum.state.unknown";
	public static final String RMS_ENUM_PROVIDERSTATE_UNINSTALLED =
		"providers.enum.state.uninstalled";
	public static final String RMS_ENUM_PROVIDERLOCATION_LOCAL =
		"providers.enum.location.local";
	public static final String RMS_ENUM_PROVIDERLOCATION_REMOTE =
		"providers.enum.location.remote";
	public static final String ERROR_PROVIDER_NOT_ACTIVATED =
		"providers.error.provider_not_activated";
	public static final String ERROR_PROVIDER_NOT_INSTALLED =
		"providers.error.provider_not_installed";
	public static final String ERROR_INVALID_PROVIDER_CONFIGURATION =
		"providers.error.invalid_provider_configuration";
}
