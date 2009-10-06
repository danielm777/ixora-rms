/*
 * Created on 03-Feb-2005
 */
package com.ixora.rms.ui.tools.providermanager;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.ixora.rms.HostId;
import com.ixora.rms.ResourceId;
import com.ixora.common.ConfigurationMgr;
import com.ixora.common.MessageRepository;
import com.ixora.common.ui.AppDialog;
import com.ixora.common.ui.UIExceptionMgr;
import com.ixora.common.ui.UIFactoryMgr;
import com.ixora.common.ui.UIUtils;
import com.ixora.common.ui.actions.ActionClose;
import com.ixora.common.ui.popup.PopupListener;
import com.ixora.common.utils.Utils;
import com.ixora.common.xml.XMLUtils;
import com.ixora.common.xml.exception.XMLException;
import com.ixora.rms.agents.AgentId;
import com.ixora.rms.providers.ProviderConfiguration;
import com.ixora.rms.providers.ProvidersComponent;
import com.ixora.rms.repository.AgentInstallationData;
import com.ixora.rms.repository.AuthoredArtefact;
import com.ixora.rms.repository.ParserInstance;
import com.ixora.rms.repository.ProviderInstance;
import com.ixora.rms.repository.ProviderInstanceMap;
import com.ixora.rms.services.AgentRepositoryService;
import com.ixora.rms.services.ParserRepositoryService;
import com.ixora.rms.services.ProviderInstanceRepositoryService;
import com.ixora.rms.services.ProviderRepositoryService;
import com.ixora.rms.ui.RMSViewContainer;
import com.ixora.rms.ui.actions.ActionViewXML;
import com.ixora.rms.ui.tools.providermanager.messages.Msg;

/**
 * @author Daniel Moraru
 */
public final class ProviderInstanceManagerDialog extends AppDialog {
	private JList fAgentsList;
	private JList fProvidersList;
	private JPanel fAgentsPanel;
	private JPanel fProvidersPanel;
	private AgentRepositoryService fAgentRepository;
	private ProviderInstanceRepositoryService fProviderInstanceRepository;
	private ParserRepositoryService fParserRepository;
	private ProviderRepositoryService fProviderRepository;
	private RMSViewContainer fViewContainer;
	private EventHandler fEventHandler;
	private ActionCreateLikeProviderInstance fActionCreateLike;
	private ActionEditProviderInstance fActionEdit;
	private ActionAddProviderInstance fActionAdd;
	private ActionRemoveProviderInstance fActionRemove;
	private ActionViewXML fActionViewXML;

    private JPopupMenu fPopupMenu;
	private JMenuItem fMenuItemEdit;
	private JMenuItem fMenuItemCreateLike;
	private JMenuItem fMenuItemAdd;
	private JMenuItem fMenuItemRemove;
	private JMenuItem fMenuItemViewXML;

	/**
	 * Edit provider action.
	 */
	private final class ActionEditProviderInstance extends AbstractAction {
		public ActionEditProviderInstance() {
			super();
			UIUtils.setUsabilityDtls(MessageRepository.get(
					ProviderManagerComponent.NAME, Msg.ACTIONS_EDIT_PROVIDER_INSTANCE), this);
		}
		/**
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent e) {
			handleEditProviderInstance();
		}
	}

	/**
	 * Create Like... provider action.
	 */
	private final class ActionCreateLikeProviderInstance extends AbstractAction {
		public ActionCreateLikeProviderInstance() {
			super();
			UIUtils.setUsabilityDtls(MessageRepository.get(
					ProviderManagerComponent.NAME, Msg.ACTIONS_CREATE_LIKE_PROVIDER_INSTANCE), this);
		}
		/**
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent e) {
			handleCreateLikeProviderInstance();
		}
	}

	/**
	 * Add provider action.
	 */
	private final class ActionAddProviderInstance extends AbstractAction {
		public ActionAddProviderInstance() {
			super();
			UIUtils.setUsabilityDtls(
					MessageRepository.get(ProviderManagerComponent.NAME, Msg.ACTIONS_ADD_PROVIDER_INSTANCE), this);
		}
		/**
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent e) {
			handleAddProviderInstance();
		}
	}

	/**
	 * Remove provider action.
	 */
	private final class ActionRemoveProviderInstance extends AbstractAction {
		public ActionRemoveProviderInstance() {
			super();
			UIUtils.setUsabilityDtls(MessageRepository.get(
					ProviderManagerComponent.NAME, Msg.ACTIONS_REMOVE_PROVIDER_INSTANCE), this);
		}
		/**
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent e) {
			handleRemoveProviderInstance();
		}
	}

	private static final class ListAgentData implements Comparable<ListAgentData> {
		String translatedName;
		String installationId;
		Set<String> systemVersions;

		ListAgentData(AgentInstallationData aid) {
			installationId = aid.getAgentInstallationId();
			translatedName = MessageRepository.get(aid.getMessageCatalog(), aid.getAgentName());
			String[] versions = aid.getSystemUnderObservationVersions();
            if(!Utils.isEmptyArray(versions)) {
                systemVersions = new TreeSet<String>(Arrays.asList(versions));
            }
		}

		public String toString() {
			return translatedName;
		}

		/**
		 * @see java.lang.Comparable#compareTo(T)
		 */
		public int compareTo(ListAgentData o) {
			return translatedName.compareToIgnoreCase(o.translatedName);
		}
	}

