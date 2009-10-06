/*
 * Created on 23-Oct-2004
 */
package com.ixora.rms.ui;

import java.awt.event.KeyEvent;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JToggleButton;

import com.ixora.rms.RMS;
import com.ixora.common.MessageRepository;
import com.ixora.common.ui.UIExceptionMgr;
import com.ixora.common.ui.UIFactoryMgr;
import com.ixora.common.ui.UIUtils;
import com.ixora.common.ui.preferences.PreferencesConfigurationConstants;
import com.ixora.rms.client.model.SessionModel;
import com.ixora.rms.services.AgentRepositoryService;
import com.ixora.rms.services.DashboardRepositoryService;
import com.ixora.rms.services.DataEngineService;
import com.ixora.rms.services.DataViewBoardRepositoryService;
import com.ixora.rms.services.DataViewRepositoryService;
import com.ixora.rms.ui.actions.ActionNextScreen;
import com.ixora.rms.ui.actions.ActionPrevScreen;
import com.ixora.rms.ui.actions.ActionSetViewBoardName;
import com.ixora.rms.ui.actions.ActionShowLegendWindow;
import com.ixora.rms.ui.actions.ActionTileViewBoards;
import com.ixora.rms.ui.actions.ActionToggleIdentifiers;
import com.ixora.rms.ui.actions.ActionToggleHTMLGeneration;
import com.ixora.rms.ui.dataviewboard.handler.DataViewBoardHandler;
import com.ixora.rms.ui.dataviewboard.handler.DataViewScreenPanel;
import com.ixora.rms.ui.exporter.HTMLGenerator;
import com.ixora.rms.ui.messages.Msg;

/**
 * @author Daniel Moraru
 */
