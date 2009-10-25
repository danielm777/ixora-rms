package com.ixora.common.ui;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.metal.MetalTheme;

import com.ixora.common.MessageRepository;
import com.ixora.common.Product;
import com.ixora.common.logging.AppLogger;
import com.ixora.common.logging.AppLoggerFactory;
import com.ixora.common.messages.Msg;
import com.ixora.common.os.OSUtils;
import com.ixora.common.security.license.LicenseMgr;
import com.ixora.common.security.license.ui.ShowLicense;
import com.ixora.common.ui.help.AppHelp;
import com.ixora.common.ui.help.AppHelpMgr;
import com.ixora.common.ui.jobs.UIWorker;
import com.ixora.common.ui.jobs.UIWorkerDefault;
import com.ixora.common.ui.jobs.UIWorkerJobDefault;
import com.ixora.common.ui.preferences.PreferencesConfiguration;
import com.ixora.common.utils.Utils;

/*
 * Created on 25-Nov-03
 */

/**
 * @author Daniel Moraru
 */
public abstract class AppFrame extends JFrame implements AppViewContainer {
	private static final long serialVersionUID = -586264475410585633L;
	/** Logger */
	private static final AppLogger logger = AppLoggerFactory.getLogger(AppFrame.class);
	// Components
	protected javax.swing.JPanel fContentPane;
	protected javax.swing.JMenuBar fJMenuBar;
	protected javax.swing.JMenu fMenuFile;
	protected javax.swing.JMenuItem fMenuItemFileExit;
	protected javax.swing.JMenuItem fMenuItemHelpAbout;
    protected javax.swing.JMenuItem fMenuItemHelpLicense;
	protected javax.swing.JMenuItem fMenuItemConfigurationSettings;
	protected javax.swing.JMenuItem fMenuItemConfigurationToggleToolBar;
	protected javax.swing.JMenuItem fMenuItemConfigurationToggleStatusBar;
	protected javax.swing.JToolBar fToolBar;
	protected javax.swing.JLabel fLabelStatusBar;
	protected javax.swing.JMenu fMenuActions;
	protected javax.swing.JMenu fMenuConfiguration;
	protected javax.swing.JMenu fMenuHelp;
	protected javax.swing.JMenuItem fMenuItemHelpLaunch;
	protected javax.swing.JMenuItem fMenuItemHelpFeedback;
	protected NonFatalErrorViewerDialog fNonFatalErrorViewerDialog;
	/** Worker */
	protected UIWorker fWorker;
	/** Current view */
	protected AppView fCurrentView;
	/** Status bar */
	protected AppStatusBarDefault fStatusBar;
	/** Event hub */
	protected AppEventHub fEventHub;
	/** Help */
	protected AppHelp fHelp;
    /** Log with non fatal errors */
	protected NonFatalErrorBuffer fNonFatalErrorBuffer;
	/** Feedback url */
	protected URL fFeedbackURL;
	/** The text pane that shows the hyper-link to open the error log */
	protected JEditorPane fTextPaneErrorLog;
	/** Private event handler */
	private EventHandler fEventHandler;

