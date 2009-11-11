/*
 * Created on 27-Sep-2004
 */
package com.ixora.jobs.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import com.ixora.jobs.JobData;
import com.ixora.jobs.JobDefinition;
import com.ixora.jobs.JobsComponent;
import com.ixora.jobs.impl.command.ExternalCommandJobData;
import com.ixora.jobs.impl.commandtelnet.TelnetCommandJobData;
import com.ixora.jobs.library.JobLibraryDefinition;
import com.ixora.jobs.services.JobLibraryService;
import com.ixora.jobs.ui.library.JobLibraryDialog;
import com.ixora.jobs.ui.messages.Msg;
import com.ixora.common.MessageRepository;
import com.ixora.common.exception.AppRuntimeException;
import com.ixora.common.ui.AppDialog;
import com.ixora.common.ui.AppViewContainer;
import com.ixora.common.ui.UIConfiguration;
import com.ixora.common.ui.UIExceptionMgr;
import com.ixora.common.ui.UIFactoryMgr;
import com.ixora.common.ui.UIUtils;
import com.ixora.common.ui.actions.ActionClose;
import com.ixora.common.ui.actions.ActionOk;

/**
 * JobDefinitionEditor.
 * @author Daniel Moraru
 */
// NOTE this class is not yet plugin oriented; it's still working with hardcoded job types
public class JobDefinitionEditor extends AppDialog {
	private static final long serialVersionUID = 7488406551250776696L;

	public interface Callback {
		/**
		 * @param def
		 * @return true to dispose the dialog false otherwise
		 */
		boolean jobDefinition(JobDefinitionEditor editor, JobDefinition def) throws Exception;
		/**
		 * @param def
		 * @return true to dispose the dialog false otherwise
		 */
		boolean jobLibraryDefinition(JobDefinitionEditor editor, JobLibraryDefinition def) throws Exception;
	}

    /** Panel displaying available job types */
    private JPanel fJobTypePanel;
    /** Job definition container panel */
    private JobDefinitionPanelContainer fJobDefContainerPanel;
    /** List with all available job types */
    private JComboBox fJobTypesList;
    /** Hosts */
    private String[] fHosts;
    /** Event handler */
    private EventHandler fEventHandler;
    /** Edits a job for library */
	private boolean fForLibrary;
    /** Job engine */
    private JobLibraryService fJobLibrary;
    /** View container */
	private AppViewContainer fViewContainer;
	/** Callback */
	private Callback fCallback;
	/** Whether or not the editor is in the view mode */
	private boolean fViewMode;

	@SuppressWarnings("serial")
	private final class ActionShowJobLibrary extends AbstractAction {
	    public ActionShowJobLibrary() {
			super();
			UIUtils.setUsabilityDtls(MessageRepository.get(JobsComponent.NAME, Msg.ACTIONS_SHOW_JOB_LIBRARY), this);
			ImageIcon icon = UIConfiguration.getIcon("show_job_lib.gif");
			if(icon != null) {
				putValue(Action.SMALL_ICON, icon);
			}
		}
		/**
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent arg0) {
			handleLoadJobFromLibrary();
		}
	}

    /**
     * Event handler.
     */
    private final class EventHandler implements ItemListener {
        /**
         * @see java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
         */
        public void itemStateChanged(ItemEvent e) {
            if(e.getSource() == getJobTypeList()) {
                handleJobTypeChanged();
            }
        }
    }

    /**
     * Job type data
     */
    private static final class JobType {
        private String type;
        private String translatedType;

        /**
         * Constructor.
         * @param type
         */
        JobType(String type) {
            this.type = type;
            this.translatedType = MessageRepository.get(JobsComponent.NAME, type);
        }
        /**
         * @return the translatedType.
         */
        String getTranslatedType() {
            return translatedType;
        }
        /**
         * @return the type.
         */
        String getType() {
            return type;
        }
        /**
         * @see java.lang.Object#toString()
         */
        public String toString() {
            return translatedType;
        }
    }

    /**
     * @param vc
     * @param jls
     * @param hosts
     * @param forLibrary
     * @param viewMode
     */
    public JobDefinitionEditor(Callback cb, AppViewContainer vc, JobLibraryService jls,
    		String[] hosts, boolean forLibrary, boolean viewMode) {
        super(vc.getAppFrame(), VERTICAL);
        initialize(cb, vc, jls, hosts, forLibrary, viewMode);
    }

    /**
	 *
	 */
	private void handleLoadJobFromLibrary() {
		try {
			JobLibraryDialog dlg = new JobLibraryDialog(
					this.fViewContainer, this.fJobLibrary, true);
			UIUtils.centerDialogAndShow(this, dlg);
			JobLibraryDefinition ret = dlg.getSelectedJob();
			if(ret != null) {
				if(fForLibrary) {
					setJobLibraryDefinition(ret);
				} else {
					setJobDefinition(new JobDefinition(ret.getName(), ret.getHost(), null, ret.getJobData()));
				}
			}
		} catch(Exception e) {
			UIExceptionMgr.userException(e);
		}
	}

    /**
     * Sets the job definition to edit
     * @param def
     */
    public void setJobDefinition(JobDefinition def) {
        JobData jd = def.getJobData();
        if(jd == null) {
            return;
        }
        updateComboForJobData(jd);
        getJobDefContainerPanel().setJobDefinition(def);
    }

