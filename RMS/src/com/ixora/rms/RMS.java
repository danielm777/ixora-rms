/*
 * Created on 15-Nov-2003
 */
package com.ixora.rms;

import java.io.FileNotFoundException;
import java.net.UnknownHostException;
import java.rmi.RemoteException;

import com.ixora.jobs.JobEngine;
import com.ixora.jobs.library.JobLibrary;
import com.ixora.jobs.services.JobEngineService;
import com.ixora.jobs.services.JobLibraryService;
import com.ixora.common.xml.exception.XMLException;
import com.ixora.rms.agents.MonitoringSession;
import com.ixora.rms.dataengine.DataEngine;
import com.ixora.rms.exception.RMSException;
import com.ixora.rms.logging.DataLogReplay;
import com.ixora.rms.logging.DataLogger;
import com.ixora.rms.providers.ProvidersManager;
import com.ixora.rms.reactions.ReactionDispatcherImpl;
import com.ixora.rms.reactions.ReactionLog;
import com.ixora.rms.reactions.ReactionLogImpl;
import com.ixora.rms.repository.AgentInstaller;
import com.ixora.rms.repository.AgentRepositoryManager;
import com.ixora.rms.repository.AgentTemplateRepositoryManager;
import com.ixora.rms.repository.DashboardRepositoryManager;
import com.ixora.rms.repository.DataViewBoardRepositoryManager;
import com.ixora.rms.repository.DataViewRepositoryManager;
import com.ixora.rms.repository.ParserRepositoryManager;
import com.ixora.rms.repository.ProviderInstanceRepositoryManager;
import com.ixora.rms.repository.ProviderRepositoryManager;
import com.ixora.rms.services.AgentInstallerService;
import com.ixora.rms.services.AgentRepositoryService;
import com.ixora.rms.services.AgentTemplateRepositoryService;
import com.ixora.rms.services.DashboardRepositoryService;
import com.ixora.rms.services.DataEngineService;
import com.ixora.rms.services.DataLogReplayService;
import com.ixora.rms.services.DataLogService;
import com.ixora.rms.services.DataViewBoardRepositoryService;
import com.ixora.rms.services.DataViewRepositoryService;
import com.ixora.rms.services.HostMonitorService;
import com.ixora.rms.services.MonitoringSessionService;
import com.ixora.rms.services.ParserRepositoryService;
import com.ixora.rms.services.ProviderInstanceRepositoryService;
import com.ixora.rms.services.ProviderRepositoryService;
import com.ixora.rms.services.ReactionLogService;

/**
 * Entry class into the RMS module.
 */
public final class RMS {
	private static HostMonitor hostMonitor;
	private static DataEngine dataEngine;
	private static DataLogger dataLogger;
	private static AgentRepositoryManager agentRepository;
	private static DataViewRepositoryManager dataViewRepository;
	private static DashboardRepositoryManager dashboardRepository;
	private static ProviderInstanceRepositoryManager providerInstanceRepository;
	private static ProviderRepositoryManager providerRepository;
	private static ParserRepositoryManager parserRepository;
    private static AgentTemplateRepositoryManager agentTemplateRepository;
	private static DataViewBoardRepositoryManager dataViewBoardRepository;
	private static JobEngine jobEngine;
	private static JobLibrary jobLibrary;
	private static ProvidersManager providersManager;
	private static AgentInstaller agentInstaller;
	private static ReactionLog reactionLog;

	/**
	 * RMS constructor.
	 */
	private RMS() {
		super();
	}

	/**
	 * @return the monitoring service.
	 * @throws RemoteException
	 * @throws StartableError
	 * @throws UnknownHostException
	 */
	public static MonitoringSessionService getMonitoringSession()
		throws RemoteException, Throwable, UnknownHostException {
		return new MonitoringSession(
				providerRepository,
				parserRepository,
				providersManager,
				providerInstanceRepository,
				agentRepository,
				hostMonitor,
				new DataSink[] {dataEngine},
				new DataSinkTrimmed[] {dataLogger});
	}

	/**
	 * @return the data log service.
	 */
	public static DataLogService getDataLogger() {
		return dataLogger;
	}

