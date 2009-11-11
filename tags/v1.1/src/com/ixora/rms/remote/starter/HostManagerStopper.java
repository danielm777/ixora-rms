package com.ixora.rms.remote.starter;

import java.net.InetAddress;

import org.apache.log4j.PropertyConfigurator;

import com.ixora.common.ConfigurationMgr;
import com.ixora.common.MessageRepository;
import com.ixora.common.RMIServices;
import com.ixora.common.remote.Shutdownable;
import com.ixora.common.utils.Utils;
import com.ixora.remote.HostManagerComponent;
import com.ixora.remote.HostManagerConfigurationConstants;
import com.ixora.remote.messages.Msg;

/**
 * 09-Sep-2005
 */

/**
 * It stops the HostManager process.
 * @author Daniel Moraru
 */
public class HostManagerStopper {

	static {
		// setup log4j
        PropertyConfigurator.configure(Utils.getPath("config/log4j.hm.properties"));
	}

	/**
	 * @param ars
	 * @param prs
	 */
	private HostManagerStopper() {
		super();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			MessageRepository.initialize(HostManagerComponent.NAME);

            if(System.getSecurityManager() == null) {
                System.setSecurityManager(new java.rmi.RMISecurityManager());
            }
            // host and port conf are not required
            Object hostObj = ConfigurationMgr.getObject(HostManagerComponent.NAME,
            		HostManagerConfigurationConstants.SOCKET_FACTORY_LOCAL_IP_ADDRESS);
            String host;
            if(hostObj == null) {
            	host = InetAddress.getLocalHost().getHostAddress();
            } else {
            	host = (String)hostObj;
            }
            int port = RMIServices.instance().getRMIRegistryPort();
    		String objName = "//" + host + ":" + port + "/HostManager";
    		Shutdownable hm;
    		try {
    			hm = (Shutdownable)java.rmi.Naming.lookup(objName);
    		} catch(Exception e) {
            	System.err.println(MessageRepository.get(
            			HostManagerComponent.NAME, Msg.HOST_MANAGER_ERROR_NOT_STARTED));
            	return;
    		}
	        if(hm == null) {
            	System.err.println(MessageRepository.get(
            			HostManagerComponent.NAME, Msg.HOST_MANAGER_ERROR_NOT_STARTED));
            	return;
	        }
	        try {
		        hm.shutdown();
	        } catch(Exception e) {
	        	; // ignore the exception; it will always happen
			}
        	System.out.println(MessageRepository.get(
        			HostManagerComponent.NAME, Msg.HOST_MANAGER_STOPPED));
		} catch(Throwable e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
}
