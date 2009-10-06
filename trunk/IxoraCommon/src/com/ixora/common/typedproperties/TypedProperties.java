/*
 * Created on Jan 25, 2004
 */
package com.ixora.common.typedproperties;

import java.awt.Color;
import java.io.File;
import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Set;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ixora.common.exception.AppRuntimeException;
import com.ixora.common.typedproperties.exception.InvalidPropertyValue;
import com.ixora.common.typedproperties.exception.PropertyNotFound;
import com.ixora.common.typedproperties.exception.PropertyTypeMismatch;
import com.ixora.common.typedproperties.exception.PropertyValueNotSet;
import com.ixora.common.typedproperties.exception.VetoException;
import com.ixora.common.utils.Utils;
import com.ixora.common.xml.XMLExternalizable;
import com.ixora.common.xml.XMLUtils;
import com.ixora.common.xml.exception.XMLAttributeMissing;
import com.ixora.common.xml.exception.XMLException;

/**
 * This class manages a set of typed properties.
 * @author Daniel Moraru
 */
public class TypedProperties extends Observable implements Cloneable, XMLExternalizable {
	// value types
	public static final int TYPE_STRING = 0;
	public static final int TYPE_INTEGER = 1;
	public static final int TYPE_FLOAT = 2;
	public static final int TYPE_BOOLEAN = 3;
	public static final int TYPE_COLOR = 4;
	public static final int TYPE_DATE = 5;
	public static final int TYPE_SERIALIZABLE = 6;
	public static final int TYPE_PERCENTAGE = 7;
	public static final int TYPE_FILE = 8;
	public static final int TYPE_XML_EXTERNALIZABLE = 9;
	public static final int TYPE_SECURE_STRING = 10;

	/** Properties. It's a linked map to preserve the order of the props */
	protected LinkedHashMap<String, PropertyEntry> props;

	/**
     * Creates a property entry.
     * @param key
     * @param visible
     * @param set
     * @param type
     * @param required
     * @return
     */
    private static PropertyEntry createEntry(String key,
            boolean visible, Object[] set, int type,
            boolean required,
            String extendedEditorClass) {
        switch(type) {
        	case TYPE_STRING:
        	    return new PropertyEntryString(key, visible, (String[])set, required);
        	case TYPE_INTEGER:
        	    return new PropertyEntryInt(key, visible, (Integer[])set, required);
        	case TYPE_FLOAT:
        	    return new PropertyEntryFloat(key, visible, (Float[])set, required);
        	case TYPE_PERCENTAGE:
        	    return new PropertyEntryPercentage(key, visible, (Float[])set, required);
        	case TYPE_BOOLEAN:
        	    return new PropertyEntryBoolean(key, visible, (Boolean[])set, required);
        	case TYPE_DATE:
        	    return new PropertyEntryDate(key, visible, (Date[])set, required);
        	case TYPE_COLOR:
        	    return new PropertyEntryColor(key, visible, (Color[])set, required);
        	case TYPE_FILE:
        	    return new PropertyEntryFile(key, visible, (File[])set, required);
        	case TYPE_SERIALIZABLE:
        	    return new PropertyEntrySerializable(key, visible, (Serializable[])set, required, extendedEditorClass);
        	case TYPE_XML_EXTERNALIZABLE:
        	    return new PropertyEntryXMLExternalizable(key, visible, (XMLExternalizable[])set, required, extendedEditorClass);
        	case TYPE_SECURE_STRING:
        	    return new PropertyEntrySecureString(key, visible, required);
        	default:
        	    return null;
        }
    }

