/*
 * Created on 23-Aug-2004
 */
package com.ixora.rms.ui.tools.providermanager.exception;

import com.ixora.rms.exception.RMSException;
import com.ixora.rms.ui.tools.providermanager.ProviderManagerComponent;
import com.ixora.rms.ui.tools.providermanager.messages.Msg;

/**
 * @author Daniel Moraru
 */
public final class EntityDescriptorsMissing extends RMSException {
	private static final long serialVersionUID = 4590403097003452921L;

	/**
     * Constructor.
     */
    public EntityDescriptorsMissing() {
        super(ProviderManagerComponent.NAME, Msg.ERROR_ENTITY_DESCRIPTORS_MISSING, true);
    }

}
