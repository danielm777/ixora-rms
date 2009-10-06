/*
 * Created on 25-Apr-2004
 */
package com.ixora.rms.repository;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import com.ixora.common.xml.exception.XMLException;
import com.ixora.rms.services.AgentTemplateRepositoryService;

/**
 * @author Daniel Moraru
 */
public final class AgentTemplateRepositoryManager implements AgentTemplateRepositoryService {
	/** Underlying repository */
	private InstallationArtefactRepository repository;

	/**
	 * Constructor.
	 * @throws FileNotFoundException
	 * @throws XMLException
	 */
	public AgentTemplateRepositoryManager() throws XMLException, FileNotFoundException {
		super();
		this.repository = new InstallationArtefactRepository("agenttemplate", new InstallationArtefactFactory() {
			public InstallationArtefact createArtefact() {
				return new AgentTemplate();
			}
		});
	}

    /**
     * @see com.ixora.rms.services.AgentTemplateRepositoryService#getAgentTemplates()
     */
    public Map<String, AgentTemplate> getAgentTemplates() {
        // TODO revisit casting stuff
        Map<String, InstallationArtefact> templates = this.repository.getInstalledArtefacts();
        Map<String, AgentTemplate> ret = new HashMap<String, AgentTemplate>(templates.size());
        for(Map.Entry<String, InstallationArtefact> me : templates.entrySet()) {
            ret.put(me.getKey(), (AgentTemplate)me.getValue());
        }
        return ret;
    }

	/**
	 * @see com.ixora.common.Service#shutdown()
	 */
	public void shutdown() {
		; // nothing
	}
}
