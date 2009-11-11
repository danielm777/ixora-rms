/*
 * Created on 06-Sep-2004
 */
package com.ixora.rms.ui.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import com.ixora.common.MessageRepository;
import com.ixora.common.ui.UIExceptionMgr;
import com.ixora.common.ui.UIUtils;
import com.ixora.rms.ui.RMSViewContainer;
import com.ixora.rms.ui.XMLInputDialog;
import com.ixora.rms.ui.messages.Msg;


/**
 * Reusable action that allows data input via XML definitions.
 */
public abstract class ActionFromXML extends AbstractAction {
	private static final long serialVersionUID = -2844750841860738444L;
	protected RMSViewContainer fContainer;

	/**
	 * Constructor.
	 * @param dbh
	 */
	public ActionFromXML(RMSViewContainer vc) {
		super();
		fContainer = vc;
		UIUtils.setUsabilityDtls(MessageRepository.get(Msg.ACTIONS_FROM_XML), this);
	}

	/**
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent arg0) {
		try {
		    // get XML input
			XMLInputDialog dlg = new XMLInputDialog(fContainer.getAppFrame(), new XMLInputDialog.Callback(){
				public void handleXMLInput(String data) throws Exception {
					handleXML(data);
				}
			});
			UIUtils.centerDialogAndShow(fContainer.getAppFrame(), dlg);
		} catch(Exception e) {
			UIExceptionMgr.userException(e);
		}
	}

	/**
	 * @param data
	 * @throws Exception
	 */
	protected abstract void handleXML(String data) throws Exception;
}