	private final class EventHandler extends WindowAdapter implements MouseListener, 
			UIExceptionHandler, ActionListener, NonFatalErrorHandler, HyperlinkListener,
			AppStatusBar.Listener {
		/**
		 * @see com.ixora.common.ui.UIExceptionHandler#exception(java.lang.Throwable)
		 */
		public void exception(Throwable e) {
			handleException(e);
		}
		/**
		 * @see com.ixora.common.ui.UIExceptionHandler#userException(java.lang.Throwable)
		 */
		public void userException(Throwable e) {
			handleUserException(e);
		}
        /**
         * @see com.ixora.common.ui.UIExceptionHandler#userException(java.awt.Frame, java.lang.Throwable)
         */
        public void userException(Frame owner, Throwable e) {
            handleUserException(owner, e);
        }
        /**
         * @see com.ixora.common.ui.NonFatalErrorHandler#nonFatalError(java.lang.String, java.lang.Throwable)
         */
        public void nonFatalError(String error, Throwable t) {
            handleNonFatalError(error, t);
        }
		/**
		 * @see java.awt.event.WindowListener#windowClosing(java.awt.event.WindowEvent)
		 */
		public void windowClosing(WindowEvent e) {
			handleClose();
		}
		/**
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent e) {
			Object source = e.getSource();
			if(source == getJMenuItemConfigurationToggleStatusBar()) {
				handleToggleStatusBar();
				return;
			}
			if(source == getJMenuItemConfigurationToggleToolBar()) {
				handleToggleToolBar();
				return;
			}
			if(source == getJMenuItemFileExit()) {
				handleClose();
				return;
			}
			if(source == getJMenuItemHelpAbout()) {
				handleAbout();
				return;
			}
            if(source == getJMenuItemHelpLicense()) {
                handleLicense();
                return;
            }
			if(source == getJMenuItemConfigurationSettings()) {
				handleSettings();
				return;
			}
			if(source == getJMenuItemHelpFeedback()) {
				handleSendFeedback();
				return;
			}
		}
		/**
         * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
		 */
        public void mouseClicked(MouseEvent e) {
            Object source = e.getSource();
            if(source == getAppStatusBar().getErrorLabel() && e.getClickCount() == 2) {
                handleShowNonFatalErrorLog();
                return;
            }
        }
        public void mousePressed(MouseEvent e) {
        }
        public void mouseReleased(MouseEvent e) {
        }
        public void mouseEntered(MouseEvent e) {
        }
        public void mouseExited(MouseEvent e) {
        }
		public void hyperlinkUpdate(HyperlinkEvent e) {
			if(e.getSource() == fTextPaneErrorLog && e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
				handleShowNonFatalErrorLog();
			}
		}
		public void errorMessageChanged(String newMessage, Throwable error) {
			handleErrorMessage(newMessage, error);
		}
		public void messageChanged(String newMessage) {
		}
		public void stateMessageChanged(String newMessage) {
		}
	}

	/**
	 * @param params
	 * @throws Throwable
	 */
	public AppFrame(AppFrameParameters params) throws Throwable {
		super();
		initialize(params);
	}
	
	

    /**
     * Shows the error log.
     */
	protected void handleShowNonFatalErrorLog() {
        try {
            if(this.fNonFatalErrorViewerDialog == null) {
                this.fNonFatalErrorViewerDialog = new NonFatalErrorViewerDialog(this);
            }
            this.fNonFatalErrorViewerDialog.setData(fNonFatalErrorBuffer);
            if(!this.fNonFatalErrorViewerDialog.isVisible()) {
                UIUtils.centerDialogAndShow(this, this.fNonFatalErrorViewerDialog);
            }
        } catch(Exception e) {
            UIExceptionMgr.userException(e);
        }
    }

    /**
	 * @see com.ixora.common.ui.AppViewContainer#getAppFrame()
	 */
	public JFrame getAppFrame() {
		return this;
	}

	/**
	 * @see AppViewContainer#registerToolBarComponents(java.swingx.JComponent[])
	 */
	public void registerToolBarComponents(JComponent[] components) {
		try {
			unregisterToolBarComponents(components);
			JToolBar top = getJToolBar();
			JToolBar container = UIFactoryMgr.createToolBar();
			container.setFloatable(false);
			JComponent comp;
			if(top.getComponentCount() > 0) {
				container.addSeparator();
			}
			for (int i = 0; i < components.length; i++) {
				comp = components[i];
				container.add(comp);
			}
			top.add(container);
			top.validate();
			top.repaint();
		} catch(Exception e) {
			UIExceptionMgr.userException(e);
		}
	}

	/**
	 * @see com.ixora.common.ui.AppViewContainer#registerToolBarButtons(javax.swing.AbstractButton[])
	 */
	public void registerToolBarButtons(AbstractButton[] buttons) {
		if(!Utils.isEmptyArray(buttons)) {
			for(AbstractButton abstractButton : buttons) {
				abstractButton.setText(null);
			}
		}
		registerToolBarComponents(buttons);
	}
	
	/**
	 * @see AppViewContainer#registerMenuItemsForActionsMenu(javax.swing.JMenuItem[])
	 */
	public void registerMenuItemsForActionsMenu(JMenuItem[] items) {
		try {
			unregisterMenuItemsForActionsMenu(items);
			if(!fMenuActions.isVisible()) {
				fMenuActions.setVisible(true);
			}
			int cc = fMenuActions.getComponentCount();
			if(cc > 0) {
				this.fMenuActions.addSeparator();
			}
			for (int i = 0; i < items.length; i++) {
				JMenuItem mi = items[i];
				mi.setToolTipText(null);
				this.fMenuActions.add(mi);
			}
		} catch(Exception e) {
			UIExceptionMgr.userException(e);
		}
	}

