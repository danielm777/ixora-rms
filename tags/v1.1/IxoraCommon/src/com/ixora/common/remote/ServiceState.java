package com.ixora.common.remote;

import com.ixora.common.Enum;

/**
 * Enumeration type for service state.
 * @author: Daniel Moraru
 */
public final class ServiceState extends Enum {
	private static final long serialVersionUID = -6937250873383296254L;
	public static final ServiceState UNKNOWN = new ServiceState(0, "unknown");
    public static final ServiceState OFFLINE = new ServiceState(1, "offline");
    public static final ServiceState ONLINE = new ServiceState(2, "online");
    public static final ServiceState ERROR = new ServiceState(3, "error");

    /**
     * Constructor.
     * @param state int
     * @param name String
     */
    private ServiceState(int state, String name) {
		super(state, name);
    }

	/**
	 * @see com.ixora.common.Enum#readResolve()
	 */
    protected Object readResolve() throws java.io.ObjectStreamException {
        switch(key) {
		case 0:
			return UNKNOWN;

        case 1:
            return OFFLINE;

        case 2:
            return ONLINE;

        case 3:
            return ERROR;
        }

        return null;
    }
}