    /**
     * Creates an entry from the given node.
     * @param type
     * @param node
     * @return
     * @throws XMLException
     */
    private static PropertyEntry createEntry(int type,
            Node node) throws XMLException {
        PropertyEntry ret = null;
        switch(type) {
        	case TYPE_STRING:
        	    ret = new PropertyEntryString();
        	    ret.fromXML(node);
        	    break;
        	case TYPE_INTEGER:
        	    ret = new PropertyEntryInt();
        	    ret.fromXML(node);
        	    break;
        	case TYPE_FLOAT:
        	    ret = new PropertyEntryFloat();
        	    ret.fromXML(node);
        	    break;
        	case TYPE_PERCENTAGE:
        	    ret = new PropertyEntryPercentage();
        	    ret.fromXML(node);
        	    break;
        	case TYPE_BOOLEAN:
        	    ret = new PropertyEntryBoolean();
        	    ret.fromXML(node);
        	    break;
        	case TYPE_DATE:
        	    ret = new PropertyEntryDate();
        	    ret.fromXML(node);
        	    break;
        	case TYPE_COLOR:
        	    ret = new PropertyEntryColor();
        	    ret.fromXML(node);
        	    break;
        	case TYPE_FILE:
        	    ret = new PropertyEntryFile();
        	    ret.fromXML(node);
        	    break;
        	case TYPE_SERIALIZABLE:
        	    ret = new PropertyEntrySerializable();
        	    ret.fromXML(node);
        	    break;
        	case TYPE_XML_EXTERNALIZABLE:
        	    ret = new PropertyEntryXMLExternalizable();
        	    ret.fromXML(node);
        	    break;
        	case TYPE_SECURE_STRING:
        	    ret = new PropertyEntrySecureString();
        	    ret.fromXML(node);
        	    break;
        }
        return ret;
    }

    /**
	 * Constructor.
	 */
	public TypedProperties() {
		super();
		props = new LinkedHashMap<String, PropertyEntry>();
	}

	/**
	 * @see com.ixora.common.xml.XMLExternalizable#toXML(org.w3c.dom.Node)
	 */
	public void toXML(Node parent) throws XMLException {
		Document doc = parent.getOwnerDocument();
		Element el;
		PropertyEntry value;
		for(Iterator iter = props.values().iterator(); iter.hasNext();) {
			value = (PropertyEntry)iter.next();
			value.toXML(parent);
		}
	}

	/**
	 * @see com.ixora.common.xml.XMLExternalizable#fromXML(org.w3c.dom.Node)
	 */
	public void fromXML(Node node) throws XMLException {
		NodeList nl = node.getChildNodes();
		if(nl == null || nl.getLength() == 0) {
			return;
		}
		Node n;
		int length = nl.getLength();
		List lst = new LinkedList();
		for(int i = 0; i < length; i++) {
			n = nl.item(i);
			if(n.getNodeType() == Node.ELEMENT_NODE) {
				lst.add(n);
			}
		}
		length = lst.size();
		if(length == 0) {
			return;
		}
		for(int i = 0; i < length; i++) {
			n = (Node)lst.get(i);
			Attr a = XMLUtils.findAttribute(n, "type");
			if(a == null) {
				throw new XMLAttributeMissing("type");
			}
			int type = Integer.parseInt(a.getValue());
			PropertyEntry entry = createEntry(type, n);
			this.props.put(entry.getProperty(), entry);
		}
	}

	/**
	 * @param key
	 * @return
	 * @throws PropertyNotFound if the given property cannot be found
	 */
	public Object getValue(String key) {
		return getValueObject(key);
	}

	/**
	 * @return a read only map containing all entries
	 */
	public Map<String, PropertyEntry> getEntries() {
		return Collections.unmodifiableMap(props);
	}

	/**
	 * Sets the date value.
	 * @param key
	 * @param date
	 */
	public void setDate(String key, Date date) {
	    setObject(key, date);
	}

	/**
	 * Sets the file value.
	 * @param key
	 * @param value
	 */
	public void setFile(String key, File value) {
	    setObject(key, value);
	}

	/**
	 * Sets the value for the given property.
	 * @param key
	 * @param value
	 */
	public void setString(String key, String value) {
	    setObject(key, value);
	}

	/**
	 * Sets the value for the given property.
	 * @param key
	 * @param value
	 */
	public void setColor(String key, Color value) {
	    setObject(key, value);
	}

	/**
	 * Sets the value for the given property.
	 * @param key
	 * @param value
	 */
	public void setInt(String key, int value) {
	    setObject(key, new Integer(value));
	}

	/**
	 * Sets the value for the given property.
	 * @param key
	 * @param value
	 */
	public void setBoolean(String key, boolean value) {
	    setObject(key, Boolean.valueOf(value));
	}

	/**
	 * Sets the value for the given property.
	 * @param key
	 * @param value
	 */
	public void setFloat(String key, float value) {
	    setObject(key, new Float(value));
	}

