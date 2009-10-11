/*
 * Created on 08-Feb-2005
 */
package com.ixora.rms.ui.tools.agentinstaller;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.AbstractTableModel;

import com.ixora.common.MessageRepository;
import com.ixora.common.typedproperties.TypedProperties;
import com.ixora.common.typedproperties.ui.list.TypedPropertiesListEditor;
import com.ixora.common.ui.AppDialog;
import com.ixora.common.ui.UIExceptionMgr;
import com.ixora.common.ui.UIFactoryMgr;
import com.ixora.common.ui.actions.ActionClose;
import com.ixora.common.ui.actions.ActionOk;
import com.ixora.common.ui.exception.InvalidFormData;
import com.ixora.common.ui.forms.FormPanel;
import com.ixora.common.utils.Utils;
import com.ixora.rms.MonitoringLevel;
import com.ixora.rms.agents.AgentLocation;
import com.ixora.rms.repository.VersionableAgentInstallationData;
import com.ixora.rms.repository.VersionableAgentInstallationDataMap;
import com.ixora.rms.repository.exception.ArtefactSaveConflict;
import com.ixora.rms.ui.AgentVersionsSelectorPanel;
import com.ixora.rms.ui.RMSViewContainer;
import com.ixora.rms.ui.tools.agentinstaller.exception.VersionDataConflict;
import com.ixora.rms.ui.tools.agentinstaller.messages.Msg;

/**
 * @author Daniel Moraru
 */
public final class AgentInstallationVersionDataDialog extends AppDialog {
	private static final long serialVersionUID = -135375609851806753L;
	private static final String LABEL_AGENT_LOCATIONS = MessageRepository.get(AgentInstallerComponent.NAME, Msg.LABEL_AGENT_LOCATIONS);
	private static final String LABEL_AGENT_LEVELS = MessageRepository.get(AgentInstallerComponent.NAME, Msg.LABEL_AGENT_LEVELS);
	private static final String LABEL_AGENT_JARS = MessageRepository.get(AgentInstallerComponent.NAME, Msg.LABEL_AGENT_JARS);
	private static final String LABEL_AGENT_NATLIBS = MessageRepository.get(AgentInstallerComponent.NAME, Msg.LABEL_AGENT_NATLIBS);
	private static final String LABEL_AGENT_UIJAR = MessageRepository.get(AgentInstallerComponent.NAME, Msg.LABEL_AGENT_UIJAR);
	private static final String LABEL_AGENT_CONFIG_PANEL = MessageRepository.get(AgentInstallerComponent.NAME, Msg.LABEL_AGENT_CONFIG_PANEL);

	private JPanel fPanel;
	private FormPanel fForm;

	private JTextField fTextFieldUIJar;
	private JTextField fTextFieldConfigPanel;
    private AgentVersionsSelectorPanel fPanelAgentVersionSelector;
	private JTable fTableLocation;
	private JTable fTableLevels;
	private TypedPropertiesListEditor fEditorJars;
	private TypedPropertiesListEditor fEditorNatlibs;
	private LocationsTableModel fLocationsTableModel;
	private LevelsTableModel fLevelsTableModel;
	private TypedProperties fPrototypeJar;
	private TypedProperties fPrototypeNatLib;
	private VersionableAgentInstallationData fResult;
	private boolean fReadOnly;
    private VersionableAgentInstallationDataMap fVersionData;
    private VersionableAgentInstallationData fOriginalVad;

	/**
	 * Model for locations table.
	 */
	private static class LocationsTableModel extends AbstractTableModel {
		private static final long serialVersionUID = -3062129406030042215L;
		private AgentLocation[] fLocations = new AgentLocation[] {AgentLocation.LOCAL, AgentLocation.REMOTE};
		private Boolean[] fEnabled = new Boolean[] {Boolean.FALSE, Boolean.FALSE};

