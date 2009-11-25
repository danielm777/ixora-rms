package com.ixora.common.ui;

import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.HyperlinkEvent.EventType;

import org.apache.commons.httpclient.NameValuePair;

import com.ixora.common.MessageRepository;
import com.ixora.common.exception.AppException;
import com.ixora.common.exception.AppRuntimeException;
import com.ixora.common.logging.AppLogger;
import com.ixora.common.logging.AppLoggerFactory;
import com.ixora.common.messages.Msg;
import com.ixora.common.net.NetUtils;
import com.ixora.common.security.license.License;
import com.ixora.common.security.license.LicenseMgr;
import com.ixora.common.ui.actions.ActionClose;
import com.ixora.common.update.Module;
import com.ixora.common.update.UpdateMgr;
import com.ixora.common.utils.Utils;


/**
 * Dialog for displaying exceptions in a production
 * environment.
 * @author Daniel Moraru
 */
final class ShowExceptionDialog extends AppDialog {
	private static final long serialVersionUID = 7271366186473496191L;
	/** Logger used to log exceptions with internal app error flag set */
	private static final AppLogger logger = AppLoggerFactory.getLogger(
				ShowExceptionDialog.class);
	/** Dimension to use when showing exceptions with small text */
	private static final Dimension smallSize = new Dimension(300, 130);
	/** Dimension to use when showing exceptions with large text */
	private static final Dimension largeSize = new Dimension(400, 230);
	/** Used to display long text exceptions */
    private JEditorPane textExceptionLong;
    /** Used to display short text exceptions */
    private JTextField textExceptionShort;
    /** Main panel */
    private JPanel panel;
    /** Exception */
    private Throwable exception;
    /** True if the current execption is an internal error */
    private boolean internalError;
    /** True if the current exception requires an html renderer */
    private boolean requiresHtmlRenderer;
	/** Event handler */
    private EventHandler eventHandler;

    private final class EventHandler implements HyperlinkListener {
		/**
		 * @see javax.swing.event.HyperlinkListener#hyperlinkUpdate(javax.swing.event.HyperlinkEvent)
		 */
		public void hyperlinkUpdate(HyperlinkEvent e) {
            if(e.getEventType() == EventType.ACTIVATED) {
                handleHyperlinkActivated(e);
			}
		}
    }

    /**
     * ShowExceptionDialog constructor comment.
     */
    public ShowExceptionDialog() {
        super(VERTICAL);
        initialize();
    }

    /**
     * ShowExceptionDialog constructor comment.
     * @param owner java.awt.Dialog
     */
    public ShowExceptionDialog(Dialog owner) {
        super(owner, VERTICAL);
        initialize();
    }

    /**
     * ShowExceptionDialog constructor comment.
     * @param owner java.awt.Frame
     * @param eh
     */
    public ShowExceptionDialog(Frame owner) {
        super(owner, VERTICAL);
        initialize();
    }

    /**
     * @see com.ixora.common.ui.AppDialog#getDisplayPanels()
     */
    protected Component[] getDisplayPanels() {
       	return new Component[]{panel};
    }

	/**
	 * Sets the exception to show.
	 * @param ex
	 */
    public void setException(Throwable ex) {
    	this.exception = ex;
        this.internalError = false;
        this.requiresHtmlRenderer = false;
        String msg = ex.getLocalizedMessage();
    	if(msg == null) {
    		msg = ex.toString();
    	}
    	if(ex instanceof AppException) {
    		AppException aex = (AppException)ex;
    		if(aex.isInternalAppError()) {
    			internalError = true;
    			logger.error(aex);
    			setTitle(MessageRepository.get(
    				Msg.COMMON_UI_TEXT_INTERNAL_APPLICATION_ERROR));
    		} else if(aex.requiresHtmlRenderer()) {
    		    requiresHtmlRenderer = true;
            }
    	} else if(ex instanceof AppRuntimeException) {
            AppRuntimeException aex = (AppRuntimeException)ex;
            if(aex.isInternalAppError()) {
                internalError = true;
                logger.error(aex);
                setTitle(MessageRepository.get(
                    Msg.COMMON_UI_TEXT_INTERNAL_APPLICATION_ERROR));
            } else if(aex.requiresHtmlRenderer()) {
                requiresHtmlRenderer = true;
            }
        }
    	if(internalError || requiresHtmlRenderer) {
			// prepare the html pane
	        textExceptionLong.setContentType("text/html");
			StringBuffer buff = new StringBuffer();
			buff.append("<html>");
            if(internalError) {
                buff.append(
                    MessageRepository.get(
                            Msg.COMMON_UI_TEXT_INTERNAL_APPLICATION_ERROR_SEND));
            }
			buff.append("<p>");
			buff.append(msg);
			buff.append("</p></html>");
			msg = buff.toString();
    		setPreferredSize(largeSize);
    		textExceptionLong.setText(msg);
    		((CardLayout)panel.getLayout()).show(panel, "long");
    	} else if(msg.length() > 80) {
    		setPreferredSize(largeSize);
    		textExceptionLong.setContentType("text/plain");
    		textExceptionLong.setText(msg);
    		((CardLayout)panel.getLayout()).show(panel, "long");
    	} else {
    		textExceptionShort.setText(msg);
    		// adjust the width if the text is too long
    		Dimension d = textExceptionShort.getPreferredSize();
    		if(d.width > smallSize.width - 20) {
    			setPreferredSize(new Dimension(d.width + 100, smallSize.height));
    		} else {
    			setPreferredSize(smallSize);
    		}

    		((CardLayout)panel.getLayout()).show(panel, "short");
    	}
    	// if any other type be a little bit more verbose
    	// for instance java.io.FileNotFoundException only
    	// has in the message the name of the file
    	// which is pretty useless so use here in the title
    	// the last part of the exception's class name
    	// as this is usually self explanatory
    	// (this pattern is followed everywhere in
    	// the jdk to avoid localization issues which is fair enough)
    	// in general this situation shouldn't happen very
    	// often as the basic exception should be wrapped
    	// inside an AppException and given a meaningful message
    	if(!(ex instanceof AppException) && !(ex instanceof AppRuntimeException)) {
    		setTitle(getTitle() + " (" + getClassName(ex) + ")");
    	}
    }

