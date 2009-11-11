/*
 * Created on 31-Dec-2003
 */
package com.ixora.common.ui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import com.ixora.common.security.license.LicenseMgr;
import com.ixora.common.utils.Utils;

/**
 * Status bar for the RMS frame.
 * @author Daniel Moraru
 */
public class AppStatusBarDefault extends StatusBar implements AppStatusBar {
	private static final long serialVersionUID = 8443992887942027172L;

	/** Progress status bar dimension */
	protected static final Dimension progressStatusBarDim = new Dimension(150, 16);
	/** Message shown when the first evaluation period has finished */
	private String fLicenseExpiredMessage;
	/** The date when the first evaluation period has expired */
	private Date fLicenseExpiredDate;
	/** Label to display the messages */
	protected JLabel fLabelMessage;
	/** Label to display the state messages */
	protected JLabel fLabelState;
	/** Label to display the warning messages */
	protected JLabel fLabelError;
	/** Progress bar */
	protected JProgressBar fProgressBar;
    /** Icon for the error label */
    protected Icon fLabelErrorIcon;
    /** Clean up timer */
    protected Timer fCleanupTimer;
    private List<Listener> fListeners;
    private boolean fUpdatingStateLabel;
    private EventHandler fEventHandler;    
    
    private final class EventHandler implements PropertyChangeListener, LicenseMgr.Listener {
		/**
		 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
		 */
		public void propertyChange(PropertyChangeEvent evt) {
			if(!fUpdatingStateLabel
					&& evt.getSource() == fLabelState
					&& "text".equals(evt.getPropertyName())) {
				handleTextChangedOnStateLabel();
			}
		}

		/**
		 * @see com.ixora.common.security.license.LicenseMgr.Listener#licenseUpdated()
		 */
		public void licenseUpdated() {
			handleLicenseUpdated();
		}
    }

