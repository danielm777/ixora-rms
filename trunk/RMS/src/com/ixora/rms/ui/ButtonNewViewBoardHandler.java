/*
 * Created on 14-Feb-2005
 */
package com.ixora.rms.ui;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import com.ixora.common.MessageRepository;
import com.ixora.common.ui.ButtonWithPopup;
import com.ixora.common.ui.UIConfiguration;
import com.ixora.common.ui.UIExceptionMgr;
import com.ixora.common.ui.UIFactoryMgr;
import com.ixora.rms.repository.DataViewBoardInstallationData;
import com.ixora.rms.services.DataViewBoardRepositoryService;
import com.ixora.rms.ui.actions.ActionNewViewBoard;
import com.ixora.rms.ui.dataviewboard.handler.DataViewBoardHandler;

/**
 * Handles the popup button 'Create New View Board'.
 * @author Daniel Moraru
 */
public class ButtonNewViewBoardHandler {
	private DataViewBoardHandler fDataViewBoardHandler;
	private ButtonWithPopup fButton;
	private ActionNewViewBoard fActionNewViewBoard;

	/**
	 * Action associated with an item in the popup menu.
	 * @author Daniel Moraru
	 */
	private final class ViewBoardAction extends AbstractAction {
		private static final long serialVersionUID = 2586160408232657823L;
		private String fBoardClass;

		ViewBoardAction(String boardClass, String component, String boardName, String iconName) {
			super(MessageRepository.get(component, boardName), UIConfiguration.getIcon(iconName));
			fBoardClass = boardClass;
		}
		/**
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent ev) {
			try {
				fDataViewBoardHandler.addNewBoard(fBoardClass);
			} catch(Exception e) {
				UIExceptionMgr.userException(e);
			}
		}
	}

	/**
	 * Constructor.
	 * @param dvbh
	 * @param vbs
	 */
	public ButtonNewViewBoardHandler(DataViewBoardHandler dvbh, DataViewBoardRepositoryService vbs) {
		super();
		fDataViewBoardHandler = dvbh;
		fActionNewViewBoard = new ActionNewViewBoard(dvbh);
		fButton = new ButtonWithPopup(fActionNewViewBoard);
		fButton.setText(null);
		fButton.setMnemonic(KeyEvent.VK_UNDEFINED);

		Map<String, DataViewBoardInstallationData> map = vbs.getInstalledDataViewBoards();
		JPopupMenu popup = fButton.getPopupMenu();
		for(DataViewBoardInstallationData bid : map.values()) {
			ViewBoardAction ba = new ViewBoardAction(
					bid.getBoardClass(),
					bid.getBoardComponenName(),
					bid.getBoardName(),
					bid.getBoardIcon());
			JMenuItem mi = UIFactoryMgr.createMenuItem(ba);
			popup.add(mi);
		}
	}

	/**
	 * @return
	 */
	public ButtonWithPopup getButton() {
		return fButton;
	}
}
