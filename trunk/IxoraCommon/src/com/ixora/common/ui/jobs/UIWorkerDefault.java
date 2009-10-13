/*
 * Created on 13-Dec-2003
 */
package com.ixora.common.ui.jobs;

import java.awt.Cursor;

import javax.swing.DefaultBoundedRangeModel;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

import com.ixora.common.ProgressProvider;
import com.ixora.common.thread.RunQueue;
import com.ixora.common.ui.NonFatalErrorHandler;
import com.ixora.common.ui.UIExceptionMgr;

/**
 * Executor of UI jobs.
 * @author Daniel Moraru
 */
// Limitations:
// 1. The mouse cursor will always be set to the default cursor
// type when jobs are finished
// 2. If two jobs are run at the same time
// and they have the same owner component the first job to
// finish will reset the mouse cursor to the default value
public final class UIWorkerDefault implements UIWorker {
    /**
	 * Asynch processor used to enqueue all the asynchronous
	 * jobs. Using a single thread to process all the asynch jobs
	 * is the ideal option since apart it ensures smooth UI,
	 * all asynch jobs will be guaranteed a single threaded environment
	 * and it makes the UI reflection of the asynch jobs much more natural
	 * (for instance it will not happen for two asynch jobs to send
	 * mixed-up progress signals to the UI).
	 */
	private RunQueue fProcessor;
	/** Label for job description */
	private JLabel fLabelJobDesc;
    /** Label for non fatal errors */
    private NonFatalErrorHandler fNonFatalErrorHandler;
	/** Progress bar for job progress */
	private JProgressBar fProgressBar;
	/** Event handler */
	private EventHandler fEventHandler;
	/** Initial label text */
	private String fLabelJobDescText;

