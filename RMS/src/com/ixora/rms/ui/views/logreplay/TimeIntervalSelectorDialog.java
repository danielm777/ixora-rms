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
import com.ixora.rms.logging.TimeInterval;
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
	private TimeInterval fResult;
	private TimeInterval fLimits;

	/**
	 * @param parent
	 * @param limits
	 */
	public TimeIntervalSelectorDialog(RMSViewContainer container,
			TimeInterval limits) {
		super(container.getAppFrame(), VERTICAL);
		setModal(true);
		setTitle(MessageRepository.get(Msg.TITLE_TIME_INTERVAL_SELECTOR));
		fForm = new FormPanel(FormPanel.VERTICAL1);
		fLimits = limits;

		fFormDateSelectorStart = new FormFieldDateSelector(this, new Date(fLimits.getStart()));
		fFormDateSelectorEnd = new FormFieldDateSelector(this, new Date(fLimits.getEnd()));

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
			long[] ti = new long[2];
			if(start != null) {
				ti[0] = start.getTime();
			} else {
				ti[0] = fLimits.getStart();
			}
			Date end = fFormDateSelectorEnd.getResult();
			if(end != null) {
				ti[1] = end.getTime();
			} else {
				ti[1] = fLimits.getEnd();
			}
			fResult = new TimeInterval(ti[0], ti[1]);
			dispose();
		} catch(Exception e) {
			UIExceptionMgr.userException(e);
		}
	}
	
	/**
	 * @return
	 */
	public TimeInterval getResult() {
		return fResult;
	}
}
