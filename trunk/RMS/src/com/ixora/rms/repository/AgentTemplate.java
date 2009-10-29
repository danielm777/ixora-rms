/*
 * Created on 19-May-2005
 */
package com.ixora.rms.repository;

import com.ixora.common.utils.Utils;
import com.ixora.common.xml.XMLTag;
import com.ixora.common.xml.XMLText;

/**
 * AgentTemplate
 */
public class AgentTemplate extends XMLTag implements InstallationArtefact {
	private static final long serialVersionUID = -580612798471758259L;
	private XMLText templateId = new XMLText("templateId");
    private XMLText templateName = new XMLText("templateName");
    private XMLText agentClass = new XMLText("agentClass");
    private XMLText agentConfigurationPanelClass = new XMLText("agentConfigurationPanelClass");
    private XMLText jars = new XMLText("jars");
    private XMLText uiJar = new XMLText("uiJar");
    private XMLText natLibs = new XMLText("natLibs");

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
    
    /**
     * @return
     */
    public String[] getJars() {
    	String val = jars.getValue();
    	if(Utils.isEmptyString(val)) {
    		return null;
    	}
    	return val.split(",");
    }

    /**
     * @return
     */
    public String[] getNativeLibs() {
    	String val = natLibs.getValue();
    	if(Utils.isEmptyString(val)) {
    		return null;
    	}
    	return val.split(",");
    }

    /**
     * @return
     */
    public String getUIJar() {
    	String val = uiJar.getValue();
    	if(Utils.isEmptyString(val)) {
    		return null;
    	}
    	return val;    	
    }
}
