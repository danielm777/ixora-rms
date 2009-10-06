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
 * Tiles the view boards.
 */
public final class ActionNextScreen extends AbstractAction {
    /** DataViewBoardHandler */
	private DataViewBoardHandler dataViewBoardHandler;

	/**
	 * Constructor.
	 * @param dbh
	 */
	public ActionNextScreen(DataViewBoardHandler dbh) {
		super();
        this.dataViewBoardHandler = dbh;
		UIUtils.setUsabilityDtls(MessageRepository.get(Msg.ACTIONS_NEXT_SCREEN), this);
	}

	/**
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent arg0) {
		try {
		    this.dataViewBoardHandler.showNextScreen();
		} catch(Exception e) {
			UIExceptionMgr.userException(e);
		}
	}
}