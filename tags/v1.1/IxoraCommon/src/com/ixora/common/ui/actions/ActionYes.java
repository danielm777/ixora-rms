/*
 * Created on 21-Dec-2003
 */
package com.ixora.common.ui.actions;

import javax.swing.AbstractAction;

import com.ixora.common.MessageRepository;
import com.ixora.common.messages.Msg;
import com.ixora.common.ui.UIUtils;

/**
 * @author Daniel Moraru
 */
@SuppressWarnings("serial")
public abstract class ActionYes extends AbstractAction {
	/**
	 * Constructor.
	 */
	public ActionYes() {
		super();
		UIUtils.setUsabilityDtls(MessageRepository.get(Msg.COMMON_UI_ACTIONS_YES), this);
		//putValue(Action.SMALL_ICON, UIConfiguration.getIcon("yes.gif"));
	}
}
