/*
 * Created on 31-Dec-2004
 */
package com.ixora.rms.ui.tools.providermanager;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import com.ixora.common.MessageRepository;
import com.ixora.common.typedproperties.TypedProperties;
import com.ixora.common.typedproperties.ui.list.PropertyListTableModel;
import com.ixora.common.typedproperties.ui.list.TypedPropertiesListEditor;
import com.ixora.common.ui.UIFactoryMgr;
import com.ixora.common.utils.Utils;
import com.ixora.rms.CounterDescriptor;
import com.ixora.rms.CounterDescriptorImpl;
import com.ixora.rms.CounterId;
import com.ixora.rms.CounterType;
import com.ixora.rms.EntityDescriptor;
import com.ixora.rms.EntityDescriptorImpl;
import com.ixora.rms.EntityId;
import com.ixora.rms.agents.providers.parsers.MonitoringDataParsingRulesDefinition;
import com.ixora.rms.repository.ParserInstance;
import com.ixora.rms.ui.tools.providermanager.exception.EntityDescriptorsMissing;
import com.ixora.rms.ui.tools.providermanager.exception.ParsingRulesMissing;
import com.ixora.rms.ui.tools.providermanager.messages.Msg;

/**
 * @author Daniel Moraru
 */
public final class DataDescriptorsPanel extends JPanel {
	private TypedPropertiesListEditor fEditorEntities;
	private TypedPropertiesListEditor fEditorCounters;
	private TypedProperties fPrototypeEntity;
	private TypedProperties fPrototypeCounter;

