package com.ixora.common;

import java.net.URL;
import java.net.URLClassLoader;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * It allows an application/component to
 * load localized messages from various repositories.
 * @author Daniel Moraru
 */
public final class MessageRepository {
	/** Instances */
	private static Map<String, MessageRepository> instances;
	/** Default repository */
	private static String defaultRepository;
	/** Resource bundle */
	private ResourceBundle bundle;

	/**
	 * This method must be called before this class is used
	 * @param def default repository
	 * @throws MissingResourceException
	 */
	public static void initialize(String def) throws MissingResourceException {
		instances = Collections.synchronizedMap(new HashMap<String, MessageRepository>());
		instances.put(def, new MessageRepository(
				def, Locale.getDefault()));
		defaultRepository = def;
	}

	/**
	 * Constructor for MessageRepository.
	 * @param repositoryName
	 * @param locale
	 */
	private MessageRepository(String repositoryName, Locale locale) {
		super();
		bundle = ResourceBundle.getBundle(repositoryName, locale);
	}

	/**
	 * Pubblic constructor for MessageRepository that reads a bundle from the given
	 * url.
	 * @param repositoryName
	 * @param locale
	 * @param url
	 */
	public MessageRepository(String repositoryName, Locale locale, URL url) {
		super();
		URLClassLoader urlLoader = new URLClassLoader(
				new URL[] {url});
		bundle = ResourceBundle.getBundle(
			repositoryName,	locale,	urlLoader);
	}

	/**
	 * @return The message with the given key
	 * @param msgKey
	 */
	public static String get(String msgKey) {
		return getDefault().getMessage(msgKey);
	}

	/**
	 * @return The message with the given key where the fields
	 * have been assigned the given values
	 * @param msgKey
	 * @param fieldValues
	 */
	public static String get(String msgKey, Object[] fieldValues) {
		return getDefault().getMessage(msgKey, fieldValues);
	}

	/**
	 * @return The message with the given key from the given repository
	 * @param repository
	 * @param msgKey
	 */
	public static String get(String repository, String msgKey) {
		return getRepository(repository).getMessage(msgKey);
	}

	/**
	 * @return The message with the given key from the given
	 * repository where the fields
	 * have been assigned the given values
	 * @param repository
	 * @param msgKey
	 * @param fieldValues
	 */
	public static String get(String repository, String msgKey, Object[] fieldValues) {
		return getRepository(repository).getMessage(msgKey, fieldValues);
	}

	/**
	 * @return The message with the given key
	 * @param msgKey
	 */
	public String getMessage(String msgKey) {
		try {
		    if(msgKey == null) {
		        return "";
		    }
			return bundle.getString(msgKey).trim();
		} catch(MissingResourceException e) {
			return msgKey;
		}
	}

	/**
	 * @return The message with the given key where the fields
	 * have been assigned the given values
	 * @param msgKey
	 * @param fieldValues
	 */
	public String getMessage(String msgKey, Object[] fieldValues) {
		String msg = getMessage(msgKey);
		return MessageFormat.format(msg, fieldValues);
	}

	/**
	 * @return the default repository
	 */
	private static MessageRepository getDefault() {
		return instances.get(defaultRepository);
	}

	/**
	 * @return the named repository
	 */
	private static MessageRepository getRepository(String name) {
		name = normalize(name);
		MessageRepository rep = null;
		synchronized(MessageRepository.class) {
			rep = instances.get(name);
			if(rep == null) {
				// try to load it
				try {
					rep = new MessageRepository(name, Locale.getDefault());
					instances.put(name, rep);
				}
				catch(MissingResourceException e) {
					//logger.error("Message repository '" + name + "' is missing");
				}
			}
		}
		if(rep == null) {
			return getDefault();
		}
		return rep;
	}

	/**
	 * Normalizes the repository name. All the dots in the
	 * name will be replaced by the underscore character.
	 * @param name
	 * @return
	 */
	private static String normalize(String name) {
		return name.replace('.', '_');
	}
}
