/*
 * Created on 21-Dec-2003
 */
package com.ixora.common.ui.actions;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

import com.ixora.common.MessageRepository;
import com.ixora.common.messages.Msg;
import com.ixora.common.ui.UIConfiguration;
import com.ixora.common.ui.UIUtils;

/**
 * @author Daniel Moraru
 */
public abstract class ActionNew extends AbstractAction {
	/**
	 * Constructor.
	 */
	public ActionNew() {
		super();
		UIUtils.setUsabilityDtls(MessageRepository.get(Msg.COMMON_UI_ACTIONS_NEW), this);
		ImageIcon icon = UIConfiguration.getIcon("new.gif");
		if(icon != null) {
			putValue(SMALL_ICON, icon);
		}
	}
}
