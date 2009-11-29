package com.ixora.rms.ui.views.logreplay;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.Date;

import javax.swing.JButton;

import com.ixora.common.MessageRepository;
import com.ixora.common.ui.AppDialog;
import com.ixora.common.ui.UIExceptionMgr;
import com.ixora.common.ui.UIFactoryMgr;
import com.ixora.common.ui.actions.ActionCancel;
import com.ixora.common.ui.actions.ActionOk;
import com.ixora.common.ui.forms.FormFieldDateSelector;
import com.ixora.common.ui.forms.FormPanel;
import com.ixora.rms.exception.RMSException;
import com.ixora.rms.logging.DataLogCompareAndReplayConfiguration;
import com.ixora.rms.ui.RMSViewContainer;
import com.ixora.rms.ui.messages.Msg;

/**
 * @author Daniel Moraru
 */
public class TimeIntervalSelectorDialog extends AppDialog {
	private static final long serialVersionUID = -25876768115386442L;

	private FormPanel fForm;
	private FormFieldDateSelector fFormDateSelectorStart;
	private FormFieldDateSelector fFormDateSelectorEnd;
	private DataLogCompareAndReplayConfiguration.LogRepositoryReplayConfig fReplayConfig;


	/**
	 * @param parent
	 * @param timeMin
	 * @param timeMax
	 * @param conf
	 */
	public TimeIntervalSelectorDialog(RMSViewContainer container,
			DataLogCompareAndReplayConfiguration.LogRepositoryReplayConfig conf) {
		super(container.getAppFrame(), VERTICAL);
		setModal(true);
		setTitle(MessageRepository.get(Msg.TITLE_TIME_INTERVAL_SELECTOR));
		fForm = new FormPanel(FormPanel.VERTICAL1);
		fReplayConfig = conf;

		fFormDateSelectorStart = new FormFieldDateSelector(this, new Date(conf.getTimeBegin()));
		fFormDateSelectorEnd = new FormFieldDateSelector(this, new Date(conf.getTimeEnd()));

		fForm.addPairs(
			new String[]{
				MessageRepository.get(Msg.LABEL_TIME_START),
				MessageRepository.get(Msg.LABEL_TIME_END)
			},
			new Component[]{
				fFormDateSelectorStart,
				fFormDateSelectorEnd,
			});

		buildContentPane();
	}


	/**
	 * @see com.ixora.common.ui.AppDialog#getDisplayPanels()
	 */
	protected Component[] getDisplayPanels() {
		return new Component[]{fForm};
	}

	/**
	 * @see com.ixora.common.ui.AppDialog#getButtons()
	 */
	@SuppressWarnings("serial")
	protected JButton[] getButtons() {
		return new JButton[]{
				UIFactoryMgr.createButton(new ActionOk(){
					public void actionPerformed(ActionEvent e) {
						handleOk();
					}}),
				UIFactoryMgr.createButton(new ActionCancel(){
					public void actionPerformed(ActionEvent e) {
						dispose();
					}})
		};
	}

	private void handleOk() {
		try {
			Date start = fFormDateSelectorStart.getResult();
			if(start != null) {
				if(start.getTime() < fReplayConfig.getTimeBegin()) {
					throw new RMSException(Msg.ERROR_TIMESTAMP_MUST_BE_GREATER_OR_EQUAL, 
							new Object[]{start, new Date(fReplayConfig.getTimeBegin())});
				} else {
					fReplayConfig.setTimestampBegin(start.getTime());
				}
			}
			Date end = fFormDateSelectorEnd.getResult();
			if(end != null) {
				if(end.getTime() > fReplayConfig.getTimeEnd()) {
					throw new RMSException(Msg.ERROR_TIMESTAMP_MUST_BE_SMALLER_OR_EQUAL, 
							new Object[]{end, new Date(fReplayConfig.getTimeEnd())});
				} else {
					fReplayConfig.setTimestampEnd(end.getTime());
				}
			}
			dispose();
		} catch(Exception e) {
			UIExceptionMgr.userException(e);
		}
	}
}
