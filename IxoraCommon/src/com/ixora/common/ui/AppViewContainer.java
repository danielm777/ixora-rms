package com.ixora.common.ui;
import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import com.ixora.common.ui.help.AppHelp;
import com.ixora.common.ui.jobs.UIWorker;

/*
 * Created on 11-Dec-03
 */
/**
 * @author Daniel Moraru
 */
public interface AppViewContainer {
	/**
	 * @return the application frame
	 */
	JFrame getAppFrame();
	/**
	 * Registers tool bar components with the container.
	 * @param components
	 */
	void registerToolBarComponents(JComponent[] components);
	/**
	 * Registers tool bar buttons with the container.
	 * It is the same as {@link #registerToolBarComponents(JComponent[])} but this method
	 * strips the text from the buttons. If you want to keep the text, 
	 * use {@link #registerToolBarComponents(JComponent[])}. To unregister the buttons use
	 * {@link #unregisterToolBarComponents(JComponent[])}.
	 * @param components
	 */
	void registerToolBarButtons(AbstractButton[] buttons);
	/**
	 * Registers menu items for the Actions menu.
	 * @param items
	 */
	void registerMenuItemsForActionsMenu(JMenuItem[] items);
	/**
	 * Registers menu items for the File menu.
	 * @param items
	 */
	void registerMenuItemsForFileMenu(JMenuItem[] items);
	/**
	 * Registers menus.
	 * @param menus
	 * @param items
	 */
	void registerMenus(JMenu[] menus, JMenuItem[][] items);
	/**
	 * Unregisters tool bar buttons with the container.
	 * @param components
	 */
	void unregisterToolBarComponents(JComponent[] components);
	/**
	 * Unregisters menu items for the Actions menu.
	 * @param items
	 */
	void unregisterMenuItemsForActionsMenu(JMenuItem[] items);
	/**
	 * Unregisters menu items for the File menu.
	 * @param items
	 */
	void unregisterMenuItemsForFileMenu(JMenuItem[] items);
	/**
	 * Unregisters menus.
	 * @param menus
	 */
	void unregisterMenus(JMenu[] menus);
	/**
	 * Unregisters menu items for the given menu.
	 * @param items
	 */
	void unregisterMenuItemsForMenu(JMenu menu, JMenuItem[] items);
	/**
	 * Registers menu items for the the given menu.
	 * @param items
	 */
	void registerMenuItemsForMenu(JMenu menu, JMenuItem[] items);
	/**
	 * Appends the given text to the title of the container.
	 * @param txt
	 */
	void appendToAppFrameTitle(String txt);
	/**
	 * @return the status bar.
	 */
	AppStatusBar getAppStatusBar();
	
	/**
	 * @return the event hub.
	 */
	AppEventHub getAppEventHub();
	
	/**
	 * @return the application worker
	 */
	UIWorker getAppWorker();
	
	/**
	 * @return the application help
	 */
	AppHelp getAppHelp();
	
	/**
	 * @return the current application view
	 */
	AppView getAppView();
	
	/**
	 * @return the handler for non-fatal errors
	 */
	NonFatalErrorHandler getAppNonFatalErrorHandler();
}
