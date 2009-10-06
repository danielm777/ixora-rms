/**
 * 01-Jan-2006
 */
package com.ixora.rms.ui.artefacts.dataview.wizard;


import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Window;
import java.io.Serializable;

import javax.swing.SwingUtilities;

import com.ixora.common.exception.AppRuntimeException;
import com.ixora.common.typedproperties.PropertyEntry;
import com.ixora.common.typedproperties.ui.ExtendedEditorAbstract;
import com.ixora.common.ui.UIUtils;
import com.ixora.rms.dataengine.definitions.FunctionDef;

/**
 * @author Daniel Moraru
 */
public final class FunctionExtendedEditor extends ExtendedEditorAbstract {
	private FunctionDef fResult;
	private String[] fParams;

	/** Wrapper of a function def to give it a meaningful string representation */
	public static class FunctionDefWrapper implements Serializable {
		private FunctionDef fDef;

		public FunctionDefWrapper(FunctionDef def) {
			fDef = def;
		}
		public String toString() {
			return fDef.getCode();
		}
		public FunctionDef getFunctionDef() {
			return fDef;
		}
	}

	/**
	 *
	 */
	public FunctionExtendedEditor() {
		super();
	}

	/**
	 * @see com.ixora.common.typedproperties.ui.ExtendedEditor#launch(java.awt.Component, com.ixora.common.typedproperties.PropertyEntry)
	 */
	public void launch(Component owner, PropertyEntry pe) {
		Object obj = pe.getValue();
		FunctionDef def = null;
		FunctionEditorDialog dlg = null;
		Window parentWindow = SwingUtilities.getWindowAncestor(owner);
		if(obj != null) {
			def = ((FunctionDefWrapper)obj).fDef;
			if(parentWindow instanceof Frame) {
				dlg = new FunctionEditorDialog((Frame)parentWindow, def);
			} else if(parentWindow instanceof Dialog) {
				dlg = new FunctionEditorDialog((Dialog)parentWindow, def);
			}
		} else {
			if(parentWindow instanceof Frame) {
				dlg = new FunctionEditorDialog((Frame)parentWindow, fParams);
			} else if(parentWindow instanceof Dialog) {
				dlg = new FunctionEditorDialog((Dialog)parentWindow, fParams);
			}
		}
		if(dlg == null) {
			throw new AppRuntimeException("Failed to create editor window");
		}
		UIUtils.centerDialogAndShow(parentWindow, dlg);
		fResult = dlg.getResult();
		if(fResult == null) {
			fireEditingCanceled();
		} else {
			fireEditingStopped(new FunctionDefWrapper(fResult));
		}
	}

	/**
	 * @see com.ixora.common.typedproperties.ui.ExtendedEditor#close()
	 */
	public void close() {
	}

	/**
	 * @param params
	 */
	public void prepare(String[] params) {
		this.fParams = params;
	}
}
