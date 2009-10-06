/*
 * Created on 19-May-2005
 */
package com.ixora.rms.repository;

import com.ixora.common.xml.XMLTag;
import com.ixora.common.xml.XMLText;

/**
 * AgentTemplate
 */
public class AgentTemplate extends XMLTag implements InstallationArtefact {
    protected XMLText templateId = new XMLText("templateId");
    protected XMLText templateName = new XMLText("templateName");
    protected XMLText agentClass = new XMLText("agentClass");
    protected XMLText agentConfigurationPanelClass = new XMLText("agentConfigurationPanelClass");

    /**
     * Default constructor to support XML.
     */
    public AgentTemplate() {
        super();
    }

    /**
     * @return
     */
    public String getAgentClass() {
        return agentClass.getValue();
    }

    /**
     * @return
     */
    public String getAgentConfigurationPanelClass() {
        return agentConfigurationPanelClass.getValue();
    }

    /**
     * @see com.ixora.common.xml.XMLTag#getTagName()
     */
    public String getTagName() {
        return "template";
    }

    /**
     * @see com.ixora.rms.repository.InstallationArtefact#getInstallationIdentifier()
     */
    public String getInstallationIdentifier() {
        return templateId.getValue();
    }

    /**
     * @return the template name
     */
    public String getTemplateName() {
        return templateName.getValue();
    }
}
