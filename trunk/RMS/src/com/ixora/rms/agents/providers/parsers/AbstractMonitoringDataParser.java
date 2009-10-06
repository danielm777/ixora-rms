/*
 * Created on 17-Jan-2005
 */
package com.ixora.rms.agents.providers.parsers;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.ixora.common.utils.Utils;
import com.ixora.rms.CounterId;
import com.ixora.rms.EntityDescriptor;
import com.ixora.rms.EntityId;
import com.ixora.rms.data.CounterValue;
import com.ixora.rms.providers.ProviderId;
import com.ixora.rms.providers.parsers.exception.InvalidData;
import com.ixora.rms.providers.parsers.exception.ParserException;

/**
 * @author Daniel Moraru
 */
public abstract class AbstractMonitoringDataParser implements MonitoringDataParser {
    // entity states
    protected static final Integer NEW = new Integer(0);
    protected static final Integer EXPIRED = new Integer(1);
    protected static final Integer SAME = new Integer(2);

    /** True if this parser has volatile entities */
    protected boolean fHasVolatileEntities;
    /** Whether or not volatile entities should be accumulated between data samples */
    protected boolean fAccumulateVolatileEntities;
	/** Descriptors for all entities provided by this parser */
	protected Map<EntityId, EntityDescriptor> fDescriptors;
	/** Listener */
	protected Listener fListener;
	/** Provider id */
	protected ProviderId fProviderId;
    /** Map holding the current entities, i.e. entities that show up in the last provider buffer */
    protected Map<EntityId, Integer> fCurrentEntities;
    /** Holds per cycle entity data */
    protected Map<EntityId, EntityData> fPerCycleEntityData;
    /** Map with new entity descriptors dicovered per cycle */
    protected Map<EntityId, EntityDescriptor> fPerCycleNewEntityDescriptors;

    protected static final class EntityData {
        public EntityId entityId;
        public List<CounterId> counterIds;
        public List<CounterValue> values;

        public EntityData(EntityId eid) {
            entityId = eid;
            counterIds = new LinkedList<CounterId>();
            values = new LinkedList<CounterValue>();
        }
    }

	/**
	 * Constructor.
	 */
	protected AbstractMonitoringDataParser() {
		super();
		fDescriptors = new LinkedHashMap<EntityId, EntityDescriptor>();
		fCurrentEntities = new LinkedHashMap<EntityId, Integer>();
        fPerCycleEntityData = new LinkedHashMap<EntityId, EntityData>();
        fPerCycleNewEntityDescriptors = new LinkedHashMap<EntityId, EntityDescriptor>();
	}

	/**
	 * @see com.ixora.rms.agents.providers.parsers.MonitoringDataParser#setEntityDescriptors(Map<EntityId, EntityDescriptor>)
	 */
	public void setEntityDescriptors(Map<EntityId, EntityDescriptor> entities) {
		fDescriptors = entities;
	}

	/**
	 * @see com.ixora.rms.agents.providers.parsers.MonitoringDataParser#setListener(com.ixora.rms.agents.providers.parsers.MonitoringDataParser.Listener)
	 */
	public void setListener(Listener l) {
		if(l == null) {
			throw new IllegalArgumentException("null listener");
		}
		this.fListener = l;
	}

	/**
	 * @see com.ixora.rms.agents.providers.parsers.MonitoringDataParser#setProviderId(com.ixora.rms.providers.ProviderId)
	 */
	public void setProviderId(ProviderId pid) {
		this.fProviderId  = pid;
	}

    /**
     * Paramter <code>obj</code> must be String[](rows) or String[][](table)
     * @throws ParserException
     * @see com.ixora.rms.providers.parsers.Parser#parse(java.lang.Object)
     */
    public void parse(Object obj) throws ParserException {
        // mark all entities as EXPIRED
        if(fHasVolatileEntities) {
            for(EntityId eid : fCurrentEntities.keySet()) {
                fCurrentEntities.put(eid, EXPIRED);
            }
        }
        // clear per cycle data
        fPerCycleEntityData.clear();
        fPerCycleNewEntityDescriptors.clear();

        // subclasses fill data here
        doParsing(obj);

        // must fire new entity before sending counter value
        if(!Utils.isEmptyMap(fPerCycleNewEntityDescriptors)) {
            fListener.newEntities(fProviderId,
                fPerCycleNewEntityDescriptors.values().toArray(
                        new EntityDescriptor[fPerCycleNewEntityDescriptors.size()]));
        }

        // fire sample data
        for(EntityData ed : fPerCycleEntityData.values()) {
        	if(ed.entityId != null) {
	            // check that data is valid
	            if(ed.counterIds.size() != ed.values.size()) {
	                throw new InvalidData("Counters/values list size mismatch");
	            }
	            fListener.sampleData(fProviderId, ed.entityId, ed.counterIds, ed.values);
        	}
        }

        // fire sample ended
        fListener.sampleEnded(fProviderId);

        // fire entities removed now if the parser doesn't specify that
        // they should be accumulated
        if(fHasVolatileEntities && !fAccumulateVolatileEntities) {
            List<EntityId> eids = new LinkedList();
            for(Map.Entry<EntityId, Integer> me : fCurrentEntities.entrySet()) {
                if(me.getValue() == EXPIRED) {
                    eids.add(me.getKey());
                }
            }
            if(eids.size() > 0) {
                fListener.expiredEntities(fProviderId,
                        eids.toArray(new EntityId[eids.size()]));
                // removed expired entities
                for(EntityId eid : eids) {
                    fCurrentEntities.remove(eid);
                }
            }
        }
    }

    /**
     * @param obj
     * @throws ParserException
     */
    protected abstract void doParsing(Object obj) throws ParserException;
}
