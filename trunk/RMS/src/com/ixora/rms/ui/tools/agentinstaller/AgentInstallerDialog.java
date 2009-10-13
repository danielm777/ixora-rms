/*
 * Created on 03-Feb-2005
 */
package com.ixora.rms.ui.tools.agentinstaller;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.ixora.common.ComponentVersion;
import com.ixora.common.ConfigurationMgr;
import com.ixora.common.MessageRepository;
import com.ixora.common.Product;
import com.ixora.common.exception.AppRuntimeException;
import com.ixora.common.ui.AppDialog;
import com.ixora.common.ui.UIExceptionMgr;
import com.ixora.common.ui.UIFactoryMgr;
import com.ixora.common.ui.UIUtils;
import com.ixora.common.ui.actions.ActionClose;
import com.ixora.common.ui.jobs.UIWorkerJobDefault;
import com.ixora.common.ui.jobs.UIWorkerJobWithExternalProgress;
import com.ixora.common.ui.popup.PopupListener;
import com.ixora.common.utils.Utils;
import com.ixora.rms.agents.AgentLocation;
import com.ixora.rms.exception.RMSException;
import com.ixora.rms.repository.AgentInstallationData;
import com.ixora.rms.repository.AgentTemplate;
import com.ixora.rms.repository.VersionableAgentInstallationData;
import com.ixora.rms.services.AgentInstallerService;
import com.ixora.rms.services.AgentRepositoryService;
import com.ixora.rms.services.AgentTemplateRepositoryService;
import com.ixora.rms.ui.RMSViewContainer;
import com.ixora.rms.ui.tools.agentinstaller.messages.Msg;

/**
 * @author Daniel Moraru
 */
public final class AgentInstallerDialog extends AppDialog {
	private static final long serialVersionUID = -2869173095388027690L;
	private JList fAgentsList;
	private JPanel fAgentsPanel;
	private AgentRepositoryService fAgentRepository;
    private AgentTemplateRepositoryService fAgentTemplateRepository;
	private AgentInstallerService fAgentInstaller;
	private RMSViewContainer fViewContainer;
	private EventHandler fEventHandler;

	private ActionEditAgent fActionEdit;
	private ActionViewAgent fActionView;
	private ActionInstallAgent fActionInstall;
	private ActionExportAgent fActionExport;
	private ActionUninstallAgent fActionUninstall;

    private JPopupMenu fPopupMenu;
    private JMenuItem fMenuItemView;
    private JMenuItem fMenuItemEdit;
	private JMenuItem fMenuItemInstall;
	private JMenuItem fMenuItemExport;
	private JMenuItem fMenuItemUninstall;

	/**
	 * View agent action.
	 */
	@SuppressWarnings("serial")
	private final class ActionViewAgent extends AbstractAction {
		public ActionViewAgent() {
			super();
			UIUtils.setUsabilityDtls(MessageRepository.get(
					AgentInstallerComponent.NAME, Msg.ACTIONS_VIEW_AGENT), this);
		}
		/**
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent e) {
			handleEditAgent();
		}
	}

	/**
	 * Edit agent action.
	 */
	@SuppressWarnings("serial")
	private final class ActionEditAgent extends AbstractAction {
		public ActionEditAgent() {
			super();
			UIUtils.setUsabilityDtls(MessageRepository.get(
					AgentInstallerComponent.NAME, Msg.ACTIONS_EDIT_AGENT), this);
		}
		/**
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent e) {
			handleEditAgent();
		}
	}

	/**
	 * Install agent action.
	 */
	@SuppressWarnings("serial")
	private final class ActionInstallAgent extends AbstractAction {
		public ActionInstallAgent() {
			super();
			UIUtils.setUsabilityDtls(MessageRepository.get(
					AgentInstallerComponent.NAME, Msg.ACTIONS_INSTALL_AGENT), this);
		}
		/**
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent e) {
			handleInstallAgent();
		}
	}

	/**
	 * Install agent action.
	 */
	@SuppressWarnings("serial")
	private final class ActionExportAgent extends AbstractAction {
		public ActionExportAgent() {
			super();
			UIUtils.setUsabilityDtls(MessageRepository.get(
					AgentInstallerComponent.NAME, Msg.ACTIONS_EXPORT_AGENT), this);
		}
		/**
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent e) {
			handleExportAgent();
		}
	}

	/**
	 * Uninstall agent action.
	 */
	@SuppressWarnings("serial")
	private final class ActionUninstallAgent extends AbstractAction {
		public ActionUninstallAgent() {
			super();
			UIUtils.setUsabilityDtls(MessageRepository.get(
					AgentInstallerComponent.NAME, Msg.ACTIONS_UNINSTALL_AGENT), this);
		}
		/**
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent e) {
			handleUninstallAgent();
		}
	}

	private static final class ListAgentData implements Comparable<ListAgentData> {
		String translatedName;
		AgentInstallationData installationData;

		ListAgentData(AgentInstallationData aid) {
			installationData = aid;
			translatedName = MessageRepository.get(installationData.getMessageCatalog(),
					aid.getAgentName());
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

	private final class EventHandler extends PopupListener implements ListSelectionListener {
		/**
		 * @see com.ixora.common.ui.popup.PopupListener#showPopup(java.awt.event.MouseEvent)
		 */
		protected void showPopup(MouseEvent e) {
			if(e.getSource() == fAgentsList) {
				handleShowPopupOnAgentsList(e);
				return;
			}
		}

