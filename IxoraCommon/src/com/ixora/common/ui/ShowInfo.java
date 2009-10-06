package com.ixora.common.ui;

import java.awt.Window;


/**
 * Displays info.
 * @author Daniel Moraru
 */
public final class ShowInfo {
    /**
     * Constructor for ShowException.
     */
    private ShowInfo() {
        super();
    }

    /**
     * Displays the info.
     * @param parent Parent component
     * @param info
     */
    public static void show(Window parent, String info) {
        ShowInfoDialog.showDialog(parent, info);
    }
}