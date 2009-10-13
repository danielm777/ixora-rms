/*
 * Created on 31-Dec-2004
 */
package com.ixora.rms.ui.tools.providermanager;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.ixora.rms.ResourceId;
import com.ixora.common.ConfigurationMgr;
import com.ixora.common.MessageRepository;
import com.ixora.common.typedproperties.exception.InvalidPropertyValue;
import com.ixora.common.typedproperties.exception.VetoException;
import com.ixora.common.ui.AppDialog;
import com.ixora.common.ui.UIExceptionMgr;
import com.ixora.common.ui.UIFactoryMgr;
import com.ixora.common.ui.actions.ActionCancel;
import com.ixora.common.ui.actions.ActionOk;
import com.ixora.common.ui.exception.InvalidFormData;
import com.ixora.common.ui.forms.FormPanel;
import com.ixora.common.ui.jobs.UIWorkerJobDefault;
import com.ixora.common.utils.Utils;
import com.ixora.common.xml.XMLUtils;
import com.ixora.rms.EntityDescriptor;
import com.ixora.rms.EntityId;
import com.ixora.rms.exception.RMSException;
import com.ixora.rms.providers.parsers.exception.InvalidParsingRules;
import com.ixora.rms.repository.AuthoredArtefact;
import com.ixora.rms.repository.AuthoredArtefactHelper;
import com.ixora.rms.repository.ParserInstance;
import com.ixora.rms.repository.ProviderInstance;
import com.ixora.rms.repository.ProviderInstanceId;
import com.ixora.rms.repository.ProviderInstanceMap;
import com.ixora.rms.repository.exception.ArtefactSaveConflict;
import com.ixora.rms.services.ParserRepositoryService;
import com.ixora.rms.services.ProviderInstanceRepositoryService;
import com.ixora.rms.services.ProviderRepositoryService;
import com.ixora.rms.ui.AgentVersionsSelectorPanel;
import com.ixora.rms.ui.RMSViewContainer;
import com.ixora.rms.ui.actions.ActionFromXML;
import com.ixora.rms.ui.tools.providermanager.ProviderPanel.ProviderData;
import com.ixora.rms.ui.tools.providermanager.exception.EntityDescriptorsMissing;
import com.ixora.rms.ui.tools.providermanager.exception.ParsingRulesMissing;
import com.ixora.rms.ui.tools.providermanager.exception.SaveConflict;
import com.ixora.rms.ui.tools.providermanager.messages.Msg;


/**
 * @author Daniel Moraru
 */
public final class ProviderInstanceEditorDialog extends AppDialog {
	private static final long serialVersionUID = 5498284720878727015L;
	private RMSViewContainer fViewContainer;
	private ProviderInstanceRepositoryService fProviderInstanceRepository;
	private ParserRepositoryService fParsingRepository;
	private FormPanel fFormProviderInstance;
	private JPanel fPanelNorth;
	private JTextField fProviderInstanceName;
	private JTextField fProviderInstanceDescription;
	private JTextField fProviderInstanceAuthor;
	private JTabbedPane fTabbedPanel;
	private ProviderPanel fPanel0;
	private ParserPanel fPanel1;
	private DataDescriptorsPanel fPanel2;
	private ResourceId fContext;
	private ProviderInstance fInitialProviderInstanceData;
	private AgentVersionsSelectorPanel fAgentVersionsSelector;
	private EventHandler fEventHandler;

    /** The index of the last selected tab */
    private int fLastSelectedTab;
    private boolean fGetLastValue;
    // keep track of the last values in tab panels
    private ProviderData fLastProviderData;
    private ParserInstance fLastParserInstanceData;
    private Map<EntityId, EntityDescriptor> fLastDescriptors;
    private ProviderInstance fSaved;
    private Set<String> fAllSUOVersions;

	/** Event handler */
	private final class EventHandler implements ChangeListener {
		/**
		 * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
		 */
		public void stateChanged(ChangeEvent e) {
			if(e.getSource() == fTabbedPanel) {
				handleTabSelectionChanged(e);
			}
		}
	}

