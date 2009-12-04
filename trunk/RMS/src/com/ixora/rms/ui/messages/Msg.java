package com.ixora.rms.ui.messages;


/**
 * Messages keys for RMS gui.
 * @author Daniel Moraru
 */
public interface Msg {
	public static final String MENU_LOADSESSION_BROWSE =
		"ui.menu.loadsession.browse";
	public static final String MENU_FILE =
		"ui.menu.file";
	public static final String MENU_FILE_EXIT =
		"ui.menu.file.exit";
	public static final String MENU_HELP_HELP =
		"ui.menu.help.help";
	public static final String MENU_HELP_UPDATES =
		"ui.menu.help.updates";
	public static final String MENU_HELP_ABOUT =
		"ui.menu.help.about";
	public static final String MENU_ACTIONS =
		"ui.menu.actions";
	public static final String MENU_TOOLS =
		"ui.menu.tools";
	public static final String MENU_CONFIGURATION =
		"ui.menu.configuration";
	public static final String MENU_CONFIGURATION_TOGGLETOOLBAR =
		"ui.menu.configuration.toggle_tool_bar";
	public static final String MENU_CONFIGURATION_TOGGLESTATUSBAR =
		"ui.menu.configuration.toggle_status_bar";
	public static final String ACTIONS_FROM_XML =
		"ui.actions.from_xml";
	public static final String ACTIONS_VIEW_XML =
		"ui.actions.view_xml";
	public static final String MENU_CONFIGURATION_SETTINGS =
		"ui.menu.configuration.settings";
	public static final String MENU_HELP =
		"ui.menu.help";
	public static final String MENU_VIEW =
		"ui.menu.view";
	public static final String MENU_WINDOWS =
		"ui.menu.windows";
    public static final String ACTIONS_NEXT_SCREEN =
        "ui.actions.next_screen";
    public static final String ACTIONS_PREV_SCREEN =
        "ui.actions.prev_screen";
	public static final String ACTIONS_LOADSESSION =
		"ui.actions.load_session";
	public static final String ACTIONS_CLOSE_REACTION_PANEL =
		"ui.actions.close_reaction_panel";
	public static final String ACTIONS_NEWSESSION =
		"ui.actions.new_session";
	public static final String ACTIONS_SAVESESSION =
		"ui.actions.save_session";
	public static final String ACTIONS_SAVESESSION_AS =
		"ui.actions.save_session_as";
	public static final String ACTIONS_LOADLOG =
		"ui.actions.load_log";
	public static final String ACTIONS_COMPARE_LOGS = 
		"ui.actions.compare_logs";
    public static final String ACTIONS_LAUNCH_JOBMANAGER =
        "ui.actions.launch_job_manager";
	public static final String ACTIONS_DISABLE_REACTIONS_FOR_CONTROL =
		"ui.actions.disable_reactions_for_control";
	public static final String ACTIONS_TOGGLE_HTML_GENERATION =
		"ui.actions.toggle_html_generation";
	public static final String ACTIONS_LAUNCH_PROVIDERMANAGER =
    	"ui.actions.launch_provider_manager";
    public static final String ACTIONS_LAUNCH_AGENTINSTALLER =
    	"ui.actions.launch_agent_installer";
	public static final String ACTIONS_STARTSESSION =
		"ui.actions.start_session";
	public static final String ACTIONS_STARTAGENTS =
		"ui.actions.start_agents";
	public static final String ACTIONS_STOPAGENTS =
		"ui.actions.stop_agents";
	public static final String ACTIONS_VIEWAGENTERROR =
		"ui.actions.view_agent_error";
	public static final String ACTIONS_REFRESHNODE =
		"ui.actions.refresh_node";
	public static final String ACTIONS_REFRESH_NODE_WITH_SCHEDULE =
		"ui.actions.refresh_node_with_schedule";
	public static final String ACTIONS_TURNSONOFFLOGGING =
		"ui.actions.turn_on_off_logging";
	public static final String ACTIONS_AGENT_CONFIGURATION_SELECT_PROVIDERS =
		"ui.actions.agent_configuration_select_providers";
	public static final String ACTIONS_SHOW_SELECTED_ITEMS_ONLY =
	    "ui.actions.show_selected_items_only";
	public static final String ACTIONS_NEW_VIEWBOARD =
		"ui.actions.new_view_board";
	public static final String ACTIONS_SET_VIEWBOARD_NAME =
	    "ui.actions.set_view_board_name";
	public static final String ACTIONS_SHOWLEGEND =
		"ui.actions.show_legend";
	public static final String ACTIONS_CHANGE_SAMPLING_INTERVAL =
		"ui.actions.change_sampling_interval";
	public static final String ACTIONS_REWIND_LOG =
		"ui.actions.rewind_log";
	public static final String ACTIONS_PAUSE_LOG =
		"ui.actions.pause_log";
	public static final String ACTIONS_STOPSESSION =
		"ui.actions.stop_session";
	public static final String ACTIONS_ADD_HOSTS =
		"ui.actions.add_hosts";
	public static final String ACTIONS_REMOVE_HOSTS =
		"ui.actions.remove_hosts";
	public static final String ACTIONS_ADD_AGENTS =
		"ui.actions.add_agents";
    public static final String ACTIONS_REMOVE_AGENTS =
		"ui.actions.remove_agents";
	public static final String ACTIONS_ACTIVATEAGENT =
		"ui.actions.activate_agent";
	public static final String ACTIONS_TILE_VIEWBOARDS =
		"ui.actions.tile_view_boards";
	public static final String ACTIONS_TOGGLE_IDENTIFIERS =
		"ui.actions.toggle_identifiers";
	public static final String ACTIONS_PLOT =
		"ui.actions.plot";
	public static final String ACTIONS_EDIT_QUERY =
		"ui.actions.edit_query";
	public static final String ACTIONS_ADD_QUERY =
		"ui.actions.add_query";
	public static final String ACTIONS_REMOVE_QUERY =
		"ui.actions.remove_query";
	public static final String ACTIONS_PLAY_LOG =
		"ui.actions.play_log";
    public static final String ACTIONS_REMOVE_DATAVIEW_CONTROL =
        "ui.actions.remove_data_view_control";
	public static final String ACTIONS_MOVE_DATAVIEW_CONTROL = 
		"ui.actions.move_data_view_control";  
	public static final String ACTIONS_MOVE_DATAVIEW_BOARD = 
		"ui.actions.move_data_view_board";
	public static final String ACTIONS_ADD_DATA_VIEW_SCREEN =
		"ui.actions.add_data_view_screen";
	public static final String ACTIONS_REMOVE_DATA_VIEW_SCREEN =
		"ui.actions.remove_data_view_screen";
	public static final String ACTIONS_RENAME_DATA_VIEW_SCREEN =
		"ui.actions.rename_data_view_screen";
	public static final String MODELS_TABLES_INSTALLEDAGENTS_COL1 =
		"ui.models.tables.installedagents.col1";
	public static final String ACTIONS_PLOT_COUNTER_WITH_CODE =
		"ui.actions.plot_counter_with_code";
	public static final String ACTIONS_PLOT_COUNTER_WITH_STYLE =
		"ui.actions.plot_counter_with_style";
	public static final String MODELS_TABLES_INSTALLEDAGENTS_COL2 =
		"ui.models.tables.installedagents.col2";
    public static final String TEXT_SELECT_LOCATION =
        "ui.text.select_location";
    public static final String TEXT_SELECT_SYSTEM_VERSION =
        "ui.text.select_system_version";
	public static final String MODELS_TABLES_HOSTINFO_COL1 =
		"ui.models.tables.hostinfo.col1";
	public static final String MODELS_TABLES_HOSTINFO_COL2 =
		"ui.models.tables.hostinfo.col2";
	public static final String MODELS_TABLES_HOSTINFO_COL3 =
		"ui.models.tables.hostinfo.col3";
	public static final String MODELS_TABLES_HOSTINFO_COL4 =
		"ui.models.tables.hostinfo.col4";
	public static final String TEXT_SPECIFYHOSTSTOMONITOR =
		"ui.text.specify_hosts_to_monitor";
	public static final String TEXT_REMOVEHOSTSWITHACTIVEAGENTS =
		"ui.text.remove_hosts_with_active_agents";
	public static final String TEXT_BEGININGNEWSESSION =
		"ui.text.begining_new_session";
	public static final String TEXT_LOADING_SESSION =
		"ui.text.loading_session";
	public static final String TEXT_ACTIVATINGAGENT =
		"ui.text.activating_agent";
	public static final String TEXT_GETTINGAGENTENTITIES =
		"ui.text.getting_agent_entities";
	public static final String TEXT_APPLYINGCHANGES_ENTITYCONFIGURATION =
		"ui.text.applying_changes_entity_configuration";
    public static final String TEXT_CHANGE_REPLAY_SPEED =
        "ui.text.change_replay_speed";
	public static final String TEXT_APPLYINGCHANGES_AGENTCONFIGURATION =
		"ui.text.applying_changes_agent_configuration";
	public static final String TEXT_SAVING_SESSION =
		"ui.text.saving_session";
	public static final String TEXT_STARTINGSESSION =
		"ui.text.starting_session";
	public static final String TEXT_STOPPINGSESSION =
		"ui.text.stopping_session";
	public static final String TEXT_RESOURCE_TREE_CONTEXT =
		"ui.text.resource_tree_context";
	public static final String TEXT_RESOURCES =
		"ui.text.resources";
    public static final String TEXT_INPUTTITLE_ADDINGHOSTSTOMONITOR =
		"ui.text.inputtitle.adding_hosts_to_monitor";
	public static final String TEXT_INPUTTITLE_SAVING_SESSION =
		"ui.text.inputtitle.saving_session";
	public static final String TEXT_UNTITLED_SESSION =
		"ui.text.untitled_session";
	public static final String TEXT_MONITORINGLEVEL =
		"ui.text.monitoring_level";
    public static final String TEXT_PLEASE_SELECT_VIEWBOARD =
        "ui.text.please_select_view_board";
	public static final String TEXT_SAMPLINGINTERVAL =
		"ui.text.sampling_interval";
    public static final String TEXT_RENAMING_VIEWBOARD =
        "ui.text.renaming_view_board";
    public static final String TEXT_NEW_VIEWBOARD_NAME =
        "ui.text.new_view_board_name";
	public static final String TEXT_ENTITYCONFIGURATION =
		"ui.text.entity_configuration";
	public static final String TEXT_AGENTCONFIGURATION =
		"ui.text.agent_configuration";
	public static final String TEXT_CONFIGURATION =
		"ui.text.configuration";
	public static final String TEXT_DATAVIEWS =
		"ui.text.dataviews";
	public static final String TEXT_PROVIDERS =
		"ui.text.providers";
	public static final String TEXT_DETAILS=
		"ui.text.details";
	public static final String TEXT_DASHBOARDS=
		"ui.text.dashboards";
	public static final String TEXT_ERROR =
		"ui.text.error";
	public static final String TEXT_LOGGINGON =
		"ui.text.logging_on";
	public static final String TEXT_PLOTGROUPEDBY =
		"ui.text.plot_grouped_by";
	public static final String TEXT_USEDEFAULT =
		"ui.text.use_default";
	public static final String TEXT_LEGEND =
		"ui.text.legend";
	public static final String TEXT_UPDATES =
		"ui.text.updates";
	public static final String ACTIONS_VIEW_REACTIONS_LOG =
		"ui.actions.view_reactions_log";
    public static final String TITLE_CONFIGURATION =
		"ui.title.configuration";
    public static final String TITLE_SELECT_OPTIONAL_PROVIDERS =
    	"ui.title.select_optional_providers";
	public static final String TEXT_RECURSIVE =
		"ui.text.recursive";
	public static final String TEXT_ADDING_HOST =
		"ui.text.adding_hosts";
	public static final String TEXT_SESSION_FILTER_DESCRIPTION =
		"ui.text.monitoring_session_filter_description";
    public static final String TEXT_LOG_FILTER_DESCRIPTION =
        "ui.text.log_filter_description";
	public static final String TEXT_REALIZING_MONITORING_SESSION =
		"ui.text.realizing_monitoring_session";
	public static final String TEXT_REALIZINGQUERY =
		"ui.text.realizing_query";
	public static final String TEXT_FAILED_TO_SAVE_QUERY =
		"ui.text.failed_to_save_query";
	public static final String TEXT_OVERWRITE_QUERY =
		"ui.text.overwrite_query";
    public static final String TITLE_SELECT_AGENT_VERSIONS =
        "ui.title.select_agent_versions";
	public static final String TITLE_DIALOGS_AGENTACTIVATOR =
		"ui.title.dialogs.agent_activator";
	public static final String TITLE_FRAME_RMS =
		"ui.title.frames.rms";
	public static final String TITLE_VIEWBOARD =
		"ui.title.viewboard";
	public static final String TITLE_QUERY_EDITOR =
		"ui.title.query_editor";
	public static final String TEXT_LOADING_LOG_VIEW =
        "ui.text.loading_log_view";
    public static final String TEXT_STARTINGREPLAY =
        "ui.text.starting_replay";
    public static final String TEXT_PAUSINGREPLAY =
        "ui.text.pausing_replay";
    public static final String TEXT_REWINDINGLOG =
        "ui.text.rewinding_log";
    public static final String TEXT_LOADED_LOG =
        "ui.text.loaded_log";
	public static final String TEXT_REFRESHING_VIEW =
		"ui.text.refreshing_view";
	public static final String ACTIONS_DISABLE_ALL_COUNTERS_RECURSIVELY =
		"ui.actions.disable_all_counters_recursively";
	public static final String ACTIONS_DISABLE_ALL_COUNTERS =
		"ui.actions.disable_all_counters";
	public static final String ACTIONS_ENABLE_ALL_COUNTERS_RECURSIVELY =
		"ui.actions.enable_all_counters_recursively";
	public static final String ACTIONS_ENABLE_ALL_COUNTERS =
		"ui.actions.enable_all_counters";
	public static final String ACTIONS_DISABLE_REACTIONS =
		"ui.actions.disable_reactions";
	public static final String TEXT_SCANNING_LOG =
		"ui.text.scanning_log";
	public static final String TEXT_ENABLING_COUNTERS =
		"ui.text.enabling_counters";
	public static final String TEXT_REMOVING_AGENT =
		"ui.text.removing_agent";
	public static final String TEXT_DISABLING_COUNTERS =
		"ui.text.disabling_counters";
	public static final String TEXT_CLOSING_LOG =
		"ui.text.closing_log";
	public static final String TEXT_READING_CONFIGURATION =
		"ui.text.reading_configuration";
	public static final String TITLE_NEW_SCREEN =
		"ui.title.new_screen";
	public static final String TEXT_NEW_SCREEN =
		"ui.text.new_screen";
	public static final String TEXT_RENAME_SCREEN =
		"ui.text.rename_screen";
	public static final String TITLE_RENAME_SCREEN =
		"ui.title.rename_screen";
	public static final String TITLE_CONFIRM_REMOVE_SCREEN =
		"ui.title.confirm_remove_screen";
	public static final String TITLE_RESOURCE_SELECTOR =
		"ui.title.resource_selector";
	public static final String TITLE_COMPARE_AND_REPLAY_CONFIGURATION = 
		"ui.title.compare_and_replay_configuration";	
	public static final String TEXT_CONFIRM_REMOVE_SCREEN =
		"ui.text.confirm_remove_screen";
	public static final String ERROR_SCREEN_NAME_ALREADY_EXISTS =
		"ui.error.screen_name_already_exists";
	public static final String ERROR_LICENSE_LIMIT_REACHED_HOSTS =
		"ui.error.license_limit_reached_hosts";
    public static final String ERROR_LICENSE_LIMIT_REACHED_AGENTS =
        "ui.error.license_limit_reached_agents";
	public static final String ERROR_LICENSE_NOT_AVAILABLE_FOR_AGENT =
		"ui.error.license_not_available_for_agent";
	public static final String ERROR_LOGGING_STOPPED_DUE_TO_FATAL_ERROR =
		"ui.error.logging_stopped_due_to_fatal_error";
	public static final String ERROR_BOARD_NAME_ALREADY_EXISTS = 
		"ui.error.board_name_already_exists";
	public static final String TEXT_CLOSING_VIEW =
		"ui.text.closing_view";
	public static final String TEXT_SAVE_MONITORING_SESSION =
		"ui.text.save_monitoring_session";
    public static final String TEXT_INPUTTITLE_VIEW_NAME =
        "ui.text.inputtitle.view_name";
    public static final String TEXT_VIEW_NAME =
        "ui.text.view_name";
    public static final String TEXT_QUERY_IS_ALREADY_REGISTERED_RETRY =
        "ui.text.query_is_already_registered_retry";

