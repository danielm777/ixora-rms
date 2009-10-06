/*
 * Created on 17-Jan-2005
 */
package com.ixora.rms.agents.providers.parsers.table.messages;

/**
 * @author Daniel Moraru
 */
public interface Msg {
	public static final String COLUMN_INDEX =
		"providers.parsers.table.column_index";
	public static final String COLUMN_ENTITY_ID =
		"providers.parsers.table.column_entity_id";
	public static final String COLUMN_COUNTER_ID =
		"providers.parsers.table.column_counter_id";
	public static final String TEXT_PARSERS_TABLE_COLUMN_SEPARATOR =
		"providers.parsers.table.column_separator";
	public static final String TEXT_PARSERS_TABLE_IGNORE_LINES_MATCHING =
		"providers.parsers.table.ignore_lines_matching";
	public static final String TEXT_PARSERS_TABLE_COLUMNS_TO_IGNORE =
		"providers.parsers.table.columns_to_ignore";
	public static final String TEXT_PARSERS_TABLE_CONVERT_SINGLE_COLUMN_TO_ROW =
		"providers.parsers.table.convert_single_column_to_row";
	public static final String TOOLTIP_PARSERS_TABLE_COLUMNS_TO_IGNORE =
		"providers.parsers.table.tooltip.columns_to_ignore";
	public static final String TOOLTIP_PARSERS_TABLE_COLUMN_SEPARATOR =
		"providers.parsers.table.tooltip.column_separator";
	public static final String TOOLTIP_PARSERS_TABLE_IGNORE_LINES_MATCHING =
		"providers.parsers.table.tooltip.ignore_line_matching";
	public static final String TOOLTIP_PARSERS_TABLE_CONVERT_SINGLE_COLUMN_TO_ROW =
		"providers.parsers.table.tooltip.convert_single_column_to_row";
}
