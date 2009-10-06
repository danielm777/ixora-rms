/*
 * Created on 17-Jan-2005
 */
package com.ixora.rms.agents.providers.parsers.property.messages;

/**
 * @author Daniel Moraru
 */
public interface Msg {
	public static final String COLUMN_ENTITY_ID =
		"providers.parsers.property.column_entity_id";
	public static final String COLUMN_COUNTER_ID =
		"providers.parsers.property.column_counter_id";
	public static final String TEXT_PARSERS_PROPERTY_ENTITY_INDENTATION =
		"providers.parsers.property.entity_indentation";
	public static final String TEXT_PARSERS_PROPERTY_PROPERTY_VALUE_REGEX =
        "providers.parsers.property.property_value_regex";
	public static final String TEXT_PARSERS_PROPERTY_PROPERTY_VALUE_REGEX_TOOLTIP =
        "providers.parsers.property.tooltip.property_value_regex";
    public static final String TEXT_PARSERS_PROPERTY_IGNORE_LINES_MATCHING =
        "providers.parsers.property.ignore_lines_matching";
    public static final String TEXT_PARSERS_PROPERTY_IGNORE_LINES_MATCHING_TOOLTIP =
        "providers.parsers.property.tooltip.ignore_lines_matching";
    public static final String TEXT_PARSERS_PROPERTY_BASE_ENTITY =
        "providers.parsers.property.base_entity";
    public static final String TEXT_PARSERS_PROPERTY_BASE_ENTITY_TOOLTIP =
        "providers.parsers.property.tooltip.base_entity";
    public static final String TEXT_PARSERS_PROPERTY_ENTITY_REGEX =
        "providers.parsers.property.entity_regex";
    public static final String TEXT_PARSERS_PROPERTY_ENTITY_REGEX_TOOLTIP =
        "providers.parsers.property.tooltip.entity_regex";
    public static final String TEXT_PARSERS_PROPERTY_VALUE_IS_SECOND_MATCH =
        "providers.parsers.property.value_is_second_match";
	public static final String TEXT_PARSERS_PROPERTY_VALUE_IS_SECOND_MATCH_TOOLTIP =
		"providers.parsers.property.tooltip.value_is_second_match";
}
