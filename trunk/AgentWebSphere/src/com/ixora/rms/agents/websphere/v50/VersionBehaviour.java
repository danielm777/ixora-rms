/*
 * Created on 24-Jun-2005
 */
package com.ixora.rms.agents.websphere.v50;

import com.ibm.websphere.pmi.PmiConstants;
import com.ibm.websphere.pmi.PmiDataInfo;
import com.ixora.rms.EntityId;
import com.ixora.rms.agents.websphere.v50.proxy.PerfDescriptor;

final class VersionBehaviour implements RelatedVersionBehaviour, PmiConstants {

    public VersionBehaviour() {
        super();
    }

    /**
     * @see com.ixora.rms.agents.websphere.v50.RelatedVersionBehaviour#acceptPmiDataAsCounter(com.ixora.rms.agents.websphere.v50.WebSphereEntity, com.ibm.websphere.pmi.PmiDataInfo)
     */
    public boolean acceptPmiDataAsCounter(WebSphereEntity we, PmiDataInfo di) {
        int type = di.getType();
        if(type < TYPE_INT || type > TYPE_LOAD) {
            return false;
        }
        String entityName = we.getName();
        if(entityName.equals(PmiConstants.CACHE_MODULE)) {
            String counterName = di.getName();
            if(counterName.indexOf(PmiConstants.TEMPLATE_SUBMODULE) >= 0) {
                return false;
            }
        } else if(entityName.equals(PmiConstants.ORBPERF_MODULE)) {
            String counterName = di.getName();
            if(counterName.indexOf(PmiConstants.INTERCEPTOR_SUBMODULE) >= 0) {
                return false;
            }
        } else if(entityName.equals(PmiConstants.ORBPERF_MODULE)) {
            String counterName = di.getName();
            if(counterName.indexOf(PmiConstants.INTERCEPTOR_SUBMODULE) >= 0) {
                return false;
            }
        } else if(entityName.equals(PmiConstants.WEBSERVICES_MODULE)) {
            String counterName = di.getName();
            if(counterName.indexOf(PmiConstants.WEBSERVICES_SUBMODULE) >= 0) {
                return false;
            }
        } else {
            EntityId parent = we.getId().getParent();
            if(parent != null && parent.getName().equals(PmiConstants.WEBAPP_MODULE)) {
                String counterName = di.getName();
                if(counterName.indexOf(PmiConstants.SERVLET_SUBMODULE) >= 0) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * @see com.ixora.rms.agents.websphere.v50.RelatedVersionBehaviour#getProxyClass()
     */
    public String getProxyClass() {
        return "com.ixora.rms.agents.websphere.v50.proxy.PmiClientProxyImpl";
    }

    /**
     * @see com.ixora.rms.agents.websphere.v50.RelatedVersionBehaviour#acceptPerfDescriptorAsEntity(com.ixora.rms.agents.websphere.v50.proxy.PerfDescriptor)
     */
	public boolean acceptPerfDescriptorAsEntity(PerfDescriptor pd) {
		return true;
	}

	/**
	 * @see com.ixora.rms.agents.websphere.v50.RelatedVersionBehaviour#invokeSetInstrumentationLevel()
	 */
	public boolean invokeSetInstrumentationLevel() {
		return true;
	}
}
