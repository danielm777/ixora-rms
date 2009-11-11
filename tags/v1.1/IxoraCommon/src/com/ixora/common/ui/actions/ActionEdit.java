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
@SuppressWarnings("serial")
public abstract class ActionEdit extends AbstractAction {
	/**
	 * Constructor.
	 */
	public ActionEdit() {
		super();
		UIUtils.setUsabilityDtls(MessageRepository.get(Msg.COMMON_UI_ACTIONS_EDIT), this);
		ImageIcon icon = UIConfiguration.getIcon("edit.gif");
		if(icon != null) {
			putValue(SMALL_ICON, icon);
		}
	}
}