	/**
	 * Constructor.
	 * @param viewContainer
	 * @param rmsProviderInstanceRepository
	 * @param rmsProviderRepository
	 * @param rmsParserRepository
	 * @param agentVersions
     * @param context
     * @param provider
	 * @throws IOException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws ClassNotFoundException
	 * @throws RMSException
	 */
	public ProviderInstanceEditorDialog(
			RMSViewContainer viewContainer,
			ProviderInstanceRepositoryService rmsProviderInstanceRepository,
			ProviderRepositoryService rmsProviderRepository,
			ParserRepositoryService rmsParserRepository,
            Set<String> agentVersions,
            ResourceId context,
            ProviderInstance provider) throws RMSException, ClassNotFoundException, InstantiationException, IllegalAccessException, IOException {
		super(viewContainer.getAppFrame(), VERTICAL);
		setTitle(MessageRepository.get(ProviderManagerComponent.NAME, Msg.TITLE_PROVIDER_INSTANCE_EDITOR));
		setPreferredSize(new Dimension(550, 550));
		this.fEventHandler = new EventHandler();
        this.fViewContainer = viewContainer;
		this.fProviderInstanceRepository = rmsProviderInstanceRepository;
		this.fParsingRepository = rmsParserRepository;

		fTabbedPanel = new JTabbedPane();
		fPanel0 = new ProviderPanel(rmsProviderRepository);
		fPanel1 = new ParserPanel(fParsingRepository);
		fPanel2 = new DataDescriptorsPanel();
		fTabbedPanel.addTab(MessageRepository.get(ProviderManagerComponent.NAME, Msg.TAB_PROVIDER_INSTANCE_PROVIDER), fPanel0);
		fTabbedPanel.addTab(MessageRepository.get(ProviderManagerComponent.NAME, Msg.TAB_PROVIDER_INSTANCE_PARSER), fPanel1);
		fTabbedPanel.addTab(MessageRepository.get(ProviderManagerComponent.NAME, Msg.TAB_PROVIDER_INSTANCE_DESCRIPTORS), fPanel2);

		fProviderInstanceName = UIFactoryMgr.createTextField();
		fProviderInstanceDescription = UIFactoryMgr.createTextField();
		fProviderInstanceAuthor = UIFactoryMgr.createTextField();

		boolean showAgentVersions = !Utils.isEmptyCollection(agentVersions);
		if(showAgentVersions) {
			fAllSUOVersions = agentVersions;
		    fAgentVersionsSelector = new AgentVersionsSelectorPanel(this, agentVersions);
		}

		fFormProviderInstance = new FormPanel(FormPanel.VERTICAL1);

		if(showAgentVersions) {
			fFormProviderInstance.addPairs(new String[] {
					MessageRepository.get(ProviderManagerComponent.NAME, Msg.TEXT_PROVIDER_INSTANCE_AGENT_VERSIONS),
					MessageRepository.get(ProviderManagerComponent.NAME, Msg.TEXT_PROVIDER_INSTANCE_NAME),
					MessageRepository.get(ProviderManagerComponent.NAME, Msg.TEXT_PROVIDER_INSTANCE_DESCRIPTION),
					MessageRepository.get(ProviderManagerComponent.NAME, Msg.TEXT_PROVIDER_INSTANCE_AUTHOR)
			}, new Component[] {
					fAgentVersionsSelector,
					fProviderInstanceName,
					fProviderInstanceDescription,
					fProviderInstanceAuthor
			});
		} else {
			fFormProviderInstance.addPairs(new String[] {
					MessageRepository.get(ProviderManagerComponent.NAME, Msg.TEXT_PROVIDER_INSTANCE_NAME),
					MessageRepository.get(ProviderManagerComponent.NAME, Msg.TEXT_PROVIDER_INSTANCE_DESCRIPTION),
					MessageRepository.get(ProviderManagerComponent.NAME, Msg.TEXT_PROVIDER_INSTANCE_AUTHOR)
			}, new Component[] {
					fProviderInstanceName,
					fProviderInstanceDescription,
					fProviderInstanceAuthor
			});
		}

		fPanelNorth = new JPanel(new BorderLayout());
		fPanelNorth.add(fFormProviderInstance, BorderLayout.NORTH);

        fGetLastValue = true;

		buildContentPane();

		fTabbedPanel.addChangeListener(fEventHandler);

        edit(context, provider);
	}

    /**
	 * @param data
	 * @throws
	 */
	private void loadDataFromXML(String data) throws Exception {
		ProviderInstance pi = (ProviderInstance) XMLUtils.fromXMLBuffer(ProviderInstance.class,
				new StringBuffer(data), "providerInstance");
		edit(fContext, pi);
	}

	/**
     * @return
     */
    public ProviderInstance saved() {
        return fSaved;
    }

