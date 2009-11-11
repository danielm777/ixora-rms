/*
 * Created on 30-Dec-2004
 */
package com.ixora.rms.services;

import java.util.Map;

import com.ixora.common.Service;
import com.ixora.rms.repository.AgentTemplate;

/**
 * @author Daniel Moraru
 */
public interface AgentTemplateRepositoryService extends Service {
	/**
	 * @return all agent templates installed
	 */
	public Map<String, AgentTemplate> getAgentTemplates();
}
