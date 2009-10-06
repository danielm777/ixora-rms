package com.ixora.rms.dataengine;

import com.ixora.rms.ResourceId;
import com.ixora.rms.CounterDescriptor;
import com.ixora.rms.client.locator.SessionArtefactInfoLocator;
import com.ixora.rms.dataengine.definitions.ResourceDef;


/**
 * Resource
 * Implementation of a query resource, created based on a ResourceDef.
 * Together with Functions, Resources are possible outputs of a Query.
 */
public class Resource extends QueryResult {
	/**
	 * IDs used to identify a counter. Some of them might be omitted,
	 * others may contain regular expressions or wildcards
	 */
	private ResourceId	resourceID;

	/**
	 * Constructs a Resource based on its associated XML definition.
	 * @param rd object holding definition (loaded from XML)
	 */
	public Resource(ResourceDef rd, ResourceId ridContext) {
	    super(rd);
	    resourceID = rd.getResourceId().complete(ridContext);
	    realize(resourceID);
	}

	/**
	 * @return the definition for this resource
	 */
	public ResourceDef getResourceDef() {
		return new ResourceDef(resourceID, fStyle.getStyleDef());
	}

    /**
     * @return Returns the resourceID.
     */
    public ResourceId getResourceID() {
        return resourceID;
    }

    /**
     * @param locator
     */
    public void localize(SessionArtefactInfoLocator locator) {
        localize(locator, this.resourceID, null, true);
    }

    /**
     * If style's ID is null, sets it to resource ID. Also tries to
     * get some hints about counter type, if not set.
     * Uses the styles repository to resolve 'style' tags inheritance.
     * @param resourceID resource ID to realize
     */
	private void realize(ResourceId resourceID) {
	    HintManager	hm = HintManager.instance();
	    hm.resolveStyle(fStyle);
		// Try to find some hints for the counter type
		if (fStyle.getType() == null) {
			// If the resource id specifies less than
			// the counter level, then it'll be a string
			if (resourceID.getRepresentation() != ResourceId.COUNTER) {
				fStyle.setType(Style.TYPE_STRING);
			} else {// The timestamp counter is of type date
				if (resourceID.getCounterId().equals(CounterDescriptor.TIMESTAMP_ID)) {
					fStyle.setType(Style.TYPE_DATE);
				} else { // default type is number
					fStyle.setType(Style.TYPE_NUMBER);
				}
			}
		}
		// set an instance name, not allowing for
		// null iname and name here at resource level
		// makes the handling of missing inames in other classes that
		// merge their styles with resources styles (like functions)
		// much easier to manage
		String iname = getDefaultIName();
		if(fStyle.getIName() == null) {
			fStyle.setIName(iname);
		}
		if(fStyle.getName() == null) {
			fStyle.setName(iname);
		}
	}

    /**
     * @param rid
     * @return
     */
    private String getDefaultIName() {
        String retVal = "";
        if (resourceID == null) {
            return retVal;
        }
        switch (resourceID.getRepresentation()) {
            case ResourceId.COUNTER:
                retVal = "$host/$agent/$entity/$counter";
                break;
            case ResourceId.ENTITY:
                retVal = "$host/$agent/$entity";
                break;
            case ResourceId.AGENT:
                retVal = "$host/$agent";
                break;
            case ResourceId.HOST:
                retVal = "$host";
                break;
        }
        return retVal;
    }
}
