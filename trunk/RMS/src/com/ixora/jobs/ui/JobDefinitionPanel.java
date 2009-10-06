/*
 * Created on 28-Sep-2004
 */
package com.ixora.jobs.ui;

import java.awt.LayoutManager;

import javax.swing.JPanel;

import com.ixora.jobs.JobDefinition;
import com.ixora.jobs.exception.CantBuildJobData;
import com.ixora.jobs.exception.CantBuildJobDefinition;
import com.ixora.jobs.library.JobLibraryDefinition;

/**
 * @author Daniel Moraru
 */
public abstract class JobDefinitionPanel extends JPanel {

    /**
     * Constructor.
     * @param layout
     */
    protected JobDefinitionPanel(LayoutManager layout) {
        super(layout);
    }
    /**
     * @return the hosted job definition
     * @throws CantBuildJobDefinition
     * @throws CantBuildJobData 
     */
    public abstract JobDefinition getJobDefinition() throws CantBuildJobDefinition, CantBuildJobData;
	/**
	 * @return
	 * @throws CantBuildJobDefinition
	 * @throws CantBuildJobData 
	 */
	public abstract JobLibraryDefinition getJobLibraryDefinition() throws CantBuildJobDefinition, CantBuildJobData;
    /**
     * Sets the job definition to display.
     * @param def
     */
    public abstract void setJobDefinition(JobDefinition def);
	/**
	 * @param def
	 */
	public abstract void setJobDefinition(JobLibraryDefinition def);
}
