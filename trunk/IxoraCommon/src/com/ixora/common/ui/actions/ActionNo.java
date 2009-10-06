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
public abstract class ActionNo extends AbstractAction {
	/**
	 * Constructor.
	 */
	public ActionNo() {
		super();
		UIUtils.setUsabilityDtls(MessageRepository.get(Msg.COMMON_UI_ACTIONS_NO), this);
		//putValue(Action.SMALL_ICON, UIConfiguration.getIcon("no.gif"));
	}
}