	/**
	 * @see com.ixora.common.ui.AppViewContainer#registerMenuItemsForFileMenu(javax.swing.JMenuItem[])
	 */
	public void registerMenuItemsForFileMenu(JMenuItem[] items) {
		try {
			unregisterMenuItemsForFileMenu(items);
			this.fMenuFile.remove(getJMenuItemFileExit());
			int cc = fMenuFile.getComponentCount();
			if(cc > 0) {
				this.fMenuFile.remove(cc - 1);
			}
			if(cc > 1) {
				this.fMenuFile.addSeparator();
			}
			for (int i = 0; i < items.length; i++) {
				JMenuItem mi = items[i];
				mi.setToolTipText(null);
				this.fMenuFile.add(mi);
			}
			this.fMenuFile.addSeparator();
			this.fMenuFile.add(getJMenuItemFileExit());
		} catch(Exception e) {
			UIExceptionMgr.userException(e);
		}
	}

	/**
	 * @see com.ixora.common.ui.AppViewContainer#unregisterMenuItemsForFileMenu(javax.swing.JMenuItem[])
	 */
	public void unregisterMenuItemsForFileMenu(JMenuItem[] items) {
		try {
			int pos;
			JMenuItem mi;
			JPopupMenu pu = this.fMenuFile.getPopupMenu();
			int count;
			for(int i = 0; i < items.length; i++) {
				mi = items[i];
				count = pu.getComponentCount();
				pos = pu.getComponentIndex(mi);
				if(pos >= 1 && pos <= count - 2) {
					Component c = pu.getComponent(pos - 1);
					if(c instanceof JPopupMenu.Separator) {
						pu.remove(pos - 1);
					} else {
						c = pu.getComponent(pos + 1);
						if(c instanceof JPopupMenu.Separator) {
							pu.remove(pos + 1);
						}
					}
				}
				this.fMenuFile.remove(items[i]);
			}
		} catch(Exception e) {
			UIExceptionMgr.userException(e);
		}
	}

	/**
	 * @see AppViewContainer#unregisterMenuItemsForActionsMenu(javax.swing.JMenuItem[])
	 */
	public void unregisterMenuItemsForActionsMenu(JMenuItem[] items) {
		try {
			int pos;
			JMenuItem mi;
			JPopupMenu pu = this.fMenuActions.getPopupMenu();
			int count;
			for(int i = 0; i < items.length; i++) {
				mi = items[i];
				count = pu.getComponentCount();
				pos = pu.getComponentIndex(mi);
				if(pos >= 1 && pos <= count - 2) {
					Component c = pu.getComponent(pos - 1);
					if(c instanceof JPopupMenu.Separator) {
						pu.remove(pos - 1);
					} else {
						c = pu.getComponent(pos + 1);
						if(c instanceof JPopupMenu.Separator) {
							pu.remove(pos + 1);
						}
					}
				}
				this.fMenuActions.remove(items[i]);
			}
			hideMenusWithNoItems();
		} catch(Exception e) {
			UIExceptionMgr.userException(e);
		}
	}

	/**
	 * @see com.ixora.common.ui.AppViewContainer#registerMenuItemsForMenu(javax.swing.JMenu, javax.swing.JMenuItem[])
	 */
	public void registerMenuItemsForMenu(JMenu menu, JMenuItem[] items) {
		try {
			unregisterMenuItemsForMenu(menu, items);
			if(!menu.isVisible()) {
				menu.setVisible(true);
			}
			int cc = menu.getComponentCount();
			if(cc > 0) {
				menu.remove(cc - 1);
			}
			if(cc > 1) {
				menu.addSeparator();
			}
			for (int i = 0; i < items.length; i++) {
				JMenuItem mi = items[i];
				mi.setToolTipText(null);
				menu.add(mi);
			}
		} catch(Exception e) {
			UIExceptionMgr.userException(e);
		}
	}

