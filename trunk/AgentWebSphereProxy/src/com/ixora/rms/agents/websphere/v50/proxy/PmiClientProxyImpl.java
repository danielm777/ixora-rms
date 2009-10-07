/*
 * Created on 19-Dec-2004
 */
package com.ixora.rms.agents.websphere.v50.proxy;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Properties;

import com.ibm.websphere.pmi.PmiException;
import com.ibm.websphere.pmi.PmiModuleConfig;
import com.ibm.websphere.pmi.client.CpdCollection;
import com.ibm.websphere.pmi.client.PerfDescriptorList;
import com.ibm.websphere.pmi.client.PerfLevelSpec;
import com.ibm.websphere.pmi.client.PmiClient;

/**
 * @author Daniel Moraru
 */
public class PmiClientProxyImpl extends UnicastRemoteObject implements PmiClientProxy {
	/** Trace flag (debugg only) */
	protected static final boolean TRACE = false;
	/** Pmi client */
	protected PmiClient client;
	/** ID assigned by the owner of the process in which the proxy is running */
	protected long id;

	/**
	 * Constructor.
	 * @throws RemoteException
	 * @param id
	 */
	public PmiClientProxyImpl(long id) throws RemoteException {
		super();
		this.id = id;
	}

	/**
	 * @see com.ixora.rms.agents.websphere.v50.proxy.PmiClientProxy#getId()
	 */
	public long getId() throws RemoteException {
		return this.id;
	}

	/**
	 * @see com.ixora.rms.agents.websphere.v50.proxy.PmiClientProxy#configure(java.util.Properties)
	 */
	public void configure(Properties p) throws RemoteException {
		long entry = 0;
		if(TRACE) {
			entry = System.currentTimeMillis();
		}
		client = new PmiClient(p, PmiClient.VERSION_WAS50);

		if(TRACE) {
			System.err.println("PmiClientProxyImpl.configure() " + (System.currentTimeMillis() - entry));
		}
	}

	/**
	 * @see com.ixora.rms.agents.websphere.v50.proxy.PmiClientProxy#getNLSValue(java.lang.String, java.lang.String)
	 */
	public String getNLSValue(String text, String module)
			throws RemoteException {
		long entry = 0;
		if(TRACE) {
			entry = System.currentTimeMillis();
		}
		String ret = PmiClient.getNLSValue(text, module);
		if(TRACE) {
			System.err.println("PmiClientProxyImpl.getNLSValue() " + (System.currentTimeMillis() - entry));
		}
		return ret;
	}

	/**
	 * @throws PmiException
	 * @see com.ixora.rms.agents.websphere.v50.proxy.PmiClientProxy#listNodes()
	 */
	public PerfDescriptor[] listNodes() throws RemoteException, PmiException {
		long entry = 0;
		if(TRACE) {
			entry = System.currentTimeMillis();
		}

		PerfDescriptor[] ret = convertWASPerfDescriptorArray(client.listNodes());
		//checkForErrors();
		if(TRACE) {
			System.err.println("PmiClientProxyImpl.listNodes() " + (System.currentTimeMillis() - entry));
		}
		return ret;
	}

	/**
	 * @throws PmiException
	 * @see com.ixora.rms.agents.websphere.v50.proxy.PmiClientProxy#listServers(com.ibm.websphere.pmi.client.PerfDescriptor)
	 */
	public PerfDescriptor[] listServers(PerfDescriptor pd)
			throws RemoteException, PmiException {
		long entry = 0;
		if(TRACE) {
			entry = System.currentTimeMillis();
		}

		PerfDescriptor[] ret = convertWASPerfDescriptorArray(client.listServers(pd));
		//checkForErrors();
		if(TRACE) {
			System.err.println("PmiClientProxyImpl.listServers() " + (System.currentTimeMillis() - entry));
		}
		return ret;
	}

	/**
	 * @see com.ixora.rms.agents.websphere.v50.proxy.PmiClientProxy#listMembers(com.ibm.websphere.pmi.client.PerfDescriptor)
	 */
	public PerfDescriptor[] listMembers(PerfDescriptor pd, boolean recursive)
			throws RemoteException, PmiException {
		long entry = 0;
		if(TRACE) {
			entry = System.currentTimeMillis();
		}
		PerfDescriptor[] ret = _listMembers(pd, recursive);
		//checkForErrors();
		if(TRACE) {
			System.err.println("PmiClientProxyImpl.listMembers() - REC: " + recursive + " ET: " + (System.currentTimeMillis() - entry));
		}
		return ret;
	}

	/**
	 * @param pd
	 * @param recursive
	 * @return
	 * @throws PmiException
	 */
	protected PerfDescriptor[] _listMembers(PerfDescriptor pd, boolean recursive) throws PmiException {
		//System.err.println("listMembers: for " + pd.toString() + " Rec: " + recursive);
        PerfDescriptor[] ret = convertWASPerfDescriptorArray(client.listMembers(pd));
		if(recursive && ret != null && ret.length > 0) {
			for(int i = 0; i < ret.length; i++) {
				PerfDescriptor d = ret[i];
				PerfDescriptor[] dc = _listMembers(d, true);
				d.setChildren(dc);
			}
		}
		return ret;
	}

