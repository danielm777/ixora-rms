/**
 * 09-Aug-2005
 */
package com.ixora.rms.ui.reactions;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import com.ixora.common.ui.UIConfiguration;
import com.ixora.common.ui.UIExceptionMgr;
import com.ixora.common.ui.UIFactoryMgr;
import com.ixora.rms.exception.RMSException;
import com.ixora.rms.reactions.ReactionLogRecord;
import com.ixora.rms.services.ReactionLogService;

/**
 * @author Daniel Moraru
 */
public class ReactionLogViewer extends JFrame {
	private static final ImageIcon iconJob = UIConfiguration.getIcon("reaction_job.gif");
	private static final ImageIcon iconEmail = UIConfiguration.getIcon("reaction_email.gif");
	private static final ImageIcon iconAdvice = UIConfiguration.getIcon("reaction_advice.gif");
	private EmailPanel fEmail;
	private JobPanel fJob;
	private AdvicePanel fAdvice;
	private Timer fTimer;
	private ReactionLogService fReactionLog;

	private final class EventHandler extends WindowAdapter {
		/**
		 * @see java.awt.event.WindowAdapter#windowClosing(java.awt.event.WindowEvent)
		 */
		public void windowClosing(WindowEvent e) {
			handleClose();
		}
	}

	/**
	 * @param rls
	 * @throws RMSException
	 */
	public ReactionLogViewer(ReactionLogService rls) throws RMSException {
		super();
		setTitle("Reactions Log");
		setIconImage(UIConfiguration.getIcon("reactions.gif").getImage());
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		fReactionLog = rls;
		JTabbedPane tabbedPane = UIFactoryMgr.createTabbedPane();
		List<ReactionLogRecord> recs = rls.getRecords();
		fEmail = new EmailPanel(recs);
		fJob = new JobPanel(recs);
		fAdvice = new AdvicePanel(recs);
		// TODO localize
		tabbedPane.addTab("Emails", iconEmail, fEmail);
		tabbedPane.addTab("Jobs", iconJob, fJob);
		tabbedPane.addTab("Advices", iconAdvice, fAdvice);

		getContentPane().add(tabbedPane);

		tabbedPane.setPreferredSize(new Dimension(700, 350));

		addWindowListener(new EventHandler());

		fTimer = new Timer(true);
		fTimer.schedule(new TimerTask(){
				public void run() {
					handleRefresh();
				}
			}, 0, 5000);
	}

	/**
	 *
	 */
	private void handleRefresh() {
		try {
			List<ReactionLogRecord> lst = fReactionLog.getRecords();
			fEmail.update(lst);
			fJob.update(lst);
			fAdvice.update(lst);
		} catch(Exception e) {
			UIExceptionMgr.exception(e);
		}
	}

	/**
	 *
	 */
	private void handleClose() {
		fTimer.cancel();
		dispose();
	}
}
