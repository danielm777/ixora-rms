package com.ixora.rms.ui;
import javax.swing.JComponent;

import com.ixora.common.ui.AppViewContainer;

/*
 * Created on 11-Dec-03
 */
/**
 * @author Daniel Moraru
 */
public interface RMSViewContainer extends AppViewContainer {
	/**
	 * Registers the left component.
	 * @param component
	 */
	void registerLeftComponent(JComponent component);
	/**
	 * Registers the right component.
	 * @param component
	 */
	void registerRightComponent(JComponent component);
	/**
	 * Invoked to mark the current session as
	 * dirty.
	 * @param d
	 */
	void setSessionDirty(boolean d);
	/**
	 * @return whether the session has been touched
	 */
	boolean isSessionDirty();	
	
	/**
	 * @return the session view
	 */
	SessionView getSessionView();
}
