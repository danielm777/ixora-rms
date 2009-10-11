package com.ixora.rms.dataengine;

import java.io.Serializable;
import java.util.HashMap;

import com.ixora.rms.dataengine.definitions.StyleDef;
import com.ixora.rms.dataengine.definitions.StyledTagDef;

/**
 * Style
 */
public class Style implements Serializable {
	private static final long serialVersionUID = 3656339114465039929L;
	// One of the possible values of the "type" attribute
	public static final String TYPE_STRING = "string";
	public static final String TYPE_NUMBER = "number";
	public static final String TYPE_DATE = "date";
	public static final String TYPE_OBJECT = "object";

	private static final String NAME_ID = "id";
	private static final String NAME_MIN = "min";
	private static final String NAME_MAX = "max";
	private static final String NAME_CONTINUOUS = "continuous";
	private static final String NAME_STACKED = "stacked";
	private static final String NAME_NAME = "name";
	private static final String NAME_INAME = "iname";
	private static final String NAME_DESCRIPTION = "description";
	private static final String NAME_FORMAT = "format";
	private static final String NAME_STYLE = "style";
	private static final String NAME_TYPE = "type";
    private static final String NAME_CODE = "code";
    private HashMap<String, Object> fStyles;

	/** Empty constructor */
	public Style() {
		fStyles = new HashMap<String, Object>();
	}

	/** XML constructor */
	public Style(StyledTagDef std) {
		fStyles = new HashMap<String, Object>();
	    this.setContinuous(std.getContinuous());
	    this.setDescription(std.getDescription());
	    this.setFormat(std.getFormat());
	    this.setID(std.getID());
	    this.setMAX(std.getMAX());
	    this.setMIN(std.getMIN());
	    this.setName(std.getName());
	    this.setIName(std.getIName());
	    this.setStacked(std.getStacked());
	    this.setStyle(std.getStyle());
	    this.setType(std.getType());
        this.setCode(std.getCode());
	}

	/**
	 * @return the style definition
	 */
	public StyleDef getStyleDef() {
		StyleDef def = new StyleDef();
	    def.setContinuous(getContinuous());
	    def.setDescription(getDescription());
	    def.setFormat(getFormat());
	    def.setID(getID());
	    def.setMAX(getMAX());
	    def.setMIN(getMIN());
	    def.setName(getName());
	    def.setIName(getIName());
	    def.setStacked(getStacked());
	    def.setStyle(getStyle());
	    def.setType(getType());
        def.setCode(getCode());
        return def;
	}

	/** Gets/sets the ID property */
	public String getID() { return (String)fStyles.get(NAME_ID); }
	public void setID(String value) { fStyles.put(NAME_ID, value); }

	/** Gets/sets the MIN property */
	public Double getMIN() { return (Double)fStyles.get(NAME_MIN); }
	public void setMIN(Double value) { fStyles.put(NAME_MIN, value); }
	public void setMIN(String value) { fStyles.put(NAME_MIN, Double.valueOf(value)); }

	/** Gets/sets the MAX property */
	public Double getMAX() { return (Double)fStyles.get(NAME_MAX); }
	public void setMAX(Double value) { fStyles.put(NAME_MAX, value); }
	public void setMAX(String value) { fStyles.put(NAME_MAX, Double.valueOf(value)); }

	/** Gets/sets the CONTINUOUS property */
	public Boolean getContinuous() { return (Boolean)fStyles.get(NAME_CONTINUOUS); }
	public void setContinuous(Boolean value) { fStyles.put(NAME_CONTINUOUS, value); }
	public void setContinuous(String value) { fStyles.put(NAME_CONTINUOUS, Boolean.valueOf(value)); }

	/** Gets/sets the STACKED property */
	public Boolean getStacked() { return (Boolean)fStyles.get(NAME_STACKED); }
	public void setStacked(Boolean value) { fStyles.put(NAME_STACKED, value); }
	public void setStacked(String value) { fStyles.put(NAME_STACKED, Boolean.valueOf(value)); }

	/** Gets/sets the NAME property */
	public String getName() { return (String)fStyles.get(NAME_NAME); }
	public void setName(String value) { fStyles.put(NAME_NAME, value); }

	/** Gets/sets the INAME property */
	public String getIName() { return (String)fStyles.get(NAME_INAME); }
	public void setIName(String value) { fStyles.put(NAME_INAME, value); }

	/** Gets/sets the DESCRIPTION property */
	public String getDescription() { return (String)fStyles.get(NAME_DESCRIPTION); }
	public void setDescription(String value) { fStyles.put(NAME_DESCRIPTION, value); }

	/** Gets/sets the FORMAT property */
	public String getFormat() { return (String)fStyles.get(NAME_FORMAT); }
	public void setFormat(String value) { fStyles.put(NAME_FORMAT, value); }

	/** Gets/sets the STYLE property */
	public String getStyle() { return (String)fStyles.get(NAME_STYLE); }
	public void setStyle(String value) { fStyles.put(NAME_STYLE, value); }

	/** Gets/sets the TYPE property */
	public String getType() { return (String)fStyles.get(NAME_TYPE); }
	public void setType(String value) { fStyles.put(NAME_TYPE, value); }

    /** Gets/sets the CODE property */
    public String getCode() { return (String)fStyles.get(NAME_CODE); }
    public void setCode(String value) { fStyles.put(NAME_CODE, value); }

	/**
	 * Merges the contents of the other style into this one. If a value
	 * is already set in the current style it will take precendence.
	 */
	public void merge(Style s) {

		if (getID() == null)
			setID(s.getID());

		if (getMIN() == null)
			setMIN(s.getMIN());

		if (getMAX() == null)
			setMAX(s.getMAX());

		if (getContinuous() == null)
			setContinuous(s.getContinuous());

		if (getStacked() == null)
			setStacked(s.getStacked());

		if (getName() == null)
			setName(s.getName());

		if (getIName() == null)
			setIName(s.getIName());

		if (getDescription() == null)
			setDescription(s.getDescription());

		if (getFormat() == null)
			setFormat(s.getFormat());

		if (getStyle() == null)
			setStyle(s.getStyle());

		if (getType() == null)
			setType(s.getType());

        if (getCode() == null)
            setCode(s.getCode());
	}
}
