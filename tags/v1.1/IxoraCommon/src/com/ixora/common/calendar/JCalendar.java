/*
 * Created on 01-Oct-2004
 */
package com.ixora.common.calendar;

import java.awt.BorderLayout;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javax.swing.JFrame;

/**
 * @author Daniel Moraru
 */
public class JCalendar extends com.toedter.calendar.JCalendar {
	private static final long serialVersionUID = -1064018079458706345L;
	/** Time of day chooser */
    private JTimeChooser timeChooser;

    /**
     * Constructor.
     */
    public JCalendar() {
        this(null, null, true, true, true);
    }

    /**
     * Constructor.
     * @param monthSpinner
     */
    public JCalendar(boolean monthSpinner) {
        this(null, null, monthSpinner, true, true);
    }

    /**
     * Constructor.
     * @param date
     */
    public JCalendar(Date date) {
        this(date, null);
    }

    /**
     * Constructor.
     * @param locale
     */
    public JCalendar(Locale locale) {
        this(null, locale);
    }

    /**
     * Constructor.
     * @param date
     * @param locale
     */
    public JCalendar(Date date, Locale locale) {
        this(date, locale, true, true, true);
    }

    /**
     * Constructor.
     * @param date
     * @param monthSpinner
     */
    public JCalendar(Date date, boolean monthSpinner) {
        this(date, null, monthSpinner, true, true);
    }

    /**
     * Constructor.
     * @param locale
     * @param monthSpinner
     */
    public JCalendar(Locale locale, boolean monthSpinner) {
        this(null, locale, monthSpinner, true, true);
    }

    /**
     * Constructor.
     * @param date
     * @param locale
     * @param monthSpinner
     * @param weekOfYearVisible
     */
    public JCalendar(Date date, Locale locale, boolean monthSpinner,
            boolean weekOfYearVisible) {
        this(date, locale, monthSpinner, weekOfYearVisible, true);
    }

    /**
     * Constructor.
     * @param date
     * @param locale
     * @param monthSpinner
     * @param weekOfYearVisible
     * @param timeOfDay
     */
    public JCalendar(Date date, Locale locale, boolean monthSpinner,
            boolean weekOfYearVisible, boolean timeOfDay) {
        super(date, locale, monthSpinner, weekOfYearVisible);
        if(timeOfDay) {
            timeChooser = new JTimeChooser(date);
            add(timeChooser, BorderLayout.SOUTH);
        }
    }

    /**
     * @see com.toedter.calendar.JCalendar#getDate()
     */
    public Date getDate() {
        Calendar cal = getCalendar();
        // reset calendar time of day, that will
        // be provided by the time chooser
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        Date date = cal.getTime();
        return new Date(date.getTime() + timeChooser.getTime());
    }

    /**
     * @see com.toedter.calendar.JCalendar#setDate(java.util.Date)
     */
    public void setDate(Date date) {
        if(date == null) {
            date = new Date();
        }
        super.setDate(date);
        timeChooser.setCalendar(getCalendar());
    }

    /**
     * Test method.
     * @param s
     */
    public static void main(String[] s) {
        JFrame frame = new JFrame("JCalendar");
        frame.getContentPane().add(new JCalendar());
        frame.pack();
        frame.setVisible(true);
    }
}
