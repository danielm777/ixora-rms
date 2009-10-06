/*
 * Created on 12-Oct-2004
 */
package com.ixora.rms.dataengine;

import java.io.Serializable;

import com.ixora.common.xml.XMLExternalizable;
import com.ixora.rms.dataengine.definitions.QueryDef;

/**
 * @author Daniel Moraru
 */
public interface QueryClient extends XMLExternalizable, Serializable {
    /**
     * @return the name of this client
     */
    String getName();
    /**
     * @return the description of this client
     */
    String getDescription();
    /**
     * @return the query used
     */
    QueryDef getQueryDef();
}
