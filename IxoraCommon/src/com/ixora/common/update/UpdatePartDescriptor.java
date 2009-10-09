package com.ixora.common.update;

import java.io.Serializable;
import java.util.regex.Pattern;

import org.w3c.dom.Node;

import com.ixora.common.xml.XMLExternalizable;
import com.ixora.common.xml.XMLUtils;
import com.ixora.common.xml.exception.XMLException;
import com.ixora.common.xml.exception.XMLNodeMissing;

/*
 * Created on Feb 12, 2004
 */
/**
 * @author Daniel Moraru
 */
public final class UpdatePartDescriptor implements XMLExternalizable, Serializable {
	private static final long serialVersionUID = 2994349584521132296L;
	/** Content location on the server, same as the local file name */
	private String contentLocation;
	/** Local install location */
	private String installLocation;
	/** Whether application needs to be restarted */
	private boolean needAppRestart;
	/** Whether system needs to be restarted */
	private boolean needSysRestart;
	/** Whether or not this is zipped */
	private boolean zipUpdate;
	/**
	 * OS regexp pattern identifing the operating
	 * systems for which this update part applies
	 */
	private String osPattern;

	/**
	 * Constructor.
	 */
	public UpdatePartDescriptor() {
		super();
	}

	/**
	 * @return the contentLocation.
	 */
	public String getContentLocation() {
		return contentLocation;
	}

	/**
	 * @return the installLocation.
	 */
	public String getInstallLocation() {
		return installLocation;
	}

	/**
	 * @return the needAppRestart.
	 */
	public boolean needsAppRestart() {
		return needAppRestart;
	}

	/**
	 * @return the needSysRestart.
	 */
	public boolean needsSysRestart() {
		return needSysRestart;
	}

	/**
	 * @return the zipUpdate.
	 */
	public boolean isZipUpdate() {
		return zipUpdate;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return contentLocation == null ? "" : contentLocation.toString();
	}

	/**
	 * @see com.ixora.common.xml.XMLExternalizable#toXML(org.w3c.dom.Node)
	 */
	public void toXML(Node parent) throws XMLException {
		;// not needed
	}

	/**
	 * @see com.ixora.common.xml.XMLExternalizable#fromXML(org.w3c.dom.Node)
	 */
	public void fromXML(Node node) throws XMLException {
		Node n = XMLUtils.findChild(node, "content");
		if(n == null) {
			throw new XMLNodeMissing("content");
		}
		this.contentLocation = n.getFirstChild().getNodeValue();
		n = XMLUtils.findChild(node, "installlocation");
		Node n1 = XMLUtils.findChild(node, "isZip");
		if(n == null && n1 == null) {
			throw new XMLNodeMissing("installlocation|isZip");
		}
		if(n != null) {
			this.installLocation = n.getFirstChild().getNodeValue();
		}
		if(n1 != null) {
			this.zipUpdate = Boolean.valueOf(
				n1.getFirstChild().getNodeValue()).booleanValue();
		}
		n = XMLUtils.findChild(node, "needapprestart");
		if(n != null) {
			this.needAppRestart = Boolean.valueOf(
				n.getFirstChild().getNodeValue()).booleanValue();
		}
		n = XMLUtils.findChild(node, "needsysrestart");
		if(n != null) {
			this.needSysRestart = Boolean.valueOf(
					n.getFirstChild().getNodeValue()).booleanValue();
		}
		n = XMLUtils.findChild(node, "os");
		if(n != null) {
			this.osPattern = n.getFirstChild().getNodeValue();
			if(this.osPattern != null) {
				this.osPattern.trim();
			}
		}
	}

// not visible outside this package
	/**
	 * @param os the operation system as returned by
	 * <code>System.getProperty("os.name")</code>.
	 * @return true if the part applies to the given
	 * operating system
	 */
	boolean appliesToOs(String os) {
		if(osPattern == null || osPattern.length() == 0) {
			return true;
		}
		return Pattern.matches(osPattern, os);
	}
}
