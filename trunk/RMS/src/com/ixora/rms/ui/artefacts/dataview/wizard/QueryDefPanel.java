/**
 * 01-Jan-2006
 */
package com.ixora.rms.ui.artefacts.dataview.wizard;

import java.util.LinkedList;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JPanel;

import com.ixora.common.typedproperties.PropertyEntry;
import com.ixora.common.typedproperties.TypedProperties;
import com.ixora.common.typedproperties.exception.VetoException;
import com.ixora.common.typedproperties.ui.ExtendedEditor;
import com.ixora.common.typedproperties.ui.PropertyTableCellEditor;
import com.ixora.common.typedproperties.ui.list.PropertyListTableModel;
import com.ixora.common.typedproperties.ui.list.TypedPropertiesListEditor;
import com.ixora.common.ui.UIUtils;
import com.ixora.common.utils.Utils;
import com.ixora.rms.client.model.SessionModel;
import com.ixora.rms.dataengine.definitions.FunctionDef;
import com.ixora.rms.dataengine.definitions.ReactionDef;
import com.ixora.rms.dataengine.definitions.ResourceDef;
import com.ixora.rms.exception.RMSException;
import com.ixora.rms.ui.artefacts.dataview.wizard.messages.Msg;
import com.ixora.rms.ui.reactions.ReactionEditorDialog;

/**
 * @author Daniel Moraru
 */
public abstract class QueryDefPanel extends JPanel {
	protected static final String LABEL_FUNCTIONS = "Functions";
	protected static final String LABEL_REACTIONS = "Reactions";

	protected SessionModel fSessionModel;
	protected TypedPropertiesListEditor fEditorFunction;
	protected TypedProperties fPrototypeFunction;
	protected TypedPropertiesListEditor fEditorReaction;
	protected TypedProperties fPrototypeReaction;
	protected JDialog fParent;
	protected int fFunctionCounter;
	private EventHandler fEventHandler;

	private class EventHandler implements
		PropertyListTableModel.Listener,
		PropertyTableCellEditor.ExtendedEditorListener {
		/**
		 * @throws RMSException
		 * @throws VetoException
		 * @see com.ixora.common.typedproperties.ui.list.PropertyListTableModel.Listener#aboutToAddNewProperties(com.ixora.common.typedproperties.ui.list.PropertyListTableModel, com.ixora.common.typedproperties.TypedProperties)
		 */
		public void aboutToAddNewProperties(PropertyListTableModel model, TypedProperties prop) throws RMSException, VetoException {
			if(model == fEditorFunction.getModel()) {
				handleAboutToAddFunction(prop);
				return;
			}
			if(model == fEditorReaction.getModel()) {
				handleAboutToAddReaction(prop);
				return;
			}
		}
		/**
		 * @see com.ixora.common.typedproperties.ui.list.PropertyListTableModel.Listener#deletedProperties(com.ixora.common.typedproperties.ui.list.PropertyListTableModel, com.ixora.common.typedproperties.TypedProperties)
		 */
		public void deletedProperties(PropertyListTableModel model, TypedProperties prop) {
		}
		/**
		 * @see com.ixora.common.typedproperties.ui.PropertyEntryCellEditorExtended.ExtendedEditorListener#aboutToLaunch(com.ixora.common.typedproperties.ui.ExtendedEditor, com.ixora.common.typedproperties.PropertyEntry)
		 */
		public void aboutToLaunch(ExtendedEditor editor, PropertyEntry pe) throws Exception {
			handleAboutToLaunchExtendedEditor(editor, pe);
		}
	}

	/**
	 * @param parent
	 * @param model
	 */
	protected QueryDefPanel(JDialog parent, SessionModel model) {
		super();
		fEventHandler = new EventHandler();
		fParent = parent;
		fSessionModel = model;
		fPrototypeFunction = new TypedProperties();
		fPrototypeFunction.setProperty(Msg.ID, TypedProperties.TYPE_STRING, true, true);
		fPrototypeFunction.setProperty(Msg.INAME, TypedProperties.TYPE_STRING, true, false);
		fPrototypeFunction.setProperty(Msg.NAME, TypedProperties.TYPE_STRING, true, false);
		fPrototypeFunction.setProperty(Msg.DESCRIPTION, TypedProperties.TYPE_STRING, true, false);
		fPrototypeFunction.setProperty(Msg.MAX, TypedProperties.TYPE_FLOAT, true, false);
		fPrototypeFunction.setProperty(Msg.MIN, TypedProperties.TYPE_FLOAT, true, false);
		fPrototypeFunction.setProperty(Msg.CODE, TypedProperties.TYPE_SERIALIZABLE,
				true, true, null, FunctionExtendedEditor.class.getName());

		fEditorFunction = new TypedPropertiesListEditor(
				fPrototypeFunction, null, null,
				TypedPropertiesListEditor.BUTTON_NEW_DELETE);

		fPrototypeReaction = new TypedProperties();
		fPrototypeReaction.setProperty(Msg.REACTION, TypedProperties.TYPE_SERIALIZABLE,
				true, true, null, ReactionExtendedEditor.class.getName());

		fEditorReaction = new TypedPropertiesListEditor(
				fPrototypeReaction, null, null,
				TypedPropertiesListEditor.BUTTON_NEW_DELETE);

		fEditorFunction.getTable().getColumnModel().getColumn(0).setPreferredWidth(25);
		fEditorFunction.getTable().getColumnModel().getColumn(1).setPreferredWidth(120);

		fEditorReaction.getCellEditor().addExtendedEditorListener(fEventHandler);
		fEditorFunction.getCellEditor().addExtendedEditorListener(fEventHandler);

		fEditorFunction.getModel().addListener(fEventHandler);
		fEditorReaction.getModel().addListener(fEventHandler);
	}