	/**
	 * @see com.ixora.common.ui.AppViewContainer#unregisterMenuItemsForMenu(javax.swing.JMenu, javax.swing.JMenuItem[])
	 */
	public void unregisterMenuItemsForMenu(JMenu menu, JMenuItem[] items) {
		try {
			int pos;
			JMenuItem mi;
			JPopupMenu pu = menu.getPopupMenu();
			int count;
			for(int i = 0; i < items.length; i++) {
				mi = items[i];
				count = pu.getComponentCount();
				pos = pu.getComponentIndex(mi);
				if(pos >= 1 && pos <= count - 2) {
					Component c = pu.getComponent(pos - 1);
					if(c instanceof JPopupMenu.Separator) {
						pu.remove(pos - 1);
					} else {
						c = pu.getComponent(pos + 1);
						if(c instanceof JPopupMenu.Separator) {
							pu.remove(pos + 1);
						}
					}
				}
				menu.remove(items[i]);
			}
			// hide menu with no menu items
			if(menu.getComponentCount() == 0) {
				menu.setVisible(false);
			}
		} catch(Exception e) {
			UIExceptionMgr.userException(e);
		}
	}

	/**
	 * @see AppViewContainer#unregisterToolBarButtons(javax.swing.JComponent[])
	 */
	public void unregisterToolBarComponents(JComponent[] components) {
		try {
			// find toolbar container
			Component[] comps = this.fToolBar.getComponents();
			JToolBar container;
			Component comp;
			for(int i = 0; i < comps.length; i++) {
				comp = comps[i];
				if(comp instanceof JToolBar) {
					container = (JToolBar)comp;
					if(container.isAncestorOf(components[0])) {
						container.removeAll();
						this.fToolBar.remove(container);
						break;
					}
				}
			}
			this.fToolBar.validate();
			this.fToolBar.repaint();
		} catch(Exception e) {
			UIExceptionMgr.userException(e);
		}
	}

	/**
	 * @see com.ixora.rms.ui.AppViewContainer#getAppStatusBar()
	 */
	public AppStatusBar getAppStatusBar() {
		return this.fStatusBar;
	}

	/**
	 * @see com.ixora.common.ui.AppViewContainer#getAppEventHub()
	 */
	public AppEventHub getAppEventHub() {
		return this.fEventHub;
	}

	/**
	 * @see com.ixora.common.ui.AppViewContainer#getAppWorker()
	 */
	public UIWorker getAppWorker() {
		return this.fWorker;
	}

	
	/**
	 * @see com.ixora.common.ui.AppViewContainer#getAppHelp()
	 */
	public AppHelp getAppHelp() {
		return this.fHelp;
	}

	/**
	 * @see com.ixora.common.ui.AppViewContainer#getAppView()
	 */
	public AppView getAppView() {
		return fCurrentView;
	}

	/**
	 * @param txt
	 * @param t
	 */
	private void handleErrorMessage(String txt, Throwable t) {
        if(txt != null) {
            fNonFatalErrorBuffer.nonFatalError(txt, t);
            logger.error(txt, t);
        }
		if(fTextPaneErrorLog != null) {
			fTextPaneErrorLog.setText("<html>&nbsp;&nbsp;<a href='#'>" 
					+ Utils.getTranslatedMessage(Msg.COMMON_UI_TEXT_ERROR_LOG)
					+ "</a>&nbsp;&nbsp;</html>");
		}
	}

	/**
	 * @see com.ixora.rms.ui.AppViewContainer#appendToAppFrameTitle(java.lang.String)
	 */
	public void appendToAppFrameTitle(String txt) {
		String title = getTitle();
		int idx = title.indexOf(':');
		if(idx > 0) {
			title = title.substring(0, idx);
		}
		setTitle(title + ": " + txt);
	}

	/**
	 * Handles non-user exceptions.
	 * @param e
	 */
	protected void handleException(Throwable e) {
		logger.error(e);
	}

    /**
     * Handles a non fatal error; the default implementation uses
     * <code>setErrorMessage(java.lang.String)</code> as a handler.
     * @param t
     * @param e
     */
    protected void handleNonFatalError(String error, Throwable t) {
        try {
            handleErrorMessage(error, t);
        } catch(Exception e) {
            UIExceptionMgr.exception(e);
        }
    }

	/**
	 * Handles user exceptions.
	 * @param e
	 */
	protected void handleUserException(Throwable e) {
		logger.error(e);
		ShowException.show(this, e);
	}

    /**
     * Handles user exceptions.
     * @param owner
     * @param e
     */
    protected void handleUserException(Frame owner, Throwable e) {
    	logger.error(e);
        ShowException.show(owner, e);
    }

