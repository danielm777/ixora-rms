/*
 * Created on 20-Feb-2005
 */
package com.ixora.rms.ui.dataviewboard.handler;

import java.util.ArrayList;
import java.util.Collection;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.ixora.common.utils.Utils;
import com.ixora.common.xml.XMLExternalizable;
import com.ixora.common.xml.XMLUtils;
import com.ixora.common.xml.exception.XMLException;
import com.ixora.common.xml.exception.XMLNodeMissing;
import com.ixora.rms.ui.dataviewboard.DataViewBoardDescriptor;

/**
 * @author Daniel Moraru
 */
public final class DataViewScreenDescriptor implements XMLExternalizable {
	private static final long serialVersionUID = -7474464649791056932L;
	private String screenName;
	private boolean selected;
	private Collection<DataViewBoardDescriptor> boardDescriptors;


	/**
	 * Default constructor to support XML.
	 */
	public DataViewScreenDescriptor() {
		super();
	}

	/**
	 * Constructor.
	 */
	public DataViewScreenDescriptor(String screenName,
			boolean selected, Collection<DataViewBoardDescriptor> boardDescriptors) {
		super();
		this.screenName = screenName;
		this.selected = selected;
		this.boardDescriptors = boardDescriptors;
	}

	/**
	 * @return the boardDescriptors.
	 */
	public Collection<DataViewBoardDescriptor> getBoardDescriptors() {
		return boardDescriptors;
	}
	/**
	 * @return the screenName.
	 */
	public String getScreenName() {
		return screenName;
	}
	/**
	 * @return the selected.
	 */
	public boolean isSelected() {
		return selected;
	}

	/**
	 * @see com.ixora.common.xml.XMLExternalizable#toXML(org.w3c.dom.Node)
	 */
	public void toXML(Node parent) throws XMLException {
		Document doc = parent.getOwnerDocument();
		Element elScreen = doc.createElement("screen");
		parent.appendChild(elScreen);
		Element elName = doc.createElement("name");
		elScreen.appendChild(elName);
		elName.appendChild(doc.createTextNode(screenName));
		Element elSelected = doc.createElement("selected");
		elScreen.appendChild(elSelected);
		elSelected.appendChild(doc.createTextNode(String.valueOf(this.selected)));
		if(this.boardDescriptors != null) {
			Element elBoards = doc.createElement("viewboards");
			elScreen.appendChild(elBoards);
			XMLUtils.writeObjects(DataViewBoardDescriptor.class, elBoards,
			       this.boardDescriptors.toArray(
			               new XMLExternalizable[this.boardDescriptors.size()]));
		}
	}

	/**
	 * @see com.ixora.common.xml.XMLExternalizable#fromXML(org.w3c.dom.Node)
	 */
	public void fromXML(Node node) throws XMLException {
		Node n = XMLUtils.findChild(node, "name");
		if(n == null) {
			throw new XMLNodeMissing("name");
		}
		this.screenName = XMLUtils.getText(n);
		n = XMLUtils.findChild(node, "selected");
		if(n != null) {
			this.selected = Utils.parseBoolean(XMLUtils.getText(n));
		}
		n = XMLUtils.findChild(node, "viewboards");
		if(n != null) {
		    XMLExternalizable[] vbs;
            try {
                vbs = XMLUtils.readObjects(
                		DataViewBoardDescriptor.class, n, "viewboard");
                if(!Utils.isEmptyArray(vbs)) {
                    this.boardDescriptors = new ArrayList<DataViewBoardDescriptor>(vbs.length);
                    for(XMLExternalizable vb : vbs) {
                    	this.boardDescriptors.add((DataViewBoardDescriptor)vb);
                    }
                }
            } catch (XMLException e) {
                throw e;
            } catch (Exception e) {
                throw new XMLException(e);
            }
		}
	}
}