	/**
	 * @param context
	 * @param pi
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws RMSException
	 * @throws IOException
	 */
	private void edit(ResourceId context, ProviderInstance pi) throws RMSException, ClassNotFoundException, InstantiationException, IllegalAccessException, IOException {
		this.fContext = context;

		if(pi != null) {
            // deep clone the initial data to get a working copy
			fInitialProviderInstanceData = (ProviderInstance)Utils.deepClone(pi);
            if(fAgentVersionsSelector != null) {
            	Set<String> provSUOVersion = fInitialProviderInstanceData.getAgentVersions();
            	// remove SUO versions which are not in the agents current SUO versions
            	// (it can happen when data is imported from XML)
            	if(Utils.isEmptyCollection(fAllSUOVersions)) {
            		provSUOVersion = null;
            	} else if(!Utils.isEmptyCollection(provSUOVersion)) {
            		for(Iterator<String> iter = provSUOVersion.iterator(); iter.hasNext();) {
            			if(!fAllSUOVersions.contains(iter.next())) {
            				iter.remove();
            			}
            		}
            	}
                fAgentVersionsSelector.setSelectedAgentVersions(provSUOVersion);
            }
			fProviderInstanceName.setText(fInitialProviderInstanceData.getInstanceName());
			fProviderInstanceDescription.setText(fInitialProviderInstanceData.getInstanceDescription());
			fProviderInstanceAuthor.setText(fInitialProviderInstanceData.getAuthor());
		} else {
		    fInitialProviderInstanceData = null;
        }
		// set first panel with initial data
		fPanel0.setProviderInstanceData(fInitialProviderInstanceData);
        if(fInitialProviderInstanceData != null) {
            fLastParserInstanceData = fInitialProviderInstanceData.getParserInstance();
            fPanel1.setParserInstanceData(fInitialProviderInstanceData.getProviderName(),
                fLastParserInstanceData);
            fLastDescriptors = fInitialProviderInstanceData.getEntityDescriptors();
            fPanel2.setProviderInstanceData(fLastDescriptors, fLastParserInstanceData);
        }
		updateTitle(context, fInitialProviderInstanceData);

		repaintDisplay();
	}

	/**
	 * @param context
	 * @param pi
	 */
	private void updateTitle(ResourceId context, ProviderInstance pi) {
		setTitle(MessageRepository.get(ProviderManagerComponent.NAME, Msg.TITLE_PROVIDER_INSTANCE_EDITOR)
				+ ": "
				+ new ProviderInstanceId(context, pi == null ? "-" : pi.getInstanceName()).toString());
	}

	/**
	 * @see com.ixora.common.ui.AppDialog#getDisplayPanels()
	 */
	protected Component[] getDisplayPanels() {
		return new Component[] {fPanelNorth, fTabbedPanel};
	}

	/**
	 * @see com.ixora.common.ui.AppDialog#getButtons()
	 */
	@SuppressWarnings("serial")
	protected JButton[] getButtons() {
		return new JButton[] {
				UIFactoryMgr.createButton(new ActionOk() {
					public void actionPerformed(ActionEvent e) {
						handleOk();
					}}),
				UIFactoryMgr.createButton(new ActionFromXML(fViewContainer) {
					protected void handleXML(String data) throws Exception {
						loadDataFromXML(data);
					}
				}),
				UIFactoryMgr.createButton(new ActionCancel() {
					public void actionPerformed(ActionEvent e) {
                        fSaved = null;
						dispose();
					}})
		};
	}

	/**
	 * Creates a provider instance and tries to save it.
	 */
	private void handleOk() {
		try {
            final ProviderInstance pi = getProviderInstanceData();
            fViewContainer.getAppWorker().runJobSynch(new UIWorkerJobDefault(
					this,
					Cursor.WAIT_CURSOR,
					MessageRepository.get(ProviderManagerComponent.NAME, Msg.TEXT_SAVING_PROVIDER_INSTANCE)) {
				public void work() throws Exception {
					saveProviderInstance(pi);
					updateTitle(fContext, pi);
					edit(fContext, pi);
				}
				public void finished(Throwable ex) {
					if(ex == null) {
                        // switch focus on the Parser tab
                        if(ex instanceof ParsingRulesMissing) {
                            fTabbedPanel.setSelectedIndex(1);
                        } else // switch focus on the Descriptors tab
                            if(ex instanceof EntityDescriptorsMissing) {
                                fTabbedPanel.setSelectedIndex(2);
                        }
                        fSaved = pi;
                        dispose();
    				}
                }
			});
		} catch(Exception e) {
			UIExceptionMgr.userException(e);
		}
	}

	/**
     * @param provider
	 * @throws RMSException
     */
    private void saveProviderInstance(ProviderInstance provider) throws RMSException {
        String agentId = fContext.getAgentId().getInstallationId();
        ProviderInstanceMap map = fProviderInstanceRepository.
        getAgentProviderInstances(agentId);
        if(map == null) {
            map = new ProviderInstanceMap();
        }
        try {
            map.addOrUpdateWithConflictDetection(fInitialProviderInstanceData, provider);
        } catch(ArtefactSaveConflict e) {
            throw new SaveConflict(provider.getArtefactName());
        }
        map.add(provider);
        fProviderInstanceRepository.setAgentProviderInstances(agentId, map);
        fProviderInstanceRepository.save();
    }