	/**
	 * Constructor.
     * @param errorIcon
	 */
	public AppStatusBarDefault(Icon errorIcon) {
		super();
		fEventHandler = new EventHandler();
		fListeners = new LinkedList<Listener>();
		// TODO localize
		fLicenseExpiredMessage = "Your evaluation period expired on ";
		fLabelMessage = UIFactoryMgr.createLabel(" ");
		fLabelState = UIFactoryMgr.createLabel(" ");
		fLabelError = UIFactoryMgr.createLabel(" ");
        fLabelErrorIcon = errorIcon;
		setCenterComponent(fLabelMessage);
		fProgressBar = UIFactoryMgr.createProgressBar();
		fProgressBar.setPreferredSize(progressStatusBarDim);
		fProgressBar.setBorderPainted(false);
		addRightComponent(fLabelError);
		addRightComponent(fLabelState);
		addRightComponent(fProgressBar);

        // create cleanup timer
        ActionListener cleanupTask = new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        try {
                            setErrorMessage(null, null);
                        } catch(Exception e) {
                            UIExceptionMgr.exception(e);
                        }
                    }
                });
            }
        };
        fCleanupTimer = new Timer(60000, cleanupTask);
        fCleanupTimer.start();

        fLabelState.addPropertyChangeListener("text", fEventHandler);
        LicenseMgr.addListener(fEventHandler);
	}

	/**
	 * @see com.ixora.common.ui.AppStatusBar#getMessageLabel()
	 */
	public JLabel getMessageLabel() {
		return this.fLabelMessage;
	}

	/**
	 * @see com.ixora.common.ui.AppStatusBar#getProgressBar()
	 */
	public JProgressBar getProgressBar() {
		return this.fProgressBar;
	}

	/**
	 * @see com.ixora.common.ui.AppStatusBar#getStateLabel()
	 */
	public JLabel getStateLabel() {
		return this.fLabelState;
	}

	/**
	 * @see com.ixora.common.ui.AppStatusBar#getErrorLabel()
	 */
	public JLabel getErrorLabel() {
		return this.fLabelError;
	}

	/**
	 * @see com.ixora.common.ui.AppStatusBar#resetCenterComponent()
	 */
	public void resetCenterComponent() {
		setCenterComponent(this.fLabelMessage);
		revalidate();
		repaint();
	}

	/**
	 * @see com.ixora.common.ui.AppStatusBar#setCenterComponent(javax.swing.JComponent)
	 */
	public void setCenterComponent(JComponent c) {
		super.setCenterComponent(c);
		revalidate();
		repaint();
	}

    /**
	 * @see com.ixora.common.ui.AppStatusBar#setStateMessage(java.lang.String)
	 */
    public void setStateMessage(String txt) {
        setStateMessage(null, txt);
    }

    /**
	 * @see com.ixora.common.ui.AppStatusBar#setMessage(java.lang.String)
	 */
    public void setMessage(String txt) {
        setMessage(null, txt);
    }

    /**
	 * @see com.ixora.common.ui.AppStatusBar#setMessage(javax.swing.Icon, java.lang.String)
	 */
    public void setMessage(Icon icon, String txt) {
        fLabelMessage.setIcon(icon);
        if(Utils.isEmptyString(txt)) {
    		fLabelMessage.setText(" ");
        } else {
        	fLabelMessage.setText(txt);
        }
        // it is run on the event dispatching thread
        // a call to paintImmediately is essential
        // for the text to be printed before the synchronous job
        // is finished
        fLabelMessage.paintImmediately(0, 0, fLabelMessage.getWidth(), fLabelMessage.getHeight());
    }

    /**
	 * @see com.ixora.common.ui.AppStatusBar#setStateMessage(javax.swing.Icon, java.lang.String)
	 */
    public void setStateMessage(Icon icon, String txt) {
    	try {
    		fUpdatingStateLabel = true;
	        fLabelState.setIcon(icon);
	        if(Utils.isEmptyString(txt)) {
	        	if(fLicenseExpiredDate == null) {
	        		fLabelState.setText(" ");
	        	} else {
	        		fLabelState.setText(fLicenseExpiredMessage + fLicenseExpiredDate);
	        	}
	        } else {
	            fLabelState.setText(txt);
	        }
	        // it is run on the event dispatching thread
	        // a call to paintImmediately is essential
	        // for the text to be printed before the synchronous job
	        // is finished
	        fLabelState.paintImmediately(0, 0, fLabelState.getWidth(), fLabelState.getHeight());
    	} finally {
    		fUpdatingStateLabel = false;
    	}
    }

    /**
	 * @see com.ixora.common.ui.AppStatusBar#setErrorMessage(java.lang.String, java.lang.Throwable)
	 */
    public void setErrorMessage(String txt, Throwable t) {
        if(txt == null) {
            fLabelError.setIcon(null);
            fLabelError.setText(" ");

        } else {
            this.fCleanupTimer.restart();
            fLabelError.setIcon(fLabelErrorIcon);
            String toShow = txt.length() > 50 ? txt.substring(0, 50) + "..." : txt;
            if(t != null) {
                String tText = t.getLocalizedMessage() != null ? t.getLocalizedMessage() : t.toString();
                tText = tText.length() > 60 ? tText.substring(0, 60) + "..." : tText;
                toShow = toShow + " Error: " + tText;
            }
            fLabelError.setText(toShow);
        }
        // it is run on the event dispatching thread
        // a call to paintImmediately is essential
        // for the text to be printed before the synchronous job
        // is finished
        fLabelError.paintImmediately(0, 0, fLabelError.getWidth(), fLabelError.getHeight());
    }

    /**
	 * @see com.ixora.common.ui.AppStatusBar#insertStatusBarComponent(javax.swing.JComponent, int, int)
	 */
    public void insertStatusBarComponent(JComponent comp, int component, int position) {
    	int idx = component;
    	if(position == POSITION_AFTER) {
    		idx++;
    	}
    	addRightComponent(comp, idx);
    }

    /**
	 * @see com.ixora.common.ui.AppStatusBar#removeStatusBarComponent(javax.swing.JComponent)
	 */
    public void removeStatusBarComponent(JComponent comp) {
    	removeRightComponent(comp);
    }

	/**
	 * @see com.ixora.common.ui.AppStatusBar#checkLicense()
	 */
	public void checkLicense() {
		Date exp = LicenseMgr.isFirstEvaluationFinished();
		fLicenseExpiredDate = exp;
		setStateMessage(null);
	}

	/**
	 *
	 */
	private void handleLicenseUpdated() {
		try {
			checkLicense();
		} catch(Exception e) {
			UIExceptionMgr.userException(e);
		}
	}

	/**
	 *
	 */
	private void handleTextChangedOnStateLabel() {
		if(fUpdatingStateLabel) {
			return;
		}
		try {
			if(Utils.isEmptyString(fLabelState.getText())) {
				setMessage(null);
			}
		} catch(Exception e) {
			UIExceptionMgr.userException(e);
		}
	}

	/**
	 * @see com.ixora.common.ui.AppStatusBar#addListener(com.ixora.common.ui.AppStatusBar.Listener)
	 */
	public void addListener(Listener list) {
		if(!fListeners.contains(list)) {
			fListeners.add(list);
		}
	}

	/**
	 * @see com.ixora.common.ui.AppStatusBar#removeListener(com.ixora.common.ui.AppStatusBar.Listener)
	 */
	public void removeListener(Listener list) {
		fListeners.remove(list);
	}
}
