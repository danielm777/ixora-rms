/*
 * Created on 25-Apr-2004
 */
package com.ixora.rms.repository;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import com.ixora.common.xml.exception.XMLException;
import com.ixora.rms.services.ParserRepositoryService;

/**
 * @author Daniel Moraru
 */
public final class ParserRepositoryManager implements ParserRepositoryService {
	/** Underlying repository */
	private InstallationArtefactRepository repository;

	/**
	 * Constructor.
	 * @throws FileNotFoundException
	 * @throws XMLException
	 */
	public ParserRepositoryManager() throws XMLException, FileNotFoundException {
		super();
		this.repository = new InstallationArtefactRepository("parser", new InstallationArtefactFactory() {
			public InstallationArtefact createArtefact() {
				return new ParserInstallationData();
			}
		});
	}

	/**
	 * @see com.ixora.rms.services.ParserRepositoryService#getInstalledProvidersParser()
	 */
	public Map<String, ParserInstallationData> getInstalledParsers() {
		// TODO revisit casting stuff
		Map<String, InstallationArtefact> parsers = this.repository.getInstalledArtefacts();
		Map<String, ParserInstallationData> ret = new HashMap<String, ParserInstallationData>(parsers.size());
		for(Map.Entry<String, InstallationArtefact> me : parsers.entrySet()) {
			ret.put(me.getKey(), (ParserInstallationData)me.getValue());
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