	/**
	 * @param lookAndFeelClass
	 * @param mainComponent
	 * @return void
	 * @throws Throwable
	 */
	protected void initialize(AppFrameParameters params) throws Throwable {
		this.fEventHandler = new EventHandler();
		this.fEventHub = new AppEventHubDefault();
		// first thing to do
		UIExceptionMgr.installExceptionHandler(this.fEventHandler);
		// Note: The following lines setting the theme must be before any
		// UI element is constructed
		// Add UI switch listener
		UIManager.addPropertyChangeListener(new LookAndFeelSwitch(getRootPane()));

		setUpLookAndFeel(params);

		int toolTipDismissDelay = UIConfiguration.getToolTipDismissDelay();
		if(toolTipDismissDelay > 0) {
			ToolTipManager.sharedInstance().setDismissDelay(toolTipDismissDelay);
		}

        String url = params.getString(AppFrameParameters.FEEDBACK_URL);
        if(!Utils.isEmptyString(url)) {
        	fFeedbackURL = new URL(url);
        }

		createStatusBar();
		createUIWorker();

		setIconImage(UIConfiguration.getIcon("application.gif").getImage());
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(this.fEventHandler);
		getAppStatusBar().getErrorLabel().addMouseListener(fEventHandler);
        setContentPane(getJContentPane());
		setJMenuBar(getJJMenuBar());
		
		setTitle();
		
		fHelp = new AppHelpMgr(this, getJMenuItemHelpLaunch());

        fNonFatalErrorBuffer = new NonFatalErrorBuffer(
                params.getInt(AppFrameParameters.NON_FATAL_ERRORS_BUFFER_SIZE));
	}

	/**
	 * Sets the frame title.
	 */
	private void setTitle() {
		String title = MessageRepository.get(Msg.COMMON_UI_APP_FRAME_TITLE);
		if(title.equals("{0}")) {
			title = Product.getProductInfo().getName();
		}
		setTitle(title);
	}

	/**
	 * Sets up loo and feel.
	 * @param
	 * @throws Exception
	 */
	protected void setUpLookAndFeel(AppFrameParameters params) throws Exception {
		MetalTheme myTheme = new PropertiesTheme(getClass()
				.getResourceAsStream(OSUtils.isOs(OSUtils.WINDOWS) ? "/theme_win.thm" : "/theme.thm"));
		MetalLookAndFeel.setCurrentTheme(myTheme);
		String lookAndFeelClass = params.getString(AppFrameParameters.LOOK_AND_FEEL_CLASS);
		if(lookAndFeelClass != null) {
			UIManager.setLookAndFeel(lookAndFeelClass);
		}
	}

	/**
	 * Allows subclasses to replace the default status bar.
	 */
	protected void createStatusBar() {
		this.fStatusBar = new AppStatusBarDefault(UIConfiguration.getIcon("warning.gif"));
		fTextPaneErrorLog = UIFactoryMgr.createHtmlPane();
		fTextPaneErrorLog.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
		fTextPaneErrorLog.addHyperlinkListener(fEventHandler);
		this.fStatusBar.insertStatusBarComponent(fTextPaneErrorLog,
				AppStatusBarDefault.COMPONENT_ERROR, AppStatusBarDefault.POSITION_AFTER);
		this.fStatusBar.addListener(fEventHandler);
	}

	/**
	 * Allows subclasses to replace the default ui worker.
	 * @throws Throwable
	 * @throws StartableError
	 */
	protected void createUIWorker() throws Throwable {
		this.fWorker = new UIWorkerDefault(
                this.fEventHandler,
				this.fStatusBar.getMessageLabel(),
				this.fStatusBar.getProgressBar());
	}

	/**
	 * @return javax.swing.JPanel
	 */
	protected javax.swing.JPanel getJContentPane() {
		if (fContentPane == null) {
			fContentPane = new javax.swing.JPanel();
			fContentPane.setLayout(new java.awt.BorderLayout());
			fContentPane.add(fStatusBar, java.awt.BorderLayout.SOUTH);
			fContentPane.add(getJToolBar(), java.awt.BorderLayout.NORTH);
			fContentPane.add(getDisplayPanel(), java.awt.BorderLayout.CENTER);
		}
		return fContentPane;
	}

	/**
	 * @return the main componentn the frame
	 */
	protected abstract JComponent getDisplayPanel();

