/*
 * Created on 19-Feb-2005
 */
package com.ixora.remote.security;

import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.ixora.remote.HostManagerComponent;
import com.ixora.remote.HostManagerConfigurationConstants;
import com.ixora.remote.security.exception.ConnectionNotAllowed;
import com.ixora.common.ConfigurationMgr;
import com.ixora.common.logging.AppLogger;
import com.ixora.common.logging.AppLoggerFactory;
import com.ixora.common.utils.Utils;

/**
 * @author Daniel Moraru
 */
public final class ConnectionChecker {
	/** Logger */
	private static final AppLogger logger = AppLoggerFactory.getLogger(ConnectionChecker.class);
	/** True if security is enabled */
	private boolean fEnabled;
	/** Lists of allowed hosts */
	private Set<String> fHosts;

	/**
	 * Constructor.
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws NoSuchMethodException
	 */
	public ConnectionChecker() throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
		super();
		fEnabled = ConfigurationMgr.getBoolean(HostManagerComponent.NAME, HostManagerConfigurationConstants.HOST_NAME_SECURITY_ENABLED);
		if(fEnabled) {
			fHosts = new HashSet<String>();
			List hosts = ConfigurationMgr.getList(HostManagerComponent.NAME,
					String.class, HostManagerConfigurationConstants.HOSTS_ALLOWED);
			if(!Utils.isEmptyCollection(hosts)) {
				for(Iterator iter = hosts.iterator(); iter.hasNext();) {
					String host = (String)iter.next();
					// get the IP address
					try {
						InetAddress ia = InetAddress.getByName(host);
						fHosts.add(ia.getHostAddress());
					}catch(UnknownHostException e) {
						logger.error("Failed to check host name " + host + ". Connections from this host will not be allowed.");
					}
				}
			}
		}
	}

	/**
	 * @param host host name to check
	 * @throws ConnectionNotAllowed
	 */
	public void check(String host) throws ConnectionNotAllowed {
		if(!fEnabled) {
			return;
		}
		try {
			InetAddress ia = InetAddress.getByName(host);
			if(fHosts.contains(ia.getHostAddress())) {
				return;
			}
		}catch(UnknownHostException e) {
			logger.error("Connection from " + host + " has been refused. Error: " + e.getMessage());
			throw new ConnectionNotAllowed();
		}
		logger.error("Connection from " + host + " has been refused");
		throw new ConnectionNotAllowed();
	}
}
