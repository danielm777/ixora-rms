/*
 * Created on 25-Sep-2004
 */
package com.ixora.jobs.ui.library;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.ixora.jobs.JobDefinition;
import com.ixora.jobs.JobsComponent;
import com.ixora.jobs.library.JobLibraryDefinition;
import com.ixora.jobs.library.JobLibraryId;
import com.ixora.jobs.services.JobLibraryService;
import com.ixora.jobs.ui.JobDefinitionEditor;
import com.ixora.jobs.ui.messages.Msg;
import com.ixora.common.MessageRepository;
import com.ixora.common.exception.AppException;
import com.ixora.common.ui.AppDialog;
import com.ixora.common.ui.AppViewContainer;
import com.ixora.common.ui.UIConfiguration;
import com.ixora.common.ui.UIExceptionMgr;
import com.ixora.common.ui.UIFactoryMgr;
import com.ixora.common.ui.UIUtils;
import com.ixora.common.ui.actions.ActionClose;
import com.ixora.common.ui.jobs.UIWorkerJobDefault;
import com.ixora.common.ui.popup.PopupListener;
import com.ixora.rms.exception.RMSException;

/**
 * @author Daniel Moraru
 */
public final class JobLibraryDialog extends AppDialog {
    /** JobLibraryService */
    private JobLibraryService fJobLibrary;
    private JPanel fMainPanel;
    private AppViewContainer fViewContainer;
    private Action fActionSelectJob;
	private Action fActionAddJob;
	private Action fActionRemoveJob;
	private Action fActionEditJob;
	private Action fActionCreateJobLike;
    private JobLibraryTableModel fTableModel;
    private JTable fTable;
    private boolean fSelectMode;
    private JobLibraryDefinition fSelectedJob;
    private JPopupMenu jPopupMenu;
	private EventHandler fEventHandler;

    /**
	 * Event handler.
	 */
	private final class EventHandler extends PopupListener implements ListSelectionListener {
        /**
		 * @see com.ixora.common.ui.popup.PopupListener#showPopup(java.awt.event.MouseEvent)
		 */
		protected void showPopup(MouseEvent e) {
			if(e.getSource() == fTable) {
				handleShowPopupOnJobTable(e);
				return;
			}
		}
		/**
		 * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
		 */
		public void valueChanged(ListSelectionEvent e) {
			if(e.getValueIsAdjusting()) {
				return;
			}
			ListSelectionModel lsm = (ListSelectionModel)e.getSource();
			if(lsm.isSelectionEmpty()) {
				return;
			} else {
				if(lsm == fTable.getSelectionModel()) {
					// row selected
					if(fSelectMode) {
						fActionSelectJob.setEnabled(true);
					} else {
						fActionEditJob.setEnabled(true);
						fActionRemoveJob.setEnabled(true);
						fActionCreateJobLike.setEnabled(true);
					}
					return;
				}
			}
		}
	}

    private final class ActionSelectJob extends AbstractAction {
		public ActionSelectJob() {
			super();
			UIUtils.setUsabilityDtls(MessageRepository.get(JobsComponent.NAME, Msg.ACTIONS_LIBRARY_SELECT_JOB), this);
			ImageIcon icon = UIConfiguration.getIcon("lib_select_job.gif");
			if(icon != null) {
				putValue(Action.SMALL_ICON, icon);
			}
		}
		/**
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent arg0) {
			handleSelectJob();
		}
    }

    private final class ActionAddJob extends AbstractAction {
		public ActionAddJob() {
			super();
			UIUtils.setUsabilityDtls(MessageRepository.get(JobsComponent.NAME, Msg.ACTIONS_LIBRARY_ADD_JOB), this);
			ImageIcon icon = UIConfiguration.getIcon("lib_add_job.gif");
			if(icon != null) {
				putValue(Action.SMALL_ICON, icon);
			}
		}
		/**
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent arg0) {
			handleAddJobToLibrary();
		}
    }

    private final class ActionRemoveJobs extends AbstractAction {
		public ActionRemoveJobs() {
			super();
			UIUtils.setUsabilityDtls(MessageRepository.get(JobsComponent.NAME, Msg.ACTIONS_LIBRARY_REMOVE_JOBS), this);
			ImageIcon icon = UIConfiguration.getIcon("lib_rem_job.gif");
			if(icon != null) {
				putValue(Action.SMALL_ICON, icon);
			}
		}
		/**
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent arg0) {
			handleRemoveJobsFromLibrary();
		}
    }

    private final class ActionCreateJobLike extends AbstractAction {
		public ActionCreateJobLike() {
			super();
			UIUtils.setUsabilityDtls(MessageRepository.get(JobsComponent.NAME, Msg.ACTIONS_CREATE_JOB_LIKE), this);
			ImageIcon icon = UIConfiguration.getIcon("create_job_like.gif");
			if(icon != null) {
				putValue(Action.SMALL_ICON, icon);
			}
		}
		/**
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent arg0) {
			handleCreateJobLike();
		}
	}

    private final class ActionEditJob extends AbstractAction {
		public ActionEditJob() {
			super();
			UIUtils.setUsabilityDtls(MessageRepository.get(JobsComponent.NAME, Msg.ACTIONS_EDIT_JOB), this);
			ImageIcon icon = UIConfiguration.getIcon("lib_edit_job.gif");
			if(icon != null) {
				putValue(Action.SMALL_ICON, icon);
			}
		}
		/**
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent arg0) {
			handleEditJob();
		}
	}

	/**
     * Constructor.
     * @param vc
     * @param jl
     * @throws RMSException
     */
    public JobLibraryDialog(
    		AppViewContainer vc,
            JobLibraryService jl,
            boolean selectMode) throws AppException {
        super(vc.getAppFrame(), VERTICAL);
        initialize(vc, jl, selectMode);
    }

