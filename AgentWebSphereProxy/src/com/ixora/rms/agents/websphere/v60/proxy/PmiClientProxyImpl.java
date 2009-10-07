/*
 * Created on 19-Dec-2004
 */
package com.ixora.rms.agents.websphere.v60.proxy;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import com.ibm.websphere.pmi.PmiException;
import com.ibm.websphere.pmi.client.CpdCollection;
import com.ibm.websphere.pmi.client.PerfDescriptorList;
import com.ixora.rms.agents.websphere.v50.proxy.PerfDescriptor;
import com.ixora.rms.agents.websphere.v50.proxy.PmiClientProxy;

/**
 * @author Daniel Moraru
 */
public class PmiClientProxyImpl extends com.ixora.rms.agents.websphere.v50.proxy.PmiClientProxyImpl implements PmiClientProxy {
	/**
	 * Constructor.
	 * @throws RemoteException
	 * @param id
	 */
	public PmiClientProxyImpl(long id) throws RemoteException {
		super(id);
	}

	/**
	 * @throws PmiException
	 * @see com.ixora.rms.agents.websphere.v50.proxy.PmiClientProxy#gets(com.ibm.websphere.pmi.client.PerfDescriptorList, boolean)
	 */
	public CpdCollection[] gets(PerfDescriptor[] lst, boolean b)
			throws RemoteException, PmiException {

		long entry = 0;
		if(TRACE) {
			entry = System.currentTimeMillis();
		}

        if(lst == null || lst.length == 0) {
            return null;
        }
        // For reasons known to IBM alone this conversion is necessary
        com.ibm.websphere.pmi.client.PerfDescriptor[] wasDescs = new com.ibm.websphere.pmi.client.PerfDescriptor[lst.length];
        for (int i = 0; i < lst.length; i++) {
            wasDescs[i] = lst[i].getWasDescriptor();
        }
		CpdCollection[] ret = client.gets(new PerfDescriptorList(wasDescs), b);
		if(TRACE) {
			System.err.println("PmiClientProxyImpl.gets(): LEN: " + lst.length + " ET: "+ (System.currentTimeMillis() - entry));
		}
		return ret;
	}


	/**
	 * Entry point into the pmi client proxy. This has to be run with IBM's JVM 1.3.1 that
	 * comes with websphere.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
        	if(args == null || args.length != 3) {
        		System.err.println("Parameters are missing");
        		return;
        	}
        	String host = args[0].trim();
        	int regPort = Integer.parseInt(args[1].trim());
        	long id = Long.parseLong(args[2].trim());
            if(System.getSecurityManager() == null) {
                System.setSecurityManager(new java.rmi.RMISecurityManager());
            }
            // create our own registry so we don't have to start
            // the rmiregistry
            Registry reg = LocateRegistry.createRegistry(regPort);
            System.out.println("Started rmi registry on port " + regPort);
            reg.rebind("PmiClientProxy", new PmiClientProxyImpl(id));
            System.out.println("Rebinded to " + "//" + host + ":" + regPort + "/PmiClientProxy");
		} catch(Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
}
