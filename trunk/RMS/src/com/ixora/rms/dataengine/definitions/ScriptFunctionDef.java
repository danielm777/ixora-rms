/*
 * Created on 05-Dec-2004
 */
package com.ixora.rms.dataengine.definitions;

import java.util.List;

import com.ixora.common.xml.XMLText;

/**
 * ScriptFunctionDef
 * Provides same functionality as a FunctionDef, adding definition for
 * a function's code (as text in some script language).
 * Loads and saves contents into XML.
 */
public class ScriptFunctionDef extends FunctionDef {
	private XMLText	code = new XMLText("code");

    /**
     * Constructs an empty object, ready to be loaded from XML
     */
    public ScriptFunctionDef() {
        super();
    }

    /**
     * Constructs an object from given values (as opposed to read it
     * from XML)
     * @param listParamDefs list of input parameters
     * @param code actual script code to execute
     */
    public ScriptFunctionDef(List<ParamDef> listParamDefs,
							 String code) {
        super("script", listParamDefs);
    	this.code.setValue(code);
    }

    /**
     * @return code for this function (script text)
     */
    public String getCode() {
    	return code.getValue();
    }
}
