package com.ixora.rms.remote;

import java.net.InetAddress;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import org.apache.log4j.PropertyConfigurator;

import com.ixora.common.ConfigurationMgr;
import com.ixora.common.MessageRepository;
import com.ixora.common.RMIServices;
import com.ixora.common.logging.AppLogger;
import com.ixora.common.logging.AppLoggerFactory;
import com.ixora.common.remote.ClientSocketFactory;
import com.ixora.common.remote.ServerSocketFactory;
import com.ixora.common.utils.Utils;
import com.ixora.remote.HostManager;
import com.ixora.remote.HostManagerComponent;
import com.ixora.remote.HostManagerConfigurationConstants;
import com.ixora.remote.HostManagerImpl;
import com.ixora.remote.messages.Msg;

/**
 * @author Daniel Moraru
 */
public final class HostManagerMain {
	/** Logger */
	private static final AppLogger logger;

	static {
		// setup log4j
        PropertyConfigurator.configure(Utils.getPath("config/log4j.hm.properties"));
		logger = AppLoggerFactory.getLogger(HostManagerMain.class);
	}

	/**
	 * Application entry point.
	 * @param args
	 */
	public static void main(String[] args) {
        try {
            // initialize the message repository, set the default
            // repository to the repository for the main application component
            MessageRepository.initialize(HostManagerComponent.NAME);
            int regPort = RMIServices.instance().getRMIRegistryPort();;
            String localIpAdd = ConfigurationMgr.getString(
            		HostManagerComponent.NAME, HostManagerConfigurationConstants.SOCKET_FACTORY_LOCAL_IP_ADDRESS);
            Registry reg = null;
            HostManager hm = null;
            ClientSocketFactory csf = null; // default
            ServerSocketFactory ssf = null; // default
            if(Utils.isEmptyString(localIpAdd)) {
            	// no specific address defined
            	// tell RMI to use the fully qualified name for this host
            	// for object references
            	System.setProperty("java.rmi.server.useLocalHostname", "true");
	            if(System.getSecurityManager() == null) {
	                System.setSecurityManager(new java.rmi.RMISecurityManager());
	            }
	            ssf = new ServerSocketFactory(null); // all ip addresses
            	reg = LocateRegistry.createRegistry(regPort, null, ssf);
            } else {
	            // need to set this to make sure remote object references
	            // contain the correct network address
	            System.setProperty("java.rmi.server.hostname", localIpAdd);
	            if(System.getSecurityManager() == null) {
	                System.setSecurityManager(new java.rmi.RMISecurityManager());
	            }
	            InetAddress inetAdd = InetAddress.getByName(localIpAdd);
	            csf = new ClientSocketFactory(inetAdd);
	            ssf = new ServerSocketFactory(inetAdd);
	            reg = LocateRegistry.createRegistry(regPort, csf, ssf);
            }
            hm = new HostManagerImpl(ssf, csf);
            reg.rebind("HostManager", hm);
            logger.info(MessageRepository.get(Msg.HOST_MANAGER_HOST_MANAGER_STARTED_ON_IP_ADDRESS,
	            				new String[]{Utils.isEmptyString(localIpAdd) ? "[all]" : localIpAdd, String.valueOf(regPort)}));
        } catch(Exception e) {
            logger.error(e);
            System.exit(1);
        }
	}
}
