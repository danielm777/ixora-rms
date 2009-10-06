/**
 * 05-Mar-2006
 */
package com.ixora.common.typedproperties;

import com.ixora.common.security.SecurityMgr;
import com.ixora.common.typedproperties.exception.InvalidPropertyValue;
import com.ixora.common.typedproperties.exception.PropertyTypeMismatch;
import com.ixora.common.utils.Utils;

/**
 * @author Daniel Moraru
 */
public class PropertyEntrySecureString extends PropertyEntryString {

	/**
	 *
	 */
	public PropertyEntrySecureString() {
		super();
		type = TypedProperties.TYPE_SECURE_STRING;
	}


	/**
	 * @param prop
	 * @param v
	 * @param set
	 * @param type
	 * @param required
	 */
	public PropertyEntrySecureString(String prop, boolean v, Object[] set, int type, boolean required) {
		this(prop, v, required);
	}


	/**
	 * @param prop
	 * @param v
	 * @param set
	 * @param required
	 */
	public PropertyEntrySecureString(String prop, boolean v, String[] set, boolean required) {
		this(prop, v, required);
	}


	/**
	 * @param prop
	 * @param v
	 * @param set
	 * @param required
	 */
	public PropertyEntrySecureString(String prop, boolean v, boolean required) {
		super(prop, v, null, TypedProperties.TYPE_SECURE_STRING, required);
	}

	/**
	 * Overriden to perform decryption.
	 * @see com.ixora.common.typedproperties.PropertyEntry#makeObject(java.lang.String)
	 */
	protected Object makeObject(String value) throws InvalidPropertyValue {
		String cipherText = (String)super.makeObject(value);
		if(Utils.isEmptyString(cipherText)) {
			return cipherText;
		}
		return SecurityMgr.decrypt(cipherText);
	}

	/**
	 * Overriden to perform encryption.
	 * @see com.ixora.common.typedproperties.PropertyEntry#makeString(java.lang.Object)
	 */
	protected String makeString(Object obj) throws PropertyTypeMismatch {
		String clearText = super.makeString(obj);
		return SecurityMgr.encrypt(clearText);
	}
}
