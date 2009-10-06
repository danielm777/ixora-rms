package com.ixora.common.ui;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.HyperlinkEvent.EventType;

import com.ixora.common.MessageRepository;
import com.ixora.common.Product;
import com.ixora.common.logging.AppLogger;
import com.ixora.common.logging.AppLoggerFactory;
import com.ixora.common.messages.Msg;
import com.ixora.common.ui.actions.ActionClose;
import com.ixora.common.update.Module;
import com.ixora.common.update.UpdateMgr;
import com.ixora.common.utils.Utils;


/**
 * Dialog for displaying exceptions in a production
 * environment.
 * @author Daniel Moraru
 */
final class ShowAboutDialog extends AppDialog {
	/** Logger */
	private static final AppLogger logger = AppLoggerFactory.getLogger(
				ShowAboutDialog.class);
	/** HTML pane */
    private JEditorPane htmlPane;
	/** Event handler */
    private EventHandler eventHandler;


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
    public ShowAboutDialog() {
        super(VERTICAL);
        initialize();
    }

    /**
     * ShowExceptionDialog constructor comment.
     * @param owner java.awt.Dialog
     */
    public ShowAboutDialog(Dialog owner) {
        super(owner, VERTICAL);
        initialize();
    }

    /**
     * ShowExceptionDialog constructor comment.
     * @param owner java.awt.Frame
     * @param eh
     */
    public ShowAboutDialog(Frame owner) {
        super(owner, VERTICAL);
        initialize();
    }

    /**
     * @see com.ixora.common.ui.AppDialog#getDisplayPanels()
     */
    protected Component[] getDisplayPanels() {
       	return new Component[] {htmlPane};
    }

	/**
	 * Initializes this dialog.
	 */
	private void initialize() {
		this.padding = 0;
		this.eventHandler = new EventHandler();
        setTitle(MessageRepository.get(Msg.COMMON_UI_TEXT_ABOUT));
        setPreferredSize(new Dimension(396, 500));
        htmlPane = UIFactoryMgr.createHtmlPane();
        htmlPane.addHyperlinkListener(this.eventHandler);

        // fill in the table with deployment modules and their versions
        StringBuffer components = new StringBuffer(100);
        Module[] modules = UpdateMgr.getAllRegisteredModules();
        if(modules != null) {
	        components.append("<br>");
	       	for(int i = 0; i < modules.length; i++) {
	       		Module module = modules[i];
	        	components.append(module.getName());
	        	components.append("(");
	        	components.append(module.getVersion());
	        	components.append(")");
	        	if(i != modules.length - 1) {
	        		components.append(", ");
	        	}
	    	}
        }
        StringBuffer jvm = new StringBuffer();
        jvm.append(System.getProperty("java.vendor"))
        	.append(" ")
        	.append(System.getProperty("java.version"));
        StringBuffer os = new StringBuffer();
        os.append(System.getProperty("os.name"))
        	.append(" ")
        	.append(System.getProperty("os.version"));

        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        int year = cal.get(Calendar.YEAR);

        htmlPane.setText(
        	MessageRepository.get(
        		Msg.COMMON_UI_TEXT_ABOUT_DOCUMENT,
				new String[] {
        				Product.getProductInfo().getVersion().toString(), // version
        				String.valueOf(year), // year
        				components.toString(), //  components
        				jvm.toString(), // JVM
        				os.toString()})); // OS
        htmlPane.setBorder(null);
        buildContentPane();
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
    public static void showDialog(Window parent) {
    	try {
	        ShowAboutDialog aShowAboutDialog;
	        if(parent instanceof Dialog) {
	            aShowAboutDialog = new ShowAboutDialog(
	            	(Dialog)parent);
	        } else {
	            if(parent instanceof Frame) {
	            	aShowAboutDialog = new ShowAboutDialog(
	                                               (Frame)parent);
	            } else {
	            	aShowAboutDialog = new ShowAboutDialog();
	            }
	        }
	        aShowAboutDialog.setModal(true);
			UIUtils.centerDialogAndShow(parent, aShowAboutDialog);
    	} catch(Exception e) {
    		logger.error(e);
    	}
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
					logger.error(ex);
				}
			}
		});
		return new JButton[]{close};
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
}