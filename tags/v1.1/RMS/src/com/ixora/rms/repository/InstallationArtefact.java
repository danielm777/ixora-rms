/*
 * Created on 30-Dec-2004
 */
package com.ixora.rms.repository;

import com.ixora.common.xml.XMLExternalizable;

/**
 * @author Daniel Moraru
 */
public interface InstallationArtefact extends XMLExternalizable {
	/**
	 * @return an identifiers that uniquely idendtifies this artefact
	 */
	String getInstallationIdentifier();
}
