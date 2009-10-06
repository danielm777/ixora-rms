package com.ixora.common;

/**
 * Enumeration type for Startable states.
 * @author: Daniel Moraru
 */
public final class StartableState extends Enum {
    public static final StartableState NOT_STARTED = new StartableState(0,
                                                                        "not started");
    public static final StartableState STARTED = new StartableState(1,
                                                                    "started");
    public static final StartableState FINISHED = new StartableState(2,
                                                                     "finished");
    public static final StartableState STOPPED = new StartableState(3,
                                                                    "stopped");
    public static final StartableState ERROR = new StartableState(4, "error");

	/**
	 * @param s
	 * @param name
	 */
    private StartableState(int s, String name) {
		super(s, name);
    }

	/**
	 * @see com.ixora.common.Enum#readResolve()
	 */
    protected Object readResolve() throws java.io.ObjectStreamException {
        switch(key) {
        case 0:
            return NOT_STARTED;

        case 1:
            return STARTED;

        case 2:
            return FINISHED;

        case 3:
            return STOPPED;

        case 4:
            return ERROR;
        }
        return null;
    }
}