/*
 * Created on 27-Sep-2004
 */
package com.ixora.jobs;

import com.ixora.common.xml.XMLExternalizable;

/**
 * @author Daniel Moraru
 */
public interface JobData extends XMLExternalizable {
    /**
     * @return whether or not this job must be run on the target host;
     * false if the job can be run locally against the remote host.
     */
    boolean runOnHost();
}
