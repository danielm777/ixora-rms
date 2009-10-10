/*
 * Created on 02-Oct-2004
 */
package com.ixora.common.calendar;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.util.Date;

import javax.swing.JButton;

import com.ixora.common.MessageRepository;
import com.ixora.common.messages.Msg;
import com.ixora.common.ui.AppDialog;
import com.ixora.common.ui.UIUtils;
import com.ixora.common.ui.actions.ActionCancel;
import com.ixora.common.ui.actions.ActionOk;

/**
 * @author Daniel Moraru
 */
public class JCalendarDialog extends AppDialog {
	private static final long serialVersionUID = 753102498051198398L;
	private JCalendar calendar;
    private boolean canceled;

    /**
     * Constructor.
     * @param parent
     * @param timeOfDay
     */
    public JCalendarDialog(Dialog parent, boolean timeOfDay) {
        super(parent, VERTICAL);
        init(timeOfDay);
    }

    /**
     * Constructor.
     * @param parent
     * @param timeOfDay
     */
    public JCalendarDialog(Frame parent, boolean timeOfDay) {
        super(parent, VERTICAL);
        init(timeOfDay);
    }

    /**
     * @see com.ixora.common.ui.AppDialog#getDisplayPanels()
     */
    protected Component[] getDisplayPanels() {
        return new Component[] {calendar};
    }

    /**
     * @see com.ixora.common.ui.AppDialog#getButtons()
     */
    @SuppressWarnings("serial")
	protected JButton[] getButtons() {
        return new JButton[] {
                new JButton(new ActionOk() {
                    public void actionPerformed(ActionEvent e) {
                        dispose();
                    }
                    }),
                new JButton(new ActionCancel() {
                    public void actionPerformed(ActionEvent e) {
                        canceled = true;
                        dispose();
                    }})
        };
    }

    /**
     * Init.
     * @param timeOfDay
     */
    private void init(boolean timeOfDay) {
        setModal(true);
        if(timeOfDay) {
            setTitle(MessageRepository.get(Msg.COMMON_UI_CALENDAR_DIALOG_TITLE_TIME));
        } else {
            setTitle(MessageRepository.get(Msg.COMMON_UI_CALENDAR_DIALOG_TITLE_DATE));
        }
        calendar = new JCalendar();
        calendar.setDecorationBackgroundVisible(false);
        buildContentPane();
    }

    /**
     * @return
     */
    public Date getDate() {
        if(canceled) {
            return null;
        }
        return calendar.getDate();
    }

    /**
     * @param d
     */
    public void setDate(Date d) {
       calendar.setDate(d);
    }

    /**
     * @param parent
     * @param timeOfDay
     * @param date
     */
    public static JCalendarDialog showDialog(
            Window parent,
            boolean timeOfDay,
            Date date) {
        JCalendarDialog dlg = null;
        if(parent instanceof Dialog) {
            dlg = new JCalendarDialog((Dialog)parent, true);
        } else if(parent instanceof Frame) {
            dlg = new JCalendarDialog((Frame)parent, true);
        }
        if(dlg == null) {
            return null;
        }
        dlg.setDate(date);
        dlg.pack();
        UIUtils.centerDialogAndShow(parent, dlg);
        return dlg;
    }

    /**
     * @return the calendar.
     */
    public JCalendar getCalendar() {
        return calendar;
    }
}
