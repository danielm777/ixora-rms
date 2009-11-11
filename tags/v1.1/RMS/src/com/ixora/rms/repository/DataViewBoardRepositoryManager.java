/*
 * Created on 25-Apr-2004
 */
package com.ixora.rms.repository;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.ixora.common.logging.AppLogger;
import com.ixora.common.logging.AppLoggerFactory;
import com.ixora.common.xml.exception.XMLException;
import com.ixora.rms.services.DataViewBoardRepositoryService;

/**
 * @author Daniel Moraru
 */
public final class DataViewBoardRepositoryManager implements DataViewBoardRepositoryService {
	/** Logger */
	private static final AppLogger logger = AppLoggerFactory.getLogger(InstallationArtefactRepository.class);
	/** Underlying repository */
	private InstallationArtefactRepository fRepository;

	/**
	 * Constructor.
	 * @throws FileNotFoundException
	 * @throws XMLException
	 */
	public DataViewBoardRepositoryManager() throws XMLException, FileNotFoundException {
		super();
		this.fRepository = new InstallationArtefactRepository("viewboard", new InstallationArtefactFactory() {
			public InstallationArtefact createArtefact() {
				return new DataViewBoardInstallationData();
			}
		});
	}

	/**
	 * @see com.ixora.common.Service#shutdown()
	 */
	public void shutdown() {
		; // nothing
	}

	/**
	 * @see com.ixora.rms.services.DataViewBoardRepositoryService#getInstalledDataViewBoards()
	 */
	public Map<String, DataViewBoardInstallationData> getInstalledDataViewBoards() {
		// TODO revisit casting stuff
		Map<String, InstallationArtefact> map = this.fRepository.getInstalledArtefacts();

		// sort in the order of preferences
		List<DataViewBoardInstallationData> lst = new ArrayList<DataViewBoardInstallationData>(map.size());
		for(InstallationArtefact ia : map.values()) {
			File folder = fRepository.getInstallationFolderForArtefact(ia.getInstallationIdentifier());
			DataViewBoardInstallationData board = (DataViewBoardInstallationData)ia;
			try {
				board.buildDataViewForSamples(folder);
			}catch(Exception e) {
				logger.error(e);
			}
			lst.add(board);
		}
		Collections.sort(lst);

		LinkedHashMap<String, DataViewBoardInstallationData> ret = new LinkedHashMap<String, DataViewBoardInstallationData>(map.size());
		for(DataViewBoardInstallationData bid : lst) {
			ret.put(bid.getInstallationIdentifier(), bid);
		}
		return ret;
	}
}
