package com.ixora.rms.batch;

import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.ExportException;

import org.apache.log4j.PropertyConfigurator;

import com.ixora.common.ComponentConfiguration;
import com.ixora.common.ConfigurationMgr;
import com.ixora.common.IxoraCommonModule;
import com.ixora.common.MessageRepository;
import com.ixora.common.RMIServices;
import com.ixora.common.exception.FailedToSaveConfiguration;
import com.ixora.common.logging.AppLogger;
import com.ixora.common.logging.AppLoggerFactory;
import com.ixora.common.remote.ServerSocketFactory;
import com.ixora.common.update.UpdateMgr;
import com.ixora.common.utils.Utils;
import com.ixora.jobs.JobsComponent;
import com.ixora.rms.RMS;
import com.ixora.rms.RMSComponent;
import com.ixora.rms.RMSConfigurationConstants;
import com.ixora.rms.RMSModule;
import com.ixora.rms.batch.messages.Msg;
import com.ixora.rms.logging.LogComponent;
import com.ixora.rms.reactions.ReactionsComponent;
import com.ixora.rms.reactions.email.ReactionsEmailComponent;
import com.ixora.rms.ui.dataviewboard.DataViewBoardComponent;
import com.ixora.rms.ui.dataviewboard.charts.ChartsBoardComponent;
import com.ixora.rms.ui.dataviewboard.logs.LogBoardComponent;
import com.ixora.rms.ui.dataviewboard.properties.PropertiesBoardComponent;
import com.ixora.rms.ui.dataviewboard.tables.TablesBoardComponent;
import com.ixora.rms.ui.session.MonitoringSessionRepositoryComponent;

/**
 * @author Daniel Moraru
 */
public final class BatchMain {
	/** Logger */
	private static final AppLogger logger;

	static {
		// setup log4j
        PropertyConfigurator.configure(Utils.getPath("config/log4j.batch.properties"));
		logger = AppLoggerFactory.getLogger(BatchMain.class);
	}

	/**
	 * Main.
	 */
	public static void main(String[] args) {
		try {
			initApplication();
			// create the RMI object
			BatchSession batchSession = new BatchSessionImpl(
					RMS.getMonitoringSession(),
					RMS.getHostMonitor(),
					RMS.getDataEngine(),
					RMS.getDataLogger(),
					RMS.getReactionLog(),
					RMS.getDashboardRepository(),
					RMS.getDataViewRepository(),
					RMS.getAgentRepository(),
					RMS.getProviderInstanceRepository());

	        ServerSocketFactory ssf = new ServerSocketFactory(null); // all ip addresses
			int port = RMIServices.instance().getRMIRegistryPort();
	        Registry reg = LocateRegistry.createRegistry(port, null, ssf);
	        reg.rebind("RMSBatch", batchSession);
	        logger.info(MessageRepository.get(Msg.SESSION_STARTED_ON_PORT,
					new String[]{String.valueOf(port)}));

	        /////////////////////// DEBUG ONLY
//	        batchSession.configure(new BatchSessionConfiguration(
//	        		"C:\\Dev\\redbox\\RMSApp\\config\\session\\repository\\was61.lperf",
//	        		new LogRepositoryInfo(LogRepositoryInfo.TYPE_XML, "C:\test.log")));
	        ///////////////////////

		} catch(Throwable e) {
			System.err.println(Utils.getApplicationErrorMessage(e));
			logger.error(e);
			System.exit(1);
		}
	}

	/**
	 * Display the main window of the application to the user.
	 * @throws Throwable
	 * @throws UnknownHostException
	 * @throws RemoteException
	 */
	private static void initApplication() throws RemoteException, UnknownHostException, Throwable {
		// first thing to do:
		// initialize the message repository, set the default
		// repository to the repository for the main application component
		MessageRepository.initialize(RMSComponent.NAME);

		// assign a public IP address for the console to use
		String currentConsoleIpAddress = ConfigurationMgr.getString(
				RMSComponent.NAME,
				RMSConfigurationConstants.NETWORK_ADDRESS);
		if(Utils.isEmptyString(currentConsoleIpAddress)) {
        	// tell RMI to use the fully qualified name for this host
        	// for object references
        	System.setProperty("java.rmi.server.useLocalHostname", "true");
		} else {
			System.setProperty("java.rmi.server.hostname", currentConsoleIpAddress);
		}

		// register deployment modules with the update manager
		UpdateMgr.registerModule(new IxoraCommonModule());
		UpdateMgr.registerModule(new RMSModule());
		UpdateMgr.registerNodeModule(new IxoraCommonModule());
		UpdateMgr.registerNodeModule(new RMSModule());

		// ConfigurationMgr.makeConfigurationEditable(PreferencesConfigurationConstants.PREFERENCES);
		ConfigurationMgr.makeConfigurationEditable(RMSComponent.NAME);
		ConfigurationMgr
				.makeConfigurationEditable(DataViewBoardComponent.NAME);
		ConfigurationMgr
				.makeConfigurationEditable(ChartsBoardComponent.NAME);
		ConfigurationMgr
				.makeConfigurationEditable(TablesBoardComponent.NAME);
		ConfigurationMgr
				.makeConfigurationEditable(PropertiesBoardComponent.NAME);
		ConfigurationMgr
				.makeConfigurationEditable(LogBoardComponent.NAME);
		ConfigurationMgr.makeConfigurationEditable(LogComponent.NAME);
		// ConfigurationMgr.makeConfigurationEditable(LogComponentXML.NAME);
		ConfigurationMgr
				.makeConfigurationEditable(MonitoringSessionRepositoryComponent.NAME);
		ConfigurationMgr.makeConfigurationEditable(JobsComponent.NAME);
		ConfigurationMgr.makeConfigurationEditable(ReactionsComponent.NAME);
		ConfigurationMgr.makeConfigurationEditable(ReactionsEmailComponent.NAME);
		// ConfigurationMgr.makeConfigurationEditable(UpdateComponent.NAME);

        if (System.getSecurityManager() == null) {
			System.setSecurityManager(new java.rmi.RMISecurityManager());
		}

		try {
			// initialize RMS
			RMS.initialize();
		} catch(Exception e) {
			// this could happen if a special IP address has been assigned to
			// the console has changed since last time the app was started
			if (e.getCause() instanceof ExportException
					|| Utils.getTrace(e).toString().contains("Port")) {
				logger.error("Ignoring the ip address assigned to the console "
								+ currentConsoleIpAddress
								+ " as it seems to be invalid.");
				resetConsoleIpAddress();
				// try again
				RMS.initialize();
			}
		}
	}

	/**
	 * Resets the ip address assigned to the console.
	 * @throws FailedToSaveConfiguration
	 */
	private static String resetConsoleIpAddress()
			throws FailedToSaveConfiguration {
		String na = "";
		ComponentConfiguration cconf = ConfigurationMgr.get(RMSComponent.NAME);
		// set both the current and the default value to the new address
		cconf.setDefaultValue(RMSConfigurationConstants.NETWORK_ADDRESS, na);
		cconf.setString(RMSConfigurationConstants.NETWORK_ADDRESS, na);
		ConfigurationMgr.save(RMSComponent.NAME);
		return na;
	}
}
