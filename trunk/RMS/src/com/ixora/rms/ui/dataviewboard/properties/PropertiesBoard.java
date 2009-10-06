/*
 * Created on 05-Mar-2005
 */
package com.ixora.rms.ui.dataviewboard.properties;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JPanel;

import com.ixora.rms.ResourceId;
import com.ixora.common.ConfigurationMgr;
import com.ixora.rms.client.QueryRealizer;
import com.ixora.rms.client.locator.SessionArtefactInfoLocator;
import com.ixora.rms.dataengine.Style;
import com.ixora.rms.repository.DataView;
import com.ixora.rms.services.DataEngineService;
import com.ixora.rms.services.ReactionLogService;
import com.ixora.rms.ui.dataviewboard.DataViewBoard;
import com.ixora.rms.ui.dataviewboard.DataViewControl;
import com.ixora.rms.ui.dataviewboard.DataViewControlContext;
import com.ixora.rms.ui.dataviewboard.exception.FailedToCreateControl;

/**
 * @author Daniel Moraru
 */
public class PropertiesBoard extends DataViewBoard implements Observer {
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
	public PropertiesBoard(QueryRealizer qr, DataEngineService des,
			ReactionLogService rls,
			SessionArtefactInfoLocator locator) {
		super(qr, des, rls, locator, "",
				true, //resizable
				true, //closable
				true, //maximizable
				true);// iconifiable
		setSize(new Dimension(500, 550));
		cols = ConfigurationMgr.getInt(
				PropertiesBoardComponent.NAME,
				PropertiesBoardConfigurationConstants.PROPERTIESBOARD_COLS);
		rows = ConfigurationMgr.getInt(
				PropertiesBoardComponent.NAME,
		        PropertiesBoardConfigurationConstants.PROPERTIESBOARD_ROWS);
		ConfigurationMgr.get(PropertiesBoardComponent.NAME).addObserver(this);
		fBoard = new JPanel(new GridLayout(rows, cols, 0, 0));
		fBoard.setOpaque(true);
		setContentPane(fBoard);
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
        ConfigurationMgr.get(PropertiesBoardComponent.NAME).deleteObserver(this);
        super.dispose();
    }

	/**
	 * @see com.ixora.rms.ui.dataviewboard.DataViewBoard#createControl(com.ixora.rms.ui.dataviewboard.DataViewControlContext, com.ixora.rms.internal.ResourceId, com.ixora.rms.repository.DataView)
	 */
	protected DataViewControl createControl(DataViewControlContext context,
			ResourceId resourceContext, DataView view)
			throws FailedToCreateControl {
		return new PropertiesControl(this, this.fEventHandler, context, fLocator, fReactionLog, resourceContext, view);
	}

	/**
	 * @see com.ixora.rms.ui.dataviewboard.DataViewBoard#createDataViewForCounter(com.ixora.rms.ResourceId, com.ixora.rms.dataengine.Style)
	 */
	protected DataView createDataViewForCounter(
			ResourceId counter, String name, String desc, Style style) {
		if(style == null && name == null) {
			return new PropertiesCounterDataView(counter);
		} else {
			return new PropertiesCounterUserDataView(counter, name, desc, style);
		}
	}

    /**
     * @see com.ixora.rms.ui.dataviewboard.DataViewBoard#createDataViewForCounters(java.util.List, java.lang.String, java.util.List)
     */
    protected DataView createDataViewForCounters(ResourceId context, List<ResourceId> counters, String viewName, List<ResourceId> rejected) {
        return new PropertiesCounterSetDataView(context, counters, viewName);
    }

	/**
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	public void update(Observable o, Object arg) {
		if(PropertiesBoardConfigurationConstants.PROPERTIESBOARD_COLS.equals(arg)) {
			cols = ConfigurationMgr.getInt(
			        PropertiesBoardComponent.NAME,
					PropertiesBoardConfigurationConstants.PROPERTIESBOARD_COLS);
			((GridLayout)fBoard.getLayout()).setColumns(cols);
			// validate() & repaint() not working...
			fBoard.doLayout();
			return;
		}
		if(PropertiesBoardConfigurationConstants.PROPERTIESBOARD_ROWS.equals(arg)) {
			rows = ConfigurationMgr.getInt(
			        PropertiesBoardComponent.NAME,
			        PropertiesBoardConfigurationConstants.PROPERTIESBOARD_ROWS);
			((GridLayout)fBoard.getLayout()).setRows(rows);
			fBoard.doLayout();
			return;
		}
	}
}
