/*
 * Created on 11-Jan-2004
 */
package com.ixora.rms.batch;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.ixora.common.xml.XMLUtils;
import com.ixora.common.xml.exception.XMLException;
import com.ixora.common.xml.exception.XMLNodeMissing;
import com.ixora.rms.client.session.MonitoringSessionDescriptor;

/**
 * @author Daniel Moraru
 */
public final class MonitoringSessionLoader {

	/**
	 * Constructor.
	 */
	private MonitoringSessionLoader() {
		super();
	}

	/**
	 * Loads a session from file.
	 * @param parent
	 * @param session
	 * @throws FileNotFoundException
	 * @throws XMLException
	 */
	public static MonitoringSessionDescriptor loadSession(String session) throws XMLException, FileNotFoundException {
		Document doc = null;
		BufferedInputStream is = null;
		try {
			doc = XMLUtils.read(
			is = new BufferedInputStream(
				new FileInputStream(session)));
		} finally {
			if(is != null) {
				try {
					is.close();
				} catch(Exception e) {
				}
			}
		}
		Node n = XMLUtils.findChild(doc.getFirstChild(), "session");
		if(n == null) {
			throw new XMLNodeMissing("session");
		}
		MonitoringSessionDescriptor ret = new MonitoringSessionDescriptor();
		ret.fromXML(n);
		ret.setLocation(new File(session).getParent());
		return ret;
	}
}