		/**
		 * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
		 */
		public void valueChanged(ListSelectionEvent e) {
			if(e.getSource() == fAgentsList.getSelectionModel()) {
				handleAgentSelected();
				return;
			}
		}
	}

	/**
	 * Constructor.
	 * @param parent
	 * @param pis
	 */
	public AgentInstallerDialog(
           RMSViewContainer viewContainer,
           AgentRepositoryService ars,
           AgentTemplateRepositoryService atrs,
		   AgentInstallerService ai) {
		super(viewContainer.getAppFrame(), VERTICAL);
		setTitle(MessageRepository.get(AgentInstallerComponent.NAME, Msg.TITLE_AGENT_INSTALLER));
		fEventHandler = new EventHandler();
		fViewContainer = viewContainer;
		fAgentRepository = ars;
        fAgentTemplateRepository = atrs;
		fAgentInstaller = ai;

		fAgentsList = UIFactoryMgr.createList();
		loadAgents();

		fAgentsPanel = new JPanel(new BorderLayout());
		fAgentsPanel.setBorder(UIFactoryMgr.createTitledBorder(MessageRepository.get(
				AgentInstallerComponent.NAME, Msg.TEXT_AGENTS)));
		JScrollPane sp = UIFactoryMgr.createScrollPane();
		sp.setViewportView(fAgentsList);
		sp.setPreferredSize(new Dimension(300, 300));
		fAgentsPanel.add(sp, BorderLayout.CENTER);

   		fActionInstall = new ActionInstallAgent();
   		fActionExport = new ActionExportAgent();
   		fActionEdit = new ActionEditAgent();
   		fActionView = new ActionViewAgent();
   		fActionUninstall= new ActionUninstallAgent();

		this.fActionUninstall.setEnabled(false);
		this.fActionEdit.setEnabled(false);
		this.fActionView.setEnabled(false);

   		fAgentsList.addMouseListener(fEventHandler);
		fAgentsList.getSelectionModel().addListSelectionListener(fEventHandler);
   		buildContentPane();
   		pack();
	}

	/**
	 * @see com.ixora.common.ui.AppDialog#getDisplayPanels()
	 */
	protected Component[] getDisplayPanels() {
		return new Component[] {fAgentsPanel};
	}

	/**
	 * @see com.ixora.common.ui.AppDialog#getButtons()
	 */
	@SuppressWarnings("serial")
	protected JButton[] getButtons() {
		return new JButton[] {
			UIFactoryMgr.createButton(fActionInstall),
			UIFactoryMgr.createButton(fActionView),
			UIFactoryMgr.createButton(fActionEdit),
			UIFactoryMgr.createButton(fActionExport),
			UIFactoryMgr.createButton(fActionUninstall),
			UIFactoryMgr.createButton(new ActionClose() {
				public void actionPerformed(ActionEvent e) {
					dispose();
				}
			})};
	}

    /**
     * Edits agent.
     */
    private void handleEditAgent() {
		try {
			ListAgentData selAg = (ListAgentData)fAgentsList.getSelectedValue();
			if(selAg == null) {
				return;
			}
			AgentInstallationDataDialog editor = new AgentInstallationDataDialog(
				       fViewContainer, selAg.installationData, true,
					   !(selAg.installationData.isCustomAgent() || ConfigurationMgr.isDeveloperMode()));
			editor.setModal(true);
			UIUtils.centerDialogAndShow(fViewContainer.getAppFrame(), editor);
			// only custom agents can be edited
			if(selAg.installationData.isCustomAgent() || ConfigurationMgr.isDeveloperMode()) {
				final AgentInstallationData aid = editor.getResult();
				if(aid != null) {
					fViewContainer.getAppWorker().runJob(new UIWorkerJobDefault(this, Cursor.WAIT_CURSOR,
							MessageRepository.get(AgentInstallerComponent.NAME, Msg.TEXT_UPDATING_AGENT,
									new String[] {
									MessageRepository.get(aid.getAgentInstallationId(), aid.getAgentName())})) {
						public void work() throws Throwable {
							fAgentInstaller.update(aid);
						}
						public void finished(Throwable ex) {
							if(ex == null) {
								// refresh list data
								loadAgents();
							}
						}
					});
				}
			}
		} catch(Exception ex) {
			UIExceptionMgr.userException(ex);
		}
    }

