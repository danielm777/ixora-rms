/*
 * Created on 20-Dec-2004
 */
package com.ixora.rms.agents.websphere;

import java.util.List;
import java.util.Properties;

import com.ixora.common.logging.AppLogger;
import com.ixora.common.logging.AppLoggerFactory;
import com.ixora.common.os.OSUtils;
import com.ixora.common.plugin.RegexClassLoader;
import com.ixora.common.process.ProcessWrapper;
import com.ixora.common.process.RMIProcessWrapper;
import com.ixora.common.utils.Utils;
import com.ixora.rms.agents.websphere.exception.ConnectedToWrongProxy;
import com.ixora.rms.agents.websphere.v50.Configuration;
import com.ixora.rms.agents.websphere.v50.proxy.PmiClientProxy;
import com.ixora.rms.exception.RMSException;

/**
 * @author Daniel Moraru
 */
public class PmiClientProxyMgr {
	/** Logger */
	private static final AppLogger logger = AppLoggerFactory.getLogger(PmiClientProxyMgr.class);
    /** The PmiClientProxy process */
    private RMIProcessWrapper process;
    /** RMI port */
    private int port;
    /** Host name */
    private String monitoredHost;
    /** Agent deployment host */
    private String deploymentHost;
    /** Event handler */
    private EventHandler eventHandler;

    /**
     * Event handler.
     */
    private class EventHandler implements ProcessWrapper.Listener {
		/**
		 * @see com.ixora.common.process.ProcessWrapper.Listener#error(java.lang.String)
		 */
		public void error(String line) {
			logger.error(line);
		}
		/**
		 * @see com.ixora.common.process.ProcessWrapper.Listener#output(java.lang.String)
		 */
		public void output(String line) {
			logger.info(line);
		}
    }

    /**
	 * Constructor.
	 */
	public PmiClientProxyMgr() {
		super();
		this.eventHandler = new EventHandler();
	}

	/**
	 * Starts the pmi client proxy process.
	 * @param monitoredHost
	 * @param port
	 * @param p
	 * @param props
     * @param proxyClass
	 * @throws Throwable
	 */
	public PmiClientProxy startPmiClientProxy(
			String monitoredHost,
			String deploymentHost, int port, Configuration p, Properties props, String proxyClass) throws Throwable {
		if(process != null) {
			stopPmiClientProxy();
		}
		this.monitoredHost = monitoredHost;
		this.deploymentHost = deploymentHost;
		this.port = port;

        String CP_SEP = OSUtils.isOs(OSUtils.WINDOWS) ? ";" : ":";
        String WAS_HOME = p.getString(Configuration.WAS_HOME);
        if(WAS_HOME.endsWith("/") || WAS_HOME.endsWith("\\")) {
        	WAS_HOME = WAS_HOME.substring(0, WAS_HOME.length() - 1);
        }
		String JAVA_HOME = WAS_HOME + "/java";

		String WAS_EXT_DIRS =
            JAVA_HOME + "/lib" + CP_SEP +
            WAS_HOME + "/classes" + CP_SEP +
            WAS_HOME + "/lib" + CP_SEP +
            WAS_HOME + "/lib/ext";

        StringBuffer buffClassPath = new StringBuffer();
        List<String> cp = RegexClassLoader.expandRegexPath(WAS_HOME + "/lib/(.*\\.jar)");
        for(String cpe : cp) {
            buffClassPath.append(encodeCmdString(cpe)).append(CP_SEP);
        }
        // this is for WAS 6.1 ---------------------------------
        cp = RegexClassLoader.expandRegexPath(WAS_HOME + "/runtimes/(.*\\.jar)");
        if(cp != null) {
	        for(String cpe : cp) {
	            buffClassPath.append(encodeCmdString(cpe)).append(CP_SEP);
	        }
        }
        cp = RegexClassLoader.expandRegexPath(WAS_HOME + "/plugins/(.*\\.jar)");
        if(cp != null) {
	        for(String cpe : cp) {
	            buffClassPath.append(encodeCmdString(cpe)).append(CP_SEP);
	        }
        }
        // ------------------------------------------------------
// PmiClientProxy code
		buffClassPath.append(encodeCmdString(Utils.getPath("/jars/AgentWebsphereProxy.jar")));
		String cmd = encodeCmdString(WAS_HOME + "/java/bin/java") +
			" -Djava.security.policy=" + encodeCmdString(Utils.getPath("/java.policy")) +
            " -Dws.install.root=" + encodeCmdString(WAS_HOME) +
            " -Duser.install.root=" + encodeCmdString(WAS_HOME) +
            " -Dwas.repository.root=" + encodeCmdString(WAS_HOME + "/config") +
            " -Dws.ext.dirs=" + encodeCmdString(WAS_EXT_DIRS) +
			" -classpath "  + buffClassPath.toString() + " " + proxyClass;

		int id = Utils.getRandomInt();
		process = new RMIProcessWrapper(cmd + " " + deploymentHost + " " + this.port + " " + id ,
				this.eventHandler, this.deploymentHost, this.port, "PmiClientProxy");
		try {
	        process.start();
			// get reference
	        PmiClientProxy proxy = (PmiClientProxy)process.getRMIObject();
        	if(proxy.getId() != id) {
        		throw new ConnectedToWrongProxy();
        	}
            proxy.configure(props);
            return proxy;
		} catch(Throwable t) {
			logger.error(t);
			// stop proxy processs
			stopPmiClientProxy();
            // IMPORTANT: DO NOT PASS THE EXCEPTION AS IT MIGHT NOT BE VISIBLE
            // IN THE OTHER APPLICATION LAYERS (if it's a WAS one for instance due to
            // class loader settings)
            // TODO localize
            throw
                new RMSException("Failed to initialize proxy. " +
                    "Make sure the agent configuration data is valid. Error:",
                        Utils.getTrace(t));
		}
	}

	/**
	 * @param string
	 * @return
	 */
	private String encodeCmdString(String string) {
		return OSUtils.isOs(OSUtils.WINDOWS) ? "\"" + string + "\"" : string;
	}

	/**
	 * Stops the PmiClientProxy process.
	 * @throws Throwable
	 */
	public void stopPmiClientProxy() throws Throwable {
		if(process == null) {
			return;
		}
		process.stop();
    	process = null;
	}
}