	/**
	 * @return javax.swing.JMenuBar
	 */
	protected javax.swing.JMenuBar getJJMenuBar() {
		if(fJMenuBar == null) {
			fJMenuBar = UIFactoryMgr.createMenuBar();
			fJMenuBar.add(getJMenuFile());
			fJMenuBar.add(getJMenuActions());
			fJMenuBar.add(getJMenuConfiguration());
			fJMenuBar.add(getJMenuHelp());
		}
		return fJMenuBar;
	}

	/**
	 * @return javax.swing.JMenu
	 */
	protected javax.swing.JMenu getJMenuFile() {
		if(fMenuFile == null) {
			fMenuFile = UIFactoryMgr.createMenu();
			fMenuFile.add(getJMenuItemFileExit());
			UIUtils.setUsabilityDtls(MessageRepository.get(Msg.COMMON_UI_APP_MENU_FILE), fMenuFile);		}
		return fMenuFile;
	}

	/**
	 * @return javax.swing.JMenuItem
	 */
	protected javax.swing.JMenuItem getJMenuItemFileExit() {
		if(fMenuItemFileExit == null) {
			fMenuItemFileExit = UIFactoryMgr.createMenuItem();
			UIUtils.setUsabilityDtls(MessageRepository.get(Msg.COMMON_UI_APP_MENU_FILE_EXIT), fMenuItemFileExit);
			fMenuItemFileExit.addActionListener(this.fEventHandler);
		}
		return fMenuItemFileExit;
	}

	/**
	 * @return javax.swing.JMenuItem
	 */
	protected javax.swing.JMenuItem getJMenuItemHelpLaunch() {
		if(fMenuItemHelpLaunch == null) {
			fMenuItemHelpLaunch = UIFactoryMgr.createMenuItem();
			UIUtils.setUsabilityDtls(MessageRepository.get(Msg.COMMON_UI_APP_MENU_HELP_LAUNCH_HELP), fMenuItemHelpLaunch);
			fMenuItemHelpLaunch.setIcon(UIConfiguration.getIcon("help.gif"));
		}
		return fMenuItemHelpLaunch;
	}

	/**
	 * @return javax.swing.JMenuItem
	 */
	protected javax.swing.JMenuItem getJMenuItemHelpFeedback() {
		if(fMenuItemHelpFeedback == null) {
			fMenuItemHelpFeedback = UIFactoryMgr.createMenuItem();
			UIUtils.setUsabilityDtls(
				MessageRepository.get(Msg.COMMON_UI_APP_MENU_HELP_FEEDBACK), fMenuItemHelpFeedback);
			fMenuItemHelpFeedback.addActionListener(fEventHandler);
		}
		return fMenuItemHelpFeedback;
	}

	/**
	 * @return javax.swing.JMenuItem
	 */
	protected javax.swing.JMenuItem getJMenuItemHelpAbout() {
		if(fMenuItemHelpAbout == null) {
			fMenuItemHelpAbout = UIFactoryMgr.createMenuItem();
			UIUtils.setUsabilityDtls(MessageRepository.get(Msg.COMMON_UI_APP_MENU_HELP_ABOUT), fMenuItemHelpAbout);
			fMenuItemHelpAbout.addActionListener(this.fEventHandler);
			fMenuItemHelpAbout.setIcon(UIConfiguration.getIcon("application.gif"));
		}
		return fMenuItemHelpAbout;
	}

    /**
     * @return javax.swing.JMenuItem
     */
    protected javax.swing.JMenuItem getJMenuItemHelpLicense() {
        if(fMenuItemHelpLicense == null) {
            fMenuItemHelpLicense = UIFactoryMgr.createMenuItem();
            UIUtils.setUsabilityDtls(MessageRepository.get(Msg.COMMON_UI_APP_MENU_HELP_LICENSE), fMenuItemHelpLicense);
            fMenuItemHelpLicense.addActionListener(this.fEventHandler);
            //fMenuItemHelpLicense.setIcon(UIConfiguration.getIcon("license.gif"));
        }
        return fMenuItemHelpLicense;
    }

	/**
	 * @return javax.swing.JMenuItem
	 */
	protected javax.swing.JMenuItem getJMenuItemConfigurationSettings() {
		if(fMenuItemConfigurationSettings == null) {
			fMenuItemConfigurationSettings = UIFactoryMgr.createMenuItem();
			UIUtils.setUsabilityDtls(MessageRepository.get(Msg.COMMON_UI_APP_MENU_CONFIGURATION_SETTINGS), fMenuItemConfigurationSettings);
			fMenuItemConfigurationSettings.addActionListener(this.fEventHandler);
		}
		return fMenuItemConfigurationSettings;
	}

