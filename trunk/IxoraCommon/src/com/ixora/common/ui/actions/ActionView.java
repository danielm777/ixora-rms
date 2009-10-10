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
public abstract class ActionView extends AbstractAction {
	/**
	 * Constructor.
	 */
	public ActionView() {
		super();
		UIUtils.setUsabilityDtls(MessageRepository.get(Msg.COMMON_UI_ACTIONS_VIEW), this);
		ImageIcon icon = UIConfiguration.getIcon("view.gif");
		if(icon != null) {
			putValue(SMALL_ICON, icon);
		}
	}
}
