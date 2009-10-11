/*
 * Created on 31-Dec-2004
 */
package com.ixora.rms.ui.artefacts.provider;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.table.AbstractTableModel;

import com.ixora.common.ui.UIConfiguration;
import com.ixora.rms.client.model.ProviderInstanceInfo;
import com.ixora.rms.providers.ProviderState;

/**
 * @author Daniel Moraru
 */
public class ProviderInstanceTableModel extends AbstractTableModel {
	private static final long serialVersionUID = -3892678242612823857L;

	/** Data */
	private List<ProviderInstanceInfo> providers;

	// icons
	private static final ImageIcon iconReady = UIConfiguration.getIcon("provider_ready.gif");
	private static final ImageIcon iconStarted = UIConfiguration.getIcon("provider_started.gif");
	private static final ImageIcon iconError = UIConfiguration.getIcon("provider_error.gif");

	/**
	 * Constructor.
	 */
	public ProviderInstanceTableModel() {
		super();
		this.providers = new LinkedList<ProviderInstanceInfo>();
	}

	/**
	 * @param providers
	 */
	public void setProviderInstances(Collection<ProviderInstanceInfo> providers) {
		this.providers.clear();
		if(providers != null) {
			this.providers.addAll(providers);
		}
		fireTableDataChanged();
	}

	/**
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	public int getRowCount() {
		return this.providers.size();
	}

	/**
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	public int getColumnCount() {
		return 2;
	}

	/**
	 * @see javax.swing.table.TableModel#getColumnClass(int)
	 */
	public Class<?> getColumnClass(int columnIndex) {
		return columnIndex == 0 ? ImageIcon.class : super.getColumnClass(1);
	}

	/**
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	public Object getValueAt(int rowIndex, int columnIndex) {
		if(columnIndex == 1) {
			return this.providers.get(rowIndex);
		} else {
			ProviderInstanceInfo pi = this.providers.get(rowIndex);
			ProviderState state = pi.getProviderState();
			if(state == ProviderState.READY) {
				return iconReady;
			} else if(state == ProviderState.STARTED) {
				return iconStarted;
			} else if(state == ProviderState.ERROR) {
				return iconError;
			}
			return null;
		}
	}

	/**
	 * @see javax.swing.table.TableModel#getColumnName(int)
	 */
	public String getColumnName(int column) {
		return "";
	}

	/**
	 * @param row
	 * @return
	 */
	public ProviderInstanceInfo getProviderInstanceInfoAtRow(int row) {
		return this.providers.get(row);
	}
}
