/*
 * Created on 30-Dec-2004
 */
package com.ixora.rms.repository;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.ixora.common.Product;
import com.ixora.common.logging.AppLogger;
import com.ixora.common.logging.AppLoggerFactory;
import com.ixora.common.utils.Utils;
import com.ixora.common.xml.XMLUtils;
import com.ixora.common.xml.exception.XMLException;

/**
 * Repository for installation artefacts.<br>
 * NOTE: not synchronized
 * @author Daniel Moraru
 */
public final class InstallationArtefactRepository {
	/** Logger */
	private static final AppLogger logger =
		AppLoggerFactory.getLogger(InstallationArtefactRepository.class);
	/**
	 * Map of installation artefacts keyed by their name.
	 */
	private Map<String, InstallationArtefact> fArtefacts;
	/** Artefact factory */
	private InstallationArtefactFactory fArtefactFactory;
	/** The type of artefacts */
	private String fArtefactName;
	/** Product root folder (installation folder) */
	private File fProductRoot;

	/**
	 * Constructor.
	 * @param repositoryName
	 * @param factory
	 * @throws FileNotFoundException
	 * @throws XMLException
	 */
	public InstallationArtefactRepository(String artefactName, InstallationArtefactFactory factory) throws XMLException, FileNotFoundException {
		this(Product.getProductInfo().getInstallationFolder(), artefactName, factory);
	}

	/**
	 * Constructor.
	 * @param productRoot
	 * @param productVersion
	 * @param repositoryName
	 * @param factory
	 * @throws FileNotFoundException
	 * @throws XMLException
	 */
	public InstallationArtefactRepository(
				File productRoot,
				String artefactName,
				InstallationArtefactFactory factory) throws XMLException, FileNotFoundException {
		super();
		this.fProductRoot = productRoot;
		this.fArtefactName = artefactName;
		this.fArtefactFactory = factory;
		this.fArtefacts = new HashMap<String, InstallationArtefact>();
		loadInstalledAtefacts();
	}

	/**
	 * @return all artefacts in the repository
	 */
	public Map<String, InstallationArtefact> getInstalledArtefacts() {
		return Collections.unmodifiableMap(fArtefacts);
	}

	/**
	 * @param installationId
	 * @return the folder where the given artefact is installed or null
	 * if the artefact with the given installation id is not found
	 */
	public File getInstallationFolderForArtefact(String installationId) {
		InstallationArtefact ia = this.fArtefacts.get(installationId);
		if(ia == null) {
			return null;
		}
		return new File(this.fProductRoot, "config/" + fArtefactName + "s/" + installationId);
	}

	/**
	 * Reloads the repository.
	 * @throws FileNotFoundException
	 * @throws XMLException
	 */
	public void reload() throws XMLException, FileNotFoundException {
		fArtefacts.clear();
		loadInstalledAtefacts();
	}

	/**
	 * Loads the installed artefacts.
	 * @throws FileNotFoundException
	 * @throws XMLException
	 */
	private void loadInstalledAtefacts() throws XMLException, FileNotFoundException {
		File repFolder = new File(this.fProductRoot, "config/" + fArtefactName + "s");
		if(!repFolder.isDirectory()) {
			throw new FileNotFoundException(repFolder.getAbsolutePath());
		}
		File[] list = Utils.listFilesForFolder(repFolder);
		File af;
		for(int i = 0; i < list.length; i++) {
			af = list[i];
			if(!af.isDirectory()) {
				continue;
			}
			BufferedInputStream bs = null;
			try {
				bs = new BufferedInputStream(
						new FileInputStream(new File(af, fArtefactName)));
				Document doc = XMLUtils.read(bs);
				Node n = doc.getFirstChild();
				InstallationArtefact ia = fArtefactFactory.createArtefact();
				ia.fromXML(n);
				fArtefacts.put(ia.getInstallationIdentifier(), ia);
			} catch (Exception e) {
				logger.error(e);
			} finally {
				if(bs != null) {
					try {
						bs.close();
					} catch (IOException e1) {
					}
				}
			}
		}
	}
}
