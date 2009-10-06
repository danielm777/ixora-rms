/*
 * Created on 28-Feb-2004
 */
package com.ixora.common;

import java.awt.Color;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.ixora.common.exception.FailedToLoadConfiguration;
import com.ixora.common.exception.FailedToSaveConfiguration;
import com.ixora.common.exception.ReadOnlyConfiguration;
import com.ixora.common.logging.AppLogger;
import com.ixora.common.logging.AppLoggerFactory;
import com.ixora.common.typedproperties.exception.InvalidPropertyValue;

/**
 * Manages the configuration for multiple components.
 * @author Daniel Moraru
 */
public final class ConfigurationMgr {
	/** Logger */
	private static final AppLogger logger = AppLoggerFactory.getLogger(ConfigurationMgr.class);
	/**
	 * Component configurations.
	 * Key: component name
	 * Value: ComponentConfiguration
	 */
	private static Map<String, ComponentConfiguration> configurations = new HashMap<String, ComponentConfiguration>();
	/**
     * Components whose configurations will be
     * available for editing.
	 */
	private static List<String> editable = new LinkedList<String>();

	/**
	 * Components that have non-standard configuration
	 * can register it with the manager to let it manage the
	 * life cycle of the config object.
	 * @param c
	 */
	public synchronized static void registerConfiguration(
			String component,
			ComponentConfiguration c) {
		configurations.put(component, c);
	}

	/**
	 * @return the components that have editable configurations
	 */
	public synchronized static Collection<String> getComponentsWithEditableConfigurations() {
		List<String> ret = new LinkedList<String>();
		for(String comp : editable) {
			if(comp.indexOf('.') < 0) {
				ret.add(comp);
			}
		}
		return ret;
	}

	/**
	 * @return the subcomponents of the given component that have
	 * an editable configuration.
	 */
	public synchronized static Collection<String> getComponentsWithEditableConfigurations(String component) {
		List<String> ret = new LinkedList<String>();
		for(String comp : editable) {
			if(!comp.equals(component)
				&& comp.startsWith(component)
				&& (comp.indexOf('.', component.length()) > 0)) {
				ret.add(comp);
			}
		}
		return ret;
	}

	/**
	 * This will make the configuration of the given component editable.
	 * @param name
	 */
	public synchronized static void makeConfigurationEditable(String name) {
		editable.add(name);
	}

	/**
	 * If there is no configuration registered for this component
	 * a standard configuration will be created and returned.<br>
	 * The component name will be split at '.' characters and
	 * the resulted path will be considered a hierarchy of components and
	 * subcomponents.
	 * @param component component name
	 * @return the configuration for the given component
	 */
	public synchronized static ComponentConfiguration get(String component) {
		ComponentConfiguration c = configurations.get(component);
		if(c == null) {
			try {
				// try to load it
				c = new ComponentConfiguration(
						"config/" + component.replace('.', '/') + "/config.properties");
				configurations.put(component, c);
			} catch (FailedToLoadConfiguration e) {
				// this can happen for dummy components which are
				// just parents for other components
				if(logger.isTraceEnabled()) {
					logger.error(e);
				}
			}
		}
		return c;
	}

	/**
	 * @see com.ixora.common.ComponentConfiguration#getString(String)
	 * @param component
	 * @param property
	 * @return
	 */
	public static String getString(String component, String property) {
		return get(component).getString(property);
	}

	/**
	 * @see com.ixora.common.ComponentConfiguration#getObject(String)
	 * @param component
	 * @param property
	 * @return
	 */
	public static Object getObject(String component, String property) {
		return get(component).getObject(property);
	}

	/**
	 * @see com.ixora.common.ComponentConfiguration#getFloat(String)
	 * @param component
	 * @param property
	 * @return
	 */
	public static float getFloat(String component, String property) {
		return get(component).getFloat(property);
	}

	/**
	 * @param property
	 * @return a boolean property
	 */
	public static boolean getBoolean(String component, String property) {
		return get(component).getBoolean(property);
	}

