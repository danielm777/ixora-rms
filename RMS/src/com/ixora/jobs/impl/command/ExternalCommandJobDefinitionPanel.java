/*
 * Created on 28-Sep-2004
 */
package com.ixora.jobs.impl.command;

import com.ixora.jobs.JobData;
import com.ixora.jobs.JobDefinition;
import com.ixora.jobs.JobProperties;
import com.ixora.jobs.impl.command.messages.Msg;
import com.ixora.jobs.library.JobLibraryDefinition;
import com.ixora.jobs.ui.JobDefinitionPanelDefault;

/**
 * @author Daniel Moraru
 */
public final class ExternalCommandJobDefinitionPanel extends
        JobDefinitionPanelDefault {
	private static final long serialVersionUID = 6162993553496652028L;
	/** Property key for external command */
    private static final String EXTERNAL_COMMAND = Msg.TEXT_JOB_EXTERNAL_COMMAND;

    /**
     * Constructor.
     * @param hosts
     * @param forLibrary
     */
    public ExternalCommandJobDefinitionPanel(String[] hosts, boolean forLibrary, boolean viewMode) {
        super(hosts, forLibrary, viewMode);
        properties.setProperty(EXTERNAL_COMMAND, JobProperties.TYPE_STRING, true);
        linkWithEditor();
    }
    
    /**
     * @see com.ixora.rms.ui.jobs.JobDefinitionPanel#setJobDefinition(com.ixora.rms.internal.jobs.JobLibraryDefinition)
     */
    public void setJobDefinition(JobDefinition def) {
        super.setJobDefinition(def);
        this.properties.setString(EXTERNAL_COMMAND,
               ((ExternalCommandJobData)def.getJobData()).getCommand());
        linkWithEditor();
    }

    /**
     * @see com.ixora.jobs.ui.JobDefinitionPanel#setJobDefinition(com.ixora.jobs.library.JobLibraryDefinition)
     */
	public void setJobDefinition(JobLibraryDefinition def) {
		super.setJobDefinition(def);
        this.properties.setString(EXTERNAL_COMMAND,
                ((ExternalCommandJobData)def.getJobData()).getCommand());
         linkWithEditor();
	}

	/**
	 * @see com.ixora.rms.ui.jobs.JobDefinitionPanelDefault#getJobDataFromProperties()
	 */
	protected JobData getJobDataFromProperties() {
        String command = properties.getString(EXTERNAL_COMMAND);
        return new ExternalCommandJobData(command);
	}
}
