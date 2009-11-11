package com.ixora.common.ui;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JProgressBar;

/**
 * @author Daniel Moraru
 */
public interface AppStatusBar {
	public static final int COMPONENT_ERROR = 0;
	public static final int COMPONENT_STATE = 1;
	public static final int COMPONENT_PROGRESS = 2;
	public static final int POSITION_AFTER = 0;
	public static final int POSITION_BEFORE = 1;

	public interface Listener {
		/**
		 * @param newMessage
		 */
		void messageChanged(String newMessage);
		/**
		 * @param newMessage
		 */
		void stateMessageChanged(String newMessage);
		/**
		 * @param newMessage
		 * @param error
		 */
		void errorMessageChanged(String newMessage, Throwable error);
	}
	/**
	 * @param list
	 */
	void addListener(Listener list);
	/**
	 * @param list
	 */
	void removeListener(Listener list);
	/**
	 * @return the message label
	 */
	JLabel getMessageLabel();

	/**
	 * @return the progress bar
	 */
	JProgressBar getProgressBar();

	/**
	 * @return the state label
	 */
	JLabel getStateLabel();

	/**
	 * @return the warning label
	 */
	JLabel getErrorLabel();

	/**
	 * Resets the center component.
	 */
	void resetCenterComponent();

	/**
	 * @see com.ixora.common.ui.StatusBar#setCenterComponent(javax.swing.JComponent)
	 */
	void setCenterComponent(JComponent c);

	/**
	 * Sets the state message
	 */
	void setStateMessage(String txt);

	/**
	 * Sets the message displayed in the main component of the status bar.
	 */
	void setMessage(String txt);

	/**
	 * Sets the message displayed in the main component of the status bar.
	 * @param icon
	 * @param txt
	 */
	void setMessage(Icon icon, String txt);

	/**
	 * Sets the state message
	 * @param icon
	 * @param txt
	 */
	void setStateMessage(Icon icon, String txt);

	/**
	 * Sets the error message
	 */
	void setErrorMessage(String txt, Throwable t);

	/**
	 * Inserts the given component at the given position relative to one of
	 * the existing components.
	 * @param comp
	 * @param component
	 * @param position
	 */
	void insertStatusBarComponent(JComponent comp, int component, int position);

	/**
	 * Removes the given status bar component (if it was previously added using
	 * <code>inserStatusBarComponent()</code> method).
	 * @param comp
	 */
	void removeStatusBarComponent(JComponent comp);

	/**
	 * Checks the license and displays warning messages if not ok.
	 */
	void checkLicense();
}