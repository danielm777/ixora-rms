/*
 * Created on 07-Feb-2005
 */
package com.ixora.rms.repository;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Observable;

import org.w3c.dom.Document;

import com.ixora.common.ComponentVersion;
import com.ixora.common.Product;
import com.ixora.common.ProgressMonitor;
import com.ixora.common.utils.FileFilterExcludeDevArtefacts;
import com.ixora.common.utils.Utils;
import com.ixora.common.xml.XMLUtils;
import com.ixora.common.xml.exception.XMLException;
import com.ixora.rms.EntityId;
import com.ixora.rms.exception.AgentIsNotInstalled;
import com.ixora.rms.exception.InvalidAgentPackage;
import com.ixora.rms.exception.RMSException;
import com.ixora.rms.repository.exception.AgentAlreadyInstalled;
import com.ixora.rms.repository.exception.FailedToUninstallAgent;
import com.ixora.rms.services.AgentInstallerService;

/**
 * @author Daniel Moraru
 */
public final class AgentInstaller extends Observable implements AgentInstallerService {
	private DataViewRepositoryManager fDataViewRepository;
	private DashboardRepositoryManager fDashboardRepository;
	private ProviderInstanceRepositoryManager fProviderInstanceRepository;
	private AgentRepositoryManager fAgentRepository;

	/**
	 * Constructor.
	 * @param agentRepository
	 * @param providerInstanceRepository
	 * @param dashboardRepository
	 * @param dataViewRepository
	 */
	public AgentInstaller(
			AgentRepositoryManager agentRepository,
			DataViewRepositoryManager dataViewRepository,
			DashboardRepositoryManager dashboardRepository,
			ProviderInstanceRepositoryManager providerInstanceRepository) {
		super();
		fAgentRepository = agentRepository;
		fDataViewRepository = dataViewRepository;
		fDashboardRepository = dashboardRepository;
		fProviderInstanceRepository = providerInstanceRepository;
	}

	/**
	 * @see com.ixora.rms.services.AgentInstallerService#install(com.ixora.rms.repository.AgentInstallationData)
	 */
	public void install(AgentInstallationData aid) throws RMSException {
		try {
			String agentId = aid.getAgentInstallationId();
			// make folder /config/agents/{agentid} and write the agent installation data
			File folder = new File(Utils.getPath("config/agents/" + aid.getAgentInstallationId()));
			if(folder.exists() || folder.isDirectory()) {
				throw new AgentAlreadyInstalled(agentId);
			}
			folder.mkdir();
			File file = new File(folder, "agent");
			file.createNewFile();
			BufferedOutputStream bos = null;
			try {
				bos = new BufferedOutputStream(new FileOutputStream(file));
				Document doc = XMLUtils.createEmptyDocument("agent");
				aid.toXML(doc.getDocumentElement());
				XMLUtils.write(doc, bos);
			}
			finally {
				if(bos != null) {
					bos.close();
				}
			}
			// make folder /config/repository/{agentid}
			folder = new File(Utils.getPath("config/repository/" + aid.getAgentInstallationId()));
			folder.mkdir();
			setChanged();
			notifyObservers();
		} catch(RMSException e) {
			throw e;
		} catch(Exception e) {
			throw new RMSException(e);
		}
	}

	/**
	 * @see com.ixora.rms.services.AgentInstallerService#uninstall(java.lang.String)
	 */
	public void uninstall(String agentId) throws RMSException {
		try {
			// remove folder /config/agents/{agentid} and write the agent installation data
			File folder = new File(Utils.getPath("config/agents/" + agentId));
			Utils.deleteFolderContent(folder);
			folder.delete();
			// remove folder /config/repository/{agentid}
			folder = new File(Utils.getPath("config/repository/" + agentId));
			Utils.deleteFolderContent(folder);
			if(folder.exists() && !folder.delete()) {
				throw new FailedToUninstallAgent(agentId,
						"Can't delete folder " + folder.getAbsolutePath());
			}
			setChanged();
			notifyObservers();
		} catch(IOException e) {
			throw new RMSException(e);
		}
	}

