/*
 * Created on 14-Aug-2004
 */
package com.ixora.rms.ui.dataviewboard.tables.messages;

/**
 * @author Daniel Moraru
 */
public interface Msg {
    public static final String TABLES_ACTONS_VIEW_AS_BAR_CHART =
        "tablesboard.actions.view_as_bar_chart";
    public static final String TABLES_ACTONS_VIEW_AS_TIMESERIES_CHART =
        "tablesboard.actions.view_as_timeseries_chart";
    public static final String TABLES_ACTONS_VIEW_SELECTED_AS_BAR_CHART =
        "tablesboard.actions.view_selected_as_bar_chart";
    public static final String TABLES_ACTONS_VIEW_SELECTED_AS_TIMESERIES_CHART =
        "tablesboard.actions.view_selected_as_timeseries_chart";
	public static final String TABLES_ACTONS_CLEAR_SELECTION =
		"tablesboard.actions.clear_selection";
    public static final String TABLES_ACTIONS_REMOVE_STALE_ENTRIES =
        "tablesboard.actions.remove_stale_entries";
	public static final String TABLES_QUICK_CHART_STYLES =
		"tablesboard.text.quick_chart_styles";
	public static final String TABLES_QUICK_CHART_ITEMS =
		"tablesboard.text.quick_chart_items";
	public static final String TABLES_TITLE_QUICK_CHART_PROPERTIES =
		"tablesboard.title.quick_chart_properties";
	public static final String TABLES_QUICK_CHART_PROPERTIES_COL2 =
		"tablesboard.text.quick_chart_properties_col2";
	public static final String TABLES_QUICK_CHART_PROPERTIES_COL3 =
		"tablesboard.text.quick_chart_properties_col3";

// temp
	public static final String TABLES_ACTONS_TOGGLE_FILTER =
		"tablesboard.actions.toggle_filter";
	public static final String TABLES_ACTONS_PRINT =
		"tablesboard.actions.print_table";
	public static final String TABLES_ACTONS_REMOVE_FILTER =
		"tablesboard.actions.remove_filter";
	public static final String TABLES_TITLE_FILTER_EDITOR =
		"tablesboard.title.filter_editor";
	public static final String TABLES_FILTER_REGEX =
		"tablesboard.text.filter_regex";
	public static final String TABLES_FILTER_COLUMNS =
		"tablesboard.text.filter_columns";
	public static final String TABLES_FILTER_INVALID_FILTER =
		"tablesboard.text.invalid_filter";
	public static final String TABLES_FILTER_WILL_NOT_BE_SET =
		"tablesboard.text.filter_will_not_be_set";
}
