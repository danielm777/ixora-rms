/**
 * 12-Jul-2005
 */
package com.ixora.rms.logging.data;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.ixora.rms.HostId;
import com.ixora.common.utils.Utils;
import com.ixora.rms.EntityDataBuffer;
import com.ixora.rms.EntityId;
import com.ixora.rms.agents.AgentDataBuffer;
import com.ixora.rms.agents.AgentDataBufferImpl;
import com.ixora.rms.agents.AgentDescriptor;
import com.ixora.rms.agents.AgentId;

/**
 * @author Daniel Moraru
 */
public class AggAgentDataBuffer {
	private HostId fHost;
    /** Agent descriptor */
    private AgentDescriptor fAgentDescriptor;
	/** Agent id */
	private AgentId fAgent;
	/** Aggregated buffer for each entity */
	private Map<EntityId, AggEntityDataBuffer> fEntitiesData;

	public AggAgentDataBuffer() {
		super();
		fEntitiesData = new HashMap<EntityId, AggEntityDataBuffer>();
	}

	/**
	 * @param buff
	 */
	public AggAgentDataBuffer(AgentDataBuffer buff) {
		this();
		addAgentDataBuffer(buff);
	}

	/**
	 * @param buff
	 * @return false if the new buffer could not be aggregated
	 */
	public boolean addAgentDataBuffer(AgentDataBuffer buff) {
		if(!buff.isValid()) {
			return true;
		}
		fHost = buff.getHost();
		fAgent = buff.getAgent();
		AgentDescriptor ad = buff.getAgentDescriptor();
		if(ad != null) {
			fAgentDescriptor = ad;
		}
		EntityDataBuffer[] ebuffs = buff.getBuffers();
		if(!Utils.isEmptyArray(ebuffs)) {
			for(EntityDataBuffer ebuff : ebuffs) {
				EntityId eid = ebuff.getEntityId();
				AggEntityDataBuffer agged = fEntitiesData.get(eid);
				if(agged == null) {
					agged = new AggEntityDataBuffer();
					fEntitiesData.put(eid, agged);
				}
				if(!agged.addEntityDataBuffer(ebuff)) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * @return
	 */
	public AgentDataBuffer getAgentDataBuffer() {
		if(fEntitiesData.size() == 0) {
			return null;
		}
		List<EntityDataBuffer> entities = new LinkedList<EntityDataBuffer>();
		for(AggEntityDataBuffer agged : fEntitiesData.values()) {
			EntityDataBuffer eb = agged.geEntityDataBuffer();
			if(eb != null) {
				entities.add(eb);
			}
		}
		return new AgentDataBufferImpl(fHost, fAgent, fAgentDescriptor, entities.toArray(
				new EntityDataBuffer[entities.size()]));
	}
}
