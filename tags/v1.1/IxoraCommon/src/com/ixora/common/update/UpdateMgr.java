/*
 * Created on 17-Jun-2004
 */
package com.ixora.common.update;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Allows applications to register local and remote
 * update modules.
 * @author Daniel Moraru
 */
public final class UpdateMgr {
	/**
	 * Key: module name
	 * Value: Module
	 */
	private static Map<String, Module> registeredModules;
	/**
	 * Key: module name
	 * Value: Module
	 */
	private static Map<String, Module> registeredNodeModules;

	/**
	 * Registers the given module.
	 * @param module
	 */
	public static synchronized void registerModule(Module module) {
		if(registeredModules == null) {
			registeredModules = new LinkedHashMap<String, Module>();
		}
		registeredModules.put(module.getName(), module);
	}

	/**
	 * Unregisters the given module.
	 * @param module
	 */
	public static synchronized void unregisterModule(String module) {
		if(registeredModules != null) {
			registeredModules.remove(module);
		}
	}

	/**
	 * @return the registered modules or null.
	 */
	public static synchronized Module[] getRegisteredModules() {
		if(registeredModules == null) {
			return null;
		}
		return (Module[])registeredModules.values().toArray(new Module[registeredModules.size()]);
	}

	/**
	 * Registers the given module.
	 * @param module
	 */
	public static synchronized void registerNodeModule(Module module) {
		if(registeredNodeModules == null) {
			registeredNodeModules = new LinkedHashMap<String, Module>();
		}
		registeredNodeModules.put(module.getName(), module);
	}

	/**
	 * @return the registered modules.
	 */
	public static synchronized Module[] getRegisteredNodeModules() {
		if(registeredNodeModules == null) {
			registeredNodeModules = new LinkedHashMap<String, Module>();
		}
		return (Module[])registeredNodeModules.values().toArray(new Module[registeredNodeModules.size()]);
	}

	/**
	 * @return all registered modules, local and remote
	 */
	public static synchronized Module[] getAllRegisteredModules() {
		HashSet<Module> ret = new HashSet<Module>();
		if(registeredModules != null) {
			for(Iterator<Module> iter = registeredModules.values().iterator(); iter.hasNext();) {
				ret.add(iter.next());
			}
		}
		if(registeredNodeModules != null) {
			for(Iterator<Module> iter = registeredNodeModules.values().iterator(); iter.hasNext();) {
				ret.add(iter.next());
			}
		}
		return (Module[])ret.toArray(new Module[ret.size()]);
	}

	/**
	 */
	public static void checkForUpdates() {
// TODO		
/*		try {
			new Thread(new Runnable(){
				public void run() {
					try {
						ConfigurationMgr.getString(UpdateComponent.NAME, UpdateConfigurationConstants.SERVER_CHECK_FOR_UPDATES_URL);
						License lic = LicenseMgr.getLicense();
						NetUtils.postHttpForm(new URL(url), new NameValuePair[]{
							new NameValuePair("lic", lic != null ? lic.toString(true) : ""),
							new NameValuePair("app_version",
									Product.getProductInfo().getVersion().toString())
						});
					} catch(Exception e) {
						; // ignore
					}
				}}) {
			}.start();
		} catch(Exception e) {
			; // ignore
		}
*/	}
}
