package com.ixora.common.ui;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import com.ixora.common.ui.jobs.UIWorkerJob;

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
	public abstract JFrame getAppFrame();
	/**
	 * Registers tool bar buttons with the container.
	 * @param components
	 */
	public abstract void registerToolBarComponents(JComponent[] components);
	/**
	 * Registers menu items for the Actions menu.
	 * @param items
	 */
	public abstract void registerMenuItemsForActionsMenu(JMenuItem[] items);
	/**
	 * Registers menu items for the File menu.
	 * @param items
	 */
	public abstract void registerMenuItemsForFileMenu(JMenuItem[] items);
	/**
	 * Registers menus.
	 * @param menus
	 * @param items
	 */
	public abstract void registerMenus(JMenu[] menus, JMenuItem[][] items);
	/**
	 * Unregisters tool bar buttons with the container.
	 * @param components
	 */
	public abstract void unregisterToolBarComponents(JComponent[] components);
	/**
	 * Unregisters menu items for the Actions menu.
	 * @param items
	 */
	public abstract void unregisterMenuItemsForActionsMenu(JMenuItem[] items);
	/**
	 * Unregisters menu items for the File menu.
	 * @param items
	 */
	public abstract void unregisterMenuItemsForFileMenu(JMenuItem[] items);
	/**
	 * Unregisters menus.
	 * @param menus
	 */
	public abstract void unregisterMenus(JMenu[] menus);
	/**
	 * Unregisters menu items for the given menu.
	 * @param items
	 */
	public abstract void unregisterMenuItemsForMenu(JMenu menu, JMenuItem[] items);
	/**
	 * Registers menu items for the the given menu.
	 * @param items
	 */
	public abstract void registerMenuItemsForMenu(JMenu menu, JMenuItem[] items);
	/**
	 * Runs the given job.
	 * @param job the job to be executed
	 */
	public abstract void runJob(UIWorkerJob job);
	/**
	 * Runs the given job.
	 * @param synch
	 */
	public abstract void runJob(UIWorkerJob job, boolean synch);
	/**
	 * Runs the given job synchronously (on the caller's thread).
	 * @param job the job to be executed
	 */
	public abstract void runJobSynch(UIWorkerJob job);
	/**
	 * Appends the given text to the title of the container.
	 * @param txt
	 */
	public abstract void appendToTitle(String txt);
	/**
	 * Sets the state text.
	 * @param txt
	 */
	public abstract void setStateMessage(String txt);
	/**
	 * Shows the warning text.
	 * @param txt
     * @param t exception acompanying the error message; it can be null
	 */
	public abstract void setErrorMessage(String txt, Throwable t);
	/**
	 * @return the status bar
	 */
	public abstract AppStatusBar getStatusBar();
}