	public static final String DATAVIEWBOARD_ACTONS_TOGGLE_FILTER =
		"dataviewboard.actions.toggle_filter";
	public static final String DATAVIEWBOARD_ACTONS_PRINT =
		"dataviewboard.actions.print_table";
	public static final String DATAVIEWBOARD_ACTONS_REMOVE_FILTER =
		"dataviewboard.actions.remove_filter";
	public static final String DATAVIEWBOARD_TITLE_FILTER_EDITOR =
		"dataviewboard.title.filter_editor";
	public static final String DATAVIEWBOARD_FILTER_REGEX =
		"dataviewboard.text.filter_regex";
	public static final String DATAVIEWBOARD_FILTER_COLUMNS =
		"dataviewboard.text.filter_columns";
	public static final String DATAVIEWBOARD_FILTER_INVALID_FILTER =
		"dataviewboard.text.invalid_filter";
	public static final String DATAVIEWBOARD_FILTER_WILL_NOT_BE_SET =
		"dataviewboard.text.filter_will_not_be_set";
	public static final String LABEL_LOG_ONE = 
		"ui.text.log_one";
	public static final String LABEL_LOG_TWO = 
		"ui.text.log_two";
	public static final String LINK_SETUP_TIME_INTERVAL = 
		"ui.text.setup_time_interval";
	public static final String LABEL_AGGREGATION_STEP = 
		"ui.text.aggregation_step";
	public static final String TEXT_NO_AGGREGATION = 
		"ui.text.no_aggregation";
	public static final String TITLE_TIME_INTERVAL_SELECTOR = 
		"ui.title.time_interval_selector";
	public static final String LABEL_TIME_START = 
		"ui.text.time_start";
	public static final String LABEL_TIME_END = 
		"ui.text.time_end";
	public static final String TITLE_REPLAY_CONFIGURATION = 
		"ui.title.replay_configuration";
	public static final String LABEL_LOG = 
		"ui.label.log";
	public static final String ERROR_LOG_ONE_IS_MISSING = 
		"ui.error.log_one_is_missing";
	public static final String ERROR_LOG_TWO_IS_MISSING = 
		"ui.error.log_two_is_missing";
	public static final String ERROR_LOGS_FOR_COMPARISON_ARE_THE_SAME = 
		"ui.error.logs_for_comparison_are_the_same";	
}
