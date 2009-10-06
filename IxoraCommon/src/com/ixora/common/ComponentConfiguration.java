package com.ixora.common;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.StringTokenizer;

import org.w3c.dom.Document;

import com.ixora.common.exception.FailedToLoadConfiguration;
import com.ixora.common.exception.FailedToSaveConfiguration;
import com.ixora.common.exception.ReadOnlyConfiguration;
import com.ixora.common.typedproperties.PropertyEntry;
import com.ixora.common.typedproperties.TypedProperties;
import com.ixora.common.utils.Utils;
import com.ixora.common.xml.XMLUtils;
import com.ixora.common.xml.exception.XMLException;


/**
 * Application/Component configuration class.
 * @author Daniel Moraru
 */
public class ComponentConfiguration extends TypedProperties {
	/** Path to the conf file */
	private String confFile;

	/**
	 * Constructor for a read only configuration.
	 * @param is
	 * @throws FailedToLoadConfiguration If an exception occurs while reading the stream
	 */
	protected ComponentConfiguration(BufferedInputStream is) throws FailedToLoadConfiguration {
		super();
		loadConfiguration(is);
	}

	/**
	 * Constructor.
	 * @param path Relative path to the configuration file.
	 * @throws FailedToLoadConfiguration If any exception is thrown while trying
	 * to load the configuration file
	 */
	protected ComponentConfiguration(String path) throws FailedToLoadConfiguration {
		super();
		loadConfigurationFromFile(path);
	}

	/**
	 * Constructor.
	 */
	private ComponentConfiguration() {
	    super();
	}

	/**
	 * Saves the configuration.
	 * @throws FailedToSaveConfiguration
	 * @throws ReadOnlyConfiguration
	 */
	public void save() throws ReadOnlyConfiguration, FailedToSaveConfiguration {
		if(this.confFile == null) {
			// read only
			throw new ReadOnlyConfiguration();
		}

		BufferedOutputStream os = null;
		File tmp = null;
		SafeOverwrite so = new SafeOverwrite(new File(this.confFile));
		try {
			// save properties to file
			so.backup();
			Document doc = XMLUtils.createEmptyDocument("config");
			toXML(doc.getDocumentElement());

			// test write
//			File test = new File(this.confFile + ".tst");
//			test.deleteOnExit();
//			try {
//				os = new BufferedOutputStream(new FileOutputStream(test));
//				XMLUtils.write(doc, os);
//			} finally {
//				if(os != null) {
//					try {
//						os.close();
//					} catch(Exception e) {}
//				}
//				test.delete();
//			}

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
	 * @return The property for the given key.<br>
	 * First it searches the system property then the configuration file properties.
	 * @param key Property key
	 */
	public String getString(String key) {
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
	 * @return ArrayList
	 */
	protected ArrayList<String> getMultipleStringProp(String propKey) {
		String lst = getString(propKey);
		if(lst == null) {
			return null;
		}
		StringTokenizer tok = new StringTokenizer(lst, File.pathSeparator);
		ArrayList ret = new ArrayList<String>(tok.countTokens());
		while(tok.hasMoreTokens()) {
			ret.add(tok.nextToken());
		}

		return ret;
	}

	/**
	 * Similar to <code>getMultipleStringProp(String)</code> with the
	 * exception that the list will contain instances of the given class
	 * who must provide a costructor taking as an argument a String.
	 * @param propKey String
	 * @return ArrayList
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	protected ArrayList getMultipleStringProp(Class clazz, String propKey)
			throws NoSuchMethodException, InstantiationException,
			IllegalAccessException, InvocationTargetException {
		String lst = getString(propKey);
		if(lst == null) {
			return null;
		}
		Constructor constr = clazz.getConstructor(
		        new Class[]{String.class});
		StringTokenizer tok = new StringTokenizer(lst, File.pathSeparator);
		ArrayList ret = new ArrayList(tok.countTokens());
		while(tok.hasMoreTokens()) {
			ret.add(constr.newInstance((Object[])new String[]{tok.nextToken()}));
		}
		return ret;
	}

	/**
	 * Packs a collection of values to a certain property<br>
	 * Collection(prop1, prop2,...) to property = part1, part2,....<br>
	 * @param propKey Name of the property
	 * @param vals List of entries to be appended to the property value
	 */
	protected void setMultipleStringProp(String propKey, List vals) {
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
	 * @param property
	 * @return a long property
	 */
	public List<String> getList(String property) {
		return getMultipleStringProp(property);
	}

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
	public List getList(Class clazz, String property) throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
		return getMultipleStringProp(clazz, property);
	}

	/**
	 * Sets the list property with the given name.
	 * @param property
	 * @param col
	 */
	public void setList(String property, List col) {
		setMultipleStringProp(property, col);
	}

	/**
	 * @see java.lang.Object#clone()
	 */
	public Object clone() {
        ComponentConfiguration conf = (ComponentConfiguration)super.clone();
        conf.props = new LinkedHashMap();
        conf.confFile = this.confFile;
		for(Iterator itr = props.keySet().iterator(); itr.hasNext();) {
			String key = (String)itr.next();
			PropertyEntry pe = props.get(key);
			conf.props.put(key, (PropertyEntry)pe.clone());
		}
		return conf;
	}
}