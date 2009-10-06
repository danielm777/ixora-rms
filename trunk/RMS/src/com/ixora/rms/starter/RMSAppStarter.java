package com.ixora.rms.starter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

import com.ixora.common.os.OSUtils;
import com.ixora.common.process.LocalProcessWrapper;
import com.ixora.common.utils.Utils;
import com.ixora.rms.exception.RMSException;
import com.ixora.rms.repository.AgentInstallationData;
import com.ixora.rms.repository.ProviderInstallationData;
import com.ixora.rms.repository.VersionableAgentInstallationData;
import com.ixora.rms.repository.VersionableAgentInstallationDataMap;
import com.ixora.rms.services.AgentRepositoryService;
import com.ixora.rms.services.ProviderRepositoryService;

/**
 * 09-Sep-2005
 */

/**
 * Base class for RMS applications launchers; It calculates the classpath by inspecting the agent and providers
 * installation data.
 * @author Daniel Moraru
 */
public abstract class RMSAppStarter {
	// NOTE: do not use a logger here, the logger is configured in subclasses
	protected ProviderRepositoryService fProviderRepository;
	protected AgentRepositoryService fAgentRepository;

	/**
	 * @param ars
	 * @param prs
	 */
	protected RMSAppStarter(AgentRepositoryService ars, ProviderRepositoryService prs) {
		super();
		fAgentRepository = ars;
		fProviderRepository = prs;
	}

	/**
	 * @param args the first argument is the path of a file that requires changing, the file must
	 * have a line like AGENT=delim where delim is the classpath delimiter; the
	 * path must be relative to application.home; the second argument is the extension name
	 * of the file that launches the app
	 * @param listener
	 * @throws RMSException
	 * @throws IOException
	 */
	public LocalProcessWrapper getProcess(String[] args, LocalProcessWrapper.Listener listener) throws RMSException, IOException {
		if(Utils.isEmptyArray(args) || args.length < 2) {
			throw new RMSException("Invalid number of arguments");
		}
		// modify startup file, append to classpath the UI jars for agents and providers
		String fullPath = Utils.getPath(args[0]);
		String exeFileExt = args[1];
		File file = new File(fullPath);
		if(!file.exists()) {
			throw new RMSException("File does not exist: " + file.getAbsolutePath());
		}
		File exeFile = new File(file.getParentFile(), file.getName() + "." + exeFileExt);
		BufferedReader is = null;
		BufferedWriter os = null;
		try {
			is = new BufferedReader(new FileReader(file));
			os = new BufferedWriter(new FileWriter(exeFile));
			String line;
			while((line = is.readLine()) != null) {
				if(line.indexOf("AGENTS=") >= 0) {
					StringTokenizer tok = new StringTokenizer(line, "=");
					if(tok.countTokens() != 2) {
						throw new RMSException("Invalid file");
					}
					StringBuffer lineBuff = new StringBuffer();
					lineBuff.append(tok.nextToken().trim());
					String classpathSeparator = tok.nextToken().trim();
					lineBuff.append("=");
					lineBuff.append(getAgentJars(classpathSeparator.charAt(0)));
					lineBuff.append(classpathSeparator);
					lineBuff.append(getProvidersJars(classpathSeparator.charAt(0)));
					line = lineBuff.toString();
				}
				os.write(line);
				os.newLine();
			}
		} finally {
			if(is != null) {
				is.close();
			}
			if(os != null) {
				os.close();
			}
		}
		StringBuffer argsLine = null;
		if(args.length > 2) {
			argsLine = new StringBuffer();
			for(int i = 2; i < args.length; i++) {
				argsLine.append(args[i]).append(" ");
			}
		}
		// if unix os give exe rights on the temporary file
		if(OSUtils.isOs(OSUtils.UNIX)) {
			try {
				Process rightsProc = Runtime.getRuntime().exec("chmod 777 " + exeFile.getAbsolutePath());
				rightsProc.waitFor();
			} catch(Exception e) {
				; // ignore, because OSUtils.isOS is not complete, it returns unix for any os other than win
				// anyway log it in case it is unix and something went wrong
				e.printStackTrace();
			}
		}

		LocalProcessWrapper proc = new LocalProcessWrapper(
				exeFile.getAbsolutePath() + " " + (argsLine == null ? "" : argsLine),
				listener);
		//exeFile.deleteOnExit();
		return proc;
	}

	/**
	 * @param classpathSeparator
	 * @return
	 * @throws RMSException
	 */
	protected String getAgentJars(char classpathSeparator) throws RMSException {
		Map<String, AgentInstallationData> data = fAgentRepository.getInstalledAgents();
		StringBuffer classpathBuff = new StringBuffer();
		if(!Utils.isEmptyMap(data)) {
			for(Iterator<AgentInstallationData> iter = data.values().iterator(); iter.hasNext();) {
				AgentInstallationData aid = iter.next();
				VersionableAgentInstallationDataMap versions = aid.getVersionData();
				if(versions != null) {
					Collection<VersionableAgentInstallationData> coll = versions.getAll();
					if(!Utils.isEmptyCollection(coll)) {
						for(Iterator<VersionableAgentInstallationData> iter2 = coll.iterator(); iter2.hasNext();) {
							String uiJar = iter2.next().getUIJar();
							if(!Utils.isEmptyString(uiJar)) {
								if(uiJar.startsWith("/") || uiJar.startsWith("\\")) {
									uiJar = uiJar.substring(1);
								}
								classpathBuff.append(uiJar);
								if(iter2.hasNext()) {
									classpathBuff.append(classpathSeparator);
								}
							}
						}
					}
					if(iter.hasNext()) {
						classpathBuff.append(classpathSeparator);
					}
				}
			}
		}
		return classpathBuff.toString();
	}

	/**
	 * @param classpathSeparator
	 * @return
	 */
	protected String getProvidersJars(char classpathSeparator) {
		Map<String, ProviderInstallationData> data = fProviderRepository.getInstalledProviders();
		StringBuffer classpathBuff = new StringBuffer();
		if(!Utils.isEmptyMap(data)) {
			for(Iterator<ProviderInstallationData> iter = data.values().iterator(); iter.hasNext();) {
				String uiJar = iter.next().getUIJar();
				if(!Utils.isEmptyString(uiJar)) {
					if(uiJar.startsWith("/") || uiJar.startsWith("\\")) {
						uiJar = uiJar.substring(1);
					}
					classpathBuff.append(uiJar);
					if(iter.hasNext()) {
						classpathBuff.append(classpathSeparator);
					}
				}
			}
		}
		return classpathBuff.toString();
	}
}
