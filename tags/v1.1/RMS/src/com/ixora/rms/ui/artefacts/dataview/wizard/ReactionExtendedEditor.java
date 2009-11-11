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
import com.ixora.rms.dataengine.definitions.ReactionDef;
import com.ixora.rms.ui.reactions.ReactionEditorDialog;

/**
 * @author Daniel Moraru
 */
public final class ReactionExtendedEditor extends ExtendedEditorAbstract<ReactionExtendedEditor.ReactionDefWrapper> {
	private ReactionDef fResult;
	private EventHandler fEventHandler;
	private String[] fParams;

	private class EventHandler implements ReactionEditorDialog.Callback {
		/**
		 * @see com.ixora.rms.ui.reactions.ReactionEditorDialog.Callback#handleReactionDef(com.ixora.rms.dataengine.definitions.ReactionDef)
		 */
		public void handleReactionDef(ReactionDef def) throws Exception {
			fResult = def;
			fireEditingStopped(new ReactionDefWrapper(def));
		}
	}

	/** Wrapper of a reaction def to give it a meaningful string representation */
	public static class ReactionDefWrapper implements Serializable {
		private static final long serialVersionUID = -6237884770292082176L;
		private ReactionDef fDef;

		public ReactionDefWrapper(ReactionDef def) {
			fDef = def;
		}
		public String toString() {
			return fDef.getArmCode();
		}
		public ReactionDef getReactionDef() {
			return fDef;
		}
	}

	/**
	 *
	 */
	public ReactionExtendedEditor() {
		super();
		fEventHandler = new EventHandler();
	}

	/**
	 * @see com.ixora.common.typedproperties.ui.ExtendedEditor#launch(java.awt.Component, com.ixora.common.typedproperties.PropertyEntry)
	 */
	public void launch(Component owner, PropertyEntry<ReactionDefWrapper> pe) {
		ReactionDefWrapper obj = pe.getValue();
		ReactionDef def = null;
		ReactionEditorDialog dlg = null;
		Window parentWindow = SwingUtilities.getWindowAncestor(owner);
		if(obj != null) {
			def = obj.fDef;
			if(parentWindow instanceof Frame) {
				dlg = new ReactionEditorDialog(
						(Frame)parentWindow,
						def, fEventHandler);
			} else if(parentWindow instanceof Dialog) {
				dlg = new ReactionEditorDialog(
						(Dialog)parentWindow,
						def, fEventHandler);
			}
		} else {
			if(parentWindow instanceof Frame) {
				dlg = new ReactionEditorDialog(
						(Frame)parentWindow,
						fParams, fEventHandler);
			} else if(parentWindow instanceof Dialog) {
				dlg = new ReactionEditorDialog(
						(Dialog)parentWindow,
						fParams, fEventHandler);
			}
		}

		if(dlg == null) {
			throw new AppRuntimeException("Failed to create editor window");
		}
		UIUtils.centerDialogAndShow(parentWindow, dlg);
		if(fResult == null) {
			fireEditingCanceled();
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
