/*
 * Created on 01-Sep-2004
 */
package com.ixora.common.plugin;

import java.io.File;
import java.io.FilenameFilter;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class loader based on regex.
 * @author Daniel Moraru
 */
public final class RegexClassLoader extends URLClassLoader {
    private boolean fParentLast;
    private String[] fClasspath;

	/**
     * Constructor.
     * @param classpath classpath fragments i.e. strings of the form
	 * "C:/lib/,C:/lib/(.*\.jar),C:/otherlib/other.jar" where the expression
	 * in the paranthesis will be treated as a regular expression.
	 * @param parentLast
	 * @throws MalformedURLException
	 * @throws URISyntaxException
     */
    public RegexClassLoader(String[] classpath, boolean parentLast) throws MalformedURLException, URISyntaxException{
        super(getURLs(classpath));
        this.fClasspath = classpath;
        this.fParentLast = parentLast;
    }

    /**
     * @param classpath
     * @return true if the given classpath is compatible with the classpath
     * covered by this loader
     */
    public boolean isCompatibleWithClasspath(String[] classpath) {
    	return Arrays.equals(fClasspath, classpath);
    }

    /**
     * @return the classpath that was used to create this loader
     */
    public String[] getClasspath() {
    	return fClasspath;
    }

    /**
     * @return the classpath that was used to create this loader
     */
    public String getClasspathAsString() {
    	return convertClasspathToString(fClasspath);
    }

    /**
     * @param cps
     * @return
     */
    public static String convertClasspathToString(String[] cps) {
    	StringBuilder buff = new StringBuilder();
    	for(String cp : cps) {
    		buff.append(cp).append(",");
    	}
    	if(buff.length() > 1) {
    		buff.deleteCharAt(buff.length() - 1);
    	}
    	return buff.toString();
    }

    /**
     * @param classpath
     * @return
     * @throws MalformedURLException
     * @throws URISyntaxException
     */
    private static URL[] getURLs(String[] classpath) throws MalformedURLException, URISyntaxException {
        List<URL> ret = new LinkedList<URL>();
        for(int i = 0; i < classpath.length; i++) {
	        StringTokenizer tok = new StringTokenizer(classpath[i], ",");
	        String element;
	        while(tok.hasMoreTokens()) {
	            element = tok.nextToken().trim();
	            if(isRegex(element)) {
	                List<String> lst = expandRegexPath(element);
	                if(lst != null) {
		                for(String s : lst) {
		                    ret.add(createFileURLForPath(s));
		                }
	                }
	            } else {
	                ret.add(createFileURLForPath(element));
	            }
	        }
        }
        return ret.toArray(new URL[ret.size()]);
    }

    /**
     * @param path
     * @return
     * @throws MalformedURLException
     * @throws URISyntaxException
     */
    private static URL createFileURLForPath(String path) throws MalformedURLException, URISyntaxException {
    	path = path.replace("\\", "/");
    	if(path.startsWith("/")) {
    		return new URL("file:" + path);
    	} else {
    		return new URL("file:/" + path);
    	}
    }

    /**
     * @return true if the given element represents a regular expression
     */
    private static boolean isRegex(String element) {
        int s = element.indexOf('(');
        int e = element.indexOf(')');
        if(s >= 0 && e > 0 && s < e) {
            return true;
        }
        return false;
    }

    /**
     * @return the list of path derived from the given regular expression file path
     */
    public static List<String> expandRegexPath(String element) {
        String tmp = element.substring(0, element.indexOf("("));
        File folder = new File(tmp);
        if(!folder.isDirectory()) {
            return null;
        }
        String regex = element.substring(element.indexOf('(') + 1,
                element.indexOf(')'));
        final Pattern pattern = Pattern.compile(regex);
        String[] lst = folder.list(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                Matcher matcher = pattern.matcher(name);
                return matcher.matches();
            }
        });
        List<String> ret = new ArrayList<String>(lst.length);
        for(int i = 0; i < lst.length; i++) {
            File file = new File(folder, lst[i]);
            ret.add(file.getAbsolutePath());
            //Debug.print(AgentClassLoader.class, file);
        }
        return ret;
    }

	/**
	 * @see java.lang.ClassLoader#findClass(java.lang.String)
	 */
	protected Class< ? > findClass(String name) throws ClassNotFoundException {
		//Debug.print(this, "find class... " + name);
		return super.findClass(name);
	}

	/**
	 * @see java.lang.ClassLoader#loadClass(java.lang.String, boolean)
	 */
	protected synchronized Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
		if(!fParentLast) {
			return super.loadClass(name, resolve);
		}
		Class c = findLoadedClass(name);
		if (c == null) {
		    try {
		    	c = findClass(name);
		    } catch (ClassNotFoundException e) {
		        c = super.loadClass(name, resolve);
		    }
		}
		if (resolve) {
		    resolveClass(c);
		}
		return c;
	}
}
