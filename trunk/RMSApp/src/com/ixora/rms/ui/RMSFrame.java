package com.ixora.rms.ui;

import java.awt.Cursor;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.rmi.RemoteException;
import java.rmi.server.ExportException;
import java.util.Observable;
import java.util.Observer;

import javax.help.HelpSetException;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.event.PopupMenuEvent;

import org.apache.log4j.PropertyConfigurator;

import com.ixora.common.ComponentConfiguration;
import com.ixora.common.ConfigurationMgr;
import com.ixora.common.IxoraCommonModule;
import com.ixora.common.MessageRepository;
import com.ixora.common.exception.AppRuntimeException;
import com.ixora.common.exception.FailedToSaveConfiguration;
import com.ixora.common.logging.AppLogger;
import com.ixora.common.logging.AppLoggerFactory;
import com.ixora.common.security.license.LicenseMgr;
import com.ixora.common.security.license.ui.DefaultLicenseProvider;
import com.ixora.common.ui.AppFrame;
import com.ixora.common.ui.AppFrameParameters;
import com.ixora.common.ui.AppSplashScreen;
import com.ixora.common.ui.ButtonWithPopup;
import com.ixora.common.ui.UIConfiguration;
import com.ixora.common.ui.UIExceptionMgr;
import com.ixora.common.ui.UIFactoryMgr;
import com.ixora.common.ui.UIUtils;
import com.ixora.common.ui.actions.ActionBrowse;
import com.ixora.common.ui.help.HelpMgr;
import com.ixora.common.ui.jobs.UIWorkerJobDefault;
import com.ixora.common.ui.popup.MRUPopupMenuBuilder;
import com.ixora.common.ui.preferences.PreferencesConfigurationConstants;
import com.ixora.common.update.UpdateMgr;
import com.ixora.common.utils.Utils;
import com.ixora.common.xml.exception.XMLException;
import com.ixora.jobs.JobsComponent;
import com.ixora.jobs.services.JobEngineService;
import com.ixora.jobs.services.JobLibraryService;
import com.ixora.jobs.ui.JobManagerDialog;
import com.ixora.rms.RMS;
import com.ixora.rms.RMSComponent;
import com.ixora.rms.RMSConfigurationConstants;
import com.ixora.rms.RMSModule;
import com.ixora.rms.client.session.MonitoringSessionDescriptor;
import com.ixora.rms.exception.RMSException;
import com.ixora.rms.logging.LogComponent;
import com.ixora.rms.logging.LogRepositoryInfo;
import com.ixora.rms.reactions.ReactionsComponent;
import com.ixora.rms.reactions.email.ReactionsEmailComponent;
import com.ixora.rms.services.AgentInstallerService;
import com.ixora.rms.services.AgentRepositoryService;
import com.ixora.rms.services.AgentTemplateRepositoryService;
import com.ixora.rms.services.HostMonitorService;
import com.ixora.rms.services.ParserRepositoryService;
import com.ixora.rms.services.ProviderInstanceRepositoryService;
import com.ixora.rms.services.ProviderRepositoryService;
import com.ixora.rms.starter.RMSAppStarter;
import com.ixora.rms.ui.dataviewboard.DataViewBoardComponent;
import com.ixora.rms.ui.dataviewboard.charts.ChartsBoardComponent;
import com.ixora.rms.ui.dataviewboard.logs.LogBoardComponent;
import com.ixora.rms.ui.dataviewboard.properties.PropertiesBoardComponent;
import com.ixora.rms.ui.dataviewboard.tables.TablesBoardComponent;
import com.ixora.rms.ui.logchooser.LogChooser;
import com.ixora.rms.ui.logchooser.LogChooserImpl;
import com.ixora.rms.ui.messages.Msg;
import com.ixora.rms.ui.session.MonitoringSessionRepository;
import com.ixora.rms.ui.session.MonitoringSessionRepositoryComponent;
import com.ixora.rms.ui.session.MonitoringSessionRepositoryImpl;
import com.ixora.rms.ui.tools.agentinstaller.AgentInstallerDialog;
import com.ixora.rms.ui.tools.providermanager.ProviderInstanceManagerDialog;
import com.ixora.rms.ui.views.logreplay.LogPlaybackView;
import com.ixora.rms.ui.views.session.LiveSessionView;


/*
 * Created on 25-Nov-03
 */

/**
 * @author Daniel Moraru
 */