	private static final class ListProviderInstanceData implements Comparable<ListProviderInstanceData> {
		String translatedName;
		String translatedDescription;
		ProviderInstance providerInstanceData;
		private String toString;

        ListProviderInstanceData(ProviderInstance pid) {
			providerInstanceData = pid;
			translatedName = MessageRepository.get(ProvidersComponent.NAME, pid.getInstanceName());
			translatedDescription = MessageRepository.get(ProvidersComponent.NAME, pid.getInstanceDescription());

            Set<String> av = pid.getAgentVersions();
            StringBuffer buff = new StringBuffer();
            if(!Utils.isEmptyCollection(av)) {
                for(Iterator<String> iter = av.iterator(); iter.hasNext();) {
                    buff.append(iter.next());
                    if(iter.hasNext()) {
                        buff.append(", ");
                    }
                }
            }
            toString = "<html>" + translatedName + " (" + translatedDescription + ") <b>" + buff.toString() + "</b><html>";
		}

		public String toString() {
			return toString;
		}

		/**
		 * @see java.lang.Comparable#compareTo(T)
		 */
		public int compareTo(ListProviderInstanceData o) {
			return this.translatedName.compareToIgnoreCase(o.translatedName);
		}
	}

	private final class EventHandler extends PopupListener
		implements ListSelectionListener {
		/**
		 * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
		 */
		public void valueChanged(ListSelectionEvent e) {
            if(e.getValueIsAdjusting()) {
                return;
            }
			if(e.getSource() == fAgentsList.getSelectionModel()) {
				handleAgentSelected();
				return;
			}
			if(e.getSource() == fProvidersList.getSelectionModel()) {
				handleProviderSelected();
				return;
			}
		}
		/**
		 * @see com.ixora.common.ui.popup.PopupListener#showPopup(java.awt.event.MouseEvent)
		 */
		protected void showPopup(MouseEvent e) {
			if(e.getSource() == fProvidersList) {
				handleShowPopupOnProvidersList(e);
				return;
			}
		}
	}

