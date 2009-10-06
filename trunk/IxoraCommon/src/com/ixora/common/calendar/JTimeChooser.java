/*
 * Created on 01-Oct-2004
 */
package com.ixora.common.calendar;

import java.awt.FlowLayout;
import java.util.Calendar;
import java.util.Date;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.toedter.components.JSpinField;

/**
 * Time chooser
 * @author Daniel Moraru
 */
public class JTimeChooser extends JPanel {
    private Calendar calendar;
    private JSpinField hour;
    private JSpinField min;
    private JSpinField sec;

    /**
     * Constructor.
     */
    public JTimeChooser() {
        this(null);
    }

    /**
     * Constructor.
     * @param date
     */
    public JTimeChooser(Date date) {
        super();
        setLayout(new FlowLayout());
        hour = new JSpinField(0, 23);
        hour.adjustWidthToMaximumValue();
        min = new JSpinField(0, 59);
        min.adjustWidthToMaximumValue();
        sec = new JSpinField(0, 59);
        sec.adjustWidthToMaximumValue();
        add(hour);
        add(new JLabel(":"));
        add(min);
        add(new JLabel(":"));
        add(sec);
        Calendar calendar = Calendar.getInstance();
        if(date != null) {
            calendar.setTime(date);
        }
        setCalendar(calendar);
    }

    /**
     * Set the calendar.
     * @param c
     */
    public void setCalendar(Calendar c) {
        this.calendar = c;
        hour.setValue(c.get(Calendar.HOUR_OF_DAY));
        min.setValue(c.get(Calendar.MINUTE));
        sec.setValue(c.get(Calendar.SECOND));
    }

    /**
     * @param d
     */
    public void setDate(Date d) {
       if(d == null) {
           d = new Date();
       }
       this.calendar.setTime(d);
       setCalendar(calendar);
    }

    /**
     * @return the time of day in milliseconds.
     */
    public long getTime() {
        return (hour.getValue() * 3600 + min.getValue() * 60 + sec.getValue()) * 1000;
    }
}