    /**
     * @return
     */
    public JobLibraryDefinition getSelectedJob() {
    	return fSelectedJob;
    }

    /**
	 *
	 */
	private void handleSelectJob() {
		try {
			int row = fTable.getSelectedRow();
			if(row < 0) {
				return;
			}
			fSelectedJob = fTableModel.getEntryAtRow(row);
			dispose();
		} catch(Exception e) {
			UIExceptionMgr.userException(e);
		}
	}

    /**
	 *
	 */
	private void handleAddJobToLibrary() {
		try {
			openUpJobEditor(null, false);
		} catch(Exception e) {
			UIExceptionMgr.userException(e);
		}
	}

    /**
	 * Reload jobs to refresh view.
     * @throws RMSException
	 */
	private void reloadJobs() throws AppException {
		Map<JobLibraryId, JobLibraryDefinition> entries = fJobLibrary.getAllJobs();
		fTableModel.setEntries(entries.values());
	}

	/**
	 *
	 */
	private void handleRemoveJobsFromLibrary() {
		try {
			int[] rows = fTable.getSelectedRows();
			if(rows == null || rows.length == 0) {
				return;
			}
			// TODO localize
			if(!UIUtils.getBooleanYesNoInput(this,
					"Delete Jobs",
					"Are you sure you want to remove the selected jobs?")) {
				return;
			}
			final List<JobLibraryId> ids = new LinkedList<JobLibraryId>();
			for(int i : rows) {
				JobLibraryDefinition entry = fTableModel.getEntryAtRow(i);
				ids.add(new JobLibraryId(entry.getName()));
			}
			fViewContainer.runJob(new UIWorkerJobDefault(
					this, Cursor.WAIT_CURSOR, ""){
				public void work() throws Throwable {
					fJobLibrary.deleteJobs(ids);
				}
				public void finished(Throwable ex) throws Throwable {
					if(ex != null) {
						UIExceptionMgr.userException(ex);
					} else {
						reloadJobs();
					}
				}
			});
		} catch(Exception e) {
			UIExceptionMgr.userException(e);
		}
	}

	/**
	 *
	 */
	private void handleCreateJobLike() {
		try {
			int[] rows = fTable.getSelectedRows();
			if(rows == null || rows.length == 0) {
				return;
			}
			int idx = rows[0];
			JobLibraryDefinition entry = fTableModel.getEntryAtRow(idx);
			openUpJobEditor(entry, false);
		}
		catch(Exception e) {
			UIExceptionMgr.userException(e);
		}
	}

	/**
	 *
	 */
	private void handleEditJob() {
		try {
			int[] rows = fTable.getSelectedRows();
			if(rows == null || rows.length == 0) {
				return;
			}
			int idx = rows[0];
			JobLibraryDefinition entry = fTableModel.getEntryAtRow(idx);

			openUpJobEditor(entry, true);
		}
		catch(Exception e) {
			UIExceptionMgr.userException(e);
		}
	}

