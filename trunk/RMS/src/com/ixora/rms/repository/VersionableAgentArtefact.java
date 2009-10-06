/*
 * Created on 26-Jun-2005
 */
package com.ixora.rms.repository;

import java.util.Collection;
import java.util.Set;

/**
 * VersionableAgentArtefactImpl
 */
public interface VersionableAgentArtefact {
    /**
     * @return the name of this artefact; it must be unique accross an agent version
     */
    public String getArtefactName();
    /**
     * @return the agentVersions.
     */
    public Set<String> getAgentVersions();
    /**
     * @param agentVersion
     * @return true if this artefact belongs to the the given agent version
     */
    public boolean appliesToAgentVersion(String agentVersion);
    /**
     * @param versions
     */
    public void addAgentVersions(Collection<String> versions);
    /**
     * @param versions
     */
    public void removeAgentVersions(Collection<String> versions);
    /**
     * @param versions
     */
    public void setAgentVersions(Collection<String> versions);
}