    /**
     * Installs agent.
     */
    private void handleInstallAgent() {
		try {
			// ask user for installation type
			AgentInstallationTypeSelectorDialog dlg = new AgentInstallationTypeSelectorDialog(this);
			UIUtils.centerDialogAndShow(this, dlg);
			int type = dlg.getSelectedInstallationType();
			if(type == AgentInstallationTypeSelectorDialog.CUSTOM_AGENT) {
	            // show agent template selector
			    Map<String, AgentTemplate> templates = fAgentTemplateRepository.getAgentTemplates();
	            AgentInstallationData init = null;
	            if(!Utils.isEmptyMap(templates)) {
	                AgentTemplateSelectorDialog templateSelector = new AgentTemplateSelectorDialog(
	                        this, templates.values().toArray(
	                                new AgentTemplate[templates.size()]));
	                UIUtils.centerDialogAndShow(this, templateSelector);
	                AgentTemplate selectedTemplate = templateSelector.getSelectedTemplate();
	                if(selectedTemplate != null) {
	                    VersionableAgentInstallationData vad =
	                    	new VersionableAgentInstallationData(
	                    			selectedTemplate.getAgentConfigurationPanelClass(),
	                    			new AgentLocation[]{AgentLocation.LOCAL, AgentLocation.REMOTE},
	                    			null,
	                    			0,
	                    			null,
	                    			null,
	                    			null);
	                    init = new AgentInstallationData(selectedTemplate.getAgentClass(), true, new ComponentVersion(1,0,0), null, null, null, null, null, vad, null);
	                }
	            }
				AgentInstallationDataDialog editor = new AgentInstallationDataDialog(
				       fViewContainer, init, false, false);
				editor.setModal(true);
				UIUtils.centerDialogAndShow(fViewContainer.getAppFrame(), editor);
				final AgentInstallationData aid = editor.getResult();
				if(aid != null) {
					fViewContainer.getAppWorker().runJob(new UIWorkerJobDefault(this, Cursor.WAIT_CURSOR,
							MessageRepository.get(AgentInstallerComponent.NAME, Msg.TEXT_INSTALLING_AGENT,
									new String[] {aid.getAgentInstallationId()})) {
						public void work() throws Throwable {
							fAgentInstaller.install(aid);
						}
						public void finished(Throwable ex) {
							if(ex == null) {
								// refresh list data
								loadAgents();
								askForAppRestart();
							}
						}
					});
				}
			} else if(type == AgentInstallationTypeSelectorDialog.PACKAGED_AGENT) {
				// show file dialog to select the agent package
				JFileChooser fc = new JFileChooser();
				fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
				fc.setAcceptAllFileFilterUsed(false);
				fc.setFileFilter(new AgentPackageFilter());
				fc.setFileView(new AgentPackageFileView());
				int returnVal = fc.showOpenDialog(this);
				if(returnVal == JFileChooser.APPROVE_OPTION) {
				    final File f = fc.getSelectedFile();
				    if(f != null && f.exists() && f.isFile()) {
				    	fViewContainer.getAppWorker().runJob(
				    			new UIWorkerJobWithExternalProgress(
				    					fViewContainer.getAppFrame(),
				    					Cursor.WAIT_CURSOR,
				    					MessageRepository.get(
				    							AgentInstallerComponent.NAME,
				    							Msg.TEXT_INSTALLING_AGENT,
				    							new String[]{""})) {
									public void work() throws Throwable {
								    	fAgentInstaller.installFromPackage(f, true, this.fProgress);
									}
									public void finished(Throwable ex) throws Throwable {
										if(ex == null) {
											// refresh list data
											loadAgents();
											askForAppRestart();
										}
									}
				    	});
				    }
				}
			} else if(type == AgentInstallationTypeSelectorDialog.OTHER_PRODUCT_INSTANCE_AGENT) {
				// show file dialog to select another product instance
				JFileChooser fc = new JFileChooser();
				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int returnVal = fc.showOpenDialog(this);
				if(returnVal == JFileChooser.APPROVE_OPTION) {
				    final File f = fc.getSelectedFile();
				    Map<String, AgentInstallationData> agents;
				    try {
				    	agents = fAgentRepository.getInstalledAgents(f);
				    } catch(FileNotFoundException e) {
				    	// TODO localize
				    	throw new RMSException("The selected folder does not seem to be a valid "
				    			+ Product.getProductInfo().getName()
				    			+ " installation");
				    }
				    // allow user to select agent to import
				    if(agents == null) {
				    	// TODO localize
				    	throw new RMSException("No agents were found in folder " + f.getAbsolutePath());
				    }
				    // filter out agents which are already installed
				    List<AgentInstallationData> newAgents = new LinkedList<AgentInstallationData>();
				    Map<String, AgentInstallationData> existingAgents = fAgentRepository.getInstalledAgents();
				    for(AgentInstallationData aid : agents.values()) {
				    	if(existingAgents.get(aid.getAgentInstallationId()) == null) {
				    		newAgents.add(aid);
				    	}
				    }
				    if(newAgents.size() == 0) {
				    	// TODO localize
				    	throw new RMSException("There are no custom agents defined in "
				    			+ Product.getProductInfo().getName()
								+ " instance at "
								+ f.getAbsolutePath() );
				    }
				    AgentSelectorDialog selector = new AgentSelectorDialog(
				    		this,
				    		f,
				    		newAgents);
				    UIUtils.centerDialogAndShow(this, selector);
				    final AgentInstallationData agentToImport = selector.getResult();
				    if(agentToImport == null) {
				    	return;
				    }

			    	fViewContainer.getAppWorker().runJob(
			    			new UIWorkerJobWithExternalProgress(
			    					fViewContainer.getAppFrame(),
			    					Cursor.WAIT_CURSOR,
			    					MessageRepository.get(
			    							AgentInstallerComponent.NAME,
			    							Msg.TEXT_INSTALLING_AGENT,
			    							new String[]{""})) {
								public void work() throws Throwable {
								    fAgentInstaller.installFromFilesystem(f, fProgress, agentToImport.getAgentInstallationId());
								}
								public void finished(Throwable ex) throws Throwable {
									if(ex != null) {
										UIExceptionMgr.userException(
												fViewContainer.getAppFrame(), ex);
									} else {
										// refresh list data
										loadAgents();
										askForAppRestart();
									}
								}
			    	});
				}
			}
		} catch(Exception ex) {
			UIExceptionMgr.userException(ex);
		}
    }

