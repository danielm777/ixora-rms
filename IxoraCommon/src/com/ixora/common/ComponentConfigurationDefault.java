package com.ixora.common;

import java.awt.Color;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.ixora.common.exception.FailedToLoadConfiguration;
import com.ixora.common.exception.FailedToSaveConfiguration;
import com.ixora.common.exception.ReadOnlyConfiguration;
import com.ixora.common.typedproperties.PropertyEntry;
import com.ixora.common.typedproperties.TypedProperties;
import com.ixora.common.typedproperties.exception.InvalidPropertyValue;
import com.ixora.common.typedproperties.exception.VetoException;
import com.ixora.common.utils.Utils;
import com.ixora.common.xml.XMLUtils;
import com.ixora.common.xml.exception.XMLException;


/**
 * Application/Component configuration class.
 * @author Daniel Moraru
 */
public class ComponentConfigurationDefault extends TypedProperties implements ComponentConfiguration {
	private static final long serialVersionUID = 7836771735023076337L;
	/** Path to the conf file */
	private String confFile;

	/**
	 * Constructor for a read only configuration.
	 * @param is
	 * @throws FailedToLoadConfiguration If an exception occurs while reading the stream
	 */
	protected ComponentConfigurationDefault(BufferedInputStream is) throws FailedToLoadConfiguration {
		super();
		loadConfiguration(is);
	}

	/**
	 * Constructor.
	 * @param path Relative path to the configuration file.
	 * @throws FailedToLoadConfiguration If any exception is thrown while trying
	 * to load the configuration file
	 */
	protected ComponentConfigurationDefault(String path) throws FailedToLoadConfiguration {
		super();
		loadConfigurationFromFile(path);
	}

	/**
	 * @see com.ixora.common.ComponentConfiguration#save()
	 */
	public synchronized void save() throws ReadOnlyConfiguration, FailedToSaveConfiguration {
		if(this.confFile == null) {
			// read only
			throw new ReadOnlyConfiguration();
		}

		BufferedOutputStream os = null;
		SafeOverwrite so = new SafeOverwrite(new File(this.confFile));
		try {
			// save properties to file
			so.backup();
			Document doc = XMLUtils.createEmptyDocument("config");
			toXML(doc.getDocumentElement());

			// write
			os = new BufferedOutputStream(new FileOutputStream(this.confFile));
			XMLUtils.write(doc, os);
			// don't forget to close before checking file length
			os.close();

			// commit only if the file has something in it
			if(new File(this.confFile).length() == 0) {
				so.rollback(os);
			} else {
				so.commit(os);
			}
		} catch(Throwable e) {
			try {
				so.rollback(os);
			}catch(IOException e1) {
				throw new FailedToSaveConfiguration(e1);
			}
			throw new FailedToSaveConfiguration(e);
		}
	}

	/**
	 * @see com.ixora.common.ComponentConfiguration#getString(java.lang.String)
	 */
	public synchronized String getString(String key) {
		// search first the system properties
		String prop = System.getProperty(key);
		if(prop != null) {
			return prop;
		}
		return super.getString(key);
	}

	/**
	 * Loads the configuration from the given stream.
	 * @param is Configuration stream
	 * @throws FailedToLoadConfiguration If the config stream cannot be accessed AND the default properties
	 * are not set
	 */
	protected void loadConfiguration(InputStream is) throws FailedToLoadConfiguration {
		try {
			Document doc = XMLUtils.read(is);
			fromXML(doc.getFirstChild());
		} catch(XMLException e) {
			throw new FailedToLoadConfiguration(e);
		}
	}

	/**
	 * Loads the configuration from the given file.
	 * @param path
	 * @throws FailedToLoadConfiguration
	 */
	protected void loadConfigurationFromFile(String path) throws FailedToLoadConfiguration {
		this.confFile = Utils.getPath(path);
		InputStream fis = null;
		try {
			fis = new FileInputStream(this.confFile);
			loadConfiguration(fis);
		} catch (FileNotFoundException e) {
			throw new FailedToLoadConfiguration(e);
		} finally {
			try {
				if(fis != null) {
					fis.close();
				}
			} catch(IOException e) {
			}
		}
	}

	/**
	 * Makes a collection out of a property of the form
	 * property = part1, part2,.... to Collection(part1, part2,...).
	 * The separator is <code>File.pathSeparatorChar</code>.
	 * @param propKey String
	 * @return List
	 */
	protected List<String> getMultipleStringProp(String propKey) {
		String lst = getString(propKey);
		if(lst == null) {
			return null;
		}
		StringTokenizer tok = new StringTokenizer(lst, File.pathSeparator);
		ArrayList<String> ret = new ArrayList<String>(tok.countTokens());
		while(tok.hasMoreTokens()) {
			ret.add(tok.nextToken());
		}

		return ret;
	}

