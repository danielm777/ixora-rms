/*
 * Created on 23-Jan-2005
 */
package com.ixora.rms.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

import com.ixora.common.MessageRepository;
import com.ixora.common.ui.AppDialog;
import com.ixora.common.ui.UIConfiguration;
import com.ixora.common.ui.UIExceptionMgr;
import com.ixora.common.ui.UIFactoryMgr;
import com.ixora.common.ui.actions.ActionCancel;
import com.ixora.common.ui.actions.ActionOk;
import com.ixora.rms.providers.ProvidersComponent;
import com.ixora.rms.repository.ProviderInstance;
import com.ixora.rms.ui.messages.Msg;

/**
 * @author Daniel Moraru
 */
public final class AgentOptionalProvidersSelectorDialog extends AppDialog {

	/**
	 * Data for the providers table model.
	 */
	private final static class ProviderData implements Comparable<ProviderData> {
		Boolean selected;
		ProviderInstance provider;
		String translatedInstanceName;
		String translatedInstanceDescription;

		ProviderData(ProviderInstance pid) {
			this.provider = pid;
			this.translatedInstanceName = MessageRepository.get(ProvidersComponent.NAME, pid.getInstanceName());
			this.translatedInstanceDescription = MessageRepository.get(ProvidersComponent.NAME, pid.getInstanceDescription());
			this.selected = Boolean.FALSE;
		}

		public int compareTo(ProviderData o) {
			return this.translatedInstanceName.compareToIgnoreCase(o.translatedInstanceName);
		}
	}

	/**
	 * Table model.
	 */
	private final static class ProvidersTableModel extends AbstractTableModel {
		private ProviderData[] providers;

		public ProvidersTableModel(ProviderInstance[] providers, String[] selectedProviderInstances) {
			this.providers = new ProviderData[providers.length];
			List<String> lst = selectedProviderInstances == null ? null
					: Arrays.asList(selectedProviderInstances);
			for(int i = 0; i < providers.length; ++i) {
				ProviderInstance pd = providers[i];
				ProviderData providerData = new ProviderData(pd);
				// if given a list use that
				if(lst != null) {
					if(lst.contains(pd.getInstanceName())) {
						providerData.selected = Boolean.TRUE;
					}
				// else enable only the ones which are selected by default
				} else if(pd.isOptional() && pd.isSelectedByDefault()) {
					providerData.selected = Boolean.TRUE;
				}
				this.providers[i] = providerData;
			}
			Arrays.sort(this.providers);
		}
		/**
		 * @see javax.swing.table.TableModel#getRowCount()
		 */
		public int getRowCount() {
			return providers.length;
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
		public Object getValueAt(int rowIndex, int columnIndex) {
			ProviderData pd = this.providers[rowIndex];
			return columnIndex == 0 ? pd.selected : pd.translatedInstanceName;
		}

		/**
		 * @see javax.swing.table.TableModel#getColumnName(int)
		 */
		public String getColumnName(int column) {
			return null;
		}
		/**
		 * @see javax.swing.table.TableModel#getColumnClass(int)
		 */
		public Class<?> getColumnClass(int columnIndex) {
			return columnIndex == 0 ? Boolean.class : super.getColumnClass(columnIndex);
		}
		/**
		 * @see javax.swing.table.TableModel#isCellEditable(int, int)
		 */
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return columnIndex == 0 ? true : false;
		}
		/**
		 * @see javax.swing.table.TableModel#setValueAt(java.lang.Object, int, int)
		 */
		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			ProviderData pd = this.providers[rowIndex];
			pd.selected = (Boolean)aValue;
		}
		/**
		 * @return the selected providers instance name
		 */
		public List<String> getSelectedProviderInstanceNames() {
			List<String> ret = new LinkedList<String>();
			for(ProviderData pd : this.providers) {
				if(pd.selected.booleanValue()) {
					ret.add(pd.provider.getInstanceName());
				}
			}
			return ret;
		}

		/**
		 * @param row
		 * @return
		 */
		public ProviderData getProviderDataAtRow(int row) {
			return this.providers[row];
		}
	}

	/**
	 * Event handler
	 */
	private final class EventHandler implements ListSelectionListener {
		/**
		 * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
		 */
		public void valueChanged(ListSelectionEvent e) {
			handleTableSelectionChanged();
		}
	}

	private JPanel fPanel;
	private JTable fTable;
	private JTextArea fTextAreaDescription;
	private ProvidersTableModel fModel;
	private List<String> fReturnValue;

	/**
	 * Constructor.
	 * @param parent
	 * @param selectedProviderInstances
	 * @param orientation
	 */
	public AgentOptionalProvidersSelectorDialog(JFrame parent, ProviderInstance[] providers,
				String[] selectedProviderInstances) {
		super(parent, VERTICAL);
		setModal(true);
		setTitle(MessageRepository.get(Msg.TITLE_SELECT_OPTIONAL_PROVIDERS));
		setPreferredSize(new Dimension(400, 300));
		fPanel = new JPanel(new BorderLayout());
		JScrollPane sp = UIFactoryMgr.createScrollPane();
		fModel = new ProvidersTableModel(providers, selectedProviderInstances);
		fTable = new JTable(fModel);
		fTable.getColumnModel().getColumn(0).setMaxWidth(25);
		sp.setViewportView(fTable);
		fPanel.add(sp, BorderLayout.CENTER);
		fTextAreaDescription = UIFactoryMgr.createTextArea();
		fTextAreaDescription.setBackground(fPanel.getBackground());
		fTextAreaDescription.setEditable(false);
		fTextAreaDescription.setWrapStyleWord(true);
		fTextAreaDescription.setLineWrap(true);
		sp = UIFactoryMgr.createScrollPane();
		sp.setViewportView(fTextAreaDescription);
		sp.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createEmptyBorder(
					UIConfiguration.getPanelPadding(), 0, 0, 0),
					sp.getBorder()));
		sp.setPreferredSize(new Dimension(200, 50));
		fPanel.add(sp, BorderLayout.SOUTH);
		fTable.getSelectionModel().addListSelectionListener(new EventHandler());
		buildContentPane();
        fReturnValue = fModel.getSelectedProviderInstanceNames();
	}

	/**
	 *
	 */
	public List<String> getSelectedProviders() {
		return this.fReturnValue;
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
				UIFactoryMgr.createButton(new ActionOk() {
					public void actionPerformed(ActionEvent e) {
						handleOk();
					}
				}),
				UIFactoryMgr.createButton(new ActionCancel() {
					public void actionPerformed(ActionEvent e) {
						handleCancel();
					}
				})
		};
	}

	/**
	 * @param e
	 */
	private void handleTableSelectionChanged() {
		try {
			int idx = fTable.getSelectedRow();
			if(idx < 0) {
				return;
			}
			ProviderData pd = fModel.getProviderDataAtRow(idx);
			fTextAreaDescription.setText(pd.translatedInstanceDescription);
		} catch(Exception e) {
			UIExceptionMgr.userException(e);
		}
	}

    /**
     *
     */
	private void handleOk() {
		fReturnValue = fModel.getSelectedProviderInstanceNames();
		dispose();
	}

    /**
     *
     */
	private void handleCancel() {
		fReturnValue = null;
		dispose();
	}
}
