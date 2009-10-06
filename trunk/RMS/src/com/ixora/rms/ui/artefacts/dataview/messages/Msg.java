/*
 * Created on 16-Oct-2004
 */
package com.ixora.rms.ui.artefacts.dataview.messages;

/**
 * @author Daniel Moraru
 */
public interface Msg {
    public static final String TITLE_DATAVIEW_EDITOR =
        "dataview.title.data_view_editor";
	public static final String ERROR_DATAVIEW_SAVING_CONFLICT =
		"dataview.error.dataview_saving_conflict";
    public static final String ERROR_FAILED_TO_SAVE_DATAVIEW =
        "dataview.error.failed_to_save_data_view";
    public static final String ERROR_DATAVIEW_NOT_READY =
        "dataview.error.data_view_not_ready";
    public static final String ACTIONS_ADD_DATAVIEW =
        "dataview.actions.add_data_view";
    public static final String ACTIONS_ADD_DATAVIEW_USING_XML_EDITOR =
    	"dataview.actions.add_data_view_using_xml_editor";
	public static final String ACTIONS_ADD_DATAVIEW_USING_WIZARD =
		"dataview.actions.add_data_view_using_wizard";
    public static final String ACTIONS_REMOVE_DATAVIEW =
        "dataview.actions.remove_data_view";
    public static final String ACTIONS_EDIT_DATAVIEW =
        "dataview.actions.edit_data_view";
    public static final String ACTIONS_PLOT_DATAVIEW =
        "dataview.actions.plot_data_view";
	public static final String ACTIONS_INSERT_RESOURCE_ID =
		"dataview.actions.insert_resource_id";
    public static final String TEXT_UNREALIZING_DATAVIEW =
        "dataview.text.unrealizing_data_view";
    public static final String TEXT_REALIZING_DATAVIEW =
        "dataview.text.realizing_data_view";
    public static final String TITLE_CONFIRM_REMOVE_DATAVIEW =
        "dataview.title.confirm_remove_dataview";
    public static final String TEXT_CONFIRM_REMOVE_DATAVIEW =
        "dataview.text.confirm_remove_dataview";
    public static final String TEXT_OVERWRITE_DATAVIEW =
        "dataview.text.overwrite_data_view";
    public static final String TITLE_CONFIRM_OVERWRITE_DATAVIEW =
		"dataview.title.confirm_overwrite_dataview";
	public static final String TEXT_INVALID_DATAVIEW_NAME =
		"dataview.text.invalid_dataview_name";
	public static final String TITLE_SAVEAS_DATAVIEW =
		"dataview.title.saveas_dataview";
	public static final String TEXT_SAVEAS_DATAVIEW =
		"dataview.text.saveas_dataview";
    public static final String ACTIONS_ASSIGN_AGENT_VERSIONS =
        "dataview.actions.assign_agent_versions";
	public static final String ACTIONS_INSERT_REACTION =
		"dataview.actions.insert_reaction";
	public static final String TITLE_CONFIRM_MONITORING_LEVEL_INCREASE =
		"dataview.title.confirm_monitoring_level_increase";
	public static final String TEXT_CONFIRM_MONITORING_LEVEL_INCREASE =
		"dataview.text.confirm_monitoring_level_increase";
}
