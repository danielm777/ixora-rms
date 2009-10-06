package com.ixora.common.logging;
import java.util.Properties;

import com.ixora.common.logging.adapters.java.AppLoggerProviderJava;

/**
 * Logger factory.
 * @author Daniel Moraru
 */
// Note: don't use any class from common lib here
// because they probably need a logger
public final class AppLoggerFactory {
	/**
	 * Provider class.
	 */
	private static AppLoggerProvider provider;

	static {
		boolean failed = false;
		try {
			Properties props = new Properties();
			props.load(AppLoggerFactory.class.getResourceAsStream("/app.properties"));
			String clazz = props.getProperty("common.logging.provider");
			if(clazz == null) {
				failed = true;
			} else {
				try {
					provider = (AppLoggerProvider)Class.forName(clazz).newInstance();
				}
				catch (Exception e) {
					failed = true;
				}
			}
		} catch(Exception e) {
			failed = true;
			// no logger - ha
			e.printStackTrace();
		}
		if(failed) {
			// go with java
			provider = new AppLoggerProviderJava();
		}
	}

	/**
	 * Constructor for AppLoggerFactory.
	 */
	private AppLoggerFactory() {
		super();
	}

	/**
	 * @return A logger for the specified name.
	 */
	public static AppLogger getLogger(String name) {
		return provider.getLogger(name);
	}

	/**
	 * @return A logger for the specified class.
	 */
	public static AppLogger getLogger(Class clazz) {
		return provider.getLogger(clazz.getName());
	}

}

