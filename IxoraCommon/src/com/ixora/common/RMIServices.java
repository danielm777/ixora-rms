/**
 * 15-Nov-2007
 */
package com.ixora.common;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import com.ixora.common.logging.AppLogger;
import com.ixora.common.logging.AppLoggerFactory;
import com.ixora.common.utils.Utils;

/**
 * Loads port numbers used by RMI servers throughout the application.
 * @author Daniel Moraru
 */
public class RMIServices {
	/** Logger */
	private static final AppLogger logger = AppLoggerFactory.getLogger(RMIServices.class);
	private static RMIServices instance;

	static {
		try {
			instance = new RMIServices();
		} catch(Exception e) {
			logger.error(e);
		}
	}
	private Properties fProps;

	/**
	 * @throws IOException
	 * @throws FileNotFoundException
	 *
	 */
	private RMIServices() throws FileNotFoundException, IOException {
		super();
		fProps = new Properties();
		fProps.load(new FileInputStream(Utils.getPath("/config/services.properties")));
	}

	public static RMIServices instance() {
		return instance;
	}

	public boolean useDynamicPorts() {
		return Boolean.parseBoolean(fProps.getProperty("useDynamicPorts", "true"));
	}

	public int getRMIRegistryPort() {
		return Integer.parseInt(fProps.getProperty("RMIRegistry"));
	}

	public int getPort(String service) {
		if(useDynamicPorts()) {
			return 0;
		}
		return Integer.parseInt(fProps.getProperty(service));
	}
}
