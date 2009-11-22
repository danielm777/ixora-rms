/**
 * 25-Dec-2005
 */
package com.ixora.rms.agents.logfile;

import java.util.LinkedHashMap;

import com.ixora.common.typedproperties.PropertyEntry;
import com.ixora.common.typedproperties.TypedProperties;
import com.ixora.common.typedproperties.exception.InvalidPropertyValue;
import com.ixora.common.typedproperties.exception.VetoException;
import com.ixora.rms.agents.AgentCustomConfiguration;
import com.ixora.rms.agents.logfile.definitions.StoredLogParserDef;
import com.ixora.rms.agents.logfile.messages.Msg;

/**
 * @author Daniel Moraru
 */
public class Configuration extends AgentCustomConfiguration {
	private static final long serialVersionUID = -7577941759062480634L;
	public static final String LOG_FILE_PATH = Msg.LOG_FILE_PATH;
	public static final String FILE_ENCODING = Msg.FILE_ENCODING;
	public static final String AVAILABLE_LOG_PARSERS = Msg.AVAILABLE_LOG_PARSERS;
	public static final String CURRENT_LOG_PARSER = Msg.CURRENT_LOG_PARSER; // this property is not visible

	/**
	 * XML support.
	 */
	public Configuration() {
		super();
	}

	/**
	 * @param parsers list of available parsers, must have at least one element
	 */
	public Configuration(LinkedHashMap<String,StoredLogParserDef> parsers) {
		super();
		setProperty(LOG_FILE_PATH, TYPE_STRING, true, true);
		setProperty(FILE_ENCODING, TYPE_STRING, true, true);
		setProperty(AVAILABLE_LOG_PARSERS, TYPE_STRING, true, true,
				parsers.keySet().toArray(new String[parsers.size()]));
		setProperty(CURRENT_LOG_PARSER, TYPE_XML_EXTERNALIZABLE, false, true);

		setString(FILE_ENCODING, "UTF-8");

		setString(AVAILABLE_LOG_PARSERS, parsers.keySet().iterator().next());

		StoredLogParserDef def = parsers.values().iterator().next();
		if(def != null) {
			setObject(CURRENT_LOG_PARSER, def.getLogParserDefinition());
		}
	}

	/**
	 * Overridden to update the list of available parsers before applying the given property.
	 * @see com.ixora.common.typedproperties.TypedProperties#apply(com.ixora.common.typedproperties.TypedProperties)
	 */
	@SuppressWarnings("unchecked")
	public void apply(TypedProperties other) throws InvalidPropertyValue {
		// the list of available parsers might have changed...
		PropertyEntry pe = other.getEntry(AVAILABLE_LOG_PARSERS);
		Object[] newSet = pe.getValueSet();
		pe = getEntry(AVAILABLE_LOG_PARSERS);
		pe.setValueSet(newSet);
		// now we can apply properties as usual
		super.apply(other);
	}

	/**
	 * @see com.ixora.common.typedproperties.TypedProperties#veto()
	 */
	public void veto() throws VetoException {
		if(getObject(CURRENT_LOG_PARSER) == null) {
			throw new VetoException("Log parser information is missing.");
		}
	}
}