	/**
	 * Constructor.
	 * @param parent
	 * @param pis
	 */
	public ProviderInstanceManagerDialog(
           RMSViewContainer viewContainer,
           AgentRepositoryService ars,
           ProviderInstanceRepositoryService providerInstanceRepository,
		   ProviderRepositoryService providerRepository,
		   ParserRepositoryService parserRepository) {
		super(viewContainer.getAppFrame(), VERTICAL);
		setPreferredSize(new Dimension(450, 400));
		setTitle(MessageRepository.get(ProviderManagerComponent.NAME, Msg.TITLE_PROVIDER_INSTANCE_MANAGER));
		fEventHandler = new EventHandler();
		fViewContainer = viewContainer;
		fAgentRepository = ars;
		fProviderInstanceRepository = providerInstanceRepository;
		fProviderRepository = providerRepository;
		fParserRepository = parserRepository;

		AgentInstallationData[] aids = fAgentRepository.getInstalledAgents().values().toArray(
				new AgentInstallationData[0]);
		ListAgentData[] lstAgents = new ListAgentData[aids.length];
		for(int i = 0; i < aids.length; ++i) {
			lstAgents[i] = new ListAgentData(aids[i]);
		}
		Arrays.sort(lstAgents);
		fAgentsList = UIFactoryMgr.createList();
		fAgentsList.setListData(lstAgents);

		fAgentsPanel = new JPanel(new BorderLayout());
		fAgentsPanel.setBorder(UIFactoryMgr.createTitledBorder(
				MessageRepository.get(ProviderManagerComponent.NAME, Msg.TEXT_AGENTS)));
		JScrollPane sp = UIFactoryMgr.createScrollPane();
		sp.setViewportView(fAgentsList);
		fAgentsPanel.add(sp, BorderLayout.CENTER);

		fProvidersList = UIFactoryMgr.createList();
		fProvidersPanel = new JPanel(new BorderLayout());
		fProvidersPanel.setBorder(UIFactoryMgr.createTitledBorder(
				MessageRepository.get(ProviderManagerComponent.NAME, Msg.TEXT_PROVIDER_INSTANCES)));
		sp = UIFactoryMgr.createScrollPane();
		sp.setViewportView(fProvidersList);
		fProvidersPanel.add(sp, BorderLayout.CENTER);

   		fActionAdd = new ActionAddProviderInstance();
   		fActionEdit = new ActionEditProviderInstance();
   		fActionRemove = new ActionRemoveProviderInstance();
   		fActionCreateLike = new ActionCreateLikeProviderInstance();
   		fActionViewXML = new ActionViewXML(fViewContainer) {
			protected String getXML() throws Exception {
				return getXMLDefinitionForSelectedProviderInstance();
			}

   		};

		fActionEdit.setEnabled(false);
		fActionCreateLike.setEnabled(false);
		fActionViewXML.setEnabled(false);
		fActionRemove.setEnabled(false);

		fAgentsList.getSelectionModel().addListSelectionListener(fEventHandler);
		fAgentsList.setSelectedIndex(0);
   		fProvidersList.addMouseListener(fEventHandler);
		fProvidersList.getSelectionModel().addListSelectionListener(fEventHandler);
   		buildContentPane();
	}

	/**
	 * @see com.ixora.common.ui.AppDialog#getDisplayPanels()
	 */
	protected Component[] getDisplayPanels() {
		return new Component[] {fAgentsPanel, fProvidersPanel};
	}

	/**
	 * @see com.ixora.common.ui.AppDialog#getButtons()
	 */
	protected JButton[] getButtons() {
		return new JButton[] {
			UIFactoryMgr.createButton(this.fActionAdd),
			UIFactoryMgr.createButton(this.fActionEdit),
			UIFactoryMgr.createButton(this.fActionCreateLike),
			UIFactoryMgr.createButton(this.fActionViewXML),
			UIFactoryMgr.createButton(this.fActionRemove),
			UIFactoryMgr.createButton(new ActionClose() {
				public void actionPerformed(ActionEvent e) {
					dispose();
				}
			})};
	}

	/**
	 *
	 */
	private void handleAgentSelected() {
		try {
			ListAgentData ad = (ListAgentData)fAgentsList.getSelectedValue();
			if(ad == null) {
				return;
			}
			this.fActionEdit.setEnabled(false);
			this.fActionEdit.setEnabled(false);
			this.fActionRemove.setEnabled(false);
			ProviderInstanceMap pim = fProviderInstanceRepository.getAgentProviderInstances(
					ad.installationId);
			if(pim == null) {
				fProvidersList.setListData(new Object[0]);
			} else {
				Collection<ProviderInstance> all = pim.getAll();
                ProviderInstance[] pids = all.toArray(new ProviderInstance[all.size()]);
                ListProviderInstanceData[] lstPids = new ListProviderInstanceData[pids.length];
				for(int i = 0; i < lstPids.length; ++i) {
					lstPids[i] = new ListProviderInstanceData(pids[i]);
				}
				Arrays.sort(lstPids);
				fProvidersList.setListData(lstPids);
			}
		} catch(Throwable e) {
			UIExceptionMgr.userException(e);
		}
	}

	/**
	 *
	 */
	private void handleProviderSelected() {
		try {
			ListProviderInstanceData pid = (ListProviderInstanceData)fProvidersList.getSelectedValue();
			if(pid == null) {
				return;
			}
			this.fActionEdit.setEnabled(true);
			this.fActionCreateLike.setEnabled(true);
			this.fActionViewXML.setEnabled(true);
			this.fActionRemove.setEnabled(true);
		} catch(Exception e) {
			UIExceptionMgr.userException(e);
		}
	}

