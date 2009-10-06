/*
 * Created on 30-Dec-2004
 */
package com.ixora.rms.services;

import java.util.Map;

import com.ixora.common.Service;
import com.ixora.rms.repository.DataViewBoardInstallationData;

/**
 * @author Daniel Moraru
 */
public interface DataViewBoardRepositoryService extends Service {
	/**
	 * @return all data view boards in the repository
	 */
	public Map<String, DataViewBoardInstallationData> getInstalledDataViewBoards();
}
