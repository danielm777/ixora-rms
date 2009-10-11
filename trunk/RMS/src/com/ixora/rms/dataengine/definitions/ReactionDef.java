/*
 * Created on 28-Nov-2004
 */
package com.ixora.rms.dataengine.definitions;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.w3c.dom.Node;

import com.ixora.common.utils.Utils;
import com.ixora.common.xml.XMLAttribute;
import com.ixora.common.xml.XMLAttributeString;
import com.ixora.common.xml.XMLCData;
import com.ixora.common.xml.XMLTag;
import com.ixora.common.xml.exception.XMLException;

/**
 * ReactionDef.
 * Contains definition for a reaction (data only, no functionality),
 * Loads and saves contents into XML.
 * @author Daniel Moraru
 */
public class ReactionDef extends XMLTag {
	private static final long serialVersionUID = 5176936784426892583L;
	private XMLAttribute params = new XMLAttributeString("params", true);
	private XMLAttribute severity = new XMLAttributeString("severity", true);
	private XMLCData arm = new XMLCData("arm", true);
	private XMLCData disarm = new XMLCData("disarm", true);
	private XMLCData fire = new XMLCData("fire", true);
	private XMLCData delivery = new XMLCData("delivery", false);
	private XMLCData message = new XMLCData("message", false);
	private XMLCData advise = new XMLCData("advise", false);

	private Set<String> parameters = new HashSet<String>();
	private String id;

	/**
     * Constructs an empty object, ready to be loaded from XML
     */
    public ReactionDef() {
        super();
    }

    /**
     * @param params
     * @param severity
     * @param armCode
     * @param disarmCode
     * @param fireCode
     * @param delivCode
     * @param msgCode
     * @param adviseCode
     */
    public ReactionDef(
    		String[] params, String severity,
    		String armCode, String disarmCode,
    		String fireCode, String delivCode,
    		String msgCode, String adviseCode) {
        super();
        this.params.setValue(paramsToString(params));
        this.parameters.addAll(Arrays.asList(params));
        this.severity.setValue(severity);
        this.arm.setValue(armCode);
        this.disarm.setValue(disarmCode);
        this.fire.setValue(fireCode);
        this.delivery.setValue(delivCode);
        this.message.setValue(msgCode);
        this.advise.setValue(adviseCode);
    }

    /**
     * @return the hardcoded name of this tag
     * @see com.ixora.common.xml.XMLTag#getTagName()
     */
    public String getTagName() {
    	return "reaction";
    }

	/**
	 * @return
	 */
	public Set<String> getParameters() {
		return Collections.unmodifiableSet(parameters);
	}

	/**
	 * @return
	 */
	public String getArmCode() {
		return this.arm.getValue();
	}

	/**
	 * @return
	 */
	public String getDisarmCode() {
		return this.disarm.getValue();
	}

	/**
	 * @return
	 */
	public String getFireCode() {
		return this.fire.getValue();
	}

	/**
	 * @return
	 */
	public String getMessageCode() {
		return this.message.getValue();
	}

	/**
	 * @return
	 */
	public String getAdviseCode() {
		return this.advise.getValue();
	}

	/**
	 * @return
	 */
	public String getDeliveryCode() {
		return this.delivery.getValue();
	}

	/**
	 * @return
	 */
	public String getSeverity() {
		return this.severity.getValue();
	}

	/**
	 * @see com.ixora.common.xml.XMLTag#fromXML(org.w3c.dom.Node)
	 */
	public void fromXML(Node node) throws XMLException {
		super.fromXML(node);
		String tmp = params.getValue();
		String[] vals = tmp.split(",");
		for(String val : vals) {
			String valTrimmed = val.trim();
			if(!Utils.isEmptyString(valTrimmed)) {
				this.parameters.add(val);
			}
		}
		if(Utils.isEmptyCollection(this.parameters)) {
			throw new XMLException("At least one parameter is required");
		}
	}

	/**
	 * Sets the id for this query.
	 * @param id
	 */
	public void setIdentifier(String id) {
		this.id = id;
	}

	/**
	 * @return
	 */
	public String geIdentifier() {
		return this.id;
	}

	/**
	 * @param params
	 * @return
	 */
	private String paramsToString(String[] params) {
		StringBuffer ret = new StringBuffer();
		if(!Utils.isEmptyArray(params)) {
			for(int i = 0; i < params.length; i++) {
				ret.append(params[i]);
				if(i < params.length - 1) {
					ret.append(",");
				}
			}
		}
		return ret.toString();
	}
}