	/**
	 * @throws PmiException
	 * @see com.ixora.rms.agents.websphere.v50.proxy.PmiClientProxy#getInstrumentationLevel(java.lang.String, java.lang.String)
	 */
	public PerfLevelSpec[] getInstrumentationLevel(String node, String server)
			throws RemoteException, PmiException {
		long entry = 0;
		if(TRACE) {
			entry = System.currentTimeMillis();
		}
		PerfLevelSpec[] ret = client.getInstrumentationLevel(node, server);
		//checkForErrors();
		if(TRACE) {
			System.err.println("PmiClientProxyImpl.getInstrumentationLevel() " + (System.currentTimeMillis() - entry));
		}
		return ret;
	}

	/**
	 * @see com.ixora.rms.agents.websphere.v50.proxy.PmiClientProxy#getConfigs()
	 */
	public PmiModuleConfig[] getConfigs() throws RemoteException {
		long entry = 0;
		if(TRACE) {
			entry = System.currentTimeMillis();
		}
		PmiModuleConfig[] ret = client.getConfigs();
		//checkForErrors();
		if(TRACE) {
			System.err.println("PmiClientProxyImpl.getConfigs() " + (System.currentTimeMillis() - entry));
		}
		return ret;
	}

	/**
	 * @throws PmiException
	 * @see com.ixora.rms.agents.websphere.v50.proxy.PmiClientProxy#gets(com.ibm.websphere.pmi.client.PerfDescriptorList, boolean)
	 */
	public CpdCollection[] gets(PerfDescriptor[] lst, boolean b)
			throws RemoteException, PmiException {
        if(lst == null || lst.length == 0) {
            return null;
        }
		long entry = 0;
		if(TRACE) {
			entry = System.currentTimeMillis();
		}
		CpdCollection[] ret = client.gets(new PerfDescriptorList(lst), b);
		if(TRACE) {
			System.err.println("PmiClientProxyImpl.gets() LEN: " + lst.length + " ET: " + (System.currentTimeMillis() - entry));
		}
		return ret;
	}

	/**
	 * @see com.ixora.rms.agents.websphere.v50.proxy.PmiClientProxy#getErrorCode()
	 */
	public int getErrorCode() throws RemoteException {
		return client.getErrorCode();
	}

	/**
	 * @see com.ixora.rms.agents.websphere.v50.proxy.PmiClientProxy#getErrorMessage()
	 */
	public String getErrorMessage() throws RemoteException {
		return client.getErrorMessage();
	}

	/**
	 * @see com.ixora.rms.agents.websphere.v50.proxy.PmiClientProxy#getErrorCode(java.lang.String)
	 */
	public int getErrorCode(String node) throws RemoteException {
		return client.getErrorCode(node);
	}

	/**
	 * @see com.ixora.rms.agents.websphere.v50.proxy.PmiClientProxy#getErrorMessage(java.lang.String)
	 */
	public String getErrorMessage(String node) throws RemoteException {
		return client.getErrorMessage(node);
	}

	/**
	 * @see com.ixora.rms.agents.websphere.v50.proxy.PmiClientProxy#setInstrumentationLevel(java.lang.String, java.lang.String, java.lang.String[], int, boolean)
	 */
	public void setInstrumentationLevel(String a, String server, String[] path, int level, boolean b) throws RemoteException, PmiException {
		long entry = 0;
		if(TRACE) {
			entry = System.currentTimeMillis();
		}
		PerfLevelSpec[] spec = new PerfLevelSpec[1];
		if(path != null) {
			spec[0] = client.createPerfLevelSpec(path, level);
		} else {
			spec[0] = client.createPerfLevelSpec(level);
		}
		client.setInstrumentationLevel(a, server, spec, b);
		//checkForErrors();
		if(TRACE) {
			System.err.println("PmiClientProxyImpl.setInstrumentationLevel() " + (System.currentTimeMillis() - entry));
		}
	}

	/**
	 * @see com.ixora.rms.agents.websphere.v50.proxy.PmiClientProxy#end()
	 */
	public void end() throws RemoteException {
		client.end();
		UnicastRemoteObject.unexportObject(this, true);
	}

	/**
	 * @param wasret
	 * @return
	 */
	protected PerfDescriptor[] convertWASPerfDescriptorArray(com.ibm.websphere.pmi.client.PerfDescriptor[] wasret) {
		if(wasret == null) {
			return null;
		}
		PerfDescriptor[] ret = new PerfDescriptor[wasret.length];
		for(int i = 0; i < ret.length; i++) {
			ret[i] = new PerfDescriptor(wasret[i]);
		}
		return ret;
	}

	/**
	 * Check for errors in the client.
	 * @throws RemoteException if error is found
	 */
	protected void checkForErrors() throws RemoteException {
		if(client.getErrorCode() > 0) {
			throw new RemoteException(client.getErrorMessage());
		}
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
