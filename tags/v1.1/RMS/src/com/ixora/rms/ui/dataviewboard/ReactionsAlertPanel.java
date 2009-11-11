/**
 * 08-Aug-2005
 */
package com.ixora.rms.ui.dataviewboard;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import com.ixora.common.MessageRepository;
import com.ixora.common.logging.AppLogger;
import com.ixora.common.logging.AppLoggerFactory;
import com.ixora.common.ui.UIConfiguration;
import com.ixora.common.ui.UIExceptionMgr;
import com.ixora.common.ui.UIFactoryMgr;
import com.ixora.common.ui.UIUtils;
import com.ixora.common.ui.popup.PopupListener;
import com.ixora.common.utils.Utils;
import com.ixora.rms.exception.RMSException;
import com.ixora.rms.reactions.ReactionId;
import com.ixora.rms.reactions.ReactionLogRecord;
import com.ixora.rms.reactions.ReactionState;
import com.ixora.rms.reactions.ReactionStateRecord;
import com.ixora.rms.services.ReactionLogService;
import com.ixora.rms.ui.RMSViewContainer;
import com.ixora.rms.ui.messages.Msg;
import com.ixora.rms.ui.reactions.ReactionLogViewer;

/**
 * @author Daniel Moraru
 */
final class ReactionsAlertPanel extends JPanel {
	private static final long serialVersionUID = -3236101799644218511L;
	private static final AppLogger logger = AppLoggerFactory.getLogger(ReactionsAlertPanel.class);
	private static final Icon reactionIcon = UIConfiguration.getIcon("reaction_armed.gif");
	private static final int ARMED = 0;
	private static final int FIRED = 1;
	private static final int DISARMED = 2;
	private JLabel fLabel;
	private Color fOriginalColor;
	private ReactionLogService fReactionLog;
	private JPopupMenu fPopupMenu;
	private JMenuItem fMenuItemClosePanel;
	private PopupEventHandler fPopupEventHandler;
	private RMSViewContainer fViewContainer;

	/**
	 * Popup event handler.
	 */
	private final class PopupEventHandler
		extends PopupListener {
		/**
		 * @see com.ixora.common.ui.PopupListener#showPopup(java.awt.event.MouseEvent)
		 */
		protected void showPopup(MouseEvent e) {
			handleShowPopup(e);
		}
	}

	private final class ActionClosePanel extends AbstractAction {
		private static final long serialVersionUID = -4494359109096599118L;

		public ActionClosePanel() {
			super();
			UIUtils.setUsabilityDtls(MessageRepository.get(Msg.ACTIONS_CLOSE_REACTION_PANEL), this);
			ImageIcon icon = UIConfiguration.getIcon("close_panel.gif");
			if(icon != null) {
				putValue(Action.SMALL_ICON, icon);
			}
		}

		/**
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent e) {
			ReactionsAlertPanel.this.setVisible(false);
		}
	}

	/**
	 * @param vc
	 * @param rls
	 */
	public ReactionsAlertPanel(RMSViewContainer vc, ReactionLogService rls) {
		super(new BorderLayout());
		if(rls == null) {
			throw new IllegalArgumentException("null reaction log service");
		}
		fViewContainer = vc;
		fReactionLog = rls;
		setBorder(BorderFactory.createEmptyBorder(2,2,2,2));
		setPreferredSize(new Dimension(200, 22));
		fLabel = UIFactoryMgr.createLabel(null);
		fLabel.setForeground(Color.GRAY);
		fLabel.setFont(getFont().deriveFont(9f));
		add(fLabel, BorderLayout.CENTER);

		JEditorPane pane = UIFactoryMgr.createHtmlPane();
		pane.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
		// TODO localize
		pane.setText("<html><a href='#'><font size='2'>Open reaction log<font></a></html>");
		pane.addHyperlinkListener(new HyperlinkListener(){
			public void hyperlinkUpdate(HyperlinkEvent e) {
				if(e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
					handleViewReactionsLog();
				}
			}
		});
		add(pane, BorderLayout.EAST);

		fPopupMenu = UIFactoryMgr.createPopupMenu();
		fMenuItemClosePanel= UIFactoryMgr.createMenuItem(new ActionClosePanel());
		fPopupMenu.add(fMenuItemClosePanel);
		fOriginalColor = fLabel.getForeground();

		this.fPopupEventHandler = new PopupEventHandler();
		addMouseListener(fPopupEventHandler);

	}