	/**
	 * Event handler.
	 */
	private final class EventHandler implements ProgressProvider.Listener {
		/**
		 * @see com.ixora.common.ProgressProvider.Listener#progress(float)
		 */
		public void progress(final float pct) {
			if(SwingUtilities.isEventDispatchThread()) {
				handleProgress(pct);
			} else {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						handleProgress(pct);
					}
				});
			}
		}

		/**
		 * @see com.ixora.common.ProgressProvider.Listener#taskStarted(java.lang.String)
		 */
		public void taskStarted(final String task) {
			if(SwingUtilities.isEventDispatchThread()) {
				handleTaskStarted(task);
			} else {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						handleTaskStarted(task);
					}
				});
			}
		}

        /**
         * @see com.ixora.common.ProgressProvider.Listener#nonFatalError(java.lang.String, java.lang.Throwable)
         */
        public void nonFatalError(final String error, final Throwable t) {
            if(SwingUtilities.isEventDispatchThread()) {
                handleNonFatalError(error, t);
            } else {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        handleNonFatalError(error, t);
                    }
                });
            }
        }
	}

	/**
	 * Runnable that executes the asynch job in the processor
	 * and when the execution is finished it will run the
	 * <code>UIWorkerJob.finished()</code> in the event dispatching thread.
	 * @author Daniel Moraru
	 */
	private final class AsynchRunnable implements Runnable {
		private UIWorkerJob job;
		private Runnable runOnFinish;
		private Throwable ex;

		/**
		 * Cosntructor.
		 * @param job the job to run
		 * @param cancelJobDialog the cancel dialog assigned to this job
		 */
		public AsynchRunnable(final UIWorkerJob job, final JDialog cancelJobDialog) {
			this.job = job;
			this.runOnFinish = new Runnable() {
				public void run() {
					try {
						job.finished(ex);
						if(ex != null) {
							UIExceptionMgr.userException(ex);
						}
					} catch(Throwable e) {
						UIExceptionMgr.userException(e);
					} finally {
						if(fLabelJobDesc != null) {
							fLabelJobDesc.setText(" ");
						}
						if(fProgressBar != null) {
							fProgressBar.setValue(0);
						}
						if(cancelJobDialog != null) {
							cancelJobDialog.dispose();
						}
						resetCursor(job);
					}
				}
			};
		}

		/**
		 * @see java.lang.Runnable#run()
		 */
		public void run() {
			try {
				job.work();
			} catch(Throwable e) {
				this.ex = e;
			} finally {
				SwingUtilities.invokeLater(runOnFinish);
			}
		}

	}

	/**
	 * Constructor.
     * @param nonFatalErrorHandler handler for non-fatal errors that occurred
     * while running jobs that provide progress feedback
	 * @param labelJobDesc a label that displays the job description
	 * for the duration of the execution, it can be null
	 * @param progressBar the progress bar to display jo progress,
	 * it can be null
	 * @throws Throwable
	 */
	public UIWorkerDefault(
            NonFatalErrorHandler nonFatalErrorsHandler,
			JLabel labelJobDesc,
			JProgressBar progressBar) throws Throwable {
		super();
        this.fNonFatalErrorHandler = nonFatalErrorsHandler;
		this.fLabelJobDesc = labelJobDesc;
		this.fProgressBar = progressBar;
		this.fEventHandler = new EventHandler();
		this.fProcessor = new RunQueue();
		this.fProcessor.start();
	}

	/**
	 * @see com.ixora.common.ui.jobs.UIWorker#runJobSynch(com.ixora.common.ui.jobs.UIWorkerJob)
	 */
	public void runJobSynch(UIWorkerJob job) {
		changeCursor(job);

		if(this.fLabelJobDesc != null) {
			this.fLabelJobDesc.setIcon(null);
			this.fLabelJobDesc.setText(job.getDescription());
			// being run on the event dispatching thread
			// a call to paintImmediately is essential
			// for the text to be printed before the synchronous job
			// is started
			this.fLabelJobDesc.paintImmediately(0, 0, fLabelJobDesc.getWidth(), fLabelJobDesc.getHeight());
			this.fLabelJobDescText = job.getDescription();
		}

		ProgressProvider pp = job.getProgressProvider();
		if(pp != null) {
			pp.addListener(this.fEventHandler);
		}
		if(this.fProgressBar != null && pp != null) {
			// set up progress bar
			this.fProgressBar.setModel(new DefaultBoundedRangeModel());
			this.fProgressBar.setMaximum(100);
			this.fProgressBar.setIndeterminate(false);
			this.fProgressBar.setStringPainted(false);
		}

		Throwable ex = null;
		try {
			job.work();
		} catch(Throwable e) {
			ex = e;
		}

		if(fLabelJobDesc != null) {
			fLabelJobDesc.setText(" ");
		}
		if(fProgressBar != null) {
			fProgressBar.setValue(0);
		}

		resetCursor(job);

		try {
			job.finished(ex);
		} catch(Throwable e) {
			UIExceptionMgr.exception(e);
		}
	}

	/**
	 * @see com.ixora.common.ui.jobs.UIWorker#runJob(com.ixora.common.ui.jobs.UIWorkerJob)
	 */
	public void runJob(final UIWorkerJob job) {
		changeCursor(job);
		if(this.fLabelJobDesc != null) {
			this.fLabelJobDesc.setText(job.getDescription());
			this.fLabelJobDescText = job.getDescription();
		}
		ProgressProvider pp = job.getProgressProvider();
		if(pp != null) {
			pp.addListener(this.fEventHandler);
		}

		if(this.fProgressBar != null && pp != null) {
			// set up progress bar
			this.fProgressBar.setModel(new DefaultBoundedRangeModel());
			this.fProgressBar.setMaximum(100);
			this.fProgressBar.setIndeterminate(false);
			this.fProgressBar.setStringPainted(false);
		}

		JDialog cancelDialog = null;
		if(job instanceof UIWorkerJobCancelable) {
			UIWorkerJobCancelable cj = (UIWorkerJobCancelable)job;
			cancelDialog =
				UIWorkerJobCancelDialog.showDialog(
					cj.getOwnerComponent(), cj);
		}
		AsynchRunnable ar = new AsynchRunnable(job, cancelDialog);
		this.fProcessor.run(ar);
	}

	/**
	 * Handles progress updates.
	 * @param pct
	 */
	private void handleProgress(float pct) {
		try {
			if(this.fProgressBar != null) {
				this.fProgressBar.setValue((int)(pct));
			}
		} catch(Exception e) {
			UIExceptionMgr.exception(e);
		}
	}

	/**
	 * Handles progress updates.
	 * @param task
	 */
	private void handleTaskStarted(String task) {
		try {
			if(this.fLabelJobDesc != null) {
				this.fLabelJobDesc.setText(this.fLabelJobDescText + " " + task);
			}
		} catch(Exception e) {
			UIExceptionMgr.exception(e);
		}
	}

    /**
     * Handles non-fatal errors
     * @param error
     * @param t
     */
    private void handleNonFatalError(String error, Throwable t) {
        try {
            if(this.fNonFatalErrorHandler != null) {
                this.fNonFatalErrorHandler.nonFatalError(error, t);
            }
        } catch(Exception e) {
            UIExceptionMgr.exception(e);
        }
    }

	/**
	 * Changes the cursor for the given job.
	 * @param job
	 */
	private static void changeCursor(UIWorkerJob job) {
		job.getOwnerComponent().setCursor(
			Cursor.getPredefinedCursor(job.getCursor()));
	}

	/**
	 * Resets the cursor for the given component
	 * to the default value.
	 * @param job
	 */
	private static void resetCursor(UIWorkerJob job) {
		job.getOwnerComponent().setCursor(Cursor.getDefaultCursor());
	}
}
