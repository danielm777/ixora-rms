package com.ixora.common.update;
import java.io.IOException;

import com.ixora.common.xml.exception.XMLException;

/*
 * Created on Feb 12, 2004
 */
/**
 * @author Daniel Moraru
 */
public interface UpdateManager {
	/**
	 * Returns the available updates for the given module.
	 * @param module
	 * @return the available updates for the given module
	 * @throws IOException
	 * @throws XMLException
	 */
	ModuleUpdateDescriptor[] getAvailableUpdates(
			Module module) throws IOException, XMLException;
	/**
	 * Returns the latest update for the given module.
	 * @param module
	 * @return the latest update for the given module
	 * @throws IOException
	 * @throws XMLException
	 */
	ModuleUpdateDescriptor getLatestUpdate(
			Module module) throws IOException, XMLException;
	/**
	 * Installs the update with the given id for the given module.
	 * @param module
	 * @param update
	 * @throws IOException
	 * @throws XMLException
	 */
	void installUpdate(Module module, UpdateId update) throws IOException, XMLException;
	/**
	 * Cancels the current update installation.
	 */
	void cancelUpdateInstallation();
	/**
	 * Installs all the latest updates for all registered
	 * modules.
	 * @throws IOException
	 * @throws XMLException
	 */
	void installAllUpdates() throws IOException, XMLException;
	/**
	 * Refreshes the available updates cache.
	 * @throws IOException
	 * @throws XMLException
	 */
	void refresh() throws IOException, XMLException;
	/**
	 * @return the registered modules.
	 */
	Module[] getRegisteredModules();
	/**
	 * @return whether or not at the end of the update
	 * process the application needs to be restarted.
	 */
	boolean needToRestartApplication();
}