    /**
     * Edits a provider instance.
     */
    private void handleEditProviderInstance() {
		try {
			ListProviderInstanceData selProv = (ListProviderInstanceData)fProvidersList.getSelectedValue();
			if(selProv == null) {
				return;
			}
			ListAgentData selAg = (ListAgentData)fAgentsList.getSelectedValue();
			if(selAg == null) {
				return;
			}
			ProviderInstanceEditorDialog editor = new ProviderInstanceEditorDialog(
			       fViewContainer,
			       fProviderInstanceRepository,
				   fProviderRepository,
				   fParserRepository,
                   selAg.systemVersions,
                   new ResourceId(new HostId(""), new AgentId(selAg.installationId), null, null),
                   selProv.providerInstanceData);
			editor.setModal(true);
			UIUtils.centerDialogAndShow(fViewContainer.getAppFrame(), editor);
            if(editor.saved() != null) {
                reloadProviders();
            }
		} catch(Exception ex) {
			UIExceptionMgr.userException(ex);
		}
    }

    /**
     * Creates a provider instance similar to the selected one.
     */
    private void handleCreateLikeProviderInstance() {
		try {
			ListProviderInstanceData selProv = (ListProviderInstanceData)fProvidersList.getSelectedValue();
			if(selProv == null) {
				return;
			}
			ListAgentData selAg = (ListAgentData)fAgentsList.getSelectedValue();
			if(selAg == null) {
				return;
			}
            ProviderInstance pid =
                new ProviderInstance(
                        selProv.providerInstanceData.getProviderName(),
                        MessageRepository.get(ProviderManagerComponent.NAME, Msg.TEXT_COPY_OF)
                            + " " + selProv.providerInstanceData.getInstanceName(),
                        selProv.providerInstanceData.getInstanceDescription(),
                        (ProviderConfiguration)selProv.providerInstanceData.getConfiguration().clone(),
                        selProv.providerInstanceData.isRemote(),
                        selProv.providerInstanceData.isOptional(),
                        selProv.providerInstanceData.isSelectedByDefault(),
                        selProv.providerInstanceData.inheritsLocationFromAgent(),
                        (ParserInstance)Utils.deepClone(selProv.providerInstanceData.getParserInstance()),
                        selProv.providerInstanceData.getEntityDescriptors().values(),
                        selProv.providerInstanceData.getAgentVersions(),
                        ConfigurationMgr.isDeveloperMode() ? AuthoredArtefact.SYSTEM : AuthoredArtefact.CUSTOMER);

			ProviderInstanceEditorDialog editor = new ProviderInstanceEditorDialog(
			       fViewContainer,
			       fProviderInstanceRepository,
				   fProviderRepository,
				   fParserRepository,
                   selAg.systemVersions,
                   new ResourceId(new HostId(""), new AgentId(selAg.installationId), null, null),
                   pid);
			editor.setModal(true);
			UIUtils.centerDialogAndShow(fViewContainer.getAppFrame(), editor);
            if(editor.saved() != null) {
                reloadProviders();
            }
		} catch(Exception ex) {
			UIExceptionMgr.userException(ex);
		}
    }

    /**
     * Adds a provider instance.
     */
    private void handleAddProviderInstance() {
		try {
			ListAgentData selAg = (ListAgentData)fAgentsList.getSelectedValue();
			if(selAg == null) {
				return;
			}
			ProviderInstanceEditorDialog editor = new ProviderInstanceEditorDialog(
			       fViewContainer,
			       fProviderInstanceRepository,
				   fProviderRepository,
				   fParserRepository,
                   selAg.systemVersions,
                   new ResourceId(new HostId(""), new AgentId(selAg.installationId), null, null),
                   null);
			editor.setModal(true);
			UIUtils.centerDialogAndShow(fViewContainer.getAppFrame(), editor);
            if(editor.saved() != null) {
                reloadProviders();
            }
		} catch(Exception ex) {
			UIExceptionMgr.userException(ex);
		}
    }

    /**
     * Adds a provider instance.
     * @return
     * @throws XMLException
     */
    private String getXMLDefinitionForSelectedProviderInstance() throws XMLException {
		ListProviderInstanceData selProv = (ListProviderInstanceData)fProvidersList.getSelectedValue();
		if(selProv == null) {
			return "";
		}
		return XMLUtils.toXMLBuffer(null, selProv.providerInstanceData, "rms", false).toString();
    }

