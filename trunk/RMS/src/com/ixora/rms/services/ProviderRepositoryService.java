/*
 * Created on 30-Dec-2004
 */
package com.ixora.rms.services;

import java.util.Map;

import com.ixora.common.Service;
import com.ixora.rms.repository.ProviderInstallationData;

/**
 * @author Daniel Moraru
 */
public interface ProviderRepositoryService extends Service {
	/**
	 * @return all providers in the repository
	 */
	public Map<String, ProviderInstallationData> getInstalledProviders();
}
