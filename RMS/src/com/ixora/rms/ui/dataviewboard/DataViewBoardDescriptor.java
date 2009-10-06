/*
 * Created on 23-Oct-2004
 */
package com.ixora.rms.ui.dataviewboard;

import java.awt.Dimension;
import java.awt.Point;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.ixora.common.xml.XMLExternalizable;
import com.ixora.common.xml.XMLUtils;
import com.ixora.common.xml.exception.XMLAttributeMissing;
import com.ixora.common.xml.exception.XMLException;

/**
 * @author Daniel Moraru
 */
public class DataViewBoardDescriptor implements XMLExternalizable {
	/** List of DataControlDescriptor for all controls that are plotted on the board */
    private List descriptors;
    /** Board class name */
    private String boardClass;
    /** Location of the frame */
    private Point location;
    /** Dimension of the framee */
    private Dimension dimension;
    /** Title */
    private String title;

    /**
     * Constructor to support XML.
     */
    public DataViewBoardDescriptor() {
        super();
    }

    /**
     * Constructor.
     * @param descriptors
     * @param boardClass
     * @param location
     * @param size
     * @param title
     */
    public DataViewBoardDescriptor(List descriptors, String boardClass,
            Point location, Dimension size, String title) {
        super();
        this.descriptors = descriptors;
        this.boardClass = boardClass;
        this.location = location;
        this.dimension = size;
        this.title = title;
    }

    /**
     * @see com.ixora.common.xml.XMLExternalizable#toXML(org.w3c.dom.Node)
     */
    public void toXML(Node parent) throws XMLException {
	    Document doc = parent.getOwnerDocument();
	    Element el = doc.createElement("viewboard");
	    parent.appendChild(el);
	    el.setAttribute("board", boardClass);
	    el.setAttribute("title", title);
	    el.setAttribute("x", String.valueOf(location.getX()));
	    el.setAttribute("y", String.valueOf(location.getY()));
	    el.setAttribute("w", String.valueOf(dimension.getWidth()));
	    el.setAttribute("h", String.valueOf(dimension.getHeight()));
	    Element el2 = doc.createElement("controls");
	    el.appendChild(el2);
	    XMLUtils.writeObjects(null, el2, (XMLExternalizable[])descriptors
	    		.toArray(new XMLExternalizable[descriptors.size()]));
    }

    /**
     * @see com.ixora.common.xml.XMLExternalizable#fromXML(org.w3c.dom.Node)
     */
    public void fromXML(Node node) throws XMLException {
        Node n = XMLUtils.findAttribute(node, "board");
        if(n == null) {
            throw new XMLAttributeMissing("board");
        }
        this.boardClass = n.getNodeValue();
        n = XMLUtils.findAttribute(node, "title");
        if(n != null) {
            this.title = n.getNodeValue();
        }
        n = XMLUtils.findAttribute(node, "x");
        if(n != null) {
            int x = (int)Double.parseDouble(n.getNodeValue());
            n = XMLUtils.findAttribute(node, "y");
            if(n != null) {
                int y = (int)Double.parseDouble(n.getNodeValue());
                this.location = new Point(x, y);
            }
        }
        n = XMLUtils.findAttribute(node, "w");
        if(n != null) {
            int w = (int)Double.parseDouble(n.getNodeValue());
            n = XMLUtils.findAttribute(node, "h");
            if(n != null) {
                int h = (int)Double.parseDouble(n.getNodeValue());
                this.dimension = new Dimension(w, h);
            }
        }
        this.descriptors = new LinkedList();
        n = XMLUtils.findChild(node, "controls");
        if(n != null) {
        	try {
				XMLExternalizable[] objs = XMLUtils.readObjects(null, n, "control");
				if(objs != null) {
					this.descriptors = Arrays.asList(objs);
				}
			} catch(Exception e) {
				throw new XMLException(e);
			}
        }
    }
    /**
     * @return the name of the board's class
     */
    public String getBoardClass() {
        return boardClass;
    }
    /**
     * @return the list of DataViewControlDescriptor
     */
    public List getControlDescriptors() {
        return descriptors;
    }
    /**
     * @return the dimension.
     */
    public Dimension getDimension() {
        return dimension;
    }
    /**
     * @return the location.
     */
    public Point getLocation() {
        return location;
    }
    /**
     * @return the title.
     */
    public String getTitle() {
        return title;
    }
}
