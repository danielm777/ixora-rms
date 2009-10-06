/*
 * Created on 25-Sep-2004
 */
package com.ixora.jobs.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JTable;

import com.ixora.jobs.JobDefinition;
import com.ixora.jobs.JobHistory;
import com.ixora.jobs.JobId;
import com.ixora.jobs.JobsComponent;
import com.ixora.jobs.library.JobLibraryDefinition;
import com.ixora.jobs.services.JobEngineService;
import com.ixora.jobs.services.JobLibraryService;
import com.ixora.jobs.ui.messages.Msg;
import com.ixora.rms.HostState;
import com.ixora.common.MessageRepository;
import com.ixora.common.ui.AppDialog;
import com.ixora.common.ui.UIUtils;
import com.ixora.common.ui.actions.ActionClose;
import com.ixora.rms.services.HostMonitorService;
import com.ixora.rms.ui.RMSViewContainer;

/**
 * @author Daniel Moraru
 */
public final class JobManagerDialog extends AppDialog implements JobManagerViewer {
    /** JobEngineService */
    private JobEngineService rmsJobEngineService;
    /** JobLibraryService */
    private JobLibraryService rmsJobLibraryService;
    /** HostMonitorService */
    private HostMonitorService rmsHostMonitorService;
    /** Job manager panel */
    private JobManagerPanel jobPanel;
    /** View container */
    private RMSViewContainer viewContainer;
    // actions
    private ActionCancelJob actionCancelJob;
    private ActionRemoveJob actionRemoveJob;
    private ActionViewJob actionViewJob;
    private ActionCreateJobLike actionCreateJobLike;
    private ActionScheduleJob actionScheduleJob;
	private ActionShowJobLibrary actionShowJobLibrary;

    /**
     * Constructor.
     * @param vc
     * @param jes
     * @param jls
     * @param hms
     */
    public JobManagerDialog(RMSViewContainer vc,
            JobEngineService jes,
            JobLibraryService jls,
            HostMonitorService hms) {
        super(vc.getAppFrame(), VERTICAL);
        initialize(vc, jes, jls, hms);
    }

    /**
     * @see com.ixora.jobs.ui.JobManagerViewer#createJobDefinition(JobManagerViewer.Callback, JobDefinition)
     */
    public void createJobDefinition(final Callback cb, JobDefinition def) {
        String[] hosts = getAvailableHosts();
        JobDefinitionEditor jeditor = new JobDefinitionEditor(
        		new JobDefinitionEditor.Callback() {
					public boolean jobDefinition(JobDefinitionEditor dlg, JobDefinition def) throws Exception {
						cb.jobDefinition(def);
						return true;
					}
					public boolean jobLibraryDefinition(JobDefinitionEditor dlg, JobLibraryDefinition def) throws Exception {
						return true;
					}
        		},
        		this.viewContainer, this.rmsJobLibraryService, hosts, false, false);
        if(def != null) {
            jeditor.setJobDefinition(def);
        }
        UIUtils.centerDialogAndShow(this, jeditor);
    }

    /**
     * @see com.ixora.jobs.ui.JobManagerViewer#getViewContainer()
     */
    public RMSViewContainer getViewContainer() {
        return this.viewContainer;
    }

    /**
     * @see com.ixora.jobs.ui.JobManagerViewer#getWindow()
     */
    public Window getWindow() {
        return this;
    }

    /**
     * @see com.ixora.jobs.ui.JobManagerViewer#getJobTable()
     */
    public JTable getJobTable() {
        return this.jobPanel.getJTableJobHistory();
    }

    /**
     * @see com.ixora.jobs.ui.JobManagerViewer#getJobHistory()
     */
    public JobHistory getJobHistory() {
        return this.rmsJobEngineService.getJobHistory();
    }

    /**
     * @see com.ixora.jobs.ui.JobManagerViewer#refreshJobTable(JobId)
     */
    public void refreshJobTable(JobId focus) {
        this.jobPanel.refreshJobHistoryTable(focus);
    }

    /**
     * @see com.ixora.jobs.ui.JobManagerViewer#showJobDefinition(com.ixora.rms.internal.jobs.JobLibraryDefinition)
     */
    public void showJobDefinition(JobDefinition def) {
        String[] hosts = getAvailableHosts();
        JobDefinitionEditor jeditor = new JobDefinitionEditor(null, this.viewContainer,
        		this.rmsJobLibraryService, hosts, false, true);
        jeditor.setJobDefinition(def);
        UIUtils.centerDialogAndShow(this, jeditor);
    }

    /**
     * @see com.ixora.common.ui.AppDialog#getDisplayPanels()
     */
    protected Component[] getDisplayPanels() {
        return new Component[]{jobPanel};
    }

    /**
     * @see com.ixora.common.ui.AppDialog#getButtons()
     */
    protected JButton[] getButtons() {
        return new JButton[]{
           new JButton(actionShowJobLibrary),
           new JButton(actionScheduleJob),
           new JButton(new ActionClose(){
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        })};
    }

    /**
     * Initializes this dialog.
     * @param vc
     * @param jes
     * @param jls
     * @param hms
     */
    private void initialize(RMSViewContainer vc, JobEngineService jes, JobLibraryService jls, HostMonitorService hms) {
        if(vc == null) {
            throw new IllegalArgumentException("null view container");
        }
        if(jes == null) {
            throw new IllegalArgumentException("null job engine service");
        }
        if(jls == null) {
            throw new IllegalArgumentException("null job library service");
        }
        if(hms == null) {
            throw new IllegalArgumentException("null host monitor service");
        }
        setTitle(MessageRepository.get(JobsComponent.NAME, Msg.TITLE_DIALOGS_JOBMANAGER));

        this.viewContainer = vc;
        this.rmsJobEngineService = jes;
        this.rmsJobLibraryService = jls;
        this.rmsHostMonitorService = hms;
        this.actionRemoveJob = new ActionRemoveJob(this);
        this.actionCancelJob = new ActionCancelJob(
                this,
                this.rmsJobEngineService);
        this.actionScheduleJob = new ActionScheduleJob(
                this,
                this.rmsJobEngineService);
        this.actionShowJobLibrary = new ActionShowJobLibrary(
                this,
                this.rmsJobLibraryService);
        this.actionCreateJobLike = new ActionCreateJobLike(
                this,
                this.rmsJobEngineService);
        this.actionViewJob = new ActionViewJob(this);
        this.jobPanel = new JobManagerPanel(this.actionCancelJob,
                this.actionRemoveJob,
                this.actionViewJob,
                this.actionCreateJobLike,
                this.rmsJobEngineService);

        this.jobPanel.setPreferredSize(new Dimension(700, 450));
        buildContentPane();
    }

    /**
     * @return
     */
    private String[] getAvailableHosts() {
        HostState[] hs = this.rmsHostMonitorService.getHostsStates();
        String[] hosts = null;
        if(hs != null && hs.length > 0) {
            hosts = new String[hs.length];
            for(int i = 0; i < hosts.length; i++) {
                hosts[i] = hs[i].getHost();
             }
        }
        return hosts;
    }
}
