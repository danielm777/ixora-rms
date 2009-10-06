package com.ixora.rms.providers;

import com.ixora.common.Enum;
import com.ixora.common.MessageRepository;
import com.ixora.rms.providers.messages.Msg;

/**
 * Enumeration type for the provider's state.
 * @author: Daniel Moraru
 */
public final class ProviderState extends Enum {
    public static final ProviderState READY =
    	new ProviderState(0, MessageRepository.get(ProvidersComponent.NAME, Msg.RMS_ENUM_PROVIDERSTATE_READY));
    public static final ProviderState STARTED =
    	new ProviderState(1, MessageRepository.get(ProvidersComponent.NAME, Msg.RMS_ENUM_PROVIDERSTATE_STARTED));
    public static final ProviderState FINISHED =
    	new ProviderState(2, MessageRepository.get(ProvidersComponent.NAME, Msg.RMS_ENUM_PROVIDERSTATE_FINISHED));
    public static final ProviderState STOPPED =
    	new ProviderState(3, MessageRepository.get(ProvidersComponent.NAME, Msg.RMS_ENUM_PROVIDERSTATE_STOPPED));
    public static final ProviderState ERROR =
    	new ProviderState(4, MessageRepository.get(ProvidersComponent.NAME, Msg.RMS_ENUM_PROVIDERSTATE_ERROR));
	public static final ProviderState UNKNOWN =
		new ProviderState(5, MessageRepository.get(ProvidersComponent.NAME, Msg.RMS_ENUM_PROVIDERSTATE_UNKNOWN));
	public static final ProviderState UNINSTALLED =
		new ProviderState(6, MessageRepository.get(ProvidersComponent.NAME, Msg.RMS_ENUM_PROVIDERSTATE_UNINSTALLED));

	/**
	 * Constructor.
	 * @param s
	 * @param name
	 */
    private ProviderState(int s, String name) {
		super(s, name);
    }

	/**
	 * @see com.ixora.common.Enum#readResolve()
	 */
    protected Object readResolve() throws java.io.ObjectStreamException {
        switch(key) {
        case 0:
            return READY;

        case 1:
            return STARTED;

        case 2:
            return FINISHED;

        case 3:
            return STOPPED;

        case 4:
            return ERROR;

        case 5:
        	return UNKNOWN;

        case 6:
        	return UNINSTALLED;
        }
        return null;
    }
}
