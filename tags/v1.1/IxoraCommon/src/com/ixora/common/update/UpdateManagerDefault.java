package com.ixora.common.update;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.ixora.common.ConfigurationMgr;
import com.ixora.common.MessageRepository;
import com.ixora.common.Stopper;
import com.ixora.common.logging.AppLogger;
import com.ixora.common.logging.AppLoggerFactory;
import com.ixora.common.os.OSUtils;
import com.ixora.common.utils.Utils;
import com.ixora.common.xml.XMLUtils;
import com.ixora.common.xml.exception.XMLException;

/*
 * Created on Feb 13, 2004
 */
/**
 * @author Daniel Moraru
 */
public class UpdateManagerDefault implements UpdateManager {
	/** Logger */
	private static AppLogger logger = AppLoggerFactory.getLogger(UpdateManagerDefault.class);
	/** Updates URI */
	protected URI url;
	/** Resource bundle with update messages */
	protected MessageRepository bundle;
	/** XML document describing the updates */
	protected Document updates;
	/** Need to restart application */
	protected boolean needToRestartApplication;
	/** Cancel update */
	protected volatile boolean cancelUpdate;
	/**
	 * Key: module name
	 * Value: Map(Key: updateId, Value: ModuleUpdateDescriptor)
	 */
	protected Map<String, Map<UpdateId, ModuleUpdateDescriptor>> moduleUpdatesCache;
	/** Registered modules */
	protected Module[] registeredModules;

	// config info
	private String server = ConfigurationMgr.getString(
			UpdateComponent.NAME,
			UpdateConfigurationConstants.SERVER);
	private int port = ConfigurationMgr.getInt(
			UpdateComponent.NAME,
			UpdateConfigurationConstants.PORT);
	private String updatePath = ConfigurationMgr.getString(
			UpdateComponent.NAME,
			UpdateConfigurationConstants.SERVER_UPDATE_PATH);
	private String updateDoc =	ConfigurationMgr.getString(
			UpdateComponent.NAME,
			UpdateConfigurationConstants.UPDATEDOC);
	private File localUpdateRepository =
		new File(Utils.getSystemTempFolder(),
				ConfigurationMgr.getString(
					UpdateComponent.NAME,
					UpdateConfigurationConstants.LOCAL_UPDATE_FOLDER));


	/**
	 * Constructor.<br>
	 * Note: As little work must be done here (the golden rule
	 * of postponing unnecessary initialization holds here as well) as
	 * the this object will be created when the program starts to register
	 * updateable modules.
	 * @throws MalformedURLException
	 * @throws URISyntaxException
	 * @throws IOException if the local temporary update folder cannot be
	 * cleared
	 */
	public UpdateManagerDefault() throws URISyntaxException, IOException {
		super();
		registeredModules = UpdateMgr.getRegisteredModules();
		url = new URI("http", null, server, port, updatePath, null, null);
		if(localUpdateRepository.exists()) {
			if(localUpdateRepository.isDirectory()) {
				Utils.deleteFolderContent(localUpdateRepository);
			} else {
				localUpdateRepository.delete();
			}
		}
	}

	/**
	 * @see UpdateManager#getAvailableUpdates(java.util.Locale, Module)
	 */
	public synchronized ModuleUpdateDescriptor[] getAvailableUpdates(
		Module module) throws XMLException, IOException {
		if(moduleUpdatesCache == null) {
			loadUpdateDescriptors();
		}
		Map<UpdateId, ModuleUpdateDescriptor> m = moduleUpdatesCache.get(module.getName());
		if(m == null || m.size() == 0) {
			return null;
		}
		return (ModuleUpdateDescriptor[])m.values().toArray(
				new ModuleUpdateDescriptor[m.size()]);
	}

	/**
	 * @see update.UpdateManager#refresh()
	 */
	public synchronized void refresh() throws IOException, XMLException {
		loadUpdateDescriptors();
	}

	/**
	 * @see update.UpdateManager#getRegisteredModules()
	 */
	public Module[] getRegisteredModules() {
		return this.registeredModules;
	}

	/**
	 * @see update.UpdateManager#getLatestUpdate(update.Module)
	 */
	public synchronized ModuleUpdateDescriptor getLatestUpdate(Module module) throws IOException, XMLException {
		ModuleUpdateDescriptor[] mds = getAvailableUpdates(module);
		if(mds == null) {
			return null;
		}
		Arrays.sort(mds);
		return mds[mds.length - 1];
	}

