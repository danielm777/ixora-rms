/*
 * Created on 06-May-2005
 */
package com.ixora.common.ui;

import java.awt.Component;
import java.awt.Graphics;
import java.util.LinkedList;
import java.util.List;

import javax.swing.Icon;

/**
 * CompositeIcon
 */
public class CompositeIcon implements Icon {
    protected List<Icon>	listIcons;
    protected int		compositeWidth;
    protected int		compositeHeight;

    /**
     * Constructor
     */
    public CompositeIcon() {
        listIcons = new LinkedList<Icon>();
        compositeWidth = 0;
        compositeHeight = 0;
    }

    /**
     * @see javax.swing.Icon#getIconHeight()
     */
    public int getIconHeight() {
        return compositeHeight;
    }

    /**
     * @see javax.swing.Icon#getIconWidth()
     */
    public int getIconWidth() {
        return compositeWidth;
    }

    /**
     * Adds a new icon to the composite
     * @param icon
     */
    public void add(Icon icon) {
        compositeWidth += icon.getIconWidth();
        compositeHeight = Math.max(compositeHeight, icon.getIconHeight());
        listIcons.add(icon);
    }

    /**
     * @see javax.swing.Icon#paintIcon(java.awt.Component, java.awt.Graphics, int, int)
     */
    public synchronized void paintIcon(Component c, Graphics g, int x, int y) {
        // Paint all icons to the right of each other
        int dx = 0;
        for (Icon icon : listIcons) {
            icon.paintIcon(c, g, x+dx, y);
            dx += icon.getIconWidth();
        }
    }
}
