/**
 * 13-Aug-2005
 */
package com.ixora.rms.ui.reactions;



import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import com.ixora.common.ui.AppDialog;
import com.ixora.common.ui.UIExceptionMgr;
import com.ixora.common.ui.UIFactoryMgr;
import com.ixora.common.ui.actions.ActionClose;
import com.ixora.common.ui.actions.ActionOk;
import com.ixora.common.ui.exception.InvalidFormData;
import com.ixora.common.ui.forms.FormPanel;
import com.ixora.common.utils.Utils;
import com.ixora.rms.dataengine.definitions.ReactionDef;

/**
 * @author Daniel Moraru
 */
public final class ReactionEditorDialog extends AppDialog {

	public interface Callback {
		/**
		 * @param def
		 */
		void handleReactionDef(ReactionDef def) throws Exception;
	}

	// TODO localize
	private static final String LABEL_PARAMETERS = "Parameters";
	private static final String LABEL_SEVERITY = "Severity";
	private static final String LABEL_ARMCODE = "Arm Code";
	private static final String LABEL_FIRECODE = "Fire Code";
	private static final String LABEL_DISARMCODE = "Disarm Code";
	private static final String LABEL_DELIVERYCODE = "Delivery Code";
	private static final String LABEL_MESSAGECODE = "Message Code";
	private static final String LABEL_ADVISECODE = "Advise Code";

	private FormPanel fForm;
	private JPanel fPanel;

	private JTextField fTextFieldParameters;
	private JComboBox fComboBoxSeverity;
	private JTextArea fArmCode;
	private JTextArea fFireCode;
	private JTextArea fDisarmCode;
	private JTextArea fDeliveryCode;
	private JTextArea fMessageCode;
	private JTextArea fAdviseCode;

	private Callback fCallback;

	/**
	 * @param parent
	 * @param params
	 * @param cb
	 */
	public ReactionEditorDialog(Frame parent, String[] params, Callback cb)  {
		super(parent, VERTICAL);
		init(parent, params, cb, null);
	}

	/**
	 * @param parent
	 * @param def
	 * @param cb
	 */
	public ReactionEditorDialog(Frame parent, ReactionDef def, Callback cb)  {
		super(parent, VERTICAL);
		init(parent, def.getParameters().toArray(new String[0]), cb, def);
	}

	/**
	 * @param parent
	 * @param params
	 * @param cb
	 */
	public ReactionEditorDialog(Dialog parent, String[] params, Callback cb)  {
		super(parent, VERTICAL);
		init(parent, params, cb, null);
	}

	/**
	 * @param parent
	 * @param def
	 * @param cb
	 */
	public ReactionEditorDialog(Dialog parent, ReactionDef def, Callback cb)  {
		super(parent, VERTICAL);
		init(parent, def.getParameters().toArray(new String[0]), cb, def);
	}

	/**
	 * @param parent
	 * @param params
	 * @param cb
	 * @param def
	 */
	void init(Window parent, String[] params, Callback cb, ReactionDef def){
		setTitle("Reaction Editor");
		setSize(500, 600);
		setModal(true);
		fCallback = cb;
		fPanel = new JPanel(new BorderLayout());
		fForm = new FormPanel(FormPanel.VERTICAL1, SwingConstants.TOP);

		fTextFieldParameters = UIFactoryMgr.createTextField();
		fComboBoxSeverity = UIFactoryMgr.createComboBox();
		fComboBoxSeverity.setModel(new DefaultComboBoxModel(new String[]{
				"CRITICAL", // TODO localize
				"HIGH",
				"LOW"
		}));
		Dimension dim = new Dimension(450, 70);
		Font font = new Font("Monospaced", Font.PLAIN, 12);
		fArmCode = UIFactoryMgr.createTextArea();
		fArmCode.setFont(font);
		JScrollPane spArm = UIFactoryMgr.createScrollPane();
		spArm.setViewportView(fArmCode);
		spArm.setPreferredSize(dim);
		fFireCode = UIFactoryMgr.createTextArea();
		fFireCode.setFont(font);
		JScrollPane spFire = UIFactoryMgr.createScrollPane();
		spFire.setViewportView(fFireCode);
		spFire.setPreferredSize(dim);
		fDisarmCode = UIFactoryMgr.createTextArea();
		fDisarmCode.setFont(font);
		JScrollPane spDisarm = UIFactoryMgr.createScrollPane();
		spDisarm.setViewportView(fDisarmCode);
		spDisarm.setPreferredSize(dim);
		fDeliveryCode = UIFactoryMgr.createTextArea();
		fDeliveryCode.setFont(font);
		JScrollPane spDelivery = UIFactoryMgr.createScrollPane();
		spDelivery.setViewportView(fDeliveryCode);
		spDelivery.setPreferredSize(dim);
		fMessageCode = UIFactoryMgr.createTextArea();
		fMessageCode.setFont(font);
		JScrollPane spMessage = UIFactoryMgr.createScrollPane();
		spMessage.setViewportView(fMessageCode);
		spMessage.setPreferredSize(dim);
		fAdviseCode = UIFactoryMgr.createTextArea();
		fAdviseCode.setFont(font);
		JScrollPane spAdvise = UIFactoryMgr.createScrollPane();
		spAdvise.setViewportView(fAdviseCode);
		spAdvise.setPreferredSize(dim);
		fForm.addPairs(
				new String[] {
						LABEL_PARAMETERS,
						LABEL_SEVERITY,
						LABEL_ARMCODE,
						LABEL_FIRECODE,
                        LABEL_DISARMCODE,
                        LABEL_DELIVERYCODE,
                        LABEL_MESSAGECODE,
                        LABEL_ADVISECODE
				},
				new Component[] {
						fTextFieldParameters,
						fComboBoxSeverity,
						spArm,
                        spFire,
                        spDisarm,
                        spDelivery,
                        spMessage,
                        spAdvise
				});
		fPanel.add(fForm, BorderLayout.NORTH);

		fTextFieldParameters.setText(getTextFromParams(params));
		initValues(def);
		buildContentPane();
	}

