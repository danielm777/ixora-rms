package com.ixora.rms;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.ixora.common.utils.Utils;
import com.ixora.common.xml.XMLUtils;
import com.ixora.common.xml.exception.XMLAttributeMissing;
import com.ixora.common.xml.exception.XMLException;
import com.ixora.common.xml.exception.XMLNodeMissing;
import com.ixora.common.xml.exception.XMLTextNodeMissing;


/**
 * Immmutable class that completely describes a monitored entity.
 * @author Daniel Moraru
 * @author Cristian Costache
 */
public class EntityDescriptorImpl extends MonitoringDescriptorImpl implements EntityDescriptor {
	private static final long serialVersionUID = -2106844972115083079L;
	/** Entity id */
	protected EntityId fEntityId;
	/** Whether or not it has children */
	protected boolean fHasChildren;
	/** Current entity configuration */
	protected EntityConfiguration fConfiguration;
	/** Supports independent sampling interval */
	protected boolean fSupportsIndependentSamplingInterval;
	/** True if able to refresh children */
	protected boolean fCanRefreshChildren;
	/** True if it's safe to refresh the subtree with this entity as the root */
	protected boolean fSafeToRefreshRecursivelly;

	/**
	 * Supported monitoring levels. If this is null and the
	 * entity has counters then the supported levels will be
	 * extracted from the counter levels.
	 */
	protected MonitoringLevel[] fSupportedLevels;
	/** Counters */
	protected Map<CounterId, CounterDescriptorImpl> fCounterDescriptors;