	/**
	 * @return javax.swing.JMenuItem
	 */
	protected javax.swing.JMenuItem getJMenuItemConfigurationToggleToolBar() {
		if(fMenuItemConfigurationToggleToolBar == null) {
			fMenuItemConfigurationToggleToolBar = UIFactoryMgr.createMenuItem();
			UIUtils.setUsabilityDtls(MessageRepository.get(Msg.COMMON_UI_APP_MENU_CONFIGURATION_TOGGLETOOLBAR), fMenuItemConfigurationToggleToolBar);
			fMenuItemConfigurationToggleToolBar.addActionListener(this.fEventHandler);
		}
		return fMenuItemConfigurationToggleToolBar;
	}

	/**
	 * @return javax.swing.JMenuItem
	 */
	protected javax.swing.JMenuItem getJMenuItemConfigurationToggleStatusBar() {
		if(fMenuItemConfigurationToggleStatusBar == null) {
			fMenuItemConfigurationToggleStatusBar = UIFactoryMgr.createMenuItem();
			UIUtils.setUsabilityDtls(MessageRepository.get(Msg.COMMON_UI_APP_MENU_CONFIGURATION_TOGGLESTATUSBAR), fMenuItemConfigurationToggleStatusBar);
			fMenuItemConfigurationToggleStatusBar.addActionListener(this.fEventHandler);
		}
		return fMenuItemConfigurationToggleStatusBar;
	}

	/**
	 * @return javax.swing.JToolBar
	 */
	protected JToolBar getJToolBar() {
		if(fToolBar == null) {
			fToolBar = UIFactoryMgr.createToolBar();
			fToolBar.setBorder(null);
			fToolBar.setFloatable(false);
		}
		return fToolBar;
	}

	/**
	 * @return javax.swing.JMenu
	 */
	protected javax.swing.JMenu getJMenuActions() {
		if(fMenuActions == null) {
			fMenuActions = UIFactoryMgr.createMenu();
			UIUtils.setUsabilityDtls(MessageRepository.get(Msg.COMMON_UI_APP_MENU_ACTIONS), fMenuActions);
		}
		return fMenuActions;
	}

	/**
	 * @return javax.swing.JMenu
	 */
	protected javax.swing.JMenu getJMenuConfiguration() {
		if(fMenuConfiguration == null) {
			fMenuConfiguration = UIFactoryMgr.createMenu();
			UIUtils.setUsabilityDtls(MessageRepository.get(Msg.COMMON_UI_APP_MENU_CONFIGURATION), fMenuConfiguration);
			fMenuConfiguration.add(getJMenuItemConfigurationToggleToolBar());
			fMenuConfiguration.add(getJMenuItemConfigurationToggleStatusBar());
			fMenuConfiguration.add(getJMenuItemConfigurationSettings());
		}
		return fMenuConfiguration;
	}

	/**
	 * @return javax.swing.JMenu
	 */
	protected javax.swing.JMenu getJMenuHelp() {
		if(fMenuHelp == null) {
			fMenuHelp = UIFactoryMgr.createMenu();
			UIUtils.setUsabilityDtls(MessageRepository.get(Msg.COMMON_UI_APP_MENU_HELP), fMenuHelp);
			//jMenuHelp.add(getJMenuItemHelpUpdates());
			fMenuHelp.add(getJMenuItemHelpLaunch());
			if(fFeedbackURL != null) {
				fMenuHelp.add(getJMenuItemHelpFeedback());
			}
			fMenuHelp.add(getJMenuItemHelpAbout());
            if(LicenseMgr.getLicense() != null) {
                fMenuHelp.add(getJMenuItemHelpLicense());
            }
		}
		return fMenuHelp;
	}


	/**
	 * Shows and hides the status bar.
	 */
	protected void handleToggleStatusBar() {
		try {
			// hide the bar only when the evaluation period is not expired
			// so that the license warning cannot be hidden
			if(LicenseMgr.isFirstEvaluationFinished() == null) {
				fStatusBar.setVisible(!(fStatusBar.isVisible()));
			}
		} catch(Exception e) {
			UIExceptionMgr.userException(e);
		}
	}