	/**
	 * @param def
	 */
	private void initValues(ReactionDef def) {
		if(def == null) {
			fArmCode.setText("return false; // BUILD LOGIC HERE");
			fDisarmCode.setText("return env.getSecondsSinceLastArmed() > 20;");
			fFireCode.setText("return env.getSecondsSinceArmed() > 60;");
			fDeliveryCode.setText("env.email();");
			fMessageCode.setText("return \"Message\"; // BUILD MESSAGE HERE");
		} else {
			fArmCode.setText(def.getArmCode());
			fDisarmCode.setText(def.getDisarmCode());
			fFireCode.setText(def.getFireCode());
			fDeliveryCode.setText(def.getDeliveryCode());
			fMessageCode.setText(def.getMessageCode());
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
	protected JButton[] getButtons() {
		return new JButton[] {
				new JButton(
					new ActionOk() {
						public void actionPerformed(ActionEvent e) {
							handleOk();
							dispose();
						}
					}
				),
				new JButton(
					new ActionClose() {
						public void actionPerformed(ActionEvent e) {
							dispose();
						}
					}
				)};
	}

	/**
	 *
	 */
	private void handleOk() {
		try {
			// check data
			String[] params = getParamsFromText(fTextFieldParameters.getText().trim());
			if(Utils.isEmptyArray(params)) {
				throw new InvalidFormData(LABEL_PARAMETERS);
			}
			String severity = (String)fComboBoxSeverity.getSelectedItem();
			String codeArm = fArmCode.getText();
			if(Utils.isEmptyString(codeArm)) {
				throw new InvalidFormData(LABEL_ARMCODE);
			}
			String codeDisarm = fDisarmCode.getText().trim();
			if(Utils.isEmptyString(codeDisarm)) {
				throw new InvalidFormData(LABEL_DISARMCODE);
			}
			String codeFire = fFireCode.getText().trim();
			if(Utils.isEmptyString(codeFire)) {
				throw new InvalidFormData(LABEL_FIRECODE);
			}
			String codeMessage = fMessageCode.getText().trim();
			String codeDelivery = fDeliveryCode.getText().trim();
			// if message specified
			if(!Utils.isEmptyString(codeMessage)) {
				// delivery method required
				if(Utils.isEmptyString(codeDelivery)) {
					throw new InvalidFormData(LABEL_DELIVERYCODE);
				}
			}
			String codeAdvise = fAdviseCode.getText().trim();
			// is advice required (only if no message specified
			// and viceversa
			if(Utils.isEmptyString(codeAdvise) && Utils.isEmptyString(codeMessage)) {
				// TODO localize
				throw new InvalidFormData("Message Code or Advise Code is required");
			}

			fCallback.handleReactionDef(new ReactionDef(params, severity, codeArm, codeDisarm,
							codeFire, codeDelivery, codeMessage, codeAdvise));
		} catch(Exception e) {
			UIExceptionMgr.userException(e);
		}
	}

	/**
	 * @param txt
	 * @return
	 */
	private String[] getParamsFromText(String txt) {
		if(Utils.isEmptyString(txt)) {
			return null;
		}
		String[] ret = txt.split(",");
		for(int i = 0; i < ret.length; i++) {
			ret[i] = ret[i].trim();
		}
		return ret;
	}

	/**
	 * @param params
	 * @return
	 */
	private String getTextFromParams(String[] params) {
		StringBuffer ret = new StringBuffer();
		if(!Utils.isEmptyArray(params)) {
			for(int i = 0; i < params.length; i++) {
				ret.append(params[i].trim());
				if(i < params.length - 1) {
					ret.append(",");
				}
			}
		}
		return ret.toString();
	}

}
