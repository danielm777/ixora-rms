/*
 * Created on 30-Sep-2004
 */
package com.ixora.common.typedproperties;

import java.io.File;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.ixora.common.typedproperties.exception.InvalidPropertyValue;
import com.ixora.common.typedproperties.exception.PropertyTypeMismatch;
import com.ixora.common.xml.XMLUtils;
import com.ixora.common.xml.exception.XMLException;


/**
 * PropertyEntryDate.
 */
public final class PropertyEntryFile extends PropertyEntry {
    /** Whether or not to allow folders */
    private boolean allowsFolders;
    /** Whether or not to allow files */
    private boolean allowsFiles;

    /**
     * Constructor to support XML.
     */
    PropertyEntryFile() {
        this(null, false, null, false);
    }
	/**
     * Constructor.
     */
    PropertyEntryFile(String prop, boolean v, File[] set, boolean required) {
        super(prop, v, set, TypedProperties.TYPE_FILE, required);
        this.extendedEditorClass = "com.ixora.common.typedproperties.ui.ExtendedEditorFile";
        allowsFiles = true;
        allowsFolders = true;
    }
    /**
     * @see com.ixora.common.typedproperties.PropertyEntry#makeObject(java.lang.String)
     */
    protected Object makeObject(String value) throws InvalidPropertyValue {
        return new File(value);
    }

    /**
     * @see com.ixora.common.typedproperties.PropertyEntry#makeString(java.lang.Object)
     */
    protected String makeString(Object obj) throws PropertyTypeMismatch {
        if(obj == null) {
            return "";
        }
        if(!(obj instanceof File)) {
            throw new PropertyTypeMismatch(property);
        }
        return ((File)obj).getAbsolutePath();
    }
    /**
     * @see com.ixora.common.typedproperties.PropertyEntry#checkObjectType(java.lang.Object)
     */
    protected boolean checkObjectType(Object obj) {
        return obj == null || obj instanceof File;
    }
    /**
     * @return the allowsFolders.
     */
    public boolean allowsFolders() {
        return allowsFolders;
    }
    /**
     * @param allowsFolders set to true to allow folders as values.
     */
    public void setAllowsFolders(boolean allowsFolders) {
        this.allowsFolders = allowsFolders;
    }
    /**
     * @return the allowsFiles.
     */
    public boolean allowsFiles() {
        return allowsFiles;
    }
    /**
     * @param allowsFiles set to true to allow files as values.
     */
    public void setAllowsFiles(boolean allowsFiles) {
        this.allowsFiles = allowsFiles;
    }
    /**
     * @see com.ixora.common.xml.XMLExternalizable#fromXML(org.w3c.dom.Node)
     */
    public void fromXML(Node node) throws XMLException {
        super.fromXML(node);
		Attr a = XMLUtils.findAttribute(node, "allow_folders");
		if(a != null) {
			allowsFolders = Boolean.valueOf(a.getValue()).booleanValue();
		}
		a = XMLUtils.findAttribute(node, "allow_files");
		if(a != null) {
			allowsFiles = Boolean.valueOf(a.getValue()).booleanValue();
		}
    }
    /**
     * @see com.ixora.common.xml.XMLExternalizable#toXML(org.w3c.dom.Node)
     */
    public void toXML(Node parent) throws XMLException {
        super.toXML(parent);
		Element el = (Element)XMLUtils.findChild(parent, property);
		if(el == null) {
		    return;
		}
        if(allowsFolders) {
			el.setAttribute("allow_folders", Boolean.TRUE.toString());
		}
        if(allowsFiles) {
			el.setAttribute("allow_files", Boolean.TRUE.toString());
		}
    }
}