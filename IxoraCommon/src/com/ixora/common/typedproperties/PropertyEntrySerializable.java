/*
 * Created on 30-Sep-2004
 */
package com.ixora.common.typedproperties;

import java.io.IOException;
import java.io.Serializable;

import com.ixora.common.logging.AppLogger;
import com.ixora.common.logging.AppLoggerFactory;
import com.ixora.common.typedproperties.exception.InvalidPropertyValue;
import com.ixora.common.typedproperties.exception.PropertyTypeMismatch;
import com.ixora.common.utils.HexConverter;
import com.ixora.common.utils.Utils;


/**
 * PropertyEntry for serializable objects.
 */
public class PropertyEntrySerializable extends PropertyEntry {
    /** Logger */
    private static final AppLogger logger = AppLoggerFactory.getLogger(PropertyEntrySerializable.class);

    /**
     * Constructor to support XML.
     */
    PropertyEntrySerializable() {
        this(null, false, null, false, null);
    }

    /**
     * Constructor.
     * @param prop
     * @param v
     * @param set
     * @param required
     * @param extendedEditorClass
     */
    PropertyEntrySerializable(String prop, boolean v,
            Serializable[] set,
            boolean required,
            String extendedEditorClass) {
        super(prop, v, set, TypedProperties.TYPE_SERIALIZABLE, required, extendedEditorClass);
    }

    /**
     * @see com.ixora.common.typedproperties.PropertyEntry#makeObject(java.lang.String)
     */
    protected Object makeObject(String value) throws InvalidPropertyValue {
        try {
        	if(Utils.isEmptyString(value)) {
        		return null;
        	}
            byte[] obj = HexConverter.decode(value);
            return Utils.unpackObject(obj);
        } catch(Exception e) {
            logger.error(e);
        }
        return null;
    }

    /**
     * @see com.ixora.common.typedproperties.PropertyEntry#makeString(java.lang.Object)
     */
    protected String makeString(Object obj) throws PropertyTypeMismatch {
        if(obj == null) {
            return "";
        }
        if(!(obj instanceof Serializable)) {
            throw new PropertyTypeMismatch(property);
        }
        byte[] pack;
        try {
            pack = Utils.packObject((Serializable)obj);
            return HexConverter.encode(pack);
        } catch (IOException e) {
            logger.error(e);
        }
        return "";
    }

    /**
     * @see com.ixora.common.typedproperties.PropertyEntry#checkObjectType(java.lang.Object)
     */
    protected boolean checkObjectType(Object obj) {
        return obj == null || obj instanceof Serializable;
    }
}