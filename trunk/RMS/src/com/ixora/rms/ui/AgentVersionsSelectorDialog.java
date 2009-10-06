/*
 * Created on 09-Mar-2005
 */
package com.ixora.rms.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import com.ixora.common.MessageRepository;
import com.ixora.common.ui.AppDialog;
import com.ixora.common.ui.UIFactoryMgr;
import com.ixora.common.ui.actions.ActionCancel;
import com.ixora.common.ui.actions.ActionOk;
import com.ixora.common.utils.Utils;
import com.ixora.rms.ui.messages.Msg;

/**
 * @author Daniel Moraru
 */
public final class AgentVersionsSelectorDialog extends AppDialog {
	private AgentVersionsTableModel fTableModel;
	private JTable fTable;
	private JPanel fPanel;
	private Set<String> fResult;

	/**
	 * Model for agent versions table.
	 */
	private static class AgentVersionsTableModel extends AbstractTableModel {
		private static class Entry implements Comparable<Entry> {
			String fVersion;
			Boolean fSelected;

			Entry(String v, boolean selected) {
				fVersion = v;
				fSelected = Boolean.valueOf(selected);
			}
			public int compareTo(Entry o) {
				return fVersion.compareToIgnoreCase(o.fVersion);
			}
		}
		private List<Entry> fVersions;

		public AgentVersionsTableModel(Set<String> versions, Set<String> selected) {
			if(Utils.isEmptyCollection(versions)) {
				fVersions = new LinkedList<Entry>();
			} else {
				fVersions = new ArrayList(versions.size());
				for(String v : versions) {
					fVersions.add(new Entry(v, selected == null ? false : selected.contains(v)));
				}
			}
			Collections.sort(fVersions);
		}
		public int getRowCount() {
			return fVersions.size();
		}
		public int getColumnCount() {
			return 2;
		}
		public String getColumnName(int column) {
			return "";
		}
		public Class< ? > getColumnClass(int columnIndex) {
			return columnIndex == 0 ? Boolean.class : String.class;
		}
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return columnIndex == 0;
		}
		public Object getValueAt(int rowIndex, int columnIndex) {
			return columnIndex == 0 ? fVersions.get(rowIndex).fSelected : fVersions.get(rowIndex).fVersion;
		}
		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			fVersions.get(rowIndex).fSelected = (Boolean)aValue;
			// this will get rid of the selection
			fireTableDataChanged();
		}
		public Set<String> getSelected() {
			Set<String> lst = new HashSet<String>();
			for(Entry e : fVersions) {
				if(e.fSelected.booleanValue()) {
					lst.add(e.fVersion);
				}
			}
			return lst;
		}
	}

    /**
     * Constructor.
     */
    public AgentVersionsSelectorDialog(JFrame parent, Set<String> versions, Set<String> selected) {
        super(parent, VERTICAL);
        init(versions, selected);
    }

	/**
	 * Constructor.
	 */
	public AgentVersionsSelectorDialog(JDialog parent, Set<String> versions, Set<String> selected) {
		super(parent, VERTICAL);
        init(versions, selected);
	}

    /**
     * @param versions
     * @param selected
     */
    private void init(Set<String> versions, Set<String> selected) {
        setTitle(MessageRepository.get(Msg.TITLE_SELECT_AGENT_VERSIONS));
        setPreferredSize(new Dimension(350, 200));
        fTableModel = new AgentVersionsTableModel(versions, selected);
        fTable = new JTable(fTableModel);
        fTable.getColumnModel().getColumn(0).setMaxWidth(25);
        JScrollPane sp = UIFactoryMgr.createScrollPane();
        sp.setViewportView(fTable);
        sp.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        sp.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        sp.setMinimumSize(new Dimension(200, 50));
        fPanel = new JPanel(new BorderLayout());
        fPanel.add(sp, BorderLayout.CENTER);

        buildContentPane();
    }

	/**
	 * @see com.ixora.common.ui.AppDialog#getDisplayPanels()
	 */
	protected Component[] getDisplayPanels() {
		return new Component[] {fPanel};
	}

	/**
	 * @see com.ixora.common.ui.AppDialog#getButtons()
	 */
	protected JButton[] getButtons() {
		return new JButton[] {
				new JButton(new ActionOk() {
					public void actionPerformed(ActionEvent e) {
						fResult = fTableModel.getSelected();
						dispose();
					}}),
				new JButton(new ActionCancel() {
					public void actionPerformed(ActionEvent e) {
						fResult = null;
						dispose();
					}})
		};
	}

	/**
	 * @return
	 */
	public Set<String> getSelectedVersions() {
		return fResult;
	}
}
