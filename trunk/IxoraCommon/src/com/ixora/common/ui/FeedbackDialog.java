/**
 * 08-Mar-2006
 */
package com.ixora.common.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URL;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;

import com.ixora.common.net.NetUtils;
import com.ixora.common.ui.actions.ActionCancel;
import com.ixora.common.ui.actions.ActionOk;
import com.ixora.common.ui.forms.FormPanel;
import com.ixora.common.ui.jobs.UIWorkerJobDefault;
import com.ixora.common.ui.preferences.PreferencesConfiguration;
import com.ixora.common.ui.preferences.PreferencesConfigurationConstants;
import com.ixora.common.utils.Utils;

/**
 * @author Daniel Moraru
 */
public class FeedbackDialog extends AppDialog {
	private static final long serialVersionUID = 8166255439999644526L;
	// TODO localize
	protected static final String LABEL_EMAIL = "Email";
	protected static final String LABEL_FEEDBACK = "Comments";

	protected AppViewContainer fAppViewContainer;
	protected URL fURL;
	protected FormPanel fForm;
	protected JTextField fTextFieldEmail;
	protected JTextArea fTextAreaFeedback;
	protected JPanel fPanel;

	/**
	 * @param parent
	 * @param url
	 */
	public FeedbackDialog(AppViewContainer vc, URL url) {
		super(vc.getAppFrame(), VERTICAL);
		setModal(true);
		setTitle("Feedback Form"); // TODO localize
		if(url == null) {
			throw new IllegalArgumentException("Null URL");
		}
		fAppViewContainer = vc;
		fURL = url;
		fForm = new FormPanel(FormPanel.VERTICAL2, SwingConstants.TOP);
		fTextFieldEmail = UIFactoryMgr.createTextField();
		fTextAreaFeedback = UIFactoryMgr.createTextArea();
		JScrollPane sp = UIFactoryMgr.createScrollPane();
		sp.setViewportView(fTextAreaFeedback);
		sp.setPreferredSize(new Dimension(300, 200));
		fForm.addPairs(
				new String[] {
					LABEL_EMAIL,
					LABEL_FEEDBACK,
				},
				new Component[] {
					fTextFieldEmail,
					sp,
				});
		fPanel = new JPanel(new BorderLayout());
		fPanel.add(fForm, BorderLayout.NORTH);

		buildContentPane();

		// check for email
		String email = PreferencesConfiguration.get().getString(PreferencesConfigurationConstants.USER_EMAIL);
		if(!Utils.isEmptyString(email)) {
			fTextFieldEmail.setText(email);
			UIUtils.setFocus(fTextAreaFeedback);
		}
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
	@SuppressWarnings("serial")
	protected JButton[] getButtons() {
		return new JButton[]{
				UIFactoryMgr.createButton(new ActionOk(){
					public void actionPerformed(ActionEvent e) {
						handleOk();
					}
				}),
				UIFactoryMgr.createButton(new ActionCancel(){
					public void actionPerformed(ActionEvent e) {
						handleCancel();
					}
				})
		};
	}

	/**
	 *
	 */
	private void handleOk() {
		try {
			final String email = fTextFieldEmail.getText().trim();
			final String comment = fTextAreaFeedback.getText().trim();
			if(!Utils.isEmptyString(comment)){
				fAppViewContainer.getAppWorker().runJob(new UIWorkerJobDefault(
						this,
						Cursor.WAIT_CURSOR,
						"Sending feedback...") { // TODO localize
					public void work() throws Throwable {
						postData(fURL, email, comment);
					}
					public void finished(Throwable ex) throws Throwable {
						dispose();
					}
				});

				// save email
				if(!Utils.isEmptyString(email)) {
					PreferencesConfiguration.get().setString(
							PreferencesConfigurationConstants.USER_EMAIL,
							email);
				}
			} else {
				// empty comments
				// if empty email remove it
				if(Utils.isEmptyString(email)) {
					PreferencesConfiguration.get().setString(
							PreferencesConfigurationConstants.USER_EMAIL,
							"");
				}
				dispose();
			}
		} catch(Exception e) {
			UIExceptionMgr.userException(e);
		}
	}

	/**
	 *
	 */
	private void handleCancel() {
		try {
			dispose();
		} catch(Exception e) {
			UIExceptionMgr.userException(e);
		}
	}

	/**
	 * Subclasses can override this method to post data. By default an HTTP post
	 * request is sent to the URL <code>url</code> with two parameters named <code>email</code> and <code>feedback</code>.
	 * @param url
	 * @param email
	 * @param comment
	 * @throws HttpException
	 * @throws IOException
	 */
	protected void postData(URL url, String email, String comment)
			throws HttpException, IOException {
		NetUtils.postHttpForm(fURL, new NameValuePair[]{
				new NameValuePair("email", email),
				new NameValuePair("feedback", comment),
		}, null);
	}
}
