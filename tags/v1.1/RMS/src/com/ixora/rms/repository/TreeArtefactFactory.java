/*
 * Created on 07-Aug-2004
 */
package com.ixora.rms.repository;

import com.ixora.common.xml.XMLExternalizable;

/**
 * Artefact factory.
 * @author Daniel Moraru
 */
public interface TreeArtefactFactory {
    /**
     * @return a newly created artefact
     */
    XMLExternalizable createArtefact();
}
