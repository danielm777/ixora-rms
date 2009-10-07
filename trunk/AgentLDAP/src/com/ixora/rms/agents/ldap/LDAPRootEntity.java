/*
 * Created on 21-Aug-2005
 */
package com.ixora.rms.agents.ldap;


import java.util.Iterator;

import com.ixora.common.MessageRepository;
import com.ixora.common.utils.Utils;
import com.ixora.rms.EntityId;
import com.ixora.rms.agents.AgentExecutionContext;
import com.ixora.rms.agents.impl.RootEntity;
import com.ixora.rms.agents.ldap.messages.Msg;
import com.ixora.rms.exception.RMSException;
import com.novell.ldap.LDAPAttributeSet;
import com.novell.ldap.LDAPConnection;
import com.novell.ldap.LDAPEntry;
import com.novell.ldap.LDAPException;
import com.novell.ldap.LDAPSearchResults;

/**
 * LDAPRootEntity
 * @author Daniel Moraru
 */
public final class LDAPRootEntity extends RootEntity {

	/**
	 * Constructor
	 * @param ctxt
	 * @throws LDAPAgentException
	 */
	public LDAPRootEntity(AgentExecutionContext ctxt) {
		super(ctxt);
	}

	/**
	 * @see com.ixora.rms.agents.impl.RootEntity#updateChildrenEntities(boolean)
	 */
	public void updateChildrenEntities(boolean recursive) throws Throwable {
		try {
	        Configuration cfg = (Configuration)fContext.getAgentConfiguration().getAgentCustomConfiguration();
	        String groupDN = cfg.getString(Configuration.BASE_DN);
	        String filter = cfg.getString(Configuration.FILTER);
	        if(Utils.isEmptyString(filter)) {
	        	filter = null;
	        }
			// Extract nodes and create root entities
	        int searchScope = LDAPConnection.SCOPE_BASE;
            LDAPSearchResults searchResults = getLDAPContext().getConnection().search(
            		groupDN,       // object to read
                    searchScope,   // scope - read this node
                    filter,          // search filter
                    null,          // return only required attributes
                    false,         // return attrs and values
                    getLDAPContext().getSearchConstraints()); // time out value

            while(searchResults.hasMore()) {
	            try {
	            	LDAPEntry nextEntry = searchResults.next();
	                LDAPAttributeSet attributeSet = nextEntry.getAttributeSet();
	                // Add entity with this DN, or the default root name
	                String name = nextEntry.getDN();
	                if(Utils.isEmptyString(name)) {
	                	name = MessageRepository.get(Msg.LDAP_NAME, Msg.LDAP_ROOT_NAME);
	                }
	                LDAPEntity oldEntity = (LDAPEntity)getChildEntity(new EntityId(getId(), name));
	                if(oldEntity == null) {
		                addChildEntity(new LDAPEntity(getId(), name,
		                		nextEntry.getDN(), attributeSet, fContext));
	                } else {
	                	oldEntity.updateData(attributeSet);
	                }
	            } catch(LDAPException e) {
	            	fContext.error(e);
	            	continue;
	            }
            }
            // remove stale children
            for(Iterator iter = fChildrenEntities.values().iterator(); iter.hasNext(); ) {
				LDAPEntity entity = (LDAPEntity)iter.next();
				if(!entity.isTouchedByUpdate()) {
					iter.remove();
				}
			}
        } catch(LDAPException e) {
        	throw new RMSException(e);
        }
		super.updateChildrenEntities(recursive);
	}

	/**
	 * @return
	 */
	private LDAPAgentExecutionContext getLDAPContext() {
		return (LDAPAgentExecutionContext)fContext;
	}
}
