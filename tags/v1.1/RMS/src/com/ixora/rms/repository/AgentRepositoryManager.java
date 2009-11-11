/*
 * Created on 25-Apr-2004
 */
package com.ixora.rms.repository;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import com.ixora.common.MessageRepository;
import com.ixora.common.logging.AppLogger;
import com.ixora.common.logging.AppLoggerFactory;
import com.ixora.common.update.Module;
import com.ixora.common.update.UpdateMgr;
import com.ixora.common.xml.exception.XMLException;
import com.ixora.rms.services.AgentRepositoryService;

/**
 * @author Daniel Moraru
 */
public final class AgentRepositoryManager implements AgentRepositoryService, Observer {
	/** Logger */
	private static final AppLogger logger = AppLoggerFactory.getLogger(AgentRepositoryManager.class);
	/** Underlying repository */
	private InstallationArtefactRepository repository;

	/**
	 * Constructor.
	 * @throws FileNotFoundException
	 * @throws XMLException
	 */
	public AgentRepositoryManager() throws XMLException, FileNotFoundException {
		super();
		this.repository = new InstallationArtefactRepository("agent", new InstallationArtefactFactory() {
			public InstallationArtefact createArtefact() {
				return new AgentInstallationData();
			}
		});
		// register now all agents with the update manager
		registerAgentsWithUpdateMgr();
	}

	/**
	 * @return a map of AgentInstallationInfo keyed by agent id.
	 */
	public synchronized Map<String, AgentInstallationData> getInstalledAgents() {
		// TODO revisit casting stuff
		Map<String, InstallationArtefact> agents = this.repository.getInstalledArtefacts();
		Map<String, AgentInstallationData> ret = new HashMap<String, AgentInstallationData>(agents.size());
		for(Map.Entry<String, InstallationArtefact> me : agents.entrySet()) {
			ret.put(me.getKey(), (AgentInstallationData)me.getValue());
		}
		return ret;
	}

	/**
	 * Loads installation data from another product instance.
	 * @param productRoot
	 * @return
	 * @throws FileNotFoundException
	 * @throws XMLException
	 */
	public Map<String, AgentInstallationData> getInstalledAgents(File productRoot)
			throws XMLException, FileNotFoundException {
		if(productRoot == null) {
			return getInstalledAgents();
		}
		InstallationArtefactRepository rep = new InstallationArtefactRepository(productRoot, "agent",
				new InstallationArtefactFactory() {
					public InstallationArtefact createArtefact() {
						return new AgentInstallationData();
					}
				});
		// TODO revisit casting stuff
		Map<String, InstallationArtefact> agents = rep.getInstalledArtefacts();
		Map<String, AgentInstallationData> ret = new HashMap<String, AgentInstallationData>(agents.size());
		for(Map.Entry<String, InstallationArtefact> me : agents.entrySet()) {
			ret.put(me.getKey(), (AgentInstallationData)me.getValue());
		}
		return ret;
	}

	/**
	 * @see com.ixora.common.Service#shutdown()
	 */
	public void shutdown() {
		; // nothing
	}

	/**
	 * It is an observer to AgentInstaller
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	public void update(Observable o, Object arg) {
		if(o instanceof AgentInstaller) {
			try {
				synchronized (this) {
					unregisterAgentsWithUpdateMgr();
					this.repository.reload();
					registerAgentsWithUpdateMgr();
				}
			} catch(Exception e) {
				logger.error(e);
			}
		}
	}

	/**
	 * Register agents as components with the update manager.
	 */
	private void registerAgentsWithUpdateMgr() {
		Map<String, InstallationArtefact> agents = repository.getInstalledArtefacts();
		for(InstallationArtefact ia : agents.values()) {
			AgentInstallationData aii = (AgentInstallationData)ia;
            if(!aii.isCustomAgent()) {
    			UpdateMgr.registerModule(
    					new Module(
    						MessageRepository.get(
    								aii.getMessageCatalog(),
    								aii.getAgentName()),
    								aii.getAgentVersion()));
            }
		}
	}

	/**
	 * Unregister agents as components with the update manager.
	 */
	private void unregisterAgentsWithUpdateMgr() {
		Map<String, InstallationArtefact> agents = repository.getInstalledArtefacts();
		for(InstallationArtefact ia : agents.values()) {
			AgentInstallationData aii = (AgentInstallationData)ia;
			UpdateMgr.unregisterModule(
						MessageRepository.get(aii.getMessageCatalog(), aii.getAgentName()));
		}
	}

}
