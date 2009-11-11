/*
 * Created on 24-Jul-2004
 */
package com.ixora.rms.client.model;

import com.ixora.rms.HostId;
import com.ixora.rms.ResourceId;
import com.ixora.rms.EntityId;

/**
 * This class holds information for a monitoring resource.
 * @author Daniel Moraru
 */
public final class ResourceInfo {
    /** The id representing this resource */
    private ResourceId resourceId;

// info for all elements describing this resource
	private SessionInfo schemeInfo;
    private HostInfo hostInfo;
	private AgentInfo agentInfo;
	private EntityInfo entityInfo;
	private CounterInfo counterInfo;


	/**
	 * Default constructor to support xml.
	 */
	public ResourceInfo() {
		super();
	}

	/**
	 * Constructor.
	 * @param si
	 * @param hi
	 * @param ai
	 * @param ei
	 * @param ci
	 */
	public ResourceInfo(
	        SessionInfo si,
	        HostInfo hi, AgentInfo ai, EntityInfo ei, CounterInfo ci) {
		super();
		if(hi == null && ai == null && ei == null && ci == null) {
		    this.resourceId = null;
		} else {
			this.resourceId = new ResourceId(
			       hi == null ? null : new HostId(hi.getName()),
			       ai == null ? null : ai.getDeploymentDtls().getAgentId(),
			       ei == null ? null : ei.getId(),
			       ci == null ? null : ci.getId());
		}
		this.schemeInfo = si;
		this.hostInfo = hi;
		this.agentInfo = ai;
		this.entityInfo = ei;
		this.counterInfo = ci;
	}
	/**
	 * @return Returns the agentInfo.
	 */
	public AgentInfo getAgentInfo() {
		return agentInfo;
	}
	/**
	 * @return Returns the counterInfo.
	 */
	public CounterInfo getCounterInfo() {
		return counterInfo;
	}
	/**
	 * @return Returns the entityInfo.
	 */
	public EntityInfo getEntityInfo() {
		return entityInfo;
	}
	/**
	 * @return Returns the hostInfo.
	 */
	public HostInfo getHostInfo() {
		return hostInfo;
	}

	/**
	 * @return the schemeInfo.
	 */
	public SessionInfo getSchemeInfo() {
	    return schemeInfo;
	}

	/**
	 * @return the resource id that identifies this resource
	 */
	public ResourceId getResourceId() {
	    return resourceId;
	}

	/**
     * @see java.lang.Object#toString()
     */
    public String toString() {
	    if(resourceId == null) {
	        return "";
	    }
	    StringBuffer buff = new StringBuffer();
	    int rep = resourceId.getRepresentation();
	    switch(rep) {
		    case ResourceId.INVALID:
				buff.append(this.hostInfo != null ? this.hostInfo.getName() : "-");
				buff.append(EntityId.DELIMITER);
				buff.append(this.agentInfo != null ? this.agentInfo.getTranslatedName() : "-");
				buff.append(EntityId.DELIMITER);
				buff.append(this.entityInfo != null ? this.entityInfo.getTranslatedPath() : "-");
				buff.append(EntityId.DELIMITER);
				buff.append("[");
				buff.append(this.counterInfo != null ? this.counterInfo.getTranslatedName() : "-");
				buff.append("]");
		        break;
		    case ResourceId.HOST:
		        buff.append(this.hostInfo.getName());
		        break;
		    case ResourceId.AGENT:
		        buff.append(this.hostInfo.getName());
		        buff.append(EntityId.DELIMITER);
		        buff.append(this.agentInfo.getTranslatedName());
		        break;
		    case ResourceId.ENTITY:
		        buff.append(this.hostInfo.getName());
		        buff.append(EntityId.DELIMITER);
		        buff.append(this.agentInfo.getTranslatedName());
		        buff.append(EntityId.DELIMITER);
		        buff.append(this.entityInfo.getTranslatedPath());
		        break;
		    case ResourceId.COUNTER:
		        buff.append(this.hostInfo.getName());
		        buff.append(EntityId.DELIMITER);
		        buff.append(this.agentInfo.getTranslatedName());
		        buff.append(EntityId.DELIMITER);
		        buff.append(this.entityInfo.getTranslatedPath());
		        buff.append(EntityId.DELIMITER);
		        buff.append("[");
		        buff.append(this.counterInfo.getTranslatedName());
		        buff.append("]");
		        break;
	    }
	    return buff.toString();
    }
}