    private void updateComboForJobData(JobData jd) {
        int idx = -1;
        if(jd instanceof ExternalCommandJobData) {
            idx = getIndexOfJobType(com.ixora.jobs.impl.command.messages.Msg.TEXT_JOB_EXTERNAL_COMMAND_NAME);
        } else if(jd instanceof TelnetCommandJobData) {
        	idx = getIndexOfJobType(com.ixora.jobs.impl.commandtelnet.messages.Msg.TEXT_JOB_TELNET_EXTERNAL_COMMAND_NAME);
        }
        if(idx < 0) {
            return;
        }
        getJobTypeList().setSelectedIndex(idx);
    }

    /**
     * Sets the job definition to edit
     * @param def
     */
    public void setJobLibraryDefinition(JobLibraryDefinition def) {
    	if(!fForLibrary) {
    		throw new AppRuntimeException("Not in library mode");
    	}
        JobData jd = def.getJobData();
        if(jd == null) {
            return;
        }
        updateComboForJobData(jd);
        getJobDefContainerPanel().setJobDefinition(def);
    }

    /**
     * @param cb
     * @param vc
     * @param jls
     * @param hosts
     * @param forLibrary
     * @param viewMode
     *
     */
    private void initialize(Callback cb, AppViewContainer vc, JobLibraryService jls,
    		String[] hosts, boolean forLibrary, boolean viewMode) {
        setModal(true);
        setPreferredSize(new Dimension(400, 400));
        setTitle(MessageRepository.get(JobsComponent.NAME, Msg.TITLE_JOB_EDITOR));
        fCallback = cb;
        this.fJobLibrary = jls;
        this.fViewContainer = vc;
        this.fForLibrary = forLibrary;
        this.fViewMode = viewMode;
        this.fHosts = hosts;
        this.fEventHandler = new EventHandler();
        getJobTypeList().addItemListener(this.fEventHandler);
       	getJobTypeList().setEnabled(!fViewMode);
        buildContentPane();
    }

    /**
     * @see com.ixora.common.ui.AppDialog#getDisplayPanels()
     */
    protected Component[] getDisplayPanels() {
        return new Component[]{getJobTypePanel(), getJobDefContainerPanel()};
    }

    /**
     * @see com.ixora.common.ui.AppDialog#getButtons()
     */
    @SuppressWarnings("serial")
	protected JButton[] getButtons() {
    	JButton ok = new JButton(new ActionOk() {
                    public void actionPerformed(ActionEvent e) {
                        handleOk();
                    }
                });
        JButton close = new JButton(new ActionClose() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        if(fViewMode) {
	        return new JButton[]{
	        		close
	        };
        } else if(!fForLibrary) {
	        return new JButton[]{
	        		ok,
	        		new JButton(new ActionShowJobLibrary()),
	        		close
	        };
    	} else {
	        return new JButton[]{
	        		ok,
	        		close
	        };
    	}
    }

    /**
     * @return the jobDefPanel.
     */
    private JobDefinitionPanelContainer getJobDefContainerPanel() {
        if(fJobDefContainerPanel == null) {
            fJobDefContainerPanel = new JobDefinitionPanelContainer(fHosts, fForLibrary, fViewMode);
            getJobDefContainerPanel().showJobDefinitionPanelFor(
                    ((JobType)getJobTypeList().getItemAt(0)).getType());
        }
        return fJobDefContainerPanel;
    }

    /**
     * @return the jobTypePanel.
     */
    private JPanel getJobTypePanel() {
        if(fJobTypePanel == null) {
            fJobTypePanel = new JPanel(new BorderLayout());
            fJobTypePanel.setBorder(UIFactoryMgr.createTitledBorder(
                    MessageRepository.get(JobsComponent.NAME, Msg.TEXT_JOB_TYPE)));
            fJobTypePanel.setPreferredSize(new Dimension(200, 60));
            fJobTypePanel.add(getJobTypeList(), BorderLayout.NORTH);
        }
        return fJobTypePanel;
    }

    /**
     * @return
     */
    private JComboBox getJobTypeList() {
        if(fJobTypesList == null) {
            fJobTypesList = UIFactoryMgr.createComboBox();
            fJobTypesList.addItem(new JobType(com.ixora.jobs.impl.command.messages.Msg.TEXT_JOB_EXTERNAL_COMMAND_NAME));
            fJobTypesList.addItem(new JobType(com.ixora.jobs.impl.commandtelnet.messages.Msg.TEXT_JOB_TELNET_EXTERNAL_COMMAND_NAME));
        }
        return fJobTypesList;
    }

    /**
     * @param e
     */
    private void handleJobTypeChanged() {
        JobType jt = (JobType)getJobTypeList().getSelectedItem();
        if(jt == null) {
            return;
        }
        getJobDefContainerPanel().showJobDefinitionPanelFor(jt.getType());
    }

    /**
     * Creates the job definition and returns
     */
    private void handleOk() {
        try {
        	boolean dispose;
        	if(!fForLibrary) {
        		dispose = fCallback.jobDefinition(this, getJobDefContainerPanel().getJobDefinition());
        	} else {
        		dispose = fCallback.jobLibraryDefinition(this, getJobDefContainerPanel().getJobLibraryDefinition());
        	}
        	if(dispose) {
        		dispose();
        	}
        } catch(Exception e) {
            UIExceptionMgr.userException(e);
        }
    }

    /**
     * @param type
     * @return the index of the job type in the list of types
     * or -1 if not found
     */
    private int getIndexOfJobType(String type) {
        int size = getJobTypeList().getModel().getSize();
        JobType jt;
        for(int i = 0; i < size; i++) {
            jt = (JobType)getJobTypeList().getItemAt(i);
            if(jt.getType().equals(type)) {
                return i;
            }
        }
        return -1;
    }
}
