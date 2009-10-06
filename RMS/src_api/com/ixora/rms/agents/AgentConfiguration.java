/*
 * Created on 16-Nov-2003
 */
package com.ixora.rms.agents;

import java.util.BitSet;
import java.util.LinkedList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.ixora.common.utils.Utils;
import com.ixora.common.xml.XMLUtils;
import com.ixora.common.xml.exception.XMLException;
import com.ixora.rms.CustomConfiguration;
import com.ixora.rms.MonitoringConfiguration;
import com.ixora.rms.MonitoringLevel;

/**
 * Monitoring agent configuration data.
 * Once configured, an agent can be passed another instance
 * of this class to change parameters in the current configuration.
 * All null parameters will be left unchanged in the current configuration.
 * @author Daniel Moraru
 */
public class AgentConfiguration extends MonitoringConfiguration {
	/** The monitored system version */
	private String systemVersion;
	/** Host to monitor */
	private String monitoredHost;
	/**
	 * Deployment host. This is the name of the host where
	 * this agent was deployed.
	 */
	private String deploymentHost;
	/**
	 * Optional provider instances to enable.
	 */
	private String[] providerInstances;

	/**
	 * Default constructor to support XML.
	 */
	public AgentConfiguration() {
		super();
	}

	/**
	 * AgentConfiguration constructor.
	 * @param si the sampling interval
	 * @param globalSampling
	 * @param ml the monitoring level
	 * @param c the custom configuration object
	 */
	public AgentConfiguration(int si,
			boolean globalSampling, MonitoringLevel ml, CustomConfiguration c) {
		super(si, globalSampling, ml, c);
		if(ml != null) {
			setRecursiveMonitoringLevel(true);
		}
	}

	/**
	 * @return the custom agent configuration
	 */
	public AgentCustomConfiguration getAgentCustomConfiguration() {
		return (AgentCustomConfiguration)this.custom;
	}
	/**
	 * @return Returns the monitored system version.
	 */
	public String getSystemUnderObservationVersion() {
		return systemVersion;
	}
	/**
	 * @param systemVersion Sets the monitored system version.
	 */
	public void setSystemUnderObservationVersion(String systemVersion) {
		this.systemVersion = systemVersion;
	}

	/**
	 * @see com.ixora.common.xml.XMLExternalizable#fromXML(org.w3c.dom.Node)
	 */
	public void fromXML(Node node) throws XMLException {
		super.fromXML(node);
		Node n = XMLUtils.findChild(node, "version");
		if(n != null) {
			this.systemVersion = n.getFirstChild().getNodeValue();
		}
		n = XMLUtils.findChild(node, "providerInstances");
		if(n != null) {
			List<Node> lst = XMLUtils.findChildren(n, "providerInstance");
			List<String> tmp = new LinkedList<String>();
			for(Node n2 : lst) {
				String pi = XMLUtils.getText(n2);
				if(pi != null && pi.length() > 0) {
					tmp.add(pi);
				}
			}
			this.providerInstances = tmp.toArray(new String[tmp.size()]);
		}
	}

	/**
	 * @see com.ixora.common.xml.XMLExternalizable#toXML(org.w3c.dom.Node)
	 */
	public void toXML(Node parent) throws XMLException {
		super.toXML(parent);
		Node config = XMLUtils.findChild(parent, "config");
		Document doc = parent.getOwnerDocument();
		if(systemVersion != null) {
			Element el = doc.createElement("version");
			el.appendChild(doc.createTextNode(systemVersion));
			config.appendChild(el);
		}
		if(providerInstances != null) {
			Element el = doc.createElement("providerInstances");
			config.appendChild(el);
			for(String pi : providerInstances) {
				Element el2 = doc.createElement("providerInstance");
				el2.appendChild(doc.createTextNode(pi));
				el.appendChild(el2);
			}
		}
	}

	/**
	 * Applies the valid parameters found in <code>dtls</code>
	 * to this configuration.
	 * @param dtls the delta configuration to apply
	 * @return the bitset specifying the parameters that
	 * have changed
	 */
	public BitSet applyDelta(AgentConfiguration dtls) {
		return super.applyDelta(dtls);
	}

	/**
	 * Returns a configuration that represents the
	 * difference between this one and the given one.
	 * @param conf
	 * @return
	 */
	public AgentConfiguration getDelta(AgentConfiguration conf) {
		if(this.equals(conf)) {
			return null;
		}
		AgentConfiguration ret = new AgentConfiguration();
		super.getDelta(conf, ret);
		return ret;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if(obj == this) {
			return true;
		}
		if(!(obj instanceof AgentConfiguration)) {
			return false;
		}
		AgentConfiguration that = (AgentConfiguration)obj;
		if(super.equals(obj)
			&& Utils.equals(systemVersion, that.systemVersion)) {
			return true;
		}
		return false;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		int ret = super.hashCode();
		if(systemVersion != null) {
			ret ^= systemVersion.hashCode();
		}
		return ret;
	}

	/**
	 * @see java.lang.Object#clone()
	 */
	public Object clone() {
		AgentConfiguration conf =
			(AgentConfiguration)super.clone();
		return conf;
	}

	/**
	 * @return the host to monitor.
	 */
	public String getMonitoredHost() {
		return monitoredHost;
	}

	/**
	 * @param host the host to monitor.
	 */
	public void setMonitoredHost(String host) {
		this.monitoredHost = host;
	}

	/**
	 * @return the host where this agent is deployed.
	 */
	public String getDeploymentHost() {
		return deploymentHost;
	}

	/**
	 * @param host the host where this agent is deployed.
	 */
	public void setDeploymentHost(String host) {
		this.deploymentHost = host;
	}

	/**
	 * @return the providerInstances.
	 */
	public String[] getProviderInstances() {
		return providerInstances;
	}

	/**
	 * @param providerInstances the providerInstances to set.
	 */
	public void setProviderInstances(String[] providerInstances) {
		this.providerInstances = providerInstances;
	}

	/**
	 * Overriden to set the recursive flag
	 * @see com.ixora.rms.MonitoringConfiguration#setEnableAllCounters(boolean)
	 */
	public void setEnableAllCounters(boolean enableAllCounters) {
		super.setEnableAllCounters(enableAllCounters);
		setRecursiveEnableAllCounters(true);
	}

	/**
	 * Overriden to set the recursive flag
	 * @see com.ixora.rms.MonitoringConfiguration#setEnableAllCounters(java.lang.Boolean)
	 */
	public void setEnableAllCounters(Boolean enableAllCounters) {
		super.setEnableAllCounters(enableAllCounters);
		if(enableAllCounters != null) {
			setRecursiveEnableAllCounters(true);
		}
	}

	/**
	 * Overriden to set the recursive flag
	 * @see com.ixora.rms.MonitoringConfiguration#setMonitoringLevel(com.ixora.rms.MonitoringLevel)
	 */
	public void setMonitoringLevel(MonitoringLevel level) {
		super.setMonitoringLevel(level);
		if(level != null) {
			setRecursiveMonitoringLevel(true);
		}
	}
}