	/**
	 * @see com.ixora.rms.services.AgentInstallerService#update(com.ixora.rms.repository.AgentInstallationData)
	 */
	public void update(AgentInstallationData aid) throws RMSException {
		try {
			// make folder /config/agents/{agentid} and write the agent installation data
			File folder = new File(Utils.getPath("config/agents/" + aid.getAgentInstallationId()));
			if(!folder.exists()) {
				throw new AgentIsNotInstalled(aid.getAgentInstallationId());
			}
			File file = new File(folder, "agent");
			if(!file.exists()) {;
				file.createNewFile();
			}
			BufferedOutputStream bos = null;
			try {
				bos = new BufferedOutputStream(new FileOutputStream(file));
				Document doc = XMLUtils.createEmptyDocument("agent");
				aid.toXML(doc.getDocumentElement());
				XMLUtils.write(doc, bos);
			}
			finally {
				if(bos != null) {
					bos.close();
				}
			}
			setChanged();
			notifyObservers();
		} catch(RMSException e) {
			throw e;
		} catch(Exception e) {
			throw new RMSException(e);
		}
	}

	/**
	 * @see com.ixora.rms.services.AgentInstallerService#installFromPackage(File
	 * , boolean, ProgressMonitor)
	 */
	public void installFromPackage(File agentPackage, boolean updateIfExists, ProgressMonitor progress)
			throws InvalidAgentPackage, RMSException {
		// There are these steps to do
		// 1. 		backup custom artefacts
		// 2.		copy new agent files
		// 3. 		reload artefacts and append the saved custom ones
		progress.setMax(3);
		try {
			// save all custom artefacts
			progress.setTask("saving custom artefacts...");
			String agentId = Utils.getFileName(agentPackage);

			// data views
			List<DataView> customDataViewsAgent = new LinkedList<DataView>();
			DataViewMap dvs = fDataViewRepository.getAgentDataViews(agentId);
			if(dvs != null) {
				Collection<DataView> coll = dvs.getAll();
				if(!Utils.isEmptyCollection(coll)) {
					for(DataView dv : coll) {
						if(!dv.isSystem()) {
							customDataViewsAgent.add(dv);
						}
					}
				}
			}
			Map<EntityId, List<DataView>> customDataViewsEntity = new HashMap<EntityId, List<DataView>>();
			Map<EntityId, DataViewMap> map = fDataViewRepository.getEntityDataViews(agentId);
			if(!Utils.isEmptyMap(map)) {
				for(Map.Entry<EntityId, DataViewMap> entry : map.entrySet()) {
					dvs = entry.getValue();
					Collection<DataView> coll = dvs.getAll();
					if(!Utils.isEmptyCollection(coll)) {
						for(DataView dv : coll) {
							if(!dv.isSystem()) {
								List<DataView> lst = customDataViewsEntity.get(entry.getKey());
								if(lst == null) {
									lst = new LinkedList<DataView>();
									customDataViewsEntity.put(entry.getKey(), lst);
								}
								lst.add(dv);
							}
						}
					}
				}
			}

			// dashboards
			List<Dashboard> customDashboardsAgent = new LinkedList<Dashboard>();
			DashboardMap dbs = fDashboardRepository.getAgentDashboards(agentId);
			if(dbs != null) {
				Collection<Dashboard> coll = dbs.getAll();
				if(!Utils.isEmptyCollection(coll)) {
					for(Dashboard db : coll) {
						if(!db.isSystem()) {
							customDashboardsAgent.add(db);
						}
					}
				}
			}
			Map<EntityId, List<Dashboard>> customDashboardsEntity = new HashMap<EntityId, List<Dashboard>>();
			Map<EntityId, DashboardMap> map2 = fDashboardRepository.getEntityDashboards(agentId);
			if(!Utils.isEmptyMap(map2)) {
				for(Map.Entry<EntityId, DashboardMap> entry : map2.entrySet()) {
					dbs = entry.getValue();
					Collection<Dashboard> coll = dbs.getAll();
					if(!Utils.isEmptyCollection(coll)) {
						for(Dashboard db : coll) {
							if(!db.isSystem()) {
								List<Dashboard> lst = customDashboardsEntity.get(entry.getKey());
								if(lst == null) {
									lst = new LinkedList<Dashboard>();
									customDashboardsEntity.put(entry.getKey(), lst);
								}
								lst.add(db);
							}
						}
					}
				}
			}

			// providers
			List<ProviderInstance> customProviderInstances = new LinkedList<ProviderInstance>();
			ProviderInstanceMap pis = fProviderInstanceRepository.getAgentProviderInstances(agentId);
			if(pis != null) {
				Collection<ProviderInstance> coll = pis.getAll();
				if(!Utils.isEmptyCollection(coll)) {
					for(ProviderInstance pi : coll) {
						if(!pi.isSystem()) {
							customProviderInstances.add(pi);
						}
					}
				}
			}
			progress.setDelta(1);

			// create or replace required files
			progress.setTask("creating files...");
			File destFile = new File(Utils.getPath("/tmp"));
			Utils.unzipToFolder(agentPackage, destFile);
			progress.setDelta(1);

			// reload repositories
			progress.setTask("restoring custom artefacts...");
			fDashboardRepository.reload();
			fDataViewRepository.reload();
			fProviderInstanceRepository.reload();

			// append the custom artefacts
			// dataviews
			dvs = fDataViewRepository.getAgentDataViews(agentId);
			for(DataView dv : customDataViewsAgent) {
				if(dvs == null) {
					dvs = new DataViewMap();
				}
				dvs.add(dv);
				fDataViewRepository.setAgentDataViews(agentId, dvs);
			}

			for(Map.Entry<EntityId, List<DataView>> entry : customDataViewsEntity.entrySet()) {
				EntityId eid = entry.getKey();
				dvs = fDataViewRepository.getEntityDataViews(agentId, eid);
				if(dvs == null) {
					dvs = new DataViewMap();
				}
				for(DataView dv : entry.getValue()) {
					dvs.add(dv);
				}
				fDataViewRepository.setEntityDataViews(agentId, eid, dvs);
			}
			fDashboardRepository.save();

			// dashboards
			dbs = fDashboardRepository.getAgentDashboards(agentId);
			for(Dashboard db : customDashboardsAgent) {
				if(dbs == null) {
					dbs = new DashboardMap();
				}
				dbs.add(db);
				fDashboardRepository.setAgentDashboards(agentId, dbs);
			}

			for(Map.Entry<EntityId, List<Dashboard>> entry : customDashboardsEntity.entrySet()) {
				EntityId eid = entry.getKey();
				dbs = fDashboardRepository.getEntityDashboards(agentId, eid);
				if(dbs == null) {
					dbs = new DashboardMap();
				}
				for(Dashboard db : entry.getValue()) {
					dbs.add(db);
				}
				fDashboardRepository.setEntityDashboards(agentId, eid, dbs);
			}
			fDashboardRepository.save();

			// provider instances
			pis = fProviderInstanceRepository.getAgentProviderInstances(agentId);
			for(ProviderInstance pi : customProviderInstances) {
				if(pis == null) {
					pis = new ProviderInstanceMap();
				}
				pis.add(pi);
				fProviderInstanceRepository.setAgentProviderInstances(agentId, pis);
			}
			fProviderInstanceRepository.save();
			progress.setDelta(1);
			setChanged();
			notifyObservers();
		} catch(Exception e) {
			throw new RMSException(e);
		} finally {
			progress.done();
		}
	}

