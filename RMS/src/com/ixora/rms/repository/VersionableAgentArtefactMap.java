/*
 * Created on 26-Jun-2005
 */
package com.ixora.rms.repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ixora.common.collections.CaseInsensitiveLinkedMap;
import com.ixora.common.utils.Utils;
import com.ixora.common.xml.XMLExternalizable;
import com.ixora.rms.repository.exception.ArtefactSaveConflict;

/**
 * VersionableAgentArtefactMap
 */
public abstract class VersionableAgentArtefactMap<T extends VersionableAgentArtefact> implements XMLExternalizable {
    private static final String ALL = "all";
    /** Map of artefacts keyed by SUO version and then by name */
    private CaseInsensitiveLinkedMap<Map<String, T>> fArtefacts;

    protected VersionableAgentArtefactMap() {
        super();
        this.fArtefacts = new CaseInsensitiveLinkedMap<Map<String, T>>();
    }

    /**
     * @return never null (could be empty)
     */
    public Collection<T> getAll() {
        return getForAgentVersion(null);
    }

    /**
     * @param version if null it returns all artefacts for all versions
     * @return a non null set (could be empty)
     */
    public Collection<T> getForAgentVersion(String version) {
        List<T> ret = new LinkedList<T>();
        if(version == null) {
            // return all artefacts for all versions
            Collection<Map<String, T>> all = fArtefacts.values();
            for(Map<String, T> forVersion : all) {
                for(T artefact : forVersion.values()) {
                    if(!ret.contains(artefact)) {
                        ret.add(artefact);
                    }
                }
            }
            return ret;
        }
        Map<String, T> forVersion = fArtefacts.get(version);
        Map<String, T> forVersionAll = fArtefacts.get(ALL);
        if(forVersion != null) {
            ret.addAll(forVersion.values());
        }
        if(forVersionAll != null) {
            ret.addAll(forVersionAll.values());
        }
        return ret;
    }

    /**
     * @param version if null it returns all artefacts that apply to all versions
     * @return
     */
    public T getForAgentVersion(String artefact, String version) {
        if(version == null) {
            version = ALL;
        }
        Map<String, T> forVersion = fArtefacts.get(version);
        if(forVersion == null) {
            return null;
        }
        return forVersion.get(artefact);
    }

    /**
     * @param artefact
     * @return true if an artefact already exists with the same name and
     * for the same versions as one of the existing artefacts
     */
    public boolean exists(T artefact) {
        Set<String> versions = artefact.getAgentVersions();
        String artefactName = artefact.getArtefactName();
        boolean global = false;
        if(Utils.isEmptyCollection(versions)) {
            global = true;
            versions = new HashSet<String>();
            versions.add(ALL);
        }
        if(global) {
            // search in all existing versions for the same name
            Collection<T> all = getForAgentVersion(null);
            for(T art : all) {
                if(art.getArtefactName().equalsIgnoreCase(artefactName)) {
                    return true;
                }
            }
        } else {
            // search first for a name like this in the global section
            Collection<T> all = getForAgentVersion(ALL);
            if(all != null) {
                for(T art : all) {
                    if(art.getArtefactName().equalsIgnoreCase(artefactName)) {
                        return true;
                    }
                }
            }
            for(String version : versions) {
                Map<String, T> forVersion = this.fArtefacts.get(version);
                if(forVersion != null) {
                    T art = forVersion.get(artefactName);
                    if(art != null) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * @param artefact
     */
    public void add(T artefact) {
        String artefactName = artefact.getArtefactName();
        Set<String> versions = artefact.getAgentVersions();
        if(Utils.isEmptyCollection(versions)) {
            versions = new HashSet<String>();
            versions.add(ALL);
        }
        for(String version : versions) {
            Map<String, T> forVersion = fArtefacts.get(version);
            if(forVersion == null) {
                forVersion = new LinkedHashMap<String, T>();
                fArtefacts.put(version, forVersion);
            }
            forVersion.put(artefactName, artefact);
        }
    }

    /**
     * @param artefactOld can be null
     * @param artefactNew new artefact
     * @throws ArtefactSaveConflict
     */
    public void addOrUpdateWithConflictDetection(T artefactOld, T artefactNew)
                    throws ArtefactSaveConflict {
        if(artefactNew == null) {
            throw new IllegalArgumentException("artefactNew is null");
        }

        if(artefactOld != null) {
            // check for overwrite if the name is different
            if(!artefactOld.getArtefactName().equalsIgnoreCase(artefactNew.getArtefactName())) {
                if(exists(artefactNew)) {
                    throw new ArtefactSaveConflict();
                }
            } else {
                // remove all providers with this name from all versions
                // so it will be freshly added again
                Set<String> versions = artefactOld.getAgentVersions();
                if(!Utils.isEmptyCollection(versions)) {
                    for(String version : versions) {
                        remove(artefactNew.getArtefactName(), version);
                    }
                } else {
                    // remove from the global path
                    remove(artefactNew.getArtefactName(), null);
                }
            }
        } else {
            // new provider was edited
            if(exists(artefactNew)) {
                throw new ArtefactSaveConflict();
            }
        }
        add(artefactNew);
    }

    /**
     * @param artefact
     */
    public void remove(T artefact) {
        Set<String> agentVersions = artefact.getAgentVersions();
        if(!Utils.isEmptyCollection(agentVersions)) {
            for(String version : agentVersions) {
                remove(artefact.getArtefactName(), version);
            }
        } else {
            remove(artefact.getArtefactName(), null);
        }
    }

    /**
     * @param artefact
     * @param agentVersion if null then the artefact with the given name that
     * applies to all versions will be removed
     */
    public void remove(String artefact, String agentVersion) {
        if(agentVersion == null) {
            agentVersion = ALL;
        }
        Map<String, T> forVersion = fArtefacts.get(agentVersion);
        if(forVersion == null) {
            // remove from ALL; anybody can remove global artefacts
            forVersion = fArtefacts.get(ALL);
        }
        if(forVersion != null) {
            T removed = forVersion.remove(artefact);
            if(removed != null) {
                List<String> removedVersions = new ArrayList(1);
                removedVersions.add(agentVersion);
                removed.removeAgentVersions(removedVersions);
            }
        }
    }

    /**
     * @param artefact
     * @param agentVersion if null then the artefact with the given name that
     * applies to all versions will be removed
     */
    public void removeForAllAgentVersions(String artefact) {
        Collection<Map<String, T>> forAllVersion = fArtefacts.values();
        for(Map<String, T> forVersion : forAllVersion) {
            forVersion.remove(artefact);
        }
    }

}
