/*
 * Created on 25-Jun-2005
 */
package com.ixora.rms.repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.ixora.common.utils.Utils;
import com.ixora.common.xml.XMLUtils;
import com.ixora.common.xml.exception.XMLAttributeMissing;
import com.ixora.common.xml.exception.XMLException;
import com.ixora.common.xml.exception.XMLNodeMissing;
import com.ixora.rms.MonitoringLevel;
import com.ixora.rms.agents.AgentLocation;

/**
 * VersionableAgentInstallationData. Agent installation data that applies for one
 * system under observation version.
 */
public final class VersionableAgentInstallationData extends VersionableAgentArtefactAbstract {
	private static final long serialVersionUID = -5508095481206689344L;
	/** There is just one version data per agent version and this is the name */
	public static final String ARTEFACT_NAME = "data";
    /** Supported states */
    private AgentLocation[] locations;
    /** Supported monitoring levels */
    private MonitoringLevel[] levels;
    /**
     * The jars containing agent's code.
     * It must be relative to the <code>application.home</code> system property. It must be not null
     * if the agent requires it's own classloader.
     */
    private String[] jars;
    /** Jar with the UI code */
    private String uiJar;
    /** Agent custom configuration panel class */
    private String configPanelClass;
    /** Configuration values */
    private Map<String, String> configValues;
	/** Default monitoring level */
    private MonitoringLevel defaultLevel;
    /**
     * Native libraries required by the agent.
     * It must be relative to the <code>application.home</code> system property.
     */
    private String[] natlibs;

    /**
     * Default constructor to support XML.
     */
    public VersionableAgentInstallationData() {
        super();
    };

    /**
     * @param configPanelClass
     * @param locations
     * @param levels
     * @param defaultLevelIdx
     * @param jars
     * @param uiJar
     * @param natlibs
     * @param configVals
     */
    public VersionableAgentInstallationData(
        String configPanelClass,
        AgentLocation[] locations,
        MonitoringLevel[] levels,
        int defaultLevelIdx,
        String[] jars,
        String uiJar,
        String[] natlibs,
        Map<String, String> configVals) {
        super();
        this.configPanelClass = configPanelClass;
        this.locations = locations;
        this.levels = levels;
        if(levels != null) {
            this.defaultLevel = levels[defaultLevelIdx];
        }
        this.jars = jars;
        this.uiJar = uiJar;
        this.natlibs = natlibs;
        this.configValues = configVals;
    }

    /**
     * @param parent
     * @throws XMLException
     */
    public void toXML(Node parentNode) throws XMLException {
        Document doc = parentNode.getOwnerDocument();
        Element node = doc.createElement("versionItem");
        parentNode.appendChild(node);
        Element el = doc.createElement("locations");
        node.appendChild(el);
        Element el2;
        for(int i = 0; i < locations.length; i++) {
            el2 = doc.createElement("location");
            el.appendChild(el2);
            el2.appendChild(doc.createTextNode(String.valueOf(locations[i].getKey())));
        }
        if(this.levels != null) {
            el = doc.createElement("levels");
            node.appendChild(el);
            MonitoringLevel lev;
            for(int i = 0; i < levels.length; i++) {
                lev = levels[i];
                el2 = doc.createElement("level");
                if(defaultLevel == lev) {
                    el2.setAttribute("default", String.valueOf(true));
                }
                el.appendChild(el2);
                el2.appendChild(doc.createTextNode(String.valueOf(lev.getKey())));
            }
        }
        if(!Utils.isEmptyArray(this.jars)) {
            el = doc.createElement("jars");
            node.appendChild(el);
            for(int i = 0; i < jars.length; i++) {
                el2 = doc.createElement("jar");
                el.appendChild(el2);
                el2.appendChild(doc.createTextNode(jars[i]));
            }
        }
        if(configPanelClass != null) {
            el = doc.createElement("customconfigpanel");
            node.appendChild(el);
            el.appendChild(doc.createTextNode(configPanelClass));
        }
        if(!Utils.isEmptyMap(configValues)) {
        	el = doc.createElement("defaultconfig");
        	node.appendChild(el);
        	for(Map.Entry<String, String> entry : configValues.entrySet()) {
				Element el3 = doc.createElement("property");
				el.appendChild(el3);
				el3.setAttribute("name", entry.getKey());
				el3.setAttribute("value", entry.getValue());
			}
        }
        if(uiJar != null) {
            el = doc.createElement("uiJar");
            node.appendChild(el);
            el.appendChild(doc.createTextNode(uiJar));
        }
        if(!Utils.isEmptyArray(this.natlibs)) {
            el = doc.createElement("natlibs");
            node.appendChild(el);
            for(int i = 0; i < natlibs.length; i++) {
                el2 = doc.createElement("natlib");
                el.appendChild(el2);
                el2.appendChild(doc.createTextNode(natlibs[i]));
            }
        }

        super.toXML(node);
    }

