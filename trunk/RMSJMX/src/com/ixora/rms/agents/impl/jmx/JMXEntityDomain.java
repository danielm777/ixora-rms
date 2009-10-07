/*
 * Created on 12-Apr-2005
 */
package com.ixora.rms.agents.impl.jmx;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.MBeanException;
import javax.management.ObjectName;
import javax.management.ReflectionException;

import com.ixora.common.logging.AppLogger;
import com.ixora.common.logging.AppLoggerFactory;
import com.ixora.rms.EntityId;
import com.ixora.rms.agents.impl.Entity;

/**
 * @author Daniel Moraru
 */
public class JMXEntityDomain extends JMXEntity {
	/**
	 * Logger. This must be used for debugging only. For any other reasons the errors
	 * must be propagated using the context.
	 */
	private static final AppLogger logger = AppLoggerFactory.getLogger(JMXEntityDomain.class);

	/**
	 * Constructor.
	 * @param id
	 * @param c
	 * @param domain
	 * @throws IntrospectionException
	 * @throws AttributeNotFoundException
	 * @throws InstanceNotFoundException
	 * @throws MBeanException
	 * @throws ReflectionException
	 * @throws IOException
	 * @throws Throwable
	 */
	public JMXEntityDomain(EntityId parent, JMXAgentExecutionContext c,
			String domain) throws IntrospectionException,
			AttributeNotFoundException, InstanceNotFoundException,
			MBeanException, ReflectionException, IOException, Throwable {
		super(new EntityId(parent, domain), c, null);
		fHasChildren = true;
		// no counters, just children
	}

	/**
	 * Constructor.
	 * @param id
	 * @param c
	 * @param domain
	 * @throws IntrospectionException
	 * @throws AttributeNotFoundException
	 * @throws InstanceNotFoundException
	 * @throws MBeanException
	 * @throws ReflectionException
	 * @throws IOException
	 * @throws Throwable
	 */
	public JMXEntityDomain(EntityId parent, JMXAgentExecutionContext c,
			String domain,
			ObjectName domainServiceBeanObjectName) throws IntrospectionException,
			AttributeNotFoundException, InstanceNotFoundException,
			MBeanException, ReflectionException, IOException, Throwable {
		super(new EntityId(parent, domain), c, domainServiceBeanObjectName, null);
		fHasChildren = true;
		// no counters, just children
	}

	/**
	 * @see com.ixora.rms.agents.impl.Entity#updateChildrenEntities(boolean)
	 */
	public void updateChildrenEntities(boolean recursive) throws Throwable {
		try {
			// get all names in this domain according to filter
			Set names = getJMXContext().getJMXConnection().queryNames(new ObjectName(getName()
					+ ":"
					+ getJMXContext().getKeyFilter(getName())), null);

			if(logger.isTraceEnabled()) {
				logger.trace("Objects filtered for " + getId() + ": " + names);
				logger.trace("Size: " + names.size());
			}

			updateEntityHierarchy(names);

			if(getJMXContext().sortEntities()) {
				sortChildren();
			}

			if(recursive) {
				for(Entity child : fChildrenEntities.values()) {
					child.updateChildrenEntities(recursive);
				}
			}
		} catch(Throwable e) {
			if(logger.isTraceEnabled()) {
				logger.error(e);
			}
			getJMXContext().processException(e);
		}
	}

	/**
	 * This method will be invoked once when constructing the tree starting with
	 * this domain.<br>
	 * The default impl will add all mbeans as children of this domain; subclasses can
	 * use this method to construct a more complicated hierarchy.
	 * @param oname
	 * @throws Throwable
	 * @throws IOException
	 * @throws ReflectionException
	 * @throws MBeanException
	 * @throws InstanceNotFoundException
	 * @throws AttributeNotFoundException
	 * @throws IntrospectionException
	 */
	protected void updateEntityHierarchy(Set<ObjectName> names) throws IntrospectionException, AttributeNotFoundException, InstanceNotFoundException, MBeanException, ReflectionException, IOException, Throwable {
		if(logger.isTraceEnabled()) {
			logger.trace("Updating entity hierarchy for " + getId());
		}

		Set<EntityId> newEntities = new HashSet<EntityId>();
		for(ObjectName oname : names) {
			try {
				if(logger.isTraceEnabled()) {
					logger.trace("Checking oname: " + oname);
				}

				if(getJMXContext().acceptEntity(oname)) {
					Entity child = fChildrenEntities.get(
							JMXEntity.createEntityId(getJMXContext(), getId(), oname));
					if(child == null) {
						child = new JMXEntity(getId(), getJMXContext(), oname, null);
						addChildEntity(child);
						if(logger.isTraceEnabled()) {
							logger.trace("Added as child: " + oname);
						}
					}
					newEntities.add(child.getId());
					if(logger.isTraceEnabled()) {
						logger.trace("Added to new entities: " + oname);
					}
				} else {
					if(logger.isTraceEnabled()) {
						logger.trace("Rejected as entity.");
					}
				}
			} catch(Exception e) {
				// allow agent implementations to treat this as a fatal error
				// or to keep going...
				getJMXContext().processException(e);
			}
		}
		// now remove all existing entities which are not part of
		// the new set
		for(Iterator<EntityId> iter = fChildrenEntities.keySet().iterator(); iter.hasNext();) {
			EntityId child = iter.next();
			if(!newEntities.contains(child)) {
				iter.remove();
				if(logger.isTraceEnabled()) {
					logger.trace("Removing: " + child);
				}
			}
		}
	}
}
