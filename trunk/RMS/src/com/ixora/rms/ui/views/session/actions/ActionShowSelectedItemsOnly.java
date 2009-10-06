/*
 * Created on 06-Sep-2004
 */
package com.ixora.rms.ui.views.session.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;

import com.ixora.common.MessageRepository;
import com.ixora.common.ui.UIConfiguration;
import com.ixora.common.ui.UIExceptionMgr;
import com.ixora.common.ui.UIUtils;
import com.ixora.rms.client.model.SessionModel;
import com.ixora.rms.ui.messages.Msg;


/**
 * Toggles identifiers instead of translated names.
 */
public final class ActionShowSelectedItemsOnly extends AbstractAction {
    /** Session model */
//    private SessionModel model;

    /**
	 * Constructor.
	 * @param model
	 */
	public ActionShowSelectedItemsOnly(SessionModel model) {
		super();
//        this.model = model;
		UIUtils.setUsabilityDtls(MessageRepository.get(Msg.ACTIONS_SHOW_SELECTED_ITEMS_ONLY), this);
		ImageIcon icon = UIConfiguration.getIcon("show_selected_items_only.gif");
		if(icon != null) {
			putValue(Action.SMALL_ICON, icon);
		}
	}

	/**
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent arg0) {
		try {
		  //  this.model.filterOutUnselectedItems(!this.model.getFilterOutUnselectedItems());
		} catch(Exception e) {
			UIExceptionMgr.userException(e);
		}
	}
}