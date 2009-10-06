/*
 * Created on 31-Dec-2003
 */
package com.ixora.rms.ui.session;

import com.ixora.rms.client.session.MonitoringSessionDescriptor;
import com.ixora.rms.ui.session.exception.FailedToLoadSession;
import com.ixora.rms.ui.session.exception.FailedToSaveSession;

/**
 * MonitoringSchemeRepository.
 * @author Daniel Moraru
 */
public interface MonitoringSessionRepository {
	/**
	 * Listener.
	 */
	public interface Listener {
		/**
		 * Invoked when the scheme has finished loading.
		 * @param scheme
		 */
		void sessionLoaded(MonitoringSessionDescriptor scheme);
	}
	/**
	 * Saves the scheme. If the scheme is saved successfuly the name and the
	 * location of the scheme will be updated.
	 * @param scheme
	 * @param asynch
	 * @param saveAs
	 * @throws FailedToSaveSession
	 */
	void saveSession(MonitoringSessionDescriptor scheme, boolean asynch, boolean saveAs) throws FailedToSaveSession;
	/**
	 * Loads the scheme with the given name
	 * as returned by <code>getMostRecentlyUsed()</code>.
	 * @param scheme the name of the scheme
	 * @throws FailedToLoadSession
	 */
	void loadSession(String scheme) throws FailedToLoadSession;
	/**
	 * Allows the user to browse to locate the scheme info
	 * @throws FailedToLoadSession
	 */
	void loadSession() throws FailedToLoadSession ;
	/**
	 * @return the most recently loaded scheme names
	 */
	String[] getMostRecentlyUsed();
}