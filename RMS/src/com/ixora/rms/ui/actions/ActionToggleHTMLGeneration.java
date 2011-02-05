/*
 * Created on 06-Sep-2004
 */
package com.ixora.rms.ui.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;

import com.ixora.common.MessageRepository;
import com.ixora.common.ui.UIConfiguration;
import com.ixora.common.ui.UIExceptionMgr;
import com.ixora.common.ui.UIUtils;
import com.ixora.rms.ui.exporter.html.HTMLGenerator;
import com.ixora.rms.ui.messages.Msg;


/**
 * Toggles the HTML generation.
 */
public final class ActionToggleHTMLGeneration extends AbstractAction {
	private static final long serialVersionUID = -4958470534630304479L;
	private HTMLGenerator fHTMLGenerator;

	/**
	 * Constructor.
	 * @param gen
	 */
	public ActionToggleHTMLGeneration(HTMLGenerator gen) {
		super();
        this.fHTMLGenerator = gen;
		UIUtils.setUsabilityDtls(MessageRepository.get(Msg.ACTIONS_TOGGLE_HTML_GENERATION), this);
		ImageIcon icon = UIConfiguration.getIcon("html_gen.gif");
		if(icon != null) {
			putValue(Action.SMALL_ICON, icon);
		}
	}

	/**
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent arg0) {
		try {
			if(fHTMLGenerator.isStarted()) {
				this.fHTMLGenerator.stop();
			} else {
				this.fHTMLGenerator.start();
			}
		} catch(Exception e) {
			UIExceptionMgr.userException(e);
		}
	}
}