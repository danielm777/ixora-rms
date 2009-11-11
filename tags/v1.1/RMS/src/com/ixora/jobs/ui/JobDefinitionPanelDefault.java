/*
 * Created on 28-Sep-2004
 */
package com.ixora.jobs.ui;

import java.awt.BorderLayout;
import java.util.Date;

import com.ixora.jobs.JobData;
import com.ixora.jobs.JobDefinition;
import com.ixora.jobs.JobProperties;
import com.ixora.jobs.JobsComponent;
import com.ixora.jobs.exception.CantBuildJobData;
import com.ixora.jobs.exception.CantBuildJobDefinition;
import com.ixora.jobs.library.JobLibraryDefinition;
import com.ixora.common.typedproperties.exception.InvalidPropertyValue;
import com.ixora.common.typedproperties.exception.VetoException;
import com.ixora.common.typedproperties.ui.TypedPropertiesEditor;

/**
 * @author Daniel Moraru
 */
public abstract class JobDefinitionPanelDefault extends JobDefinitionPanel {
	private static final long serialVersionUID = 1776297727925661108L;
	/** Typed props editor */
    protected TypedPropertiesEditor editor;
    /** Job properties */
    protected JobProperties properties;

    /**
     * Constructor.
     * @param hosts
     * @param forLibrary
     * @param viewMode
     */
    protected JobDefinitionPanelDefault(String[] hosts, boolean forLibrary, boolean viewMode) {
        super(new BorderLayout());
        editor = new TypedPropertiesEditor(true);
        editor.setEnabled(!viewMode);
        properties = new JobProperties(hosts, forLibrary);
        add(editor, BorderLayout.CENTER);
    }

   /**
     * @throws CantBuildJobData
 * @see com.ixora.jobs.ui.JobDefinitionPanel#getJobDefinition()
     */
    public JobDefinition getJobDefinition() throws CantBuildJobDefinition, CantBuildJobData {
        try {
            // apply changes
            editor.applyChanges();
        } catch (InvalidPropertyValue e) {
            throw new CantBuildJobDefinition(e);
        } catch (VetoException e) {
            throw new CantBuildJobDefinition(e);
        }
        // don't need to check values, the props are required
        String name = properties.getString(JobProperties.JOB_NAME);
        String host = properties.getString(JobProperties.JOB_HOST);

        // this can be null
        Object exeDate = properties.getValue(JobProperties.JOB_DATE);
        JobDefinition jdef = new JobDefinition(name, host, exeDate != null ? (Date)exeDate : null,
                getJobDataFromProperties());
        return jdef;
    }
	/**
	 * @throws CantBuildJobData
	 * @see com.ixora.jobs.ui.JobDefinitionPanel#getJobLibraryDefinition()
	 */
	public JobLibraryDefinition getJobLibraryDefinition() throws CantBuildJobDefinition, CantBuildJobData {
        try {
            // apply changes
            editor.applyChanges();
        } catch (InvalidPropertyValue e) {
            throw new CantBuildJobDefinition(e);
        } catch (VetoException e) {
            throw new CantBuildJobDefinition(e);
        }
        // don't need to check values, the props are required
        String name = properties.getString(JobProperties.JOB_NAME);
        String host = properties.getString(JobProperties.JOB_HOST);

        JobLibraryDefinition jdef = new JobLibraryDefinition(name, host, getJobDataFromProperties());
        return jdef;
	}

	/**
	 * Subclasses must build job data from properties.
	 * @return
	 * @throws CantBuildJobData
	 */
	protected abstract JobData getJobDataFromProperties() throws CantBuildJobData;

    /**
     * @see com.ixora.jobs.ui.JobDefinitionPanel#setJobDefinition(com.ixora.rms.internal.jobs.JobLibraryDefinition)
     */
    public void setJobDefinition(JobDefinition def) {
        properties.setString(JobProperties.JOB_NAME, def.getName());
        properties.setString(JobProperties.JOB_HOST, def.getHost());
        Date date = def.getDate();
        if(date != null) {
            properties.setDate(JobProperties.JOB_DATE, date);
        }
    }

    /**
	 * @see com.ixora.jobs.ui.JobDefinitionPanel#setJobDefinition(com.ixora.jobs.library.JobLibraryDefinition)
	 */
	public void setJobDefinition(JobLibraryDefinition def) {
        properties.setString(JobProperties.JOB_NAME, def.getName());
        properties.setString(JobProperties.JOB_HOST, def.getHost());
        linkWithEditor();
	}

	/**
     * Places the job properties in the editor.
     */
    protected void linkWithEditor() {
        editor.setTypedProperties(JobsComponent.NAME, properties);
    }
}