	/**
	 * Opens the job editor.
	 * @param originalDef
	 */
	private void openUpJobEditor(final JobLibraryDefinition originalDef, final boolean forEdit) {
		JobDefinitionEditor editor = new JobDefinitionEditor(
				new JobDefinitionEditor.Callback(){
					public boolean jobDefinition(JobDefinitionEditor dlg, JobDefinition def) throws Exception {
						return true;
					}
					public boolean jobLibraryDefinition(final JobDefinitionEditor dlg,
							final JobLibraryDefinition def) throws Exception {
						final JobLibraryId[] jobsToRemove = new JobLibraryId[2];
						if(forEdit) {
							// remove existing job first
							if(!def.getName().equalsIgnoreCase(originalDef.getName())) {
								JobLibraryId newJobId = new JobLibraryId(def.getName());
								if(fJobLibrary.getJob(newJobId) != null) {
									// ask for permission to replace existing job;
									// TODO localize
									if(UIUtils.getBooleanYesNoInput(dlg,
											"Replace Job",
											"A job named " + def.getName()
											+ " already exists in the library. "
											+ "Would you like to replace it?")) {
										jobsToRemove[0] = newJobId;
									} else {
										// don't dispose editor
										return false;
									}
								}
							}
							jobsToRemove[1] = new JobLibraryId(originalDef.getName());
						}
						fViewContainer.runJob(new UIWorkerJobDefault(
								JobLibraryDialog.this, Cursor.WAIT_CURSOR, ""){
							public void work() throws Throwable {
								// any jobs to delete?
								for(JobLibraryId jid : jobsToRemove) {
									if(jid != null) {
										fJobLibrary.deleteJob(jid);
									}
								}
								fJobLibrary.storeJob(def);
							}
							public void finished(Throwable ex) throws Throwable {
								if(ex != null) {
									UIExceptionMgr.userException(ex);
								} else {
									reloadJobs();
								}
							}
						});
						return true;
					}
				},
				fViewContainer, fJobLibrary, null, true, false);
		if(originalDef != null) {
			editor.setJobLibraryDefinition(originalDef);
		}
		UIUtils.centerDialogAndShow(this, editor);
	}

	/**
     * @see com.ixora.common.ui.AppDialog#getDisplayPanels()
     */
    protected Component[] getDisplayPanels() {
        return new Component[]{fMainPanel};
    }

    /**
     * @see com.ixora.common.ui.AppDialog#getButtons()
     */
    protected JButton[] getButtons() {
    	if(fSelectMode) {
	        return new JButton[]{
	 	           new JButton(fActionSelectJob),
	 	           new JButton(new ActionClose(){
	 	            public void actionPerformed(ActionEvent e) {
	 	                dispose();
	 	            }
	 	        })};
    	} else {
	        return new JButton[]{
	           new JButton(fActionAddJob),
	           new JButton(fActionEditJob),
	           new JButton(fActionRemoveJob),
	           new JButton(fActionCreateJobLike),
	           new JButton(new ActionClose(){
	            public void actionPerformed(ActionEvent e) {
	                dispose();
	            }
	        })};
    	}
    }

    /**
     * Initializes this dialog.
     * @param vc
     * @param jl
     * @param selectMode
     * @throws RMSException
     */
    private void initialize(AppViewContainer vc, JobLibraryService jl, boolean selectMode) throws AppException {
        if(vc == null) {
            throw new IllegalArgumentException("null view container");
        }
        if(jl == null) {
            throw new IllegalArgumentException("null job library service");
        }
        setTitle(MessageRepository.get(JobsComponent.NAME, Msg.TITLE_DIALOGS_JOBLIBRARY));
        setModal(true);
        fViewContainer = vc;
        fJobLibrary = jl;
        fSelectMode = selectMode;
        fEventHandler = new EventHandler();
        fActionSelectJob = new ActionSelectJob();
        fActionAddJob = new ActionAddJob();
        fActionEditJob = new ActionEditJob();
        fActionRemoveJob = new ActionRemoveJobs();
        fActionCreateJobLike = new ActionCreateJobLike();
        fTableModel = new JobLibraryTableModel(fJobLibrary.getAllJobs().values());
        fTable = new JTable(fTableModel);
        fMainPanel = new JPanel(new BorderLayout());
        JScrollPane sp = UIFactoryMgr.createScrollPane();
        sp.setViewportView(fTable);
        sp.setPreferredSize(new Dimension(300, 250));
        fMainPanel.add(sp, BorderLayout.CENTER);

        if(!selectMode) {
	        fTable.addMouseListener(fEventHandler);
    	}
        fTable.getSelectionModel().addListSelectionListener(fEventHandler);
        fActionRemoveJob.setEnabled(false);
        fActionEditJob.setEnabled(false);
		fActionCreateJobLike.setEnabled(false);
		fActionSelectJob.setEnabled(false);

        buildContentPane();
    }

	/**
	 * @param e
	 */
	private void handleShowPopupOnJobTable(MouseEvent ev) {
		getJPopupMenu().show(ev.getComponent(), ev.getX(), ev.getY());
	}

	/**
	 * @return javax.swing.JPopupMenu
	 */
	private JPopupMenu getJPopupMenu() {
		if(jPopupMenu == null) {
			jPopupMenu = UIFactoryMgr.createPopupMenu();
			jPopupMenu.add(UIFactoryMgr.createMenuItem(fActionAddJob));
			jPopupMenu.add(UIFactoryMgr.createMenuItem(fActionEditJob));
			jPopupMenu.add(UIFactoryMgr.createMenuItem(fActionRemoveJob));
			jPopupMenu.add(UIFactoryMgr.createMenuItem(fActionCreateJobLike));
		}
		return jPopupMenu;
	}
}
