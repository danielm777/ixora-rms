/**
 * 29-Dec-2005
 */
package com.ixora.rms.ui.artefacts.dataview.wizard;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.JDialog;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.ixora.rms.ResourceId;
import com.ixora.common.typedproperties.TypedProperties;
import com.ixora.common.typedproperties.ui.list.PropertyListTableModel;
import com.ixora.common.typedproperties.ui.list.TypedPropertiesListEditor;
import com.ixora.common.ui.UIExceptionMgr;
import com.ixora.common.ui.UIUtils;
import com.ixora.common.ui.forms.FormPanel;
import com.ixora.common.utils.Utils;
import com.ixora.rms.CounterId;
import com.ixora.rms.EntityId;
import com.ixora.rms.client.model.CounterInfo;
import com.ixora.rms.client.model.ResourceInfo;
import com.ixora.rms.client.model.SessionModel;
import com.ixora.rms.dataengine.definitions.ResourceDef;
import com.ixora.rms.exception.RMSException;
import com.ixora.rms.ui.ResourceSelectorPanel;
import com.ixora.rms.ui.SessionTreeExplorer;
import com.ixora.rms.ui.artefacts.dataview.wizard.messages.Msg;

/**
 * @author Daniel Moraru
 */
public class EntityRegexQueryDefPanel extends QueryDefPanel {
	private static final String LABEL_ENTITY_PATH = "Entity Regular Expression";
	private static final String LABEL_COUNTERS = "Counters";

	private FormPanel fForm;
	private TypedPropertiesListEditor fEditorResource;
	private TypedProperties fPrototypeResource;
	private ResourceSelectorPanel fRidPanel;
	private EventHandler fEventHandler;
	private ResourceId fContext;
	/** The iname of the category resource */
	private String fCategoryIName;

	private class EventHandler implements DocumentListener {
		/**
		 * @see javax.swing.event.DocumentListener#insertUpdate(javax.swing.event.DocumentEvent)
		 */
		public void insertUpdate(DocumentEvent e) {
			handleRegexChanged();
		}
		/**
		 * @see javax.swing.event.DocumentListener#removeUpdate(javax.swing.event.DocumentEvent)
		 */
		public void removeUpdate(DocumentEvent e) {
			handleRegexChanged();
		}
		/**
		 * @see javax.swing.event.DocumentListener#changedUpdate(javax.swing.event.DocumentEvent)
		 */
		public void changedUpdate(DocumentEvent e) {
			handleRegexChanged();
		}
	}

	/**
	 * @param parent
	 * @param sessionModel
	 * @param context
	 * @param explorer
	 */
	public EntityRegexQueryDefPanel(JDialog parent, SessionModel sessionModel,
			ResourceId context, SessionTreeExplorer explorer) {
		super(parent, sessionModel);
		setLayout(new BorderLayout());
		fEventHandler = new EventHandler();
		fForm = new FormPanel(FormPanel.VERTICAL2);
		fContext = context;

		fPrototypeResource = new TypedProperties();
		fPrototypeResource.setProperty(Msg.USE, TypedProperties.TYPE_BOOLEAN, true, true);
		fPrototypeResource.setBoolean(Msg.USE, false);
		fPrototypeResource.setProperty(Msg.COUNTER, TypedProperties.TYPE_STRING, true, true);
		fPrototypeResource.setProperty(Msg.ID, TypedProperties.TYPE_STRING, true, true);
		fPrototypeResource.setProperty(Msg.INAME, TypedProperties.TYPE_STRING, true, true);
		fPrototypeResource.setProperty(Msg.NAME, TypedProperties.TYPE_STRING, true, true);
		fPrototypeResource.setProperty(Msg.DESCRIPTION, TypedProperties.TYPE_STRING, true, false);
		fPrototypeResource.setProperty(Msg.MAX, TypedProperties.TYPE_FLOAT, true, false);
		fPrototypeResource.setProperty(Msg.MIN, TypedProperties.TYPE_FLOAT, true, false);
		fPrototypeResource.setProperty(Msg.CODE, TypedProperties.TYPE_STRING, true, false);

		fEditorResource = new TypedPropertiesListEditor(
				new PropertyListTableModel(fPrototypeResource, null, null) {
					public boolean isCellEditable(int rowIndex, int columnIndex) {
						// counter column not editable
						if(columnIndex == 1) {
							return false;
						}
						return super.isCellEditable(rowIndex, columnIndex);
					}

				},
				TypedPropertiesListEditor.BUTTON_UP_DOWN);

		fRidPanel = new ResourceSelectorPanel(parent, ResourceSelectorPanel.SELECT_ENTITY, explorer, sessionModel, context);

		fForm.addPairs(new String[]{
				LABEL_ENTITY_PATH,
				LABEL_COUNTERS,
				LABEL_FUNCTIONS,
				LABEL_REACTIONS
		},
		new Component[]{
				fRidPanel,
				fEditorResource,
				fEditorFunction,
				fEditorReaction
		});

		add(fForm, BorderLayout.NORTH);

		fEditorResource.getTable().getColumnModel().getColumn(0).setPreferredWidth(25);
		fEditorResource.getTable().getColumnModel().getColumn(1).setPreferredWidth(120);
		fEditorResource.getTable().getColumnModel().getColumn(2).setPreferredWidth(25);
		fEditorResource.getTable().getColumnModel().getColumn(3).setPreferredWidth(120);

		if(context != null) {
			applyRegexRid(context);

		}

		fRidPanel.getResourceIdTextField().getDocument().addDocumentListener(fEventHandler);

		fEditorResource.setPreferredSize(new Dimension(650, 150));
		fEditorFunction.setPreferredSize(new Dimension(650, 70));
		fEditorReaction.setPreferredSize(new Dimension(650, 70));


		if(context != null) {
			if(context.getRepresentation() == ResourceId.ENTITY) {
				// now this is the only style supported by this regex wizard
				// later on we should support regex on host and agent
				fRidPanel.getResourceIdTextField().setText(context.getRelativeTo(context).toString() + "/(.*)");
			}
		}

		setPreferredSize(new Dimension(650, 450));
	}

