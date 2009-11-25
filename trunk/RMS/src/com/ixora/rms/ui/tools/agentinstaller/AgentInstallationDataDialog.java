/*
 * Created on 08-Feb-2005
 */
package com.ixora.rms.ui.tools.agentinstaller;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.ixora.common.MessageRepository;
import com.ixora.common.typedproperties.TypedProperties;
import com.ixora.common.typedproperties.ui.list.TypedPropertiesListEditor;
import com.ixora.common.ui.AppDialog;
import com.ixora.common.ui.LayoutUtils;
import com.ixora.common.ui.UIExceptionMgr;
import com.ixora.common.ui.UIFactoryMgr;
import com.ixora.common.ui.UIUtils;
import com.ixora.common.ui.actions.ActionClose;
import com.ixora.common.ui.actions.ActionDelete;
import com.ixora.common.ui.actions.ActionEdit;
import com.ixora.common.ui.actions.ActionNew;
import com.ixora.common.ui.actions.ActionOk;
import com.ixora.common.ui.actions.ActionView;
import com.ixora.common.ui.exception.InvalidFormData;
import com.ixora.common.ui.forms.FormPanel;
import com.ixora.common.utils.Utils;
import com.ixora.rms.repository.AgentInstallationData;
import com.ixora.rms.repository.VersionableAgentInstallationData;
import com.ixora.rms.repository.VersionableAgentInstallationDataMap;
import com.ixora.rms.ui.RMSViewContainer;
import com.ixora.rms.ui.tools.agentinstaller.exception.VersionDataMissing;
import com.ixora.rms.ui.tools.agentinstaller.messages.Msg;

/**
 * @author Daniel Moraru
 */
public final class AgentInstallationDataDialog extends AppDialog {
	private static final long serialVersionUID = -5421573883756354569L;
	private static final String LABEL_AGENT_NAME = MessageRepository.get(AgentInstallerComponent.NAME, Msg.LABEL_AGENT_NAME);
	private static final String LABEL_AGENT_DESCRIPTION = MessageRepository.get(AgentInstallerComponent.NAME, Msg.LABEL_AGENT_DESCRIPTION);
	private static final String LABEL_AGENT_CATEGORY = MessageRepository.get(AgentInstallerComponent.NAME, Msg.LABEL_AGENT_CATEGORY);
	private static final String LABEL_AGENT_TARGET_VERSIONS = MessageRepository.get(AgentInstallerComponent.NAME, Msg.LABEL_AGENT_TARGET_VERSIONS);
	private static final String LABEL_AGENT_MSG_CATALOG = MessageRepository.get(AgentInstallerComponent.NAME, Msg.LABEL_AGENT_MSG_CATALOG);

	private JPanel fPanel;
	private FormPanel fForm;

	private JTextField fTextFieldName;
	private JTextField fTextFieldDescription;
	
	private JTextField fTextFieldCategory;
	private JTextField fTextFieldMessageCatalog;
	private JTextField fTextFieldHelpIdJava;
	private JTextField fTextFieldHelpIdWeb;
	private TypedPropertiesListEditor fEditorTargetVersions;
	private TypedProperties fPrototypeTargetVersion;
	private JList fVersionDataList;
	private AgentInstallationData fResult;
	private JPanel fPanelVersionData;
	private RMSViewContainer fViewContainer;
	private boolean fReadOnly;
	private JButton fButtonNewVersionData;
	private JButton fButtonEditVersionData;
	private JButton fButtonDeleteVersionData;
	private JButton fButtonViewVersionData;
	private VersionableAgentInstallationDataMap fVersionData;
	

	private final class EventHandler implements ListSelectionListener {
		/**
		 * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
		 */
		public void valueChanged(ListSelectionEvent e) {
			if(!e.getValueIsAdjusting() && e.getSource() == fVersionDataList.getSelectionModel()) {
				handleVersionDataSelected(e);
			}
		}
	}

	private static final class VersionDataListEntry {
		VersionableAgentInstallationData fVersionData;
		private String toString;

		VersionDataListEntry(VersionableAgentInstallationData vad) {
			fVersionData = vad;
			Set<String> av = vad.getAgentVersions();
	        StringBuffer buff = new StringBuffer();
	        if(!Utils.isEmptyCollection(av)) {
	            for(Iterator<String> iter = av.iterator(); iter.hasNext();) {
	                buff.append(iter.next());
	                if(iter.hasNext()) {
	                    buff.append(", ");
	                }
	            }
	        } else {
	        	String allText = "All"; // TODO localize
	        	buff.append(allText);
	        }
	        String fragment = "Data for version(s): "; // TODO localize
	        toString = "<html>" + fragment + " <b>" + buff.toString() + "</b><html>";
		}

