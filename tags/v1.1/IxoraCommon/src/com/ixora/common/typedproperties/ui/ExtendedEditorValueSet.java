/*
 * Created on 02-Oct-2004
 */
package com.ixora.common.typedproperties.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import javax.swing.JList;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import com.ixora.common.typedproperties.PropertyEntry;
import com.ixora.common.ui.UIExceptionMgr;

/**
 * Extended editor used to choose possible values for properties
 * of any type.
 * @author Daniel Moraru
 */
final class ExtendedEditorValueSet<T> extends ExtendedEditorAbstract<T> {
    /** The max height of the popup menu */
    private static final int HEIGHT = 200;
    /** List that displays all values in set */
    private JList list;
    /** The list's scroll pane */
    private JScrollPane scrollPane;
    /** Popup menu showing the list */
    private JPopupMenu popup;
    /** This will be the component owing the popup */
    private CellComponent<T> popupOwner;
    /** Event handler */
    private EventHandler eventHandler;
    /** True if editting has stopped */
    private boolean edittingStopped;
    /**
     * Event handler.
     */
    private final class EventHandler extends MouseAdapter implements PopupMenuListener,
			MouseMotionListener {
        public void mouseClicked(MouseEvent e) {
            handleMouseClicked(e);
        }
        public void mouseMoved(MouseEvent e) {
            handleMouseMoved(e);
        }
		public void mouseDragged(MouseEvent e) {
		}
		public void popupMenuCanceled(PopupMenuEvent e) {
		}
		public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
			handlePopupHidden(e);
		}
		public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
		}
    }

    /**
     * Constructor.
     */
    public ExtendedEditorValueSet() {
        super();
        eventHandler = new EventHandler();
        list = new JList();
        scrollPane = new JScrollPane();
        scrollPane.setViewportView(list);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(null);
        list.setBorder(null);
        list.setBackground(CellComponent.BACKGROUND);
        list.setCellRenderer(new ValueSetListCellRenderer());
        list.setAutoscrolls(true);
        popup = new JPopupMenu();
        popup.setBorder(null);
        popup.add(scrollPane);
        popup.addPopupMenuListener(eventHandler);
        list.addMouseListener(eventHandler);
        list.addMouseMotionListener(eventHandler);
    }

	/**
	 * Sets the component.
	 * @param component
	 */
	public void setComponentName(String component) {
	    ((ValueSetListCellRenderer)list.getCellRenderer()).setComponentName(component);
	}

    /**
     * @see com.ixora.common.ui.typedproperties.ExtendedEditor#launch(java.awt.Component, PropertyEntry pe)
     */
    public void launch(Component owner, PropertyEntry<T> pe) {
        ((ValueSetListCellRenderer)list.getCellRenderer()).setPropertyEntry(pe);
		list.setListData(pe.getValueSet());
		list.setSelectedValue(pe.getValue(), true);
        int e = list.getModel().getSize();
        int cellHeight = popupOwner.getHeight();
        int height = e * (cellHeight + 3);
        this.scrollPane.setPreferredSize(
                new Dimension(popupOwner.getWidth() - 1,
                height > HEIGHT ? HEIGHT : height));
        popup.show(this.popupOwner, 0, this.popupOwner.getHeight());
        this.popupOwner.addMouseListener(eventHandler);
        edittingStopped = false;
    }

	/**
	 * @see com.ixora.common.typedproperties.ui.ExtendedEditor#close()
	 */
	public void close() {
		popup.setVisible(false);
	}

// package
    /**
     * @param display
     */
    void setCellComponent(CellComponentExtended<T> display) {
        this.popupOwner = display;
    }

    /**
     * Handles mouse click events on the list.
     */
    private void handleMouseClicked(MouseEvent e) {
        try {
            if(e.getSource() == list) {
                Object obj = list.getSelectedValue();
                edittingStopped = true;
                popup.setVisible(false);
                fireEditingStopped(obj);
            } else {
            	popup.setVisible(false);
            }
        } catch(Exception ex) {
            UIExceptionMgr.userException(ex);
        }
    }

    /**
     * Handles mouse moved events on the list
     */
    private void handleMouseMoved(MouseEvent e) {
        try {
            if(e.getSource() == list) {
                int idx = list.locationToIndex(e.getPoint());
                if(idx != list.getSelectedIndex()) {
                    list.setSelectedIndex(idx);
                }
            }
        } catch(Exception ex) {
            UIExceptionMgr.userException(ex);
        }
    }

	/**
	 * @param e
	 */
	private void handlePopupHidden(PopupMenuEvent e) {
        try {
            if(e.getSource() == popup) {
            	if(!edittingStopped) {
            		fireEditingCanceled();
            	}
            	this.popupOwner.removeMouseListener(eventHandler);
            }
        } catch(Exception ex) {
            UIExceptionMgr.userException(ex);
        }
	}
}
