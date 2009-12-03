package com.ixora.rms.batch.starter;

import java.net.InetAddress;

import org.apache.log4j.PropertyConfigurator;

import com.ixora.common.MessageRepository;
import com.ixora.common.RMIServices;
import com.ixora.common.process.LocalProcessWrapper;
import com.ixora.common.process.RMIProcessWrapper;
import com.ixora.common.utils.Utils;
import com.ixora.remote.HostManagerComponent;
import com.ixora.rms.batch.BatchComponent;
import com.ixora.rms.batch.BatchSession;
import com.ixora.rms.batch.BatchSessionConfiguration;
import com.ixora.rms.batch.messages.Msg;
import com.ixora.rms.exception.RMSException;
import com.ixora.rms.logging.LogRepositoryInfo;
import com.ixora.rms.repository.AgentRepositoryManager;
import com.ixora.rms.repository.ProviderRepositoryManager;
import com.ixora.rms.services.AgentRepositoryService;
import com.ixora.rms.services.ProviderRepositoryService;
import com.ixora.rms.starter.RMSAppStarter;

/**
 * 09-Sep-2005
 */

/**
 * Launches the application; It calculates the classpath by inspecting the agent installation data.
 * @author Daniel Moraru
 */
public class BatchStarter extends RMSAppStarter {

	static {
		// setup log4j
        PropertyConfigurator.configure(Utils.getPath("config/log4j.batch.properties"));
	}


	/**
	 * @param ars
	 * @param prs
	 */
	private BatchStarter(AgentRepositoryService ars, ProviderRepositoryService prs) {
		super(ars, prs);
	}

	/**
	 * @param args the first argument is the path of a file that requires changing, the file must
	 * have a line like AGENT=delim where delim is the classpath delimiter; the
	 * path must be relative to application.home; the second argument is the extension name
	 * of the file that launches the app; the third is the RMI registry port number to be
	 * used by the RMSBatch process.
	 * <br>
	 * arg1 - batchStart
	 * arg2 - sh|bat
	 * arg3 - port<br>
	 * arg4 - sessionName<br>
	 * arg5 - logFile<br>
	 */
	public static void main(String[] args) {
		try {
			MessageRepository.initialize(HostManagerComponent.NAME);

			if(args == null || args.length < 4) {
				// TODO localize
				System.err.println("Invalid parameters. Usage: batchStart sh|bat monitoringSessionName logFile");
        		return;
        	}
        	int regPort = RMIServices.instance().getRMIRegistryPort();
        	String sessionName = args[2].trim();
        	String logFile = args[3].trim();
            if(System.getSecurityManager() == null) {
                System.setSecurityManager(new java.rmi.RMISecurityManager());
            }

			AgentRepositoryService agentRepository = new AgentRepositoryManager();
			ProviderRepositoryService providerRepository = new ProviderRepositoryManager();
			BatchStarter launcher = new BatchStarter(agentRepository, providerRepository);
			LocalProcessWrapper process = launcher.getProcess(args,
					new LocalProcessWrapper.Listener() {
						public void error(String line) {
							System.err.println(line);
						}
						public void output(String line) {
							System.out.println(line);
						}});
			String rmiAddressIpAdd = InetAddress.getLocalHost().getHostAddress();
			RMIProcessWrapper rmiProcess = new RMIProcessWrapper(
					process, rmiAddressIpAdd, regPort, "RMSBatch");
			System.out.println(MessageRepository.get(BatchComponent.NAME, Msg.STARTING_SESSION,
					new String[]{String.valueOf(regPort)}));
			try {
				rmiProcess.start();
				BatchSession batchSession = (BatchSession)rmiProcess.getRMIObject();
				if(batchSession == null) {
					throw new RMSException("Failed to get reference to BatchSession");
				}
				try {
					batchSession.configure(
							new BatchSessionConfiguration(
									sessionName,
									new LogRepositoryInfo(LogRepositoryInfo.Type.xml, logFile)));
					batchSession.start();
					System.out.println(MessageRepository.get(BatchComponent.NAME, Msg.SESSION_STARTED));
				} catch(Exception e) {
					// avoid displaying the full stack trace
					String msg = e.getLocalizedMessage();
					System.err.println(
							MessageRepository.get(
									BatchComponent.NAME,
									Msg.FAILED_TO_START_SESSION,
									new Object[]{msg}));
					// stop the process if the session can't be initialized properly
					rmiProcess.stop();
				}
			} catch(Exception e) {
				// avoid displaying the full stack trace
				String msg = e.getLocalizedMessage();
				System.err.println(
						MessageRepository.get(
								BatchComponent.NAME,
								Msg.FAILED_TO_START_SESSION,
								new Object[]{msg}));
			}
			System.out.println(MessageRepository.get(BatchComponent.NAME, Msg.PRESS_ENTER_TO_EXIT_CONSOLE));
			System.in.read();
			System.exit(0);
		} catch(Throwable e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
}
