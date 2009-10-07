/**
 * 06-Sep-2005
 */
package com.ixora.rms.agents.impl.jmx.jsr77;

import com.ixora.rms.agents.impl.jmx.JMXAgentExecutionContext;
import com.ixora.rms.agents.impl.jmx.JMXEntityRoot;

/**
 * @author Daniel Moraru
 */
public class JMXJSR77EntityRoot extends JMXEntityRoot {

	/**
	 * @param c
	 */
	public JMXJSR77EntityRoot(JMXAgentExecutionContext c) {
		super(c);
	}

	/**
	 * Need to override this as the MEJB doesn't implement getDomains() but only getDefaultDomain() according
	 * to the JSR-77 spec document.
	 * @see com.ixora.rms.agents.impl.Entity#updateChildrenEntities(boolean)
	 */
	public void updateChildrenEntities(boolean recursive) throws Throwable {
		// domains don't change so do this just once
		if(getChildrenCount() == 0) {
			String domain = getJMXContext().getJMXConnection().getDefaultDomain();
			addChildEntity(new JMXJSR77EntityDomain(getId(), (JMXAgentExecutionContext)fContext, domain));
		}
		// sort if required
		if(getJMXContext().sortEntities()) {
			sortChildren();
		}

		if(recursive) {
			super.updateChildrenEntities(recursive);
		}
	}
}
