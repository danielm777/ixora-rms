package com.ixora.rms.batch.starter;

import java.net.InetAddress;

import org.apache.log4j.PropertyConfigurator;

import com.ixora.common.MessageRepository;
import com.ixora.common.RMIServices;
import com.ixora.common.remote.Shutdownable;
import com.ixora.common.utils.Utils;
import com.ixora.rms.batch.BatchComponent;
import com.ixora.rms.batch.messages.Msg;

/**
 * 31-May-2006
 */

/**
 * It stops the RMSBatch process.
 * @author Daniel Moraru
 */
public class BatchStopper {

	static {
		// setup log4j
        PropertyConfigurator.configure(Utils.getPath("config/log4j.batch.properties"));
	}

	/**
	 * @param ars
	 * @param prs
	 */
	private BatchStopper() {
		super();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			MessageRepository.initialize(BatchComponent.NAME);

			int port = RMIServices.instance().getRMIRegistryPort();
            if(System.getSecurityManager() == null) {
                System.setSecurityManager(new java.rmi.RMISecurityManager());
            }
           	String host = InetAddress.getLocalHost().getHostAddress();
    		String objName = "//" + host + ":" + port + "/RMSBatch";
    		Shutdownable ref;
    		try {
    			ref = (Shutdownable)java.rmi.Naming.lookup(objName);
    		} catch(Exception e) {
            	System.err.println(MessageRepository.get(BatchComponent.NAME, Msg.SESSION_PROCESS_NOT_STARTED));
            	return;
    		}
	        if(ref == null) {
            	System.err.println(MessageRepository.get(BatchComponent.NAME, Msg.SESSION_PROCESS_NOT_STARTED));
            	return;
	        }
	        try {
	        	System.out.println(MessageRepository.get(BatchComponent.NAME, Msg.SESSION_STOPPING));
		        ref.shutdown();
	        } catch(Exception e) {
	        	; // ignore the exception; it will always happen
			}
        	System.out.println(MessageRepository.get(BatchComponent.NAME, Msg.SESSION_PROCESS_WAS_STOPPED));
		} catch(Throwable e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
}