   /**
     * Exports agent.
     */
    private void handleExportAgent() {
		try {
			// get the agent to export
			final ListAgentData selAg = (ListAgentData)fAgentsList.getSelectedValue();
			if(selAg == null) {
				return;
			}

			// ask user for the export folder
			JFileChooser fc = new JFileChooser();
			fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			fc.setMultiSelectionEnabled(false);
			// TODO localize
			fc.setDialogTitle("Select Export Folder");
			fc.setApproveButtonText("Select");
			int returnVal = fc.showOpenDialog(this);
			if(returnVal == JFileChooser.APPROVE_OPTION) {
				final File folder = fc.getSelectedFile();
				if(folder == null || !folder.isDirectory()) {
					throw new AppRuntimeException("Please select a folder");
				}
				fViewContainer.getAppWorker().runJob(new UIWorkerJobWithExternalProgress(
						this, Cursor.WAIT_CURSOR,
						MessageRepository.get(AgentInstallerComponent.NAME,
								Msg.TEXT_EXPORTING_AGENT,
								new String[] {selAg.translatedName})) {
					public void work() throws Throwable {
						this.fResult = fAgentInstaller.export(
								selAg.installationData.getAgentInstallationId(),
								folder, fProgress);
					}
					public void finished(Throwable ex) {
						if(ex == null) {
							if(fResult != null) {
								// TODO localize
								UIUtils.showInfo(AgentInstallerDialog.this,
										"Agent Exported",
										"Agent "
										+ selAg.translatedName
										+ " was exported to file \n"
										+ ((File)fResult).getAbsolutePath());
							}
						}
					}
				});
			}
		} catch(Exception ex) {
			UIExceptionMgr.userException(ex);
		}
    }

    /**
	 * Asks the user to restart the application.
	 */
	private void askForAppRestart() {
		UIUtils.showWarning(this,
				MessageRepository.get(AgentInstallerComponent.NAME, Msg.TITLE_APP_RESTART_WARNING),
				MessageRepository.get(AgentInstallerComponent.NAME, Msg.TEXT_APP_RESTART_WARNING));

	}

