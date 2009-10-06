/*
 * Created on 07-Feb-2005
 */
package com.ixora.rms.services;

import java.io.File;
import java.io.IOException;

import com.ixora.common.ProgressMonitor;
import com.ixora.common.xml.exception.XMLException;
import com.ixora.rms.exception.AgentIsNotInstalled;
import com.ixora.rms.exception.InvalidAgentPackage;
import com.ixora.rms.exception.RMSException;
import com.ixora.rms.repository.AgentInstallationData;
import com.ixora.rms.repository.exception.AgentAlreadyInstalled;

/**
 * @author Daniel Moraru
 */
public interface AgentInstallerService {
	/**
	 * Installs an agent.
	 * @param aid
	 * @throws RMSException
	 */
	void install(AgentInstallationData aid) throws AgentAlreadyInstalled, RMSException;

	/**
	 * Updates an already installed agent.
	 * @param aid
	 * @throws AgentIsNotInstalled
	 * @throws RMSException
	 */
	void update(AgentInstallationData aid) throws AgentIsNotInstalled, RMSException;

	/**
	 * Uninstalls the given agent.
	 * @param agentId
	 * @throws RMSException
	 */
	void uninstall(String agentId) throws RMSException;
	/**
	 * Installs an agent from a package.
	 * @param agentPackage the agent package file.
	 * @param updateIfExists true to update an existing agent with information from the package;
	 * this preserves user defined artefacts like data views and dashboards
	 * @param progress used to send progress info for this operation
	 * @throws InvalidAgentPackage
	 * @throws RMSException
	 */
	void installFromPackage(File agentPackage, boolean updateIfExists, ProgressMonitor progress) throws InvalidAgentPackage, RMSException;

	/**
	 * Installs/copies an agent from another product installation.
	 * @param productRoot the installation folder of the product instance where the agent will be
	 * imported from
	 * @param progress
	 * @param agentInstallationId
	 * @throws RMSException
	 * @throws IOException
	 * @throws IOException
	 * @throws XMLException
	 */
	void installFromFilesystem(File productRoot, ProgressMonitor progress, String agentInstallationId) throws RMSException, IOException, IOException, XMLException;

	/**
	 * Exports the agent with id <code>agentInstallationId</code> into the folder
	 * <code>destinationFolder</code>.
	 * @param agentInstallationId
	 * @param destinationFolder
	 * @param progress
	 * @return the file with the packaged agent
	 * @throws IOException, RMSException
	 * @throws XMLException
	 */
	File export(String agentInstallationId, File destinationFolder, ProgressMonitor progress) throws IOException, RMSException, XMLException;
}
