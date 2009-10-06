/**
 * 30-Jul-2005
 */
package com.ixora.rms.reactions;


import java.io.ObjectStreamException;

import com.ixora.common.Enum;

/**
 * @author Daniel Moraru
 */
public final class ReactionDeliveryType extends Enum {
    public static final ReactionDeliveryType EMAIL = new ReactionDeliveryType(0, "email");
	public static final ReactionDeliveryType JOB = new ReactionDeliveryType(1, "job");
	public static final ReactionDeliveryType ADVICE = new ReactionDeliveryType(2, "advice");

	/**
	 * @param i
	 * @param string
	 */
	private ReactionDeliveryType(int i, String string) {
		super(i, string);
	}

	/**
	 * @see com.ixora.common.Enum#readResolve()
	 */
	protected Object readResolve() throws ObjectStreamException {
        switch(key) {
        case 0:
            return EMAIL;
        case 1:
            return JOB;
        case 2:
            return ADVICE;
        }
		return null;
	}
}
