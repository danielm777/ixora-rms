/**
 * 01-Aug-2005
 */
package com.ixora.jobs.ui.library;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import com.ixora.jobs.JobsComponent;
import com.ixora.jobs.library.JobLibraryDefinition;
import com.ixora.jobs.ui.messages.Msg;
import com.ixora.common.MessageRepository;

/**
 * @author Daniel Moraru
 */
public class JobLibraryTableModel extends AbstractTableModel {
	private static final long serialVersionUID = -6088172562724008270L;

	/** Column1 */
	private final String[] fColumns = {
		MessageRepository.get(JobsComponent.NAME, Msg.MODELS_TABLES_JOBLIBRARY_COL1),
	};

	private List<JobLibraryDefinition> fEntries;


	/**
	 * @param entries
	 */
	public JobLibraryTableModel(Collection<JobLibraryDefinition> entries) {
		super();
		fEntries = new ArrayList<JobLibraryDefinition>(entries);
	}

	/**
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	public int getRowCount() {
		return fEntries.size();
	}

	/**
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	public int getColumnCount() {
		return fColumns.length;
	}

	/**
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	public Object getValueAt(int rowIndex, int columnIndex) {
		JobLibraryDefinition  entry = fEntries.get(rowIndex);
		switch(columnIndex) {
		case 0:
			return entry.getName();
		}
		return null;
	}

	/**
	 * @see javax.swing.table.AbstractTableModel#getColumnName(int)
	 */
	public String getColumnName(int column) {
		return fColumns[column];
	}

	/**
	 * @param rowIndex
	 * @return
	 */
	public JobLibraryDefinition getEntryAtRow(int rowIndex) {
		return fEntries.get(rowIndex);
	}

// package level access
	/**
	 * Sets new entries.
	 * @param entries
	 */
	void setEntries(Collection<JobLibraryDefinition> entries) {
		fEntries = new ArrayList<JobLibraryDefinition>(entries);
		fireTableDataChanged();
	}
}