public abstract class SessionView
		implements Observer, RMSView, PreferencesConfigurationConstants {
	protected JMenu jMenuView;
	protected JMenu jMenuWindows;
	protected JMenuItem jMenuItemToggleIdentifiers;
	protected JMenuItem jMenuItemShowHideLegendWindow;
	protected JMenuItem jMenuItemTileBoards;
    protected JMenuItem jMenuItemSetViewBoardName;
    protected JMenuItem jMenuItemNextScreen;
    protected JMenuItem jMenuItemPrevScreen;
	protected JButton jButtonShowHideLegendWindow;
	protected JCheckBoxMenuItem jMenuItemToggleHTMLGen;
	protected JToggleButton jButtonHTMLGen;

	/**
	 * Handler for the popup button 'New View Board'
	 */
	protected ButtonNewViewBoardHandler buttonNewViewBoardHandler;
	/**
	 * View container hosting this session view.
	 */
	protected RMSViewContainer viewContainer;
	/**
	 * Handles the interactions with the data view boards.
	 */
	protected DataViewBoardHandler dataViewBoardHandler;
	/**
	 * This is the model of the monitoring session. It's the object
	 * containing the state of this session, all hosts, agents,
	 * discovered entities and their current configuration.
	 */
	protected SessionModel sessionModel;
	/**
	 * Data view screen panel.
	 */
	protected DataViewScreenPanel screenPanel;
	/**
	 * Agent repository used to provide agent installation information.
	 */
	protected AgentRepositoryService rmsAgentRepository;
	/**
	 * Repository used to load and save dashboards.
	 */
	protected DashboardRepositoryService rmsDashboardRepository;
	/**
	 * Repository used to load and save queries.
	 */
	protected DataViewRepositoryService rmsDataViewRepository;
	/**
	 * Repository for data view boards.
	 */
	protected DataViewBoardRepositoryService rmsDataViewBoardRepository;
	/**
	 * Data engine service used to satisfy data queries.
	 */
	protected DataEngineService rmsDataEngineService;
	/**
	 * HTML generator.
	 */
	protected HTMLGenerator htmlGenerator;

// actions
	protected ActionShowLegendWindow actionShowHideLegendWindow;
	protected ActionToggleIdentifiers actionToggleIdentifiers;
	protected ActionTileViewBoards actionTileBoards;
	protected ActionSetViewBoardName actionSetViewBoardName;
    protected ActionNextScreen actionNextScreen;
    protected ActionPrevScreen actionPrevScreen;
	protected ActionToggleHTMLGeneration actionToggleHTMLGen;

	/**
     * Constructor.
     * @param vc
     */
    public SessionView(RMSViewContainer vc) {
        super();
		if(vc == null) {
			throw new IllegalArgumentException("null view container");
		}
		this.viewContainer = vc;
		this.rmsAgentRepository = RMS.getAgentRepository();
		this.rmsDataEngineService = RMS.getDataEngine();
		this.rmsDataViewRepository = RMS.getDataViewRepository();
		this.rmsDashboardRepository = RMS.getDashboardRepository();
		this.rmsDataViewBoardRepository = RMS.getDataViewBoardRepository();
    }

	/**
	 * @return javax.swing.JMenu
	 */
	protected javax.swing.JMenu getJMenuView() {
		if(jMenuView == null) {
			jMenuView = UIFactoryMgr.createMenu();
			UIUtils.setUsabilityDtls(MessageRepository.get(Msg.MENU_VIEW), jMenuView);
		}
		return jMenuView;
	}

	/**
	 * @return javax.swing.JMenu
	 */
	protected javax.swing.JMenu getJMenuWindows() {
		if(jMenuWindows == null) {
			jMenuWindows = UIFactoryMgr.createMenu();
			UIUtils.setUsabilityDtls(MessageRepository.get(Msg.MENU_WINDOWS), jMenuWindows);
		}
		return jMenuWindows;
	}

    /**
     * @return the jMenuItemToggleIdentifiers.
     */
    protected javax.swing.JMenuItem getJMenuItemToggleIdentifiers() {
        if(jMenuItemToggleIdentifiers == null) {
            jMenuItemToggleIdentifiers = UIFactoryMgr.createMenuItem(getActionToggleIdentifiers());
        }
        return jMenuItemToggleIdentifiers;
    }

    /**
     * @return the jMenuItemShowHideLegendWindow.
     */
    protected javax.swing.JMenuItem getJMenuItemShowHideLegendWindow() {
        if(jMenuItemShowHideLegendWindow == null) {
            jMenuItemShowHideLegendWindow = UIFactoryMgr.createMenuItem(getActionShowHideLegend());
        }
        return jMenuItemShowHideLegendWindow;
    }

    /**
     * @return the jMenuItemToggleIdentifiers.
     */
    protected javax.swing.JMenuItem getJMenuItemTileBoards() {
        if(jMenuItemTileBoards == null) {
            jMenuItemTileBoards = UIFactoryMgr.createMenuItem(getActionTileBoards());
        }
        return jMenuItemTileBoards;
    }

    /**
     * @return the jMenuItemNextScreen.
     */
    protected javax.swing.JMenuItem getJMenuItemNextScreen() {
        if(jMenuItemNextScreen == null) {
            jMenuItemNextScreen = UIFactoryMgr.createMenuItem(getActionNextScreen());
        }
        return jMenuItemNextScreen;
    }

    /**
     * @return the jMenuItemPrevScreen.
     */
    protected javax.swing.JMenuItem getJMenuItemPrevScreen() {
        if(jMenuItemPrevScreen == null) {
            jMenuItemPrevScreen = UIFactoryMgr.createMenuItem(getActionPrevScreen());
        }
        return jMenuItemPrevScreen;
    }

    /**
     * @return the jMenuItemSetViewBoardName.
     */
    protected javax.swing.JMenuItem getJMenuItemSetViewBoardName() {
        if(jMenuItemSetViewBoardName == null) {
            jMenuItemSetViewBoardName = UIFactoryMgr.createMenuItem(getActionSetViewBoardName());
        }
        return jMenuItemSetViewBoardName;
    }

	/**
	 * @return javax.swing.JButton
	 */
	protected JButton getJButtonShowHideLegendWindow() {
		if(jButtonShowHideLegendWindow == null) {
			jButtonShowHideLegendWindow = UIFactoryMgr.createButton(getActionShowHideLegend());
			jButtonShowHideLegendWindow.setText(null);
			jButtonShowHideLegendWindow.setMnemonic(KeyEvent.VK_UNDEFINED);
			jButtonShowHideLegendWindow.setSelected(false);
		}
		return jButtonShowHideLegendWindow;
	}

	/**
	 * @return javax.swing.JToggleButton
	 */
	protected javax.swing.JToggleButton getJButtonToggleHTMLGeneration() {
		if(jButtonHTMLGen == null) {
			jButtonHTMLGen = UIFactoryMgr.createToggleButton(getActionToggleHTMLGeneration());
			jButtonHTMLGen.setText(null);
			jButtonHTMLGen.setMnemonic(KeyEvent.VK_UNDEFINED);
		}
		return jButtonHTMLGen;
	}

	/**
	 * @return javax.swing.JCheckBoxMenuItem
	 */
	protected javax.swing.JCheckBoxMenuItem getJMenuItemToggleHTMLGen() {
		if(jMenuItemToggleHTMLGen == null) {
			jMenuItemToggleHTMLGen = UIFactoryMgr.createCheckBoxMenuItem(getActionToggleHTMLGeneration());
		}
		return jMenuItemToggleHTMLGen;
	}

    /**
     * @see com.ixora.rms.ui.SessionView#getActionToggleIdentifiers()
     */
    protected ActionToggleIdentifiers getActionToggleIdentifiers() {
        if(actionToggleIdentifiers == null) {
            actionToggleIdentifiers = new ActionToggleIdentifiers(sessionModel);
        }
        return actionToggleIdentifiers;
    }

    /**
     * @see com.ixora.rms.ui.SessionView#getActionShowHideLegend()
     */
    protected ActionShowLegendWindow getActionShowHideLegend() {
        if(actionShowHideLegendWindow == null) {
        	actionShowHideLegendWindow = new ActionShowLegendWindow(viewContainer);
        }
        return actionShowHideLegendWindow;
    }

    /**
     * @see com.ixora.rms.ui.SessionView#getActionToggleGeneration()
     */
    protected ActionToggleHTMLGeneration getActionToggleHTMLGeneration() {
        if(actionToggleHTMLGen == null) {
            actionToggleHTMLGen = new ActionToggleHTMLGeneration(htmlGenerator);
        }
        return actionToggleHTMLGen;
    }

    /**
     * @see com.ixora.rms.ui.SessionView#getActionTileBoards()
     */
    protected ActionTileViewBoards getActionTileBoards() {
        if(actionTileBoards == null) {
            actionTileBoards = new ActionTileViewBoards(dataViewBoardHandler);
        }
        return actionTileBoards;
    }

    /**
     * @see com.ixora.rms.ui.SessionView#getActionNextScreen()
     */
    protected ActionNextScreen getActionNextScreen() {
        if(actionNextScreen == null) {
            actionNextScreen = new ActionNextScreen(dataViewBoardHandler);
        }
        return actionNextScreen;
    }

    /**
     * @see com.ixora.rms.ui.SessionView#getActionNextScreen()
     */
    protected ActionPrevScreen getActionPrevScreen() {
        if(actionPrevScreen == null) {
            actionPrevScreen = new ActionPrevScreen(dataViewBoardHandler);
        }
        return actionPrevScreen;
    }

    /**
     * @see com.ixora.rms.ui.SessionView#getActionSetViewBoardName()
     */
    protected ActionSetViewBoardName getActionSetViewBoardName() {
        if(actionSetViewBoardName == null) {
            actionSetViewBoardName = new ActionSetViewBoardName(dataViewBoardHandler);
        }
        return actionSetViewBoardName;
    }


	/**
	 * Unregisters components with the view container.
	 */
	protected void unregisterComponentsWithViewContainer() {
		this.viewContainer.unregisterToolBarComponents(
				new JComponent[] {
					buttonNewViewBoardHandler.getButton(),
					getJButtonShowHideLegendWindow(),
				});
		this.viewContainer.unregisterToolBarComponents(
				new JComponent[] {
					screenPanel,
				});
		this.viewContainer.unregisterMenus(
				new JMenu[] {
					getJMenuView(),
					getJMenuWindows()});
	}

	/**
	 * Registers components with the view container.
	 */
	protected void registerComponentsWithViewContainer() {
		viewContainer.registerToolBarComponents(
				new JComponent[] {
					buttonNewViewBoardHandler.getButton(),
					getJButtonShowHideLegendWindow(),
				});
		viewContainer.registerToolBarComponents(
				new JComponent[] {
					screenPanel,
				});
		viewContainer.registerMenus(
				new JMenu[] {
					getJMenuView(),
					getJMenuWindows()},
				new JMenuItem[][] {
				   {
					   getJMenuItemToggleIdentifiers(),
					   getJMenuItemShowHideLegendWindow()
				   },
				   {
					   getJMenuItemTileBoards(),
				       getJMenuItemSetViewBoardName(),
                       getJMenuItemNextScreen(),
                       getJMenuItemPrevScreen()}
				   });
	}

	/**
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	public void update(Observable o, Object arg) {
		if(o == dataViewBoardHandler) {
			handleChangeScreen();
		}
	}

	/**
	 * Changes the current screen.
	 */
	protected void handleChangeScreen() {
		try {
			viewContainer.registerRightComponent(
			        dataViewBoardHandler.getScreen());
		} catch(Exception e) {
			UIExceptionMgr.userException(e);
		}
	}

	/**
	 * Invoked when the HTML generation is finished.
	 * @param error
	 */
	protected void handleHTMLGenerationFinished(Exception error) {
		try {
			getJButtonToggleHTMLGeneration().setSelected(false);
			getJMenuItemToggleHTMLGen().setSelected(false);
			if(error != null) {
				// TODO localize
				viewContainer.setErrorMessage("Error generating HTML.", error);
			}
		} catch(Exception e) {
			UIExceptionMgr.userException(e);
		}
	}

	/**
	 * Invoked when the HTML generation is cancelled.
	 */
	protected void handleHTMLGenerationCancelled() {
		try {
			getJButtonToggleHTMLGeneration().setSelected(false);
			getJMenuItemToggleHTMLGen().setSelected(false);
		} catch(Exception e) {
			UIExceptionMgr.userException(e);
		}
	}

	/**
	 * Invoked when the HTML generation is started.
	 */
	protected void handleHTMLGenerationStarted() {
		try {
			getJButtonToggleHTMLGeneration().setSelected(true);
			getJMenuItemToggleHTMLGen().setSelected(true);
		} catch(Exception e) {
			UIExceptionMgr.userException(e);
		}
	}
}