	/**
	 * Similar to <code>getMultipleStringProp(String)</code> with the
	 * exception that the list will contain instances of the given class
	 * who must provide a constructor taking as an argument a String.
	 * @param propKey String
	 * @return List
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	protected <T> List<T> getMultipleStringProp(Class<T> clazz, String propKey)
			throws NoSuchMethodException, InstantiationException,
			IllegalAccessException, InvocationTargetException {
		String lst = getString(propKey);
		if(lst == null) {
			return null;
		}
		Constructor<T> constr = clazz.getConstructor(
		        new Class<?>[]{String.class});
		StringTokenizer tok = new StringTokenizer(lst, File.pathSeparator);
		List<T> ret = new ArrayList<T>(tok.countTokens());
		while(tok.hasMoreTokens()) {
			ret.add(constr.newInstance(new Object[]{tok.nextToken()}));
		}
		return ret;
	}

	/**
	 * Packs a collection of values to a certain property<br>
	 * Collection(prop1, prop2,...) to property = part1, part2,....<br>
	 * @param propKey Name of the property
	 * @param vals List of entries to be appended to the property value
	 */
	protected void setMultipleStringProp(String propKey, List<?> vals) {
		int size = vals.size();
		StringBuffer str = new StringBuffer(size * 10);
		for(int i = 0; i < size; ++i) {
			str.append(vals.get(i).toString());
			if(i < (size - 1)) {
				str.append(File.pathSeparatorChar);
			}
		}
		setString(propKey, str.toString());
	}

	/**
	 * @see com.ixora.common.ComponentConfiguration#getList(java.lang.String)
	 */
	public synchronized List<String> getList(String property) {
		return getMultipleStringProp(property);
	}