	/**
	 * Constructor to support XML.
	 */
	public EntityDescriptorImpl() {
	    this.fCounterDescriptors = new LinkedHashMap<CounterId, CounterDescriptorImpl>();
	    this.fCanRefreshChildren = true;
	    this.fSafeToRefreshRecursivelly = true;
	}

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj) {
        if(obj == this) {
            return true;
        }
        if(obj == null || !(obj instanceof EntityDescriptorImpl)) {
            return false;
        }
        EntityDescriptorImpl that = (EntityDescriptorImpl)obj;
        return fHasChildren == that.fHasChildren
            && fSupportsIndependentSamplingInterval == that.fSupportsIndependentSamplingInterval
            && fCanRefreshChildren == that.fCanRefreshChildren
            && fSafeToRefreshRecursivelly == that.fSafeToRefreshRecursivelly
            && super.equals(obj)
            && Utils.equals(fConfiguration, that.fConfiguration)
            && Utils.equals(fCounterDescriptors, that.fCounterDescriptors)
            && Utils.equals(fSupportedLevels, that.fSupportedLevels);
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        int hc = super.hashCode();
        if(this.fConfiguration != null) {
            hc ^= this.fConfiguration.hashCode();
        }
        if(this.fCounterDescriptors != null) {
            hc ^= this.fCounterDescriptors.hashCode();
        }
        if(this.fSupportedLevels != null) {
			hc ^= Utils.hashCode(this.fSupportedLevels);
        }
        hc ^= new Boolean(this.fHasChildren).hashCode();
        hc ^= new Boolean(this.fSupportsIndependentSamplingInterval).hashCode();
        hc ^= new Boolean(this.fCanRefreshChildren).hashCode();
        hc ^= new Boolean(this.fSafeToRefreshRecursivelly).hashCode();
        if(this.fCounterDescriptors != null) {
            hc ^= this.fCounterDescriptors.hashCode();
        }
        return hc;
    }

	/**
	 * @param entityId
	 * @param desc
	 */
	public EntityDescriptorImpl(EntityId entityId, EntityDescriptor desc) {
		this.fEntityId = entityId;
		this.fName = entityId.getName();
		this.fAlternateName = desc.getAlternateName();
		this.fConfiguration = desc.getConfiguration();
		this.fDescription = desc.getDescription();
		this.fEnabled = desc.isEnabled();
	    this.fLevel = desc.getLevel();
	    this.fHasChildren = desc.hasChildren();
	    this.fSupportsIndependentSamplingInterval = desc.supportsSamplingInterval();
	    this.fCanRefreshChildren = desc.canRefreshChildren();
	    this.fSafeToRefreshRecursivelly = desc.safeToRefreshRecursivelly();
	    this.fSupportedLevels = desc.getSupportedLevels();
	    this.fCounterDescriptors = new LinkedHashMap<CounterId, CounterDescriptorImpl>();
	    Collection<CounterDescriptor> cdescs = desc.getCounterDescriptors();
	    for(CounterDescriptor cd : cdescs) {
	    	this.fCounterDescriptors.put(cd.getId(), new CounterDescriptorImpl(cd));
	    }
	}

	/**
	 * Constructor for EntityDescriptor.
	 * @param id
	 * @param hasChildren
	 */
	public EntityDescriptorImpl(EntityId id, boolean hasChildren) {
		this(id, null, null, false);
		this.fHasChildren = hasChildren;
	}

	/**
	 * Constructor for EntityDescriptor.
	 * @param id
	 * @param description
	 * @param counters
	 */
	public EntityDescriptorImpl(EntityId id, String description, List<CounterDescriptorImpl> counters) {
		this(id, description, counters, false);
	}

	/**
	 * Constructor for EntityDescriptor.
	 * @param id
	 * @param description
	 * @param counters
	 * @param enabled
	 */
	public EntityDescriptorImpl(
			EntityId id,
			String description,
			List<CounterDescriptorImpl> counters,
			boolean enabled) {
		this(id, null, description, counters, enabled);
	}

	/**
	 * Constructor for EntityDescriptor.
	 * @param id
	 * @param description
	 * @param counters
	 * @param enabled
	 */
	public EntityDescriptorImpl(
			EntityId id,
			String alternateName,
			String description,
			List<CounterDescriptorImpl> counters,
			boolean enabled) {
		this(id, alternateName, description, null, null, counters, enabled, null, false, false);
	}

	/**
	 * Constructor for EntityDescriptor.
	 * @param id
	 * @param description
	 * @param counters
	 * @param enabled
	 */
	public EntityDescriptorImpl(
			EntityId id,
			String alternateName,
			String description,
			MonitoringLevel level,
			MonitoringLevel[] supportedLevels,
			List<CounterDescriptorImpl> counters,
			boolean enabled,
			EntityConfiguration config,
			boolean supportsIndependentSamplingInterval,
			boolean hasChildren) {
		this(id, alternateName, description, level, supportedLevels, counters,
				enabled, config, supportsIndependentSamplingInterval, hasChildren, true, true);
	}

	/**
	 * @param id
	 * @param alternateName
	 * @param description
	 * @param level
	 * @param supportedLevels
	 * @param counters
	 * @param enabled
	 * @param config
	 * @param supportsIndependentSamplingInterval
	 * @param hasChildren
	 * @param canRefrehChildren
	 * @param safeToRefreshRecursivelly
	 */
	public EntityDescriptorImpl(
			EntityId id,
			String alternateName,
			String description,
			MonitoringLevel level,
			MonitoringLevel[] supportedLevels,
			List<CounterDescriptorImpl> counters,
			boolean enabled,
			EntityConfiguration config,
			boolean supportsIndependentSamplingInterval,
			boolean hasChildren,
			boolean canRefrehChildren,
			boolean safeToRefreshRecursivelly) {
		super();
		this.fEntityId = id;
		this.fName = id.getName();
		this.fAlternateName = alternateName;
		this.fDescription = description;
		this.fEnabled = enabled;
		this.fLevel = level;
		this.fSupportedLevels = supportedLevels;
		this.fSupportsIndependentSamplingInterval = supportsIndependentSamplingInterval;
		this.fCanRefreshChildren = canRefrehChildren;
		this.fSafeToRefreshRecursivelly = safeToRefreshRecursivelly;
		this.fConfiguration = config;
		this.fHasChildren = hasChildren;
		if(counters != null) {
			this.fCounterDescriptors = new LinkedHashMap<CounterId, CounterDescriptorImpl>(counters.size());
			for(CounterDescriptorImpl cd : counters) {
				this.fCounterDescriptors.put(cd.getId(), cd);
			}
		} else {
			this.fCounterDescriptors = new LinkedHashMap<CounterId, CounterDescriptorImpl>();
		}
	}

	/**
	 * This has to be final to make sure that the subclasses
	 * will not redefine this method.
	 * @see java.lang.Object#clone()
	 */
	public final Object clone() {
	// Deep copy the counters
		List<CounterDescriptorImpl> newCounters = new ArrayList<CounterDescriptorImpl>(fCounterDescriptors.size());
		for(CounterDescriptorImpl c : this.fCounterDescriptors.values()) {
			newCounters.add((CounterDescriptorImpl)c.clone());
		}
		EntityDescriptorImpl ret = new EntityDescriptorImpl(this.fEntityId, this.fDescription, newCounters, fEnabled);
		if(fConfiguration != null) {
			ret.fConfiguration =
				(EntityConfiguration)fConfiguration.clone();
		}
		ret.fHasChildren = fHasChildren;
		ret.fLevel = fLevel;
		ret.fAlternateName = fAlternateName;
		ret.fSupportedLevels = fSupportedLevels;
		ret.fSupportsIndependentSamplingInterval = fSupportsIndependentSamplingInterval;
		ret.fCanRefreshChildren = fCanRefreshChildren;
		ret.fSafeToRefreshRecursivelly = fSafeToRefreshRecursivelly;
		return ret;
	}

	/**
	 * @return the counters
	 */
	public Collection<CounterDescriptor> getCounterDescriptors() {
		Collection<CounterDescriptor> ret = new ArrayList<CounterDescriptor>(fCounterDescriptors.size());
		ret.addAll(fCounterDescriptors.values());
		return ret;
	}

	/**
	 * @param cid
	 * @return the counter descriptor for the counter
	 * with the given id
	 */
	public CounterDescriptor getCounterDescriptor(CounterId cid) {
		return fCounterDescriptors.get(cid);
	}

	/**
	 * @param level
	 * @return the counters that fit the given monitoring level
	 */
	public Collection<CounterDescriptor> getCounterDescriptorsForLevel(
			MonitoringLevel level) {
		if(level == null) {
			return getCounterDescriptors();
		}
		// filter out according to the current level
		Collection<CounterDescriptor> tmp = new LinkedList<CounterDescriptor>();
		for(CounterDescriptorImpl c : fCounterDescriptors.values()) {
			if(c.getLevel() == null ||
					c.getLevel().compareTo(level) <= 0) {
				tmp.add(c);
			}
		}
		return tmp;
	}

	/**
	 * @param level
	 * @return the counters that fit the given monitoring level
	 */
	public Set<CounterId> getCounterIdsForLevel(
			MonitoringLevel level) {
		if(level == null) {
			return fCounterDescriptors.keySet();
		}
		// filter out according to the current level
		Set<CounterId> tmp = new HashSet<CounterId>();
		for(CounterDescriptorImpl c : fCounterDescriptors.values()) {
			if(c.getLevel() == null ||
					c.getLevel().compareTo(level) <= 0) {
				tmp.add(c.getId());
			}
		}
		return tmp;
	}

	/**
	 * @return the id
	 */
	public EntityId getId() {
		return fEntityId;
	}

	/**
	 * @return the current configuration of this entity
	 */
	public EntityConfiguration getConfiguration() {
		return fConfiguration;
	}

	/**
	 * @see com.ixora.rms.EntityDescriptor#hasChildren()
	 */
	public boolean hasChildren() {
		return fHasChildren;
	}

	/**
	 * @return the supported monitoring levels or null
	 * if the entity is not aware of monitoring levels
	 */
	public MonitoringLevel[] getSupportedLevels() {
		// if specified return it
		if(fSupportedLevels != null) {
			return fSupportedLevels;
		}
		// else get it from the counters
		Set<MonitoringLevel> ret = null;
		MonitoringLevel level;
		for(CounterDescriptor cd : fCounterDescriptors.values()) {
			level = cd.getLevel();
			if(level != null) {
				if(ret == null) {
					ret = new HashSet<MonitoringLevel>();
				}
				ret.add(level);
			}
		}
		if(ret == null) {
			return null;
		}
		return ret.toArray(new MonitoringLevel[ret.size()]);
	}

	/**
	 * @return true if it supports an independent sampling period
	 */
	public boolean supportsSamplingInterval() {
		return fSupportsIndependentSamplingInterval;
	}
    /**
     * @see com.ixora.common.xml.XMLExternalizable#fromXML(org.w3c.dom.Node)
     */
    public void fromXML(Node node) throws XMLException {
        super.fromXML(node);
        Attr ida = XMLUtils.findAttribute(node, "id");
        if(ida == null) {
            throw new XMLAttributeMissing("id");
        }
        fEntityId = new EntityId(ida.getNodeValue());
        fName = fEntityId.getName();
        Node haschildrenn = XMLUtils.findChild(node, "haschildren");
        if(haschildrenn == null) {
            throw new XMLNodeMissing("haschildren");
        }
        String haschildrens = XMLUtils.getText(haschildrenn);
        if(haschildrens == null) {
            throw new XMLTextNodeMissing("haschildren");
        }
        fHasChildren = Boolean.valueOf(haschildrens).booleanValue();
        Node indsamplingn = XMLUtils.findChild(node, "indsampling");
        if(indsamplingn == null) {
            throw new XMLNodeMissing("indsampling");
        }
        String indsamplings = XMLUtils.getText(indsamplingn);
        if(indsamplings == null) {
            throw new XMLTextNodeMissing("haschildren");
        }
        fSupportsIndependentSamplingInterval =
            Boolean.valueOf(indsamplings).booleanValue();
        List<Node> cds = XMLUtils.findChildren(node, "counterdescriptor");
        Node cdn;
        CounterDescriptorImpl cd;
        for(Iterator<Node> iter = cds.iterator(); iter.hasNext();) {
            cdn = iter.next();
            cd = new CounterDescriptorImpl();
            cd.fromXML(cdn);
            fCounterDescriptors.put(cd.getId(), cd);
        }
		Node n = XMLUtils.findChild(node, "config");
		if(n != null) {
			this.fConfiguration = new EntityConfiguration();
			this.fConfiguration.fromXML(n);
		}
    }

    /**
     * @see com.ixora.common.xml.XMLExternalizable#toXML(org.w3c.dom.Node)
     */
    public void toXML(Node parent) throws XMLException {
        super.toXML(parent);
        Document doc = parent.getOwnerDocument();
        Attr ida = doc.createAttribute("id");
        ida.setNodeValue(fEntityId.toString());
        parent.getAttributes().setNamedItem(ida);
        Element haschildrene = doc.createElement("haschildren");
        parent.appendChild(haschildrene);
        haschildrene.appendChild(doc.createTextNode(String.valueOf(fHasChildren)));
        Element indsamplinge = doc.createElement("indsampling");
        parent.appendChild(indsamplinge);
        indsamplinge.appendChild(doc.createTextNode(String.valueOf(
                fSupportsIndependentSamplingInterval)));
        for(Iterator<CounterDescriptorImpl> iter = fCounterDescriptors.values().iterator(); iter.hasNext();) {
        	CounterDescriptor cd = iter.next();
            Element cde = doc.createElement("counterdescriptor");
            cd.toXML(cde);
            parent.appendChild(cde);
        }
        if(fConfiguration != null) {
        	fConfiguration.toXML(parent);
        }
    }

    /**
     * @throws XMLException
     * @see com.ixora.rms.EntityDescriptor#toXML(org.w3c.dom.Node, java.util.Set)
     */
    public void toXML(Node parent, Set<CounterId> monitoredCountersIds) throws XMLException {
        super.toXML(parent);
        Document doc = parent.getOwnerDocument();
        Attr ida = doc.createAttribute("id");
        ida.setNodeValue(fEntityId.toString());
        parent.getAttributes().setNamedItem(ida);
        Element haschildrene = doc.createElement("haschildren");
        parent.appendChild(haschildrene);
        haschildrene.appendChild(doc.createTextNode(String.valueOf(fHasChildren)));
        Element indsamplinge = doc.createElement("indsampling");
        parent.appendChild(indsamplinge);
        indsamplinge.appendChild(doc.createTextNode(String.valueOf(
                fSupportsIndependentSamplingInterval)));
        CounterDescriptor cd;
        Element cde;
        for(Iterator<CounterDescriptorImpl> iter = fCounterDescriptors.values().iterator(); iter.hasNext();) {
            cd = iter.next();
            if(monitoredCountersIds.contains(cd.getId())) {
	            cde = doc.createElement("counterdescriptor");
	            cd.toXML(cde);
	            parent.appendChild(cde);
            }
        }
        if(fConfiguration != null) {
        	fConfiguration.toXML(parent);
        }
    }


	/**
	 * Debug only.
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return fEntityId != null ? fEntityId.toString() : super.toString();
	}

	/**
	 * @see com.ixora.rms.EntityDescriptor#canRefreshChildren()
	 */
	public boolean canRefreshChildren() {
		return fCanRefreshChildren;
	}

	/**
	 * @see com.ixora.rms.EntityDescriptor#safeToRefreshRecursivelly()
	 */
	public boolean safeToRefreshRecursivelly() {
		return fSafeToRefreshRecursivelly;
	}
}
