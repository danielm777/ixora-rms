/*
 * Created on 24-Dec-2003
 */
package com.ixora.rms.ui.artefacts;

import java.awt.Cursor;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import com.ixora.rms.ResourceId;
import com.ixora.common.ui.UIExceptionMgr;
import com.ixora.common.ui.jobs.UIWorkerJobDefault;
import com.ixora.rms.client.model.ArtefactInfo;
import com.ixora.rms.client.model.SessionModel;
import com.ixora.rms.ui.RMSViewContainer;

/**
 * The model for the queries table.
 * @author Daniel Moraru
 */
public abstract class SelectableArtefactTableModel<T extends ArtefactInfo>
	extends AbstractTableModel {
	private static final long serialVersionUID = 7632004347915596419L;
	/** List of ArtefactInfo */
	protected List<T> fArtefactData;
	/** Context */
	protected ResourceId fContext;
	/** Session model */
	protected SessionModel fSessionModel;
	/** View container */
	protected RMSViewContainer fViewContainer;
	/** Log replay mode */
	protected boolean fLogReplayMode;

	/**
	 * ArtefactsTableModel.
	 * @param vc
	 * @param sm
	 * @param logReplayMode
	 */
	public SelectableArtefactTableModel(
	        RMSViewContainer vc, SessionModel sm, boolean logReplayMode) {
		super();
		if(vc == null) {
			throw new IllegalArgumentException("null view container");
		}
		if(sm == null) {
			throw new IllegalArgumentException("null session model");
		}
		this.fArtefactData = new LinkedList<T>();
		this.fSessionModel = sm;
		this.fViewContainer = vc;
		this.fLogReplayMode = logReplayMode;
	}

	/**
	 * @see javax.swing.table.AbstractTableModel#gtColumnName(int)
	 */
	public String getColumnName(int arg1) {
		return "";
	}

	/**
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	public int getRowCount() {
		return this.fArtefactData.size();
	}

	/**
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	public int getColumnCount() {
		return 2;
	}

	/**
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	public Object getValueAt(int arg0, int arg1) {
		switch(arg1) {
			case 0: // enabled column
				ArtefactInfo ai = (ArtefactInfo)this.fArtefactData.get(arg0);
				if(ai.getFlag(ArtefactInfo.ENABLED)) {
					return Boolean.TRUE;
				} else {
					return Boolean.FALSE;
				}
			case 1: // query name column
				return (ArtefactInfo)this.fArtefactData.get(arg0);
		}
		return null;
	}

	/**
	 * @see javax.swing.table.TableModel#getColumnClass(int)
	 */
	public Class<?> getColumnClass(int columnIndex) {
		switch(columnIndex) {
			case 0: // enabled column
				return Boolean.class;
			case 1: // counter name column
				return String.class;
		}
		return null;
	}

	/**
	 * @see javax.swing.table.TableModel#isCellEditable(int, int)
	 */
	public boolean isCellEditable(int rowIndex, int columnIndex) {
	    if(fLogReplayMode) {
	        return false;
	    }
		if(columnIndex == 0) {
			return true;
		}
		return false;
	}

	/**
	 * Sets the artefacts to display.
	 * @param context the resource id describing the context
	 * of the current artefacts.
	 * @param artefacts the artefacts to set (collection of ArtefactInfo)
	 */
	public void setArtefacts(
			ResourceId context,
			Collection<T> artefacts) {
		this.fContext = context;
		this.fArtefactData.clear();
		if(artefacts != null) {
			this.fArtefactData.addAll(artefacts);
		}
		Collections.sort(this.fArtefactData);
		fireTableDataChanged();
	}

	/**
	 * @see javax.swing.table.TableModel#setValueAt(java.lang.Object, int, int)
	 */
	public void setValueAt(final Object aValue,
	        final int rowIndex, final int columnIndex) {
		try {
			if(columnIndex != 0) {
				return;
			}
			this.fViewContainer.getAppWorker().runJobSynch(
			        new UIWorkerJobDefault(
			                fViewContainer.getAppFrame(), Cursor.WAIT_CURSOR, "") {
                public void work() throws Exception {
        			setArtefactEnabled(rowIndex, ((Boolean)aValue).booleanValue());
        			// this will trigger a refresh on the entire row
        			fireTableRowsUpdated(rowIndex, rowIndex);
                }
                public void finished(Throwable ex) {
                }
			});
		} catch(Exception e) {
			// this will trigger a refresh on the entire row
			// before the exception is displayed
			fireTableRowsUpdated(rowIndex, rowIndex);
			UIExceptionMgr.userException(e);
		}
	}

	/**
	 * Enables the artefact.
	 * @param rowIndex
	 * @param e
	 */
	protected abstract void setArtefactEnabled(int rowIndex, boolean e);

	/**
	 * @return the artefacts that must be realized
	 * (enabled but not committed)<br>
	 * List of ArtefactInfo
	 */
	protected List<T> getArtefactsToRealize() {
		List<T> ret = new LinkedList<T>();
		for(Iterator<T> iter = this.fArtefactData.iterator(); iter.hasNext();) {
			T ai = iter.next();
			if(ai.getFlag(ArtefactInfo.ENABLED) && !ai.isCommitted()) {
				ret.add(ai);
			}
		}
		return ret;
	}

	/**
	 * @return the artefacts that must be unrealized
	 * (disabled but not committed)<br>
	 * List of ArtefactInfo
	 */
	protected List<T> getArtefactsToUnRealize() {
		List<T> ret = new LinkedList<T>();
		for(Iterator<T> iter = this.fArtefactData.iterator(); iter.hasNext();) {
			T ai = iter.next();
			if(!ai.getFlag(ArtefactInfo.ENABLED) && !ai.isCommitted()) {
				ret.add(ai);
			}
		}
		return ret;
	}
}