	/**
	 * @see com.ixora.common.ComponentConfiguration#getInt(String)
	 * @param component
	 * @param property
	 * @return
	 */
	public static int getInt(String component, String property) {
		return get(component).getInt(property);
	}

	/**
	 * @see com.ixora.common.ComponentConfiguration#getDate(String)
	 * @param component
	 * @param property
	 * @return
	 */
	public static Date getDate(String component, String property) {
		return get(component).getDate(property);
	}

	/**
	 * @see com.ixora.common.ComponentConfiguration#getFile(String)
	 * @param component
	 * @param property
	 * @return
	 */
	public static File getFile(String component, String property) {
		return get(component).getFile(property);
	}

	/**
	 * @see com.ixora.common.ComponentConfiguration#getColor(String)
	 * @param component
	 * @param property
	 * @return
	 */
	public static Color getColor(String component, String property) {
		return get(component).getColor(property);
	}

	/**
	 * @see com.ixora.common.ComponentConfiguration#getList(String)
	 * @param component
	 * @param property
	 * @return
	 */
	public static List getList(String component, String property) {
		return get(component).getList(property);
	}

	/**
	 * @see com.ixora.common.ComponentConfiguration#getList(String)
	 * @param component
	 * @param clazz
	 * @param property
	 * @return
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws NoSuchMethodException
	 */
	public static List getList(String component,
	        Class clazz, String property) throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
		return get(component).getList(clazz, property);
	}

	/**
	 * @see com.ixora.common.ComponentConfiguration#set(String, String, Object)
	 * @param component
	 * @param o
	 * @throws InvalidPropertyValue
	 */
	public static void setList(String component, String property, List value) {
		get(component).setList(property, value);
	}

	/**
	 * @see com.ixora.common.ComponentConfiguration#setInt(String, int)
	 * @param component
	 * @param o
	 */
	public static void setInt(String component, String property, int value) {
		get(component).setInt(property, value);
	}

    /**
     * @see com.ixora.common.ComponentConfiguration#setString(String, String)
     * @param component
     * @param o
     */
    public static void setString(String component, String property, String value) {
        get(component).setString(property, value);
    }

	/**
	 * @see com.ixora.common.ComponentConfiguration#setFloat(String, float)
	 * @param component
	 * @param o
	 */
	public static void setFloat(String component, String property, float value) {
		get(component).setFloat(property, value);
	}

	/**
	 * @see com.ixora.common.ComponentConfiguration#setBoolean(String, boolean)
	 * @param component
	 * @param o
	 */
	public static void setBoolean(String component, String property, boolean value) {
		get(component).setBoolean(property, value);
	}

	/**
	 * @see com.ixora.common.ComponentConfiguration#setDate(String, Date)
	 * @param component
	 * @param o
	 */
	public static void setDate(String component, String property, Date value) {
		get(component).setDate(property, value);
	}

	/**
	 * @see com.ixora.common.ComponentConfiguration#setFile(String, File)
	 * @param component
	 * @param o
	 */
	public static void setFile(String component, String property, File value) {
		get(component).setFile(property, value);
	}

	/**
	 * @see com.ixora.common.ComponentConfiguration#setColor(String, Color)
	 * @param component
	 * @param o
	 */
	public static void setColor(String component, String property, Color value) {
		get(component).setColor(property, value);
	}

	/**
	 * Saves the configuration for the given component.
	 * @param component
	 */
	public static void save(String component) throws FailedToSaveConfiguration {
		try {
			get(component).save();
		} catch (ReadOnlyConfiguration e) {
			logger.error(e);
		}
	}

	/**
	 * Saves all configurations.
	 */
	public synchronized static void saveAll() throws FailedToSaveConfiguration {
		try {
			for(Iterator iter = configurations.values().iterator(); iter.hasNext();) {
				((ComponentConfiguration)iter.next()).save();
			}
		} catch (ReadOnlyConfiguration e) {
			logger.error(e);
		}
	}

	/**
	 * @return true if the application runs in developer mode
	 */
	public static boolean isDeveloperMode() {
		return Boolean.parseBoolean(System.getProperty("application.dev"));
	}
}