	/**
	 * Sets the value for the given property.
	 * @param key
	 * @param value
	 * @throws PropertyTypeMismatch if the value object is not
	 * of the type required by the given property
	 * @throws PropertyNotFound
	 */
	public void setObject(String key, Object value) {
		PropertyEntry pe = this.props.get(key);
		if(pe != null) {
			if(pe.setValue(value)) {
				setChanged();
				notifyObservers(key);
			}
		} else {
		    throw new PropertyNotFound(key);
		}
	}

	/**
	 * Sets the default value for the given property
	 * @param key
	 * @param value
	 * @throws PropertyTypeMismatch
	 * @throws PropertyNotFound
	 */
	public void setDefaultValue(String key, Object value) {
		PropertyEntry pe = this.props.get(key);
		if(pe != null) {
			pe.setDefaultValue(value);
		} else {
		    throw new PropertyNotFound(key);
		}
	}

	/**
	 * @param key
	 * @return the entry for the given key
	 */
	public PropertyEntry getEntry(String key) {
		return this.props.get(key);
	}

	/**
	 * @param key
	 * @return the entry at the given position
	 * @throws IllegalArgumentException if idx is not in the correct range
	 */
	public PropertyEntry getEntryAt(int idx) {
		int i = 0;
		for(Iterator<PropertyEntry> iter = props.values().iterator(); iter.hasNext(); ++i) {
			if(i == idx) {
				return iter.next();
			} else {
				iter.next();
			}
		}
		throw new IllegalArgumentException("invalid index value");
	}

	/**
	 * @return the set of keys
	 */
	public Set<String> keys() {
		return Collections.unmodifiableMap(this.props).keySet();
	}

	/**
	 * Sets the current values to the default values.
	 */
	public void setDefaults() {
		for(String key : props.keySet()) {
			PropertyEntry pe = props.get(key);
			if(pe.restoreDefault()) {
				setChanged();
				notifyObservers(key);
			}
		}
	}

    /**
     * Subclasses could veto the set of properties here.
     * @throws VetoException
     */
    public void veto() throws VetoException {
        ; // default is ok
    }

