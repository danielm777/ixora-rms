package com.ixora.rms.agents.jboss.v5;

import com.ixora.common.logging.AppLogger;
import com.ixora.common.logging.AppLoggerFactory;
import com.ixora.common.utils.Utils;
import com.ixora.rms.agents.AgentId;
import com.ixora.rms.agents.jmxjsr160.JMXJSR160Agent;

/**
 * JBoss agent for v5+ that supports JSR160.
 * @author Daniel Moraru
 */
public class JBossAgent extends JMXJSR160Agent {
	/**
	 * Logger. This must be used for debugging only. For any other reasons the errors
	 * must be propagated using the context.
	 */
	private static final AppLogger logger = AppLoggerFactory.getLogger(JBossAgent.class);

	public JBossAgent(AgentId agentId, Listener listener) {
		super(agentId, listener);
	}

	/**
	 * This is overridden because an exception (java.io.NotSerializableException) is thrown when 
	 * retrieving data from JBoss for certain MBeans. This method simply ignores this exception which allows the
	 * agent to continue the operation.
	 * @see com.ixora.rms.agents.impl.jmx.JMXAbstractAgent#processException(java.lang.Throwable)
	 */
	protected void processException(Throwable t) throws Throwable {
		// this exception is thrown when retrieving the details
		// for certain entities (JBoss should be careful here)...
		if(Utils.getTrace(t).indexOf("java.io.NotSerializableException") >= 0) {
			// ignore
			if(logger.isTraceEnabled()) {
				logger.error(t);
			}
			return;
		}
		super.processException(t);
	}
}
