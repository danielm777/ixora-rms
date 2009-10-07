/*
 * Created on 12-Sep-2005
 */
package com.ixora.rms.agents.snmp;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import net.percederberg.mibble.MibType;
import net.percederberg.mibble.value.ObjectIdentifierValue;

public class MIBNode implements Serializable {
    private static final long serialVersionUID = 264448826444722285L;

	private List<MIBNode>	fListChildren = new LinkedList<MIBNode>();

    /** MIB node name. */
    private String name;

    /** MIB node description. */
    private String description;

    /** MIB node type (integer, string etc). */
    private MibType type;

    /** MIB node object identifier (oid) value. */
    private ObjectIdentifierValue value;

    /**
     * Creates a new MIB tree node.
     *
     * @param name           the node name
     * @param value          the node object identifier value
     */
    public MIBNode(String name, MibType mibType, ObjectIdentifierValue value) {
        this.name = name;
        this.value = value;
        this.type = mibType;
        if (mibType != null) {
	        String htmlType = mibType.toString().replaceAll("\n", "<br>");
	        this.description = htmlType;
        } else {
        	this.description = "";
        }
    }

    /**
     * Returns the node name.
     *
     * @return the node name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the node description.
     *
     * @return the node description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Adds a new child node
     * @param node
     */
    public void add(MIBNode node) {
    	fListChildren.add(node);
    }

    /**
     * @return the list of children
     */
    public List<MIBNode> getChildren() {
    	return fListChildren;
    }

    /**
     * @return the number of children
     */
    public int getChildCount() {
    	return fListChildren.size();
    }

    /**
     * @param index
     * @return the child at specified index, or null
     */
    public MIBNode getChild(int index) {
    	if (index < getChildCount()) {
    		return fListChildren.get(index);
    	} else {
    		return null;
    	}
    }

    /**
     * Returns the node object identifier value.
     *
     * @return the node object identifier value, or
     *         null if no value is present
     */
    public ObjectIdentifierValue getValue() {
        return value;
    }

    /**
     * @return just the last number in the OID string (OID relative to the parent)
     */
    public String getRelativeOID() {
    	if (value == null) {
    		return name;
    	} else {
    		return Integer.toString(value.getValue());
    	}
    }

    /**
     * Returns the object identifier (oid) associated with the node.
     *
     * @return the node object identifier (oid), or
     *         an empty string if no object identifier is present
     */
    public String getOid() {
        if (value == null) {
            return "";
        } else {
            return value.toString();
        }
    }

    /**
     * Note: this can be null, which means this node does not have an associated value.
     * @return the MibType associated with this node (boolean, integer, string etc).
     */
    public MibType getType() {
    	return type;
    }

}
