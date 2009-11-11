/**
 * 17-Jul-2005
 */
package com.ixora.rms.repository;

import com.ixora.common.ConfigurationMgr;
import com.ixora.common.utils.Utils;

/**
 * @author Daniel Moraru
 */
public final class AuthoredArtefactHelper {
	/**
	 * Checks the authoring info of the given artefact.
	 * @param aa
	 */
	public static final void checkArtefact(AuthoredArtefact aa) {
    	if(ConfigurationMgr.isDeveloperMode()) {
    		aa.setAuthor(AuthoredArtefact.SYSTEM);
    	} else if(Utils.isEmptyString(aa.getAuthor())) {
    		aa.setAuthor(AuthoredArtefact.CUSTOMER);
    	}
	}
}