		public int getRowCount() {
			return 2;
		}
		public int getColumnCount() {
			return 2;
		}
		public String getColumnName(int column) {
			return "";
		}
		public Class< ? > getColumnClass(int columnIndex) {
			return columnIndex == 0 ? Boolean.class : AgentLocation.class;
		}
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return columnIndex == 0;
		}
		public Object getValueAt(int rowIndex, int columnIndex) {
			return columnIndex == 0 ? fEnabled[rowIndex] : fLocations[rowIndex];
		}
		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			fEnabled[rowIndex] = (Boolean)aValue;
			// this will get rid of the selection
			fireTableDataChanged();
		}
		boolean isRemoteEnabled() {
			return fEnabled[1].booleanValue();
		}
		boolean isLocalEnabled() {
			return fEnabled[0].booleanValue();
		}
		public void setLocation(AgentLocation location, boolean b) {
			if(location == AgentLocation.LOCAL) {
				fEnabled[0] = Boolean.valueOf(b);
			} else if(location == AgentLocation.REMOTE) {
				fEnabled[1] = Boolean.valueOf(b);
			}
		}
		public AgentLocation[] getLocations() {
			List<AgentLocation> lst = new LinkedList<AgentLocation>();
			if(fEnabled[0].booleanValue()) {
				lst.add(fLocations[0]);
			}
			if(fEnabled[1].booleanValue()) {
				lst.add(fLocations[1]);
			}
			return lst.toArray(new AgentLocation[lst.size()]);
		}
	}

	/**
	 * Model for levels table.
	 */
	private static class LevelsTableModel extends AbstractTableModel {
		private static final long serialVersionUID = -5484520287978467346L;
		private MonitoringLevel[] fLevels = new MonitoringLevel[] {
				MonitoringLevel.LOW, MonitoringLevel.MEDIUM, MonitoringLevel.HIGH, MonitoringLevel.MAXIMUM};
		private Boolean[] fDefault = new Boolean[] {Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE};
		private Boolean[] fUsed = new Boolean[] {Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE};

		public int getRowCount() {
			return 4;
		}
		public int getColumnCount() {
			return 3;
		}
		public String getColumnName(int column) {
			switch(column) {
				case 0:
					return "Used";
				case 1:
					return "Default";
				case 2:
					return "Level";
			}
			return "";
		}
		public Class< ? > getColumnClass(int columnIndex) {
			return columnIndex == 0 || columnIndex == 1 ? Boolean.class : MonitoringLevel.class;
		}
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return columnIndex == 0 || columnIndex == 1;
		}
		public Object getValueAt(int rowIndex, int columnIndex) {
			switch(columnIndex) {
				case 0:
					return fUsed[rowIndex];
				case 1:
					return fDefault[rowIndex];
				case 2:
					return fLevels[rowIndex];
			}
			return null;
		}
		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			switch(columnIndex) {
				case 0:
					Boolean newVal = (Boolean)aValue;
					fUsed[rowIndex] = (Boolean)aValue;
					if(!newVal.booleanValue()) {
						fDefault[rowIndex] = Boolean.FALSE;
					}
					break;
				case 1:
					newVal = (Boolean)aValue;
					if(newVal.booleanValue()) {
						fUsed[rowIndex] = newVal;
					}
					for(int i = 0; i < fDefault.length; i++) {
						fDefault[i] = Boolean.FALSE;
					}
					fDefault[rowIndex] = newVal;
					break;
			}
			fireTableDataChanged();
		}
		MonitoringLevel[] getUsedMonitoringLevels() {
			List<MonitoringLevel> lst = new LinkedList<MonitoringLevel>();
			for(int i = 0; i < fLevels.length; i++) {
				if(fUsed[i].booleanValue()) {
					lst.add(fLevels[i]);
				}
			}
			return lst.toArray(new MonitoringLevel[lst.size()]);
		}
		MonitoringLevel getDefaultMonitoringLevel() {
			for(int i = 0; i < fDefault.length; i++) {
				if(fDefault[i].booleanValue()) {
					return fLevels[i];
				}
			}
			return null;
		}
		public void setUsedLevel(MonitoringLevel level, boolean b) {
			int idx = Arrays.binarySearch(fLevels, level);
			if(idx < 0) {
				return;
			}
			fUsed[idx] = Boolean.valueOf(b);
		}
		public void setDefaultLevel(MonitoringLevel level, boolean b) {
			int idx = Arrays.binarySearch(fLevels, level);
			if(idx < 0) {
				return;
			}
			fDefault[idx] = Boolean.valueOf(b);
		}
	}

	/**
	 * Constructor.
	 * @param vc
	 * @param suoVersions available suo versions
	 * @param vad
	 * @param readOnly
	 * @param versionData
	 */
	@SuppressWarnings("serial")
	public AgentInstallationVersionDataDialog(
			RMSViewContainer vc,
			String[] suoVersions,
			VersionableAgentInstallationData vad,
			boolean readOnly,
			VersionableAgentInstallationDataMap versionData) {
		super(vc.getAppFrame(), VERTICAL);
		setModal(true);
		setTitle(MessageRepository.get(
				AgentInstallerComponent.NAME, Msg.TITLE_AGENT_INSTALLATION_VERSION_DATA_EDITOR));
		fReadOnly = readOnly;
		fVersionData = versionData;
		fOriginalVad = vad;
		fPanel = new JPanel(new BorderLayout());
		fForm = new FormPanel(FormPanel.VERTICAL1, SwingConstants.TOP) {
			public void setEnabled(boolean e) {
				super.setEnabled(e);
				fTableLevels.setEnabled(e);
				fTableLocation.setEnabled(e);
				fEditorJars.setEnabled(e);
			}
		};

		fTextFieldUIJar = UIFactoryMgr.createTextField();
		fTextFieldConfigPanel = UIFactoryMgr.createTextField();
		fPanelAgentVersionSelector = new AgentVersionsSelectorPanel(
				this,
				suoVersions == null ? null : new HashSet<String>(Arrays.asList(suoVersions)));
		fLocationsTableModel = new LocationsTableModel();
		fTableLocation = new JTable(fLocationsTableModel);
		fTableLocation.getColumnModel().getColumn(0).setMaxWidth(25);
		JScrollPane spLocations = UIFactoryMgr.createScrollPane();
		spLocations.setViewportView(fTableLocation);
		spLocations.setVerticalScrollBarPolicy(
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		spLocations.setHorizontalScrollBarPolicy(
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		spLocations.setMinimumSize(new Dimension(200, 50));
		spLocations.setMaximumSize(new Dimension(200, 60));
		spLocations.setPreferredSize(new Dimension(200, 50));

		fLevelsTableModel = new LevelsTableModel();
		fTableLevels = new JTable(fLevelsTableModel);
		fTableLevels.getColumnModel().getColumn(0).setMaxWidth(75);
		fTableLevels.getColumnModel().getColumn(1).setMaxWidth(75);
		JScrollPane spLevels = UIFactoryMgr.createScrollPane();
		spLevels.setViewportView(fTableLevels);
		spLevels.setVerticalScrollBarPolicy(
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		spLevels.setHorizontalScrollBarPolicy(
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		spLevels.setMinimumSize(new Dimension(200, 90));
		spLevels.setMaximumSize(new Dimension(200, 90));
		spLevels.setPreferredSize(new Dimension(200, 90));

		fPrototypeJar = createFromJar(null);

		fEditorJars = new TypedPropertiesListEditor(fPrototypeJar,
				AgentInstallerComponent.NAME, null, TypedPropertiesListEditor.BUTTON_ALL);
		fEditorJars.setPreferredSize(new Dimension(300, 120));
		fEditorJars.setMaximumSize(new Dimension(300, 120));

		fPrototypeNatLib = createFromNatlib(null);

		fEditorNatlibs = new TypedPropertiesListEditor(fPrototypeNatLib,
				AgentInstallerComponent.NAME, null, TypedPropertiesListEditor.BUTTON_ALL);
		fEditorNatlibs.setPreferredSize(new Dimension(300, 120));
		fEditorNatlibs.setMaximumSize(new Dimension(300, 120));

		fForm.addPairs(
				new String[] {
                        "System Versions", // TODO localize
                        LABEL_AGENT_LOCATIONS,
						LABEL_AGENT_LEVELS,
						LABEL_AGENT_JARS,
						LABEL_AGENT_NATLIBS,
						LABEL_AGENT_UIJAR,
						LABEL_AGENT_CONFIG_PANEL
				},
				new Component[] {
                        fPanelAgentVersionSelector,
                        spLocations,
						spLevels,
						fEditorJars,
						fEditorNatlibs,
						fTextFieldUIJar,
						fTextFieldConfigPanel
				});
		fPanel.add(fForm, BorderLayout.NORTH);

		if(readOnly) {
			fForm.setEnabled(false);
		}

		if(vad != null) {
			// set with data from the given vad if not null
			configurePanelForVersionData(vad);
		}
		buildContentPane();
		pack();
	}

    /**
     * @param version
     */
	private void configurePanelForVersionData(VersionableAgentInstallationData vad) {
		fPanelAgentVersionSelector.setSelectedAgentVersions(vad.getAgentVersions());
        fTextFieldConfigPanel.setText(vad.getConfigPanelClass());
        String uiJar = vad.getUIJar();
        if(uiJar != null) {
            fTextFieldUIJar.setText(uiJar);
        }
        // location
        AgentLocation[] locations = vad.getLocations();
        if(locations != null) {
            if(Arrays.binarySearch(locations, AgentLocation.LOCAL) >= 0) {
                fLocationsTableModel.setLocation(AgentLocation.LOCAL, true);
            } else {
                fLocationsTableModel.setLocation(AgentLocation.LOCAL, false);
            }
            if(Arrays.binarySearch(locations, AgentLocation.REMOTE) >= 0) {
                fLocationsTableModel.setLocation(AgentLocation.REMOTE, true);
            } else {
                fLocationsTableModel.setLocation(AgentLocation.REMOTE, false);
            }
        }
        // monitoring level
        MonitoringLevel[] levels = vad.getMonitoringLevels();
        MonitoringLevel defaultLevel = vad.getDefaultMonitoringLevel();
        if(levels != null) {
            for(MonitoringLevel level : levels) {
                fLevelsTableModel.setUsedLevel(level, true);
                if(defaultLevel == level) {
                    fLevelsTableModel.setDefaultLevel(level, true);
                }
            }
        }
        // jars
        String[] jars = vad.getJars();
        if(!Utils.isEmptyArray(jars)) {
            List<TypedProperties> lstJars = new ArrayList<TypedProperties>(jars.length);
            for(String j : jars) {
                TypedProperties tp = createFromJar(j);
                lstJars.add(tp);
            }
            fEditorJars.getModel().setProperties(lstJars);
        }
        // native libraries
        String[] natlibs = vad.getNativeLibraries();
        if(!Utils.isEmptyArray(natlibs)) {
            List<TypedProperties> lstNatLibs = new ArrayList<TypedProperties>(natlibs.length);
            for(String nl : natlibs) {
                TypedProperties tp = createFromNatlib(nl);
                lstNatLibs.add(tp);
            }
            fEditorNatlibs.getModel().setProperties(lstNatLibs);
        }
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
			AgentLocation[] locations = fLocationsTableModel.getLocations();
			if(Utils.isEmptyArray(locations)) {
				throw new InvalidFormData(LABEL_AGENT_LOCATIONS);
			}
			MonitoringLevel[] levels = fLevelsTableModel.getUsedMonitoringLevels();
			int defaultLevelIdx = 0;
			if(Utils.isEmptyArray(levels)) {
				levels = null;
			} else {
				MonitoringLevel defaultLevel = fLevelsTableModel.getDefaultMonitoringLevel();
				if(defaultLevel == null) {
					defaultLevel = levels[0];
				} else {
					int idx = Arrays.binarySearch(levels, defaultLevel);
					if(idx < 0) {
						defaultLevelIdx = 0;
						defaultLevel = levels[defaultLevelIdx];
					} else {
						defaultLevelIdx = idx;
					}
				}
			}
			String uiJar = fTextFieldUIJar.getText();
			if(Utils.isEmptyString(uiJar)) {
				uiJar = null;
			}
			fEditorJars.stopEditing();
			String[] jars = getJarsFromTableModel();

			fEditorNatlibs.stopEditing();
			String[] natlibs = getNativeLibsFromTableModel();

			String configPanel = fTextFieldConfigPanel.getText();
			if(Utils.isEmptyString(configPanel)) {
				configPanel = null;
			}

			VersionableAgentInstallationData vad = new VersionableAgentInstallationData(
					configPanel,
					locations,
					levels,
					defaultLevelIdx,
					jars,
					uiJar,
					natlibs);
			Set<String> vers = fPanelAgentVersionSelector.getSelectedAgentVersions();
			if(!Utils.isEmptyCollection(vers)) {
				vad.addAgentVersions(vers);
			}
			// add checking for conflicts
			try {
				fVersionData.addOrUpdateWithConflictDetection(fOriginalVad, vad);
			} catch(ArtefactSaveConflict e) {
				throw new VersionDataConflict();
			}
			fResult = vad;
			dispose();
		} catch(Exception e) {
			UIExceptionMgr.userException(e);
		}
	}

	/**
	 * @param jar can be null
	 * @return
	 */
	private static TypedProperties createFromJar(String jar) {
		TypedProperties prop = new TypedProperties();
		prop.setProperty(Msg.COLUMN_JAR_PATH, TypedProperties.TYPE_STRING, true, false);
		if(jar != null) {
			prop.setString(Msg.COLUMN_JAR_PATH, jar);
		}
		return prop;
	}

	/**
	 * @param natlib can be null
	 * @return
	 */
	private static TypedProperties createFromNatlib(String natlib) {
		TypedProperties prop = new TypedProperties();
		prop.setProperty(Msg.COLUMN_NATLIB_PATH, TypedProperties.TYPE_STRING, true, false);
		if(natlib != null) {
			prop.setString(Msg.COLUMN_NATLIB_PATH, natlib);
		}
		return prop;
	}

	/**
	 * @param prop
	 * @return
	 */
	private static String createJarFromTypedProperties(TypedProperties prop) {
		return prop.getString(Msg.COLUMN_JAR_PATH);
	}

	/**
	 * @param prop
	 * @return
	 */
	private static String createNatlibFromTypedProperties(TypedProperties prop) {
		return prop.getString(Msg.COLUMN_NATLIB_PATH);
	}

	/**
	 * @return
	 */
	private String[] getJarsFromTableModel() {
		List<TypedProperties> lst = fEditorJars.getModel().getAllProperties();
		if(Utils.isEmptyCollection(lst)) {
			return null;
		}
		List<String> ret = new LinkedList<String>();
		for(Iterator<TypedProperties> iter = lst.iterator(); iter.hasNext();) {
			String s = createJarFromTypedProperties(iter.next());
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
	 * @return
	 */
	private String[] getNativeLibsFromTableModel() {
		List<TypedProperties> lst = fEditorNatlibs.getModel().getAllProperties();
		if(Utils.isEmptyCollection(lst)) {
			return null;
		}
		List<String> ret = new LinkedList<String>();
		for(Iterator<TypedProperties> iter = lst.iterator(); iter.hasNext();) {
			String s = createNatlibFromTypedProperties(iter.next());
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
	 * @return the versionable agent installation data
	 */
	public VersionableAgentInstallationData getResult() {
		return fResult;
	}
}
