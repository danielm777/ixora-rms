package com.ixora.common.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.WindowConstants;

/**
 * Dialog that displays a number of resizable panels horizontally
 * or vertically. Ideally it should be the superclass of all
 * dialogs in the application as it handles common
 * work and it makes it easy to alter the behaviour or
 * look and feel of dialogs throughout the application.
 * @author: Daniel Moraru
 */
public abstract class AppDialog extends JDialog {
	protected static final int VERTICAL = 0;
	protected static final int HORIZONTAL = 1;
    private JPanel contentPane;
    private JPanel jPanelSouth;
	private JPanel jPanelCenter;
	/** Display panels */
	private Component[] displayPanels;
	/** Panel orientation */
	private int orientation;
	/** Padding used to space panels */
	protected int padding;
	/** Whether or not to process buttons */
	protected boolean processButtons;

    /**
     * AppDialog constructor comment.
     * @param orientation
     */
    protected AppDialog(int orientation) {
        super();
        initialize(orientation, true);
    }

    /**
     * AppDialog constructor comment.
     * @param parent
     * @param orientation
     */
    protected AppDialog(Dialog parent, int orientation) {
        super(parent);
        initialize(orientation, true);
    }

   /**
     * AppDialog constructor comment.
     * @param parent
     * @param orientation
     */
    protected AppDialog(Frame parent, int orientation) {
        super(parent);
        initialize(orientation, true);
    }

    /**
     * AppDialog constructor comment.
     * @param parent
     * @param orientation
     * @param processButtons
     */
    protected AppDialog(Dialog parent, int orientation, boolean processButtons) {
        super(parent);
        initialize(orientation, processButtons);
    }

   /**
     * AppDialog constructor comment.
     * @param parent
     * @param orientation
     * @param processButtons
     */
    protected AppDialog(Frame parent, int orientation, boolean processButtons) {
        super(parent);
        initialize(orientation, processButtons);
    }

	/**
	 * Subclasses must call this method in their constructor after
	 * their initialization is complete.
	 */
	protected final void buildContentPane() {
       	this.displayPanels = getDisplayPanels();
    	if(this.displayPanels == null || this.displayPanels.length == 0) {
    		throw new IllegalArgumentException("no panels to display");
    	}
		setContentPane(getJDialogContentPane());
	}

	/**
	 * Removes a display panel.
	 * @param panel
	 */
	protected void removeDisplayPanel(Component panel) {
		getJPanelCenter().remove(panel);
	}

	/**
	 * Refreshes the display.
	 */
	protected void repaintDisplay() {
		for(Component panel : displayPanels) {
			if(panel instanceof JComponent) {
				JComponent jpanel = (JComponent)panel;
				jpanel.revalidate();
				jpanel.repaint();
			} else {
				panel.repaint();
			}
		}
	}

	/**
	 * Adds a display panel.
	 * @param panel
	 */
	protected void addDisplayPanel(Component panel) {
		getJPanelCenter().add(panel);
	}

    /**
     * Returns the panels to display.
     * @return java.awt.Component[]
     */
    protected abstract Component[] getDisplayPanels() ;

    /**
     * Returns the buttons to display at the bottom of this
     * dialog.<br> If <code>processButton</code> is true the
     * following processing is done:
     * The first button in the list will be made the default
     * button and the last one will be the 'escape'(its action
     * triggered by pressing the Esc key)  button.<br>
     * As per Java UI design recommendations the default and 'escape'
     * buttons will have their mnemonics removed.<br>
     * Note: for the 'escape' button to work it must have an action
     * associated with it.
     * @return the buttons to display
     */
    protected abstract JButton[] getButtons();

    /**
     * Returns the the box containing the panels to be displayed.
     * @return java.awt.Container
     */
    private JPanel getJPanelCenter() {
        if(jPanelCenter == null) {
            jPanelCenter = new JPanel();
			jPanelCenter.setLayout(new BoxLayout(jPanelCenter, this.orientation == VERTICAL ? BoxLayout.Y_AXIS : BoxLayout.X_AXIS));
			for(int i = 0; i < this.displayPanels.length; ++i) {
                jPanelCenter
                    .add(this.displayPanels[i]);
                if(padding > 0) {
	                if(i != this.displayPanels.length - 1) {
		                jPanelCenter.add((orientation == HORIZONTAL ?
							Box.createHorizontalStrut(padding)
							: Box.createVerticalStrut(padding)));
	                }
                }
             }
        }
        return jPanelCenter;
    }

    /**
     * Return the JDialogContentPane property value.
     * @return javax.swing.JPanel
     */
    protected javax.swing.JPanel getJDialogContentPane() {
        if(contentPane == null) {
            contentPane = new javax.swing.JPanel();
            contentPane.setOpaque(true);
           	contentPane.setBorder(
            			BorderFactory.createEmptyBorder(padding, padding, padding, padding));
            contentPane.setLayout(new BorderLayout());
            contentPane
                .add(getJPanelCenter(), BorderLayout.CENTER);
            if(getButtons() != null) {
            	contentPane
	            	.add(getJPanelSouth(), BorderLayout.SOUTH);
            }
        }
        return contentPane;
    }


    /**
     * Return the JPanelSouth property value.
     * @return javax.swing.JPanel
     */
    private javax.swing.JPanel getJPanelSouth() {
        if(jPanelSouth == null) {
	        JButton[] buttons = getButtons();
	        // TODO temporary fix for JDK6.0 as FlowLayout makes the
	        // buttons too small for their text
/*	        if(JavaVersion.isAtLeast(JavaVersion.Version.V1_6)) {
	        	jPanelSouth = new JPanel(new GridLayout(1, buttons.length));
		        for (int i = 0; i < buttons.length; i++) {
					jPanelSouth.add(buttons[i]);
				}
	        } else {
*/		        jPanelSouth = new JPanel(new FlowLayout(FlowLayout.TRAILING));
		        for (int i = 0; i < buttons.length; i++) {
					jPanelSouth.add(buttons[i]);
				}
//	        }

	        if(processButtons) {
		        JButton first = buttons[0];
		        JButton last = buttons[buttons.length - 1];
		        // disable mnemonics for the default and the escape button
		        first.setMnemonic(KeyEvent.VK_UNDEFINED);
		        last.setMnemonic(KeyEvent.VK_UNDEFINED);
		        getRootPane().setDefaultButton(buttons[0]);
		    	// make 'escape' button work
		    	Action escapeKeyAction = last.getAction();
		    	if(escapeKeyAction != null) {
			    	KeyStroke cancelKeyStroke = KeyStroke.getKeyStroke((char)KeyEvent.VK_ESCAPE);
			    	InputMap inputMap = last.getInputMap(JComponent.
			    						     WHEN_IN_FOCUSED_WINDOW);
			    	ActionMap actionMap = last.getActionMap();
			    	if (inputMap != null && actionMap != null) {
			    	    inputMap.put(cancelKeyStroke, "escape");
			    	    actionMap.put("escape", escapeKeyAction);
			    	}
		    	}
	        }
        }
        return jPanelSouth;
    }

	/**
	 * Initializes the dialog.
	 * @param orientation
	 * @param processButtons
	 */
	private void initialize(int orientation, boolean processButtons) {
		this.orientation = orientation;
		this.processButtons = processButtons;
		this.padding = UIConfiguration.getPanelPadding();
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	}
}