/*
 * Created on 24-Dec-2003
 */
package com.ixora.rms.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.swing.table.AbstractTableModel;

import com.ixora.rms.CounterId;
import com.ixora.rms.MonitoringLevel;
import com.ixora.rms.client.model.CounterInfo;
import com.ixora.rms.client.model.EntityNode;
import com.ixora.rms.client.model.SessionModel;

/**
 * The model for the entity counters table.
 * @author Daniel Moraru
 */
public final class EntityCountersTableModel
	extends AbstractTableModel {
	/** Counter data. List of CounterInfo */
	private List counterData;
	/** In edit node */
	private EntityNode entity;
	/** Session model */
	private SessionModel sessionModel;
	private boolean logReplayMode;

	/**
	 * EntityCountersTableModel.
	 * @param sm
	 * @param logReplayMode
	 */
	public EntityCountersTableModel(SessionModel sm, boolean logReplayMode) {
		super();
		this.counterData = new LinkedList();
		this.sessionModel = sm;
		this.logReplayMode = logReplayMode;
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
		return this.counterData.size();
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
				CounterInfo cd = (CounterInfo)this.counterData.get(arg0);
				if(cd.isEnabled()) {
					return Boolean.TRUE;
				} else {
					return Boolean.FALSE;
				}
			case 1: // counter name column
				return (CounterInfo)this.counterData.get(arg0);
		}
		return null;
	}

	/**
	 * @see javax.swing.table.TableModel#getColumnClass(int)
	 */
	public Class getColumnClass(int columnIndex) {
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
	    if(logReplayMode) {
	        return false;
	    }
		if(columnIndex == 0) {
			return true;
		}
		return false;
	}

	/**
	 * Sets the counters to display.
	 * @param en entity node
	 * @param level if not null only counters matching the given level will be shown, if null
	 * the monitoring level in the entity configuration will be used
	 */
	public void setCounters(
			EntityNode en, MonitoringLevel level) {
		this.entity = en;
		// clear existing data
		this.counterData.clear();
		Collection<CounterInfo> counters = entity.getEntityInfo()
			.getCounterInfoForLevel(
					level != null ? level : en.getEntityInfo().getConfiguration().getMonitoringLevel());
		if(!logReplayMode) {
			this.counterData = new ArrayList(counters);
		} else {
			// in log replay mode filter out all disabled counters
			this.counterData = new LinkedList();
			for(CounterInfo ci : counters) {
				if(ci.isEnabled()) {
					this.counterData.add(ci);
				}
			}
		}
		fireTableDataChanged();
	}

	/**
	 * @return the selected counters as a collection of CounterId
	 */
	public Set<CounterId> getEnabledCounters() {
		Set ret = new HashSet();
		CounterInfo cd;
		for(Iterator iter = this.counterData.iterator(); iter.hasNext();) {
			cd = (CounterInfo)iter.next();
			if(cd.isEnabled()) {
				ret.add(cd.getId());
			}
		}
		return ret;
	}

	/**
	 * @param row
	 * @return
	 */
	public CounterInfo getCounterAt(int row) {
		return (CounterInfo)getValueAt(row, 1);
	}

	/**
	 * @see javax.swing.table.TableModel#setValueAt(java.lang.Object, int, int)
	 */
	// this will only be called by the editor of collumn 0
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		if(columnIndex != 0) {
			return;
		}
		CounterInfo cd = (CounterInfo)this.counterData.get(rowIndex);
		sessionModel.getCounterHelper().setCounterFlag(
		        entity,
				cd.getId(),
				CounterInfo.ENABLED,
				((Boolean)aValue).booleanValue(), true);
		// this will trigger a refresh on the entire row
		fireTableRowsUpdated(rowIndex, rowIndex);
	}

	/**
	 * Commits changes to counters.
	 */
	public void commit() {
		// just refresh data as the counters have
		// been already processed when the configuration
		// has been set
		fireTableDataChanged();
	}

	/**
	 * Rolls back changes to counters.
	 */
	public void rollback() {
		sessionModel.getCounterHelper().rollbackCounters(this.entity);
		fireTableDataChanged();
	}
}


