/*
 * Created on 06-Jan-2004
 */
package com.ixora.rms;

import java.io.Serializable;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.ixora.common.utils.Utils;
import com.ixora.common.xml.XMLUtils;
import com.ixora.common.xml.exception.XMLException;
import com.ixora.common.xml.exception.XMLNodeMissing;
import com.ixora.common.xml.exception.XMLTextNodeMissing;


/**
 * Immmutable class that describes a monitored entity.
 * @author Daniel Moraru
 */
public abstract class MonitoringDescriptorImpl
	implements Serializable, Cloneable, MonitoringDescriptor  {
	private static final long serialVersionUID = -8345434807739801779L;
	/** Monitoring entity name key */
	protected String fName;
	/** Monitoring entity description key */
	protected String fDescription;
	/**
	 * Monitoring entity alternate name.
	 * Used as the last alternative for a translated
	 * name if no translated value is found for <code>name</code>
	 * in the message repository.
	 * Used for agents that provide both a name key as well as
	 * a translated value, which means we don't necessarilly provide
	 * a message file. In this case by using the name key in the
	 * queries and the alternate name in UI we can used the queries
	 * for any languages provided by the agent.
	 */
	protected String fAlternateName;
	/** True if this monitoring entity is enabled */
	protected boolean fEnabled;
	/**
	 * Monitoring level to which this monitoring entity belongs to.
	 * Note: This is not the current level of this entity.
	 */
	protected MonitoringLevel fLevel;

	/**
	 * Constructor.
	 */
	protected MonitoringDescriptorImpl() {
	}
	/**
	 * Constructor.
	 * @param name
	 * @param description
	 * @param enabled
	 * @param level
	 */
	protected MonitoringDescriptorImpl(
			String name,
			String description,
			boolean enabled,
			MonitoringLevel level) {
		this(name, null, description, enabled, level);
	}

	/**
	 * Constructor.
	 * @param name
	 * @param alternateName
	 * @param description
	 * @param enabled
	 * @param level
	 */
	protected MonitoringDescriptorImpl(
			String name,
			String alternateName,
			String description,
			boolean enabled,
			MonitoringLevel level) {
		super();
		this.fName = name;
		this.fAlternateName = alternateName;
		this.fDescription = description;
		this.fEnabled = enabled;
		this.fLevel = level;
	}

	/**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj) {
        if(obj == this) {
            return true;
        }
        if(obj == null || !(obj instanceof MonitoringDescriptorImpl)) {
            return false;
        }
        MonitoringDescriptorImpl that = (MonitoringDescriptorImpl)obj;
        return fEnabled == that.fEnabled
            && Utils.equals(fLevel, that.fLevel)
            && Utils.equals(fAlternateName, that.fAlternateName)
            && Utils.equals(fName, that.fName)
            && Utils.equals(fDescription, that.fDescription);
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        int hc = super.hashCode();
        if(this.fAlternateName != null) {
            hc ^= this.fAlternateName.hashCode();
        }
        if(this.fName != null) {
            hc ^= this.fName.hashCode();
        }
        if(this.fDescription != null) {
            hc ^= this.fDescription.hashCode();
        }
        hc ^= new Boolean(this.fEnabled).hashCode();
        if(this.fLevel != null) {
            hc ^= this.fLevel.hashCode();
        }
        return hc;
    }

    /**
	 * @see com.ixora.rms.MonitoringDescriptor#getName()
	 */
	public String getName() {
		return fName;
	}

	/**
	 * @see com.ixora.rms.MonitoringDescriptor#isEnabled()
	 */
	public boolean isEnabled() {
		return fEnabled;
	}

	/**
	 * @see com.ixora.rms.MonitoringDescriptor#getDescription()
	 */
	public String getDescription() {
		return this.fDescription;
	}

	/**
	 * @see com.ixora.rms.MonitoringDescriptor#getLevel()
	 */
	public MonitoringLevel getLevel() {
		return fLevel;
	}

	/**
	 * @param level
	 * @return true if this entity applies to the given level
	 */
	public boolean appliesToLevel(MonitoringLevel level) {
		if(level == null || this.fLevel == null ||
				this.fLevel.compareTo(level) <= 0) {
			return true;
		}
		return false;
	}

	/**
	 * @see com.ixora.rms.MonitoringDescriptor#getAlternateName()
	 */
	public String getAlternateName() {
		return fAlternateName;
	}
	  /**
	* @see com.ixora.common.xml.XMLExternalizable#fromXML(org.w3c.dom.Node)
	*/
    public void fromXML(Node node) throws XMLException {
        Node ann = XMLUtils.findChild(node, "alternatename");
        if(ann == null) {
            throw new XMLNodeMissing("alternatename");
        }
        fAlternateName = XMLUtils.getText(ann);
        Node descn = XMLUtils.findChild(node, "description");
        if(descn == null) {
            throw new XMLNodeMissing("description");
        }
        fDescription = XMLUtils.getText(descn);
        Node leveln = XMLUtils.findChild(node, "level");
        if(leveln != null) {
            String levels = XMLUtils.getText(leveln);
            if(levels == null) {
                throw new XMLTextNodeMissing("level");
            }
            fLevel = MonitoringLevel.resolve(Integer.parseInt(levels));
        }
    }
    /**
     * @see com.ixora.common.xml.XMLExternalizable#toXML(org.w3c.dom.Node)
     */
    public void toXML(Node parent) throws XMLException {
        Document doc = parent.getOwnerDocument();
        Element ane = doc.createElement("alternatename");
        parent.appendChild(ane);
        ane.appendChild(doc.createTextNode(fAlternateName));
        Element desce = doc.createElement("description");
        parent.appendChild(desce);
        desce.appendChild(doc.createTextNode(fDescription));
        if(fLevel != null) {
	        Element levele = doc.createElement("level");
	        parent.appendChild(levele);
	        levele.appendChild(doc.createTextNode(String.valueOf(fLevel.getKey())));
        }
    }
}
