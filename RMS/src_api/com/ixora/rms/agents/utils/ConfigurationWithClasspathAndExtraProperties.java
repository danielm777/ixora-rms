/*
 * Created on Jan 25, 2004
 */
package com.ixora.rms.agents.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ixora.common.utils.Utils;

/**
 * Configuration with classpath and extra properties.
 * @author Daniel Moraru
 */
public class ConfigurationWithClasspathAndExtraProperties extends ConfigurationWithClasspath {
	public static final String EXTRA_PROPERTIES = Msg.EXTRA_PROPERTIES;

	/**
	 * Empty constructor. Required to allow the associated panel
	 * to create an empty configuration instance for editing.
	 */
	public ConfigurationWithClasspathAndExtraProperties() {
		super();
        setProperty(EXTRA_PROPERTIES, TYPE_STRING, true, false);
	}


    /**
     * @return
     */
    public Map<String, ?> getExtraProperties() {
    	Object obj = getString(EXTRA_PROPERTIES);
    	if(obj == null) {
    		return null;
    	}
    	String entries = (String)obj;
    	Pattern pat = Pattern.compile("([^=]+)=([^,]*)");
    	Matcher matcher = pat.matcher(entries);
    	Map<String, Object> ret = new HashMap<String, Object>();
    	while(matcher.find()) {
    		String name = matcher.group(1).trim();
    		if(!Utils.isEmptyString(name)) {
	    		if(name.charAt(0) == ',') {
	    			name = name.substring(1);
	    		}
	    		String value = matcher.group(2).trim();
	    		if(value.indexOf('#') >= 0) {
	    			String[] vals = value.split("#");
	    			ret.put(name, vals);
	    		} else {
	    			ret.put(name, value);
	    		}
    		}
    	}
    	return ret;
    }

//    public static void main(String[] args) {
//		try {
//			ConfigurationWithClasspathAndExtraProperties c = new ConfigurationWithClasspathAndExtraProperties();
//			c.setString(EXTRA_PROPERTIES, "name1=value1,name2=value21#value22,name3=value3");
//			Map<String, ?> ep = c.getExtraProperties();
//			System.err.println(ep);
//		} catch(Exception e) {
//			e.printStackTrace();
//		}
//	}
}