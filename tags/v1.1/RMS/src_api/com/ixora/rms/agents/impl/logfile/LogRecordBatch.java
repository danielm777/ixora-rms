/**
 * 13-Feb-2006
 */
package com.ixora.rms.agents.impl.logfile;

import java.util.LinkedList;
import java.util.List;

import org.w3c.dom.Node;

import com.ixora.common.utils.Utils;
import com.ixora.common.xml.XMLExternalizable;
import com.ixora.common.xml.XMLUtils;
import com.ixora.common.xml.exception.XMLException;
import com.ixora.rms.data.ValueObject;

/**
 * This class holds a list of log records and it's used as the object
 * in the CounterValueObject counter.
 * @author Daniel Moraru
 */
public class LogRecordBatch implements ValueObject {
	private static final long serialVersionUID = -8194457418276606804L;
	public static final String XML_RECORD_BATCH = "rb";
	private List<LogRecord> fRecords;

	/**
	 * XML support.
	 */
	public LogRecordBatch() {
		super();
		fRecords = new LinkedList<LogRecord>();
	}

	/**
	 * @param records
	 */
	public LogRecordBatch(List<LogRecord> records) {
		super();
		fRecords = records;
	}

	/**
	 * @return
	 */
	public List<LogRecord> getLogRecords() {
		return fRecords;
	}

	/**
	 * @see com.ixora.rms.data.ValueObject#aggregate(com.ixora.rms.data.ValueObject)
	 */
	public void aggregate(ValueObject obj) {
		if(obj instanceof LogRecordBatch) {
			fRecords.addAll(((LogRecordBatch)obj).fRecords);
		}
	}

	/**
	 * @see com.ixora.common.xml.XMLExternalizable#toXML(org.w3c.dom.Node)
	 */
	public void toXML(Node parent) throws XMLException {
		Node el = parent.getOwnerDocument().createElement(XML_RECORD_BATCH);
		parent.appendChild(el);
		XMLUtils.writeObjects(LogRecord.class, el, fRecords.toArray(new LogRecord[fRecords.size()]));
	}

	/**
	 * @see com.ixora.common.xml.XMLExternalizable#fromXML(org.w3c.dom.Node)
	 */
	public void fromXML(Node node) throws XMLException {
		try {
			XMLExternalizable[] objs = XMLUtils.readObjects(LogRecord.class, node, LogRecord.XML_RECORD);
			if(!Utils.isEmptyArray(objs)) {
				for(XMLExternalizable ext : objs) {
					fRecords.add((LogRecord)ext);
				}
			}
		} catch(XMLException e) {
			throw e;
		} catch(Exception e) {
			throw new XMLException(e);
		}
	}
}