public final class RMSFrame extends AppFrame implements RMSViewContainer,
		Observer {
	/** Logger */
	private static final AppLogger logger;

	static {
		// setup log4j
        PropertyConfigurator.configure(Utils.getPath("config/log4j.console.properties"));
		logger = AppLoggerFactory.getLogger(RMSFrame.class);
	}

	private static AppSplashScreen fSplashScreen;

	// Components
	private javax.swing.JDesktopPane fDesktopPane;

	private javax.swing.JMenuItem fMenuItemNewSession;

	private javax.swing.JMenuItem fMenuItemLoadSessionBrowse;

	private javax.swing.JMenu fMenuLoadSession;

	private javax.swing.JMenuItem fMenuItemLoadLog;

	private javax.swing.JButton fButtonNewSession;

	private ButtonWithPopup fButtonLoadSession;

	private javax.swing.JButton fButtonLoadLog;

	private javax.swing.JSplitPane fSplitPane;

	private javax.swing.JMenu fMenuTools;

	private javax.swing.JMenuItem fMenuItemToolsJobManager;

	private javax.swing.JMenuItem fMenuItemToolsProviderManager;

	private javax.swing.JMenuItem fMenuItemToolsAgentInstaller;

	// MRU Popup menu builders for the popup menus of the load
	// session button and menu
	private MRUPopupMenuBuilder fMRUPopupMenuForButton;

	private MRUPopupMenuBuilder fMRUPopupMenuForMenu;

	// Event handler
	private EventHandler fEventHandler;

	private MonitoringSessionRepository fMonitoringSessionRepository;

	// the following will be created as needed; they are used by various tools
	private JobEngineService fRMSJobEngine;

	private JobLibraryService fRMSJobLibrary;

	private HostMonitorService fRMSHostMonitor;

	private AgentRepositoryService fRMSAgentRepository;

	private AgentInstallerService fRMSAgentInstaller;

	private ProviderRepositoryService fRMSProviderRepository;

	private ProviderInstanceRepositoryService fRMSProviderInstanceRepository;

	private ParserRepositoryService fRMSParserRepository;

	private AgentTemplateRepositoryService fRMSAgentTemplateRepository;

	private boolean fLicenseChecked;

	private boolean fDirtySession;

	// Actions
	private Action fActionLoadSession = new ActionLoadSession();

	private Action fActionLoadLog = new ActionLoadLog();

	private Action fActionNewSession = new ActionNewSession();

	private Action fActionBrowseForScheme = new ActionBrowseForScheme();

	/**
	 * Button with popup menu that will load the MRU list of available scheme
	 * before showing the popup menu
	 *
	 * @author Daniel Moraru
	 */
	private final class ButtonWithPopupLoadScheme extends ButtonWithPopup {
		/**
		 * @param action
		 */
		public ButtonWithPopupLoadScheme(Action action) {
			super(action);
		}

		/**
		 * @see javax.swing.event.PopupMenuListener#popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent)
		 */
		public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
			super.popupMenuWillBecomeVisible(e);
			buildLoadSchemeMruPopupMenu(fMRUPopupMenuForButton);
		}
	}

	/**
	 * Browse for scheme action.
	 */
	private final class ActionBrowseForScheme extends ActionBrowse {
		/**
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent arg0) {
			handleBrowseForSession();
		}
	}

	/**
	 * Load session action.
	 */
	private final class ActionLoadSession extends AbstractAction {
		public ActionLoadSession() {
			super();
			UIUtils.setUsabilityDtls(MessageRepository
					.get(Msg.ACTIONS_LOADSESSION), this);
			ImageIcon icon = UIConfiguration.getIcon("load_session.gif");
			if(icon != null) {
				putValue(Action.SMALL_ICON, icon);
			}
		}

		/**
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent arg0) {
			handleLoadMonitoringSessionAction(arg0);
		}
	}

	/**
	 * Launch job manager.
	 */
	private final class ActionLaunchJobManager extends AbstractAction {
		public ActionLaunchJobManager() {
			super();
			UIUtils.setUsabilityDtls(MessageRepository
					.get(Msg.ACTIONS_LAUNCH_JOBMANAGER), this);
			ImageIcon icon = UIConfiguration.getIcon("job_manager.gif");
			if(icon != null) {
				putValue(Action.SMALL_ICON, icon);
			}
		}

		/**
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent arg0) {
			handleLaunchJobManager();
		}
	}

	/**
	 * Launch provider manager.
	 */
	private final class ActionLaunchProviderManager extends AbstractAction {
		public ActionLaunchProviderManager() {
			super();
			UIUtils.setUsabilityDtls(MessageRepository
					.get(Msg.ACTIONS_LAUNCH_PROVIDERMANAGER), this);
			ImageIcon icon = UIConfiguration.getIcon("provider_manager.gif");
			if(icon != null) {
				putValue(Action.SMALL_ICON, icon);
			}
		}

		/**
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent arg0) {
			handleLaunchProviderManager();
		}
	}

	/**
	 * Launch agent installer.
	 */
	private final class ActionLaunchAgentInstaller extends AbstractAction {
		public ActionLaunchAgentInstaller() {
			super();
			UIUtils.setUsabilityDtls(MessageRepository
					.get(Msg.ACTIONS_LAUNCH_AGENTINSTALLER), this);
			ImageIcon icon = UIConfiguration.getIcon("agent_installer.gif");
			if(icon != null) {
				putValue(Action.SMALL_ICON, icon);
			}
		}

		/**
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent arg0) {
			handleLaunchAgentInstaller();
		}
	}

	/**
	 * New session action.
	 */
	private final class ActionNewSession extends AbstractAction {
		public ActionNewSession() {
			super();
			UIUtils.setUsabilityDtls(MessageRepository
					.get(Msg.ACTIONS_NEWSESSION), this);
			ImageIcon icon = UIConfiguration.getIcon("new_session.gif");
			if(icon != null) {
				putValue(Action.SMALL_ICON, icon);
			}
		}

		/**
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent arg0) {
			handleNewMonitoringSession();
		}
	}

	/**
	 * Load log action.
	 */
	private final class ActionLoadLog extends AbstractAction {
		public ActionLoadLog() {
			super();
			UIUtils.setUsabilityDtls(
					MessageRepository.get(Msg.ACTIONS_LOADLOG), this);
			ImageIcon icon = UIConfiguration.getIcon("load_log.gif");
			if(icon != null) {
				putValue(Action.SMALL_ICON, icon);
			}
		}

		/**
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent arg0) {
			handleLoadLogForPlayback();
		}
	}

	/**
	 * Event handler.
	 */
	private final class EventHandler implements ActionListener, MenuListener,
			MRUPopupMenuBuilder.Listener, MonitoringSessionRepository.Listener {
		/**
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent arg0) {
			Object source = arg0.getSource();
			if (source == getJMenuItemToolsJobManager()) {
				handleLaunchJobManager();
				return;
			}
		}

		/**
		 * @see javax.swing.event.MenuListener#menuSelected(javax.swing.event.MenuEvent)
		 */
		public void menuSelected(MenuEvent e) {
			handleMenuSelected(e);
		}

		/**
		 * @see javax.swing.event.MenuListener#menuDeselected(javax.swing.event.MenuEvent)
		 */
		public void menuDeselected(MenuEvent e) {
			// handleMenuDeselected(e);
		}

		/**
		 * @see javax.swing.event.MenuListener#menuCanceled(javax.swing.event.MenuEvent)
		 */
		public void menuCanceled(MenuEvent e) {
		}

		/**
		 * @see com.ixora.common.ui.MRUMenu.Callback#actionPerformed(java.lang.Object)
		 */
		public void actionPerformed(Object o) {
			handleLoadMonitoringSession((String) o);
		}

		/**
		 * @see com.ixora.rms.ui.session.MonitoringSessionRepository.Listener#sessionLoaded(com.ixora.rms.ui.session.MonitoringSessionDescriptor)
		 */
		public void sessionLoaded(MonitoringSessionDescriptor scheme) {
			handleMonitoringSessionLoaded(scheme);
		}
	}

	/**
	 * Main.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			showSplashScreen();
			initApplication();
		    EventQueue.invokeLater(new Runnable() {
		        public void run(){
		            fSplashScreen.dispose();
		            fSplashScreen = null;
		        }
		    });
		} catch(Exception e) {
			logger.error(e);
			System.exit(1);
		}
	}

	/**
	 * Show a simple graphical splash screen, as a quick preliminary to the main
	 * screen.
	 */
	private static void showSplashScreen() {
		fSplashScreen = new AppSplashScreen("/splash.gif");
		fSplashScreen.splash();
	}

	/**
	 * Display the main window of the application to the user.
	 * @throws FailedToSaveConfiguration
	 * @throws SocketException
	 * @throws RMSException
	 */
	private static void initApplication() throws SocketException, FailedToSaveConfiguration, RMSException {
		// first thing to do:
		// initialize the message repository, set the default
		// repository to the repository for the main application component
		MessageRepository.initialize(RMSComponent.NAME);

		// assign a public IP address for the console to use
		String currentConsoleIpAddress = ConfigurationMgr.getString(
				RMSComponent.NAME,
				RMSConfigurationConstants.NETWORK_ADDRESS);
		if(Utils.isEmptyString(currentConsoleIpAddress)) {
        	// tell RMI to use the fully qualified name for this host
        	// for object references
        	System.setProperty("java.rmi.server.useLocalHostname", "true");
		} else {
			System.setProperty("java.rmi.server.hostname", currentConsoleIpAddress);
		}

		// register deployment modules with the update manager
		UpdateMgr.registerModule(new IxoraCommonModule());
		UpdateMgr.registerModule(new RMSModule());
		UpdateMgr.registerNodeModule(new IxoraCommonModule());
		UpdateMgr.registerNodeModule(new RMSModule());

		// ConfigurationMgr.makeConfigurationEditable(PreferencesConfigurationConstants.PREFERENCES);
		ConfigurationMgr.makeConfigurationEditable(RMSComponent.NAME);
		ConfigurationMgr
				.makeConfigurationEditable(DataViewBoardComponent.NAME);
		ConfigurationMgr
				.makeConfigurationEditable(ChartsBoardComponent.NAME);
		ConfigurationMgr
				.makeConfigurationEditable(TablesBoardComponent.NAME);
		ConfigurationMgr
				.makeConfigurationEditable(PropertiesBoardComponent.NAME);
		ConfigurationMgr
				.makeConfigurationEditable(LogBoardComponent.NAME);
		ConfigurationMgr.makeConfigurationEditable(LogComponent.NAME);
//		ConfigurationMgr.makeConfigurationEditable(LogComponentDB.NAME);
		// ConfigurationMgr.makeConfigurationEditable(LogComponentXML.NAME);
		ConfigurationMgr
				.makeConfigurationEditable(MonitoringSessionRepositoryComponent.NAME);
		ConfigurationMgr.makeConfigurationEditable(JobsComponent.NAME);
		ConfigurationMgr.makeConfigurationEditable(ReactionsComponent.NAME);
		ConfigurationMgr.makeConfigurationEditable(ReactionsEmailComponent.NAME);
		// ConfigurationMgr.makeConfigurationEditable(UpdateComponent.NAME);

        if (System.getSecurityManager() == null) {
			System.setSecurityManager(new java.rmi.RMISecurityManager());
		}

		try {
			// initialize RMS
			RMS.initialize();
		} catch(Exception e) {
			// this could happen if a special IP address has been assigned to
			// the console has changed since last time the app was started
			if (e.getCause() instanceof ExportException
					|| Utils.getTrace(e).toString().contains("Port")) {
				logger.error("Ignoring the ip address assigned to the console "
								+ currentConsoleIpAddress
								+ " as it seems to be invalid.");
				resetConsoleIpAddress();
				// try again
				RMS.initialize();
			} else {
				throw new AppRuntimeException(e);
			}
		}

		// build the GUI on the event dispatch thread
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					// install UI factory in the commons library
					UIFactoryMgr.installUIFactory(new RMSUIFactory());
					// JDialog.setDefaultLookAndFeelDecorated(true);
					// JFrame.setDefaultLookAndFeelDecorated(true);
					// Toolkit.getDefaultToolkit().setDynamicLayout(true);
					Toolkit.getDefaultToolkit().setDynamicLayout(false);
					AppFrameParameters params = new AppFrameParameters();
					params.setString(
							AppFrameParameters.LOOK_AND_FEEL_CLASS,
							"javax.swing.plaf.metal.MetalLookAndFeel");
							//"com.jgoodies.looks.plastic.PlasticXPLookAndFeel");
					params.setString(AppFrameParameters.FEEDBACK_URL,
							"http://www.ixoragroup.com/rms/app_feedback.php");
					JFrame frame = new RMSFrame(params);
					UIUtils.maximizeFrameAndShow(frame);
				} catch(Throwable e) {
					logger.error(e);
					System.exit(1);
				}
			}
		});
	}

	/**
	 * Resets the ip address assigned to the console.
	 * @throws FailedToSaveConfiguration
	 */
	private static String resetConsoleIpAddress()
			throws FailedToSaveConfiguration {
		String na = "";
		ComponentConfiguration cconf = ConfigurationMgr.get(RMSComponent.NAME);
		// set both the current and the default value to the new address
		cconf.setDefaultValue(RMSConfigurationConstants.NETWORK_ADDRESS, na);
		cconf.setString(RMSConfigurationConstants.NETWORK_ADDRESS, na);
		ConfigurationMgr.save(RMSComponent.NAME);
		return na;
	}

	/**
	 * This is the default constructor
	 *
	 * @param params
	 * @throws Throwable
	 */
	public RMSFrame(AppFrameParameters params) throws Throwable {
		super(params);
		initialize();
	}

	/**
	 * @see com.ixora.rms.ui.RMSViewContainer#registerLeftPanel(javax.swing.JPanel)
	 */
	public void registerLeftComponent(JComponent panel) {
		try {
			getJSplitPane().setEnabled(true);
			getJSplitPane().setLeftComponent(panel);
			// TODO: hack to overcome a problem where the
			// divider is positioned at 0px
			if(getJSplitPane().getLastDividerLocation() == 0) {
				getJSplitPane().setDividerLocation(260);
			}
		} catch (Exception e) {
			UIExceptionMgr.userException(e);
		}
	}

	/**
	 * @see com.ixora.rms.ui.RMSViewContainer#registerRightPanel(javax.swing.JPanel)
	 */
	public void registerRightComponent(JComponent panel) {
		try {
			getJSplitPane().setEnabled(true);
			getJSplitPane().setRightComponent(panel);
		} catch (Exception e) {
			UIExceptionMgr.userException(e);
		}
	}

	/**
	 * @see com.ixora.rms.ui.RMSViewContainer#setSessionDirty(boolean)
	 */
	public void setSessionDirty(boolean b) {
		this.fDirtySession = b;
	}

	/**
	 * @see com.ixora.rms.ui.RMSViewContainer#isSessionDirty()
	 */
	public boolean isSessionDirty() {
		return this.fDirtySession;
	}

	/**
	 * @see com.ixora.rms.ui.RMSViewContainer#appendToTitle(java.lang.String)
	 */
	public void appendToTitle(String txt) {
		setTitle(MessageRepository.get(Msg.TITLE_FRAME_RMS) + ": " + txt);
	}

	/**
	 * @return void
	 * @throws HelpSetException
	 */
	private void initialize() throws RMSException, IOException,
			ClassNotFoundException, InstantiationException,
			IllegalAccessException, UnsupportedLookAndFeelException,
			HelpSetException {
		this.fEventHandler = new EventHandler(); // install license provider

		// install license provider
		//LicenseMgr.installProvider(new DefaultLicenseProvider(this));

		// add main frame as observer to the user preferences
		ConfigurationMgr.get(PreferencesConfigurationConstants.PREFERENCES)
				.addObserver(this);

		this.fMRUPopupMenuForButton = new MRUPopupMenuBuilder(
				getJButtonLoadScheme().getPopupMenu(), this.fEventHandler);
		this.fMRUPopupMenuForMenu = new MRUPopupMenuBuilder(
				getJMenuLoadSession().getPopupMenu(), this.fEventHandler);

		setTitle(MessageRepository.get(Msg.TITLE_FRAME_RMS));

		registerToolBarComponents(new JComponent[] { getJButtonLoadScheme(),
				getJButtonNewSession(), getJButtonLoadLog() });

		this.fMonitoringSessionRepository = new MonitoringSessionRepositoryImpl(
				this, UIConfiguration.getIcon("session_file.gif"),
				this.fEventHandler);
		getJMenuLoadSession().addMenuListener(this.fEventHandler);

		fJMenuBar.add(getJMenuTools(), 2);

		registerMenuItemsForFileMenu(new JMenuItem[] { getJMenuLoadSession(),
				getJMenuItemNewSession(), getJMenuItemLoadLog() });
	}

	/**
	 * @see com.ixora.common.ui.AppFrame#prepareHelp()
	 */
	protected void prepareHelp() throws MalformedURLException, HelpSetException {
		HelpMgr.initialize(this, "help/jhelpset.hs", RMSHelpItems.ABOUT,
				getJMenuItemHelpLaunch(), null);
	}

	/**
	 * @see java.awt.Component#setVisible(boolean)
	 */
	public void setVisible(boolean b) {
		super.setVisible(b);

		if (b && !fLicenseChecked) {
			// check license
			try {
//				LicenseMgr.checkLicense();
//				fStatusBar.checkLicense();
				fLicenseChecked = true;
				// do a quick ping to website
				UpdateMgr.checkForUpdates("http://www.ixoragroup.com/rms/app_update_check.php");
			} catch (Exception e) {
				UIExceptionMgr.userException(e);
				handleClose();
			}
		}
	}

	/**
	 * @return javax.swing.JButton
	 */
	private javax.swing.JButton getJButtonNewSession() {
		if (fButtonNewSession == null) {
			fButtonNewSession = UIFactoryMgr
					.createButton(this.fActionNewSession);
			fButtonNewSession.setText(null);
			fButtonNewSession.setMnemonic(KeyEvent.VK_UNDEFINED);
		}
		return fButtonNewSession;
	}

	/**
	 * @return javax.swing.JButton
	 */
	private ButtonWithPopup getJButtonLoadScheme() {
		if (fButtonLoadSession == null) {
			fButtonLoadSession = new ButtonWithPopupLoadScheme(
					this.fActionLoadSession);
			fButtonLoadSession.setText(null);
			fButtonLoadSession.setMnemonic(KeyEvent.VK_UNDEFINED);
		}
		return fButtonLoadSession;
	}

	/**
	 * @return javax.swing.JButton
	 */
	private javax.swing.JButton getJButtonLoadLog() {
		if (fButtonLoadLog == null) {
			fButtonLoadLog = UIFactoryMgr.createButton(this.fActionLoadLog);
			fButtonLoadLog.setText(null);
			fButtonLoadLog.setMnemonic(KeyEvent.VK_UNDEFINED);
		}
		return fButtonLoadLog;
	}

	/**
	 * @return javax.swing.JMenuItem
	 */
	private javax.swing.JMenuItem getJMenuItemLoadSchemeBrowse() {
		if (fMenuItemLoadSessionBrowse == null) {
			fMenuItemLoadSessionBrowse = UIFactoryMgr
					.createMenuItem(this.fActionBrowseForScheme);
		}
		return fMenuItemLoadSessionBrowse;
	}

	/**
	 * @return javax.swing.JMenuItem
	 */
	private javax.swing.JMenuItem getJMenuItemNewSession() {
		if (fMenuItemNewSession == null) {
			fMenuItemNewSession = UIFactoryMgr
					.createMenuItem(this.fActionNewSession);
		}
		return fMenuItemNewSession;
	}

	/**
	 * @return javax.swing.JMenuItem
	 */
	private javax.swing.JMenu getJMenuLoadSession() {
		if (fMenuLoadSession == null) {
			fMenuLoadSession = UIFactoryMgr.createMenu(this.fActionLoadSession);
		}
		return fMenuLoadSession;
	}

	/**
	 * @return javax.swing.JMenuItem
	 */
	private javax.swing.JMenuItem getJMenuItemLoadLog() {
		if (fMenuItemLoadLog == null) {
			fMenuItemLoadLog = UIFactoryMgr.createMenuItem(this.fActionLoadLog);
		}
		return fMenuItemLoadLog;
	}

	/**
	 * @return javax.swing.JSplitPane
	 */
	private javax.swing.JSplitPane getJSplitPane() {
		if (fSplitPane == null) {
			fSplitPane = UIFactoryMgr.createSplitPane("horizontal1");
			fSplitPane.setLeftComponent(null);
			fSplitPane.setRightComponent(getJDesktopPane());
			fSplitPane.setEnabled(false);
		}
		return fSplitPane;
	}

	/**
	 * @return javax.swing.JSplitPane
	 */
	private javax.swing.JDesktopPane getJDesktopPane() {
		if (fDesktopPane == null) {
			fDesktopPane = new javax.swing.JDesktopPane();
		}
		return fDesktopPane;
	}

	/**
	 * @return javax.swing.JMenu
	 */
	private javax.swing.JMenu getJMenuTools() {
		if (fMenuTools == null) {
			fMenuTools = UIFactoryMgr.createMenu();
			fMenuTools.add(getJMenuItemToolsProviderManager());
			fMenuTools.add(getJMenuItemToolsAgentInstaller());
			UIUtils.setUsabilityDtls(MessageRepository.get(Msg.MENU_TOOLS),
					fMenuTools);
			fMenuTools.add(getJMenuItemToolsJobManager());
		}
		return fMenuTools;
	}

	/**
	 * @return javax.swing.JMenuItem
	 */
	private javax.swing.JMenuItem getJMenuItemToolsJobManager() {
		if (fMenuItemToolsJobManager == null) {
			fMenuItemToolsJobManager = UIFactoryMgr
					.createMenuItem(new ActionLaunchJobManager());
		}
		return fMenuItemToolsJobManager;
	}

	/**
	 * @return javax.swing.JMenuItem
	 */
	private javax.swing.JMenuItem getJMenuItemToolsProviderManager() {
		if (fMenuItemToolsProviderManager == null) {
			fMenuItemToolsProviderManager = UIFactoryMgr
					.createMenuItem(new ActionLaunchProviderManager());
		}
		return fMenuItemToolsProviderManager;
	}

	/**
	 * @return javax.swing.JMenuItem
	 */
	private javax.swing.JMenuItem getJMenuItemToolsAgentInstaller() {
		if (fMenuItemToolsAgentInstaller == null) {
			fMenuItemToolsAgentInstaller = UIFactoryMgr
					.createMenuItem(new ActionLaunchAgentInstaller());
		}
		return fMenuItemToolsAgentInstaller;
	}

	/**
	 * Handles the browse for session.
	 */
	private void handleBrowseForSession() {
		try {
			resetCurrentView();
			fMonitoringSessionRepository.loadSession();
		} catch (Exception e) {
			UIExceptionMgr.userException(e);
		}
	}

	/**
	 * Handles the load session action event.
	 */
	private void handleLoadMonitoringSessionAction(ActionEvent e) {
		handleBrowseForSession();
	}

	/**
	 * Loads the session with the given name.
	 *
	 * @param session
	 */
	private void handleLoadMonitoringSession(final String session) {
		try {
			if (resetCurrentView()) {
				return;
			}
			fMonitoringSessionRepository.loadSession(session);
		} catch (Exception ex) {
			UIExceptionMgr.userException(ex);
		}
	}

	/**
	 * Handles the load log event.
	 */
	private void handleLoadLogForPlayback() {
		try {
			if (resetCurrentView()) {
				return;
			}
			// run this job synchronously as
			// it does UI work
			this.fWorker.runJobSynch(new UIWorkerJobDefault(this,
					Cursor.WAIT_CURSOR, MessageRepository
							.get(Msg.TEXT_LOADING_LOG_VIEW)) {
				public void work() throws Throwable {
					LogChooser logChooser = new LogChooserImpl(
					        RMSFrame.this);
					LogRepositoryInfo log = logChooser.getLogInfoForRead();
					if(log != null) {
						fCurrentView = new LogPlaybackView(RMSFrame.this, log);
					}
				}

				public void finished(Throwable ex) {
					if (ex != null) {
						resetCurrentView();
						UIExceptionMgr.userException(ex);
					}
				}
			});
			if (fCurrentView != null) {
				fCurrentView.initialize();
			}
		} catch (Throwable e) {
			resetCurrentView();
			UIExceptionMgr.userException(e);
		}
	}

	/**
	 * Handles the new session event.
	 */
	private void handleNewMonitoringSession() {
		try {
			// try to close current view
			if (resetCurrentView()) {
				return;
			}
			// run this job synchronously as
			// it does UI work
			this.fWorker.runJobSynch(new UIWorkerJobDefault(this,
					Cursor.WAIT_CURSOR, MessageRepository
							.get(Msg.TEXT_BEGININGNEWSESSION)) {
				public void work() throws Throwable {
					fCurrentView = new LiveSessionView(getHostMonitor(),
							fMonitoringSessionRepository, null, RMSFrame.this);
				}

				public void finished(Throwable ex) {
					if (ex != null) {
						resetCurrentView();
						UIExceptionMgr.userException(ex);
					}
				}
			});
			if (fCurrentView != null) {
				fCurrentView.initialize();
			}
		} catch (Exception e) {
			resetCurrentView();
			UIExceptionMgr.userException(e);
		}
	}

	/**
	 * Handles menu selected event.
	 *
	 * @param e
	 */
	private void handleMenuSelected(MenuEvent e) {
		try {
			JMenu m = getJMenuLoadSession();
			if (m == e.getSource()) {
				buildLoadSchemeMruPopupMenu(this.fMRUPopupMenuForMenu);
			}
		} catch (Exception ex) {
			UIExceptionMgr.userException(ex);
		}
	}

	/**
	 * @param scheme
	 */
	private void handleMonitoringSessionLoaded(
			MonitoringSessionDescriptor scheme) {
		try {
			if(resetCurrentView()) {
				return;
			}
			fCurrentView = new LiveSessionView(getHostMonitor(),
					fMonitoringSessionRepository, scheme, this);
			fCurrentView.initialize();
		} catch (Throwable e) {
			resetCurrentView();
			UIExceptionMgr.userException(e);
		}
	}

	/**
	 * Sets up the monitoring session MRU menu.
	 */
	private void buildLoadSchemeMruPopupMenu(MRUPopupMenuBuilder menu) {
		String[] mru = this.fMonitoringSessionRepository.getMostRecentlyUsed();
		if (!Utils.isEmptyArray(mru)) {
			menu.setList(mru);
			if (menu == this.fMRUPopupMenuForButton) {
				getJButtonLoadScheme().addSeparator();
			} else if (menu == this.fMRUPopupMenuForMenu) {
				getJMenuLoadSession().addSeparator();
			}
		}
		if (menu == this.fMRUPopupMenuForButton) {
			getJButtonLoadScheme().add(getJMenuItemLoadSchemeBrowse());
		} else if (menu == this.fMRUPopupMenuForMenu) {
			getJMenuLoadSession().add(getJMenuItemLoadSchemeBrowse());
		}
	}

	/**
	 * Resets the current view.
	 *
	 * @return true if the current view vetoed the close request
	 */
	protected boolean resetCurrentView() {
		boolean veto = super.resetCurrentView();
		if (veto) {
			return true;
		}
		JSplitPane p = getJSplitPane();
		p.setLeftComponent(null);
		p.setEnabled(false);
		p.setRightComponent(getJDesktopPane());
		setTitle(MessageRepository.get(Msg.TITLE_FRAME_RMS));
		fDirtySession = false;
		return false;
	}

	/**
	 * Launches the job manager tool.
	 */
	private void handleLaunchJobManager() {
		try {
			JDialog jd = new JobManagerDialog(this, getJobEngine(), getJobLibrary(),
					getHostMonitor());
			jd.setSize(600, 450);
			jd.setModal(false);
			UIUtils.centerDialogAndShow(this, jd);
		} catch (Throwable e) {
			UIExceptionMgr.userException(e);
		}
	}

	/**
	 * Launches the provider manager tool.
	 */
	private void handleLaunchProviderManager() {
		try {
			JDialog jd = new ProviderInstanceManagerDialog(this,
					getAgentRepository(), getProviderInstanceRepository(),
					getProviderRepository(), getParserRepository());
			jd.setModal(true);
			UIUtils.centerDialogAndShow(this, jd);
		} catch (Exception e) {
			UIExceptionMgr.userException(e);
		}
	}

	/**
	 * Launches the agent installer tool.
	 */
	private void handleLaunchAgentInstaller() {
		try {
			JDialog jd = new AgentInstallerDialog(this, getAgentRepository(),
					getAgentTemplateRepository(), getAgentInstaller());
			jd.setModal(true);
			UIUtils.centerDialogAndShow(this, jd);
		} catch (Exception e) {
			UIExceptionMgr.userException(e);
		}
	}

	/**
	 * Lazily creates the job engine reference.
	 *
	 * @return
	 * @throws RemoteException
	 */
	private JobEngineService getJobEngine() throws Throwable {
		if (fRMSJobEngine == null) {
			fRMSJobEngine = RMS.getJobEngine();
		}
		return fRMSJobEngine;
	}

	/**
	 * Lazily creates the job library reference.
	 *
	 * @return
	 * @throws RemoteException
	 */
	private JobLibraryService getJobLibrary() throws Throwable {
		if (fRMSJobLibrary == null) {
			fRMSJobLibrary = RMS.getJobLibrary();
		}
		return fRMSJobLibrary;
	}

	/**
	 * Lazily creates the agent repository reference.
	 *
	 * @return
	 */
	private AgentRepositoryService getAgentRepository() {
		if (fRMSAgentRepository == null) {
			fRMSAgentRepository = RMS.getAgentRepository();
		}
		return fRMSAgentRepository;
	}

	/**
	 * Lazily creates the agent template repository reference.
	 *
	 * @return
	 * @throws FileNotFoundException
	 * @throws XMLException
	 */
	private AgentTemplateRepositoryService getAgentTemplateRepository()
			throws XMLException, FileNotFoundException {
		if (fRMSAgentTemplateRepository == null) {
			fRMSAgentTemplateRepository = RMS.getAgentTemplateRepository();
		}
		return fRMSAgentTemplateRepository;
	}

	/**
	 * Lazily creates the agent installer.
	 *
	 * @return
	 */
	private AgentInstallerService getAgentInstaller() {
		if (fRMSAgentInstaller == null) {
			fRMSAgentInstaller = RMS.getAgentInstaller();
		}
		return fRMSAgentInstaller;
	}

	/**
	 * Lazily creates the provider repository reference.
	 *
	 * @return
	 */
	private ProviderRepositoryService getProviderRepository() {
		if (fRMSProviderRepository == null) {
			fRMSProviderRepository = RMS.getProviderRepository();
		}
		return fRMSProviderRepository;
	}

	/**
	 * Lazily creates the provider repository reference.
	 *
	 * @return
	 */
	private ProviderInstanceRepositoryService getProviderInstanceRepository() {
		if (fRMSProviderInstanceRepository == null) {
			fRMSProviderInstanceRepository = RMS
					.getProviderInstanceRepository();
		}
		return fRMSProviderInstanceRepository;
	}

	/**
	 * Lazily creates the provider repository reference.
	 *
	 * @return
	 */
	private ParserRepositoryService getParserRepository() {
		if (fRMSParserRepository == null) {
			fRMSParserRepository = RMS.getParserRepository();
		}
		return fRMSParserRepository;
	}

	/**
	 * Lazily creates the host monitor.
	 *
	 * @return
	 */
	private HostMonitorService getHostMonitor() {
		if (fRMSHostMonitor == null) {
			fRMSHostMonitor = RMS.getHostMonitor();
		}
		return fRMSHostMonitor;
	}

	/**
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	public void update(Observable o, Object arg) {
	}

	/**
	 * @see com.ixora.common.ui.AppFrame#getDisplayPanel()
	 */
	protected JComponent getDisplayPanel() {
		return getJSplitPane();
	}
}
