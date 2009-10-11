/*
 * Created on 06-Sep-2004
 */
package com.ixora.rms.ui.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import com.ixora.common.MessageRepository;
import com.ixora.common.ui.UIExceptionMgr;
import com.ixora.common.ui.UIUtils;
import com.ixora.rms.client.model.SessionModel;
import com.ixora.rms.ui.messages.Msg;


/**
 * Toggles identifiers instead of translated names.
 */
public final class ActionToggleIdentifiers extends AbstractAction {
	private static final long serialVersionUID = -8375710128564576029L;
	/** Session model */
    private SessionModel model;

    /**
	 * Constructor.
	 * @param model
	 */
	public ActionToggleIdentifiers(SessionModel model) {
		super();
        this.model = model;
		UIUtils.setUsabilityDtls(MessageRepository.get(Msg.ACTIONS_TOGGLE_IDENTIFIERS), this);
		//putValue(Action.SMALL_ICON, UIConfiguration.getIcon("toggle_identifiers.gif"));
	}

	/**
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent arg0) {
		try {
		    this.model.setShowIdentifiers(!this.model.getShowIdentifiers());
		} catch(Exception e) {
			UIExceptionMgr.userException(e);
		}
	}
}