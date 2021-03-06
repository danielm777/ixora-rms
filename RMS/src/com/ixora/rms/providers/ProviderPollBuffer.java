/**
 * 19-Mar-2006
 */
package com.ixora.rms.providers;

import java.io.Serializable;

/**
 * This is the data retrieved from the remote agent manager by the global collector.
 * @author Daniel Moraru
 */
public class ProviderPollBuffer implements Serializable {
	private static final long serialVersionUID = -9082868169260501996L;

	public final static class StateChange implements Serializable {
		private static final long serialVersionUID = 1417328064495084970L;
		public ProviderId fProviderId;
		public ProviderState fProviderState;
		public Throwable fError;
		public StateChange(ProviderId providerId, ProviderState state, Throwable e) {
			super();
			fProviderId = providerId;
			fProviderState = state;
			fError = e;
		}
	}
	private ProviderDataBuffer[][] fDataBuffers;
	private StateChange[] fStateChanges;

	/**
	 * @param buffs
	 * @param sc
	 */
	public ProviderPollBuffer(
				ProviderDataBuffer[][] buffs,
				StateChange[] sc) {
		super();
		fDataBuffers = buffs;
		fStateChanges = sc;
	}

	/**
	 * @return
	 */
	public ProviderDataBuffer[][] getDataBuffers() {
		return fDataBuffers;
	}

	/**
	 * @return
	 */
	public StateChange[] getStateChanges() {
		return fStateChanges;
	}
}
