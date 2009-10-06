package com.ixora.remote;

import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import com.ixora.RMIServiceNames;
import com.ixora.remote.exception.RemoteManagedListenerIsUnreachable;
import com.ixora.remote.messages.Msg;
import com.ixora.remote.security.ConnectionChecker;
import com.ixora.rms.HostInformation;
import com.ixora.rms.OperatingSystemInfo;
import com.ixora.rms.RMSModule;
import com.ixora.common.ConfigurationMgr;
import com.ixora.common.MessageRepository;
import com.ixora.common.RMIServices;
import com.ixora.common.exception.AppException;
import com.ixora.common.logging.AppLogger;
import com.ixora.common.logging.AppLoggerFactory;
import com.ixora.common.update.multinode.UpdatePart;
import com.ixora.rms.exception.RMSException;

/**
 * @author Daniel Moraru
 */
public final class HostManagerImpl
	extends UnicastRemoteObject
	implements HostManager, HostManagerConfigurationConstants {
	/**
	 * Logger.
	 */
	private static final AppLogger logger = AppLoggerFactory.getLogger(HostManagerImpl.class);
	/**
	 * Host info.
	 */
	private HostInformation fHostInfo;
	/**
	 * Timestamps when the last ping was received.
	 * Key: client host name
	 * Value: client data
	 */
	private Map<String, ClientData> fClientData;
	/**
	 * Maximum interval between pings. If a client
	 * doesn't ping this HostManager sooner than
	 * this value its RemoteAgentManager will be destroyed.
	 * A similar mechanism is also provided by RMI but only for objects that
	 * implement the Unreferenced interface which is a local interface.
	 */
	private int fCheckLostClientsInterval;
	/**
	 * Connection checker that enforces host name based security.
	 */
	private ConnectionChecker fChecker;

	/**
	 * Client data.
	 */
	private static final class ClientData {
		/** This client's RemoteManaged instances */
		Set<RemoteManaged> references;
		/** Timestamps when the last ping was received */
		long lastPing;

		ClientData(RemoteManaged rm) {
			this.references = new HashSet<RemoteManaged>();
			this.references.add(rm);
			lastPing = System.currentTimeMillis();
		}
	}

    /**
     * Constructor for HostManagerImpl.
     * @throws RemoteException
     * @throws StartableError
     * @throws NoSuchMethodException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public HostManagerImpl(RMIServerSocketFactory ssf, RMIClientSocketFactory csf) throws RemoteException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        super(RMIServices.instance().getPort(RMIServiceNames.HOSTMANAGER), csf, ssf);
        init();
    }

    /**
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws NoSuchMethodException
     * @throws StartableError
     *
     */
	private void init() throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        this.fClientData = new HashMap<String, ClientData>();
        this.fChecker = new ConnectionChecker();
        try {
            fCheckLostClientsInterval = ConfigurationMgr.getInt(HostManagerComponent.NAME,
                    CHECK_LOST_CLIENTS_INTERVAL);
            // set minimum value to 2 minutes
            if(fCheckLostClientsInterval < 120) {
                fCheckLostClientsInterval = 120;
            }
            fCheckLostClientsInterval *= 1000;
        } catch(Exception e) {
            fCheckLostClientsInterval = 120000;
            logger.error(e);
        }
        setupHostInfo();
        // start the timer daemon
        Timer timer = new Timer(true);
        timer.schedule(new TimerTask(){
				public void run() {
					checkLostClients();
				}
        	}, 0, fCheckLostClientsInterval);
    }

    /**
	 * Constructor for HostManagerImpl.
	 * @throws RemoteException
	 * @throws StartableError
	 * @throws NoSuchMethodException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public HostManagerImpl() throws RemoteException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
		super();
        init();
	}

	/**
	 * @see com.ixora.remote.HostManager#createManaged(String, String, RemoteManagedListener)
	 */
	public synchronized RemoteManaged createManaged(String clazz,
			String host, RemoteManagedListener listener)
				throws RMSException {
		try {
			String client = getClientHost();
			fChecker.check(client);
			RemoteManaged rm = (RemoteManaged)Class.forName(clazz).newInstance();
			rm.initialize(host, listener);
			ClientData cd = this.fClientData.get(client);
			if(cd == null) {
				this.fClientData.put(client, new ClientData(rm));
			} else {
				cd.references.add(rm);
			}
			return rm;
		} catch(RemoteManagedListenerIsUnreachable e) {
			logger.error(e);
			throw e;
		} catch(Exception e) {
			RMSException ex = new RMSException(e);
			logger.error(ex);
			throw ex;
		}
	}

	/**
	 * @see com.ixora.remote.HostManager#getHostInfo()
	 */
	public synchronized HostInformation getHostInfo() throws RMSException {
		try {
			fChecker.check(getClientHost());
			this.fHostInfo.setRemoteTime();
			return this.fHostInfo;
		} catch(Exception e) {
			RMSException ex = new RMSException(e);
			logger.error(ex);
			throw ex;
		}
	}

	/**
	 * @see com.ixora.common.remote.Pingable#ping()
	 */
	public synchronized void ping() {
		try {
			fChecker.check(getClientHost());
			String client = getClientHost();
			ClientData cd = fClientData.get(client);
			if(cd != null) {
				cd.lastPing = System.currentTimeMillis();
			}
		} catch(Exception e) {
			logger.error(e);
		}
	}

	/**
	 * @see com.ixora.common.update.multinode.Updateable#update(com.ixora.common.update.multinode.UpdatePart[])
	 */
	public void update(UpdatePart[] parts) throws RemoteException, AppException {
		try {
			fChecker.check(getClientHost());
			// not implemented
		} catch(Exception e) {
			logger.error(e);
			throw new AppException(e);
		}
	}

	/**
	 * @see com.ixora.common.update.multinode.Updateable#restart()
	 */
	public void restart() throws RemoteException, AppException {
		try {
			fChecker.check(getClientHost());
			// not implemented
		} catch(Exception e) {
			logger.error(e);
			throw new AppException(e);
		}
	}

	/**
	 * @see com.ixora.remote.HostManager#destroyManaged(com.ixora.rms.remote.Shutdownable)
	 */
	public synchronized void destroyManaged(RemoteManaged rm)
		throws RMSException {
		if(rm == null) {
			throw new IllegalArgumentException("null remote managed");
		}
		try {
			String client = getClientHost();
			ClientData cd = this.fClientData.get(client);
			if(cd != null) {
				cd.references.remove(rm);
				if(cd.references.size() == 0) {
					this.fClientData.remove(client);
				}
			}
            try {
                rm.shutdown();
            } finally {
            	try {
            		UnicastRemoteObject.unexportObject(rm, true);
            	} catch(Throwable e) {
            		if(logger.isTraceEnabled()) {
            			logger.error(e);
            		}
				}
            }
		} catch(Exception e) {
			RMSException ex = new RMSException(e);
			logger.error(ex);
			throw ex;
		}
	}

	/**
	 * Sets up the host info structure.
	 */
	private void setupHostInfo() {
		OperatingSystemInfo os = new OperatingSystemInfo(
			System.getProperty("os.name"),
			System.getProperty("os.arch"),
			System.getProperty("os.version"));
		this.fHostInfo = new HostInformation(os, new RMSModule().getVersion());
	}

	/**
	 * Checks for clients that haven't ping for
	 * a longer time than configured.
	 */
	private synchronized void checkLostClients() {
		try {
			long time = System.currentTimeMillis();
			ClientData cd;
			String client;
			Map.Entry entry;
			for(Iterator iter = this.fClientData.entrySet().iterator();
				iter.hasNext();) {
				entry = (Map.Entry)iter.next();
				cd = (ClientData)entry.getValue();
				if(time - cd.lastPing > fCheckLostClientsInterval) {
					client = (String)entry.getKey();
					logger.error(MessageRepository.get(
							Msg.HOST_MANAGER_LOST_CLIENT_DETECTED,
							new String[] {client}));
					iter.remove();

					for(RemoteManaged rm : cd.references) {
						try {
							rm.shutdown();
						} catch(Throwable e) {
							logger.error(e);
						} finally {
			            	try {
			            		UnicastRemoteObject.unexportObject(rm, true);
			            	} catch(Throwable e) {
			            		logger.error(e);
							}
						}
					}
				}
			}
		} catch(Exception e) {
			logger.error(e);
		}
	}

	/**
	 * @see com.ixora.common.update.multinode.Updateable#getOsName()
	 */
	public String getOsName() throws RemoteException, AppException {
		try {
			fChecker.check(getClientHost());
			return fHostInfo.getOperatingSystem().getName();
		} catch(Exception e) {
			logger.error(e);
			throw new AppException(e);
		}
	}

	/**
	 * @see com.ixora.remote.HostManager#shutdown()
	 */
	public void shutdown() throws RemoteException, AppException {
		try {
			for(ClientData cd : fClientData.values()) {
				for(RemoteManaged rm : cd.references) {
					try {
						rm.shutdown();
		            } finally {
		                UnicastRemoteObject.unexportObject(rm, true);
		            }
				}
			}
			UnicastRemoteObject.unexportObject(this, true);
		} catch(RemoteException e) {
			logger.error(e);
			throw e;
		} catch(AppException e) {
			logger.error(e);
			throw e;
		} catch(Throwable e) {
			logger.error(e);
			throw new RMSException(e);
		} finally {
			System.exit(0);
		}
	}
}
