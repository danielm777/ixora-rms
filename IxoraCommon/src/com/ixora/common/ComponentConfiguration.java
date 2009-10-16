package com.ixora.common;

import java.awt.Color;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;
import java.util.Observer;
import java.util.Set;

import org.w3c.dom.Node;

import com.ixora.common.exception.FailedToSaveConfiguration;
import com.ixora.common.exception.ReadOnlyConfiguration;
import com.ixora.common.typedproperties.TypedProperties;
import com.ixora.common.typedproperties.exception.InvalidPropertyValue;
import com.ixora.common.xml.exception.XMLException;

/**
 * @author Daniel Moraru
 */
public interface ComponentConfiguration {

	/**
	 * Saves the configuration.
	 * @throws FailedToSaveConfiguration
	 * @throws ReadOnlyConfiguration
	 */
	void save() throws ReadOnlyConfiguration, FailedToSaveConfiguration;

	/**
	 * @return The property for the given key.<br>
	 * First it searches the system property then the configuration file properties.
	 * @param key Property key
	 */
	String getString(String key);

	/**
	 * @param property
	 * @return a long property
	 */
	List<String> getList(String property);

	/**
	 * @param property
	 * @return a list of objects constructed from the property
	 * with the given name; the given class must provide a constructor
	 * that takes as an argument a String
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws NoSuchMethodException
	 */
	<T> List<T> getList(Class<T> clazz, String property)
			throws NoSuchMethodException, InstantiationException,
			IllegalAccessException, InvocationTargetException;

	/**
	 * Sets the list property with the given name.
	 * @param property
	 * @param col
	 */
	void setList(String property, List<?> col);

	/**
	 * @see java.lang.Object#clone()
	 */
	Object clone();

	/**
	 * @see com.ixora.common.typedproperties.TypedProperties#apply(com.ixora.common.typedproperties.TypedProperties)
	 */
	void apply(TypedProperties other) throws InvalidPropertyValue;

	/**
	 * @see com.ixora.common.typedproperties.TypedProperties#fromXML(org.w3c.dom.Node)
	 */
	void fromXML(Node node) throws XMLException;

	/**
	 * @see com.ixora.common.typedproperties.TypedProperties#getBoolean(java.lang.String)
	 */
	boolean getBoolean(String property);

	/**
	 * @see com.ixora.common.typedproperties.TypedProperties#getColor(java.lang.String)
	 */
	Color getColor(String property);

	/**
	 * @see com.ixora.common.typedproperties.TypedProperties#getDate(java.lang.String)
	 */
	Date getDate(String property);

	/**
	 * @see com.ixora.common.typedproperties.TypedProperties#getFile(java.lang.String)
	 */
	File getFile(String property);

	/**
	 * @see com.ixora.common.typedproperties.TypedProperties#getFloat(java.lang.String)
	 */
	float getFloat(String property);

	/**
	 * @see com.ixora.common.typedproperties.TypedProperties#getInt(java.lang.String)
	 */
	int getInt(String property);

	/**
	 * @see com.ixora.common.typedproperties.TypedProperties#getObject(java.lang.String)
	 */
	Object getObject(String property);

	/**
	 * @see com.ixora.common.typedproperties.TypedProperties#getValue(java.lang.String)
	 */
	Object getValue(String key);

	/**
	 * @see com.ixora.common.typedproperties.TypedProperties#hasProperty(java.lang.String)
	 */
	boolean hasProperty(String prop);

	/**
	 * @see com.ixora.common.typedproperties.TypedProperties#keys()
	 */
	Set<String> keys();

	/**
	 * @see com.ixora.common.typedproperties.TypedProperties#setBoolean(java.lang.String, boolean)
	 */
	void setBoolean(String key, boolean value);

	/**
	 * @see com.ixora.common.typedproperties.TypedProperties#setColor(java.lang.String, java.awt.Color)
	 */
	void setColor(String key, Color value);

	/**
	 * @see com.ixora.common.typedproperties.TypedProperties#setDate(java.lang.String, java.util.Date)
	 */
	void setDate(String key, Date date);

	/**
	 * @see com.ixora.common.typedproperties.TypedProperties#setDefaults()
	 */
	void setDefaults();

	/**
	 * @see com.ixora.common.typedproperties.TypedProperties#setFile(java.lang.String, java.io.File)
	 */
	void setFile(String key, File value);

	/**
	 * @see com.ixora.common.typedproperties.TypedProperties#setFloat(java.lang.String, float)
	 */
	void setFloat(String key, float value);

	/**
	 * @see com.ixora.common.typedproperties.TypedProperties#setInt(java.lang.String, int)
	 */
	void setInt(String key, int value);

	/**
	 * @see com.ixora.common.typedproperties.TypedProperties#setObject(java.lang.String, java.lang.Object)
	 */
	<T> void setObject(String key, T value);

	/**
	 * @see com.ixora.common.typedproperties.TypedProperties#setString(java.lang.String, java.lang.String)
	 */
	void setString(String key, String value);

	/**
	 * @see com.ixora.common.typedproperties.TypedProperties#toXML(org.w3c.dom.Node)
	 */
	void toXML(Node parent) throws XMLException;

	/**
	 * @see com.ixora.common.typedproperties.TypedProperties#setDefaultValue(String, Object)
	 */
	<T> void setDefaultValue(String key, T value);

	/**
	 * @param observer
	 */
	void addObserver(Observer observer);

	/**
	 * @param observer
	 */
	void deleteObserver(Observer observer);
}