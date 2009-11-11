/*
 * Created on 25-Apr-2004
 */
package com.ixora.rms.repository;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import com.ixora.common.xml.exception.XMLException;
import com.ixora.rms.services.ProviderRepositoryService;

/**
 * @author Daniel Moraru
 */
public final class ProviderRepositoryManager implements ProviderRepositoryService {
	/** Underlying repository */
	private InstallationArtefactRepository repository;

	/**
	 * Constructor.
	 * @throws FileNotFoundException
	 * @throws XMLException
	 */
	public ProviderRepositoryManager() throws XMLException, FileNotFoundException {
		super();
		this.repository = new InstallationArtefactRepository("provider", new InstallationArtefactFactory() {
			public InstallationArtefact createArtefact() {
				return new ProviderInstallationData();
			}
		});
	}

	/**
	 * @see com.ixora.rms.services.ProviderRepositoryService#getInstalledProviders()
	 */
	public Map<String, ProviderInstallationData> getInstalledProviders() {
		// TODO revisit casting stuff
		Map<String, InstallationArtefact> providers = this.repository.getInstalledArtefacts();
		Map<String, ProviderInstallationData> ret = new HashMap<String, ProviderInstallationData>(providers.size());
		for(Map.Entry<String, InstallationArtefact> me : providers.entrySet()) {
			ret.put(me.getKey(), (ProviderInstallationData)me.getValue());
		}
		return ret;
	}

	/**
	 * @see com.ixora.common.Service#shutdown()
	 */
	public void shutdown() {
		; // nothing
	}
}
