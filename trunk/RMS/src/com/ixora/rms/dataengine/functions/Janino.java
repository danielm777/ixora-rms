/*
 * Created on 12-Nov-2004
 */
package com.ixora.rms.dataengine.functions;

import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import org.codehaus.janino.ScriptEvaluator;

import com.ixora.rms.ResourceId;
import com.ixora.common.exception.AppRuntimeException;
import com.ixora.common.utils.Utils;
import com.ixora.rms.CounterType;
import com.ixora.rms.data.CounterValue;
import com.ixora.rms.data.CounterValueDouble;
import com.ixora.rms.data.CounterValueString;
import com.ixora.rms.dataengine.Resource;
import com.ixora.rms.dataengine.Style;
import com.ixora.rms.dataengine.definitions.ParamDef;
import com.ixora.rms.dataengine.definitions.ScriptFunctionDef;
import com.ixora.rms.exception.RMSException;

/**
 * JaninoFunction
 * @author Cristian Costache
 * @author Daniel Moraru
 */
public class Janino extends Function {
	private static final long serialVersionUID = -9195236613294967965L;
	private transient ScriptEvaluator fScriptEvaluator;
    private Class<?> fReturnType;

    /** Used to check if Resource ids make good variable names */
    private static Pattern 	identifierPattern = Pattern.compile("[a-zA-Z]\\w*");
    private static Pattern 	javaKeywordsPattern = Pattern.compile(
            "class|interface|extends|implements|import|package|public|private|protected|" +
            "static|super|null|transient|" +
    		"new|for|while|break|continue|switch|case|true|false|final|" +
    		"if|else|return|throw|catch|instanceof|this|synchronized|" +
    		"void|boolean|int|Integer|double|Double|long|Long|char|byte|String");

    /** These members are storing state through serialization */
    private String[] fVars;
    private Class<?>[] fTypes;
    private String  fCode;

    /**
     * @param sfd
     * @param listQueryResources
     * @param ridContext
     */
    public Janino(ScriptFunctionDef sfd, List<Resource> listQueryResources, ResourceId ridContext) {
    	this(sfd, listQueryResources, ridContext, null);
    }
    /**
     * @param sfd
     * @param listQueryResources
     * @param ridContext
     * @param inheritFrom style to inherit from, used when a Janino function is created from a Resource that
     * has a code tag attached to its style
     */
    public Janino(ScriptFunctionDef sfd, List<Resource> listQueryResources, ResourceId ridContext, Style inheritFrom) {
        super(sfd, listQueryResources, ridContext, false, inheritFrom);

        // Compile code and create script evaluator
        fCode = sfd.getCode();

        List<ParamDef>	listParamDefs = sfd.getParameters();
		fVars = new String[listParamDefs.size()];
        fTypes = new Class[listParamDefs.size()];
        int i = 0;
        for (ParamDef paramDef : listParamDefs) {
            String type;
            fVars[i] = getVarName(paramDef.getID(), i);

            // Look for the resource with given ID
		    Resource foundResource = null;
		    if (paramDef.getID() != null) {
			    for (Resource resource : listQueryResources){
			        if (paramDef.getID().equals(resource.getStyle().getID())) {
			            foundResource = resource;
			            break;
			        }
			    }
		    }

            // If there is no such resource (or if ID is null) then the
		    // parameter specifies the value (and optionally the type).
            if (foundResource != null) {
                type = foundResource.getStyle().getType();
            } else {
                // Use param's type, if any
                type = paramDef.getType();
            }

            // Create script type out of our own type
            if (Style.TYPE_STRING.equalsIgnoreCase(type)) {
                fTypes[i] = String.class;
            }
            else if (Style.TYPE_NUMBER.equalsIgnoreCase(type)) {
                fTypes[i] = Double.TYPE;
            }
            else if (Style.TYPE_DATE.equalsIgnoreCase(type)) {
                fTypes[i] = Date.class;
            }
            else {
                fTypes[i] = Double.TYPE;
            }

            i++;
        }
        initScriptEvaluator();
    }

    /**
     * Creates an instance of a script evaluator.
     */
    private void initScriptEvaluator() {
        try {
            String type = getStyle().getType();
            if (Utils.isEmptyString(type)) {
                type = Style.TYPE_NUMBER;
            }
            fReturnType = Double.TYPE;
            if (type.equalsIgnoreCase(Style.TYPE_STRING)) {
                fReturnType = String.class;
            }

            // Compile the code
            fScriptEvaluator = new ScriptEvaluator(fCode, fReturnType, fVars, fTypes);
        } catch (Exception e) {
            // (script syntax error, invalid types etc).
            throw new AppRuntimeException(e);
        }
    }

	/**
	 * @return Hardcoded name of this function's operation
	 */
	public static String getOp() {
		return "script";
	}

    public String test(String s) {
        return new Date(Long.parseLong(s)).toString();
    }

    /**
     * @see com.ixora.rms.dataengine.functions.Function#execute(com.ixora.rms.data.CounterValue[], com.ixora.rms.CounterType[])
     */
    public CounterValue execute(CounterValue[] args, CounterType[] types) throws RMSException {
        CounterValue retCV = null;
        try {
            if (args.length != fTypes.length) {
                throw new AppRuntimeException("Insufficient arguments for function");
            }

            // Prepare parameters: strings and doubles
            Object[] params = new Object[fTypes.length];
            for (int i=0; i<fTypes.length; i++) {
                if (fTypes[i].equals(String.class)) {
                    params[i] = args[i].toString();
                } else {
                    if (args[i] instanceof CounterValueDouble) {
                        params[i] = new Double(((CounterValueDouble)args[i]).getDouble());
                    } else if (args[i] instanceof CounterValueString) {
                        throw new AppRuntimeException("Received a String instead of Double for parameter " + i);
                    }
                }
            }

            // Execute script and get result
            if (fScriptEvaluator == null) {
                initScriptEvaluator();
            }

            Object retVal = fScriptEvaluator.evaluate(params);
            if (fReturnType.equals(Double.TYPE)) {
                Double value = (Double)retVal;
                retCV = new CounterValueDouble(value.doubleValue());
            } else {
                retCV = new CounterValueString(retVal.toString());
            }
        } catch (Exception e) {
            // Script code has errors: stop processing and inform the user
        	throw new RMSException(e);
        }
        return retCV;
    }

	/**
	 * @see com.ixora.rms.dataengine.functions.Function#getReturnedType()
	 */
	public CounterType getReturnedType() {
		if(fReturnType.equals(String.class)) {
			return CounterType.STRING;
		}
		return CounterType.DOUBLE;
	}

    /**
     * There will be one variable for each resource of the function;
     * called r0, r1 etc. If a resource has an ID defined and it is a valid
     * identifier then the id is used as the variable name.
     * @param id ID to check for validity
     * @param index index of this resource in the list of all query resources
     * @return a valid script identifier
     */
    private String getVarName(String id, int index) {
        // is it a valid identifier and not a java keyword?
        if (id == null ||
                !identifierPattern.matcher(id).matches() ||
                javaKeywordsPattern.matcher(id).matches()) {
            id = "r" + index;
        }
        return id;
    }


}
