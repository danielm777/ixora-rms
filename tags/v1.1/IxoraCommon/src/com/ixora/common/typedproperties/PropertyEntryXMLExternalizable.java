/*
 * Created on 30-Sep-2004
 */
package com.ixora.common.typedproperties;

import java.io.IOException;

import com.ixora.common.logging.AppLogger;
import com.ixora.common.logging.AppLoggerFactory;
import com.ixora.common.typedproperties.exception.InvalidPropertyValue;
import com.ixora.common.typedproperties.exception.PropertyTypeMismatch;
import com.ixora.common.utils.HexConverter;
import com.ixora.common.utils.Utils;
import com.ixora.common.xml.XMLExternalizable;
import com.ixora.common.xml.XMLUtils;
import com.ixora.common.xml.exception.XMLException;


/**
 * PropertyEntry for XMLExternalizable objects.
 */
public class PropertyEntryXMLExternalizable extends PropertyEntry<XMLExternalizable> {
	private static final long serialVersionUID = 6981655747624406419L;
	/** Logger */
    private static final AppLogger logger = AppLoggerFactory.getLogger(PropertyEntryXMLExternalizable.class);

    /**
     * Constructor to support XML.
     */
    PropertyEntryXMLExternalizable() {
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
    PropertyEntryXMLExternalizable(String prop, boolean v,
            XMLExternalizable[] set,
            boolean required,
            String extendedEditorClass) {
        super(prop, v, set, TypedProperties.TYPE_XML_EXTERNALIZABLE, required, extendedEditorClass);
    }

    /**
     * @see com.ixora.common.typedproperties.PropertyEntry#makeObject(java.lang.String)
     */
    protected XMLExternalizable makeObject(String value) throws InvalidPropertyValue {
        try {
        	if(Utils.isEmptyString(value)) {
        		return null;
        	}
            byte[] obj = HexConverter.decode(value);
            String txt = new String(obj, XMLUtils.ENCODING);
            return XMLUtils.fromXMLBuffer(null, new StringBuffer(txt));
        } catch(Exception e) {
            logger.error(e);
        }
        return null;
    }

    /**
     * @see com.ixora.common.typedproperties.PropertyEntry#makeString(java.lang.Object)
     */
    protected String makeString(XMLExternalizable obj) throws PropertyTypeMismatch {
        if(obj == null) {
            return "";
        }
        try {
        	StringBuffer buff = XMLUtils.toXMLBuffer(null, obj, "rms", true);
            return HexConverter.encode(buff.toString().getBytes(XMLUtils.ENCODING));
        } catch (IOException e) {
            logger.error(e);
        } catch (XMLException e) {
            logger.error(e);
		}
        return "";
    }
}