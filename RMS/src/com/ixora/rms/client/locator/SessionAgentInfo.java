/*
 * Created on 05-Jun-2005
 */
package com.ixora.rms.client.locator;

import com.ixora.rms.client.model.ResourceInfo;

/**
 * SessionAgentInfoImpl
 * @author Daniel Moraru
 */
public final class SessionAgentInfo extends AbstractSessionArtefactInfo {

    /**
     * @param ri
     */
    public SessionAgentInfo(ResourceInfo ri) {
        this.fTranslatedName = ri.getAgentInfo().getTranslatedName();
        this.fTranslatedDescription = ri.getAgentInfo().getTranslatedDescription();
    }
}