    /**
     * Removes the provider instance.
     */
    private void handleRemoveProviderInstance() {
		try {
			ListProviderInstanceData selProv = (ListProviderInstanceData)fProvidersList.getSelectedValue();
			if(selProv == null) {
				return;
			}
			ListAgentData selAg = (ListAgentData)fAgentsList.getSelectedValue();
			if(selAg == null) {
				return;
			}
			ProviderInstanceMap map = fProviderInstanceRepository.getAgentProviderInstances(selAg.installationId);
			if(map == null) {
			    return;
			}
			// ask for confirmation
			if(!UIUtils.getBooleanOkCancelInput(this.fViewContainer.getAppFrame(),
					MessageRepository.get(ProviderManagerComponent.NAME,
							Msg.TITLE_CONFIRM_REMOVE_PROVIDER_INSTANCE),
					MessageRepository.get(ProviderManagerComponent.NAME,
							Msg.TEXT_CONFIRM_REMOVE_PROVIDER_INSTANCE,
							new String[] {selProv.translatedName}))) {
				return;
			}
            // remove this provider from all agent versions where it is defined
            map.remove(selProv.providerInstanceData);
			fProviderInstanceRepository.setAgentProviderInstances(selAg.installationId, map);
			fProviderInstanceRepository.save();
			// refresh list data
			reloadProviders();
		} catch(Exception ex) {
			UIExceptionMgr.userException(ex);
		}
    }

    /**
     * Updates the query group table model.
     */
    private void reloadProviders() {
    	// reload for currently selected agent
    	handleAgentSelected();
    }


	/**
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getJMenuItemEdit() {
		if(fMenuItemEdit == null) {
			fMenuItemEdit = UIFactoryMgr.createMenuItem(
					fActionEdit);
		}
		return fMenuItemEdit;
	}

	/**
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getJMenuItemCreateLike() {
		if(fMenuItemCreateLike == null) {
			fMenuItemCreateLike = UIFactoryMgr.createMenuItem(
					fActionCreateLike);
		}
		return fMenuItemCreateLike;
	}

	/**
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getJMenuItemAdd() {
		if(fMenuItemAdd == null) {
			fMenuItemAdd = UIFactoryMgr.createMenuItem(
					fActionAdd);
		}
		return fMenuItemAdd;
	}

	/**
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getJMenuItemRemove() {
		if(fMenuItemRemove == null) {
			fMenuItemRemove = UIFactoryMgr.createMenuItem(
					fActionRemove);
		}
		return fMenuItemRemove;
	}

	/**
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getJMenuItemViewXML() {
		if(fMenuItemViewXML == null) {
			fMenuItemViewXML = UIFactoryMgr.createMenuItem(
					fActionViewXML);
		}
		return fMenuItemViewXML;
	}

	/**
	 * @return javax.swing.JPopupMenu
	 */
	private JPopupMenu getJPopupMenu() {
		if(fPopupMenu == null) {
			fPopupMenu = UIFactoryMgr.createPopupMenu();
			fPopupMenu.add(getJMenuItemEdit());
			fPopupMenu.add(getJMenuItemRemove());
			fPopupMenu.add(getJMenuItemAdd());
			fPopupMenu.add(getJMenuItemCreateLike());
			fPopupMenu.add(getJMenuItemViewXML());
		}
		return fPopupMenu;
	}

	/**
	 * @param e
	 */
	private void handleShowPopupOnProvidersList(MouseEvent e) {
		try {
			getJMenuItemAdd().setVisible(true);
			getJMenuItemEdit().setVisible(false);
			getJMenuItemRemove().setVisible(false);
			getJMenuItemCreateLike().setVisible(false);
			getJMenuItemViewXML().setVisible(false);

			int sel = fProvidersList.getSelectedIndex();
			if(sel >= 0) {
				getJMenuItemEdit().setVisible(true);
				getJMenuItemRemove().setVisible(true);
                getJMenuItemCreateLike().setVisible(true);
                getJMenuItemViewXML().setVisible(true);
			}
			getJPopupMenu().show(e.getComponent(), e.getX(), e.getY());
		} catch(Exception ex) {
			UIExceptionMgr.userException(ex);
		}
	}
}