	/**
	 * The last element in the list is the category resource.
	 * @throws RMSException
	 * @see com.ixora.rms.ui.artefacts.dataview.wizard.QueryDefPanel#getResources()
	 */
	public List<ResourceDef> getResources() throws RMSException {
		fEditorResource.stopEditing();
		String txt = getRegexEntityPath();
		List<ResourceDef> ret = new LinkedList<ResourceDef>();
		List<TypedProperties> props = fEditorResource.getModel().getAllProperties();
		for(TypedProperties prop : props) {
			if(prop.getBoolean(Msg.USE)) {
				ResourceId rid = new ResourceId(txt);
				rid = new ResourceId(rid.getHostId(), rid.getAgentId(),
						rid.getEntityId(), new CounterId(prop.getString(Msg.COUNTER)));
				ResourceDef rdef = new ResourceDef(rid);
				// check for styles
				rdef.setID(prop.getString(Msg.ID));
				rdef.setIName(prop.getString(Msg.INAME));
				rdef.setName(prop.getString(Msg.NAME));

				Object obj = prop.getObject(Msg.MAX);
				if(obj != null) {
					rdef.setMAX(new Double((Float)obj));
				}
				obj = prop.getObject(Msg.MIN);
				if(obj != null) {
					rdef.setMIN(new Double((Float)obj));
				}
				obj = prop.getObject(Msg.CODE);
				if(obj != null) {
					rdef.setCode(obj.toString());
				}
				obj = prop.getObject(Msg.DESCRIPTION);
				if(obj != null) {
					rdef.setDescription(obj.toString());
				}
				ret.add(rdef);
			}
		}
		ResourceId catId = new ResourceId(txt);
		ResourceDef cat = new ResourceDef(catId);
		cat.setID("id0");
		cat.setIName(fCategoryIName);
		cat.setName(getNameForCategory(catId));
		ret.add(cat);
		return ret;
	}

	/**
	 * @param catId
	 * @return
	 */
	private String getNameForCategory(ResourceId catId) {
		// if the host part is a regex create the
		// name Host
		if(catId.isRegex(ResourceId.HOST)) {
			return "Host";
		}
		if(catId.isRegex(ResourceId.AGENT)) {
			return "Agent";
		}
		if(catId.isRegex(ResourceId.ENTITY)) {
			// return last non regex entity name
			EntityId eid = catId.getEntityId();
			String[] comps = eid.getPathComponents();
			if(!Utils.isEmptyArray(comps)) {
				String last = null;
				for(String comp : comps) {
					if(!comp.startsWith("(")) {
						last = comp;
					} else {
						break;
					}
				}
				return last;
			}
			return "Entity";
		}
		return "Category";
	}

