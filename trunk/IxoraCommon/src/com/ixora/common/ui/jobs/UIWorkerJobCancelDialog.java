package com.ixora.common.ui.jobs;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.SwingUtilities;

import com.ixora.common.MessageRepository;
import com.ixora.common.ProgressProvider;
import com.ixora.common.messages.Msg;
import com.ixora.common.ui.AppDialog;
import com.ixora.common.ui.UIConfiguration;
import com.ixora.common.ui.UIExceptionMgr;
import com.ixora.common.ui.UIFactoryMgr;
import com.ixora.common.ui.UIUtils;
import com.ixora.common.ui.actions.ActionCancel;

/**
 * Wait dialog. Only used for cancelable ui jobs.
 * @author: Daniel Moraru
 */
final class UIWorkerJobCancelDialog extends AppDialog {
    private javax.swing.JPanel contentPane;
    private javax.swing.JLabel messageLabel;
    private javax.swing.JLabel stepLabel;
    private javax.swing.JLabel nonFatalErrorLabel;
	private javax.swing.JProgressBar progressBar;
	private UIWorkerJobCancelable job;
	private ProgressProvider progress;
	private static Dimension sizeDialog = new Dimension(250, 150);
	private static Dimension sizeLabel = new Dimension(10, 20);

	/**
	 * Event handler.
	 */
	private final class EventHandler implements ProgressProvider.Listener {
		/**
		 * @see com.ixora.common.ProgressProvider.Listener#progress(float)
		 */
		public void progress(final float pct) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					handleProgress(pct);
				}
			});
		}
		/**
		 * @see com.ixora.common.ProgressProvider.Listener#taskStarted(java.lang.String)
		 */
		public void taskStarted(final String task) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					handleTaskStarted(task);
				}
			});
		}
        /**
         * @see com.ixora.common.ProgressProvider.Listener#nonFatalError(java.lang.String, java.lang.Throwable)
         */
        public void nonFatalError(final String error, final Throwable t) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    handleNonFatalError(error, t);
                }
            });
        }
	}

	/**
	 * Shows the dialog.
	 * @param parent
	 * @param job
	 * @return
	 */
	public static JDialog showDialog(
				Window parent,
				UIWorkerJobCancelable job) {
		UIWorkerJobCancelDialog dialog;
		if(parent instanceof Dialog) {
			dialog = new UIWorkerJobCancelDialog(
				(Dialog)parent, job);
		} else {
			if(parent instanceof Frame) {
				dialog = new UIWorkerJobCancelDialog(
					(Frame)parent, job);
			} else {
				dialog = new UIWorkerJobCancelDialog(job);
			}
		}
		UIUtils.centerDialogAndShow(parent, dialog);
		return dialog;
	}

	/**
	 * Constructor.
	 * @param job
	 * @param eh
	 */
	public UIWorkerJobCancelDialog(
				UIWorkerJobCancelable job) {
		super(VERTICAL);
		initialize(job);
	}

    /**
     * Constructor.
     * @param parent
     * @param job
     */
    public UIWorkerJobCancelDialog(
    			Frame parent,
    			UIWorkerJobCancelable job) {
        super(parent, VERTICAL);
        initialize(job);
    }

	/**
	 * Constructor.
	 * @param parent
	 * @param job
	 * @param eh
	 */
	public UIWorkerJobCancelDialog(
				Dialog parent,
				UIWorkerJobCancelable job) {
		super(parent, VERTICAL);
		initialize(job);
	}

	/**
	 * @param job
	 * @param eh
	 */
	private void initialize(
					UIWorkerJobCancelable job) {
		setTitle(MessageRepository.get(Msg.COMMON_UI_WAIT_DIALOG_TITLE));
		this.progress = job.getProgressProvider();
		if(this.progress != null) {
			this.progress.addListener(new EventHandler());
		}
		this.job = job;
		setDefaultCloseOperation(
				javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		setPreferredSize(sizeDialog);
		setModal(false);
		setResizable(false);
		buildContentPane();
	}

    /**
     * Return the JDialogContentPane property value.
     * @return javax.swing.JPanel
     */
    private javax.swing.JPanel getCenterPanel() {
        if(contentPane == null) {
            contentPane = new javax.swing.JPanel();
            contentPane.setOpaque(true);
            int padding = UIConfiguration.getPanelPadding() * 2;
            contentPane.setBorder(BorderFactory.createEmptyBorder(padding, padding, padding, padding));
            contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
            getCenterPanel()
                .add(getJLabelMessage());
			if(this.progress != null) {
				getCenterPanel().add(getJLabelStep());
				getCenterPanel().add(getJProgressBar());
			}
            getCenterPanel()
                .add(getJLabelError());
        }
        return contentPane;
    }

    /**
     * @return javax.swing.JLabel
     */
    private javax.swing.JLabel getJLabelMessage() {
        if(messageLabel == null) {
        	String msg = job.getDescription();
            messageLabel = new javax.swing.JLabel(msg == null ? getTitle() : msg);
            messageLabel.setPreferredSize(sizeLabel);
        }
        return messageLabel;
    }

	/**
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getJLabelStep() {
		if(stepLabel == null) {
			stepLabel = UIFactoryMgr.createLabel(" ");
			stepLabel.setPreferredSize(sizeLabel);
		}
		return stepLabel;
	}

    /**
     * @return javax.swing.JLabel
     */
    private javax.swing.JLabel getJLabelError() {
        if(nonFatalErrorLabel == null) {
            nonFatalErrorLabel = UIFactoryMgr.createLabel(" ");
            nonFatalErrorLabel.setForeground(Color.RED);
            nonFatalErrorLabel.setPreferredSize(sizeLabel);
        }
        return nonFatalErrorLabel;
    }

	/**
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JProgressBar getJProgressBar() {
		if(progressBar == null) {
			progressBar = UIFactoryMgr.createProgressBar();
			progressBar.setPreferredSize(sizeLabel);
			progressBar.setMaximum(100);
			progressBar.setIndeterminate(false);
		}
		return progressBar;
	}

	/**
	 * Handles progress updates.
	 * @param pct
	 */
	private void handleProgress(float pct) {
		try {
			getJProgressBar().setValue((int)(pct));
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
			getJLabelStep().setText(task);
		} catch(Exception e) {
			UIExceptionMgr.exception(e);
		}
	}

    /**
     * @param error
     * @param t
     */
    private void handleNonFatalError(String error, Throwable t) {
        try {
            getJLabelError().setText(error);
        } catch(Exception e) {
            UIExceptionMgr.exception(e);
        }
    }

	/**
	 * @see com.ixora.common.ui.AppDialog#getDisplayPanels()
	 */
	protected Component[] getDisplayPanels() {
		return new Component[]{
			getCenterPanel()
		};
	}

	/**
	 * @see com.ixora.common.ui.AppDialog#getButtons()
	 */
	protected JButton[] getButtons() {
		return new JButton[]{new JButton(new ActionCancel() {
			public void actionPerformed(ActionEvent e) {
				job.cancel();
			}
		}
		)};
	}
}