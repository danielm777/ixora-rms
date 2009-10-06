/*
 * Created on 30-Dec-2004
 */
package com.ixora.rms.services;

import java.util.Map;

import com.ixora.common.Service;
import com.ixora.rms.repository.ParserInstallationData;

/**
 * @author Daniel Moraru
 */
public interface ParserRepositoryService extends Service {
	/**
	 * @return all parsers in the repository
	 */
	public Map<String, ParserInstallationData> getInstalledParsers();
}
