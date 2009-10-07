/**
 * 08-Sep-2005
 */
package com.ixora.rms.agents.sunapp.v8;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.MBeanException;
import javax.management.ReflectionException;

import com.ixora.common.utils.Utils;
import com.ixora.rms.EntityId;
import com.ixora.rms.agents.impl.Entity;
import com.ixora.rms.agents.impl.jmx.JMXAgentExecutionContext;
import com.ixora.rms.agents.impl.jmx.JMXEntityDomain;
import com.ixora.rms.agents.impl.jmx.JMXEntityRoot;

/**
 * @author Daniel Moraru
 */
public class SunAppEntityRoot extends JMXEntityRoot {

	/**
	 * @param c
	 */
	public SunAppEntityRoot(JMXAgentExecutionContext c) {
		super(c);
	}

	/**
	 * @see com.ixora.rms.agents.impl.jmx.JMXEntityRoot#retrieveDomains()
	 */
	protected String[] retrieveDomains() throws Exception {
		String[] domainsOrig = super.retrieveDomains();
		Set<String> domains = new HashSet<String>();
			if(!Utils.isEmptyArray(domainsOrig)) {
				for(String domain : domainsOrig) {
					if(getJMXContext().getAgentConfiguration()
							.getAgentCustomConfiguration().getBoolean(Configuration.SHOW_JUST_RUNTIME_DATA)) {
						if(!"JMImplementation".equals(domain)
							&& !"amx-support".equals(domain)
								&& !"ias".equals(domain)) {
							domains.add(domain);
						}
					} else {
						domains.add(domain);
					}
				}
		}
		return domains.toArray(new String[domains.size()]);
	}



	/**
	 * @see com.ixora.rms.agents.impl.jmx.JMXEntityRoot#buildDomainEntity(com.ixora.rms.EntityId, com.ixora.rms.agents.impl.jmx.JMXAgentExecutionContext, java.lang.String)
	 */
	protected Entity buildDomainEntity(EntityId id, JMXAgentExecutionContext context, String domain) throws IntrospectionException, AttributeNotFoundException, InstanceNotFoundException, MBeanException, ReflectionException, IOException, Throwable {
		return new JMXEntityDomain(id, context, domain);
	}
}