    /**
     * @param node
     * @throws XMLException
     */
    public void fromXML(Node node) throws XMLException {
    	super.fromXML(node);
        Node n = XMLUtils.findChild(node, "locations");
        if(n == null) {
            throw new XMLNodeMissing("locations");
        }
        List<Node> m = XMLUtils.findChildren(n, "location");
        if(m.size() == 0) {
            throw new XMLNodeMissing("location");
        }
        List<AgentLocation> lst = new ArrayList<AgentLocation>(m.size());
        for(Iterator<Node> iter = m.iterator(); iter.hasNext();) {
             n = iter.next();
             AgentLocation al = AgentLocation.resolve(
                Integer.parseInt(XMLUtils.getText(n)));
             if(al != null) {
                lst.add(al);
             }
        }
        this.locations = (AgentLocation[])lst.toArray(new AgentLocation[lst.size()]);
        // levels are optional
        n = XMLUtils.findChild(node, "levels");
        if(n != null) {
            m = XMLUtils.findChildren(n, "level");
            if(m.size() != 0) {
                List<MonitoringLevel> lstml = new ArrayList<MonitoringLevel>(m.size());
                MonitoringLevel ml;
                for(Iterator<Node> iter = m.iterator(); iter.hasNext();) {
                     n = iter.next();
                     ml = MonitoringLevel.resolve(
                        Integer.parseInt(XMLUtils.getText(n)));
                     if(ml != null) {
                        lstml.add(ml);
                        // check if it's default
                        Attr def = XMLUtils.findAttribute(n, "default");
                        if(def != null) {
                            this.defaultLevel = ml;
                        }
                     }
                }
                // sort levels
                Collections.sort(lstml);
                this.levels = lstml.toArray(new MonitoringLevel[lstml.size()]);
            }
        }
        // optional
        n = XMLUtils.findChild(node, "customconfigpanel");
        if(n != null) {
            this.configPanelClass = XMLUtils.getText(n);
        }
        // optional
        n = XMLUtils.findChild(node, "defaultconfig");
        if(n != null) {
            m = XMLUtils.findChildren(n, "property");
            if(m.size() != 0) {
                for(Iterator<Node> iter = m.iterator(); iter.hasNext();) {
                    n = iter.next();
                    Attr a = XMLUtils.findAttribute(n, "name");
                    if(a == null) {
                    	throw new XMLAttributeMissing("name");
                    }
                    String prop = a.getValue();
                    a = XMLUtils.findAttribute(n, "value");
                    if(a == null) {
                    	throw new XMLAttributeMissing("value");
                    }
                    if(this.configValues == null) {
                        this.configValues = new HashMap<String, String>();
                    }
                    this.configValues.put(prop, a.getValue());        
                }
            }        	
        }        
        // optional
        n = XMLUtils.findChild(node, "jars");
        if(n != null) {
            m = XMLUtils.findChildren(n, "jar");
            if(m.size() == 0) {
                throw new XMLNodeMissing("jar");
            }
            List<String> jlist = new LinkedList<String>();
            String j;
            for(Iterator<Node> iter = m.iterator(); iter.hasNext();) {
                 n = iter.next();
                 j = XMLUtils.getText(n);
                 if(j != null && j.length() > 0) {
                    jlist.add(j);
                 }
            }
            int size = jlist.size();
            if(size > 0) {
                jars = jlist.toArray(new String[size]);
            }
        }
        // optional
        n = XMLUtils.findChild(node, "uiJar");
        if(n != null) {
            this.uiJar = XMLUtils.getText(n);
        }
        // natlibs
        n = XMLUtils.findChild(node, "natlibs");
        if(n != null) {
            m = XMLUtils.findChildren(n, "natlib");
            if(m.size() == 0) {
                throw new XMLNodeMissing("natlib");
            }
            List<String> jlist = new LinkedList<String>();
            String j;
            for(Iterator<Node> iter = m.iterator(); iter.hasNext();) {
                 n = iter.next();
                 j = XMLUtils.getText(n);
                 if(j != null && j.length() > 0) {
                    jlist.add(j);
                 }
            }
            int size = jlist.size();
            if(size > 0) {
                natlibs = jlist.toArray(new String[size]);
            }
        }
    }

