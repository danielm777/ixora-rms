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
public final class ActionPrevScreen extends AbstractAction {
	private static final long serialVersionUID = 752747388744814128L;
	/** DataViewBoardHandler */
	private DataViewBoardHandler dataViewBoardHandler;

	/**
	 * Constructor.
	 * @param dbh
	 */
	public ActionPrevScreen(DataViewBoardHandler dbh) {
		super();
        this.dataViewBoardHandler = dbh;
		UIUtils.setUsabilityDtls(MessageRepository.get(Msg.ACTIONS_PREV_SCREEN), this);
	}

	/**
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent arg0) {
		try {
		    this.dataViewBoardHandler.showPrevScreen();
		} catch(Exception e) {
			UIExceptionMgr.userException(e);
		}
	}
}