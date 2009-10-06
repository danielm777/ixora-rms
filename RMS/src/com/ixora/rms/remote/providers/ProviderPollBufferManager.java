/**
 * 19-Mar-2006
 */
package com.ixora.rms.remote.providers;

import com.ixora.common.collections.CircullarLinkedList;
import com.ixora.rms.providers.HostProviderManager;
import com.ixora.rms.providers.ProviderDataBuffer;
import com.ixora.rms.providers.ProviderId;
import com.ixora.rms.providers.ProviderPollBuffer;
import com.ixora.rms.providers.ProviderState;

/**
 * Holds data between polling cycles.
 * @author Daniel Moraru
 */
public class ProviderPollBufferManager implements HostProviderManager.Listener {
	private CircullarLinkedList<ProviderDataBuffer[]> fDataBuffers;
	private CircullarLinkedList<ProviderPollBuffer.StateChange> fStateChanges;

	/**
	 *
	 */
	public ProviderPollBufferManager() {
		super();
		fDataBuffers = new CircullarLinkedList<ProviderDataBuffer[]>(200);
		fStateChanges = new CircullarLinkedList<ProviderPollBuffer.StateChange>(50);
	}

	/**
	 * @return
	 */
	public synchronized ProviderPollBuffer getProviderPullBuffer() {
		ProviderPollBuffer buff = new ProviderPollBuffer(
				fDataBuffers.size() == 0 ?
						null : fDataBuffers.toArray(new ProviderDataBuffer[fDataBuffers.size()][]),
				fStateChanges.size() == 0 ?
						null : fStateChanges.toArray(new ProviderPollBuffer.StateChange[fStateChanges.size()])
		);
		fDataBuffers.clear();
		fStateChanges.clear();
		return buff;
	}

	/**
	 * @see com.ixora.rms.providers.HostProviderManager.Listener#providerStateChanged(com.ixora.rms.providers.ProviderId, com.ixora.rms.providers.ProviderState, java.lang.Throwable)
	 */
	public void providerStateChanged(ProviderId provider, ProviderState state, Throwable err) {
		fStateChanges.add(new ProviderPollBuffer.StateChange(provider, state, err));
	}

	/**
	 * @see com.ixora.rms.providers.HostProviderManager.Listener#data(com.ixora.rms.providers.ProviderDataBuffer[])
	 */
	public void data(ProviderDataBuffer[] buff) {
		fDataBuffers.add(buff);
	}
}
