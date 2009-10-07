/*
 * Created on 28-Dec-2004
 */
package com.ixora.rms.providers.impl.sql;

import org.w3c.dom.Node;

import com.ixora.common.typedproperties.ui.ExtendedEditorMultilineText;
import com.ixora.common.xml.exception.XMLException;
import com.ixora.rms.providers.ProviderCustomConfiguration;

/**
 * @author Daniel Moraru
 */
public class Configuration extends ProviderCustomConfiguration {
	public static final String CLASSPATH = "providers.sql.classpath";
	public static final String CLASSLOADER_ID = "providers.sql.classloader_id";
	public static final String DRIVER_CLASS = "providers.sql.driver_class";
	public static final String USERNAME = "providers.sql.username";
	public static final String PASSWORD = "providers.sql.password";
	public static final String CONNECTION_STRING = "providers.sql.connection_string";
	public static final String SQL_QUERY = "providers.sql.sql_query";

	/**
	 * Constructor.
	 */
	public Configuration() {
		super();
		setProperty(CLASSPATH, TYPE_STRING, true, false);
		setProperty(CLASSLOADER_ID, TYPE_STRING, true, false);
		setProperty(DRIVER_CLASS, TYPE_STRING, true, true);
		setProperty(USERNAME, TYPE_STRING, true, false);
		setProperty(PASSWORD, TYPE_STRING, true, false);
		setProperty(CONNECTION_STRING, TYPE_STRING, true, true);
		setProperty(SQL_QUERY, TYPE_SERIALIZABLE, true, true, null, ExtendedEditorMultilineText.class.getName());

		setString(DRIVER_CLASS, "sun.jdbc.odbc.JdbcOdbcDriver");
		setString(CLASSLOADER_ID, "shared");
	}

	/**
	 * @see com.ixora.rms.CustomConfiguration#getClasspath()
	 */
	public String getClasspath() {
		return getString(CLASSPATH);
	}

	/**
	 * @see com.ixora.rms.CustomConfiguration#getClassLoaderId()
	 */
	public String getClassLoaderId() {
		return getString(CLASSLOADER_ID);
	}

	/**
	 * @see com.ixora.rms.CustomConfiguration#fromXML(org.w3c.dom.Node)
	 */
	public void fromXML(Node node) throws XMLException {
		super.fromXML(node);
		// TODO just to preserve compatibility with beta releases for 1.3
		// need to remove this obsolete configuration entry
		this.props.remove("providers.sql.convert_properties_to_table");
	}


}
