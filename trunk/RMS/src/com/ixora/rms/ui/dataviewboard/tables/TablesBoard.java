/*
 * Created on 17-Oct-2004
 */
package com.ixora.rms.ui.dataviewboard.tables;

import java.awt.Dimension;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JPanel;
import javax.swing.SpringLayout;

import com.ixora.rms.ResourceId;
import com.ixora.common.ConfigurationMgr;
import com.ixora.common.ui.SpringUtilities;
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
public final class TablesBoard extends DataViewBoard
		implements Observer {
	/** Columns */
	private int colsMax;
	/** Rows */
	private int rowsMax;
    /** Columns */
    private int cols;
    /** Rows */
    private int rows;
    /** List of placeholders */
	private List<JPanel> fPlaceholders;

	/**
	 * @param qr
	 * @param des
	 * @param rls
	 * @param locator
	 */
    public TablesBoard(
    		QueryRealizer qr, DataEngineService des, ReactionLogService rls, SessionArtefactInfoLocator locator) {
		super(qr, des, rls, locator, "",
				true, //resizable
				true, //closable
				true, //maximizable
				true);// iconifiable
		setSize(new Dimension(600, 550));
		colsMax = ConfigurationMgr.getInt(
				TablesBoardComponent.NAME,
				TablesBoardConfigurationConstants.TABLESBOARD_COLS);
		rowsMax = ConfigurationMgr.getInt(
		        TablesBoardComponent.NAME,
		        TablesBoardConfigurationConstants.TABLESBOARD_ROWS);
		ConfigurationMgr.get(TablesBoardComponent.NAME).addObserver(this);
		fBoard = new JPanel(new SpringLayout());
		fBoard.setOpaque(true);
		setContentPane(fBoard);
        fPlaceholders = new LinkedList<JPanel>();
    }

    /**
     * @see com.ixora.rms.ui.dataviewboard.DataViewBoard#addControl(com.ixora.rms.ui.dataviewboard.DataViewControl)
     */
    protected void addControl(DataViewControl dvc) {
        super.addControl(dvc);
        rearrange();
    }

    /**
     * @see com.ixora.rms.ui.dataviewboard.DataViewBoard#removeControl(com.ixora.rms.ui.dataviewboard.DataViewControl)
     */
    public void removeControl(DataViewControl dvc) {
        super.removeControl(dvc);
        rearrange();
    }

    /**
     * Rearranges the layout.
     */
    private void rearrange() {
        // remove placeholders
        for(JPanel placeholder : fPlaceholders) {
            fBoard.remove(placeholder);
        }
        fPlaceholders.clear();

        int size = fControls.size();
        if(size <= rowsMax) {
            rows = size;
            cols = 1;
        } else {
            rows = rowsMax;
            int mod = size % rows;
            cols = size / rows;
            if(mod != 0) {
                cols++;
            }
        }

        // add placeholders
        int phs = rows * cols - size;
        if(phs > 0) {
            for (int i = 0; i < phs; i++) {
                JPanel ph = new JPanel();
                fBoard.add(ph);
                fPlaceholders.add(ph);
            }
        }

        SpringUtilities.makeCompactGrid(
                fBoard,
                rows, cols, //rows, cols
                0, 0,        //initX, initY
                0, 0);       //xPad, yPad

        fBoard.revalidate();
        fBoard.repaint();
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    public void update(Observable o, Object arg) {
		if(TablesBoardConfigurationConstants.TABLESBOARD_COLS.equals(arg)) {
			colsMax = ConfigurationMgr.getInt(
			        TablesBoardComponent.NAME,
			        TablesBoardConfigurationConstants.TABLESBOARD_COLS);
			return;
		}
		if(TablesBoardConfigurationConstants.TABLESBOARD_ROWS.equals(arg)) {
			rowsMax = ConfigurationMgr.getInt(
			        TablesBoardComponent.NAME,
			        TablesBoardConfigurationConstants.TABLESBOARD_ROWS);
			return;
		}
    }

    /**
     * @see com.ixora.rms.ui.dataviewboard.DataViewBoard#reachedMaximumControls()
     */
    public boolean reachedMaximumControls() {
        return fControls.size() >= colsMax * rowsMax;
    }

    /**
     * Overriden to do cleanup.
     * @see javax.swing.JInternalFrame#dispose()
     */
    public void dispose() {
        // cleanup
        ConfigurationMgr.get(TablesBoardComponent.NAME).deleteObserver(this);
        super.dispose();
    }

	/**
	 * @see com.ixora.rms.ui.dataviewboard.DataViewBoard#createControl(com.ixora.rms.ui.dataviewboard.DataViewControlContext, com.ixora.rms.internal.ResourceId, com.ixora.rms.repository.DataView)
	 */
	protected DataViewControl createControl(DataViewControlContext context, ResourceId resourceContext, DataView view) throws FailedToCreateControl {
        return new TableControl(this, this.fEventHandler, context, fLocator, fReactionLog, resourceContext, view);
	}

	/**
	 * @see com.ixora.rms.ui.dataviewboard.DataViewBoard#createDataViewForCounter(com.ixora.rms.ResourceId, java.lang.String, java.lang.String, com.ixora.rms.dataengine.Style)
	 */
	protected DataView createDataViewForCounter(ResourceId counter, String name, String desc, Style style) {
		return null;
	}

    /**
     * @see com.ixora.rms.ui.dataviewboard.DataViewBoard#createDataViewForCounters(java.util.Set, java.lang.String, java.util.Set)
     */
    protected DataView createDataViewForCounters(ResourceId context, List<ResourceId> counter, String viewName, List<ResourceId> rejected) {
        return null;
    }
}