    /**
     * @return
     */
    public String getConfigPanelClass() {
        return configPanelClass;
    }

    /**
     * @return the supported locations
     */
    public AgentLocation[] getLocations() {
        return locations;
    }

    /**
     * @return the jar paths (relative to application.home).
     */
    public String[] getJars() {
        return jars;
    }

   /**
     * @return the native libraries paths (relative to application.home).
     */
    public String[] getNativeLibraries() {
        return natlibs;
    }

    /**
     * @return the ui jar.
     */
    public String getUIJar() {
        return uiJar;
    }

    /**
     * @return the supported monitoring levels
     */
    public MonitoringLevel[] getMonitoringLevels() {
        return levels;
    }

    /**
     * @return the default location for this agent
     */
    public MonitoringLevel getDefaultMonitoringLevel() {
        return defaultLevel;
    }

    /**
     * @param location
     * @return true if the <code>location</code> is supported
     */
    public boolean supports(AgentLocation location) {
        for (int i = 0; i < this.locations.length; i++) {
            if(location == this.locations[i]) {
                return true;
            }
        }
        return false;
    }

    /**
     * @return the default location for this agent
     */
    public AgentLocation getDefaultLocation() {
        if(supports(AgentLocation.LOCAL)) {
            return AgentLocation.LOCAL;
        }
        return AgentLocation.REMOTE;
    }

    /**
     * @param level
     * @return true if the <code>level</code> is supported
     */
    public boolean supports(MonitoringLevel level) {
        if(levels == null) {
            return false;
        }
        for (int i = 0; i < this.levels.length; i++) {
            if(level == this.levels[i]) {
                return true;
            }
        }
        return false;
    }

    /**
     * @return
     */
    public Map<String, String> getConfigValues() {
    	if(configValues == null) {
    		return null;
    	} else {
    		return Collections.unmodifiableMap(configValues);
    	}
	}
    
    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj) {
        if(obj == this) {
            return true;
        }
        if(!(obj instanceof VersionableAgentInstallationData)) {
            return false;
        }
        VersionableAgentInstallationData that = (VersionableAgentInstallationData)obj;
        return Utils.equals(this.configPanelClass, that.configPanelClass)
            && Utils.equals(this.defaultLevel, that.defaultLevel)
            && Utils.equals(this.jars, that.jars)
            && Utils.equals(this.levels, that.levels)
            && Utils.equals(this.locations, that.locations)
            && Utils.equals(this.uiJar, that.uiJar)
            && Utils.equals(this.configValues, that.configValues);
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        int hc = 13;
        hc = Utils.hashCode(hc, this.configPanelClass);
        hc = Utils.hashCode(hc, this.defaultLevel);
        hc = Utils.hashCode(hc, this.uiJar);
        hc = Utils.hashCode(hc, this.jars);
        hc = Utils.hashCode(hc, this.levels);
        hc = Utils.hashCode(hc, this.locations);
        hc = Utils.hashCode(hc, this.configValues);
        return hc;
    }

	/**
	 * @see com.ixora.rms.repository.VersionableAgentArtefact#getArtefactName()
	 */
	public String getArtefactName() {
		return ARTEFACT_NAME;
	}
}
