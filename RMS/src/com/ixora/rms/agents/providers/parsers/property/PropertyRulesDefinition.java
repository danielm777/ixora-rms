/*
 * Created on 11-Jun-2005
 */
package com.ixora.rms.agents.providers.parsers.property;



import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.ixora.rms.ResourceId;
import com.ixora.common.utils.Utils;
import com.ixora.common.xml.XMLUtils;
import com.ixora.common.xml.exception.XMLException;
import com.ixora.common.xml.exception.XMLNodeMissing;
import com.ixora.rms.CounterId;
import com.ixora.rms.EntityId;
import com.ixora.rms.agents.providers.parsers.MonitoringDataParsingRulesDefinition;
import com.ixora.rms.providers.parsers.exception.InvalidParsingRules;

/**
 * PropertiesRulesDefinition.
 */
public final class PropertyRulesDefinition implements
        MonitoringDataParsingRulesDefinition {
    /** Whether or not to accumulate volatile entities */
    private boolean fAccumulateVolatileEntities;
    /** Counters provided */
    private Set<ResourceId> fCounters;
    /** String that is used to indend entities */
    private String fEntityIndentation;
    /** Base entity */
    private EntityId fBaseEntity;
    /** Property value regex */
    private String fPropertyValueRegex;
    /** Entity regex */
    private String fEntityRegex;
    /** True if the value should be extracted from the second regex group */
    private boolean fValueIsSecondMatch;
    /** Ignore lines that matching this regex */
    private String fIgnoreLines;

    /**
     * Constructor to support XML.
     */
    public PropertyRulesDefinition() {
        super();
        fCounters = new LinkedHashSet<ResourceId>();
    }

    /**
     * Constructor.
     * @param counters
     * @param entityInd
     * @param baseEntity
     * @param entityRegex
     * @param propValRegex
     * @param valueIsSecondMatch
     * @param accVolatileEntities
     * @param ignoreLines
     * @throws InvalidParsingRules
     */
    public PropertyRulesDefinition(
            Set<ResourceId> counters,
            String entityInd,
            String baseEntity,
            String entityRegex,
            String propValRegex,
            boolean valueIsSecondMatch,
            boolean accVolatileEntities,
            String ignoreLines) throws InvalidParsingRules {
        super();
        fCounters = new LinkedHashSet<ResourceId>(counters);
        // check they are all counters
        for(ResourceId c : fCounters) {
            if(c.getRelativeRepresentation() != ResourceId.COUNTER) {
                // TODO localize
                throw new InvalidParsingRules("Entry " + c.toString() + " must be a counter");
            }
            EntityId eid = c.getEntityId();
            if(eid == null || eid.equals(ResourceId.EMPTY_ENTITY)) {
                // TODO localize
                throw new InvalidParsingRules("Entity ID is missing for entry " + c.toString());
            }
        }

        fEntityIndentation = entityInd;
        try {
            fBaseEntity = new EntityId(baseEntity);
        } catch(Exception e) {
            fBaseEntity = null;
        }

        fPropertyValueRegex = propValRegex;
        // check is valid
        if(!Utils.isEmptyString(fPropertyValueRegex)) {
            Pattern.compile(fPropertyValueRegex);
        }

        fEntityRegex = entityRegex;
        // check valid
        if(!Utils.isEmptyString(fEntityRegex)) {
            Pattern.compile(fEntityRegex);
        }
        fValueIsSecondMatch = valueIsSecondMatch;
        fAccumulateVolatileEntities = accVolatileEntities;
        fIgnoreLines = ignoreLines;
    }

    /**
     * @see com.ixora.rms.agents.providers.parsers.MonitoringDataParsingRulesDefinition#getEntities()
     */
    public Set<EntityId> getEntities() {
        Set<EntityId> ret = new LinkedHashSet<EntityId>();
        for(ResourceId cid : fCounters) {
            ResourceId eid = cid.getSubResourceId(ResourceId.ENTITY);
            if(eid != null) {
                ret.add(eid.getEntityId());
            }
        }
        return ret;
    }

    /**
     * @see com.ixora.rms.agents.providers.parsers.MonitoringDataParsingRulesDefinition#getCounters(com.ixora.rms.EntityId)
     */
    public Set<CounterId> getCounters(EntityId e) {
        Set<CounterId> ret = new HashSet<CounterId>();
        for(ResourceId cid : fCounters) {
            EntityId eid = cid.getSubResourceId(ResourceId.ENTITY).getEntityId();
            if(eid != null) {
                if(eid.equals(e)) {
                    CounterId id = cid.getCounterId();
                    if(id != null) {
                        ret.add(id);
                    }
                }
            }
        }
        return ret;
    }

    /**
     * @see com.ixora.rms.agents.providers.parsers.MonitoringDataParsingRulesDefinition#accumulateVolatileEntities()
     */
    public boolean accumulateVolatileEntities() {
        return false;
    }

    /**
     * @see com.ixora.common.xml.XMLExternalizable#toXML(org.w3c.dom.Node)
     */
    public void toXML(Node parent) throws XMLException {
        Document doc = parent.getOwnerDocument();
        Element el = doc.createElement("rules");
        parent.appendChild(el);
        Element el2;
        el2 = doc.createElement("counters");
        el.appendChild(el2);
        for(ResourceId cid : fCounters) {
            Element el3 = doc.createElement("counter");
            el2.appendChild(el3);
            el3.appendChild(doc.createTextNode(cid.toString()));
        }
        if(fEntityIndentation != null) {
            el2 = doc.createElement("entityIndentation");
            el.appendChild(el2);
            el2.appendChild(doc.createTextNode(fEntityIndentation));
        }
        if(fBaseEntity != null) {
            el2 = doc.createElement("baseEntity");
            el.appendChild(el2);
            el2.appendChild(doc.createTextNode(fBaseEntity.toString()));
        }
        if(!Utils.isEmptyString(fPropertyValueRegex)) {
            el2 = doc.createElement("propertyValueRegex");
            el.appendChild(el2);
            el2.appendChild(doc.createTextNode(fPropertyValueRegex));
        }
        if(!Utils.isEmptyString(fEntityRegex)) {
            el2 = doc.createElement("entityRegex");
            el.appendChild(el2);
            el2.appendChild(doc.createTextNode(fEntityRegex));
        }
        el2 = doc.createElement("isValueSecondMatch");
        el.appendChild(el2);
        el2.appendChild(doc.createTextNode(String.valueOf(fValueIsSecondMatch)));
        el2 = doc.createElement("accumulateVolatileEntities");
        el.appendChild(el2);
        el2.appendChild(doc.createTextNode(String.valueOf(fAccumulateVolatileEntities)));
        if(!Utils.isEmptyString(fIgnoreLines)) {
	        el2 = doc.createElement("ignoreLines");
	        el.appendChild(el2);
	        el2.appendChild(doc.createTextNode(fIgnoreLines));
        }
    }

    /**
     * @see com.ixora.common.xml.XMLExternalizable#fromXML(org.w3c.dom.Node)
     */
    public void fromXML(Node node) throws XMLException {
        Node n = XMLUtils.findChild(node, "counters");
        if(n == null) {
            throw new XMLNodeMissing("counters");
        }
        List<Node> nl = XMLUtils.findChildren(n, "counter");
        if(nl.size() == 0) {
            throw new XMLNodeMissing("counter");
        }
        for(Node n2 : nl) {
            String txt = XMLUtils.getText(n2);
            if(!Utils.isEmptyString(txt)) {
                ResourceId cid = new ResourceId(txt);
                fCounters.add(cid);
            }
        }
        n = XMLUtils.findChild(node, "baseEntity");
        if(n != null) {
            String be = XMLUtils.getText(n);
            if(!Utils.isEmptyString(be)) {
                this.fBaseEntity = new EntityId(be);
            }
        }
        n = XMLUtils.findChild(node, "entityIndentation");
        if(n != null) {
            String ei = XMLUtils.getText(n);
            if(!Utils.isEmptyString(ei)) {
                this.fEntityIndentation = ei;
            }
        }
        n = XMLUtils.findChild(node, "entityRegex");
        if(n != null) {
            String ei = XMLUtils.getText(n);
            if(!Utils.isEmptyString(ei)) {
                this.fEntityRegex = ei;
            }
        }
        n = XMLUtils.findChild(node, "propertyValueRegex");
        if(n != null) {
            String pvs = XMLUtils.getText(n);
            if(!Utils.isEmptyString(pvs)) {
                this.fPropertyValueRegex = pvs;
            }
        }
        n = XMLUtils.findChild(node, "isValueSecondMatch");
        if(n != null) {
            String ave = XMLUtils.getText(n);
            if(!Utils.isEmptyString(ave)) {
                this.fValueIsSecondMatch = Boolean.valueOf(ave);
            }
        }
        n = XMLUtils.findChild(node, "accumulateVolatileEntities");
        if(n != null) {
            String ave = XMLUtils.getText(n);
            if(!Utils.isEmptyString(ave)) {
                this.fAccumulateVolatileEntities = Boolean.valueOf(ave);
            }
        }
        n = XMLUtils.findChild(node, "ignoreLines");
        if(n != null) {
            String il = XMLUtils.getText(n);
            if(!Utils.isEmptyString(il)) {
                this.fIgnoreLines = il;
            }
        }
    }

    /**
     * @return
     */
    public Set<ResourceId> getResourceIds() {
       return Collections.unmodifiableSet(fCounters);
    }

    /**
     * @return the entity indentation string.
     */
    public String getEntityIndentation() {
        return fEntityIndentation;
    }

    /**
     * @return the regex used to find entities.
     */
    public String getEntityRegex() {
        return fEntityRegex;
    }

    /**
     * @return the regex used to find properties and values.
     */
    public String getPropertyValueRegex() {
        return fPropertyValueRegex;
    }

    /**
     * @return the base entity.
     */
    public EntityId getBaseEntity() {
        return fBaseEntity;
    }

    /**
     * @return the pattern for lines to ignore.
     */
    public String getIgnoreLines() {
        return fIgnoreLines;
    }

    /**
     * @return
     */
    public boolean isValueSecondMatch() {
        return fValueIsSecondMatch;
    }
}
