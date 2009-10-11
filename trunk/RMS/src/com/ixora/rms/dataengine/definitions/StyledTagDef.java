/*
 * Created on 28-Nov-2004
 */
package com.ixora.rms.dataengine.definitions;

import com.ixora.common.xml.XMLAttribute;
import com.ixora.common.xml.XMLAttributeBoolean;
import com.ixora.common.xml.XMLAttributeDouble;
import com.ixora.common.xml.XMLAttributeString;
import com.ixora.common.xml.XMLTag;

/**
 * StyledTagDef
 * Defines a series of atributes which can be associated with various tags.
 * Loads and saves contents into XML.
 */
public abstract class StyledTagDef extends XMLTag {
	private static final long serialVersionUID = 5897934505864193726L;
	protected XMLAttribute id = new XMLAttributeString("id");
	protected XMLAttributeDouble min = new XMLAttributeDouble("min");
	protected XMLAttributeDouble max = new XMLAttributeDouble("max");
	protected XMLAttributeBoolean continuous = new XMLAttributeBoolean("continuous");
	protected XMLAttributeBoolean stacked = new XMLAttributeBoolean("stacked");
	protected XMLAttribute name = new XMLAttributeString("name");
	protected XMLAttribute iname = new XMLAttributeString("iname");
	protected XMLAttribute description = new XMLAttributeString("description");
	protected XMLAttribute format = new XMLAttributeString("format");
	protected XMLAttribute style = new XMLAttributeString("style");
	protected XMLAttribute type = new XMLAttributeString("type");
    protected XMLAttribute code = new XMLAttributeString("code");

    protected StyledTagDef() {
    	super();
    }

    /**
     * @param def
     */
    protected StyledTagDef(StyledTagDef def) {
    	super();
    	id.setValue(def.getID());
    	min.setValue(def.getMIN());
    	max.setValue(def.getMAX());
    	continuous.setValue(def.getContinuous());
    	stacked.setValue(def.getStacked());
    	name.setValue(def.getName());
    	iname.setValue(def.getIName());
    	description.setValue(def.getDescription());
    	format.setValue(def.getFormat());
    	style.setValue(def.getStyle());
    	type.setValue(def.getType());
    	code.setValue(def.getCode());
    }

	/** Gets/sets the ID property */
	public String getID() { return id.getValue(); }
	public void setID(String value) { id.setValue(value); }

	/** Gets/sets the MIN property */
	public Double getMIN() { return min.getDouble(); }
	public void setMIN(Double value) { min.setValue(value); }
	public void setMIN(String value) { min.setValue(value); }

	/** Gets/sets the MAX property */
	public Double getMAX() { return max.getDouble(); }
	public void setMAX(Double value) { max.setValue(value); }
	public void setMAX(String value) { max.setValue(value); }

	/** Gets/sets the CONTINUOUS property */
	public Boolean getContinuous() { return continuous.getBoolean(); }
	public void setContinuous(Boolean value) { continuous.setValue(value); }
	public void setContinuous(String value) { continuous.setValue(value); }

	/** Gets/sets the STACKED property */
	public Boolean getStacked() { return stacked.getBoolean(); }
	public void setStacked(Boolean value) { stacked.setValue(value); }
	public void setStacked(String value) { stacked.setValue(value); }

	/** Gets/sets the NAME property */
	public String getName() { return name.getValue(); }
	public void setName(String value) { name.setValue(value); }

	/** Gets/sets the INAME property */
	public String getIName() { return iname.getValue(); }
	public void setIName(String value) { iname.setValue(value); }

	/** Gets/sets the DESCRIPTION property */
	public String getDescription() { return description.getValue(); }
	public void setDescription(String value) { description.setValue(value); }

	/** Gets/sets the FORMAT property */
	public String getFormat() { return format.getValue(); }
	public void setFormat(String value) { format.setValue(value); }

	/** Gets/sets the STYLE property */
	public String getStyle() { return style.getValue(); }
	public void setStyle(String value) { style.setValue(value); }

	/** Gets/sets the TYPE property */
	public String getType() { return type.getValue(); }
	public void setType(String value) { type.setValue(value); }

    /** Gets/sets the CODE property */
    public String getCode() { return code.getValue(); }
    public void setCode(String value) { code.setValue(value); }
}
