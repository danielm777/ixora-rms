/**
 * 03-Jul-2006
 */
package com.ixora.rms.ui.tools.agentinstaller;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import com.ixora.common.Product;
import com.ixora.common.ui.AppDialog;
import com.ixora.common.ui.UIFactoryMgr;
import com.ixora.common.ui.actions.ActionCancel;
import com.ixora.common.ui.actions.ActionOk;
import com.ixora.rms.repository.AgentInstallationData;

/**
 * @author Daniel Moraru
 */
public class AgentSelectorDialog extends AppDialog {
	private JPanel fContainer;
	private AgentsTableModel fModel;
	private AgentInstallationData fResult;

	private static class AgentsTableModel extends AbstractTableModel {
		private static class Entry implements Comparable<Entry> {
			AgentInstallationData fAgent;
			Boolean fSelected;

			Entry(AgentInstallationData agent) {
				fAgent = agent;
			}
			public int compareTo(Entry o) {
				return String.valueOf(fAgent.getAgentInstallationId())
					.compareTo(String.valueOf(o.fAgent.getAgentInstallationId()));
			}
		}

		private List<Entry> fAgents;

		AgentsTableModel(List<AgentInstallationData> agents) {
			fAgents = new LinkedList<Entry>();
			for(AgentInstallationData aid : agents) {
				fAgents.add(new Entry(aid));
			}
		}
		public int getColumnCount() {
			return 2;
		}
		public Class< ? > getColumnClass(int columnIndex) {
			return columnIndex == 0 ? Boolean.class : String.class;
		}
		public String getColumnName(int column) {
			return column == 0 ? "" : "Agent identifier"; // TODO localize
		}
		public int getRowCount() {
			return fAgents.size();
		}
		public Object getValueAt(int row, int column) {
			return column == 0 ? fAgents.get(row).fSelected : fAgents.get(row).fAgent.getAgentInstallationId();
		}
		public boolean isCellEditable(int row, int column) {
			return column == 0;
		}
		public void setValueAt(Object aValue, int row, int column) {
			for(Entry entry : fAgents) {
				entry.fSelected = Boolean.FALSE;
			}
			fAgents.get(row).fSelected = (Boolean)aValue;
			// this will get rid of the selection
			fireTableDataChanged();
		}
		public AgentInstallationData getSelectedAgent() {
			for(Entry entry : fAgents) {
				if(entry.fSelected) {
					return entry.fAgent;
				}
			}
			return null;
		}
	}

	/**
	 * @param parent
	 * @param orientation
	 */
	public AgentSelectorDialog(Dialog parent,
			File productRoot,
			List<AgentInstallationData> newAgents) {
		super(parent, VERTICAL);
		setModal(true);
		// TODO localize
		setTitle("Select Custom Agent To Import");

		fContainer = new JPanel(new BorderLayout());

		fModel = new AgentsTableModel(newAgents);
		JTable table  = new JTable(fModel);
		table.getColumnModel().getColumn(0).setMaxWidth(25);

		JScrollPane sp = new JScrollPane();
		sp.setViewportView(table);

		JEditorPane htmlPane = UIFactoryMgr.createHtmlPane();
		// TODO localize
		htmlPane.setText("The following custom agents have been found in the "
				+ Product.getProductInfo().getName()
				+ " instance at "
				+ productRoot.getAbsolutePath()
				+ ".<br>Please select the one you wish to import.");
		htmlPane.setPreferredSize(new Dimension(300, 50));
		fContainer.add(htmlPane, BorderLayout.NORTH);
		fContainer.add(sp, BorderLayout.CENTER);

		buildContentPane();
	}

	/**
	 * @return
	 */
	public AgentInstallationData getResult() {
		return fResult;
	}

	/**
	 * @see com.ixora.common.ui.AppDialog#getDisplayPanels()
	 */
	protected Component[] getDisplayPanels() {
		return new Component[]{fContainer};
	}

	/**
	 * @see com.ixora.common.ui.AppDialog#getButtons()
	 */
	protected JButton[] getButtons() {
		return new JButton[]{
				new JButton(new ActionOk() {
					public void actionPerformed(ActionEvent e) {
						fResult = fModel.getSelectedAgent();
						dispose();
					}}),
				new JButton(new ActionCancel(){
					public void actionPerformed(ActionEvent e) {
						fResult = null;
						dispose();
					}
				})};
	}

}
