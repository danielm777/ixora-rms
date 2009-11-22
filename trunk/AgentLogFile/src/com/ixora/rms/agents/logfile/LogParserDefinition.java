/**
 * 25-Dec-2005
 */
package com.ixora.rms.agents.logfile;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.ixora.common.typedproperties.TypedProperties;
import com.ixora.common.typedproperties.exception.VetoException;
import com.ixora.common.typedproperties.ui.ExtendedEditorMultilineText;
import com.ixora.common.xml.exception.XMLException;
import com.ixora.rms.agents.logfile.messages.Msg;

/**
 * @author Daniel Moraru
 */
public class LogParserDefinition extends TypedProperties {
	private static final long serialVersionUID = 6719702976924693120L;
	public static final String XML_NODE = "logParserDef";
	public static final String LOG_RECORD_BEGIN_REGEX = Msg.LOG_RECORD_BEGIN_REGEX;
	public static final String LOG_RECORD_END_REGEX = Msg.LOG_RECORD_END_REGEX;
	public static final String PARSER_CODE = Msg.LOG_PARSER_CODE;

	/**
	 *
	 */
	public LogParserDefinition() {
		super();
		setProperty(LOG_RECORD_BEGIN_REGEX, TYPE_STRING, true, true);
		setProperty(LOG_RECORD_END_REGEX, TYPE_STRING, true, false);
		setProperty(PARSER_CODE, TYPE_SERIALIZABLE, true, true, null, ExtendedEditorMultilineText.class.getName());
	}

	/**
	 * @see com.ixora.common.typedproperties.TypedProperties#veto()
	 */
	public void veto() throws VetoException {
	}

	/**
	 * @see com.ixora.common.xml.XMLExternalizable#toXML(org.w3c.dom.Node)
	 */
	public void toXML(Node parent) throws XMLException {
		Document doc = parent.getOwnerDocument();
		Element el = doc.createElement(XML_NODE);
		parent.appendChild(el);
		super.toXML(el);
	}
}
