/*
 * Created on 05-Jun-2005
 */
package com.ixora.common.ui;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.ixora.common.utils.Utils;

/**
 * NonFatalErrorHandlerDefault
 * @author Daniel Moraru
 */
public final class NonFatalErrorBuffer implements NonFatalErrorHandler {
    /** Log entries */
    private List<LogEntry> fBuffer;
    /** Max size */
    private int fMaxSize;

    /**
     * LogEntry
     */
    public static final class LogEntry {
        private String error;
        private Throwable exception;
        private Date date;

       /**
        * @param error
        * @param t
        */
       LogEntry(String error, Throwable exception) {
           if(error == null) {
               throw new IllegalArgumentException("no error message");
           }
           this.error = error;
           this.exception = exception;
           this.date = new Date();
       }

        /**
         * @see java.lang.Object#toString()
         */
        public String toString() {
            if(exception != null) {
                return date.toString()
                    + " - "
                    + error
                    + Utils.getNewLine()
                    + "Error: "
                    + (exception.getLocalizedMessage() != null
                            ? exception.getLocalizedMessage() : exception.toString());

            } else {
                return date.toString()
                    + " - "
                    + error;
            }
        }

        /**
         * @return
         */
        public String toHtmlString() {
            if(exception != null) {
                return "<b>" + date.toString()
                    + "</b> - "
                    + error
                    + "<br>"
                    + "Error: "
                    + (exception.getLocalizedMessage() != null
                            ? exception.getLocalizedMessage() : exception.toString());
            } else {
                return "<b>" + date.toString()
                + "</b> - "
                + error
                + "<br>";
            }
        }
    }

    /**
     * @param size
     */
    public NonFatalErrorBuffer(int size) {
        super();
        this.fBuffer = new LinkedList<LogEntry>();
        this.fMaxSize = size;
    }

    /**
     * @return
     */
    public List<LogEntry> getLogEntries() {
        return new ArrayList<LogEntry>(fBuffer);
    }

    /**
     * @see com.ixora.common.ui.NonFatalErrorHandler#nonFatalError(java.lang.String, java.lang.Throwable)
     */
    public void nonFatalError(String error, Throwable t) {
        if(this.fBuffer.size() >= this.fMaxSize) {
            this.fBuffer.clear();
        }
        this.fBuffer.add(new LogEntry(error, t));
    }
}
