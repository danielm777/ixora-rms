package com.ixora.rms.remote.starter;

import java.net.InetAddress;

import org.apache.log4j.PropertyConfigurator;

import com.ixora.common.ConfigurationMgr;
import com.ixora.common.MessageRepository;
import com.ixora.common.RMIServices;
import com.ixora.common.process.LocalProcessWrapper;
import com.ixora.common.process.RMIProcessWrapper;
import com.ixora.common.utils.Utils;
import com.ixora.remote.HostManagerComponent;
import com.ixora.remote.HostManagerConfigurationConstants;
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
public class HostManagerStarter extends RMSAppStarter {

	static {
		// setup log4j
        PropertyConfigurator.configure(Utils.getPath("config/log4j.hm.properties"));
	}

	/**
	 * @param ars
	 * @param prs
	 */
	private HostManagerStarter(AgentRepositoryService ars, ProviderRepositoryService prs) {
		super(ars, prs);
	}

	/**
	 * @param args the first argument is the path of a file that requires changing, the file must
	 * have a line like AGENT=delim where delim is the classpath delimiter; the
	 * path must be relative to application.home; the second argument is the extension name
	 * of the file that launches the application.
	 */
	public static void main(String[] args) {
		try {
			MessageRepository.initialize(HostManagerComponent.NAME);

			if(args == null || args.length < 2) {
        		System.err.print("Incorrect set of parameters.");
        		return;
        	}

            if(System.getSecurityManager() == null) {
                System.setSecurityManager(new java.rmi.RMISecurityManager());
            }

			// before launching the application, configure HostManager
			InetAddress address = LocalIpAddressSelector.select();
			boolean allIps = false;
			String configuredIpAdd = null;
			if(address != null) {
				configuredIpAdd = address.getHostAddress();
			} else {
				configuredIpAdd = "";
				allIps = true;
			}
			ConfigurationMgr.setString(HostManagerComponent.NAME,
					HostManagerConfigurationConstants.SOCKET_FACTORY_LOCAL_IP_ADDRESS,
					configuredIpAdd);
			ConfigurationMgr.save(HostManagerComponent.NAME);

			AgentRepositoryService agentRepository = new AgentRepositoryManager();
			ProviderRepositoryService providerRepository = new ProviderRepositoryManager();
			HostManagerStarter launcher = new HostManagerStarter(agentRepository, providerRepository);
			LocalProcessWrapper process = launcher.getProcess(args,
					new LocalProcessWrapper.Listener() {
						public void error(String line) {
							System.err.println(line);
						}
						public void output(String line) {
							System.out.println(line);
						}});
			String rmiAddressIpAdd = configuredIpAdd;
			if(allIps) {
				rmiAddressIpAdd = InetAddress.getLocalHost().getHostAddress();
			}
			int regPort = RMIServices.instance().getRMIRegistryPort();
			RMIProcessWrapper rmiProcess = new RMIProcessWrapper(process, rmiAddressIpAdd, regPort, "HostManager");
			// TODO localize
			if(!allIps) {
				System.out.println("Starting HostManager on ip address "
						+ configuredIpAdd + ", port " + regPort + "...");
			} else {
				System.out.println("Starting HostManager on port " + regPort + "...");
			}
			try {
				rmiProcess.start();
				// TODO localize
				System.out.println("HostManager started.");
			} catch(Exception e) {
				// TODO localize
				System.err.println("Failed to start the HostManager. Error:");
				e.printStackTrace();
			}
			// TODO localize
			System.out.println("Press 'Enter' to exit this console...");
			System.in.read();
			System.exit(0);
		} catch(Throwable e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
}
