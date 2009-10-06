package com.ixora.common.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.util.StringTokenizer;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import com.ixora.common.exception.AppRuntimeException;
import com.ixora.common.ui.actions.ActionCancel;
import com.ixora.common.ui.actions.ActionOk;
import com.ixora.common.ui.forms.FormPanel;

/**
 * GUI Utils.
 * @author Daniel Moraru
 */
public final class UIUtils {

	private static final class StringInputDialog extends AppDialog {
		private JTextField fField;
		private FormPanel fForm;
		private String fResult;
		private JPanel fPanel;

		StringInputDialog(Dialog parent, String title, String msg) {
			super(parent, VERTICAL);
			init(title, msg);
		}

		StringInputDialog(Frame parent, String title, String msg) {
			super(parent, VERTICAL);
			init(title, msg);
		}

		private void init(String title, String msg) {
			setTitle(title);
			fField = UIFactoryMgr.createTextField();
			fField.setPreferredSize(new Dimension(250, fField.getPreferredSize().height));
			fForm = new FormPanel(FormPanel.VERTICAL2, SwingConstants.TOP);
			fForm.addPairs(new String[]{msg}, new Component[]{fField});

			fPanel = new JPanel(new BorderLayout());
			fPanel.add(fForm, BorderLayout.NORTH);

			buildContentPane();
		}

		/**
		 * @see com.ixora.common.ui.AppDialog#getDisplayPanels()
		 */
		protected Component[] getDisplayPanels() {
			return new Component[]{fPanel};
		}

		/**
		 * @see com.ixora.common.ui.AppDialog#getButtons()
		 */
		protected JButton[] getButtons() {
			return new JButton[]{
					UIFactoryMgr.createButton(new ActionOk() {
						public void actionPerformed(ActionEvent e) {
							fResult = fField.getText();
							dispose();
						}
					}),
					UIFactoryMgr.createButton(new ActionCancel(){
						public void actionPerformed(ActionEvent e) {
							fResult = null;
							dispose();
						}
					})
			};
		}

		public String getResult() {
			return fResult;
		}
	}

	/**
	 * Contains the details of an action.
	 */
	public static final class UsabilityDtls {
		private String text;
		private String shortDescription;
		private String longDescription;
		private char mnemonic;
		private KeyStroke accelerator;

		/**
		 * Constructor.
		 * @param text
		 * @param mnemonic
		 * @param acc
		 * @param shortDescription
		 * @param longDescription
		 */
		// no need to be public
		UsabilityDtls(
			String text,
			String shortDescription,
			String longDescription,
			char mnemonic,
			KeyStroke acc) {
			this.text = text;
			this.shortDescription = shortDescription;
			this.longDescription = longDescription;
			this.mnemonic = mnemonic;
			this.accelerator = acc;
		}

		/**
		 * @return the accelerator
		 */
		public KeyStroke getAccelerator() {
			return accelerator;
		}

		/**
		 * @return the mnemonic
		 */
		public char getMnemonic() {
			return mnemonic;
		}

		/**
		 * @return the text
		 */
		public String getText() {
			return text;
		}

		/**
		 * @return the long description
		 */
		public String getLongDescription() {
			return longDescription;
		}

		/**
		 * @return the short description
		 */
		public String getShortDescription() {
			return shortDescription;
		}
	}

	/**
	 * Constructor for UIUtils.
	 */
	private UIUtils() {
		super();
	}

	/**
	 * Shows the given dialog centered in relation to its parent.
	 * @param parent
	 * @param dlg
	 */
	public static void centerDialogAndShow(Component parent, Dialog dlg) {
        Dimension frameSize = null;
        Point loc = null;
        if(parent != null) {
            frameSize = parent.getSize();
            loc = parent.getLocation();
        }
        dlg.pack();
        Dimension dialogSize = dlg.getSize();
        if((frameSize != null) && (loc != null)) {
            dlg.setLocation(((frameSize.width - dialogSize.width) / 2)
                                             + loc.x,
                                             ((frameSize.height - dialogSize.height) / 2)
                                             + loc.y);
        }
        dlg.setVisible(true);
	}