	/**
	 * @param e
	 */
	private void handleShowPopup(MouseEvent e) {
		try {
			fPopupMenu.show(e.getComponent(), e.getX(), e.getY());
		} catch(Exception ex) {
			UIExceptionMgr.userException(ex);
		}
	}

	/**
	 *
	 */
	private void handleViewReactionsLog() {
		try {
			ReactionLogViewer reactionLogViewer = new ReactionLogViewer(fReactionLog);
			UIUtils.centerFrameAndShow(fViewContainer.getAppFrame(), reactionLogViewer);
		} catch(Exception e) {
			UIExceptionMgr.userException(e);
		}
	}

	/**
	 * @param reactions
	 */
	public void reactionsArmed(final ReactionId[] reactions) {
		processReactionIds(reactions, ARMED);
	}

	/**
	 * @param revs
	 * @param mode
	 */
	private void processReactionIds(ReactionId[] rids, int mode) {
		try {
			StringBuffer buff = new StringBuffer();
			buff.append("<html>");
			List<ReactionLogRecord> recs = new LinkedList<ReactionLogRecord>();
			for(int i = 0; i < rids.length; i++) {
				ReactionId rid = rids[i];
				ReactionLogRecord rec = fReactionLog.getRecord(rid);
				if(rec != null) {
					recs.add(rec);
				}
				buff.append("<b>");
				List<ReactionStateRecord> srs = rec.getStates();
				if(!Utils.isEmptyCollection(srs)) {
					boolean exit = false;
					for(ReactionStateRecord sr : srs) {
						if(exit) {
							break;
						}
						switch(mode) {
						case ARMED:
							if(sr.getState() == ReactionState.ARMED) {
								fLabel.setForeground(Color.YELLOW);
								fLabel.setIcon(reactionIcon);
								// TODO localize
								fLabel.setText("REACTION ARMED - " + new Date(sr.getTimestamp()));
								buff.append("Armed for reaction of type " + rec.getReactionDeliveryType().toString() + " with message:");
								exit = true;
							}
						break;
						case FIRED:
							if(sr.getState() == ReactionState.FIRED) {
								fLabel.setForeground(Color.RED);
								fLabel.setIcon(reactionIcon);
								// TODO localize
								fLabel.setText("REACTION FIRED - " + new Date(sr.getTimestamp()));
								buff.append("Fired reaction of type " + rec.getReactionDeliveryType().toString() + " with message:");
								exit = true;
							}
						break;
						case DISARMED:
							if(sr.getState() == ReactionState.DISARMED) {
								fLabel.setText(null);
								fLabel.setIcon(null);
								exit = true;
							}
						break;
						}
					}
				}
				buff.append("</b>");
				buff.append("<br>");
				String msg = rec.getReactionEvent().getMessage();
				if(!Utils.isEmptyString(msg)) {
					//buff.append("<font size=\"2\">");
					buff.append(UIUtils.getMultilineHtmlFragment(msg, UIConfiguration.getMaximumLineLengthForToolTipText()));
					//buff.append("</font>");
				}
				if(i < rids.length - 1) {
					buff.append("<br>");
				}
			}
			buff.append("</html>");
			setToolTipText(buff.toString());
		} catch(RMSException e) {
			logger.error(e);
		}
	}

	/**
	 * @param reactions
	 */
	public void reactionsDisarmed(final ReactionId[] reactions) {
		fLabel.setForeground(fOriginalColor);
		fLabel.setIcon(null);
		processReactionIds(reactions, DISARMED);
	}

	/**
	 * @param reactions
	 */
	public void reactionsFired(final ReactionId[] reactions) {
		processReactionIds(reactions, FIRED);
	}
}
