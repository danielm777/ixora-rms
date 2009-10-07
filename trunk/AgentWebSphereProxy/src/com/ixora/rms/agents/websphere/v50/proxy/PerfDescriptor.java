/*
 * Created on 22-Dec-2004
 */
package com.ixora.rms.agents.websphere.v50.proxy;

import java.io.Serializable;

import com.ibm.ws.pmi.server.DataDescriptor;

/**
 * @author Daniel Moraru
 */
public class PerfDescriptor implements com.ibm.websphere.pmi.client.PerfDescriptor, Serializable, Cloneable {
	/** Underlying was descriptor */
	private com.ibm.websphere.pmi.client.PerfDescriptor wasDescriptor;
// fields that seem to be transient in the was implementation
	private String name;
	private String moduleName;
	private PerfDescriptor[] children;

	/**
	 * Constructor.
	 * @param pd
	 */
	public PerfDescriptor(com.ibm.websphere.pmi.client.PerfDescriptor pd) {
		super();
		wasDescriptor = pd;
		name = pd.getName();
		moduleName = pd.getModuleName();
	}


	/**
	 * @return the children.
	 */
	public PerfDescriptor[] getChildren() {
		return children;
	}

	/**
	 * @see com.ibm.websphere.pmi.client.PerfDescriptor#getNodeName()
	 */
	public String getNodeName() {
		return wasDescriptor.getNodeName();
	}

	/**
	 * @see com.ibm.websphere.pmi.client.PerfDescriptor#getServerName()
	 */
	public String getServerName() {
		return wasDescriptor.getServerName();
	}

	/**
	 * @see com.ibm.websphere.pmi.client.PerfDescriptor#getModuleName()
	 */
	public String getModuleName() {
		return moduleName;
	}

	/**
	 * @see com.ibm.websphere.pmi.client.PerfDescriptor#getName()
	 */
	public String getName() {
		return name;
	}

	/**
	 * @see com.ibm.websphere.pmi.client.PerfDescriptor#getPath()
	 */
	public String[] getPath() {
		return wasDescriptor.getPath();
	}

	/**
	 * @see com.ibm.websphere.pmi.client.PerfDescriptor#getDataIds()
	 */
	public int[] getDataIds() {
		return wasDescriptor.getDataIds();
	}

	/**
	 * @see com.ibm.websphere.pmi.client.PerfDescriptor#getFullName()
	 */
	public String getFullName() {
		return wasDescriptor.getFullName();
	}

	/**
	 * @see com.ibm.websphere.pmi.client.PerfDescriptor#getType()
	 */
	public int getType() {
		return wasDescriptor.getType();
	}

	/**
	 * @see com.ibm.websphere.pmi.client.PerfDescriptor#getDataDescriptor()
	 */
	public DataDescriptor getDataDescriptor() {
		return wasDescriptor.getDataDescriptor();
	}

	/**
	 * @see com.ibm.websphere.pmi.client.PerfDescriptor#getName(int)
	 */
	public String getName(int arg0) {
		return wasDescriptor.getName(arg0);
	}

	/**
	 * @see com.ibm.websphere.pmi.client.PerfDescriptor#getMaxPathLength()
	 */
	public int getMaxPathLength() {
		return wasDescriptor.getMaxPathLength();
	}

	/**
	 * @see com.ibm.websphere.pmi.client.PerfDescriptor#getType(int)
	 */
	public int getType(int arg0) {
		return wasDescriptor.getType(arg0);
	}

	/**
	 * @see com.ibm.websphere.pmi.client.PerfDescriptor#equals(com.ibm.websphere.pmi.client.PerfDescriptor)
	 */
	public boolean equals(com.ibm.websphere.pmi.client.PerfDescriptor arg0) {
		return wasDescriptor.equals(arg0);
	}

	/**
	 * @see com.ibm.websphere.pmi.client.PerfDescriptor#isDescendingFrom(com.ibm.websphere.pmi.client.PerfDescriptor)
	 */
	public boolean isDescendingFrom(com.ibm.websphere.pmi.client.PerfDescriptor arg0) {
		return wasDescriptor.isDescendingFrom(arg0);
	}

	/**
	 * @see com.ibm.websphere.pmi.client.PerfDescriptor#toXMLTagStart(boolean)
	 */
	public String toXMLTagStart(boolean arg0) {
		return wasDescriptor.toXMLTagStart(arg0);
	}

	/**
	 * @see com.ibm.websphere.pmi.client.PerfDescriptor#toXMLTagEnd(boolean)
	 */
	public String toXMLTagEnd(boolean arg0) {
		return wasDescriptor.toXMLTagEnd(arg0);
	}

	/**
	 * @see com.ibm.websphere.pmi.client.PerfDescriptor#postInit()
	 */
	public void postInit() {
		wasDescriptor.postInit();
	}

	/**
	 * @see com.ibm.websphere.pmi.client.PerfDescriptor#postInit(java.lang.String)
	 */
	public void postInit(String arg0) {
		wasDescriptor.postInit(arg0);
	}

	/**
	 * @param dc
	 */
	public void setChildren(PerfDescriptor[] dc) {
		this.children = dc;
	}

	/**
	 * Debug only.
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		String[] path = getPath();
		if(path == null) {
			return null;
		}
		StringBuffer buff = new StringBuffer();
		for(int i = 0; i < path.length; i++) {
			buff.append(path[i]);
			buff.append("/");
		}
		return buff.toString();
	}

    /**
     * @return Returns the wasDescriptor.
     */
    public com.ibm.websphere.pmi.client.PerfDescriptor getWasDescriptor() {
        return wasDescriptor;
    }
}
