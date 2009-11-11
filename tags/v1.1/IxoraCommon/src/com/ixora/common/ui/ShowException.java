package com.ixora.common.ui;

import java.awt.Window;

/**
 * Displays exceptions.
 * @author Daniel Moraru
 */
public final class ShowException {
    /**
     * Constructor for ShowException.
     */
    private ShowException() {
        super();
    }

    /**
     * Displays the exception.
     * @param parent Parent component
     * @param exception
     */
    public static void show(Window parent, Throwable exception) {
        ShowExceptionDialog.showDialog(parent, exception);
    }
}