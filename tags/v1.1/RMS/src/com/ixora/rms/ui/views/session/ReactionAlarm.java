/**
 * 11-Mar-2006
 */
package com.ixora.rms.ui.views.session;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.Timer;
import javax.swing.border.Border;

import com.ixora.common.ui.UIExceptionMgr;
import com.ixora.common.utils.Utils;
import com.ixora.rms.reactions.ReactionState;
import com.ixora.rms.services.ReactionLogService;
import com.ixora.rms.services.ReactionLogService.ReactionLogEvent;

/**
 * @author Daniel Moraru
 */
public class ReactionAlarm {
	private static final Color colorArmed = Color.YELLOW;
	private static final Color colorFired = Color.RED;
	private static final Border borderArmed = BorderFactory.createLineBorder(colorArmed, 1);
	private static final Border borderFired = BorderFactory.createLineBorder(colorFired, 1);
	private static final int animationRate = 1000;

	/** Editor pane to change */
	private JEditorPane fPane;
	/** Button to change */
	private JButton fButton;
	/** Timer to animate the pane */
	private Timer fTimer;
	/** Original color of the pane */
	private Color fColorOriginalPane;
	/** Original color of the button */
	private Color fColorOriginalButton;
	/** Original border */
	private Border fBorderOriginal;
	/** Last reaction log event */
	private ReactionLogService.ReactionLogEvent fLastEvent;
	/** Animation two state flip */
	private boolean fFlip;

	/**
	 * @param pane
	 * @param button
	 */
	public ReactionAlarm(JEditorPane pane, JButton button) {
		super();
		fPane = pane;
		fButton = button;
		fColorOriginalPane = pane.getBackground();
		fColorOriginalButton = button.getBackground();
		fBorderOriginal = BorderFactory.createLineBorder(fColorOriginalPane, 1);
		pane.setBorder(fBorderOriginal);
	}

	/**
	 * @param event
	 */
	public void setReactionLogEvent(ReactionLogEvent event) {
		ReactionState state = event.getReactionState();
		if(state != ReactionState.ARMED && state != ReactionState.FIRED) {
			return;
		}
		fLastEvent = event;
		if(fTimer == null) {
			fTimer = new Timer(animationRate, new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					handleAnimation();
				}
			});
			fTimer.start();
		}
	}

	/**
	 *
	 */
	private void handleAnimation() {
		try {
			ReactionState state = fLastEvent.getReactionState();
			String txt = null;
			Color color = fColorOriginalButton;
			Border border = fBorderOriginal;
			if(state == ReactionState.ARMED) {
				txt = "armed";
				if(fFlip) {
					color = colorArmed;
					border = borderArmed;
				}
			} else if(state == ReactionState.FIRED) {
				txt = "fired";
				if(fFlip) {
					color = colorFired;
					border = borderFired;
				}
			}
			fButton.setBackground(color);
			fPane.setBorder(border);
			if(!Utils.isEmptyString(txt)) {
				// TODO localize
				fPane.setText("<html>&nbsp;&nbsp;<a href='#'>Reaction event:" + txt + "</a>&nbsp;&nbsp;</html>");
			}
			fFlip = !fFlip;
		} catch (Exception e) {
			UIExceptionMgr.exception(e);
		}
	}

	/**
	 * Resets the state of the handler.
	 */
	public void reset() {
		if(fTimer != null) {
			fTimer.stop();
			fTimer = null;
		}
		fPane.setBorder(fBorderOriginal);
		fButton.setBackground(fColorOriginalButton);
		fPane.setText("");
	}
}
