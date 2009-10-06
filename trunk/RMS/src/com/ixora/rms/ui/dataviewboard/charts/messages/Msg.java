/*
 * Created on 14-Aug-2004
 */
package com.ixora.rms.ui.dataviewboard.charts.messages;

/**
 * @author Daniel Moraru
 */
public interface Msg {
    public static final String RMS_CHARTSBOARD_NO_CONTROL_CAN_SATISFY_QUERY =
        "chartsboard.no_control_can_statisfy_query";
    public static final String RMS_CHARTSBOARD_NO_DATA_AVAILABLE =
        "chartsboard.no_data_available";
    public static final String RMS_CHARTSBOARD_EMPTY_QUERY =
        "chartsboard.empty_query";
    public static final String RMS_MULTIPLE_RENDERERS_FOR_CATEGORY_AND_XY_ONLY =
    	"chartsboard.multiple_renderers_for_category_and_xy_only";
    public static final String RMS_MULTIPLE_RENDERERS_CANNOT_MIX =
    	"chartsboard.multiple_renderers_cannot_mix";
    public static final String RMS_INVALID_DATASET =
    	"chartsboard.invalid_dataset";
    public static final String RMS_INVALID_RENDERER =
    	"chartsboard.invalid_renderer";
    public static final String RMS_UNKNOWN_RENDERER =
    	"chartsboard.unknown_renderer";
    public static final String RMS_NO_RENDERER =
    	"chartsboard.no_renderer";
    public static final String CHART_STYLE_STACKED_BAR_2D =
    	"chartsboard.chart_style.stacked_bar_2d";
    public static final String CHART_STYLE_BAR_2D =
    	"chartsboard.chart_style.bar_2d";
    public static final String CHART_STYLE_XY_LINE =
    	"chartsboard.chart_style.xy_line";
    public static final String CHART_STYLE_BAR_3D =
    	"chartsboard.chart_style.bar_3d";
    public static final String CHART_STYLE_STACKED_BAR_3D =
    	"chartsboard.chart_style.stacked_bar_3d";
    public static final String CHART_STYLE_XY_AREA =
    	"chartsboard.chart_style.xy_area";
    public static final String CHART_STYLE_STACKED_XY_AREA =
    	"chartsboard.chart_style.stacked_xy_area";
    public static final String CHART_STYLE_CATEGORY_LINE =
    	"chartsboard.chart_style.category_line";
    public static final String CHART_STYLE_CATEGORY_AREA =
    	"chartsboard.chart_style.category_area";
    public static final String CHART_STYLE_CATEGORY_STACKED_AREA =
    	"chartsboard.chart_style.category_stacked_area";
}
