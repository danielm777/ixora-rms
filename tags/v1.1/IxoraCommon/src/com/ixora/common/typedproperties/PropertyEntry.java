/*
 * Created on 30-Sep-2004
 */
package com.ixora.common.typedproperties;

import java.io.Serializable;
import java.util.StringTokenizer;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.ixora.common.typedproperties.exception.InvalidPropertyValue;
import com.ixora.common.typedproperties.exception.PropertyTypeMismatch;
import com.ixora.common.typedproperties.exception.PropertyValueNotSet;
import com.ixora.common.typedproperties.exception.PropertyValueOutOfRange;
import com.ixora.common.utils.Utils;
import com.ixora.common.xml.XMLExternalizable;
import com.ixora.common.xml.XMLUtils;
import com.ixora.common.xml.exception.XMLAttributeMissing;
import com.ixora.common.xml.exception.XMLException;


/**
 * An entry in the property map.
 * It describes the property.
 */
public abstract class PropertyEntry<T>
		implements Cloneable, Serializable, XMLExternalizable {
	private static final long serialVersionUID = -3898200359990027502L;
	/** Property name */
    protected String property;
    /** Value */
    protected T value;
	/** Default value */
    protected T defaultValue;
	/** Whether or not this property is visible to the user */
    protected boolean visible;
	/** All possible values */
    protected T[] valueSet;
	/** Value type */
    protected int type;
	/** Whether or not this property is required to have a value set */
	protected boolean required;
	/** The class implementing an extended editor for this property type */
	protected String extendedEditorClass;
	/** Whether or not this property needs translation when it's value is rendered */
	protected boolean needsTranslation;
	/** Whether or not this property is read only */
	protected boolean readOnly;

	/**
	 * Constructor to support XML.
	 */
	protected PropertyEntry() {
	    super();
	}

	/**
	 * Constructor.
	 * @param prop
	 * @param v
	 * @param set
	 * @param type
	 * @param required
	 */
	protected PropertyEntry(String prop, boolean v, T[] set, int type, boolean required) {
	    this(prop, v, set, type, required, null);
	}

	/**
	 * Constructor.
	 * @param prop
	 * @param v
	 * @param set
	 * @param type
	 * @param required
	 * @param extendedEditorClass
	 */
	protected PropertyEntry(String prop, boolean v,
	        T[] set, int type, boolean required,
	        String extendedEditorClass) {
		super();
	    this.property = prop;
	    this.visible = v;
		this.valueSet = set;
		this.type = type;
		this.required = required;
		this.extendedEditorClass = extendedEditorClass;
	}

	/**
	 * Validates the value.
	 * @param value
	 * @throws PropertyValueOutOfRange
	 * @throws PropertyTypeMismatch
	 * @throws PropertyValueNotSet
	 */
	public void validateValue(T value) {
	    // if value null and not required is ok
	    if(value == null) {
	    	if(!this.required) {
	    		return;
	    	} else {
	    		throw new PropertyValueNotSet(property);
	    	}
	    }
	    // now check the value is in the set
		if(valueSet != null) {
			for(int i = valueSet.length - 1; i >= 0; --i) {
				if(valueSet[i].equals(value)) {
					return;
				} else {
					if(i == 0) {
						throw new PropertyValueOutOfRange(makeString(value));
					}
				}
			};
		}
	}

    /**
     * @return the object represented by this property
     * or null if no value has been set
     */
    public T getValue() {
         return value;
    }

    /**
     * Subclasses should attempt to create an object from the given string.
     * @param value
     * @return
     * @throws InvalidPropertyValue
     */
	protected abstract T makeObject(String value) throws InvalidPropertyValue;

    /**
     * Subclasses should return a string representation of the given object.
     * @param value
     * @return
     * @throws PropertyTypeMismatch
     */
	protected abstract String makeString(T obj) throws PropertyTypeMismatch;

	/**
	 * @see java.lang.Object#clone()
	 */
	public final Object clone() {
		try {
			return super.clone();
		} catch(CloneNotSupportedException e) {
			throw new InternalError();
		}
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	public final boolean equals(Object obj) {
		if(obj == null) {
			return false;
		}
		if(obj == this) {
			return true;
		}
		if(!(obj instanceof PropertyEntry)) {
			return false;
		}
		PropertyEntry that = (PropertyEntry)obj;
		// interested in value only
		if((that.value == value)
			|| (that.value != null
				&& value != null
				&& that.value.equals(value))) {
			return true;
		}
		return false;
	}
    /**
     * @return the defaultValue.
     */
    public T getDefaultValue() {
        return defaultValue;
    }
    /**
     * @return
     */
    public String getProperty() {
        return property;
    }
    /**
     * @return the type.
     */
    public int getType() {
        return type;
    }
    /**
     * @return the name of the class implementing an extended editor
     * for this property type or null if none
     */
    public String getExtendedEditorClass() {
        return extendedEditorClass;
    }
    /**
     * @return the valueSet.
     */
    public T[] getValueSet() {
        return valueSet;
    }
    /**
     * @return the visible.
     */
    public boolean isVisible() {
        return visible;
    }
    /**
     * @return whether or not this property must always be set
     * i.e. must have a valid non empty value
     */
    public boolean isRequired() {
        return required;
    }
    /**
     * @return whether or not this property needs to have it's value translated
     * when rendered
     */
    public boolean needsTranslation() {
        return needsTranslation;
    }

    /**
     * @param b true if this property needs to have it's value translated
     * when rendered
     */
    public void setNeedsTranslation(boolean b) {
    	this.needsTranslation = b;
    }

    /**
     * @param date
     * @throws PropertyTypeMismatch
     * @return
     */
    public boolean setValue(T obj) {
        if(Utils.equals(obj, this.value)) {
            return false;
        }
        validateValue(obj);
        this.value = obj;
        return true;
    }
    
    /**
     * @param val
     */
    public void setValueFromString(String val) {
    	T obj = makeObject(val);
    	setValue(obj);
    }

    /**
     * @param value the value to set.
     * @throws PropertyTypeMismatch
     */
    public boolean setDefaultValue(T value) {
        if(Utils.equals(this.defaultValue, value)) {
            return false;
        }
        validateValue(value);
        this.defaultValue = value;
        return true;
    }
    /**
     * Restores the default value.
     * @return
     */
    public boolean restoreDefault() {
        if(Utils.equals(this.value, this.defaultValue)) {
            return false;
        }
        this.value = this.defaultValue;
        return true;
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return property + ": " + value;
    }

    /**
     * @see com.ixora.common.xml.XMLExternalizable#fromXML(org.w3c.dom.Node)
     */
    public void fromXML(Node node) throws XMLException {
        property = node.getNodeName();
        Attr a = XMLUtils.findAttribute(node, "notvisible");
		visible = true;
		if(a != null) {
			visible = !Boolean.valueOf(a.getValue()).booleanValue();
		}
		a = XMLUtils.findAttribute(node, "extended_editor");
		if(a != null) {
			extendedEditorClass = a.getValue();
		}
		a = XMLUtils.findAttribute(node, "notrequired");
		required = true;
		if(a != null) {
			required = !Boolean.valueOf(a.getValue()).booleanValue();
		}
		a = XMLUtils.findAttribute(node, "translate");
		if(a != null) {
			needsTranslation = Boolean.valueOf(a.getValue()).booleanValue();
		}
		a = XMLUtils.findAttribute(node, "set");
		if(a != null) {
			this.valueSet = parseSetValues(a.getValue());
		}
		try {
			a = XMLUtils.findAttribute(node, "default");
			if(a != null) {
				setDefaultValue(makeObject(a.getValue()));
			}
			a = XMLUtils.findAttribute(node, "value");
			if(a == null) {
				throw new XMLAttributeMissing("value");
			}
			String sv = a.getValue();
			if((sv == null || sv.length() == 0) && required) {
			    throw new PropertyValueNotSet(property);
			}
			setValue(makeObject(a.getValue()));
		} catch(InvalidPropertyValue e) {
			new XMLException(e);
		}
    }
    /**
     * @see com.ixora.common.xml.XMLExternalizable#toXML(org.w3c.dom.Node)
     */
    public void toXML(Node parent) throws XMLException {
        Document doc = parent.getOwnerDocument();
        Element el = doc.createElement(property);
        parent.appendChild(el);
		el.setAttribute("type", String.valueOf(type));
		if(this.extendedEditorClass != null) {
		    el.setAttribute("extended_editor", this.extendedEditorClass);
		}
		if(!this.visible) {
			el.setAttribute("notvisible", Boolean.TRUE.toString());
		}
		if(this.needsTranslation) {
			el.setAttribute("translate", Boolean.TRUE.toString());
		}
		if(!this.required) {
			el.setAttribute("notrequired", Boolean.TRUE.toString());
		}
		if(this.valueSet != null) {
			el.setAttribute("set", valuesToString(this.valueSet));
		}
		if(this.defaultValue != null) {
			el.setAttribute("default", makeString(this.defaultValue));
		}
		el.setAttribute("value", makeString(this.value));
    }

	/**
	 * @param vals
	 * @return
	 */
	private String valuesToString(T[] vals) {
		StringBuffer buff = new StringBuffer();
		for(int i = 0; i < vals.length; i++) {
			buff.append(makeString(vals[i]));
			if(i != vals.length - 1) {
				buff.append(",");
			}
		}
		return buff.toString();
	}

	/**
	 * @param vals
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private T[] parseSetValues(String vals) {
		StringTokenizer tok = new StringTokenizer(vals, ",");
		Object[] ret = new Object[tok.countTokens()];
		int i = 0;
		while(tok.hasMoreTokens()) {
			ret[i] = makeObject(tok.nextToken());
			++i;
		}
		return (T[])ret;
	}

	/**
	 * @return true if this property is read only.
	 */
	public boolean isReadOnly() {
		return readOnly;
	}

	/**
	 * @param readOnly
	 */
	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	/**
	 * @param set
	 */
	public void setValueSet(T[] set) {
		this.valueSet = set;
	}
}