/*
 * Created on 25-Sep-2004
 */
package com.ixora.jobs.impl.command;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.ixora.jobs.JobData;
import com.ixora.common.xml.XMLUtils;
import com.ixora.common.xml.exception.XMLException;
import com.ixora.common.xml.exception.XMLNodeMissing;


/**
 * @author Daniel Moraru
 */
public class ExternalCommandJobData implements JobData {
    /** Command job */
    private String command;

    /**
     * Constructor to support xml.
     */
    public ExternalCommandJobData() {
        super();
    }

    /**
     * Constructor.
     * @param command
     */
    public ExternalCommandJobData(String command) {
        super();
        this.command = command;
    }

    /**
     * @return the command.
     */
    public String getCommand() {
        return command;
    }

	/**
	 * @see com.ixora.common.xml.XMLExternalizable#toXML(org.w3c.dom.Node)
	 */
	public void toXML(Node parent) throws XMLException {
		Document doc = parent.getOwnerDocument();
		Element el = doc.createElement("jobData");
		parent.appendChild(el);
		Element el2 = doc.createElement("command");
		el.appendChild(el2);
		el2.appendChild(doc.createTextNode(command));
	}

	/**
	 * @see com.ixora.common.xml.XMLExternalizable#fromXML(org.w3c.dom.Node)
	 */
	public void fromXML(Node node) throws XMLException {
		Node n = XMLUtils.findChild(node, "command");
		if(n == null) {
			throw new XMLNodeMissing("command");
		}
		this.command = XMLUtils.getText(n);
	}

	/**
	 * @see com.ixora.jobs.JobData#runOnHost()
	 */
	public boolean runOnHost() {
		return true;
	}
}