	/**
	 * Displays the given frame maximized on screen.
	 * @param frame
	 */
	public static void maximizeFrameAndShow(JFrame frame) {
		Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screenSize = toolkit.getScreenSize();
        Insets screenInsets = toolkit.getScreenInsets(
        	frame.getGraphicsConfiguration());
		frame.setSize(
			(int)screenSize.getWidth()
			- (screenInsets.left + screenInsets.right),
			(int)screenSize.getHeight()
			- (screenInsets.top + screenInsets.bottom));
        frame.setLocation(screenInsets.left, screenInsets.top);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		frame.setVisible(true);
	}

    /**
     * Displays the given frame centered relative to it's owner.
     * @param frame
     */
    public static void centerFrameAndShow(Component owner, JFrame frame) {
        Dimension frameSize = null;
        Point loc = null;
        if(owner != null) {
            frameSize = owner.getSize();
            loc = owner.getLocation();
        }
        frame.pack();
        Dimension size = frame.getSize();
        if((frameSize != null) && (loc != null)) {
            frame.setLocation(((frameSize.width - size.width) / 2)
                                             + loc.x,
                                             ((frameSize.height - size.height) / 2)
                                             + loc.y);
        }
        frame.setVisible(true);
    }

    /**
     * @return the screen size
     */
    public static Dimension getScreenSize() {
    	return Toolkit.getDefaultToolkit().getScreenSize();
    }

	/**
	 * Reads the specified line and returns the usability details.
	 * The format is text;short desc;long desc;mnemonic_char;accelerator where
	 * accelerator follows the same format as that specified
	 * in java API. If the format in <i>line</i> is not understood
	 * it will be treated as pure text.
	 * @param line
	 * @return the usability details
	 */
	public static UsabilityDtls parseUsabilityString(String line) {
		StringTokenizer tok = new StringTokenizer(line, ";");
		String tmp;
		String text = null;
		String sd = null;
		String ld = null;
		char mnem = 0;
		KeyStroke acc = null;
		int i = 0;
		while(tok.hasMoreTokens()) {
			++i;
			tmp = tok.nextToken();
			if(tmp.trim().length() <= 0 || tmp.charAt(0) == '*') {
				continue;
			}
			switch(i) {
				case 1:
					text = tmp;
				break;
				case 2:
					sd = tmp;
				break;
				case 3:
					ld = tmp;
				break;
				case 4:
					mnem = tmp.charAt(0);
				break;
				case 5:
					acc = KeyStroke.getKeyStroke(tmp);
				break;
				default :
					break;
			}
		}
		return new UsabilityDtls(text, sd, ld, mnem, acc);
	}

	/**
	 * Applies the action details to the given component.
	 * @param menu
	 * @param dtls
	 */
	public static void setUsabilityDtls(String dtls, JMenu menu) {
		UIUtils.UsabilityDtls adtls = UIUtils.parseUsabilityString(dtls);
		menu.setText(adtls.getText());
		char mn = adtls.getMnemonic();
		if(mn != 0) {
			menu.setMnemonic(mn);
		}
	}

	/**
	 * Applies the action details to the given component.
	 * @param menuItem
	 * @param dtls
	 */
	public static void setUsabilityDtls(String dtls, JMenuItem menuItem) {
		UIUtils.UsabilityDtls adtls = UIUtils.parseUsabilityString(dtls);
		menuItem.setText(adtls.getText());
		String tmp = adtls.getShortDescription();
		if(tmp != null) {
			menuItem.setToolTipText(tmp);
		}
		char mn = adtls.getMnemonic();
		if(mn != 0) {
			menuItem.setMnemonic(mn);
		}
		KeyStroke acc = adtls.getAccelerator();
		if(acc != null) {
			menuItem.setAccelerator(acc);
		}
	}

