package com.ixora.rms.dataengine;

import java.io.Serializable;
import java.util.regex.Pattern;

import com.ixora.rms.HostId;
import com.ixora.rms.ResourceId;
import com.ixora.common.utils.Utils;
import com.ixora.rms.client.locator.SessionAgentInfo;
import com.ixora.rms.client.locator.SessionArtefactInfoLocator;
import com.ixora.rms.client.locator.SessionCounterInfo;
import com.ixora.rms.client.locator.SessionEntityInfo;
import com.ixora.rms.client.locator.SessionResourceInfo;
import com.ixora.rms.dataengine.definitions.StyledTagDef;


/**
 * QueryResult
 * Base class for Resources and Functions, both possible outputs of
 * a Query.
 */
public class QueryResult implements Serializable {
	private static final long serialVersionUID = 5138998403900525967L;
	/** Style associated with this query result */
	protected Style	fStyle;
	/** True if it was already localized */
	protected boolean fWasLocalized;

    /** Patterns used when replacing tokens in 'name' and 'iname' tags */
    protected static Pattern[] patEntityPaths = {
        Pattern.compile("\\$entity\\[0\\]"), Pattern.compile("\\$entity\\[1\\]"), Pattern.compile("\\$entity\\[2\\]"),
        Pattern.compile("\\$entity\\[3\\]"), Pattern.compile("\\$entity\\[4\\]"), Pattern.compile("\\$entity\\[5\\]"),
        Pattern.compile("\\$entity\\[6\\]"), Pattern.compile("\\$entity\\[7\\]"), Pattern.compile("\\$entity\\[8\\]"),
        Pattern.compile("\\$entity\\[9\\]"),
    };
    protected static Pattern[] patCaptures = {
        Pattern.compile("\\$1"), Pattern.compile("\\$2"),
        Pattern.compile("\\$3"), Pattern.compile("\\$4"), Pattern.compile("\\$5"),
        Pattern.compile("\\$6"), Pattern.compile("\\$7"), Pattern.compile("\\$8"),
        Pattern.compile("\\$9"),
    };

    /**
     * Default constructor
     */
	public QueryResult() {
		setStyle(new Style());
	}

    /**
     * Constructs an object from given values
     * @param std definition for either a resource or a function
     */
	public QueryResult(StyledTagDef std) {
	    setStyle(new Style(std));
	}

	/**
	 * Two QueryResults are equal if they have the same ID property
	 */
	public boolean equals(Object rhs) {
		QueryResult qr = (QueryResult)rhs;
		String	lhsID = this.getStyle().getID();
		String	rhsID = qr.getStyle().getID();
		if(lhsID == null || rhsID == null) {
			return false;
		}
		return lhsID.equals(rhsID);
	}

    /**
     * @param style The style to set.
     */
    public void setStyle(Style style) {
        this.fStyle = style;
    }

    /**
     * @return Returns the style.
     */
    public Style getStyle() {
        return fStyle;
    }

    /**
     * Searches for localization data and then calls the other version of localize
     * @param locator
     * @param ridMatched
     * @param captures
     * @param bLocalizeStaticOnly
     */
    public void localize(SessionArtefactInfoLocator locator,
    		ResourceId ridMatched, String[] captures,
    		boolean bLocalizeStaticOnly) {
    	if(fWasLocalized) {
    		return;
    	}
    	if(locator != null) {
    		SessionResourceInfo rInfo = locator.getResourceInfo(ridMatched);
    		localize(rInfo, ridMatched, captures, bLocalizeStaticOnly);
    	}

    }

