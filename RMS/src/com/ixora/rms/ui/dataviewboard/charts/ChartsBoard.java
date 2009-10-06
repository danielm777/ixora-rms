/*
 * Created on 07-Jan-2004
 */
package com.ixora.rms.ui.dataviewboard.charts;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.HashSet;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import javax.swing.JPanel;

import com.ixora.rms.ResourceId;
import com.ixora.common.ConfigurationMgr;
import com.ixora.common.logging.AppLogger;
import com.ixora.common.logging.AppLoggerFactory;
import com.ixora.rms.CounterType;
import com.ixora.rms.client.QueryRealizer;
import com.ixora.rms.client.locator.SessionArtefactInfoLocator;
import com.ixora.rms.client.locator.SessionResourceInfo;
import com.ixora.rms.dataengine.Style;
import com.ixora.rms.repository.DataView;
import com.ixora.rms.services.DataEngineService;
import com.ixora.rms.services.ReactionLogService;
import com.ixora.rms.ui.dataviewboard.DataViewBoard;
import com.ixora.rms.ui.dataviewboard.DataViewControl;
import com.ixora.rms.ui.dataviewboard.DataViewControlContext;
import com.ixora.rms.ui.dataviewboard.exception.FailedToCreateControl;

/**
 * ChartsBoard.
 * @author Daniel Moraru
 */
public final class ChartsBoard extends DataViewBoard
				implements Observer {
	/** Logger */
	private AppLogger logger = AppLoggerFactory.getLogger(ChartsBoard.class);
	/** Columns */
	private int cols;
	/** Rows */
	private int rows;

	/**
	 * Constructor.
	 * @param qr
	 * @param des
	 * @param rls
	 * @param locator
	 */
	public ChartsBoard(QueryRealizer qr, DataEngineService des, ReactionLogService rls, SessionArtefactInfoLocator locator) {
		super(qr, des, rls, locator, "",
		true, //resizable
		true, //closable
		true, //maximizable
		true);// iconifiable
		setSize(new Dimension(500, 550));
		cols = ConfigurationMgr.getInt(
				ChartsBoardComponent.NAME,
				ChartsBoardConfigurationConstants.CHARTSBOARD_COLS);
		rows = ConfigurationMgr.getInt(
				ChartsBoardComponent.NAME,
				ChartsBoardConfigurationConstants.CHARTSBOARD_ROWS);
		fBoard = new JPanel(new GridLayout(rows, cols, 0, 0));
		fBoard.setOpaque(true);
		setContentPane(fBoard);
		ConfigurationMgr.get(ChartsBoardComponent.NAME).addObserver(this);
	}

	/**
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	public void update(Observable o, Object arg) {
		if(ChartsBoardConfigurationConstants.CHARTSBOARD_COLS.equals(arg)) {
			cols = ConfigurationMgr.getInt(
					ChartsBoardComponent.NAME,
					ChartsBoardConfigurationConstants.CHARTSBOARD_COLS);
			((GridLayout)fBoard.getLayout()).setColumns(cols);
			// validate() & repaint() not working...
			fBoard.doLayout();
			return;
		}
		if(ChartsBoardConfigurationConstants.CHARTSBOARD_ROWS.equals(arg)) {
			rows = ConfigurationMgr.getInt(
					ChartsBoardComponent.NAME,
					ChartsBoardConfigurationConstants.CHARTSBOARD_ROWS);
			((GridLayout)fBoard.getLayout()).setRows(rows);
			fBoard.doLayout();
			return;
		}
	}

    /**
     * @see com.ixora.rms.ui.dataviewboard.DataViewBoard#reachedMaximumControls()
     */
    public boolean reachedMaximumControls() {
        return fControls.size() >= cols * rows;
    }

    /**
     * Overriden to do cleanup.
     * @see javax.swing.JInternalFrame#dispose()
     */
    public void dispose() {
        // cleanup
        ConfigurationMgr.get(ChartsBoardComponent.NAME).deleteObserver(this);
        super.dispose();
    }

    /**
     * @see com.ixora.rms.ui.dataviewboard.DataViewBoard#createDataViewForCounter(com.ixora.rms.ResourceId, java.lang.String, java.lang.String, com.ixora.rms.dataengine.Style)
     */
	protected DataView createDataViewForCounter(
				ResourceId counter,	String name, String desc, Style style) {
		if(style == null && name == null) {
			return new ChartCounterDataView(counter);
		} else {
			return new ChartCounterUserDataView(counter, name, desc, style);
		}
	}

    /**
     * @see com.ixora.rms.ui.dataviewboard.DataViewBoard#createDataViewForCounters(ResourceId, java.util.List, java.lang.String, java.util.List)
     */
    protected DataView createDataViewForCounters(ResourceId context, List<ResourceId> counters, String viewName, List<ResourceId> rejected) {
        // reject the counters which are not numerical
        Set<ResourceId> numerics = new HashSet<ResourceId>();
        Set<ResourceId> strings = new HashSet<ResourceId>();
        for(ResourceId counter : counters) {
            SessionResourceInfo ri = fLocator.getResourceInfo(counter);
            if(ri == null || ri.getCounterInfo() == null) {
                logger.error("Counter " + counter
                        + " not found. Derived query will be incomplete");
                continue;
            }
            CounterType counterType = ri.getCounterInfo().getDescriptor().getType();
            if(counterType != CounterType.STRING) {
                numerics.add(counter);
            } else {
                strings.add(counter);
            }
        }
        rejected.addAll(strings);
        return new ChartCounterSetDataView(context, numerics, viewName);
    }

	/**
	 * @see com.ixora.rms.ui.dataviewboard.DataViewBoard#createControl(com.ixora.rms.ui.dataviewboard.DataViewControlContext, com.ixora.rms.internal.ResourceId, com.ixora.rms.repository.DataView)
	 */
	protected DataViewControl createControl(DataViewControlContext context, ResourceId resourceContext, DataView view) throws FailedToCreateControl {
        try {
            return new ChartControl(this, this.fEventHandler, context, fLocator, fReactionLog, resourceContext, view);
        } catch(Exception e) {
            throw new FailedToCreateControl(e);
        }
	}
}