	/**
     * Uninstall agent.
     */
    private void handleUninstallAgent() {
		try {
			final ListAgentData selAg = (ListAgentData)fAgentsList.getSelectedValue();
			if(selAg == null || !selAg.installationData.isCustomAgent()) {
				return;
			}
			if(!UIUtils.getBooleanOkCancelInput(this,
					MessageRepository.get(AgentInstallerComponent.NAME, Msg.TITILE_UNINSTALL_AGENT),
					MessageRepository.get(AgentInstallerComponent.NAME, Msg.TEXT_AGENT_CONFIRM_UNINSTALL_AGENT, new String[] {
							selAg.installationData.getAgentInstallationId()}))) {
				return;
			}
			fViewContainer.getAppWorker().runJob(new UIWorkerJobDefault(this, Cursor.WAIT_CURSOR,
					MessageRepository.get(AgentInstallerComponent.NAME, Msg.TEXT_UNINSTALLING_AGENT,
							new String[] {selAg.installationData.getAgentInstallationId()})) {
				public void work() throws Throwable {
					fAgentInstaller.uninstall(selAg.installationData.getAgentInstallationId());
				}
				public void finished(Throwable ex) {
					if(ex == null) {
						// refresh list data
						loadAgents();
					}
				}
			});
		} catch(Exception ex) {
			UIExceptionMgr.userException(ex);
		}
    }

    /**
     * Updates the agent list.
     */
    private void loadAgents() {
		AgentInstallationData[] aids = fAgentRepository.getInstalledAgents().values().toArray(
				new AgentInstallationData[0]);
		ListAgentData[] lstAgents = new ListAgentData[aids.length];
		for(int i = 0; i < aids.length; ++i) {
			lstAgents[i] = new ListAgentData(aids[i]);
		}
		// present them in alphabetical order
		Arrays.sort(lstAgents);
		fAgentsList.setListData(lstAgents);
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
	private JMenuItem getJMenuItemView() {
		if(fMenuItemView == null) {
			fMenuItemView = UIFactoryMgr.createMenuItem(
					fActionView);
		}
		return fMenuItemView;
	}

	/**
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getJMenuItemInstall() {
		if(fMenuItemInstall == null) {
			fMenuItemInstall = UIFactoryMgr.createMenuItem(
					fActionInstall);
		}
		return fMenuItemInstall;
	}

	/**
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getJMenuItemExport() {
		if(fMenuItemExport == null) {
			fMenuItemExport = UIFactoryMgr.createMenuItem(
					fActionExport);
		}
		return fMenuItemExport;
	}

	/**
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getJMenuItemUninstall() {
		if(fMenuItemUninstall == null) {
			fMenuItemUninstall = UIFactoryMgr.createMenuItem(
					fActionUninstall);
		}
		return fMenuItemUninstall;
	}

	/**
	 * @return javax.swing.JPopupMenu
	 */
	private JPopupMenu getJPopupMenu() {
		if(fPopupMenu == null) {
			fPopupMenu = UIFactoryMgr.createPopupMenu();
			fPopupMenu.add(getJMenuItemEdit());
			fPopupMenu.add(getJMenuItemView());
			fPopupMenu.add(getJMenuItemUninstall());
			fPopupMenu.add(getJMenuItemInstall());
			fPopupMenu.add(getJMenuItemExport());
		}
		return fPopupMenu;
	}

	/**
	 * @param e
	 */
	private void handleShowPopupOnAgentsList(MouseEvent e) {
		try {
			getJMenuItemInstall().setVisible(true);
			getJMenuItemExport().setVisible(true);
			getJMenuItemEdit().setVisible(false);
			getJMenuItemView().setVisible(false);
			getJMenuItemUninstall().setVisible(false);

			ListAgentData selAg = (ListAgentData)fAgentsList.getSelectedValue();
			if(selAg != null) {
				getJMenuItemEdit().setVisible(selAg.installationData.isCustomAgent());
				getJMenuItemView().setVisible(!selAg.installationData.isCustomAgent());
				getJMenuItemUninstall().setVisible(selAg.installationData.isCustomAgent());
			}
			getJPopupMenu().show(e.getComponent(), e.getX(), e.getY());
		} catch(Exception ex) {
			UIExceptionMgr.userException(ex);
		}
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
			if(ad.installationData.isCustomAgent() || ConfigurationMgr.isDeveloperMode()) {
				this.fActionUninstall.setEnabled(true);
				this.fActionEdit.setEnabled(true);
			} else {
				this.fActionUninstall.setEnabled(false);
				this.fActionEdit.setEnabled(false);
			}
			this.fActionView.setEnabled(true);
		} catch(Exception e) {
			UIExceptionMgr.userException(e);
		}
	}
}
