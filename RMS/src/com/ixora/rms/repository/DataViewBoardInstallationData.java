/*
 * Created on 30-Dec-2004
 */
package com.ixora.rms.repository;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.w3c.dom.Node;

import com.ixora.common.utils.Utils;
import com.ixora.common.xml.XMLUtils;
import com.ixora.common.xml.exception.XMLException;
import com.ixora.common.xml.exception.XMLNodeMissing;

/**
 * @author Daniel Moraru
 */
public final class DataViewBoardInstallationData implements InstallationArtefact, Comparable {
	/** Board class */
	private String fBoardClass;
	/** Board counter filter class */
	private String fBoardCounterFilterClass;
	/** Board name */
	private String fBoardName;
	/** Board view name */
	private String fViewName;
	/** Board icon */
	private String fBoardIcon;
	/** Component name */
	private String fBoardComponenName;
	/** Preference index. Lower goes first */
	private int fPreferenceIndex;
	/** Whether or not users are allowed to create views */
	private boolean fAllowUserToCreateView;
	/** Samples */
	private List<DataViewBoardSampleData> fSamples;

	/**
	 * Constructor.
	 */
	public DataViewBoardInstallationData() {
		super();
	}

	/**
	 * @return the class of the board.
	 */
	public String getBoardClass() {
		return fBoardClass;
	}
	/**
	 * @return the class of the board's counter filter; if null then this board accepts all counters.
	 */
	public String getBoardCounterFilterClass() {
		return fBoardCounterFilterClass;
	}
	/**
	 * @return the name of the board.
	 */
	public String getBoardName() {
		return fBoardName;
	}
	/**
	 * @return the name of the view.
	 */
	public String getViewName() {
		return fViewName;
	}
	/**
	 * @see com.ixora.rms.repository.InstallationArtefact#getInstallationIdentifier()
	 */
	public String getInstallationIdentifier() {
		return fBoardComponenName;
	}
	/**
	 * @return the name of the component which the board belongs to
	 */
	public String getBoardComponenName() {
		return fBoardComponenName;
	}
	/**
	 * @return the icon associated with this board
	 */
	public String getBoardIcon() {
		return fBoardIcon;
	}

	/**
	 * @return the the preference index for this board. Lower means higher priority.
	 */
	public int getPreferenceIndex() {
		return fPreferenceIndex;
	}

	/**
	 * @return
	 */
	public boolean allowUserToCreateView() {
		return fAllowUserToCreateView;
	}

	/**
	 * @return the samples.
	 */
	public List<DataViewBoardSampleData> getSamples() {
		return fSamples == null ? null : Collections.unmodifiableList(fSamples);
	}

	/**
	 * @see com.ixora.common.xml.XMLExternalizable#toXML(org.w3c.dom.Node)
	 */
	public void toXML(Node parent) throws XMLException {
		; // no need to implement yet
	}

	/**
	 * @see com.ixora.common.xml.XMLExternalizable#fromXML(org.w3c.dom.Node)
	 */
	public void fromXML(Node node) throws XMLException {
		Node n = XMLUtils.findChild(node, "class");
		if(n == null) {
			throw new XMLNodeMissing("class");
		}
		this.fBoardClass = XMLUtils.getText(n);
		n = XMLUtils.findChild(node, "counterFilterClass");
		if(n != null) {
			this.fBoardCounterFilterClass = XMLUtils.getText(n);
		}
		n = XMLUtils.findChild(node, "name");
		if(n == null) {
			throw new XMLNodeMissing("name");
		}
		this.fBoardName = XMLUtils.getText(n);
		n = XMLUtils.findChild(node, "viewName");
		if(n == null) {
			throw new XMLNodeMissing("viewName");
		}
		this.fViewName = XMLUtils.getText(n);
		n = XMLUtils.findChild(node, "component");
		if(n == null) {
			throw new XMLNodeMissing("component");
		}
		this.fBoardComponenName = XMLUtils.getText(n);
		n = XMLUtils.findChild(node, "icon");
		if(n == null) {
			throw new XMLNodeMissing("icon");
		}
		this.fBoardIcon = XMLUtils.getText(n);
		n = XMLUtils.findChild(node, "allowUserToCreateView");
		if(n != null) {
			fAllowUserToCreateView = Boolean.parseBoolean(XMLUtils.getText(n));
		} else { // default
			fAllowUserToCreateView = true;
		}
		n = XMLUtils.findChild(node, "preference");
		if(n == null) {
			throw new XMLNodeMissing("preference");
		}
		this.fPreferenceIndex = Integer.parseInt(XMLUtils.getText(n));
		n = XMLUtils.findChild(node, "samples");
		if(n != null) {
			List<Node> lst = XMLUtils.findChildren(n, "sample");
			if(lst != null) {
				this.fSamples = new LinkedList<DataViewBoardSampleData>();
				for(Node n1 : lst) {
					String fileName = XMLUtils.getText(n1);
					if(!Utils.isEmptyString(fileName)) {
						fSamples.add(new DataViewBoardSampleData(fileName));
					}
				}
			}
		}

	}

	/**
	 * Compares according to the preference index.
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object arg0) {
		int other = ((DataViewBoardInstallationData)arg0).fPreferenceIndex;
		if(fPreferenceIndex > other) {
			return 1;
		} else if(fPreferenceIndex < other) {
			return -1;
		}
		return 0;
	}

// package access
	void buildDataViewForSamples(File installationFolder) throws XMLException, IOException {
		if(fSamples != null) {
			for(DataViewBoardSampleData sample : fSamples) {
				sample.buildDataView(installationFolder);
			}
		}
	}
}

