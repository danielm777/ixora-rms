/*
 * Created on 24-Sep-2004
 */
package com.ixora.jobs.library;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.ixora.jobs.JobData;
import com.ixora.common.xml.XMLExternalizable;
import com.ixora.common.xml.XMLUtils;
import com.ixora.common.xml.exception.XMLException;
import com.ixora.common.xml.exception.XMLNodeMissing;

/**
 * @author Daniel Moraru
 */
public final class JobLibraryDefinition implements XMLExternalizable {
	private static final long serialVersionUID = 3463494824633280880L;
	/** Host where the command is to be executed */
    private String fHost;
    /** Job name */
    private String fName;
    /** Job data */
    private JobData fJobData;

    /**
     * Default constructor to support xml.
     */
    public JobLibraryDefinition() {
    }

    /**
     * Constructor.
     * @param name
     * @param host
     * @param data
     */
    public JobLibraryDefinition(
            String name,
            String host,
            JobData data) {
        super();
        if(name == null || host == null
                || data == null) {
            throw new IllegalArgumentException("null parameters");
        }
        this.fName = name;
        this.fHost = host;
        this.fJobData = data;
    }
    /**
     * @return the job data
     */
    public JobData getJobData() {
        return fJobData;
    }
    /**
     * @return the job's name
     */
    public String getName() {
        return fName;
    }
    /**
     * @return the host.
     */
    public String getHost() {
        return fHost;
    }
	/**
	 * @see com.ixora.common.xml.XMLExternalizable#toXML(org.w3c.dom.Node)
	 */
	public void toXML(Node parent) throws XMLException {
		Document doc = parent.getOwnerDocument();
		Element el = doc.createElement("job");
		parent.appendChild(el);
		Element el2 = doc.createElement("name");
		el.appendChild(el2);
		el2.appendChild(doc.createTextNode(this.fName));
		el2 = doc.createElement("host");
		el.appendChild(el2);
		el2.appendChild(doc.createTextNode(this.fHost));
		XMLUtils.writeObject(null, el, fJobData);
	}
	/**
	 * @see com.ixora.common.xml.XMLExternalizable#fromXML(org.w3c.dom.Node)
	 */
	public void fromXML(Node node) throws XMLException {
		Node n = XMLUtils.findChild(node, "name");
		if(n == null) {
			throw new XMLNodeMissing("name");
		}
		this.fName = XMLUtils.getText(n);
		n = XMLUtils.findChild(node, "host");
		if(n == null) {
			throw new XMLNodeMissing("host");
		}
		this.fHost = XMLUtils.getText(n);
		n = XMLUtils.findChild(node, "jobData");
		if(n == null) {
			throw new XMLNodeMissing("jobData");
		}
		try {
			fJobData = (JobData)XMLUtils.readObject(null, n);
		} catch(XMLException e) {
			throw e;
		} catch(Exception e) {
			throw new XMLException(e);
		}
	}
}