	/**
	 * @throws IOException
	 * @throws RMSException
	 * @throws XMLException
	 * @see com.ixora.rms.services.AgentInstallerService#export(java.lang.String, java.io.File, ProgressMonitor)
	 */
	public File export(
				String agentInstallationId,
				File destinationFolder,
				ProgressMonitor progress) throws IOException, RMSException, XMLException {
		return export(
				null,
				agentInstallationId, destinationFolder, progress);
	}

	/**
	 * @throws RMSException
	 * @throws IOException
	 * @throws IOException
	 * @throws XMLException
	 * @see com.ixora.rms.services.AgentInstallerService#installFromFilesystem(java.io.File, com.ixora.common.ProgressMonitor)
	 */
	public void installFromFilesystem(File productRoot, ProgressMonitor progress, String agentInstallationId) throws RMSException, IOException, IOException, XMLException {
		// check first that the version of the product that we are importing from
		// is less or equal to this product's version
		ComponentVersion versionThis = Product.getProductInfo().getVersion();
		Product thatProdInfo = null;
		try {
			thatProdInfo = Product.getProductInfo(productRoot);
		} catch(Exception e) {
			; // ignore, probably an old version
		}
		ComponentVersion versionThat = (thatProdInfo == null ? null : thatProdInfo.getVersion());
		if(versionThat != null && versionThis.compareTo(versionThat) < 0) {
			// TODO localize
			throw new RMSException("The version of the product that you are trying to import the agent from " +
					"is greater than the version of the product you are trying to import into.");
		}
		// at the moment we only support importing custom agents
		Map<String, AgentInstallationData> agents = this.fAgentRepository.getInstalledAgents(productRoot);
		AgentInstallationData aid = agents.get(agentInstallationId);
		if(aid == null) {
			// TODO localize
			throw new RMSException("Agent with identifier "
					+ agentInstallationId
					+ " was not found in product installation at " + productRoot.getAbsolutePath());
		}
		agents = fAgentRepository.getInstalledAgents();
		if(agents.get(agentInstallationId) != null) {
			// TODO localize
			throw new RMSException("Agent with identifier " + agentInstallationId + " is already installed");
		}
		// export first then install as a package
		File tmpFolder = Utils.getSystemTempFolder();
		File tmpAgentPackage = export(productRoot, agentInstallationId, tmpFolder, progress);

		installFromPackage(tmpAgentPackage, false, progress);
	}

