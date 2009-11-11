/*
 * Created on 24-Jun-2005
 */
package com.ixora.rms.agents.websphere.v50;

import com.ibm.websphere.pmi.PmiDataInfo;
import com.ixora.rms.agents.websphere.v50.proxy.PerfDescriptor;

/**
 * RelatedVersionBehaviour
 */
public interface RelatedVersionBehaviour {
    /**
     * @param we
     * @param di
     * @return
     */
    boolean acceptPmiDataAsCounter(WebSphereEntity we, PmiDataInfo di);
    /**
     * @param pd
     * @return
     */
    boolean acceptPerfDescriptorAsEntity(PerfDescriptor pd);
    /**
     * @return the class name of the WAS proxy
     */
    String getProxyClass();

    /**
     * @return true to invoke <code>setInstrumentationLevel()</code> on server
     */
    boolean invokeSetInstrumentationLevel();
}
