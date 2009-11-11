/*
 * Created on 28-Sep-2004
 */
package com.ixora.jobs.impl.commandtelnet;

import com.ixora.jobs.JobData;
import com.ixora.jobs.JobDefinition;
import com.ixora.jobs.JobProperties;
import com.ixora.jobs.impl.commandtelnet.messages.Msg;
import com.ixora.jobs.library.JobLibraryDefinition;
import com.ixora.jobs.ui.JobDefinitionPanelDefault;

/**
 * @author Daniel Moraru
 */
public final class TelnetCommandJobDefinitionPanel extends
        JobDefinitionPanelDefault {
	private static final long serialVersionUID = -6345887713841804690L;
	/** Property key for external command */
    private static final String EXTERNAL_COMMAND = Msg.TEXT_JOB_TELNET_EXTERNAL_COMMAND;
    private static final String USERNAME = Msg.TEXT_JOB_TELNET_USERNAME;
    private static final String PASSWORD = Msg.TEXT_JOB_TELNET_PASSWORD;
    private static final String USERNAME_PROMPT = Msg.TEXT_JOB_TELNET_USERNAME_PROMPT;
    private static final String PASSWORD_PROMPT = Msg.TEXT_JOB_TELNET_PASSWORD_PROMPT;
    private static final String SHELL_PROMPT = Msg.TEXT_JOB_TELNET_SHELL_PROMPT;


    /**
     * Constructor.
     * @param hosts
     * @param forLibrary
     * @param viewMode 
     */
    public TelnetCommandJobDefinitionPanel(String[] hosts, boolean forLibrary, boolean viewMode) {
        super(hosts, forLibrary, viewMode);
        properties.setProperty(EXTERNAL_COMMAND, JobProperties.TYPE_STRING, true);
        properties.setProperty(USERNAME, JobProperties.TYPE_STRING, true);
        properties.setProperty(PASSWORD, JobProperties.TYPE_SECURE_STRING, true);
        properties.setProperty(USERNAME_PROMPT, JobProperties.TYPE_STRING, true);
        properties.setProperty(PASSWORD_PROMPT, JobProperties.TYPE_STRING, true);
        properties.setProperty(SHELL_PROMPT, JobProperties.TYPE_STRING, true);
        
        properties.setString(SHELL_PROMPT, "]$");
        properties.setString(USERNAME_PROMPT, "login:");
        properties.setString(PASSWORD_PROMPT, "Password:");
        linkWithEditor();
    }
    
    /**
     * @see com.ixora.rms.ui.jobs.JobDefinitionPanel#setJobDefinition(com.ixora.rms.internal.jobs.JobLibraryDefinition)
     */
    public void setJobDefinition(JobDefinition def) {
        super.setJobDefinition(def);
        TelnetCommandJobData data = (TelnetCommandJobData)def.getJobData();
        this.properties.setString(EXTERNAL_COMMAND, data.getCommand());
        this.properties.setString(USERNAME, data.getUsername());
        this.properties.setString(PASSWORD, data.getPassword());
        this.properties.setString(SHELL_PROMPT, data.getShellPrompt());
        this.properties.setString(USERNAME_PROMPT, data.getUsernamePrompt());
        this.properties.setString(PASSWORD_PROMPT, data.getPasswordPrompt());
        linkWithEditor();
    }

    /**
     * @see com.ixora.jobs.ui.JobDefinitionPanel#setJobDefinition(com.ixora.jobs.library.JobLibraryDefinition)
     */
	public void setJobDefinition(JobLibraryDefinition def) {
		super.setJobDefinition(def);
        TelnetCommandJobData data = (TelnetCommandJobData)def.getJobData();
        this.properties.setString(EXTERNAL_COMMAND, data.getCommand());
        this.properties.setString(USERNAME, data.getUsername());
        this.properties.setString(PASSWORD, data.getPassword());
        this.properties.setString(SHELL_PROMPT, data.getShellPrompt());
        this.properties.setString(USERNAME_PROMPT, data.getUsernamePrompt());
        this.properties.setString(PASSWORD_PROMPT, data.getPasswordPrompt());        
        linkWithEditor();
	}

	/**
	 * @see com.ixora.rms.ui.jobs.JobDefinitionPanelDefault#getJobDataFromProperties()
	 */
	protected JobData getJobDataFromProperties() {
        String command = properties.getString(EXTERNAL_COMMAND).trim();
        String username = properties.getString(USERNAME).trim();
        String password = properties.getString(PASSWORD).trim();
        String shell = properties.getString(SHELL_PROMPT).trim();
        String usernamePrompt = properties.getString(USERNAME_PROMPT).trim();
        String passwordPrompt = properties.getString(PASSWORD_PROMPT).trim();
        return new TelnetCommandJobData(command, username, password, usernamePrompt, passwordPrompt, shell);
	}
}