	/**
	 * @param productRoot
	 * @param agentInstallationId
	 * @param destinationFolder
	 * @param progress
	 * @return the agent package file
	 * @throws IOException
	 * @throws RMSException
	 * @throws XMLException
	 */
	public File export(File productRoot, String agentInstallationId, File destinationFolder, ProgressMonitor progress) throws IOException, RMSException, XMLException {
		try {
			// productVersion is not used for the moment... as so far the current version
			// is backward compatible...
			progress.setMax(3);
			// copy required files into a tmp folder
			File tmpFolder = new File(destinationFolder, "agent" + Utils.getRandomInt());
			Utils.deleteFolderContent(tmpFolder);
			// create all folders
			File configFolder = new File(tmpFolder, "config");
			File configAgentsFolder = new File(
					new File(configFolder, "agents"), agentInstallationId);
			File configMessagesFolder = new File(configFolder, "messages");
			File configRepositoryFolder = new File(
					new File(configFolder, "repository"), agentInstallationId);
			// setup source folders
			File homeFolder;
			if(productRoot == null) {
				homeFolder = Product.getProductInfo().getInstallationFolder();
			} else {
				homeFolder = productRoot;
			}
			File homeConfigFolder = new File(homeFolder, "config");
			File homeConfigAgentsFolder = new File(
					new File(homeConfigFolder, "agents"), agentInstallationId);
			File homeConfigMessagesFolder = new File(homeConfigFolder, "messages");
			File homeConfigRepositoryFolder = new File(
					new File(homeConfigFolder, "repository"), agentInstallationId);

			// copy messages
			File sourceFile = new File(homeConfigMessagesFolder, agentInstallationId + ".properties");
			if(sourceFile.exists()) {
				Utils.copyFile(
						sourceFile,
						new File(configMessagesFolder, agentInstallationId + ".properties"));
			}
			// copy agent definition file
			Utils.copyFile(
					new File(homeConfigAgentsFolder, "agent"),
					new File(configAgentsFolder, "agent"));

			progress.setTask("copying artefacts...");
			// copy artefacts
			File[] artefactFiles = Utils.listFilesForFolder(homeConfigRepositoryFolder);
			if(!Utils.isEmptyArray(artefactFiles)) {
				for(File artefactFile : artefactFiles) {
					Utils.copyFile(
							artefactFile,
							new File(configRepositoryFolder, artefactFile.getName()));
				}
			}
			progress.setDelta(1);
			// copy jars
			Map<String, AgentInstallationData> agents;
			if(productRoot == null) {
				agents = fAgentRepository.getInstalledAgents();
			} else {
				agents = fAgentRepository.getInstalledAgents(productRoot);
			}

			AgentInstallationData aid = agents.get(agentInstallationId);
			VersionableAgentInstallationDataMap vmap = aid.getVersionData();
			Collection<VersionableAgentInstallationData> vaids = vmap.getAll();
			List<String> jarsList = new LinkedList<String>();
			List<String> uijarsList = new LinkedList<String>();
			List<String> natlibsList = new LinkedList<String>();
			if(!Utils.isEmptyCollection(vaids)) {
				for(VersionableAgentInstallationData vaid : vaids) {
					String[] jarsArr = vaid.getJars();
					if(!Utils.isEmptyArray(jarsArr)) {
						jarsList.addAll(Arrays.asList(jarsArr));
					}
					String[] natlibsArr = vaid.getNativeLibraries();
					if(!Utils.isEmptyArray(natlibsArr)) {
						natlibsList.addAll(Arrays.asList(natlibsArr));
					}
					String uiJar = vaid.getUIJar();
					if(!Utils.isEmptyString(uiJar)) {
						uijarsList.add(uiJar);
					}
				}
			}
			progress.setTask("copying jars...");
			for(String jar : jarsList) {
				String filePath = jar;
				if(!jar.startsWith("/") && !jar.startsWith("\\")) {
					filePath = "/" + filePath;
				}
				sourceFile = new File(homeFolder.getAbsolutePath() + filePath);
				File destFile = new File(tmpFolder.getAbsolutePath() + filePath);
				Utils.copyFile(sourceFile, destFile);
			}
			if(!Utils.isEmptyCollection(uijarsList)) {
				for(String uijar : uijarsList) {
					String filePath = uijar;
					if(!uijar.startsWith("/") && !uijar.startsWith("\\")) {
						filePath = "/" + filePath;
					}
					sourceFile = new File(homeFolder.getAbsolutePath() + filePath);
					File destFile = new File(tmpFolder.getAbsolutePath() + filePath);
					Utils.copyFile(sourceFile, destFile);
				}
			}
			progress.setDelta(1);
			progress.setTask("copying native libraries...");
			for(String natlib : natlibsList) {
				String filePath = natlib;
				if(!natlib.startsWith("/") && !natlib.startsWith("\\")) {
					filePath = "/" + filePath;
				}
				sourceFile = new File(homeFolder.getAbsolutePath() + filePath + ".dll");
				if(sourceFile.exists()) {
					File destFile = new File(tmpFolder.getAbsolutePath() + filePath + ".dll");
					Utils.copyFile(sourceFile, destFile);
				}
				sourceFile = new File(homeFolder.getAbsolutePath() + filePath + ".so");
				if(sourceFile.exists()) {
					File destFile = new File(tmpFolder.getAbsolutePath() + filePath + ".so");
					Utils.copyFile(sourceFile, destFile);
				}
			}
			File zipFile = new File(destinationFolder, agentInstallationId + ".agent");
			Utils.zipFolder(tmpFolder, zipFile, new FileFilterExcludeDevArtefacts());
			Utils.deleteFolderContent(tmpFolder);
			tmpFolder.delete();
			return zipFile;
		} finally {
			progress.done();
		}
	}
}
