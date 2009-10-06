/**
 * 13-Aug-2005
 */
package com.ixora.rms.ui.artefacts.dataview.wizard;



import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
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
import com.ixora.rms.dataengine.definitions.FunctionDef;
import com.ixora.rms.dataengine.definitions.ParamDef;
import com.ixora.rms.dataengine.definitions.ScriptFunctionDef;

/**
 * @author Daniel Moraru
 */
public final class FunctionEditorDialog extends AppDialog {
	// TODO localize
	private static final String LABEL_PARAMETERS = "Parameters";
	private static final String LABEL_CODE = "Code";
	private FormPanel fForm;
	private JPanel fPanel;
	private JTextField fTextFieldParameters;
	private JTextArea fCode;
	private FunctionDef fResult;

	/**
	 * @param parent
	 * @param params
	 */
	public FunctionEditorDialog(Frame parent, String[] params)  {
		super(parent, VERTICAL);
		init(parent, params, null);
	}

	/**
	 * @param parent
	 * @param def
	 * @param cb
	 */
	public FunctionEditorDialog(Frame parent, FunctionDef def)  {
		super(parent, VERTICAL);
		init(parent, def.getParameters().toArray(new String[0]), def);
	}

	/**
	 * @param parent
	 * @param params
	 * @param cb
	 */
	public FunctionEditorDialog(Dialog parent, String[] params)  {
		super(parent, VERTICAL);
		init(parent, params, null);
	}

	/**
	 * @param parent
	 * @param def
	 * @param cb
	 */
	public FunctionEditorDialog(Dialog parent, FunctionDef def)  {
		super(parent, VERTICAL);
		List<ParamDef> params = def.getParameters();
		String[] paramsArr = new String[params.size()];
		int i = 0;
		for(ParamDef param : params) {
			paramsArr[i] = param.getID();
			++i;
		}
		init(parent, paramsArr, def);
	}

	/**
	 * @param parent
	 * @param params
	 * @param def
	 */
	void init(Window parent, String[] params, FunctionDef def){
		setTitle("Function Editor");
		setSize(500, 600);
		setModal(true);
		fPanel = new JPanel(new BorderLayout());
		fForm = new FormPanel(FormPanel.VERTICAL1, SwingConstants.TOP);

		fTextFieldParameters = UIFactoryMgr.createTextField();
		Dimension dim = new Dimension(450, 70);
		Font font = new Font("Monospaced", Font.PLAIN, 12);
		fCode = UIFactoryMgr.createTextArea();
		fCode.setFont(font);
		JScrollPane spCode = UIFactoryMgr.createScrollPane();
		spCode.setViewportView(fCode);
		spCode.setPreferredSize(dim);
		fForm.addPairs(
				new String[] {
						LABEL_PARAMETERS,
						LABEL_CODE,
				},
				new Component[] {
						fTextFieldParameters,
						spCode,
				});
		fPanel.add(fForm, BorderLayout.NORTH);

		fTextFieldParameters.setText(getTextFromParams(params));
		initValues(def);
		buildContentPane();
	}

	/**
	 * @param def
	 */
	private void initValues(FunctionDef def) {
		if(def == null) {
			fCode.setText("// BUILD LOGIC HERE");
		} else {
			fCode.setText(def.getCode());
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
			String code = fCode.getText();
			if(Utils.isEmptyString(code)) {
				throw new InvalidFormData(LABEL_CODE);
			}
			List<ParamDef> paramsLst = new ArrayList<ParamDef>(params.length);
			for(String param : params) {
				paramsLst.add(new ParamDef(param));
			}
			fResult = new ScriptFunctionDef(paramsLst, code);
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

	/**
	 * @return
	 */
	public FunctionDef getResult() {
		return fResult;
	}
}
