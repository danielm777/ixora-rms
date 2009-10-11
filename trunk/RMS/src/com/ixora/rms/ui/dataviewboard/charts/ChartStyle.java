/*
 * Created on Nov 8, 2004
 */
package com.ixora.rms.ui.dataviewboard.charts;

import java.io.ObjectStreamException;

import com.ixora.common.Enum;
import com.ixora.common.MessageRepository;
import com.ixora.rms.ui.dataviewboard.charts.messages.Msg;

/**
 * @author Daniel Moraru
 */
public final class ChartStyle extends Enum {
	private static final long serialVersionUID = 7035252788868463233L;
	public static final ChartStyle STACKED_BAR_2D = new ChartStyle(0, "StackedBar2D", MessageRepository.get(ChartsBoardComponent.NAME, Msg.CHART_STYLE_STACKED_BAR_2D));
    public static final ChartStyle BAR_2D = new ChartStyle(1, "Bar2D", MessageRepository.get(ChartsBoardComponent.NAME, Msg.CHART_STYLE_BAR_2D));
    public static final ChartStyle XY_LINE = new ChartStyle(2, "XYLine", MessageRepository.get(ChartsBoardComponent.NAME, Msg.CHART_STYLE_XY_LINE));
    public static final ChartStyle BAR_3D = new ChartStyle(3, "Bar3D", MessageRepository.get(ChartsBoardComponent.NAME, Msg.CHART_STYLE_BAR_3D));
    public static final ChartStyle STACKED_BAR_3D = new ChartStyle(4, "StackedBar3D", MessageRepository.get(ChartsBoardComponent.NAME, Msg.CHART_STYLE_STACKED_BAR_3D));
    public static final ChartStyle XY_AREA = new ChartStyle(5, "XYArea", MessageRepository.get(ChartsBoardComponent.NAME, Msg.CHART_STYLE_XY_AREA));
    public static final ChartStyle STACKED_XY_AREA = new ChartStyle(6, "StackedXYArea", MessageRepository.get(ChartsBoardComponent.NAME, Msg.CHART_STYLE_STACKED_XY_AREA));
    public static final ChartStyle CATEGORY_LINE = new ChartStyle(7, "CategoryLine", MessageRepository.get(ChartsBoardComponent.NAME, Msg.CHART_STYLE_CATEGORY_LINE));
    public static final ChartStyle CATEGORY_AREA = new ChartStyle(8, "CategoryArea", MessageRepository.get(ChartsBoardComponent.NAME, Msg.CHART_STYLE_CATEGORY_AREA));
    public static final ChartStyle CATEGORY_STACKED_AREA = new ChartStyle(9, "CategoryStackedArea", MessageRepository.get(ChartsBoardComponent.NAME, Msg.CHART_STYLE_CATEGORY_STACKED_AREA));

    /** The actual name for the associated renderer */
    private String renderer;

    /**
	 * Constructor.
	 * @param key
	 * @param name
	 */
	private ChartStyle(int key, String renderer, String name) {
		super(key, name);
		this.renderer = renderer;
	}

	/**
	 * @see com.ixora.common.Enum#readResolve()
	 */
	protected Object readResolve() throws ObjectStreamException {
        switch(key) {
            case 0:
                return STACKED_BAR_2D;
            case 1:
                return BAR_2D;
            case 2:
                return XY_LINE;
            case 3:
                return BAR_3D;
            case 4:
                return STACKED_BAR_3D;
            case 5:
                return XY_AREA;
            case 6:
                return STACKED_XY_AREA;
            case 7:
                return CATEGORY_LINE;
            case 8:
                return CATEGORY_AREA;
            case 9:
                return CATEGORY_STACKED_AREA;
            }
            return null;
	}

	/**
	 * @return the name for the renderer identified by this ChartStyle
	 */
	public String getRenderer() {
		return this.renderer;
	}
}
