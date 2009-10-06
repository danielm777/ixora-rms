/*
 * Created on 27-Jul-2004
 */
package com.ixora.rms;

import java.util.HashMap;
import java.util.Map;

import com.ixora.common.logging.AppLogger;
import com.ixora.common.logging.AppLoggerFactory;

/**
 * Cache with remote hosts time difference.
 * @author Daniel Moraru
 */
public final class TimeDeltaCache {
	/** Logger */
	private static final AppLogger logger = AppLoggerFactory.getLogger(TimeDeltaCache.class);

	/**
	 * Cache of host time differences.
	 * Key: host
	 * Value: Long time difference between this host
	 * and the remote one
	 */
	private Map<String, Long> cache;
	/** Host monitor */
	private HostMonitor hostMonitor;

	/**
	 * TimeDeltaCache.
	 * @param hm
	 */
	public TimeDeltaCache(HostMonitor hm) {
		this.hostMonitor = hm;
		this.cache = new HashMap<String, Long>();
	}

	/**
	 * @param host
	 * @return the time delta for the given host
	 */
	public synchronized long getDelta(String host) {
		Long d = cache.get(host);
		if(d == null) {
			try {
				HostInformation dtls = this.hostMonitor.getHostInfo(host);
				if(dtls != null) {
					long ld = dtls.getDeltaTime();
					cache.put(host, new Long(ld));
					return ld;
				}
			} catch(Exception e) {
				logger.error(e);
			}
		}
		return 0;
	}
}
