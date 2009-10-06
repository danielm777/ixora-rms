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
public abstract class ActionPrevious extends AbstractAction {
	/**
	 * Constructor.
	 */
	public ActionPrevious() {
		super();
		UIUtils.setUsabilityDtls(MessageRepository.get(Msg.COMMON_UI_ACTIONS_PREVIOUS), this);
	}
}
