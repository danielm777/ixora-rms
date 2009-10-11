/*
 * Created on 16-Nov-2003
 */
package com.ixora.rms;

import java.io.Serializable;
import java.util.BitSet;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.ixora.common.utils.Utils;
import com.ixora.common.xml.XMLExternalizable;
import com.ixora.common.xml.XMLUtils;
import com.ixora.common.xml.exception.XMLException;

/**
 * Monitoring agent configuration data.
 */
public abstract class MonitoringConfiguration
	implements Serializable, XMLExternalizable, Cloneable {
	private static final long serialVersionUID = 3175708342695870857L;
	// bits for changes	to the runtime settings
	public static final int BIT_MONITORING_LEVEL = 0;
	public static final int BIT_SAMPLING_INTERVAL = 1;
	public static final int BIT_ENABLE_ALL_COUNTERS = 2;
	public static final int BIT_CUSTOM = 3;
	public static final int BIT_RECURSIVE_MONITORING_LEVEL = 4;
	public static final int BIT_RECURSIVE_ENABLE_ALL_COUNTERS = 5;
	public static final int BIT_GLOBAL_SAMPLING_INTERVAL = 6;
	public static final int BIT_REFRESH_INTERVAL = 7;

// runtime settings
	/** Sampling interval */
	protected Integer samplingInterval;
	/** Monitoring level */
	protected MonitoringLevel monitoringLevel;
	/** Custom configuration object */
	protected CustomConfiguration custom;
	/** Whether or not the sampling interval is the global one or not */
	protected Boolean globalSamplingInterval;
	/** Whether or not to enables all counters */
	protected Boolean enableAllCounters;
	/** Whether or not to apply monitoring level recursively */
	protected Boolean recursiveMonitoringLevel;
	/** Whether or not to apply the enabled all counters flag recursively */
	protected Boolean recursiveEnableAllCounters;
	/**
	 * The interval expressed as multiples of the sampling
	 * interval (e.g if sampling interval is 5 and
	 * this parameter is 2 the children of this entity will
	 * be refreshed every 10 seconds)
	 */
	private Integer fRefrehInterval;

// activation settings
	/** True to use a private collector thread */
	protected Boolean usePrivateCollector;

	/**
	 * Default constructor to support XML and allow for construction
	 * of an empty configuration used for expressing configuration deltas.
	 */
	public MonitoringConfiguration() {
		super();
	}

	/**
	 * Constructor.
	 * @param conf
	 */
	public MonitoringConfiguration(MonitoringConfiguration conf) {
		this.samplingInterval = conf.samplingInterval;
		this.custom = conf.custom;
		this.enableAllCounters = conf.enableAllCounters;
		this.globalSamplingInterval = conf.globalSamplingInterval;
		this.monitoringLevel = conf.monitoringLevel;
		this.recursiveEnableAllCounters = conf.recursiveEnableAllCounters;
		this.recursiveMonitoringLevel = conf.recursiveMonitoringLevel;
		this.usePrivateCollector = conf.usePrivateCollector;
	}

	/**
	 * MonitoringConfiguration constructor.
	 * The global sampling interval flag will be set to true.
	 * @param ml the monitoring level
	 * @param c the custom configuration object
	 */
	public MonitoringConfiguration(
			MonitoringLevel ml,
			CustomConfiguration c) {
		this(-1, true, ml, c);
	}

	/**
	 * MonitoringConfiguration constructor.
	 * @param si the sampling interval
	 * @param globalSampling
	 * @param ml the monitoring level
	 * @param c the custom configuration object
	 */
	public MonitoringConfiguration(
			int si,
			boolean globalSampling,
			MonitoringLevel ml,
			CustomConfiguration c) {
		super();
		if(si > 0) {
			this.samplingInterval = new Integer(si);
			this.globalSamplingInterval = Boolean.valueOf(globalSampling);
		} else {
			this.globalSamplingInterval = Boolean.TRUE;
		}
		this.monitoringLevel = ml;
		this.custom = c;
	}

	/**
	 * @return true if a private collector must be used
	 */
	public boolean usePrivateCollector() {
		return usePrivateCollector != null && usePrivateCollector.booleanValue();
	}

	/**
	 * @param usePrivateCollector
	 */
	public void setUsePrivateCollector(boolean usePrivateCollector) {
		this.usePrivateCollector = Boolean.valueOf(usePrivateCollector);
	}

	/**
	 * Returns the sampling interval.
	 * @return int
	 */
	public Integer getSamplingInterval() {
		return samplingInterval;
	}

	/**
	 * Returns the custom configuration object.
	 * @return CustomConfiguration
	 */
	public CustomConfiguration getCustom() {
		return custom;
	}

	/**
	 * @return
	 */
	public MonitoringLevel getMonitoringLevel() {
		return monitoringLevel;
	}

	/**
	 * Sets the custom configuration.
	 * @param dtls
	 */
	public void setCustom(CustomConfiguration dtls) {
		custom = dtls;
	}

	/**
	 * Sets the monitoring level.
	 * @param level
	 */
	public void setMonitoringLevel(MonitoringLevel level) {
		monitoringLevel = level;
	}

	/**
	 * Sets the sampling interval.
	 * @param i if negative the global sampling interval will
	 * be used
	 */
	public void setSamplingInterval(int i) {
		if(i > 0) {
			samplingInterval = new Integer(i);
		}
	}

	/**
	 * Sets the sampling interval.
	 * @param i if negative the global sampling interval will
	 * be used
	 */
	public void setSamplingInterval(Integer i) {
		samplingInterval = i;
		if(i != null && i.intValue() < 0) {
			samplingInterval = null;
		}
	}

	/**
	 * @return the globalSamplingInterval
	 */
	public boolean isGlobalSamplingInterval() {
		return globalSamplingInterval != null && globalSamplingInterval.booleanValue();
	}

	/**
	 * @return true if the sampling interval was set for this configuration
	 */
	public boolean isSamplingIntervalSet() {
		return this.globalSamplingInterval != null || this.samplingInterval != null;
	}

	/**
	 * @return true if this configuration has recursive parameters set
	 */
	public boolean hasRecursiveSettings() {
		return ((recursiveEnableAllCounters != null && recursiveEnableAllCounters.booleanValue())
				|| (recursiveMonitoringLevel != null && recursiveMonitoringLevel.booleanValue()));
	}

	/**
	 * @param globalSamplingInterval
	 */
	public void setGlobalSamplingInterval(boolean globalSamplingInterval) {
		this.globalSamplingInterval = Boolean.valueOf(globalSamplingInterval);
	}

	/**
	 * @param globalSamplingInterval
	 */
	public void setGlobalSamplingInterval(Boolean globalSamplingInterval) {
		this.globalSamplingInterval = globalSamplingInterval;
	}

	/**
	 * @return true if the sampling interval is set
	 * to a valid value
	 */
	public boolean hasValidSamplingInterval() {
		return this.samplingInterval != null && this.samplingInterval.intValue() > 0;
	}

	/**
	 * @return the enableAllCounters.
	 */
	public Boolean getEnableAllCounters() {
		return enableAllCounters;
	}

	/**
	 * @param enableAllCounters the enableAllCounters to set.
	 */
	public void setEnableAllCounters(boolean enableAllCounters) {
		this.enableAllCounters = Boolean.valueOf(enableAllCounters);
	}

	/**
	 * @param enableAllCounters the enableAllCounters to set.
	 */
	public void setEnableAllCounters(Boolean enableAllCounters) {
		this.enableAllCounters = enableAllCounters;
	}

	/**
	 * @return true if the monitoring level must be passed recursively.
	 */
	public Boolean isRecursiveMonitoringLevel() {
		return recursiveMonitoringLevel;
	}

	/**
	 * Sets the recursive flag for monitoring level.
	 * @param recursive
	 */
	public void setRecursiveMonitoringLevel(boolean recursive) {
		this.recursiveMonitoringLevel = Boolean.valueOf(recursive);
	}

	/**
	 * Sets the recursive flag for monitoring level.
	 * @param recursive
	 */
	public void setRecursiveMonitoringLevel(Boolean recursive) {
		this.recursiveMonitoringLevel = recursive;
	}

	/**
	 * @return true if the enable all counters flag must be passed recursively.
	 */
	public Boolean isRecursiveEnableAllCounters() {
		return recursiveEnableAllCounters;
	}

	/**
	 * Sets the recursive flag for enable all counters flag.
	 * @param recursive
	 */
	public void setRecursiveEnableAllCounters(boolean recursive) {
		this.recursiveEnableAllCounters = Boolean.valueOf(recursive);
	}

	/**
	 * Sets the recursive flag for enable all counters flag.
	 * @param recursive
	 */
	public void setRecursiveEnableAllCounters(Boolean recursive) {
		this.recursiveEnableAllCounters = recursive;
	}

	/**
	 * @return the interval at which the subtree is refreshed.
	 */
	public Integer getRefreshInterval() {
		return fRefrehInterval;
	}

	/**
	 * @param refrehInterval the interval at which the subtree is refreshed.
	 */
	public void setRefreshInterval(Integer refrehInterval) {
		fRefrehInterval = refrehInterval;
	}

	/**
	 * Applies the valid parameters found in <code>dtls</code>
	 * to this configuration.<br>
	 * Only runtime settings are used when applying delta.
	 * @param dtls the delta configuration to apply
	 * @return the bitset specifying the parameters that
	 * have changed
	 */
	public BitSet applyDelta(MonitoringConfiguration dtls) {
		BitSet ret = new BitSet(8);
		if(dtls.monitoringLevel != null
				&& !dtls.monitoringLevel.equals(this.monitoringLevel)) {
			this.monitoringLevel = dtls.monitoringLevel;
			ret.set(BIT_MONITORING_LEVEL);
		}
		if(dtls.samplingInterval != null
				&& !dtls.samplingInterval.equals(this.samplingInterval)) {
			this.samplingInterval = dtls.samplingInterval;
			ret.set(BIT_SAMPLING_INTERVAL);
		}
		if(dtls.globalSamplingInterval != null
				&& !dtls.globalSamplingInterval.equals(this.globalSamplingInterval)) {
			this.globalSamplingInterval = dtls.globalSamplingInterval;
			ret.set(BIT_GLOBAL_SAMPLING_INTERVAL);
		}
		if(dtls.enableAllCounters != null
				&& !dtls.enableAllCounters.equals(this.enableAllCounters)) {
			this.enableAllCounters = dtls.enableAllCounters;
			ret.set(BIT_ENABLE_ALL_COUNTERS);
		}
		if(dtls.recursiveMonitoringLevel != null
				&& !dtls.recursiveMonitoringLevel.equals(this.recursiveMonitoringLevel)) {
			this.recursiveMonitoringLevel = dtls.recursiveMonitoringLevel;
			ret.set(BIT_RECURSIVE_MONITORING_LEVEL);
		}
		if(dtls.recursiveEnableAllCounters != null
				&& !dtls.recursiveEnableAllCounters.equals(this.recursiveEnableAllCounters)) {
			this.recursiveEnableAllCounters = dtls.recursiveEnableAllCounters;
			ret.set(BIT_RECURSIVE_ENABLE_ALL_COUNTERS);
		}
		if(dtls.custom != null
				&& !dtls.custom.equals(this.custom)) {
			this.custom = dtls.custom;
			ret.set(BIT_CUSTOM);
		}
		if(dtls.fRefrehInterval != null
				&& !dtls.fRefrehInterval.equals(this.fRefrehInterval)) {
			this.fRefrehInterval = dtls.fRefrehInterval;
			ret.set(BIT_REFRESH_INTERVAL);
		}
		return ret;
	}

	/**
	 * Returns a configuration that represents the
	 * difference between this one and the given one.<br>
	 * Only runtime settings are used to calculate deltas.
	 * @param conf
	 * @param confOut out parameter
	 * @return
	 */
	protected void getDelta(MonitoringConfiguration conf, MonitoringConfiguration confOut) {
		if(this.equals(conf)) {
			return;
		}
		if(conf.custom != null
				&& !conf.custom.equals(custom)) {
			confOut.custom = conf.custom;
		}
		if(conf.monitoringLevel != null
				&& conf.monitoringLevel != monitoringLevel) {
			confOut.monitoringLevel = conf.monitoringLevel;
		}
		if(conf.samplingInterval != null
				&& !conf.samplingInterval.equals(samplingInterval)) {
			confOut.samplingInterval = conf.samplingInterval;
		}
		if(conf.recursiveEnableAllCounters != null
				&& !conf.recursiveEnableAllCounters.equals(recursiveEnableAllCounters)) {
			confOut.recursiveEnableAllCounters = conf.recursiveEnableAllCounters;
		}
		if(conf.recursiveMonitoringLevel != null
				&& !conf.recursiveMonitoringLevel.equals(recursiveMonitoringLevel)) {
			confOut.recursiveMonitoringLevel = conf.recursiveMonitoringLevel;
		}
		if(conf.globalSamplingInterval != null
				&& !conf.globalSamplingInterval.equals(globalSamplingInterval)) {
			confOut.globalSamplingInterval = conf.globalSamplingInterval;
		}
		if(conf.fRefrehInterval != null
				&& !conf.fRefrehInterval.equals(fRefrehInterval)) {
			confOut.fRefrehInterval = conf.fRefrehInterval;
		}
	}

	/**
	 * @see java.lang.Object#clone()
	 */
	public Object clone() {
		try {
			MonitoringConfiguration conf =
				(MonitoringConfiguration)super.clone();
			if(custom != null) {
				conf.custom = (CustomConfiguration)custom.clone();
			}
			return conf;
		}catch(CloneNotSupportedException e) {
			// impossible
			throw new InternalError();
		}
	}

	/**
	 * @see com.ixora.common.xml.XMLExternalizable#fromXML(org.w3c.dom.Node)
	 */
	public void fromXML(Node node) throws XMLException {
		Node n = XMLUtils.findChild(node, "level");
		if(n != null) {
			this.monitoringLevel = MonitoringLevel.resolve(
				Integer.parseInt(n.getFirstChild().getNodeValue()));
		}
		n = XMLUtils.findChild(node, "sampling");
		if(n != null) {
			this.samplingInterval = Integer.valueOf(n.getFirstChild().getNodeValue());
			if(this.samplingInterval.intValue() < 0) {
				this.globalSamplingInterval = Boolean.TRUE;
			} else {
				this.globalSamplingInterval = Boolean.FALSE;
			}
		}
		n = XMLUtils.findChild(node, "recursiveMonitoringLevel");
		if(n != null) {
			this.recursiveMonitoringLevel = Boolean.valueOf(Utils.parseBoolean(XMLUtils.getText(n)));
		}
		n = XMLUtils.findChild(node, "recursiveEnableAllCounters");
		if(n != null) {
			this.recursiveEnableAllCounters = Boolean.valueOf(Utils.parseBoolean(XMLUtils.getText(n)));
		}
		n = XMLUtils.findChild(node, "enableAllCounters");
		if(n != null) {
			this.enableAllCounters = Boolean.valueOf(Utils.parseBoolean(XMLUtils.getText(n)));
		}
		n = XMLUtils.findChild(node, "usePrivateCollector");
		if(n != null) {
			this.usePrivateCollector = Boolean.valueOf(Utils.parseBoolean(XMLUtils.getText(n)));
		}
		n = XMLUtils.findChild(node, "config");
		if(n != null) {
			try {
				this.custom = (CustomConfiguration)XMLUtils.readObject(null, n);
			} catch(XMLException e) {
				throw e;
			} catch(Exception e) {
				throw new XMLException(e);
			}
		}
		n = XMLUtils.findChild(node, "refreshInterval");
		if(n!= null) {
			fRefrehInterval = Integer.parseInt(XMLUtils.getText(n));
		}
	}

	/**
	 * @see com.ixora.common.xml.XMLExternalizable#toXML(org.w3c.dom.Node)
	 */
	public void toXML(Node parent) throws XMLException {
		Document doc = parent.getOwnerDocument();
		Element el = doc.createElement("config");
		parent.appendChild(el);
		Element el2;
		if(monitoringLevel != null) {
			el2 = doc.createElement("level");
			el2.appendChild(doc.createTextNode(String.valueOf(monitoringLevel.getKey())));
			el.appendChild(el2);
		}
		if(this.samplingInterval != null) {
			el2 = doc.createElement("sampling");
			el.appendChild(el2);
			if(globalSamplingInterval == null || !globalSamplingInterval.booleanValue()) {
				el2.appendChild(doc.createTextNode(String.valueOf(samplingInterval)));
			} else {
				el2.appendChild(doc.createTextNode("-1"));
			}
		}
		if(recursiveMonitoringLevel != null) {
			el2 = doc.createElement("recursiveMonitoringLevel");
			el2.appendChild(doc.createTextNode(String.valueOf(recursiveMonitoringLevel)));
			el.appendChild(el2);
		}
		if(recursiveEnableAllCounters != null) {
			el2 = doc.createElement("recursiveEnableAllCounters");
			el2.appendChild(doc.createTextNode(String.valueOf(recursiveEnableAllCounters)));
			el.appendChild(el2);
		}
		if(enableAllCounters != null) {
			el2 = doc.createElement("enableAllCounters");
			el2.appendChild(doc.createTextNode(String.valueOf(enableAllCounters)));
			el.appendChild(el2);
		}
		if(custom != null) {
			XMLUtils.writeObject(null, el, custom);
		}
		if(usePrivateCollector != null) {
			el2 = doc.createElement("usePrivateCollector");
			el2.appendChild(doc.createTextNode(String.valueOf(usePrivateCollector)));
			el.appendChild(el2);
		}
		if(fRefrehInterval != null) {
			el2 = doc.createElement("refreshInterval");
			el2.appendChild(doc.createTextNode(fRefrehInterval.toString()));
			el.appendChild(el2);
		}
	}

	/**
	 * Only runtime settings are used to calculate object equality.
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if(obj == this) {
			return true;
		}
		if(!(obj instanceof MonitoringConfiguration)) {
			return false;
		}
		MonitoringConfiguration that = (MonitoringConfiguration)obj;
		if(Utils.equals(monitoringLevel, that.monitoringLevel)
				&& Utils.equals(recursiveMonitoringLevel, that.recursiveMonitoringLevel)
				&& Utils.equals(recursiveEnableAllCounters, that.recursiveEnableAllCounters)
				&& Utils.equals(samplingInterval, that.samplingInterval)
				&& Utils.equals(globalSamplingInterval, that.globalSamplingInterval)
				&& Utils.equals(enableAllCounters, that.enableAllCounters)
				&& Utils.equals(custom, that.custom)
				&& Utils.equals(fRefrehInterval, that.fRefrehInterval)) {
			return true;
		}
		return false;
	}

	/**
	 * Only runtime settings are used to calculate hash code.
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		int ret = 0;
		if(monitoringLevel != null) {
			ret ^= monitoringLevel.hashCode();
		}
		if(samplingInterval != null) {
			ret ^= samplingInterval.hashCode();
		}
		if(enableAllCounters != null) {
			ret ^= enableAllCounters.hashCode();
		}
		if(globalSamplingInterval != null) {
			ret ^= globalSamplingInterval.hashCode();
		}
		if(custom != null) {
			ret ^= custom.hashCode();
		}
		if(recursiveEnableAllCounters != null) {
			ret ^= recursiveMonitoringLevel.hashCode();
		}
		if(recursiveMonitoringLevel != null) {
			ret ^= recursiveMonitoringLevel.hashCode();
		}
		if(fRefrehInterval != null) {
			ret ^= fRefrehInterval.hashCode();
		}
		return ret;
	}

}
