/**
 * 26-Jul-2005
 */
package com.ixora.rms.agents.jboss.v4;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.TreeMap;

import javax.management.MBeanAttributeInfo;
import javax.management.ObjectName;
import javax.naming.Context;
import javax.naming.InitialContext;

import org.jboss.jmx.adaptor.rmi.RMIAdaptor;

import com.ixora.common.logging.AppLogger;
import com.ixora.common.logging.AppLoggerFactory;
import com.ixora.common.utils.Utils;
import com.ixora.rms.agents.AgentId;
import com.ixora.rms.agents.impl.jmx.JMXAbstractAgent;
import com.ixora.rms.agents.impl.jmx.JMXConnectionMBeanServer;
import com.ixora.rms.agents.impl.jmx.MBeanServerProxy;
import com.ixora.rms.exception.InvalidConfiguration;
import com.ixora.rms.exception.RMSException;

/**
 * @author Daniel Moraru
 */
public class JBossAgent extends JMXAbstractAgent {
	/**
	 * Logger. This must be used for debugging only. For any other reasons the errors
	 * must be propagated using the context.
	 */
	private static final AppLogger logger = AppLoggerFactory.getLogger(JBossAgent.class);

	/**
	 * @param agentId
	 * @param listener
	 */
	public JBossAgent(AgentId agentId, Listener listener) {
		super(agentId, listener);
		//fRootEntity = new JBossEntityRoot((JMXAgentExecutionContext)fContext);
		fEnvMap = new HashMap<String, Object>();
		fEnvMap.put(Context.INITIAL_CONTEXT_FACTORY, "org.jnp.interfaces.NamingContextFactory");
		fEnvMap.put(Context.URL_PKG_PREFIXES, "org.jboss.naming:org.jnp.interfaces");
	}

	/**
	 * @see com.ixora.rms.agents.impl.AbstractAgent#configCustomChanged()
	 */
	@SuppressWarnings("unchecked")
	protected void configCustomChanged() throws InvalidConfiguration, Throwable {
		// set up credentials
		fEnvMap.put(Context.SECURITY_PRINCIPAL, fConfiguration.getAgentCustomConfiguration().getString(Configuration.USERNAME));
		fEnvMap.put(Context.SECURITY_CREDENTIALS, fConfiguration.getAgentCustomConfiguration().getString(Configuration.PASSWORD));
		fEnvMap.put(Context.PROVIDER_URL, fConfiguration.getAgentCustomConfiguration().getString(Configuration.SERVER_URL));
		super.configCustomChanged();
		try {
	        InitialContext context = new InitialContext(new Hashtable(fEnvMap));
	        RMIAdaptor rmiAdaptor = (RMIAdaptor)context.lookup(
	        		fConfiguration.getAgentCustomConfiguration().getString(Configuration.JNDI_NAME));
			fJMXConnection = new JMXConnectionMBeanServer(MBeanServerProxy.buildProxy(rmiAdaptor));
	//		InitialContext context = new InitialContext(new Hashtable(fNamingEnv));
	//        Object objref = context.lookup("ejb/mgmt/MEJB");
	//        ManagementHome home = (ManagementHome)PortableRemoteObject.narrow(objref, javax.management.j2ee.ManagementHome.class);
	//        Management mejb = home.create();
	//        fMBeanServer = MBeanServerProxy.buildProxy(mejb);
		} catch(Exception e) {
			throw new RMSException(e);
		}
	}

	/**
	 * @see com.ixora.rms.agents.impl.jmx.JMXAbstractAgent#getDomainFilter()
	 */
	protected String getDomainFilter() {
		return "(?!jboss\\.deployment).*";
	}

	/**
	 * @see com.ixora.rms.agents.impl.jmx.JMXAbstractAgent#getKeyFilter(java.lang.String)
	 */
	protected String getKeyFilter(String domain) {
		return "*";
	}

	/**
	 * Ignores counters Type and Name
	 * @see com.ixora.rms.agents.impl.jmx.JMXAbstractAgent#acceptCounter(javax.management.ObjectName, javax.management.MBeanAttributeInfo)
	 */
	protected boolean acceptCounter(ObjectName oname, MBeanAttributeInfo ainfo) {
		return true;
	}

	/**
	 * @see com.ixora.rms.agents.impl.jmx.JMXAbstractAgent#processException(java.lang.Throwable)
	 */
	protected void processException(Throwable t) throws Throwable {
		// for some reasons this exception is thrown when retrieving the details
		// for certain entities...
		if(Utils.getTrace(t).indexOf("java.io.NotSerializableException") >= 0) {
			// ignore
			if(logger.isTraceEnabled()) {
				logger.error(t);
			}
			return;
		}
		super.processException(t);
	}

	/**
	 * @see com.ixora.rms.agents.impl.jmx.JMXAbstractAgent#getEntityName(javax.management.ObjectName)
	 */
	protected String getEntityName(ObjectName on) {
		if(logger.isTraceEnabled()) {
			logger.trace("Getting entity name from ObjectName: " + on);
		}
		if(on.toString().equals("jboss.management.local:j2eeType=J2EEDomain,name=Manager")) {
			return on.getDomain();
		}

		if(logger.isTraceEnabled()) {
			logger.trace("Rearranging attributes for: " + on);
		}

		Hashtable<String, String> props = on.getKeyPropertyList();
		if(!Utils.isEmptyMap(props)) {
			StringBuffer buff = new StringBuffer();
			buff.append(on.getDomain()).append(":");
			// reorganize string, place j2eeType first then name and the rest sorted
			TreeMap<String, String> sorted = new TreeMap<String, String>(props);
			String j2eeType = props.get("j2eeType");
			if(j2eeType != null) {
				buff.append("j2eeType=").append(j2eeType).append(",");
				sorted.remove("j2eeType");
			}
			String type = props.get("type");
			if(type != null) {
				buff.append("type=").append(type).append(",");
				sorted.remove("type");
			}
			String name = props.get("name");
			if(name != null) {
				buff.append("name=").append(name).append(",");
				sorted.remove("name");
			}
			for(Map.Entry<String, String> e : sorted.entrySet()) {
				buff.append(e.getKey()).append("=").append(e.getValue()).append(",");
			}
			buff.delete(buff.length() - 1, buff.length());

			if(logger.isTraceEnabled()) {
				logger.trace("Rearranged: " + buff.toString());
			}

			return buff.toString();
		}

		return on.toString();
	}

	/**
	 * @see com.ixora.rms.agents.impl.jmx.JMXAbstractAgent#sortEntities()
	 */
	protected boolean sortEntities() {
		return true;
	}
}
