/*
 * Created on 06-Sep-2004
 */
package com.ixora.rms.ui.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;

import com.ixora.common.MessageRepository;
import com.ixora.common.ui.UIConfiguration;
import com.ixora.common.ui.UIExceptionMgr;
import com.ixora.common.ui.UIUtils;
import com.ixora.rms.ui.dataviewboard.handler.DataViewBoardHandler;
import com.ixora.rms.ui.messages.Msg;


/**
 * Creates a new view board.
 */
public final class ActionNewViewBoard extends AbstractAction {
    /** DataViewBoardHandler */
	private DataViewBoardHandler dataViewBoardHandler;

	/**
	 * Constructor.
	 * @param dbh
	 */
	public ActionNewViewBoard(DataViewBoardHandler dbh) {
		super();
        this.dataViewBoardHandler = dbh;
		UIUtils.setUsabilityDtls(MessageRepository.get(Msg.ACTIONS_NEW_VIEWBOARD), this);
		ImageIcon icon = UIConfiguration.getIcon("new_view_board.gif");
		if(icon != null) {
			putValue(Action.SMALL_ICON, icon);
		}
	}

	/**
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent arg0) {
		try {
		    this.dataViewBoardHandler.addNewBoard();
		} catch(Exception e) {
			UIExceptionMgr.userException(e);
		}
	}
}