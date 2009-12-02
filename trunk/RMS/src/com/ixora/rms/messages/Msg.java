/*
 * Created on 26-Dec-2003
 */
package com.ixora.rms.messages;

/**
 * @author Daniel Moraru
 */
public interface Msg {
	public static final String RMS_ENUM_AGENTLOCATION_LOCAL =
		"rms.enum.agentlocation.local";
	public static final String RMS_ENUM_AGENTLOCATION_REMOTE =
		"rms.enum.agentlocation.remote";
    public static final String RMS_ENUM_AGENTSTATE_ERROR =
		"rms.enum.agentstate.error";
	public static final String RMS_ENUM_AGENTSTATE_FINISHED =
		"rms.enum.agentstate.finished";
	public static final String RMS_ENUM_AGENTSTATE_READY =
		"rms.enum.agentstate.ready";
	public static final String RMS_ENUM_AGENTSTATE_STARTED =
		"rms.enum.agentstate.started";
	public static final String RMS_ENUM_AGENTSTATE_STOPPED =
		"rms.enum.agentstate.stopped";
	public static final String RMS_ENUM_AGENTSTATE_UNKNOWN =
		"rms.enum.agentstate.unknown";
	public static final String RMS_ENUM_AGENTSTATE_DEACTIVATED =
		"rms.enum.agentstate.deactivated";
	public static final String RMS_ERROR_AGENT_NOT_INSTALLED =
	    "rms.error.agent_not_installed";
    public static final String RMS_ERROR_AGENT_VERSION_NOT_INSTALLED =
        "rms.error.agent_version_not_installed";
	public static final String RMS_ERROR_AGENT_NOT_ACTIVATED =
	    "rms.error.agent_not_activated";
	public static final String RMS_ERROR_AGENT_NOT_CONFIGURED =
	    "rms.error.agent_not_configured";
	public static final String RMS_ERROR_AGENT_NOT_STARTED =
	    "rms.error.agent_not_started";
	public static final String RMS_ERROR_AGENT_ALREADY_STARTED =
	    "rms.error.agent_already_started";
	public static final String RMS_ERROR_AGENT_ALREADY_ACTIVATED =
	    "rms.error.agent_already_activated";

	public static final String RMS_ENUM_MONITORINGLEVEL_NONE =
		"rms.enum.monitoringlevel.none";
	public static final String RMS_ENUM_MONITORINGLEVEL_LOW =
		"rms.enum.monitoringlevel.low";
	public static final String RMS_ENUM_MONITORINGLEVEL_HIGH =
		"rms.enum.monitoringlevel.high";
	public static final String RMS_ENUM_MONITORINGLEVEL_MEDIUM =
		"rms.enum.monitoringlevel.medium";
	public static final String RMS_ENUM_MONITORINGLEVEL_MAXIMUM =
		"rms.enum.monitoringlevel.maximum";
	public static final String RMS_ENUM_COUNTERTYPE_DOUBLE =
		"rms.enum.countertype.double";
	public static final String RMS_ENUM_COUNTERTYPE_LONG =
		"rms.enum.countertype.long";
	public static final String RMS_ENUM_COUNTERTYPE_STRING =
		"rms.enum.countertype.string";
	public static final String RMS_ENUM_COUNTERTYPE_OBJECT =
		"rms.enum.countertype.object";
	public static final String RMS_ENUM_COUNTERTYPE_DATE =
		"rms.enum.countertype.date";
	public static final String RMS_ERROR_INVALID_ENTITY =
	    "rms.error.invalid_entity";
	public static final String RMS_ERROR_INVALID_AGENT_PACKAGE =
		"rms.error.invalid_agent_package";

	public static final String RMS_RECORD_DEFINITION_NOT_FOUND =
		"rms.error.record_definition_not_found";
	public static final String RMS_QUERY_NO_SUCH_RESULT =
		"rms.error.query_no_such_result";
	public static final String RMS_NON_EXISTENT_RESOURCEID_IN_REACTION_DEF =
		"rms.error.non_existent_resourceid_in_reaction_def";
    public static final String RMS_AGENT_DESCRIPTOR_NOT_FOUND =
        "rms.error.agent_descriptor_not_found";
	public static final String RMS_ERROR_FAILED_TO_UNINSTALL_AGENT = null;
	public static final String RMS_QUERY_IS_EMPTY =
		"rms.error.query_is_empty";
	public static final String RMS_UNREACHABLE_HOST_MANAGER =
		"rms.unreachable_host_manager";
	public static final String RMS_ERROR_NO_PARSERS_INSTALLED_FOR_PROVIDER =
		"rms.error.no_parsers_installed_for_provider";
	public static final String RMS_ERROR_PROVIDER_IS_MISSING =
		"rms.error.provider_is_missing";
	public static final String RMS_ERROR_PARSER_IS_MISSING =
		"rms.error.parser_is_missing";
	public static final String RMS_ERROR_PROVIDER_ERROR =
		"rms.error.provider_error";
	public static final String RMS_ERROR_PROVIDER_OVERLAPPING_AGENT_ENTITY =
		"rms.error.provider_overlapping_agent_entity";
	public static final String RMS_ERROR_PROVIDER_CONFIGURATION_TOKEN_REPLACEMENT_ERROR =
		"rms.error.provider_configuration_token_replacement_error";
	public static final String RMS_QUERY_CONTAINS_DUPLICATE_ID =
		"rms.error.query_contains_duplicate_id";

	public static final String RMS_TEXT_UNTITLED_SESSION =
		"rms.text.untitled_session";
	
	public static final String ERROR_LOG_ONE_IS_MISSING = 
		"rms.error.log_one_is_missing";
	public static final String ERROR_LOG_TWO_IS_MISSING = 
		"rms.error.log_two_is_missing";
	public static final String ERROR_LOGS_FOR_COMPARISON_ARE_THE_SAME = 
		"rms.error.logs_for_comparison_are_the_same";	
	public static final String ERROR_TIMESTAMP_MUST_BE_SMALLER_OR_EQUAL = 
		"rms.error.timestamp_must_be_smaller_or_equal";
	public static final String ERROR_TIMESTAMP_MUST_BE_GREATER_OR_EQUAL = 
		"rms.error.timestamp_must_be_greater_or_equal";	
}