		public String toString() {
			return toString;
		}
	}

	/**
	 * Constructor.
	 * @param vc
	 * @param aid
	 * @param editMode
	 * @param readOnly
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	@SuppressWarnings("serial")
	public AgentInstallationDataDialog(RMSViewContainer vc, AgentInstallationData aid,
			boolean editMode, boolean readOnly) throws IOException, ClassNotFoundException {
		super(vc.getAppFrame(), VERTICAL);
		setTitle(MessageRepository.get(AgentInstallerComponent.NAME,
				Msg.TITLE_AGENT_INSTALLATION_DETAILS));
		fViewContainer = vc;
		fReadOnly = readOnly;
		fPanel = new JPanel(new BorderLayout());
		fForm = new FormPanel(FormPanel.VERTICAL1, SwingConstants.TOP) {
			private static final long serialVersionUID = 4198380579494172527L;

			public void setEnabled(boolean e) {
				super.setEnabled(e);
				fEditorTargetVersions.setEnabled(e);
			}
		};

		fTextFieldName = UIFactoryMgr.createTextField();
		fTextFieldDescription = UIFactoryMgr.createTextField();
		fTextFieldCategory = UIFactoryMgr.createTextField();
		fTextFieldMessageCatalog = UIFactoryMgr.createTextField();
		fTextFieldHelpIdJava = UIFactoryMgr.createTextField();
		fTextFieldHelpIdWeb = UIFactoryMgr.createTextField();
		fPrototypeTargetVersion = createFromVersion(null);

		fEditorTargetVersions = new TypedPropertiesListEditor(fPrototypeTargetVersion,
				AgentInstallerComponent.NAME, null, TypedPropertiesListEditor.BUTTON_ALL);
		fEditorTargetVersions.setPreferredSize(new Dimension(300, 150));
		fEditorTargetVersions.setMaximumSize(new Dimension(300, 150));

		fVersionDataList = UIFactoryMgr.createList();
		JScrollPane spVersionDataList = UIFactoryMgr.createScrollPane();
		spVersionDataList.setViewportView(fVersionDataList);

		fButtonNewVersionData = UIFactoryMgr.createButton(new ActionNew() {
					public void actionPerformed(ActionEvent e) {
						handleNewVersionData();
					}
				});
		fButtonEditVersionData = UIFactoryMgr.createButton(new ActionEdit() {
					public void actionPerformed(ActionEvent e) {
						handleEditVersionData();
					}
				});
		fButtonDeleteVersionData = UIFactoryMgr.createButton(new ActionDelete() {
					public void actionPerformed(ActionEvent e) {
						handleDeleteVersionData();
					}
				});
		fButtonViewVersionData = UIFactoryMgr.createButton(new ActionView() {
					public void actionPerformed(ActionEvent e) {
						handleViewVersionData();
					}
				});
		fButtonEditVersionData.setEnabled(false);
		fButtonDeleteVersionData.setEnabled(false);
		fButtonViewVersionData.setEnabled(false);
		fPanelVersionData = new JPanel(new BorderLayout()) {
			/**
			 * @see javax.swing.JComponent#setEnabled(boolean)
			 */
			public void setEnabled(boolean enabled) {
				super.setEnabled(enabled);
				fButtonNewVersionData.setEnabled(enabled);
				fButtonDeleteVersionData.setEnabled(enabled);
				fButtonEditVersionData.setEnabled(enabled);
			}
		};
		fPanelVersionData.add(spVersionDataList, BorderLayout.CENTER);
		LayoutUtils.alignVerticallyInPanelWithBorderLayout(
				fPanelVersionData,
				new Component[]{
					fButtonNewVersionData,
					fButtonEditVersionData,
					fButtonDeleteVersionData,
					fButtonViewVersionData
				},
				BorderLayout.EAST);

		// initialize with info from AgentInstallationData
		if(aid != null) {
			fVersionData = aid.getVersionData();
			fTextFieldName.setText(aid.getAgentName());
			fTextFieldDescription.setText(aid.getAgentDescription());
			fTextFieldCategory.setText(aid.getCategory());
			fTextFieldMessageCatalog.setText(aid.getMessageCatalog());
			fTextFieldHelpIdJava.setText(aid.getJavaHelp());
			fTextFieldHelpIdWeb.setText(aid.getWebHelp());
            // monitored system versions
            String[] versions = aid.getSystemUnderObservationVersions();
            if(versions != null) {
            	List<TypedProperties> lstTargetVersions = new ArrayList<TypedProperties>(versions.length);
                for(String v : versions) {
                    TypedProperties tp = createFromVersion(v);
                    lstTargetVersions.add(tp);
                }
                fEditorTargetVersions.getModel().setProperties(lstTargetVersions);
            }

            // add items to version data list
            fVersionData = (VersionableAgentInstallationDataMap)Utils.deepClone(aid.getVersionData());
            updateVersionDataList();
		}
		if(fVersionData == null) {
			fVersionData = new VersionableAgentInstallationDataMap();
		}
		fForm.addPairs(
				new String[] {
						LABEL_AGENT_NAME,
						LABEL_AGENT_DESCRIPTION,
						LABEL_AGENT_CATEGORY,
                        LABEL_AGENT_MSG_CATALOG,
                        "Java Help ID", // TODO localize
                        "Web Help URL", // TODO localize
                        LABEL_AGENT_TARGET_VERSIONS,
                        "Version Data" // TODO localize
				},
				new Component[] {
						fTextFieldName,
						fTextFieldDescription,
						fTextFieldCategory,
                        fTextFieldMessageCatalog,
                        fTextFieldHelpIdJava,
                        fTextFieldHelpIdWeb,
                        fEditorTargetVersions,
                        fPanelVersionData
				});
		fPanel.add(fForm, BorderLayout.NORTH);

		if(editMode) {
			fForm.setEnabled(LABEL_AGENT_NAME, false);
		}
		if(readOnly) {
			fForm.setEnabled(false);
		}
		fVersionDataList.getSelectionModel().addListSelectionListener(new EventHandler());
		buildContentPane();
		pack();
	}

	/**
	 *
	 */
	private void updateVersionDataList() {
		if(fVersionData == null) {
			return;
		}
        Collection<VersionableAgentInstallationData> coll = fVersionData.getAll();
        VersionDataListEntry[] les = new VersionDataListEntry[coll.size()];
        int i = 0;
        for(VersionableAgentInstallationData vad : coll) {
        	les[i] = new VersionDataListEntry(vad);
        	++i;
		}
        fVersionDataList.setListData(les);

		fButtonEditVersionData.setEnabled(false);
		fButtonDeleteVersionData.setEnabled(false);
		fButtonViewVersionData.setEnabled(false);
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
	@SuppressWarnings("serial")
	protected JButton[] getButtons() {
		return new JButton[] {
				new JButton(
					new ActionOk() {
						public void actionPerformed(ActionEvent e) {
							handleOk();
						}
					}
				),
				new JButton(
					new ActionClose() {
						public void actionPerformed(ActionEvent e) {
							dispose();
						}
					}
				)};
	}

	/**
	 *
	 */
	private void handleOk() {
		try {
			if(fReadOnly) {
				fResult = null;
				dispose();
				return;
			}
			// build the result
			String name = getAgentId();
			
			String description = fTextFieldDescription.getText();
			if(Utils.isEmptyString(description)) {
				description = null;
			}
			String category = fTextFieldCategory.getText();
			if(Utils.isEmptyString(category)) {
				category = "miscellaneous";
			}
			fEditorTargetVersions.stopEditing();
			String[] versions = getTargetVersionsFromTableModel();
			String msgCatalog = fTextFieldMessageCatalog.getText();
			if(Utils.isEmptyString(msgCatalog)) {
				msgCatalog = null;
			}
			String helpIdJava = fTextFieldHelpIdJava.getText();
			if(Utils.isEmptyString(helpIdJava)) {
				helpIdJava = null;
			}
			String helpIdWeb = fTextFieldHelpIdWeb.getText();
			if(Utils.isEmptyString(helpIdWeb)) {
				helpIdWeb = null;
			}
	        // check if we have at least one version data
	        Collection<VersionableAgentInstallationData> coll = fVersionData.getAll();
	        if(Utils.isEmptyCollection(coll)) {
	        	throw new VersionDataMissing();
	        }
			fResult = new AgentInstallationData(
					true,
					null,
					name,
					description, versions, msgCatalog, helpIdJava, 
					helpIdWeb, fVersionData, category);
			dispose();
		} catch(Exception e) {
			UIExceptionMgr.userException(e);
		}
	}

	/**
	 * @param jar can be null
	 * @return
	 */
	private static TypedProperties createFromVersion(String version) {
		TypedProperties prop = new TypedProperties();
		prop.setProperty(Msg.COLUMN_TARGET_VERSION, TypedProperties.TYPE_STRING, true, false);
		if(version != null) {
			prop.setString(Msg.COLUMN_TARGET_VERSION, version);
		}
		return prop;
	}

	/**
	 * @param prop
	 * @return
	 */
	private static String createVersionFromTypedProperties(TypedProperties prop) {
		return prop.getString(Msg.COLUMN_TARGET_VERSION);
	}

	/**
	 * @return
	 */
	private String[] getTargetVersionsFromTableModel() {
		List<TypedProperties> lst = fEditorTargetVersions.getModel().getAllProperties();
		if(Utils.isEmptyCollection(lst)) {
			return null;
		}
		List<String> ret = new LinkedList<String>();
		int i = 0;
		for(Iterator<TypedProperties> iter = lst.iterator(); iter.hasNext(); ++i) {
			String s = createVersionFromTypedProperties(iter.next());
			if(!Utils.isEmptyString(s)) {
				ret.add(s);
			}
		}
		if(Utils.isEmptyCollection(ret)) {
			return null;
		}
		return ret.toArray(new String[ret.size()]);
	}

	/**
	 * @return the edited agent installation data
	 */
	public AgentInstallationData getResult() {
		return fResult;
	}

	/**
	 *
	 */
	private void handleNewVersionData() {
		try {
			if(fReadOnly) {
				return;
			}
			AgentInstallationVersionDataDialog dlg = new AgentInstallationVersionDataDialog(getAgentId(),
					fViewContainer, getTargetVersionsFromTableModel(), null, fReadOnly, fVersionData);
			UIUtils.centerDialogAndShow(this, dlg);
			if(dlg.getResult() != null) {
				updateVersionDataList();
			}
		} catch(Exception e) {
			UIExceptionMgr.userException(e);
		}
	}

	/**
	 *
	 */
	private void handleEditVersionData() {
		try {
			if(fReadOnly) {
				return;
			}
			VersionDataListEntry e = (VersionDataListEntry)fVersionDataList.getSelectedValue();
			if(e == null) {
				return;
			}
			AgentInstallationVersionDataDialog dlg = new AgentInstallationVersionDataDialog(getAgentId(),
					fViewContainer, getTargetVersionsFromTableModel(), e.fVersionData, fReadOnly, fVersionData);
			UIUtils.centerDialogAndShow(this, dlg);
			if(dlg.getResult() != null) {
				updateVersionDataList();
			}
		} catch(Exception e) {
			UIExceptionMgr.userException(e);
		}
	}
	
	private String getAgentId() throws InvalidFormData {
		String name = fTextFieldName.getText();
		if(Utils.isEmptyString(name)) {
			throw new InvalidFormData(LABEL_AGENT_NAME);
		}
		return name;
	}

	/**
	 *
	 */
	private void handleDeleteVersionData() {
		try {
			if(fReadOnly) {
				return;
			}
			boolean ok = UIUtils.getBooleanYesNoInput(this,
					"Delete version data", // TODO localize
					"Are you sure you want to delete version data?");
			if(!ok) {
				return;
			}
			VersionDataListEntry e = (VersionDataListEntry)fVersionDataList.getSelectedValue();
			if(e == null) {
				return;
			}
			fVersionData.remove(e.fVersionData);
			updateVersionDataList();
		} catch(Exception e) {
			UIExceptionMgr.userException(e);
		}
	}

	/**
	 *
	 */
	private void handleViewVersionData() {
		try {
			VersionDataListEntry e = (VersionDataListEntry)fVersionDataList.getSelectedValue();
			if(e == null) {
				return;
			}
			AgentInstallationVersionDataDialog dlg = new AgentInstallationVersionDataDialog(getAgentId(),
					fViewContainer, getTargetVersionsFromTableModel(), e.fVersionData, true, fVersionData);
			UIUtils.centerDialogAndShow(this, dlg);
			
		} catch(Exception e) {
			UIExceptionMgr.userException(e);
		}
	}

    /**
	 * @param ev
	 */
	public void handleVersionDataSelected(ListSelectionEvent ev) {
		try {
			fButtonEditVersionData.setEnabled(!fReadOnly);
			fButtonDeleteVersionData.setEnabled(!fReadOnly);
			fButtonViewVersionData.setEnabled(true);
		} catch(Exception e) {
			UIExceptionMgr.userException(e);
		}
	}
}