	/**
	 * Applies the action details to the given component.
	 * @param button
	 * @param dtls
	 */
	public static void setUsabilityDtls(String dtls, JButton button) {
		UIUtils.UsabilityDtls adtls = UIUtils.parseUsabilityString(dtls);
		button.setText(adtls.getText());
		String tmp = adtls.getShortDescription();
		if(tmp != null) {
			button.setToolTipText(tmp);
		}
		char mn = adtls.getMnemonic();
		if(mn != 0) {
			button.setMnemonic(mn);
		}
	}

	/**
	 * Applies the action details to the given action.
	 * @param action
	 * @param dtls
	 */
	public static void setUsabilityDtls(String dtls, Action action) {
		UIUtils.UsabilityDtls adtls = UIUtils.parseUsabilityString(dtls);
		action.putValue(Action.NAME, adtls.getText());
		String tmp = adtls.getShortDescription();
		if(tmp != null) {
			action.putValue(Action.SHORT_DESCRIPTION, tmp);
		}
		tmp = adtls.getLongDescription();
		if(tmp != null) {
			action.putValue(Action.LONG_DESCRIPTION, tmp);
		}
		char mn = adtls.getMnemonic();
		if(mn != 0) {
			action.putValue(Action.MNEMONIC_KEY, new Integer(mn));
		}
		KeyStroke acc = adtls.getAccelerator();
		if(acc != null) {
			action.putValue(Action.ACCELERATOR_KEY, acc);
		}
	}

	/**
	 * Shows a input dialog with the given title and message.
	 * @param parent the parent component
	 * @param msgTitle the message for the title
	 * @param msgMessage the message for the message
	 * @return the value entered in the input dialog
	 */
	public static String getStringInput(Component parent,
				String msgTitle, String msgMessage) {
		Window win = null;
		if(parent instanceof Window) {
			win = (Window)parent;
		} else {
			win = SwingUtilities.getWindowAncestor(parent);
		}
		if(win == null) {
			throw new AppRuntimeException("Component has no window ancestor");
		}
		StringInputDialog dlg = null;
		if(win instanceof Frame) {
			dlg = new StringInputDialog((Frame)win, msgTitle, msgMessage);
		} else if(win instanceof Dialog) {
			dlg = new StringInputDialog((Dialog)win, msgTitle, msgMessage);
		} else {
			throw new AppRuntimeException("Component has no window ancestor");
		}
		dlg.setModal(true);
		UIUtils.centerDialogAndShow(parent, dlg);
		return dlg.getResult();
	}