	/**
	 * Shows and hides the tool bar.
	 */
	protected void handleToggleToolBar() {
		try {
			getJToolBar().setVisible(!(getJToolBar().isVisible()));
		} catch(Exception e) {
			UIExceptionMgr.userException(e);
		}
	}

	/**
	 * Shows and hides the tool bar.
	 */
	protected void handleClose() {
		try {
			if(this.resetCurrentView()) {
			    return;
            }
			this.dispose();
			try {
				// save preferences
				PreferencesConfiguration.get().save();
			} catch(Exception e) {
				logger.error(e);
			}
			System.exit(0);
		} catch(Exception e) {
			UIExceptionMgr.userException(e);
		}
	}

	/**
	 * Resets the current view.
     * @return true if the current view vetoed the close
	 */
	protected boolean resetCurrentView() {
		if(this.fCurrentView != null) {
			boolean veto = this.fCurrentView.close();
            if(!veto) {
                // view closed successfully
                this.fCurrentView = null;
            }
            return veto;
		}
		setTitle();
        return false;
	}

	/**
	 * Shows the about dialog.
	 */
	protected void handleAbout() {
		try {
			ShowAbout.showAbout(this);
		} catch(Exception e) {
			UIExceptionMgr.userException(e);
		}
	}

    /**
     * Shows the license dialog.
     */
    protected void handleLicense() {
        try {
            ShowLicense.showLicense(this);
        } catch(Exception e) {
            UIExceptionMgr.userException(e);
        }
    }

	/**
	 * Handle settings.
	 */
	protected void handleSettings() {
		try {
			final AppFrame vc = this;
			fWorker.runJobSynch(new UIWorkerJobDefault(
					this,
					Cursor.WAIT_CURSOR,
					MessageRepository.get(Msg.COMMON_UI_APP_TEXT_READING_CONFIGURATION)) {
				public void work() throws Exception {
					fResult = new ConfigurationEditorDialog(vc);
				}
				public void finished(Throwable ex) {
					if(ex == null) {
						JDialog ud = (JDialog)fResult;
						ud.setTitle(MessageRepository.get(Msg.COMMON_UI_APP_TITLE_CONFIGURATION_DIALOG));
						ud.setSize(500, 350);
						ud.setModal(true);
						UIUtils.centerDialogAndShow(vc, ud);
					}
				}
			});
		} catch(Exception e) {
			UIExceptionMgr.userException(e);
		}
	}

	/**
	 * Sends feedback.
	 */
	protected void handleSendFeedback() {
		try {
			FeedbackDialog dlg = new FeedbackDialog(this, fFeedbackURL);
			UIUtils.centerDialogAndShow(this, dlg);
		} catch(Exception e) {
			UIExceptionMgr.userException(e);
		}
	}

    /**
     * @see com.ixora.rms.ui.AppViewContainer#unregisterMenus(javax.swing.JMenu[])
     */
    public void unregisterMenus(JMenu[] menus) {
		try {
			for(int i = 0; i < menus.length; i++) {
			    getJMenuBar().remove(menus[i]);
			}
			getJMenuBar().validate();
			getJMenuBar().repaint();
		} catch(Exception e) {
			UIExceptionMgr.userException(e);
		}
    }


	/**
     * @see com.ixora.rms.ui.AppViewContainer#registerMenus(javax.swing.JMenu[], javax.swing.JMenuItem[][])
     */
    public void registerMenus(JMenu[] menus, JMenuItem[][] items) {
		try {
			unregisterMenus(menus);
			int startingIndex = 2;
			JMenu m;
			JMenuItem mi;
			for(int i = 0; i < menus.length; i++) {
			    m = menus[i];
				getJMenuBar().add(m, startingIndex++);
				for(int j = 0; j < items[i].length; j++) {
				    mi = items[i][j];
					mi.setToolTipText(null);
					m.add(mi);
                }
			}
			getJMenuBar().validate();
			getJMenuBar().repaint();
		} catch(Exception e) {
			UIExceptionMgr.userException(e);
		}
    }


	/**
	 * @see java.awt.Component#setVisible(boolean)
	 */
	public void setVisible(boolean b) {
		hideMenusWithNoItems();
		super.setVisible(b);
	}

	/**
	 * Hides menus that have no items.
	 */
	private void hideMenusWithNoItems() {
		// hide all menus with no menu items
		if(fMenuActions.getItemCount() == 0) {
			fMenuActions.setVisible(false);
		}
	}
}
