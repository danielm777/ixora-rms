/**
 * 19-Mar-2006
 */
package com.ixora.rms.providers;

import java.util.List;

import com.ixora.rms.Collector;
import com.ixora.common.logging.AppLogger;
import com.ixora.common.logging.AppLoggerFactory;
import com.ixora.common.utils.Utils;
import com.ixora.rms.remote.providers.RemoteProviderManager;

/**
 * This class is used to collect data from remote HostProviderManagers
 * @author Daniel Moraru
 */
final class GlobalCollector extends Collector {
	/** Logger */
	private static final AppLogger logger = AppLoggerFactory.getLogger(GlobalCollector.class);

	/**
	 * Callback.
	 */
	public interface Callback {
		/**
		 * @return the list of remote provider managers for which
		 * to collect data.
		 */
		List<RemoteProviderManager> getRemoteProviderManagers();
	}

	private HostProviderManager.Listener fListener;
	private Callback fCallback;

	/**
	 * @param listener
	 * @param callback
	 */
	public GlobalCollector(HostProviderManager.Listener listener, Callback callback) {
		super("GlobalCollectorProviders");
		if(listener == null) {
			throw new IllegalArgumentException("Null listener");
		}
		if(callback == null) {
			throw new IllegalArgumentException("Null callback");
		}
		fListener = listener;
		fCallback = callback;
	}

	/**
	 * @see com.ixora.rms.Collector#collect()
	 */
	protected void collect() throws Throwable {
		try {
			List<RemoteProviderManager> rpms = fCallback.getRemoteProviderManagers();
			if(Utils.isEmptyCollection(rpms)) {
				return;
			}
			for(RemoteProviderManager rpm : rpms) {
				if(rpm == null) {
					continue;
				}
				ProviderPollBuffer buff = rpm.getProviderPollBuffer();
				ProviderDataBuffer[][] pdbs = buff.getDataBuffers();
				if(!Utils.isEmptyArray(pdbs)) {
					for(ProviderDataBuffer[] pdb : pdbs) {
						fListener.data(pdb);
					}
				}

				ProviderPollBuffer.StateChange[] scs = buff.getStateChanges();
				if(!Utils.isEmptyArray(scs)) {
					for(ProviderPollBuffer.StateChange sc : scs) {
						fListener.providerStateChanged(sc.fProviderId,
								sc.fProviderState, sc.fError);
					}
				}
			}
		} catch(Throwable e) {
			logger.error(e);
		}
	}
}
