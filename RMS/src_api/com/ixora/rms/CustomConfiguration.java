/*
 * Created on 13-Jan-2004
 */
package com.ixora.rms;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.ixora.common.plugin.PluginDescriptor;
import com.ixora.common.typedproperties.TypedProperties;
import com.ixora.common.xml.XMLExternalizable;
import com.ixora.common.xml.exception.XMLException;

/**
 * Every custom configuration must implement this interface.
 * @author Daniel Moraru
 */
public abstract class CustomConfiguration extends TypedProperties implements PluginDescriptor, XMLExternalizable {

	/**
	 * @see com.ixora.common.plugin.PluginDescriptor#getClasspath()
	 */
	public String getClasspath() {
	    // no classpath by default
	    return null;
	}

	/**
	 * @see com.ixora.common.plugin.PluginDescriptor#getClassLoaderId()
	 */
	public String getClassLoaderId() {
		// new one by default
		return null;
	}

	/**
	 * @see com.ixora.common.plugin.PluginDescriptor#useParentLastClassloader()
	 */
	public boolean useParentLastClassloader() {
		// parent last by default
		return true;
	}

	/**
	 * @see com.ixora.common.typedproperties.TypedProperties#fromXML(org.w3c.dom.Node)
	 */
	public void fromXML(Node node) throws XMLException {
		super.fromXML(node);
	}

	/**
	 * @see com.ixora.common.typedproperties.TypedProperties#toXML(org.w3c.dom.Node)
	 */
	public void toXML(Node parent) throws XMLException {
		Document doc = parent.getOwnerDocument();
		Element ce = doc.createElement("config");
		parent.appendChild(ce);
		super.toXML(ce);
	}
}
