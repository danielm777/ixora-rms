package com.ixora.rms;

import com.ixora.common.Enum;
import com.ixora.common.MessageRepository;
import com.ixora.rms.messages.Msg;

/**
 * Enumeration type for the monitoring levels.
 * @author: Daniel Moraru
 */
public final class MonitoringLevel extends Enum {
	private static final long serialVersionUID = 7513914386073152674L;
	public static final MonitoringLevel NONE =
    	new MonitoringLevel(-1, MessageRepository.get(Msg.RMS_ENUM_MONITORINGLEVEL_NONE));
	public static final MonitoringLevel LOW =
    	new MonitoringLevel(0, MessageRepository.get(Msg.RMS_ENUM_MONITORINGLEVEL_LOW));
    public static final MonitoringLevel MEDIUM =
    	new MonitoringLevel(1, MessageRepository.get(Msg.RMS_ENUM_MONITORINGLEVEL_MEDIUM));
    public static final MonitoringLevel HIGH =
    	new MonitoringLevel(2, MessageRepository.get(Msg.RMS_ENUM_MONITORINGLEVEL_HIGH));
    public static final MonitoringLevel MAXIMUM =
		new MonitoringLevel(3, MessageRepository.get(Msg.RMS_ENUM_MONITORINGLEVEL_MAXIMUM));

	/**
	 * Returns the enum element corresponding to the given key.
	 * @param key
	 * @return
	 */
	public static MonitoringLevel resolve(int key) {
		switch(key) {
		case -1:
			return NONE;
		case 0:
			return LOW;
		case 1:
			return MEDIUM;
		case 2:
			return HIGH;
		case 3:
			return MAXIMUM;
		}
		return null;
	}

	/**
	 * Constructor.
	 * @param l
	 * @param name
	 */
    private MonitoringLevel(int l, String name) {
		super(l, name);
    }

	/**
	 * @see com.ixora.common.Enum#readResolve()
	 */
    protected Object readResolve() throws java.io.ObjectStreamException {
    	return resolve(key);
    }
}
