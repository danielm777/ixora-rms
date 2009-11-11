/*
 * Created on 21-Dec-2003
 */
package com.ixora.common.ui.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;

import com.ixora.common.MessageRepository;
import com.ixora.common.messages.Msg;
import com.ixora.common.ui.AppFrame;
import com.ixora.common.ui.UIConfiguration;
import com.ixora.common.ui.UIExceptionMgr;
import com.ixora.common.ui.UIUtils;

/**
 * @author Daniel Moraru
 */
@SuppressWarnings("serial")
public class ActionIncreaseFontApplicationWide extends AbstractAction {
	private AppFrame fFrame;
	
	/**
	 * Constructor.
	 */
	public ActionIncreaseFontApplicationWide(AppFrame frame) {
		super();
		fFrame = frame;
		UIUtils.setUsabilityDtls(MessageRepository.get(Msg.COMMON_UI_INCREASE_FONT_APPLICATION_WIDE), this);
		ImageIcon icon = UIConfiguration.getIcon("increase_font.gif");
		if(icon != null) {
			putValue(Action.SMALL_ICON, icon);
		}
	}

	public void actionPerformed(ActionEvent e) {
		try {
			float sf = UIConfiguration.getFontScalingStep();
			UIUtils.changeAppFontSize(fFrame, sf);
		} catch(Exception ex) {
			UIExceptionMgr.userException(ex);
		}		
	}
}
