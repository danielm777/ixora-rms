package com.ixora.common.ui;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;

import javax.swing.JButton;

import com.ixora.common.MessageRepository;
import com.ixora.common.messages.Msg;
import com.ixora.common.ui.actions.ActionClose;

/**
 * Dialog for displaying exceptions.
 * @author Daniel Moraru
 */
final class ShowInfoDialog extends AppDialog {
    private javax.swing.JScrollPane scrollPaneInfo;
    private javax.swing.JTextArea textInfo;
    private static final Dimension size = new Dimension(400, 250);

    /**
     * ShowExceptionDialog constructor comment.
     */
    public ShowInfoDialog() {
        super(VERTICAL);
        initialize();
    }

    /**
     * ShowExceptionDialog constructor comment.
     * @param owner java.awt.Dialog
     */
    public ShowInfoDialog(Dialog owner) {
        super(owner, VERTICAL);
        initialize();
    }

    /**
     * ShowExceptionDialog constructor comment.
     * @param owner java.awt.Frame
     */
    public ShowInfoDialog(Frame owner) {
        super(owner, VERTICAL);
        initialize();
    }

    /**
     * @see bktoolkit.infra.utils.gui.AppDialog#getDisplayPanels()
     */
    protected Component[] getDisplayPanels() {
    	return new Component[]{getJScrollPaneInfo()};
    }

	/**
	 * Sets the info to display.
	 * @param info
	 */
    public void setInfo(String info) {
        getJTextInfo().append(info.toString());
        getJTextInfo().setCaretPosition(0);
    }

    /**
     * Return the JScrollPaneInfo.
     * @return javax.swing.JScrollPane
     */
    private javax.swing.JScrollPane getJScrollPaneInfo() {
        if(scrollPaneInfo == null) {
            scrollPaneInfo = UIFactoryMgr.createScrollPane();
            scrollPaneInfo.setViewportView(getJTextInfo());
        }
        return scrollPaneInfo;
    }

    /**
     * Return the JTextInfo property value.
     * @return javax.swing.JTextPane
     */
    private javax.swing.JTextArea getJTextInfo() {
        if(textInfo == null) {
            textInfo = UIFactoryMgr.createTextArea();
            textInfo.setWrapStyleWord(true);
            textInfo.setTabSize(2);
        }
        return textInfo;
    }

    /**
     * Initialize the class.
     */
    private void initialize() {
		setPreferredSize(size);
		setTitle(MessageRepository.get(Msg.COMMON_UI_INFO_DIALOG_TITLE));
		buildContentPane();
    }

    /**
     * Shows the dialog.
     * @param parent Parent component
     * @param info info to display
     */
    public static void showDialog(Window parent, String info) {
	    ShowInfoDialog dialog;
	    if(parent instanceof Dialog) {
	        dialog = new ShowInfoDialog((Dialog)parent);
	    } else {
	        if(parent instanceof Frame) {
	            dialog = new ShowInfoDialog((Frame)parent);
	        } else {
	            dialog = new ShowInfoDialog();
	        }
	    }
	    dialog.setModal(true);
	    dialog.setInfo(info);
		UIUtils.centerDialogAndShow(parent, dialog);
    }

	/**
	 * @see com.ixora.common.ui.AppDialog#getButtons()
	 */
	protected JButton[] getButtons() {
		JButton close = UIFactoryMgr.createButton(new ActionClose() {
			public void actionPerformed(ActionEvent e) {
				try {
					dispose();
				} catch(Exception ex) {
					UIExceptionMgr.userException(ex);
				}
			}
		});
		return new JButton[]{close};
	}
}