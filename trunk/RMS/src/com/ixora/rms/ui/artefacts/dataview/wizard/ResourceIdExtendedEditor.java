/**
 * 01-Jan-2006
 */
package com.ixora.rms.ui.artefacts.dataview.wizard;


import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Window;
import java.util.List;

import javax.swing.SwingUtilities;

import com.ixora.rms.ResourceId;
import com.ixora.common.exception.AppRuntimeException;
import com.ixora.common.typedproperties.PropertyEntry;
import com.ixora.common.typedproperties.ui.ExtendedEditorAbstract;
import com.ixora.common.ui.UIUtils;
import com.ixora.common.utils.Utils;
import com.ixora.rms.client.model.SessionModel;
import com.ixora.rms.dataengine.definitions.FunctionDef;
import com.ixora.rms.ui.ResourceSelectorDialog;
import com.ixora.rms.ui.SessionTreeExplorer;

/**
 * @author Daniel Moraru
 */
public final class ResourceIdExtendedEditor extends ExtendedEditorAbstract {
	private ResourceId fResult;
	private SessionModel fModels;
	private SessionTreeExplorer fExplorer;
	private ResourceId fContext;

	/**
	 *
	 */
	public ResourceIdExtendedEditor() {
		super();
	}

	/**
	 * @see com.ixora.common.typedproperties.ui.ExtendedEditor#launch(java.awt.Component, com.ixora.common.typedproperties.PropertyEntry)
	 */
	public void launch(Component owner, PropertyEntry pe) {
		Object obj = pe.getValue();
		FunctionDef def = null;
		ResourceSelectorDialog dlg = null;
		Window parentWindow = SwingUtilities.getWindowAncestor(owner);
		if(parentWindow instanceof Frame) {
			dlg = new ResourceSelectorDialog((Frame)parentWindow, ResourceSelectorDialog.SELECT_COUNTER, fExplorer, fModels, fContext, true);
		} else if(parentWindow instanceof Dialog) {
			dlg = new ResourceSelectorDialog((Dialog)parentWindow, ResourceSelectorDialog.SELECT_COUNTER, fExplorer, fModels, fContext, true);
		}
		if(dlg == null) {
			throw new AppRuntimeException("Failed to create editor window");
		}
		UIUtils.centerDialogAndShow(parentWindow, dlg);
		List<ResourceId> result = dlg.getResult();
		if(!Utils.isEmptyCollection(result) && result.size() >= 1) {
			fResult = result.get(0);
		}
		if(fResult == null) {
			fireEditingCanceled();
		} else {
			fireEditingStopped(fResult);
		}
	}

	/**
	 * @see com.ixora.common.typedproperties.ui.ExtendedEditor#close()
	 */
	public void close() {
	}

	/**
	 * @param context
	 * @param model
	 * @param explorer
	 */
	public void prepare(ResourceId context, SessionModel model, SessionTreeExplorer explorer) {
		fContext = context;
		fModels = model;
		fExplorer = explorer;
	}
}
