/**
 * 29-Jan-2006
 */
package com.ixora.rms.agents.impl.logfile;

import java.io.UnsupportedEncodingException;
import java.util.Date;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.ixora.common.utils.HexConverter;
import com.ixora.common.xml.XMLExternalizable;
import com.ixora.common.xml.XMLUtils;
import com.ixora.common.xml.exception.XMLAttributeMissing;
import com.ixora.common.xml.exception.XMLException;

/**
 * Holds basic details about a log record.
 * @author Daniel Moraru
 */
public final class LogRecord implements XMLExternalizable {
	private static final long serialVersionUID = 6577826842167422770L;
	public static final String XML_RECORD = "r";
	private static final String XML_TIMESTAMP = "t";
	private static final String XML_SEQUENCE_NUMBER = "sn";
	private static final String XML_SEVERITY = "s";
	private static final String XML_SOURCE_COMPONENT = "c";
	private static final String XML_SOURCE_CLASS = "cs";
	private static final String XML_SOURCE_METHOD = "m";
	private static final String XML_SOURCE_LINE_NUMBER = "n";
	private static final String XML_THREAD = "th";
	private static final String XML_MESSAGE = "mg";

	private long fTimestamp;
	private long fSequenceNumber;
	private String fSeverity;
	private String fSourceComponent;
	private String fSourceClass;
	private String fSourceMethod;
	private String fSourceLineNumber;
	private String fThread;
	private String fMessage;

	/**
	 * XML support.
	 */
	public LogRecord() {
		super();
	}

	/**
	 * @param timestamp
	 * @param severity
	 * @param comp
	 * @param clazz
	 * @param method
	 * @param line
	 * @param thread
	 * @param seq
	 * @param message
	 */
	public LogRecord(
			long timestamp,
			String severity,
			String comp,
			String clazz,
			String method,
			String line,
			String thread,
			long seq,
			String message) {
		super();
		if(timestamp == 0) {
			throw new IllegalArgumentException("Invalid timestamp: " + timestamp);
		}
		if(message == null) {
			throw new IllegalArgumentException("Log message is null");
		}
		this.fTimestamp = timestamp;
		this.fSeverity = severity;
		this.fSourceComponent = comp;
		this.fSourceClass = clazz;
		this.fSourceMethod = method;
		this.fSourceLineNumber = line;
		this.fThread = thread;
		this.fMessage = message;
	}

	/**
	 * @return
	 */
	public String getMessage() {
		return fMessage;
	}

	/**
	 * @return
	 */
	public long getSequenceNumber() {
		return fSequenceNumber;
	}

	/**
	 * @return
	 */
	public String getSourceClass() {
		return fSourceClass;
	}

	/**
	 * @return
	 */
	public String getSourceComponent() {
		return fSourceComponent;
	}

	/**
	 * @return
	 */
	public String getSourceMethod() {
		return fSourceMethod;
	}

	/**
	 * @return
	 */
	public String getThread() {
		return fThread;
	}

	/**
	 * @return
	 */
	public long getTimestamp() {
		return fTimestamp;
	}

	/**
	 * @return
	 */
	public String getSourceLineNumber() {
		return fSourceLineNumber;
	}

	/**
	 * @return
	 */
	public String getSeverity() {
		return fSeverity;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "Timestamp: " + new Date(fTimestamp) + "\n"
			+ "Seq number: " + fSequenceNumber + "\n"
			+ "Severity: " + fSeverity + "\n"
			+ "Class: " + fSourceClass + "\n"
			+ "Component: " + fSourceComponent + "\n"
			+ "Line number: " + fSourceLineNumber + "\n"
			+ "Method: " + fSourceMethod + "\n"
			+ "Thread: " + fThread + "\n"
			+ "Message: " + fMessage;

	}

	/**
	 * Remember: the output must be as small as possible as it will
	 * be stored in session recordings.
	 * @see com.ixora.common.xml.XMLExternalizable#toXML(org.w3c.dom.Node)
	 */
	public void toXML(Node parent) throws XMLException {
		Document doc = parent.getOwnerDocument();
		Element el = doc.createElement(XML_RECORD);
		parent.appendChild(el);
		el.setAttribute(XML_TIMESTAMP, String.valueOf(fTimestamp));
		el.setAttribute(XML_SEQUENCE_NUMBER, String.valueOf(fSequenceNumber));
		if(fSeverity != null) {
			el.setAttribute(XML_SEVERITY, fSeverity);
		}
		if(fSourceClass != null) {
			el.setAttribute(XML_SOURCE_CLASS, fSourceClass);
		}
		if(fSourceComponent != null) {
			el.setAttribute(XML_SOURCE_COMPONENT, fSourceComponent);
		}
		if(fSourceMethod != null) {
			el.setAttribute(XML_SOURCE_METHOD, fSourceMethod);
		}
		if(fSourceLineNumber != null) {
			el.setAttribute(XML_SOURCE_LINE_NUMBER, fSourceLineNumber);
		}
		if(fThread != null) {
			el.setAttribute(XML_THREAD, fThread);
		}
		try {
			el.setAttribute(XML_MESSAGE, HexConverter.encode(fMessage.getBytes(XMLUtils.ENCODING)));
		} catch (UnsupportedEncodingException e) {
			throw new XMLException(e);
		}
	}

	/**
	 * @see com.ixora.common.xml.XMLExternalizable#fromXML(org.w3c.dom.Node)
	 */
	public void fromXML(Node node) throws XMLException {
		Attr a = XMLUtils.findAttribute(node, XML_TIMESTAMP);
		if(a == null) {
			throw new XMLAttributeMissing(XML_TIMESTAMP);
		}
		fTimestamp = Long.parseLong(a.getValue());

		a = XMLUtils.findAttribute(node, XML_MESSAGE);
		if(a == null) {
			throw new XMLAttributeMissing(XML_MESSAGE);
		}
		try {
			fMessage = new String(HexConverter.decode(a.getValue()), XMLUtils.ENCODING);
		} catch (UnsupportedEncodingException e) {
			throw new XMLException(e);
		}

		a = XMLUtils.findAttribute(node, XML_THREAD);
		if(a != null) {
			fThread = a.getValue();
		}

		a = XMLUtils.findAttribute(node, XML_SOURCE_METHOD);
		if(a != null) {
			fSourceMethod = a.getValue();
		}

		a = XMLUtils.findAttribute(node, XML_SOURCE_LINE_NUMBER);
		if(a != null) {
			fSourceLineNumber = a.getValue();
		}

		a = XMLUtils.findAttribute(node, XML_SOURCE_COMPONENT);
		if(a != null) {
			fSourceComponent = a.getValue();
		}

		a = XMLUtils.findAttribute(node, XML_SOURCE_CLASS);
		if(a != null) {
			fSourceClass = a.getValue();
		}

		a = XMLUtils.findAttribute(node, XML_SEVERITY);
		if(a != null) {
			fSeverity = a.getValue();
		}

		a = XMLUtils.findAttribute(node, XML_SEQUENCE_NUMBER);
		if(a != null) {
			fSequenceNumber = Long.parseLong(a.getValue());
		}
	}
}
