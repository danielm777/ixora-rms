/*
 * Created on 13-Apr-2005
 */
package com.ixora.rms.agents.weblogic.v8;

import java.util.HashSet;
import java.util.Set;

import javax.management.ObjectName;

import com.ixora.common.utils.Utils;
import com.ixora.rms.agents.impl.jmx.JMXAgentExecutionContext;
import com.ixora.rms.agents.impl.jmx.JMXEntityRoot;

/**
 * @author Daniel Moraru
 */
public class WeblogicEntityRoot extends JMXEntityRoot {

	/**
	 * Constructor.
	 * @param c
	 * @throws Throwable
	 */
	public WeblogicEntityRoot(JMXAgentExecutionContext c) throws Throwable {
		super(c);
	}

	/**
	 * @see com.ixora.rms.agents.weblogic.v8.duplicates.JMXEntityRoot#retrieveDomains()
	 */
	protected String[] retrieveDomains() throws Exception {
		Set<ObjectName> mbeans = getJMXContext().getJMXConnection().queryNames(null, null);
		Set<String> domains = new HashSet<String>();
		if(!Utils.isEmptyCollection(mbeans)) {
			for(ObjectName mbean : mbeans) {
				String domain = mbean.getDomain();
				if(getJMXContext().getAgentConfiguration()
						.getAgentCustomConfiguration().getBoolean(Configuration.SHOW_JUST_RUNTIME_DATA)) {
					if(!"JMImplementation".equals(domain)
						&& !"weblogic".equals(domain)
							&& !"WeblogicManagement".equals(domain)
								&& !"Security".equals(domain)) {
						domains.add(domain);
					}
				} else {
					domains.add(domain);
				}
			}
		}
		return domains.toArray(new String[domains.size()]);
	}
}