	/**
	 * Initializes this dialog.
	 */
	private void initialize() {
		this.eventHandler = new EventHandler();
        setSize(smallSize);
        setTitle(MessageRepository.get(Msg.COMMON_UI_TEXT_ERROR));
        if(isDefaultLookAndFeelDecorated()) {
        	getRootPane().setWindowDecorationStyle(JRootPane.ERROR_DIALOG);
        }
        textExceptionLong = UIFactoryMgr.createHtmlPane();
        textExceptionLong.setBorder(
            	BorderFactory.createEmptyBorder(6, 6, 6, 6));
        textExceptionLong.setEditable(false);
        textExceptionLong.addHyperlinkListener(this.eventHandler);
        textExceptionShort = UIFactoryMgr.createTextField();
        textExceptionShort.setHorizontalAlignment(JTextField.CENTER);
        textExceptionShort.setBorder(null);
        textExceptionShort.setEditable(false);

    	panel = new JPanel(new CardLayout());
    	JScrollPane s = UIFactoryMgr.createScrollPane();
    	s.setBorder(null);
    	s.setViewportView(textExceptionLong);
    	panel.add(s, "long");
    	panel.add(textExceptionShort, "short");

    	// set background color to the color of the panel
        textExceptionShort.setBackground(panel.getBackground());
        textExceptionLong.setBackground(panel.getBackground());

        buildContentPane();
	}

    /**
     * Shows the dialog.
     * @param parent Parent component
     * @param ex Exception to display
     */
    public static void showDialog(Window parent, Throwable ex) {
    	try {
	        ShowExceptionDialog aShowExceptionDialog;
	        if(parent instanceof Dialog) {
	            aShowExceptionDialog = new ShowExceptionDialog(
	            	(Dialog)parent);
	        } else {
	            if(parent instanceof Frame) {
	                aShowExceptionDialog = new ShowExceptionDialog(
	                                               (Frame)parent);
	            } else {
	                aShowExceptionDialog = new ShowExceptionDialog();
	            }
	        }
	        aShowExceptionDialog.setModal(true);
	        aShowExceptionDialog.setException(ex);
			UIUtils.centerDialogAndShow(parent, aShowExceptionDialog);
    	} catch(Exception e) {
    		logger.error(e);
    	}
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
		return new JButton[]{close};
	}

	/**
	 * Sends the current internal application error.
	 */
	private void handleSendError(final URL url) {
		try {
			// don't penalize the user
			// run it on a separate thread
			final Throwable copy = this.exception;
			new Thread(new Runnable() {
				public void run() {
					try {
						Module[] mods = UpdateMgr.getRegisteredModules();
						StringBuffer buff = new StringBuffer();
						if(!Utils.isEmptyArray(mods)) {
							for(Module mod : mods) {
								buff.append(mod.toString()).append(Utils.getNewLine());
							}
						}
                        License lic = LicenseMgr.getLicense();
						NetUtils.postHttpForm(
								url, 
								new NameValuePair[]{
									new NameValuePair("license", lic.toString(true)),
									new NameValuePair("error", Utils.getTrace(copy).toString()),
									new NameValuePair("appversion", buff.toString())
								}, 
								null);
					} catch(Exception e) {
						logger.error(e);
					}
				}}).start();
			StringBuffer buff = new StringBuffer();
			buff.append("<html>");
			buff.append(MessageRepository.get(Msg.COMMON_UI_TEXT_THANK_YOU));
			buff.append("<p>");
			String msg = this.exception.getLocalizedMessage();
			if(msg == null) {
				msg = this.exception.getMessage();
			}
			buff.append(msg);
			buff.append("</p></html>");
			msg = buff.toString();
			textExceptionLong.setText(msg);
			textExceptionLong.setCaretPosition(0);
		} catch(Exception e) {
			logger.error(e);
		}
	}

    /**
     * @param e
     */
    private void handleHyperlinkActivated(HyperlinkEvent e) {
        try {
            if(internalError) {
                handleSendError(e.getURL());
            } else if(requiresHtmlRenderer) {
                Utils.launchBrowser(e.getURL());
            }
        } catch(Throwable ex) {
            logger.error(ex);
        }
    }

	/**
	 * @param ex
	 * @return the last part of the name of the class of the
	 * given exception
	 */
	private String getClassName(Throwable ex) {
		String cn = ex.getClass().getName();
		int idx = cn.lastIndexOf(".");
		if(idx < 0) {
			return cn;
		}
		return cn.substring(++idx);
	}
}