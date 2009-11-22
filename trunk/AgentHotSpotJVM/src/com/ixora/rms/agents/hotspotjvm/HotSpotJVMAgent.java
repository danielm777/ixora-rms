/*
 * Created on 10-Apr-2005
 */
package com.ixora.rms.agents.hotspotjvm;

import java.util.HashMap;

import javax.management.MBeanAttributeInfo;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenType;
import javax.management.remote.JMXConnector;
import javax.naming.Context;

import com.ixora.common.utils.Utils;
import com.ixora.rms.agents.AgentId;
import com.ixora.rms.agents.hotspotjvm.messages.Msg;
import com.ixora.rms.agents.impl.jmx.JMXAgentExecutionContext;
import com.ixora.rms.agents.impl.jmx.jsr160.JMXJSR160AbstractAgent;

/**
 * @author Daniel Moraru
 */
public class HotSpotJVMAgent extends JMXJSR160AbstractAgent {

	/**
	 * Constructor.
	 */
	public HotSpotJVMAgent(AgentId agentId, Listener listener) {
		super(agentId, listener);
		fRootEntity = new HotSpotJVMEntityRoot((JMXAgentExecutionContext)fContext);
    	fEnvMap = new HashMap<String, Object>();
    	fEnvMap.put(Context.INITIAL_CONTEXT_FACTORY,
    			"com.sun.jndi.rmi.registry.RegistryContextFactory");
    	fEnvMap.put(Context.URL_PKG_PREFIXES, "");

	}

	/**
	 * @see com.ixora.rms.agents.impl.jmx.JMXAbstractAgent#getJMXConnectionURL()
	 */
	protected String getJMXConnectionURL() {
		// pass credentials if present
		String username = fConfiguration.getAgentCustomConfiguration().getString(Configuration.USERNAME);
		if(!Utils.isEmptyString(username)) {
			String password = fConfiguration.getAgentCustomConfiguration().getString(Configuration.PASSWORD);
			if(password == null) {
				password = "";
			}
			String[] credentials = new String[] { username , password };
	        fEnvMap.put(JMXConnector.CREDENTIALS, credentials);
		}

		return ((Configuration)this.fConfiguration.getCustom())
			.getString(Msg.JMX_CONNECTION_STRING);
	}

	/**
	 * @see com.ixora.rms.agents.impl.jmx.JMXAbstractAgent#getDomainFilter()
	 */
	protected String getDomainFilter() {
		return "java.lang";
	}

	/**
	 * @see com.ixora.rms.agents.impl.jmx.JMXAbstractAgent#getKeyFilter(java.lang.String)
	 */
	protected String getKeyFilter(String domain) {
		return "*";
	}

	/**
	 * @see com.ixora.rms.agents.impl.jmx.JMXAbstractAgent#acceptCounter(javax.management.ObjectName, javax.management.MBeanAttributeInfo)
	 */
	protected boolean acceptCounter(ObjectName oname, MBeanAttributeInfo ainfo) {
		if(ainfo.getName().equals("Name")) {
			return false;
		}
		String soname = oname.toString();
		if(soname.startsWith("java.lang:type=MemoryPool,name=")) {
			if(ainfo.getName().indexOf("Threshold") >= 0) {
				return false;
			}
		}
		return true;
	}


	/**
	 * @see com.ixora.rms.agents.impl.jmx.JMXAbstractAgent#acceptCounter(javax.management.ObjectName, java.lang.String, java.lang.String, javax.management.openmbean.OpenType)
	 */
	protected boolean acceptCounter(ObjectName oname, String attrName,
			String sitem, OpenType<?> otype) {
		String soname = oname.toString();
		if(soname.startsWith("java.lang:type=GarbageCollector")) {
			if(attrName.equals("LastGcInfo")) {
				if(sitem.equals("id")) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * @see com.ixora.rms.agents.impl.jmx.JMXAbstractAgent#getCounterNameAndDescription(javax.management.ObjectName, javax.management.MBeanAttributeInfo)
	 */
	protected String[] getCounterNameAndDescription(ObjectName oname,
			MBeanAttributeInfo ainfo) {
		String soname = oname.toString();
		if(soname.equals("java.lang:type=Compilation")) {
			return new String[] {
					"Compilation." + ainfo.getName(),
					"Compilation." + ainfo.getName() + ".desc"};
		}
		if(soname.equals("java.lang:type=MemoryManager,name=CodeCacheManager")) {
			return new String[] {
					"CodeCacheManager." + ainfo.getName(),
					"CodeCacheManager." + ainfo.getName() + ".desc"};
		}
		if(soname.startsWith("java.lang:type=GarbageCollector,name=")) {
			return new String[] {
					"GarbageCollector." + ainfo.getName(),
					"GarbageCollector." + ainfo.getName() + ".desc"};
		}
		if(soname.startsWith("java.lang:type=Runtime")) {
			return new String[] {
					"Runtime." + ainfo.getName(),
					"Runtime." + ainfo.getName() + ".desc"};
		}
		if(soname.startsWith("java.lang:type=ClassLoading")) {
			return new String[] {
					"ClassLoading." + ainfo.getName(),
					"ClassLoading." + ainfo.getName() + ".desc"};
		}
		if(soname.startsWith("java.lang:type=Threading")) {
			return new String[] {
					"Threading." + ainfo.getName(),
					"Threading." + ainfo.getName() + ".desc"};
		}
		if(soname.startsWith("java.lang:type=OperatingSystem")) {
			return new String[] {
					"OperatingSystem." + ainfo.getName(),
					"OperatingSystem." + ainfo.getName() + ".desc"};
		}
		if(soname.startsWith("java.lang:type=Memory")) {
			return new String[] {
					"Memory." + ainfo.getName(),
					"Memory." + ainfo.getName() + ".desc"};
		}
		return super.getCounterNameAndDescription(oname, ainfo);
	}

	/**
	 * @see com.ixora.rms.agents.impl.jmx.JMXAbstractAgent#getCounterDescription(javax.management.ObjectName, javax.management.openmbean.CompositeType, java.lang.String)
	 */
	protected String[] getCounterNameAndDescription(ObjectName oname, String attrName, CompositeType ct,
			String item) {
		String soname = oname.toString();
		if(soname.startsWith("java.lang:type=GarbageCollector") && attrName.equals("LastGcInfo")) {
			return new String[] {
					"GarbageCollector." + item,
					"GarbageCollector." + item + ".desc"};
		}
		if(soname.startsWith("java.lang:type=MemoryPool,name=")
				|| soname.startsWith("java.lang:type=Memory")) {
			if(attrName.equals("Usage")
					|| attrName.equals("PeakUsage")
					|| attrName.equals("CollectionUsage")
					|| attrName.equals("HeapMemoryUsage")
					|| attrName.equals("NonHeapMemoryUsage"))
			return new String[] {
					"Memory." + item,
					"Memory." + item + ".desc"};
		}
		return super.getCounterNameAndDescription(oname, attrName, ct, item);
	}
}
