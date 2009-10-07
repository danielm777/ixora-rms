/*
 * Created on 15-Apr-2004
 */
package com.ixora.rms.agents.apache;

import java.awt.image.DataBuffer;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import com.ixora.common.logging.AppLogger;
import com.ixora.common.logging.AppLoggerFactory;
import com.ixora.common.utils.Utils;
import com.ixora.rms.EntityDescriptorTree;
import com.ixora.rms.EntityId;
import com.ixora.rms.agents.AgentDataBufferImpl;
import com.ixora.rms.agents.AgentId;
import com.ixora.rms.agents.apache.exception.FailedToRetrieveServerStatusPage;
import com.ixora.rms.agents.apache.exception.InvalidDataPattern;
import com.ixora.rms.agents.impl.AbstractAgent;
import com.ixora.rms.exception.InvalidConfiguration;
import com.ixora.rms.exception.InvalidEntity;

/**
 * @author Daniel Moraru
 */
public final class ApacheAgent extends AbstractAgent
	implements ApacheConstants {
	/** Logger */
	private static final AppLogger logger = AppLoggerFactory.getLogger(ApacheAgent.class);
	/** Last content of the status page */
	private String content;
	/** URL of the status page of the Apache server */
	private URL url;
	/** Apache version */
	private int version;
	/** Extended status flag */
	private Boolean extendedStatusOn;
	/**
	 * Apache execution context.
	 */
	final class ApacheExecutionContext
		extends ExecutionContext implements ApacheAgentExecutionContext {
		/**
		 * @see com.ixora.rms.agents.apache.ApacheAgentExecutionContext#getApacheVersion()
		 */
		public int getApacheVersion() {
			return version;
		}
		/**
		 * @see com.ixora.rms.agents.apache.ApacheAgentExecutionContext#getStatusPageContent()
		 */
		public String getStatusPageContent() {
			return content;
		}
		/**
		 * @see com.ixora.rms.agents.apache.ApacheAgentExecutionContext#isExtendedStatusOn()
		 */
		public boolean isExtendedStatusOn() {
			return extendedStatusOn == null ? false : extendedStatusOn.booleanValue();
		}
	}

	/**
	 * Constructor.
	 */
	public ApacheAgent(AgentId agentId, Listener listener) {
		super(agentId, listener);
		ApacheExecutionContext ctxt = new ApacheExecutionContext();
		fContext = ctxt;
		fRootEntity = new ApacheEntityRoot(ctxt);
	}

	/**
	 * This is the signal that a new data collection cycle begins.
	 * Override to retrieve the status page.
	 * @see com.ixora.rms.agents.impl.AbstractAgent#prepareBuffer(DataBuffer)
	 */
	protected void prepareBuffer(AgentDataBufferImpl buffer) throws Throwable {
		// retrieve the content of the status page
		try {
			retrieveStatusPageContent();
			super.prepareBuffer(buffer);
		} catch(NumberFormatException e) {
			if(logger.isTraceEnabled()) {
				logger.error(e);
			}
			throw new InvalidDataPattern();
		}
	}

	/**
	 * Sets up the URL to the server status page
	 * @throws MalformedURLException
	 */
	private void setupURL() throws MalformedURLException {
		Configuration conf = (Configuration)this.fConfiguration.getAgentCustomConfiguration();
		String urls = "http://"
			+ this.fConfiguration.getMonitoredHost()
			+ ":" + conf.getInt(Configuration.PORT)
			+ "/server-status";
		this.url = new URL(urls);
	}

	/**
	 * @return the content availble through the http connection
	 * @throws FailedToRetrieveServerStatusPage
	 */
	private void retrieveStatusPageContent() throws IOException, FailedToRetrieveServerStatusPage {
		if(url == null) {
			setupURL();
		}
		HttpURLConnection connection = null;
		try {
			connection = (HttpURLConnection)url.openConnection();
			connection.setDoInput(true);
			ByteArrayOutputStream bos = new ByteArrayOutputStream(6000);
			Utils.transferContent(connection.getInputStream(), bos, null);
			// AFAIK the output is ANSI
			String text = bos.toString();
			this.content = text;
		} catch(FileNotFoundException e) {
			// this is thrown if server-status url is not found because the
			// ServerStatus module was not enabled
			this.content = null;
			throw new FailedToRetrieveServerStatusPage(e);
		} catch(IOException e) {
			this.content = null;
			throw new FailedToRetrieveServerStatusPage(e);
		} finally {
			if(connection != null) {
				connection.disconnect();
			}
		}
	}

	/**
	 * @see com.ixora.rms.agents.Agent#getEntities(com.ixora.rms.EntityId, boolean, boolean)
	 */
	public synchronized EntityDescriptorTree getEntities(EntityId idParent, boolean recursive, boolean refresh)
			throws InvalidEntity, Throwable {
		if(content == null) {
			// if the content was not yet set
			// try to do it now
			retrieveStatusPageContent();

			if(extendedStatusOn == null) {
				extendedStatusOn = new Boolean(ApacheUtils.isExtendedStatusOn(version, content));
			}
			if((idParent == null || "root".equals(idParent)) && fRootEntity.getChildrenCount() == 0) {
				// set up the roo entity
				((ApacheEntityRoot)fRootEntity).createChildren();
			}
		}
		return super.getEntities(idParent, recursive, refresh);
	}

	/**
	 * @see com.ixora.rms.agents.impl.AbstractAgent#configCustomChanged()
	 */
	protected void configCustomChanged() throws InvalidConfiguration, Throwable {
		String vers = fConfiguration.getSystemUnderObservationVersion();
		if(STRING_VERSION_ORACLE_1_3_X.equals(vers)) {
			version = VERSION_ORACLE_1_3_X;
		} else if(STRING_VERSION_IBM_1_3_X.equals(vers)) {
			version = VERSION_IBM_1_3_X;
		} else if(STRING_VERSION_ORACLE_2_0_X.equals(vers)) {
			version = VERSION_ORACLE_2_0_X;
		} else if(STRING_VERSION_IBM_2_0_X.equals(vers)) {
			version = VERSION_IBM_2_0_X;
		} else if(STRING_VERSION_IBM_6_0_X.equals(vers)) {
			version = VERSION_IBM_6_0_X;
		}
		// TODO revisit
		url = null;
		content = null;
		getEntities(null, false, true);
	}
}
