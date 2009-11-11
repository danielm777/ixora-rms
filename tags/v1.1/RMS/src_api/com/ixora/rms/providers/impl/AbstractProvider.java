/*
 * Created on 28-Dec-2004
 */
package com.ixora.rms.providers.impl;

import java.io.Serializable;

import com.ixora.rms.Collector;
import com.ixora.common.logging.AppLogger;
import com.ixora.common.logging.AppLoggerFactory;
import com.ixora.rms.exception.RMSException;
import com.ixora.rms.providers.Provider;
import com.ixora.rms.providers.ProviderConfiguration;
import com.ixora.rms.providers.ProviderDataBuffer;
import com.ixora.rms.providers.ProviderDataBufferImpl;
import com.ixora.rms.providers.ProviderId;
import com.ixora.rms.providers.exception.InvalidProviderConfiguration;

/**
 * @author Daniel Moraru
 */
public abstract class AbstractProvider implements Provider {
	/** Logger */
	private static final AppLogger logger = AppLoggerFactory.getLogger(AbstractProvider.class);
	/** Provider Id */
	protected ProviderId fId;
	/** Listener */
	protected Listener fListener;
	/** Configuration */
	protected ProviderConfiguration fConfiguration;
	/** Collector for providers that require a private collector */
	protected Collector fCollector;
	/**
	 * Object used for synchronization; this is necessary in
	 * order to support private collectors.
	 */
	protected Object fCollectionLock;
	/**
	 * Constructor.
	 */
	protected AbstractProvider() {
		super();
		fCollectionLock = new Object();
	}

	/**
	 * Constructor.
	 * @param useOwnCollector
	 */
	protected AbstractProvider(boolean useOwnCollector) {
		this();
		fCollectionLock = new Object();
		if(useOwnCollector) {
			this.fCollector = createCollector();
		}
	}

	/**
	 * Creates the private collector.
	 * @return
	 */
	private Collector createCollector() {
		return new Collector("Collector for provider " + getClass().getName()) {
			protected void collect() throws Throwable {
				collectData();
			}
		};
	}

	/**
	 * @see com.ixora.rms.providers.Provider#setId(com.ixora.rms.providers.ProviderId)
	 */
	public void setId(ProviderId id) {
		if(id == null) {
			throw new IllegalArgumentException("Null provider id");
		}
		this.fId = id;
	}

	/**
	 * @see com.ixora.rms.providers.Provider#setListener(com.ixora.rms.internal.providers.Provider.Listener)
	 */
	public void setListener(Listener l) {
		this.fListener = l;
	}

	/**
	 * By default this method has no implementation and suits
	 * active providers. Because providers must support a private collector
	 * this method must snchronize using the <code>fCollectionLock</code> object.
	 * @see com.ixora.rms.providers.Provider#collectData()
	 */
	public void collectData() throws Throwable {
		; // by default don't do anything to suit active providers
	}


	/**
	 * @see com.ixora.rms.providers.Provider#configure(com.ixora.rms.providers.ProviderConfiguration)
	 */
	public void configure(ProviderConfiguration conf)
			throws InvalidProviderConfiguration, RMSException, Throwable {
		// must synchronize with the collector
		synchronized(fCollectionLock) {
			if(fConfiguration == null) { // set activation settings
				fConfiguration = conf;
				if(fConfiguration.usePrivateCollector()) {
					if(fCollector == null) {
						fCollector = createCollector();
					}
				}
			} else { // set runtime settings
				fConfiguration.applyDelta(conf);
			}
			if(fCollector != null) {
				fCollector.setCollectionInterval(1000 * fConfiguration.getSamplingInterval().intValue());
			}
		}
	}

	/**
	 * @see com.ixora.common.Startable#start()
	 */
	public void start() throws Throwable {
		if(fCollector != null) {
			fCollector.startCollector();
		}
	}

	/**
	 * @see com.ixora.common.Startable#stop()
	 */
	public void stop() throws Throwable {
		if(fCollector != null) {
			fCollector.stopCollector();
		}
	}

	/**
	 * @see com.ixora.rms.providers.Provider#getConfiguration()
	 */
	public ProviderConfiguration getConfiguration() {
		return fConfiguration;
	}

	/**
	 * @see com.ixora.rms.providers.Provider#requiresExternalCollector()
	 */
	public boolean requiresExternalCollector() {
		return fCollector == null;
	}

	/**
	 * @param err
	 * @param t
	 */
	protected void fireError(String err, Throwable t) {
		if(this.fListener != null) {
			try {
				this.fListener.error(fId, err, t);
			} catch(Throwable e) {
				logger.error(e);
			}
		}
	}

	/**
	 * @param data
	 */
	protected void fireData(Serializable data) {
		if(this.fListener != null) {
			ProviderDataBuffer buff = new ProviderDataBufferImpl(fId, data);
			try {
				this.fListener.data(buff);
			} catch(Exception e) {
				logger.error(e);
			}
		}
	}
}
