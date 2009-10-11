package com.ixora.rms.providers;

import com.ixora.common.Enum;
import com.ixora.common.MessageRepository;
import com.ixora.rms.providers.messages.Msg;

/**
 * Enumeration type for the monitoring agent locations.
 * @author: Daniel Moraru
 */
public final class ProviderLocation extends Enum {
	private static final long serialVersionUID = 5438110468792465545L;
	public static final ProviderLocation LOCAL =
    	new ProviderLocation(0, MessageRepository.get(
    			ProvidersComponent.NAME, Msg.RMS_ENUM_PROVIDERLOCATION_LOCAL));
    public static final ProviderLocation REMOTE =
    	new ProviderLocation(1, MessageRepository.get(
    			ProvidersComponent.NAME, Msg.RMS_ENUM_PROVIDERLOCATION_REMOTE));

	/**
	 * Returns the enum element corresponding to the given key.
	 * @param key
	 * @return
	 */
	public static ProviderLocation resolve(int key) {
		switch(key) {
		case 0:
			return LOCAL;
		case 1:
			return REMOTE;
		}
		return null;
	}

	/**
	 * Constructor.
	 * @param l
	 * @param name
	 */
    private ProviderLocation(int l, String name) {
		super(l, name);
    }

	/**
	 * @see com.ixora.common.Enum#readResolve()
	 */
    protected Object readResolve() throws java.io.ObjectStreamException {
		return resolve(key);
    }
}