	/**
	 * @return
	 * @throws RMSException
	 */
	public List<FunctionDef> getFunctions() throws RMSException {
		fEditorFunction.stopEditing();
		List<FunctionDef> ret = new LinkedList<FunctionDef>();
		List<TypedProperties> props = fEditorFunction.getModel().getAllProperties();
		if(!Utils.isEmptyCollection(props)) {
			for(TypedProperties prop : props) {
				Object obj = prop.getObject(Msg.CODE);
				if(obj == null) {
					throw new RMSException("Code is missing for function " + prop.getString(Msg.ID) + ".");
				}
				FunctionDef fdef = ((FunctionExtendedEditor.FunctionDefWrapper)obj).getFunctionDef();
				// get styles
				fdef.setID(prop.getString(Msg.ID));
				obj = prop.getObject(Msg.INAME);
				if(obj != null) {
					fdef.setIName(obj.toString());
				}
				obj = prop.getObject(Msg.NAME);
				if(obj != null) {
					fdef.setName(obj.toString());
				}
				obj = prop.getObject(Msg.MAX);
				if(obj != null) {
					fdef.setMAX(new Double((Float)obj));
				}
				obj = prop.getObject(Msg.MIN);
				if(obj != null) {
					fdef.setMIN(new Double((Float)obj));
				}
				obj = prop.getObject(Msg.DESCRIPTION);
				if(obj != null) {
					fdef.setDescription(obj.toString());
				}
				ret.add(fdef);
			}
		}
		return ret;
	}

	/**
	 * @return
	 * @throws RMSException
	 */
	public List<ReactionDef> getReactions() throws RMSException {
		fEditorReaction.stopEditing();
		List<ReactionDef> ret = new LinkedList<ReactionDef>();
		List<TypedProperties> props = fEditorReaction.getModel().getAllProperties();
		if(!Utils.isEmptyCollection(props)) {
			for(TypedProperties prop : props) {
				Object obj = prop.getObject(Msg.REACTION);
				if(obj == null) {
					throw new RMSException("One of the reactions is not defined.");
				}
				ReactionDef rdef = ((ReactionExtendedEditor.ReactionDefWrapper)obj).getReactionDef();
				ret.add(rdef);
			}
		}
		return ret;
	}

	/**
	 * @return the resources to be used in the data view definition
	 * @throws RMSException
	 */
	public abstract List<ResourceDef> getResources() throws RMSException;

	/**
	 * @return a list of potential parameters for functions and reactions
	 */
	protected abstract String[] getPotentialParameters();

	/**
	 * @param funcName
	 * @return
	 */
	protected abstract String getFunctionIName(String funcName);

	/**
	 * @param prop
	 * @throws RMSException
	 * @throws VetoException
	 */
	protected void handleAboutToAddFunction(TypedProperties prop) throws RMSException, VetoException {
		String[] params = getPotentialParameters();
		if(Utils.isEmptyArray(params)) {
			throw new RMSException("Before adding functions, counters must be added for this data view.");
		}
		FunctionEditorDialog dlg = new FunctionEditorDialog(fParent, params);
		UIUtils.centerDialogAndShow(fParent, dlg);
		FunctionDef fun = dlg.getResult();
		if(fun == null) {
			// cancel
			throw new VetoException();
		}
		fFunctionCounter++;
		prop.setObject(Msg.CODE, new FunctionExtendedEditor.FunctionDefWrapper(fun));
		prop.setString(Msg.ID, "fid" + fFunctionCounter);
		String funcName = "Function"  + fFunctionCounter;
		prop.setString(Msg.NAME, funcName);
		String functionIName = getFunctionIName(funcName);
		if(functionIName != null) {
			// make a unique function iname
			prop.setString(Msg.INAME, functionIName);
		}
	}

	/**
	 * @param editor
	 * @param pe
	 * @throws RMSException
	 */
	protected void handleAboutToLaunchExtendedEditor(ExtendedEditor editor, PropertyEntry pe) throws RMSException {
		String[] params = getPotentialParameters();
		if(editor instanceof ReactionExtendedEditor) {
			if(params == null) {
				throw new RMSException("There are no parameters available to define a reaction.\nSelect first counters for this data view.");
			}
			((ReactionExtendedEditor)editor).prepare(params);
		} else if(editor instanceof FunctionExtendedEditor) {
			if(params == null) {
				throw new RMSException("There are no parameters available to define a function.\nSelect first counters for this data view.");
			}
			((FunctionExtendedEditor)editor).prepare(params);
		}
	}

	/**
	 * @param prop
	 * @throws RMSException
	 * @throws VetoException
	 */
	protected void handleAboutToAddReaction(final TypedProperties prop) throws RMSException, VetoException {
		String[] params = getPotentialParameters();
		if(Utils.isEmptyArray(params)) {
			throw new RMSException("Before adding reactions, counters must be added for this data view.");
		}
		ReactionEditorDialog dlg = new ReactionEditorDialog(fParent,
			params,
			new ReactionEditorDialog.Callback(){
				public void handleReactionDef(ReactionDef def) throws Exception {
					prop.setObject(Msg.REACTION, new ReactionExtendedEditor.ReactionDefWrapper(def));
				}
		});
		// modal
		UIUtils.centerDialogAndShow(fParent, dlg);
		if(prop.getObject(Msg.REACTION) == null) {
			throw new VetoException();
		}
	}

}
