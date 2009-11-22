package com.ixora.rms.agents.ldap;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.ixora.common.utils.Utils;
import com.ixora.rms.CounterId;
import com.ixora.rms.CounterType;
import com.ixora.rms.EntityId;
import com.ixora.rms.agents.AgentExecutionContext;
import com.ixora.rms.agents.impl.Counter;
import com.ixora.rms.agents.impl.Entity;
import com.ixora.rms.data.CounterValueString;
import com.ixora.rms.exception.RMSException;
import com.novell.ldap.LDAPAttribute;
import com.novell.ldap.LDAPAttributeSet;
import com.novell.ldap.LDAPConnection;
import com.novell.ldap.LDAPEntry;
import com.novell.ldap.LDAPException;
import com.novell.ldap.LDAPSearchResults;

/**
 * LDAPEntity
 * Represents a node in a LDAP tree. Contains LDAP attributes as counters
 * @author Daniel Moraru
 */
public final class LDAPEntity extends Entity {
	private static final long serialVersionUID = -4038994152871931L;
	private String fLDAPPath;

	/**
	 * @param parentEntity
	 * @param name
	 * @param ldapPath
	 * @param attributeSet
	 * @param ctx
	 */
	public LDAPEntity(EntityId parentEntity,
			String name, String ldapPath, LDAPAttributeSet attributeSet,
			AgentExecutionContext ctx) {
		super(new EntityId(parentEntity, name), ctx);
		fLDAPPath = ldapPath;
		fHasChildren = true;
		updateData(attributeSet);
	}

	/**
	 * Updates child LDAP nodes
	 */
	@SuppressWarnings("unchecked")
	public void updateData(LDAPAttributeSet attributeSet) {
		setTouchedByUpdate(true);
        // Read attributes and make counters out of them
        Iterator allAttributes = attributeSet.iterator();
        while(allAttributes.hasNext()) {
            LDAPAttribute attribute = (LDAPAttribute)allAttributes.next();
            String attributeName = attribute.getName();
            CounterId cid = new CounterId(attributeName);
            if(fCounters.get(cid) == null) {
	            // TODO: determine the attribute type
				addCounter(new LDAPCounter(attributeName,
						attributeName, CounterType.STRING));
            }
        }
	}

	/**
	 * @see com.ixora.rms.agents.impl.Entity#updateChildrenEntities(boolean)
	 */
	public void updateChildrenEntities(boolean recursive) throws Throwable {
		// Extract nodes and create root entities
        Configuration cfg = (Configuration)fContext.getAgentConfiguration().getAgentCustomConfiguration();
        String filter = cfg.getString(Configuration.FILTER);
        if(Utils.isEmptyString(filter)) {
        	filter = null;
        }
        int searchScope = LDAPConnection.SCOPE_ONE;
        LDAPSearchResults searchResults = getLDAPContext().getConnection().search(
        		fLDAPPath,     // object to read
                searchScope,   // scope - read subtree
                filter,          // search filter
                null,          // return only required attributes
                false,         // return attrs and values
                getLDAPContext().getSearchConstraints()); // time out value

        resetTouchedByUpdateForChildren();
        while (searchResults.hasMore()) {
            try {
            	LDAPEntry nextEntry = searchResults.next();
                LDAPAttributeSet attributeSet = nextEntry.getAttributeSet();
                String name = nextEntry.getDN();
                if(!Utils.isEmptyString(name)) {
	                // Create or update subentity
	                LDAPEntity oldEntity = (LDAPEntity)getChildEntity(new EntityId(getId(), name));
	                if (oldEntity == null) {
		                addChildEntity(new LDAPEntity(getId(), name,
		                		nextEntry.getDN(), attributeSet, fContext));
	                } else {
	                	oldEntity.updateData(attributeSet);
	                }
                }
            } catch(LDAPException e) {
            	//e.printStackTrace();
            	fContext.error(e);
            	continue;
            }
		}
        removeStaleChildren();
		super.updateChildrenEntities(recursive);
	}

	/**
	 * @see com.ixora.rms.agents.impl.Entity#retrieveCounterValues()
	 */
	@SuppressWarnings("unchecked")
	protected void retrieveCounterValues() throws Throwable {
		try {
			// Extract a list of enabled counters
			List<String> listAttributes = new LinkedList<String>();
			for (Counter counter : fCounters.values()) {
				if (counter.isEnabled()) {
					listAttributes.add(counter.getName());
				}
			}

			String[] attrs = listAttributes.toArray(new String[listAttributes.size()]);
	        int searchScope = LDAPConnection.SCOPE_BASE;
            LDAPSearchResults searchResults = getLDAPContext().getConnection().search(
            		fLDAPPath,     // object to read
                    searchScope,   // scope - read single object
                    null,          // search filter
                    attrs,         // return only required attributes
                    false,         // return attrs and values
                    getLDAPContext().getSearchConstraints()); // time out value

            // Results of this search is at most one entry
            if (searchResults.hasMore()) {
	            LDAPEntry nextEntry = null;
	            try {
	                nextEntry = searchResults.next();

	                // Read attribute values
	                LDAPAttributeSet attributeSet = nextEntry.getAttributeSet();
	                Iterator allAttributes = attributeSet.iterator();
	                while(allAttributes.hasNext()) {
	                    LDAPAttribute attribute = (LDAPAttribute)allAttributes.next();
	                    String attributeName = attribute.getName();
	                    String attributeValue = attribute.getStringValue();

	                    Counter counter = getCounter(new CounterId(attributeName));
	                    if (counter != null) {
	                    	// TODO: an attribute can have more values, deal with this.
	                    	counter.dataReceived(
	                    			new CounterValueString(attributeValue));
	                    }
	                }

	            } catch(LDAPException e) {
	            	fContext.error(e);
	            }
            }
		} catch(LDAPException e) {
			throw new RMSException(e);
		}
	}

	/**
	 * @return
	 */
	private LDAPAgentExecutionContext getLDAPContext() {
		return (LDAPAgentExecutionContext)fContext;
	}

}