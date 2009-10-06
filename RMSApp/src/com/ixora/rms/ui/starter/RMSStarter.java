package com.ixora.rms.ui.starter;

import org.apache.log4j.PropertyConfigurator;

import com.ixora.common.MessageRepository;
import com.ixora.common.process.LocalProcessWrapper;
import com.ixora.common.utils.Utils;
import com.ixora.rms.RMS;
import com.ixora.rms.RMSComponent;
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
public class RMSStarter extends RMSAppStarter {

	static {
		// setup log4j
        PropertyConfigurator.configure(Utils.getPath("config/log4j.console.properties"));
	}

	/**
	 * @param ars
	 * @param prs
	 */
	private RMSStarter(AgentRepositoryService ars, ProviderRepositoryService prs) {
		super(ars, prs);
	}

	/**
	 * @param args the path of a file that requires changing, the file must
	 * have a line like AGENT=delim where delim is the classpath delimiter; the
	 * path must be relative to application.home
	 */
	public static void main(String[] args) {
		try {
			MessageRepository.initialize(RMSComponent.NAME);
			RMS.initializeRepositories();
			RMSStarter launcher = new RMSStarter(RMS.getAgentRepository(), RMS.getProviderRepository());
			LocalProcessWrapper process = launcher.getProcess(args,
					new LocalProcessWrapper.Listener() {
						public void error(String line) {
							System.err.println(line);
						}
						public void output(String line) {
							System.out.println(line);
						}});
			process.start();
			// wait here for a while, the launcher needs to
			// shutdown only after the app process has been launched...
			Thread.sleep(10000);
			System.exit(0);
		} catch(Throwable e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
}
