/*
 * Created on 21-Dec-2003
 */
package com.ixora.common.ui.actions;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;

import com.ixora.common.MessageRepository;
import com.ixora.common.messages.Msg;
import com.ixora.common.ui.UIConfiguration;
import com.ixora.common.ui.UIUtils;

/**
 * @author Daniel Moraru
 */
@SuppressWarnings("serial")
public abstract class ActionHelp extends AbstractAction {
	/**
	 * Constructor.
	 */
	public ActionHelp() {
		super();
		UIUtils.setUsabilityDtls(MessageRepository.get(Msg.COMMON_UI_ACTIONS_HELP), this);
		ImageIcon icon = UIConfiguration.getIcon("help.gif");
		if(icon != null) {
			putValue(Action.SMALL_ICON, icon);
		}
	}
}