	/**
	 * Model for the object editor.
	 */
	private final class CustomPropertiesListTableModelEntities extends PropertyListTableModel {
		public CustomPropertiesListTableModelEntities(TypedProperties arg0, String arg1, List<TypedProperties> arg2) {
			super(arg0, arg1, arg2, true);
		}
		/**
		 * @see javax.swing.table.TableModel#isCellEditable(int, int)
		 */
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return columnIndex == 0 ? false : true;
		}
	}

	/**
	 * Model for the object editor.
	 */
	private final class CustomPropertiesListTableModelCounters extends PropertyListTableModel {
		public CustomPropertiesListTableModelCounters(TypedProperties arg0, String arg1, List<TypedProperties> arg2) {
			super(arg0, arg1, arg2, true);
		}
		/**
		 * @see javax.swing.table.TableModel#isCellEditable(int, int)
		 */
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return columnIndex == 0 || columnIndex == 1 ? false : true;
		}
	}

	/**
	 * Constructor.
	 */
	public DataDescriptorsPanel() {
		super();
		fPrototypeEntity = createFromEntityDescriptor(null);
		fPrototypeCounter = createFromCounterDescriptor(null, null);
	}

	/**
	 * @param entityDescriptors an existing set of descriptors;
     * entities found in the parsing rules will be assigned a matching descriptor from this set
	 * @param parserInstanceData
     * @param mergeWithExistingData
	 * @throws ParsingRulesMissing
	 */
	public void setProviderInstanceData(
						Map<EntityId, EntityDescriptor> entityDescriptors,
						ParserInstance parserInstanceData) throws ParsingRulesMissing {
        if(parserInstanceData == null) {
            throw new ParsingRulesMissing();
        }
        if(fEditorCounters != null) {
			remove(fEditorCounters);
		}
		if(fEditorEntities != null) {
			remove(fEditorEntities);
		}
		// must get first all entities and counters from the parser and
		// then add descriptors only for them
		MonitoringDataParsingRulesDefinition trules = (MonitoringDataParsingRulesDefinition)parserInstanceData.getRules();
		Set<EntityId> eids = trules.getEntities();
		List<TypedProperties> lstEntities = new LinkedList<TypedProperties>();
		for(EntityId eid : eids) {
			EntityDescriptor ed = null;
			if(entityDescriptors != null) {
				ed = entityDescriptors.get(eid);
			}
			if(ed != null) {
				lstEntities.add(createFromEntityDescriptor(ed));
			} else {
				// build a bare descriptor to be further edited
				lstEntities.add(createFromEntityDescriptor(new EntityDescriptorImpl(eid, false)));
			}
		}
		fEditorEntities = new TypedPropertiesListEditor(
				new CustomPropertiesListTableModelEntities(
						fPrototypeEntity,
						ProviderManagerComponent.NAME,
						lstEntities),
				TypedPropertiesListEditor.BUTTON_NONE);
		fEditorEntities.setBorder(UIFactoryMgr.createTitledBorder(
                MessageRepository.get(ProviderManagerComponent.NAME, Msg.TEXT_ENTITIES)));

		List<TypedProperties> lstCounters = new LinkedList<TypedProperties>();
		for(EntityId eid : eids) {
			// get existing counter descriptors for this entity
			Map<CounterId, CounterDescriptor> cdsMap = new TreeMap<CounterId, CounterDescriptor>();
            Set<CounterId> cids = trules.getCounters(eid);
            if(entityDescriptors != null) {
				EntityDescriptor ed = entityDescriptors.get(eid);
				if(ed != null) {
					Collection<CounterDescriptor> ecds = ed.getCounterDescriptors();
					for(CounterDescriptor cd : ecds) {
                        // add it only if it's in the rules def
                        CounterId counterId = cd.getId();
                        if(cids.contains(counterId)) {
                            cdsMap.put(counterId, cd);
                        }
					}
				}
			}
			// now add bare counter descriptors for counters that only appears
			// in the parser data and have no descriptor
			for(CounterId ci : cids) {
				if(cdsMap.get(ci) == null) {
					cdsMap.put(ci, new CounterDescriptorImpl(ci, "", CounterType.DOUBLE, false));
				}
			}
			// build now the list of TypedProperties for counters
			for(CounterDescriptor cd : cdsMap.values()) {
				lstCounters.add(createFromCounterDescriptor(eid, cd));
			}
		}

		fEditorCounters = new TypedPropertiesListEditor(
				new CustomPropertiesListTableModelCounters(
						fPrototypeCounter,
                        ProviderManagerComponent.NAME,
						lstCounters),
				TypedPropertiesListEditor.BUTTON_NONE);
		fEditorCounters.setBorder(UIFactoryMgr.createTitledBorder(
                MessageRepository.get(ProviderManagerComponent.NAME, Msg.TEXT_COUNTERS)));
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		fEditorCounters.getTable().getColumnModel().getColumn(2).setMaxWidth(60);
		fEditorCounters.getTable().getColumnModel().getColumn(3).setMaxWidth(60);

		add(fEditorEntities);
		add(fEditorCounters);
	}

    /**
     * Stops all editing
     */
    public void stopEditing() {
        if(fEditorCounters!= null) {
            fEditorCounters.stopEditing();
        }
        if(fEditorEntities!= null) {
            fEditorEntities.stopEditing();
        }
    }

	/**
	 * @param pid parser data that will be used to filter the returned entity descriptors
	 * @return
	 * @throws EntityDescriptorsMissing
	 */
	public Map<EntityId, EntityDescriptor> getEntityDescriptors(ParserInstance pid) throws EntityDescriptorsMissing {
		if(fEditorCounters == null || fEditorEntities == null) {
		    throw new EntityDescriptorsMissing();
		}
		fEditorCounters.stopEditing();
		fEditorEntities.stopEditing();

        MonitoringDataParsingRulesDefinition trules = (MonitoringDataParsingRulesDefinition)pid.getRules();
        // this is the filter set
        Set<EntityId> filterEntities = trules.getEntities();

		List<TypedProperties> lstCounters = fEditorCounters.getModel().getAllProperties();
		List<TypedProperties> lstEntities = fEditorEntities.getModel().getAllProperties();

		Map<EntityId, List<CounterDescriptorImpl>> counters =
				new HashMap<EntityId, List<CounterDescriptorImpl>>();
		for(TypedProperties prop : lstCounters) {
			EntityId eid = new EntityId(prop.getString(Msg.TEXT_PROPERTY_ENTITY));
            // use this entity only if in the filter set
            if(!filterEntities.contains(eid)) {
                continue;
            }

            // this is the counter filter
            Set<CounterId> filterCounters = trules.getCounters(eid);
			CounterDescriptorImpl cd = createCounterFromTypedProperties(prop);
            // use this counter only if in the filter set
            if(!filterCounters.contains(cd.getId())) {
                continue;
            }

			List<CounterDescriptorImpl> lst = counters.get(eid);
			if(lst == null) {
				lst = new LinkedList<CounterDescriptorImpl>();
				counters.put(eid, lst);
			}
            // add counter only if in counter filter

			lst.add(cd);
		}

		Map<EntityId, EntityDescriptor> ret = new HashMap<EntityId, EntityDescriptor>();
		for(TypedProperties prop : lstEntities) {
            EntityId eid = new EntityId(prop.getString(Msg.TEXT_PROPERTY_ENTITY));
            // use filter again
            if(!filterEntities.contains(eid)) {
                continue;
            }
			EntityDescriptor ed = createEntityFromTypedProperties(prop,
					counters.get(eid));
			ret.put(ed.getId(), ed);
		}
		if(Utils.isEmptyMap(ret)) {
		    throw new EntityDescriptorsMissing();
        }
		return ret;
	}

	/**
	 * @param oldEntitiesData
	 * @param cd can be null
     * @param oldEntitiesData can be null
	 * @return
	 */
	private static TypedProperties createFromEntityDescriptor(EntityDescriptor ed) {
		TypedProperties prop = new TypedProperties();
		prop.setProperty(Msg.TEXT_PROPERTY_ENTITY, TypedProperties.TYPE_STRING, true, true);
		prop.setProperty(Msg.TEXT_PROPERTY_ENTITY_DESCRIPTION, TypedProperties.TYPE_STRING, true, false);
		if(ed != null) {
            String eid = ed.getId().toString();
			prop.setString(Msg.TEXT_PROPERTY_ENTITY, eid);
			prop.setString(Msg.TEXT_PROPERTY_ENTITY_DESCRIPTION, ed.getDescription());
		}
		return prop;
	}

	/**
	 * @param prop
	 * @param name
	 * @return
	 */
	private static EntityDescriptor createEntityFromTypedProperties(TypedProperties prop, List<CounterDescriptorImpl> counters) {
		String description = prop.getString(Msg.TEXT_PROPERTY_ENTITY_DESCRIPTION);
		EntityId eid = new EntityId(prop.getString(Msg.TEXT_PROPERTY_ENTITY));
		EntityDescriptor ret = new EntityDescriptorImpl(eid, description, counters);
		return ret;
	}

	/**
	 * @param eid can be null
	 * @param cd can be null
	 * @return
	 */
	private static TypedProperties createFromCounterDescriptor(EntityId eid, CounterDescriptor cd) {
		TypedProperties prop = new TypedProperties();
		prop.setProperty(Msg.TEXT_PROPERTY_ENTITY, TypedProperties.TYPE_STRING, true, true);
		prop.setProperty(Msg.TEXT_PROPERTY_COUNTER, TypedProperties.TYPE_STRING, true, true);
		prop.setProperty(Msg.TEXT_PROPERTY_COUNTER_TYPE, TypedProperties.TYPE_SERIALIZABLE, true, new CounterType[] {CounterType.DOUBLE, CounterType.LONG, CounterType.STRING});
		prop.setProperty(Msg.TEXT_PROPERTY_COUNTER_DISCRETE, TypedProperties.TYPE_BOOLEAN, true, false);
		prop.setProperty(Msg.TEXT_PROPERTY_COUNTER_DESCRIPTION, TypedProperties.TYPE_STRING, true, false);
		if(cd != null || eid != null) {
            String seid = eid.toString();
            String cid = cd.getId().toString();
			prop.setString(Msg.TEXT_PROPERTY_ENTITY, seid);
			prop.setString(Msg.TEXT_PROPERTY_COUNTER, cid);
			prop.setString(Msg.TEXT_PROPERTY_COUNTER_DESCRIPTION, cd.getDescription());
			prop.setObject(Msg.TEXT_PROPERTY_COUNTER_TYPE, cd.getType());
			prop.setBoolean(Msg.TEXT_PROPERTY_COUNTER_DISCRETE, cd.isDiscreet());
		}
		return prop;
	}

	/**
	 * @param prop
	 * @return
	 */
	private static CounterDescriptorImpl createCounterFromTypedProperties(TypedProperties prop) {
		String description = prop.getString(Msg.TEXT_PROPERTY_COUNTER_DESCRIPTION);
		CounterId cid = new CounterId(prop.getString(Msg.TEXT_PROPERTY_COUNTER));
		CounterType type = (CounterType)prop.getObject(Msg.TEXT_PROPERTY_COUNTER_TYPE);
		boolean discrete = prop.getBoolean(Msg.TEXT_PROPERTY_COUNTER_DISCRETE);
		CounterDescriptorImpl ret = new CounterDescriptorImpl(cid, description, type, discrete);
		return ret;
	}
}