    /**
     * Replaces known members (name, iname and description) with localized values
     * extracted from the locator. Tokens such as $host, $entity are also replaced
     * with actual values.
     * @param locator
     * @param ridMatched
     * @param captures
     * @param bLocalizeStaticOnly If true then iname will not be changed,
     * because it's yet to be used with regular expressions.
     */
    public void localize(SessionResourceInfo rInfo, ResourceId ridMatched,
    		String[] captures, boolean bLocalizeStaticOnly) {
		if(fWasLocalized) {
			return;
		}
    	if(rInfo != null) {
			fStyle.setName(replaceTokens(rInfo, fStyle.getName(), ridMatched, captures));
			if(!bLocalizeStaticOnly) {
				fStyle.setIName(replaceTokens(rInfo, fStyle.getIName(), ridMatched, captures));
			}
			// If this is a counter, search for its description
			if(ridMatched.getRepresentation() == ResourceId.COUNTER) {
                SessionCounterInfo counterInfo = rInfo.getCounterInfo();
                if(counterInfo != null) {
                	if(Utils.isEmptyString(fStyle.getDescription())) {
                		fStyle.setDescription(counterInfo.getTranslatedDescription());
                	}
                }
			}
		}
    }

    /**
     * Replaces references to regexp matches ($1, $2 etc) as well as resource ID
     * references ($host, $agent, $entity[n], $counter) with actual values.
     * @param rInfo
     * @param name String containing tokens to replace
     * @param ridMatched
     * @param captures
     * @return
     */
    protected String replaceTokens(SessionResourceInfo rInfo,
            String name, ResourceId ridMatched, String[] captures) {
    	if(name == null) {
    		return name;
    	}
        // Replace $1 - $9 tags from regexp captures
        if(captures != null) {
            for(int caps = 0; caps < captures.length; caps++) {
                if(caps < patCaptures.length) { // use cached matchers
                    name = patCaptures[caps].matcher(name).replaceAll(
                    		Utils.quoteReplacement(captures[caps]));
                } else {
                	// starts at 1
                    name = name.replaceAll("\\$" + (caps + 1), Utils.quoteReplacement(captures[caps]));
                }
            }
        } else {
            name = patCaptures[0].matcher(name).replaceAll(
            		Utils.quoteReplacement(ridMatched.toString()));
        }

        SessionEntityInfo entityInfo = null;
        // Replace $entity[n] tags
        if (ridMatched.getRepresentation() >= ResourceId.ENTITY) {
            entityInfo = rInfo.getEntityInfo();
            String[] entityPath = null;
            if (entityInfo != null) {
                entityPath = entityInfo.getTranslatedEntityPathFragments();
            } else {
                entityPath = ridMatched.getEntityId().getPathComponents();
            }
            for (int i = 0; i < entityPath.length; i++) {
                if (i < patEntityPaths.length) { // use cached matchers
                    name = patEntityPaths[i].matcher(name).replaceAll(
							Utils.quoteReplacement(entityPath[i]));
                } else {
                    name = name.replaceAll("\\$entity\\[" + i + "\\]",
                    		Utils.quoteReplacement(entityPath[i]));
                }
            }
        }

        String hostID = "";
        String agentID = "";
        String entityID = "";
        String counterID = "";
        // Note: no breaks, the case statements are intentionally fall-through
        switch (ridMatched.getRepresentation()) {
            case ResourceId.COUNTER : {
                SessionCounterInfo counterInfo = rInfo.getCounterInfo();
                if (counterInfo != null) {
                    counterID = counterInfo.getTranslatedName();
                } else {
                    counterID = ridMatched.getCounterId().toString();
                }
            }
            case ResourceId.ENTITY :
                if (entityInfo != null) {
                    entityID = entityInfo.getTranslatedEntityPath();
                } else {
                    entityID = ridMatched.getEntityId().getPath();
                }
            case ResourceId.AGENT :
                SessionAgentInfo agentInfo = rInfo.getAgentInfo();
                if (agentInfo != null) {
                    agentID = agentInfo.getTranslatedName();
                } else {
                    agentID = ridMatched.getAgentId().toString();
                }
            case ResourceId.HOST :
                HostId hID = ridMatched.getHostId();
                if(hID != null) {
                    hostID = hID.toString();
                }
        }

        // Replace $host, $agent, $entity and $counter tags
        name = Utils.replace(name, "$host", hostID);
        name = Utils.replace(name, "$agent", agentID);
        name = Utils.replace(name, "$entity", entityID);
        name = Utils.replace(name, "$counter", counterID);

        fWasLocalized = true;
        return name;
    }

    /**
     * @return whether or not this result was localized
     */
	public boolean isLocalized() {
		return fWasLocalized;
	}


}
