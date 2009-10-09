package com.ixora.common.security.license.ui;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.net.URL;
import java.util.Date;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.HyperlinkEvent.EventType;

import com.ixora.common.MessageRepository;
import com.ixora.common.logging.AppLogger;
import com.ixora.common.logging.AppLoggerFactory;
import com.ixora.common.messages.Msg;
import com.ixora.common.security.license.License;
import com.ixora.common.security.license.LicenseMgr;
import com.ixora.common.ui.AppDialog;
import com.ixora.common.ui.UIExceptionMgr;
import com.ixora.common.ui.UIFactoryMgr;
import com.ixora.common.ui.UIUtils;
import com.ixora.common.ui.actions.ActionClose;
import com.ixora.common.utils.Utils;


/**
 * Dialog for displaying the license.
 * @author Daniel Moraru
 */
final class ShowLicenseDialog extends AppDialog {
	private static final long serialVersionUID = 3678441293198654535L;
	/** Logger */
	private static final AppLogger logger = AppLoggerFactory.getLogger(
				ShowLicenseDialog.class);
	/** HTML pane */
    private JEditorPane fHtmlPane;
	/** Event handler */
    private EventHandler fEventHandler;
    /** License text */
	private String fLicenseKey;

	@SuppressWarnings("serial")
	private final class ActionUpdateLicense extends AbstractAction {
		ActionUpdateLicense() {
			super();
			UIUtils.setUsabilityDtls(MessageRepository.get(Msg.COMMON_UI_ACTIONS_UPDATE_LICENSE), this);

		}
		/**
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent e) {
			handleUpdateLicense();
		}
	}

    private final class EventHandler implements HyperlinkListener {
		/**
		 * @see javax.swing.event.HyperlinkListener#hyperlinkUpdate(javax.swing.event.HyperlinkEvent)
		 */
		public void hyperlinkUpdate(HyperlinkEvent e) {
			if(e.getEventType() == EventType.ACTIVATED) {
				handleSeeURL(e.getURL());
			}
		}
    }

    /**
     * ShowExceptionDialog constructor comment.
     */
    public ShowLicenseDialog() {
        super(VERTICAL);
        initialize();
    }

    /**
     * ShowExceptionDialog constructor comment.
     * @param owner java.awt.Dialog
     */
    public ShowLicenseDialog(Dialog owner) {
        super(owner, VERTICAL);
        initialize();
    }

    /**
     * ShowExceptionDialog constructor comment.
     * @param owner java.awt.Frame
     * @param eh
     */
    public ShowLicenseDialog(Frame owner) {
        super(owner, VERTICAL);
        initialize();
    }

    /**
     * @see com.ixora.common.ui.AppDialog#getDisplayPanels()
     */
    protected Component[] getDisplayPanels() {
       	return new Component[] {fHtmlPane};
    }

	/**
	 * Initializes this dialog.
	 */
	private void initialize() {
		this.padding = 0;
		this.fEventHandler = new EventHandler();
        setTitle(MessageRepository.get(Msg.COMMON_UI_TITLE_LICENSE));
        setPreferredSize(new Dimension(350, 350));
        fHtmlPane = UIFactoryMgr.createHtmlPane();
        fHtmlPane.addHyperlinkListener(this.fEventHandler);
        fHtmlPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        showLicenseText();
        buildContentPane();
	}

	/**
	 * Sets the license text.
	 */
	private void showLicenseText() {
        String text = null;
        // calculate evaluation days left if under an evaluation license
        License lic = LicenseMgr.getLicense();
        if(lic == null) {
        	// direct user to company website
            text = MessageRepository.get(
                    Msg.COMMON_UI_TEXT_LICENSE_GET_LICENSE);
        } else if(lic.isEvaluation()) {
            Date expDate = lic.getExpiryDate();
            if(expDate != null) {
                long daysLeft = expDate.getTime() - new Date().getTime();
                daysLeft = daysLeft / 86400000;
                if(daysLeft < 0) {
                    daysLeft = 0;
                }
                text = MessageRepository.get(
                        Msg.COMMON_UI_TEXT_LICENSE_DOCUMENT_EVALUATION,
                        new String[] {String.valueOf(daysLeft)});
            }
        } else if(lic.getType().equals("free")) {
            text = MessageRepository.get(
                    Msg.COMMON_UI_TEXT_LICENSE_DOCUMENT_FREE);
        }

        if(lic != null && text == null) {
            Date issued = lic.getIssueDate();
            String type = lic.getType();
            String key = lic.getKey();
            text = MessageRepository.get(
                    Msg.COMMON_UI_TEXT_LICENSE_DOCUMENT, new String[] {
                            issued.toString(),
                            type,
                            key
                    });

        }
        Date exp = LicenseMgr.isFirstEvaluationFinished();
        if(exp != null) {
        	text = "<html><font color='#AA0000'>Your evaluation period expired on " + exp + "</font>" +
        		"<br>You can purchase a license at <a href='http://www.ixoragroup.com/IxoraRMS'>www.ixoragroup.com/IxoraRMS</a>.</html>";
        }
        fHtmlPane.setText(text);
	}

	/**
	 * Overridden to remove border.
	 * @see com.ixora.common.ui.AppDialog#getJDialogContentPane()
	 */
	protected JPanel getJDialogContentPane() {
		JPanel ret = super.getJDialogContentPane();
        // remove border
       	ret.setBorder(null);
       	return ret;
	}

    /**
     * Shows the dialog.
     * @param parent the parent component
     */
    public static String showDialog(Window parent) {
        ShowLicenseDialog aDialog;
        if(parent instanceof Dialog) {
            aDialog = new ShowLicenseDialog(
            	(Dialog)parent);
        } else {
            if(parent instanceof Frame) {
            	aDialog = new ShowLicenseDialog(
                                               (Frame)parent);
            } else {
            	aDialog = new ShowLicenseDialog();
            }
        }
        aDialog.setModal(true);
		UIUtils.centerDialogAndShow(parent, aDialog);
		return aDialog.getLicenseText();
    }

	/**
	 * @return
	 */
	private String getLicenseText() {
		return fLicenseKey;
	}

	/**
	 * @see com.ixora.common.ui.AppDialog#getButtons()
	 */
	@SuppressWarnings("serial")
	protected JButton[] getButtons() {
		JButton close = UIFactoryMgr.createButton(new ActionClose() {
			public void actionPerformed(ActionEvent e) {
				try {
					dispose();
				} catch(Exception ex) {
					logger.error(ex);
				}
			}
		});
		JButton updateLicense = UIFactoryMgr.createButton(new ActionUpdateLicense());
		return new JButton[]{close, updateLicense};
	}

	/**
	 * Launches the external browser with
	 * the given url.
	 */
	private void handleSeeURL(URL url) {
		try {
			Utils.launchBrowser(url);
		} catch(Throwable e) {
			UIExceptionMgr.userException(e);
		}
	}

	/**
	 *
	 */
	private void handleUpdateLicense() {
		try {
			// TODO localize
			fLicenseKey = UIUtils.getStringInput(this, "License", "Please enter the license key:");
			if(fLicenseKey != null) {
				fLicenseKey = fLicenseKey.trim();
				// try to update license
				LicenseMgr.updateLicense(fLicenseKey);
				showLicenseText();
			}
		} catch(Exception ex) {
			UIExceptionMgr.userException(ex);
		}
	}
}