	/**
	 * @see java.lang.Object#clone()
	 */
	public Object clone() {
        try {
            // need deep cloning because...
            // we need a clean set of observers as this is an
            // independent object which will have its own
            // observers and since the vector in the superclass
            // is private this is the only way
            TypedProperties conf = (TypedProperties)Utils.deepClone(this);
            conf.deleteObservers();
    		return conf;
        } catch (Exception e) {
            throw new AppRuntimeException(e);
        }
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if(obj == null) {
			return false;
		}
		if(this == obj) {
			return true;
		}
		if(!(obj instanceof TypedProperties)) {
			return false;
		}
		TypedProperties that = (TypedProperties)obj;
		// props can't be null
		return this.props.equals(that.props);
	}

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        StringBuffer buff = new StringBuffer();
        for(PropertyEntry entry : props.values()) {
            buff.append("[");
            buff.append(entry);
            buff.append("]");
        }
        return buff.toString();
    }

	/**
	 * @param key
	 * @param type
	 * @param visible
	 * @return
	 */
	public PropertyEntry setProperty(
			String key, int type, boolean visible) {
	    return setProperty(key, type, visible, true, null);
	}

	/**
	 * @param key
	 * @param type
	 * @param visible
	 * @param required
	 * @return
	 */
	public PropertyEntry setProperty(
			String key, int type, boolean visible, boolean required) {
	    return setProperty(key, type, visible, required, null);
	}

	/**
	 * @param key
	 * @param type
	 * @param visible
	 * @param set
	 * @return
	 */
	public PropertyEntry setProperty(
			String key, int type, boolean visible, Serializable[] set) {
	    return setProperty(key, type, visible, true, set);
	}

	/**
	 * @param prop
	 * @return
	 */
	public boolean hasProperty(String prop) {
		return props.get(prop) != null;
	}

	/**
	 * @param key
	 * @param type
	 * @param visible
	 * @param required
	 * @param set
	 * @return
	 */
	public PropertyEntry setProperty(
			String key,
			int type,
			boolean visible,
			boolean required,
			Serializable[] set) {
	    PropertyEntry ret = createEntry(key, visible, set, type, required, null);
		this.props.put(key, ret);
		return ret;
	}

	/**
	 * @param key
	 * @param type
	 * @param visible
	 * @param required
	 * @param set
	 * @return
	 * @throws NoSuchMethodException
	 * @throws ClassNotFoundException
	 * @throws SecurityException
	 */
	public PropertyEntry setProperty(
			String key,
			int type,
			boolean visible,
			boolean required,
			Serializable[] set,
			String extendedEditorClass) {
	    PropertyEntry ret = createEntry(
	            key, visible, set, type, required,
	            extendedEditorClass);
		this.props.put(key, ret);
		return ret;
	}

	/**
	 * @param property
	 * @return an int property
	 * @throws PropertyNotFound if the given property is not found
	 */
	public Object getObject(String property) {
	    return getValueObject(property);
	}

	/**
	 * @param property
	 * @return a boolean property
	 * @throws PropertyNotFound if the given property is not found
	 * @throws PropertyValueNotSet if the given property hasn't been assigned a value
	 */
	public boolean getBoolean(String property) {
		return ((Boolean)getValueNonNullObject(property)).booleanValue();
	}

	/**
	 * @param property
	 * @return an int property
	 * @throws PropertyNotFound if the given property is not found
	 * @throws PropertyValueNotSet  if the given property hasn't been assigned a value
	 */
	public int getInt(String property) {
	    return ((Integer)getValueNonNullObject(property)).intValue();
	}

	/**
	 * @param property
	 * @return a string property value or rather than throwing
     * PropertyValueNotSet if the given property hasn't been assigned a value
     * an empty string will be returned
	 * @throws PropertyNotFound if the given property is not found
	 */
	public String getString(String property) {
        try {
            return (String)getValueNonNullObject(property);
        } catch(PropertyValueNotSet e) {
            return "";
        }
	}

	/**
	 * @param property
	 * @return a float property
	 * @throws PropertyNotFound if the given property is not found
     * @throws PropertyValueNotSet if the given property hasn't been assigned a value
	 */
	public float getFloat(String property) {
		return ((Float)getValueNonNullObject(property)).floatValue();
	}

	/**
	 * @param property
	 * @return a color property value or null if the value is not set
	 * @throws PropertyNotFound if the given property is not found
     * @throws PropertyValueNotSet if the given property hasn't been assigned a value
	 */
	public Color getColor(String property) {
	    return (Color)getValueNonNullObject(property);
	}

	/**
	 * @param property
	 * @return a date property value or null if the value is not set
	 * @throws PropertyNotFound if the given property is not found
     * @throws PropertyValueNotSet if the given property hasn't been assigned a value
	 */
	public Date getDate(String property) {
	    return (Date)getValueNonNullObject(property);
	}

	/**
	 * @param property
	 * @return a file property value or null if the value is not set
	 * @throws PropertyNotFound if the given property is not found
     * @throws PropertyValueNotSet if the given property hasn't been assigned a value
	 */
	public File getFile(String property) {
	    return (File)getValueNonNullObject(property);
	}

	/**
	 * Applies the new values found in the given property.
	 * @param other
	 */
	public void apply(TypedProperties other) throws InvalidPropertyValue {
		for(String key : props.keySet()) {
			PropertyEntry peOther = other.getEntry(key);
			if(peOther != null) {
				PropertyEntry pe = this.props.get(key);
				if(pe != null
						&& pe.setValue(peOther.getValue())) {
					setChanged();
					notifyObservers(key);
				}
			}
		}
	}

	/**
	 * @param property
	 * @return
	 * @throws PropertyNotFound if the given property is not found
	 * @throws PropertyValueNotSet
	 */
	private Object getValueObject(String property) {
		PropertyEntry pe = this.props.get(property);
		if(pe == null) {
			throw new PropertyNotFound(property);
		}
        return pe.getValue();
	}

	/**
	 * @param property
	 * @return
	 * @throws PropertyNotFound if the given property is not found
	 * @throws PropertyValueNotSet
	 */
	private Object getValueNonNullObject(String property) {
		PropertyEntry pe = this.props.get(property);
		if(pe == null) {
			throw new PropertyNotFound(property);
		}
        Object obj = pe.getValue();
        if(obj == null) {
            throw new PropertyValueNotSet(property);
        }
        return obj;
	}
}