	/**
	 * @see com.ixora.common.ComponentConfiguration#getList(java.lang.Class, java.lang.String)
	 */
	public synchronized <T> List<T> getList(Class<T> clazz, String property) throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
		return getMultipleStringProp(clazz, property);
	}

	/**
	 * @see com.ixora.common.ComponentConfiguration#setList(java.lang.String, java.util.List)
	 */
	public synchronized void setList(String property, List<?> col) {
		setMultipleStringProp(property, col);
	}

	/**
	 * @see com.ixora.common.ComponentConfiguration#clone()
	 */
	public synchronized Object clone() {
        ComponentConfigurationDefault conf = (ComponentConfigurationDefault)super.clone();
        conf.props = new LinkedHashMap<String, PropertyEntry<?>>();
        conf.confFile = this.confFile;
		for(Iterator<String> itr = props.keySet().iterator(); itr.hasNext();) {
			String key = itr.next();
			PropertyEntry<?> pe = props.get(key);
			conf.props.put(key, (PropertyEntry<?>)pe.clone());
		}
		return conf;
	}

	/**
	 * @see com.ixora.common.ComponentConfiguration#apply(com.ixora.common.typedproperties.TypedProperties)
	 */
	public synchronized void apply(TypedProperties other) throws InvalidPropertyValue {
		super.apply(other);
	}

	/**
	 * @see com.ixora.common.typedproperties.TypedProperties#equals(java.lang.Object)
	 */
	public synchronized boolean equals(Object obj) {
		return super.equals(obj);
	}

	/**
	 * @see com.ixora.common.ComponentConfiguration#fromXML(org.w3c.dom.Node)
	 */
	public synchronized void fromXML(Node node) throws XMLException {
		super.fromXML(node);
	}

	/**
	 * @see com.ixora.common.ComponentConfiguration#getBoolean(java.lang.String)
	 */
	public synchronized boolean getBoolean(String property) {
		return super.getBoolean(property);
	}

	/**
	 * @see com.ixora.common.ComponentConfiguration#getColor(java.lang.String)
	 */
	public synchronized Color getColor(String property) {
		return super.getColor(property);
	}

	/**
	 * @see com.ixora.common.ComponentConfiguration#getDate(java.lang.String)
	 */
	public synchronized Date getDate(String property) {
		return super.getDate(property);
	}

	/**
	 * @see com.ixora.common.typedproperties.TypedProperties#getEntries()
	 */
	public synchronized Map<String, PropertyEntry<?>> getEntries() {
		return super.getEntries();
	}

	/**
	 * @see com.ixora.common.typedproperties.TypedProperties#getEntry(java.lang.String)
	 */
	public synchronized PropertyEntry<?> getEntry(String key) {
		return super.getEntry(key);
	}

	/**
	 * @see com.ixora.common.typedproperties.TypedProperties#getEntryAt(int)
	 */
	public synchronized PropertyEntry<?> getEntryAt(int idx) {
		return super.getEntryAt(idx);
	}

	/**
	 * @see com.ixora.common.ComponentConfiguration#getFile(java.lang.String)
	 */
	public synchronized File getFile(String property) {
		return super.getFile(property);
	}

	/**
	 * @see com.ixora.common.ComponentConfiguration#getFloat(java.lang.String)
	 */
	public synchronized float getFloat(String property) {
		return super.getFloat(property);
	}

	/**
	 * @see com.ixora.common.ComponentConfiguration#getInt(java.lang.String)
	 */
	public synchronized int getInt(String property) {
		return super.getInt(property);
	}

	/**
	 * @see com.ixora.common.ComponentConfiguration#getObject(java.lang.String)
	 */
	public synchronized Object getObject(String property) {
		return super.getObject(property);
	}

	/**
	 * @see com.ixora.common.ComponentConfiguration#getValue(java.lang.String)
	 */
	public synchronized Object getValue(String key) {
		return super.getValue(key);
	}

	/**
	 * @see com.ixora.common.ComponentConfiguration#hasProperty(java.lang.String)
	 */
	public synchronized boolean hasProperty(String prop) {
		return super.hasProperty(prop);
	}

	/**
	 * @see com.ixora.common.ComponentConfiguration#keys()
	 */
	public synchronized Set<String> keys() {
		return super.keys();
	}

	/**
	 * @see com.ixora.common.ComponentConfiguration#setBoolean(java.lang.String, boolean)
	 */
	public synchronized void setBoolean(String key, boolean value) {
		super.setBoolean(key, value);
	}

	/**
	 * @see com.ixora.common.ComponentConfiguration#setColor(java.lang.String, java.awt.Color)
	 */
	public synchronized void setColor(String key, Color value) {
		super.setColor(key, value);
	}

	/**
	 * @see com.ixora.common.ComponentConfiguration#setDate(java.lang.String, java.util.Date)
	 */
	public synchronized void setDate(String key, Date date) {
		super.setDate(key, date);
	}

	/**
	 * @see com.ixora.common.ComponentConfiguration#setDefaults()
	 */
	public synchronized void setDefaults() {
		super.setDefaults();
	}

	/**
	 * @see com.ixora.common.typedproperties.TypedProperties#setDefaultValue(java.lang.String, java.lang.Object)
	 */
	public synchronized <T> void setDefaultValue(String key, T value) {
		super.setDefaultValue(key, value);
	}

	/**
	 * @see com.ixora.common.ComponentConfiguration#setFile(java.lang.String, java.io.File)
	 */
	public synchronized void setFile(String key, File value) {
		super.setFile(key, value);
	}

	/**
	 * @see com.ixora.common.ComponentConfiguration#setFloat(java.lang.String, float)
	 */
	public synchronized void setFloat(String key, float value) {
		super.setFloat(key, value);
	}

	/**
	 * @see com.ixora.common.ComponentConfiguration#setInt(java.lang.String, int)
	 */
	public synchronized void setInt(String key, int value) {
		super.setInt(key, value);
	}

	/**
	 * @see com.ixora.common.ComponentConfiguration#setObject(java.lang.String, T)
	 */
	public synchronized <T> void setObject(String key, T value) {
		super.setObject(key, value);
	}

	/**
	 * @see com.ixora.common.typedproperties.TypedProperties#setProperty(java.lang.String, int, boolean, boolean, java.io.Serializable[], java.lang.String)
	 */
	public synchronized PropertyEntry<?> setProperty(String key, int type, boolean visible,
			boolean required, Serializable[] set, String extendedEditorClass) {
		return super
				.setProperty(key, type, visible, required, set, extendedEditorClass);
	}

	/**
	 * @see com.ixora.common.typedproperties.TypedProperties#setProperty(java.lang.String, int, boolean, boolean, java.io.Serializable[])
	 */
	public synchronized PropertyEntry<?> setProperty(String key, int type, boolean visible,
			boolean required, Serializable[] set) {
		return super.setProperty(key, type, visible, required, set);
	}

	/**
	 * @see com.ixora.common.typedproperties.TypedProperties#setProperty(java.lang.String, int, boolean, boolean)
	 */
	public synchronized PropertyEntry<?> setProperty(String key, int type, boolean visible,
			boolean required) {
		return super.setProperty(key, type, visible, required);
	}

	/**
	 * @see com.ixora.common.typedproperties.TypedProperties#setProperty(java.lang.String, int, boolean, java.io.Serializable[])
	 */
	public synchronized PropertyEntry<?> setProperty(String key, int type, boolean visible,
			Serializable[] set) {
		return super.setProperty(key, type, visible, set);
	}

	/**
	 * @see com.ixora.common.typedproperties.TypedProperties#setProperty(java.lang.String, int, boolean)
	 */
	public synchronized PropertyEntry<?> setProperty(String key, int type, boolean visible) {
		return super.setProperty(key, type, visible);
	}

	/**
	 * @see com.ixora.common.ComponentConfiguration#setString(java.lang.String, java.lang.String)
	 */
	public synchronized void setString(String key, String value) {
		super.setString(key, value);
	}

	/**
	 * @see com.ixora.common.typedproperties.TypedProperties#toString()
	 */
	public synchronized String toString() {
		return super.toString();
	}

	/**
	 * @see com.ixora.common.ComponentConfiguration#toXML(org.w3c.dom.Node)
	 */
	public synchronized void toXML(Node parent) throws XMLException {
		super.toXML(parent);
	}

	/**
	 * @see com.ixora.common.ComponentConfiguration#veto()
	 */
	public synchronized void veto() throws VetoException {
		super.veto();
	}
}