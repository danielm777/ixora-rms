/*
 * Created on 12-Apr-2005
 */
package com.ixora.rms.agents.impl.jmx;

import java.io.IOException;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.MBeanException;
import javax.management.ReflectionException;

import com.ixora.common.utils.Utils;
import com.ixora.rms.EntityId;
import com.ixora.rms.agents.impl.Entity;
import com.ixora.rms.agents.impl.RootEntity;

/**
 * @author Daniel Moraru
 */
public class JMXEntityRoot extends RootEntity {

	/**
	 * Constructor.
	 * @param c
	 */
	public JMXEntityRoot(JMXAgentExecutionContext c) {
		super(c);
	}

	/**
	 * @see com.ixora.rms.agents.impl.Entity#updateChildrenEntities(boolean)
	 */
	public void updateChildrenEntities(boolean recursive) throws Throwable {
		try {
			// domains don't change so do this just once
			if(getChildrenCount() == 0) {
				// get domains
				String[] domains = retrieveDomains();
				String filter = getJMXContext().getDomainFilter();
				if(!Utils.isEmptyArray(domains)) {
					for(String domain : domains) {
						if(Utils.isEmptyString(filter) || domain.matches(filter)) {
							Entity e = buildDomainEntity(getId(), getJMXContext(), domain);
							if(e != null) {
								addChildEntity(e);
							}
						}
					}
				}
			}
			// sort if required
			if(getJMXContext().sortEntities()) {
				sortChildren();
			}
			if(recursive) {
				super.updateChildrenEntities(recursive);
			}
		} catch(Throwable e) {
			getJMXContext().processException(e);
		}
	}

	/**
	 * @return the jmx execution context
	 */
	protected JMXAgentExecutionContext getJMXContext() {
		return (JMXAgentExecutionContext)fContext;
	}

	/**
	 * Subclasses can override this method to create their own domain entities. Invoked from
	 * <code>updateChildrenEntities(boolean)</code>.
	 * @param parent
	 * @param context
	 * @param domain
	 * @return return null to ignore the domain with this name
	 * @throws Throwable
	 * @throws IOException
	 * @throws ReflectionException
	 * @throws MBeanException
	 * @throws InstanceNotFoundException
	 * @throws AttributeNotFoundException
	 * @throws IntrospectionException
	 */
	protected Entity buildDomainEntity(EntityId parent, JMXAgentExecutionContext context, String domain) throws IntrospectionException, AttributeNotFoundException, InstanceNotFoundException, MBeanException, ReflectionException, IOException, Throwable {
		return new JMXEntityDomain(parent, context, domain);
	}

	/**
	 * Subclasses can override how the domains are retrieved; by default MBeanServer.getDomains() is used
	 * but this method was not available for earlier JMX specs.
	 * @return
	 * @throws Exception
	 */
	protected String[] retrieveDomains() throws Exception {
		return getJMXContext().getJMXConnection().getDomains();
	}
}