    /**
	 * @return
	 * @throws InvalidFormData
	 * @throws ParsingRulesMissing
	 * @throws VetoException
	 * @throws InvalidPropertyValue
	 * @throws EntityDescriptorsMissing
	 * @throws InvalidParsingRules
	 */
	private ProviderInstance getProviderInstanceData() throws InvalidFormData, ParsingRulesMissing, InvalidPropertyValue, VetoException, EntityDescriptorsMissing, InvalidParsingRules {
		String instanceName = this.fProviderInstanceName.getText();
		if(Utils.isEmptyString(instanceName)) {
			throw new InvalidFormData(MessageRepository.get(ProviderManagerComponent.NAME, Msg.TEXT_PROVIDER_INSTANCE_NAME));
		}
		String instanceDescription = this.fProviderInstanceDescription.getText();
		String instanceAuthor = this.fProviderInstanceAuthor.getText();
        // finish tabs
        int selectedTab = fTabbedPanel.getSelectedIndex();
        switch(selectedTab) {
            case 0:
                fTabbedPanel.setSelectedIndex(1);
                fTabbedPanel.setSelectedIndex(2);
            break;
            case 1:
                fTabbedPanel.setSelectedIndex(2);
            break;
        }

        // wizard completed, get data...
        ProviderPanel.ProviderData pd = this.fPanel0.getProviderData();
        ParserInstance pid = this.fPanel1.getParserInstanceData();
		Map<EntityId, EntityDescriptor> edesc = fPanel2.getEntityDescriptors(pid);
		ProviderInstance ret = new ProviderInstance(
				pd.providerName,
				instanceName,
				instanceDescription,
				pd.configuration,
				pd.remote,
				pd.optional,
				pd.selectedByDefault,
				pd.inheritsLocationFromAgent, pid,
				edesc.values(),
				fAgentVersionsSelector == null ? null : fAgentVersionsSelector.getSelectedAgentVersions(),
				instanceAuthor);
		// check author info
		AuthoredArtefactHelper.checkArtefact(ret);
    	if(ConfigurationMgr.isDeveloperMode()) {
    		ret.setAuthor(AuthoredArtefact.SYSTEM);
    	} else if(Utils.isEmptyString(ret.getAuthor())) {
    		ret.setAuthor(AuthoredArtefact.CUSTOMER);
    	}
		return ret;
	}

	/**
	 * @param ev
	 */
	private void handleTabSelectionChanged(ChangeEvent ev) {
		try {
            // stop editing on all panels first
            fPanel0.stopEditing();
            fPanel1.stopEditing();
            fPanel2.stopEditing();

            // get data from the tab which was deselected
            if(fGetLastValue) {
                switch(fLastSelectedTab) {
                    case 0:
                        try {
                            fLastProviderData = fPanel0.getProviderData();
                            fGetLastValue = true;
                        } catch(Exception e) {
                            UIExceptionMgr.userException(e);
                            fGetLastValue = false;
                            fTabbedPanel.setSelectedIndex(0);
                            return;
                        }
                    break;
                    case 1:
                        try {
                            fLastParserInstanceData = fPanel1.getParserInstanceData();
                            fGetLastValue = true;
                        } catch(Exception e) {
                            UIExceptionMgr.userException(e);
                            fGetLastValue = false;
                            fTabbedPanel.setSelectedIndex(1);
                            return;
                        }
                    break;
                    case 2:
                        try {
                            fLastDescriptors = fPanel2.getEntityDescriptors(fLastParserInstanceData);
                            fGetLastValue = true;
                        } catch(Exception e) {
                            UIExceptionMgr.userException(e);
                            fGetLastValue = false;
                            fTabbedPanel.setSelectedIndex(2);
                            return;
                        }
                    break;
                }
            }

            // take care of dependencies
            int idx = fTabbedPanel.getModel().getSelectedIndex();
            switch(idx) {
                case 0:
                    fLastSelectedTab = 0;
                    fGetLastValue = true;
                break;
                case 1: // depends on 0
                    if(fGetLastValue) {
                        fPanel1.setParserInstanceData(fLastProviderData.providerName,
                                fLastParserInstanceData != null ? fLastParserInstanceData
                                        : (fInitialProviderInstanceData != null ? fInitialProviderInstanceData.getParserInstance() : null));
                    }
                    fLastSelectedTab = 1;
                    fGetLastValue = true;
                break;
                case 2: // depends on 1
                    // if 1 was missed go back to that tab
                    if(fGetLastValue) {
                        if(fLastParserInstanceData == null) {
                            fTabbedPanel.setSelectedIndex(1);
                        }
                        fPanel2.setProviderInstanceData(
                                fLastDescriptors != null ? fLastDescriptors
                                        : (fInitialProviderInstanceData != null ? fInitialProviderInstanceData.getEntityDescriptors() : null),
                                fLastParserInstanceData);
                    }
                    fLastSelectedTab = 2;
                    fGetLastValue = true;
               break;
            }
		} catch(Exception e) {
			UIExceptionMgr.userException(e);
        }
	}
}
