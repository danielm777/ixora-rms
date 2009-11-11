/*
 * Created on 15-Mar-2005
 */
package com.ixora.common.plugin;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import com.ixora.common.exception.AppException;
import com.ixora.common.plugin.exception.IncompatibleClassloaderError;
import com.ixora.common.utils.Utils;

/**
 * Helper that loads classes for monitoring entities that require
 * their own class loader.
 * @author Daniel Moraru
 */
public final class ClassLoadingHelper {
	/**
	 * Class loaders grouped by the configuration ids.
	 */
	private Map<String, RegexClassLoader> fLoadersPerId;
	/**
	 * Class loaders grouped by an external identifier.
	 */
	private Map<String, RegexClassLoader> fLoadersPerExternalId;
	/**
	 * Original thread class loaders.
	 */
	private Map<Thread, ClassLoader> fOriginalLoaders;

	/**
	 * Constructor.
	 */
	public ClassLoadingHelper() {
		super();
		fLoadersPerId = new HashMap<String, RegexClassLoader>();
		fLoadersPerExternalId = new HashMap<String, RegexClassLoader>();
		fOriginalLoaders = new WeakHashMap<Thread, ClassLoader>();
	}

	/**
	 * Prepares the current thread that will invoke methods on the plugin instance with the
	 * given id.
	 * @param identifier plugin instance identifier
	 */
	public void prepareThreadOnEnter(String identifier) {
		RegexClassLoader loader = fLoadersPerExternalId.get(identifier);
		if(loader != null) {
			Thread thread = Thread.currentThread();
			fOriginalLoaders.put(thread, thread.getContextClassLoader());
			thread.setContextClassLoader(loader);
		}
	}

	/**
	 * Prepares the current thread that will invoke methods on the agent with the
	 * given id.
	 */
	public void prepareThreadOnExit() {
		Thread thread = Thread.currentThread();
		ClassLoader loader = fOriginalLoaders.get(thread);
		if(loader != null) {
			thread.setContextClassLoader(loader);
		}
		fOriginalLoaders.remove(thread);
	}

	/**
	 * Invoked to clean up for the given identifier.
	 * @param identifier plugin instance identifier
	 */
	public void identifierExpired(String identifier) {
		fLoadersPerExternalId.remove(identifier);
	}

	/**
	 * NOTE: this method will change the context of the calling thread and the caller must insure
	 * that prepareThreadOnExit(String) is invoked at the end of the operation.
	 * @param identifier plugin instance identifier
	 * @param classToLoad the class to load
	 * @param jars jars with entity code and resources; path relative to application.home property
	 * @param pluginData
	 * @return
	 * @throws AppException
	 * @throws ClassNotFoundException
	 * @throws MalformedURLException
	 * @throws URISyntaxException
	 */
	public Class<?> getClass(String identifier, String classToLoad, String[] jars, PluginDescriptor pluginData) throws AppException, ClassNotFoundException, MalformedURLException, URISyntaxException {
	    Class<?> clazz = null;
	    String classpath = null;
	    if(pluginData != null) {
	        classpath = pluginData.getClasspath();
	        if(classpath != null) {
	        	// if cutom classpath is specified, the jars are mandatory
	        	if(jars == null) {
	        		AppException e = new AppException("Code location information is missing");
	        		e.setIsInternalAppError();
	        		throw e;
	        	}
	        }
	    }
	    if(jars != null) {
        	List<String> pathList = new LinkedList<String>();
        	for(int i = 0; i < jars.length; i++) {
        		// now complete jars information
	        	pathList.add(Utils.getPath(jars[i]));
			}
        	if(classpath != null) {
        		pathList.add(classpath);
        	}
        	// check class loader id
        	String classLoaderId = pluginData.getClassLoaderId();
        	RegexClassLoader loader;
        	if(Utils.isEmptyString(classLoaderId)) {
        		loader = new RegexClassLoader(
					pathList.toArray(new String[pathList.size()]),
					pluginData.useParentLastClassloader());
        	} else {
        		// check if a classloader with the same id already created
        		loader = fLoadersPerId.get(classLoaderId);
        		String[] path = pathList.toArray(new String[pathList.size()]);
        		if(loader == null) {
        			// add it to the map
        			loader = new RegexClassLoader(
        					path,
        					pluginData.useParentLastClassloader());
        			fLoadersPerId.put(classLoaderId, loader);
        		} else {
        			// check for consistency: the classpath must be the same
        			if(!loader.isCompatibleWithClasspath(path)) {
        				throw new IncompatibleClassloaderError(
        						classLoaderId,
        						loader.getClasspathAsString(),
        						RegexClassLoader.convertClasspathToString(path));
        			}
        		}
        	}
        	fLoadersPerExternalId.put(identifier, loader);
			prepareThreadOnEnter(identifier);
			clazz = Class.forName(classToLoad, true, loader);
	    }
	    if(clazz == null){
	        clazz = Class.forName(classToLoad);
	    }
		return clazz;
	}
}
