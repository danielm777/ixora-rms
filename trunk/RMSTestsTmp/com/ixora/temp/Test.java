/*
 * Created on 31-Aug-2004
 */
package com.ixora.temp;

import java.net.InetAddress;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.text.BadLocationException;

/**
 * @author Daniel Moraru
 */
public class Test {


    /**
     * Constructor.
     *
     */
    public Test() {
        super();
    }

    public static final class MyLoader extends URLClassLoader {
        public MyLoader(URL[] urls) {
            super(urls);
        }

        /**
         * @see java.lang.ClassLoader#findClass(java.lang.String)
         */
        protected Class findClass(String name) throws ClassNotFoundException {
            System.out.println("Checking for... " + name);
            return super.findClass(name);
        }
    }


    public static void main(String[] args) {
        try {
//        	String regex = "resource[\\s]+id[\\s]*=[\\s]*\"([^\"]+)\"";
//    		Pattern pat = Pattern.compile(regex);
//    		Matcher mat = pat.matcher("resource id=\"usertime\"");
//        	boolean f = mat.find();

        	InetAddress ia = InetAddress.getByName("192.168.1.33");
        	getParametersFromDocument();

        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    boolean an(Object log_records) {
    	String match="Failed";
    	java.util.List records = ((com.ixora.rms.agents.impl.logfile.LogRecordBatch)log_records).getLogRecords();
    	for(java.util.Iterator iter = records.iterator();iter.hasNext();){
    		com.ixora.rms.agents.impl.logfile.LogRecord
    			logRecord = (com.ixora.rms.agents.impl.logfile.LogRecord)iter.next();
    		String message = logRecord.getMessage();
    		if(message == null) {
    			return false;
    		} else {
    			return message.contains(match);
    		}
    	}
    	return false;
    }
	/**
	 * @return
	 * @throws BadLocationException
	 */
	private static String[] getParametersFromDocument() throws BadLocationException {
		// regex used to extract parameters
		String regex = "resource[\\s]+id[\\s]*=[\\s]*\"([^\"]+)\"";
		String text = "<rms>"
					+ "<view>"
					+ "<description>CPU usage</description>"
					+ "<query>"
					+ "<resource id=\"time\" rid=\"-/-/root/Processor/[#time#]\"/>"
					+ "<resource id=\"kerntime\" max=\"100.0\" rid=\"-/-/root/Processor/[% Privileged Time]\"/>"
					+ "<resource id=\"inttime\" max=\"100.0\" rid=\"-/-/root/Processor/[% Interrupt Time]\"/>"
					+ "<resource id=\"usertime\" max=\"100.0\" rid=\"-/-/root/Processor/[% User Time]\"/>"
					+ "<reaction params=\"usertime,kerntime\" severity=\"HIGH\">"
					+ "<arm><![CDATA[return usertime + kerntime > 20;]]></arm>"
					+ "<disarm><![CDATA[return env.getSecondsSinceLastArmed() > 20;]]></disarm>";
		Pattern pat = Pattern.compile(regex);
		Matcher mat = pat.matcher(text);
		List<String> ret = new LinkedList<String>();
		while(mat.find()) {
			ret.add(mat.group(1));
		}
		return ret.toArray(new String[ret.size()]);
	}

}
