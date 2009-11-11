package com.ixora.rms;

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
 * Immmutable class that describes the counter of a monitored
 * entity. Each entity has a set of counters and each counter
 * is described by an instance of this classs.
 * @author Daniel Moraru
 * @author Cristian Costache
 */
public class CounterDescriptorImpl
	extends MonitoringDescriptorImpl implements CounterDescriptor {
	private static final long serialVersionUID = -8849618717358474308L;
	/** Counter id */
	protected CounterId fCounterId;
	/** True if counter has discreet (non-continuous) values */
	protected boolean fDiscreet;
	/** One of the types defined in CounterDescriptor */
	protected CounterType fType;
	/** Used only by counters that require specialized viewers */
	protected String fViewboardClassName;

	/**
	 * CounterDescriptor constructor.
	 * @param id
	 * @param fDescription
	 * @param type
	 * @param discreet
	 */
	public CounterDescriptorImpl(
			CounterDescriptor cd) {
		this(cd.getId(), cd.getAlternateName(),
			cd.getDescription(), cd.getType(),
			cd.isDiscreet(), cd.getLevel(), cd.getViewboardClassName());
	}

	/**
	 * CounterDescriptor constructor.
	 * @param id
	 * @param description
	 * @param type
	 * @param discreet
	 */
	public CounterDescriptorImpl(
			CounterId id,
			String description,
			CounterType type,
			boolean discreet) {
		this(id, description, type, discreet, null);
	}

	/**
	 * CounterDescriptor constructor.
	 * @param id
	 * @param description
	 * @param type
	 * @param discreet
	 * @param level
	 */
	public CounterDescriptorImpl(
			CounterId id,
			String description,
			CounterType type,
			boolean discreet,
			MonitoringLevel level) {
		this(id, null, description, type, discreet, level);
	}

	/**
	 * CounterDescriptor constructor.
	 * @param id
	 * @param alternateName
	 * @param description
	 * @param type
	 * @param discreet
	 * @param level
	 */
	public CounterDescriptorImpl(
			CounterId id,
			String alternateName,
			String description,
			CounterType type,
			boolean discreet,
			MonitoringLevel level) {
		this(id, alternateName, description, type, discreet, level, null);
	}

	/**
	 * CounterDescriptor constructor.
	 * @param id
	 * @param alternateName
	 * @param description
	 * @param type
	 * @param discreet
	 * @param level
	 * @param viewBoardClass
	 */
	public CounterDescriptorImpl(
			CounterId id,
			String alternateName,
			String description,
			CounterType type,
			boolean discreet,
			MonitoringLevel level,
			String viewBoardClass) {
		super();
		this.fCounterId = id;
		this.fName = id.toString();
		this.fAlternateName = alternateName;
		this.fDescription = description;
		this.fType = type;
		if(fType == null) {
			throw new IllegalArgumentException("Counter type cannot be null");
		}
		this.fDiscreet = discreet;
		this.fLevel = level;
		this.fViewboardClassName = viewBoardClass;
	}

	/**
     * Constructor to support XML.
     */
    public CounterDescriptorImpl() {
    }

    /**
	 * @return the counter id
	 */
	public CounterId getId() {
		return this.fCounterId;
	}

	/**
	 * @return this counter's data type
	 */
	public CounterType getType() {
		return fType;
	}

	/**
	 * @return if counter has discreet (non-continuous) values
	 */
	public boolean isDiscreet() {
		return fDiscreet;
	}

	/**
	 * This has to be final to make sure that the subclasses
	 * will not redefine this method.
	 * @see java.lang.Object#clone()
	 */
	public final Object clone() {
		CounterDescriptorImpl cd = new CounterDescriptorImpl(
				fCounterId,
				fAlternateName,
				fDescription,
				fType,
				fDiscreet,
				fLevel,
				fViewboardClassName);
		cd.fEnabled = fEnabled;
		return cd;
	}

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj) {
        if(obj == this) {
            return true;
        }
        if(obj == null || !(obj instanceof CounterDescriptorImpl)) {
            return false;
        }
        CounterDescriptorImpl that = (CounterDescriptorImpl)obj;
        return fCounterId.equals(that.fCounterId)
        	&& fDiscreet == that.fDiscreet
            && fEnabled == that.fEnabled
            && Utils.equals(fViewboardClassName, that.fViewboardClassName)
            && super.equals(obj);
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        int hc = super.hashCode();
        hc ^= new Boolean(this.fDiscreet).hashCode();
        hc ^= new Boolean(this.fEnabled).hashCode();
        hc ^= fCounterId.hashCode();
        if(fViewboardClassName != null) {
        	hc ^= fViewboardClassName.hashCode();
        }
        return hc;
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
        fCounterId = new CounterId(ida.getNodeValue());
        fName = fCounterId.toString();
        Node discreetn = XMLUtils.findChild(node, "discreet");
        if(discreetn == null) {
            throw new XMLNodeMissing("discreet");
        }
        String discreets = XMLUtils.getText(discreetn);
        if(discreets == null) {
            throw new XMLTextNodeMissing("discreet");
        }
        fDiscreet = Boolean.valueOf(discreets).booleanValue();
        Node typen = XMLUtils.findChild(node, "type");
        if(typen == null) {
            throw new XMLNodeMissing("type");
        }
        String types = XMLUtils.getText(typen);
        if(types == null) {
            throw new XMLTextNodeMissing("type");
        }
        fType = CounterType.resolve(Integer.parseInt(types));
        if(fType == null) {
        	throw new XMLException("Invalid value for counter type: " + types);
        }
        Node viewBoardNode = XMLUtils.findChild(node, "viewboard");
        if(viewBoardNode != null) {
        	fViewboardClassName = XMLUtils.getText(viewBoardNode);
        }
    }
    /**
     * @see com.ixora.common.xml.XMLExternalizable#toXML(org.w3c.dom.Node)
     */
    public void toXML(Node parent) throws XMLException {
        super.toXML(parent);
        Document doc = parent.getOwnerDocument();
        Attr ida = doc.createAttribute("id");
        ida.setNodeValue(fCounterId.toString());
        parent.getAttributes().setNamedItem(ida);
        Element discreete = doc.createElement("discreet");
        parent.appendChild(discreete);
        discreete.appendChild(doc.createTextNode(String.valueOf(fDiscreet)));
        Element typee = doc.createElement("type");
        parent.appendChild(typee);
        typee.appendChild(doc.createTextNode(String.valueOf(fType.getKey())));
        if(fViewboardClassName != null) {
        	Element viewboard = doc.createElement("viewboard");
        	parent.appendChild(viewboard);
        	viewboard.appendChild(doc.createTextNode(fViewboardClassName));
        }
    }

	/**
	 * Debug only.
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return fCounterId != null ? fCounterId.toString() : super.toString();
	}

	/**
	 * @see com.ixora.rms.CounterDescriptor#getViewboardClassName()
	 */
	public String getViewboardClassName() {
		return fViewboardClassName;
	}
}