	/**
	 * @see update.UpdateManager#installAllUpdates()
	 */
	public synchronized void installAllUpdates() throws IOException, XMLException {
		Module m;
		ModuleUpdateDescriptor mud;
		for(int i = 0; i < registeredModules.length; i++) {
			m = registeredModules[i];
			mud = getLatestUpdate(m);
			if(mud != null) {
				installUpdate(m, mud.getUpdateId());
			}
		}
	}

	/**
	 * @see UpdateManager#installUpdate(update.Module, UpdateId)
	 */
	public synchronized void installUpdate(Module module, UpdateId update) throws IOException {
		loadUpdate(module, update);
	}

	/**
	 * @see update.UpdateManager#cancelUpdateInstallation()
	 */
	public void cancelUpdateInstallation() {
		cancelUpdate = true;
	}

	/**
	 * @see update.UpdateManager#needToRestartApplication()
	 */
	public boolean needToRestartApplication() {
		return needToRestartApplication;
	}

	/**
	 * Loads the cache with available module updates descriptors.
	 * @throws XMLException
	 * @throws IOException
	 */
	private void loadUpdateDescriptors()
	throws XMLException, IOException {
		URI urlDoc = url.resolve(updateDoc);
		HttpURLConnection conn = (HttpURLConnection)urlDoc.toURL().openConnection();
		conn.setDoInput(true);
		this.updates = XMLUtils.read(
				new BufferedInputStream((InputStream)conn.getContent()));
		conn.disconnect();
		moduleUpdatesCache = new HashMap<String, Map<UpdateId, ModuleUpdateDescriptor>>();
		List<Node> lst = XMLUtils.findChildren(this.updates.getFirstChild(), "module");
		if(lst.size() == 0) {
			return;
		}

		// try and load the message bundle
		try {
			this.bundle = new MessageRepository(
					"updates",
					Locale.getDefault(),
					url.toURL());
		} catch(Exception e) {
			logger.error(e);
		}

		Node n;
		String moduleName;
		Module module;
		for(Iterator<Node> iter = lst.iterator(); iter.hasNext();) {
			n = iter.next();
			moduleName = XMLUtils.findAttribute(n, "name").getValue();
			List<Node> lst1 = XMLUtils.findChildren(n, "update");
			for(Iterator<Node> iter1 = lst1.iterator(); iter1.hasNext();) {
				n = iter1.next();
				ModuleUpdateDescriptor mud = new ModuleUpdateDescriptor(moduleName);
				mud.fromXML(n);
				// update the description and name with the
				// translated values
				if(this.bundle != null) {
					mud.setUpdateDescription(
						this.bundle.getMessage(mud.getUpdateDescription()));
				}
				module = getRegisteredModule(moduleName);
				if(module != null && mud.isForModule(module)) {
					// add it to the available updates
					Map<UpdateId, ModuleUpdateDescriptor> m = moduleUpdatesCache.get(moduleName);
					if(m == null) {
						m = new HashMap<UpdateId, ModuleUpdateDescriptor>();
						moduleUpdatesCache.put(moduleName, m);
					}
					m.put(mud.getUpdateId(), mud);
				}
			}
		}
	}

	/**
	 * Retrieves the content for the given update.
	 * @param uid
	 */
	private void loadUpdate(Module module, UpdateId uid) throws IOException {
		Map<UpdateId, ModuleUpdateDescriptor> m = this.moduleUpdatesCache.get(module.getName());
		if(m == null) {
			return;
		}
		ModuleUpdateDescriptor mud = (ModuleUpdateDescriptor)m.get(uid);
		UpdatePartDescriptor[] ucds = mud.getComponents(OSUtils.getOsName());
		UpdatePartDescriptor ucd;
		Stopper stopper = new Stopper() {
			public boolean stop() {
				return cancelUpdate;
			}
		};
		for(int i = 0; i < ucds.length; i++) {
			if(cancelUpdate) {
				return;
			}
			ucd = ucds[i];
			URI nurl = url.resolve(uid.toString() + "/").resolve(ucd.getContentLocation());
			HttpURLConnection conn = (HttpURLConnection)nurl.toURL().openConnection();
			conn.setDoInput(true);
			BufferedInputStream is = new BufferedInputStream(
					(InputStream)conn.getContent());
			UpdatePartInstaller.installUpdatePart(
				ucd, is, localUpdateRepository, stopper);
			conn.disconnect();
			if(ucd.needsAppRestart()) {
				needToRestartApplication = true;
			}
		}
		mud.setInstalled(true);
	}

	/**
	 * @param name
	 * @return the module with the given name
	 */
	private Module getRegisteredModule(String name) {
		Module m;
		for(int i = 0; i < registeredModules.length; i++) {
			m = registeredModules[i];
			if(m.getName().equals(name)) {
				return m;
			}
		}
		return null;
	}
}
