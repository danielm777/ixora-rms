package com.ixora.rms;

import com.ixora.common.Enum;
import com.ixora.common.MessageRepository;
import com.ixora.rms.messages.Msg;

/**
 * Enumeration type for counter types.
 * @author: Daniel Moraru
 */
public final class CounterType extends Enum {
    public static final CounterType DOUBLE =
    	new CounterType(0, MessageRepository.get(Msg.RMS_ENUM_COUNTERTYPE_DOUBLE));
	public static final CounterType LONG =
    	new CounterType(1, MessageRepository.get(Msg.RMS_ENUM_COUNTERTYPE_LONG));
    public static final CounterType STRING =
    	new CounterType(2, MessageRepository.get(Msg.RMS_ENUM_COUNTERTYPE_STRING));
    public static final CounterType OBJECT =
    	new CounterType(3, MessageRepository.get(Msg.RMS_ENUM_COUNTERTYPE_OBJECT));
    public static final CounterType DATE =
    	new CounterType(4, MessageRepository.get(Msg.RMS_ENUM_COUNTERTYPE_DATE));

	/**
	 * Returns the enum element corresponding to the given key.
	 * @param key
	 * @return
	 */
	public static CounterType resolve(int key) {
		switch(key) {
		case 0:
			return DOUBLE;
		case 1:
			return LONG;
		case 2:
			return STRING;
		case 3:
			return OBJECT;
		case 4:
			return DATE;
		}
		return null;
	}

	/**
	 * Constructor.
	 * @param l
	 * @param name
	 */
    private CounterType(int l, String name) {
		super(l, name);
    }

	/**
	 * @see com.ixora.common.Enum#readResolve()
	 */
    protected Object readResolve() throws java.io.ObjectStreamException {
    	return resolve(key);
    }
}