	/**
	 * Shows a input dialog with the given title and message.
	 * @param parent the parent component
	 * @param msgTitle the message for the title
	 * @param msgMessage the message for the message
	 * @return the value entered in the input dialog
	 */
	public static boolean getBooleanOkCancelInput(Component parent,
				String msgTitle, String msgMessage) {
		return JOptionPane.showConfirmDialog(
			parent,
			msgMessage,
			msgTitle,
			JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION;
	}

	/**
	 * Shows a input dialog with the given title and message.
	 * @param parent the parent component
	 * @param msgTitle
	 * @param msgMessage
	 * @return the value entered in the input dialog
	 */
	public static boolean getBooleanYesNoInput(Component parent,
				String msgTitle, String msgMessage) {
		//Icon iconQuestion = UIConfiguration.getIcon("question.gif");
	    return JOptionPane.showConfirmDialog(
	    		parent,
	    		msgMessage,
	    		msgTitle,
	            JOptionPane.YES_NO_OPTION,
	            JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION;
	}

    /**
     * Shows a input dialog with the given title and message.
     * @param parent the parent component
     * @param msgTitle
     * @param msgMessage
     * @return the respective JOptionPane constant
     */
    public static int getYesNoCancelInput(Component parent,
                String msgTitle, String msgMessage) {
        //Icon iconQuestion = UIConfiguration.getIcon("question.gif");
        return JOptionPane.showConfirmDialog(
                parent,
                msgMessage,
                msgTitle,
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE);
    }

	/**
	 * Shows an error dialog with the given title and message.
	 * @param parent the parent component
	 * @param msgTitle
	 * @param msgMmessage
	 */
	public static void showError(Component parent,
				String msgTitle, String msgMmessage) {
		JOptionPane.showMessageDialog(
			parent,
			msgMmessage,
			msgTitle,
			JOptionPane.ERROR_MESSAGE);
	}

	/**
	 * Shows an warning dialog with the given title and message.
	 * @param parent the parent component
	 * @param msgTitle
	 * @param msgMmessage
	 */
	public static void showWarning(Component parent,
				String msgTitle, String msgMmessage) {
		JOptionPane.showMessageDialog(
			parent,
			msgMmessage,
			msgTitle,
			JOptionPane.WARNING_MESSAGE);
	}

	/**
	 * Shows an info dialog with the given title and message.
	 * @param parent the parent component
	 * @param msgTitle
	 * @param msgMmessage
	 */
	public static void showInfo(Component parent,
				String msgTitle, String msgMmessage) {
		JOptionPane.showMessageDialog(
			parent,
			msgMmessage,
			msgTitle,
			JOptionPane.INFORMATION_MESSAGE);
	}

	/**
	 * Returns an html version of the given string where best effort was made
	 * to keep the line length smaller than <code>lineLength</code>.
	 * @param txt
	 * @param lineLength
	 * @return
	 */
	public static String getMultilineHtmlText(String txt, int lineLength) {
        return getMultilineHtmlText(txt, " \t\n\r\f", lineLength);
	}

    /**
     * Returns an html version of the given string where best effort was made
     * to keep the line length smaller than <code>lineLength</code>.
     * @param txt
     * @param delimiters
     * @param lineLength
     * @return
     */
    public static String getMultilineHtmlText(String txt, String delimiters, int lineLength) {
        if(txt.length() <= lineLength) {
            return "<html>" + txt + "</html>";
        }
        StringTokenizer tok = new StringTokenizer(txt, delimiters);
        StringBuffer buff = new StringBuffer((int)(txt.length()*1.3));
        buff.append("<html>");
        int counter = 0;
        while(tok.hasMoreTokens()) {
            String s = tok.nextToken();
            counter += s.length();
            if(counter > lineLength) {
                counter = 0;
                buff.append("<br>");
            }
            buff.append(" ");
            buff.append(s);
        }
        buff.append("</html>");
        return buff.toString();
    }

    /**
     * Returns an html version of the given string where each line will have a length
     * of <code>lenChars</code>.
     * @param txt
     * @param lenChars
     * @return
     */
    public static String getMultilineHtmlTextChars(String txt, int lenChars) {
        if(txt.length() <= lenChars) {
            return "<html>" + txt + "</html>";
        }
        StringBuffer buff = new StringBuffer((int)(txt.length()*1.3));
        buff.append("<html>");
        int counter = 0;
        char[] chars = txt.toCharArray();
        for(int i = 0; i < chars.length; i++) {
            counter++;
            if(counter > lenChars) {
                counter = 0;
                buff.append("<br>");
            }
            buff.append(chars[i]);
        }
        buff.append("</html>");
        return buff.toString();
    }

	/**
	 * @param txt
	 * @param lineLength
	 * @return
	 */
	public static String getMultilineHtmlFragment(String txt, int lineLength) {
		StringTokenizer tok = new StringTokenizer(txt);
		if(tok.countTokens() == 1) {
			return txt;
		}
		StringBuffer buff = new StringBuffer((int)(txt.length()*1.3));
		int counter = 0;
		while(tok.hasMoreTokens()) {
			String s = tok.nextToken();
			buff.append(s);
			buff.append(" ");
			counter += s.length();
			if(counter > lineLength) {
				counter = 0;
				buff.append("<br>");
			}
		}
		return buff.toString();
	}

	/**
	 * @param textAreaFeedback
	 */
	public static void setFocus(final JComponent comp) {
		SwingUtilities.invokeLater(new Runnable(){
			public void run() {
				comp.requestFocus();
			}
		});
	}
}
