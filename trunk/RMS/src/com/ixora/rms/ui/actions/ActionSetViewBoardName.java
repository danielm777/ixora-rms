/*
 * Created on 06-Sep-2004
 */
package com.ixora.rms.ui.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import com.ixora.common.MessageRepository;
import com.ixora.common.ui.UIExceptionMgr;
import com.ixora.common.ui.UIUtils;
import com.ixora.rms.ui.dataviewboard.handler.DataViewBoardHandler;
import com.ixora.rms.ui.messages.Msg;


/**
 * Sets the name of the selected view board.
 */
public final class ActionSetViewBoardName extends AbstractAction {
    /** DataViewBoardHandler */
	private DataViewBoardHandler dataViewBoardHandler;

	/**
	 * Constructor.
	 * @param dbh
	 */
	public ActionSetViewBoardName(DataViewBoardHandler dbh) {
		super();
        this.dataViewBoardHandler = dbh;
		UIUtils.setUsabilityDtls(MessageRepository.get(Msg.ACTIONS_SET_VIEWBOARD_NAME), this);
		//putValue(Action.SMALL_ICON, UIConfiguration.getIcon("set_viewboard_name.gif"));
	}

	/**
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent arg0) {
		try {
		    this.dataViewBoardHandler.renameBoard();
		} catch(Exception e) {
			UIExceptionMgr.userException(e);
		}
	}
}