	/**
	 * @return
	 * @throws RMSException
	 */
	public String getRegexEntityPath() throws RMSException {
		ResourceId rid = fRidPanel.getResourceId();
		if(rid == null) {
			throw new RMSException("Regex entity path can not be empty.");
		}
		return rid.toString();
	}

	/**
	 * @param regexRid
	 */
	private void applyRegexRid(ResourceId regexRid) {
		if(regexRid != null) {
 			regexRid = regexRid.complete(fContext);
		}
		if(regexRid.getRepresentation() == ResourceId.ENTITY) {
 			ResourceInfo[] entities = fSessionModel.getResourceInfo(regexRid, true);
			if(entities != null) {
				// see if they are eligible i.e. they all have similar
				// counters
				Map<CounterId, CounterInfo> lastCounterInfos = null;
				try {
					for(ResourceInfo ri : entities) {
						Collection<CounterInfo> counters = ri.getEntityInfo().getCounterInfo();
						if(lastCounterInfos != null) {
							if(counters.size() == lastCounterInfos.size()) {
								for(CounterInfo counter : counters) {
									// check that this counter id is in the lastCounterInfo
									if(lastCounterInfos.get(counter.getId()) == null) {
										throw new RMSException();
									}
								}
							} else {
								throw new RMSException();
							}
						}
						lastCounterInfos = new TreeMap<CounterId, CounterInfo>();
						for(CounterInfo counter : counters) {
							lastCounterInfos.put(counter.getId(), counter);
						}
					}

				} catch(RMSException e) {
					lastCounterInfos = null;
				}
				if(lastCounterInfos != null) {
					List<TypedProperties> props = new ArrayList<TypedProperties>(lastCounterInfos.size());
					int i = 0;
					for(CounterInfo counter : lastCounterInfos.values()) {
						++i;
						TypedProperties prop = (TypedProperties)fPrototypeResource.clone();
						prop.setString(Msg.COUNTER, counter.getId().toString());
						prop.setString(Msg.ID, "id" + i);
						prop.setString(Msg.NAME, "$counter");
						// default iname = $host/$agent/$entity[1]/.../$entity[n]/$counter - starts at 1
						// to get rid of root
						EntityId eid = regexRid.getEntityId();
						StringBuffer buff = new StringBuffer();
						buff.append("$host/$agent/");
						String[] parts = eid.getPathComponents();
						for(int j = 1; j < parts.length; j++) {
							buff.append("$entity[" + j + "]");
							if(j < parts.length - 1) {
								buff.append("/");
							}
						}
						fCategoryIName = buff.toString();
						buff.append("/$counter");
						prop.setString(Msg.INAME, buff.toString());
						props.add(prop);
					}
					fEditorResource.getModel().setProperties(props);
				}
			}
		}
	}

	private void handleRegexChanged() {
		try {
			fEditorResource.getModel().clear();
			fCategoryIName = null;
			try {
				ResourceId rid = fRidPanel.getResourceId();
				fForm.getLabelWithName(LABEL_ENTITY_PATH).setText(LABEL_ENTITY_PATH);
				if(rid != null) {
					applyRegexRid(rid);
				}
			} catch(Exception e) {
				String txt = UIUtils.getMultilineHtmlText(LABEL_ENTITY_PATH
						+ "&nbsp;&nbsp;<font color=\"FF0000\">(" + e.getMessage() + ")</font>", 400);
				fForm.getLabelWithName(LABEL_ENTITY_PATH).setText(txt);
			}
		} catch(Exception e) {
			UIExceptionMgr.userException(e);
		}
	}


	/**
	 * @see com.ixora.rms.ui.artefacts.dataview.wizard.QueryDefPanel#getPotentialParameters()
	 */
	protected String[] getPotentialParameters() {
		List<TypedProperties> props = fEditorResource.getModel().getAllProperties();
		List<String> params = new LinkedList<String>();
		params.add("id0"); // the category
		for(TypedProperties prop : props) {
			if(prop.getBoolean(Msg.USE)) {
				params.add(prop.getString(Msg.ID));
			}
		}
		if(!Utils.isEmptyCollection(params)) {
			return params.toArray(new String[params.size()]);
		}
		return null;
	}

	/**
	 * @see com.ixora.rms.ui.artefacts.dataview.wizard.QueryDefPanel#getFunctionIName(java.lang.String)
	 */
	protected String getFunctionIName(String funcName) {
		return fCategoryIName + "/" + funcName;
	}

}