	/**
	 * @return the data log replay service.
	 */
	public static DataLogReplayService getDataLogReplay() {
		// build this only when needed
		return new DataLogReplay(dataEngine);
	}

	/**
	 * @return the data service.
	 */
	public static DataEngineService getDataEngine()	{
		return dataEngine;
	}

	/**
	 * @return the host monitor service.
	 */
	public static HostMonitorService getHostMonitor() {
		return hostMonitor;
	}

	/**
	 * @return the agent repository service
	 */
	public static AgentRepositoryService getAgentRepository() {
		return agentRepository;
	}

	/**
	 * @return the dashboard repository service
	 */
	public synchronized static DashboardRepositoryService getDashboardRepository() {
		if(dashboardRepository == null) {
			dashboardRepository = new DashboardRepositoryManager();
		}
		return dashboardRepository;
	}

	/**
	 * @return the data view repository service
	 */
	public synchronized static DataViewRepositoryService getDataViewRepository() {
		if(dataViewRepository == null) {
		    dataViewRepository = new DataViewRepositoryManager();
		}
		return dataViewRepository;
	}

	/**
	 * @return
	 */
	public static ProviderInstanceRepositoryService getProviderInstanceRepository() {
		return providerInstanceRepository;
	}

	/**
	 * @return
	 */
	public static ProviderRepositoryService getProviderRepository() {
		return providerRepository;
	}

	/**
	 * @return
	 */
	public static ParserRepositoryService getParserRepository() {
		return parserRepository;
	}

	/**
	 * @return
	 */
	public static DataViewBoardRepositoryService getDataViewBoardRepository() {
		return dataViewBoardRepository;
	}

	/**
	 * @return the job engine
	 */
	public static JobEngineService getJobEngine() {
        return jobEngine;
	}

	/**
	 * @return
	 */
	public static JobLibraryService getJobLibrary() {
        return jobLibrary;
	}

	/**
	 * @return an agent installer
	 */
	public synchronized static AgentInstallerService getAgentInstaller() {
		getAgentRepository();
		getDataViewRepository();
		getDashboardRepository();
		getProviderInstanceRepository();
		if(agentInstaller == null) {
			agentInstaller = new AgentInstaller(
						agentRepository,
						dataViewRepository,
						dashboardRepository,
						providerInstanceRepository);
			agentInstaller.addObserver(agentRepository);
		}
		return agentInstaller;
	}

    /**
     * @return an agent installer
     * @throws FileNotFoundException
     * @throws XMLException
     */
    public synchronized static AgentTemplateRepositoryService getAgentTemplateRepository() throws XMLException, FileNotFoundException {
        if(agentTemplateRepository == null) {
            agentTemplateRepository = new AgentTemplateRepositoryManager();
        }
        return agentTemplateRepository;
    }

    /**
     * @return
     */
    public static ReactionLogService getReactionLog() {
    	return reactionLog;
    }

	/**
	 * Initializes RMS. Must be called only once before using RMS.
	 * @throws RMSException
	 */
	public static void initialize() throws RMSException {
		try {
			hostMonitor = new HostMonitor();
			jobEngine = new JobEngine(hostMonitor);
			jobLibrary = new JobLibrary();
			reactionLog = new ReactionLogImpl();
			dataEngine = new DataEngine(
						new ReactionDispatcherImpl(
							reactionLog, jobEngine, jobLibrary));
			dataLogger = new DataLogger();
			initializeRepositories();
			providersManager = new ProvidersManager(providerRepository, hostMonitor);
		} catch(Throwable e) {
			throw new RMSException(e);
		}
	}

	/**
	 * @throws RMSException
	 *
	 */
	public static void initializeRepositories() throws RMSException {
		try {
			agentRepository = new AgentRepositoryManager();
			providerRepository = new ProviderRepositoryManager();
			parserRepository = new ParserRepositoryManager();
			dataViewBoardRepository  = new DataViewBoardRepositoryManager();
			providerInstanceRepository = new ProviderInstanceRepositoryManager();
		} catch(Throwable e) {
			throw new RMSException(e);
		}
	}
}