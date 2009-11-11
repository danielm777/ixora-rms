/*
 * Created on 16-Nov-2003
 */
package com.ixora.rms;

import java.util.BitSet;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.ixora.common.utils.Utils;
import com.ixora.common.xml.XMLUtils;
import com.ixora.common.xml.exception.XMLException;

/**
 * Monitored entity configuration data.
 * Once an entity has been configured the same class is
 * used to express configuration changes by nulifying (or
 * setting invalid values) all parameters that are not to be changed.
 * @author Daniel Moraru
 */
public final class EntityConfiguration extends MonitoringConfiguration {
	private static final long serialVersionUID = 2251309416061708345L;

	// bit set indexes with change information
	public static final int BIT_COUNTERS = 8;

	/** Monitored counters (collection of CounterId) */
	private HashSet<CounterId> fCounters;

	/**
	 * Default constructor to support XML.
	 */
	public EntityConfiguration() {
		super();
	}

	/**
	 * Default constructor to support XML.
	 */
	public EntityConfiguration(EntityConfiguration conf) {
		super(conf);
		this.fCounters = conf.fCounters;
	}

	/**
	 * EntityConfiguration constructor.
	 * This constructor can be used to set the monitoring level.
	 * @param ml the monitoring level
	 */
	public EntityConfiguration(MonitoringLevel ml) {
		super();
		this.monitoringLevel = ml;
	}

	/**
	 * EntityConfiguration constructor.
	 * @param counters
	 */
	public EntityConfiguration(Set<CounterId> counters) {
		super();
		this.fCounters = new HashSet<CounterId>(counters);
	}

	/**
	 * @return returns the monitored counters or null if none.
	 */
	public Set<CounterId> getMonitoredCountersIds() {
		return fCounters == null ? null : Collections.unmodifiableSet(fCounters);
	}

	/**
	 * @see com.ixora.common.xml.XMLExternalizable#fromXML(org.w3c.dom.Node)
	 */
	public void fromXML(Node node) throws XMLException {
		super.fromXML(node);
		Node n = XMLUtils.findChild(node, "counters");
		if(n != null) {
			List<Node> l = XMLUtils.findChildren(n, "counter");
			if(!Utils.isEmptyCollection(l)) {
				fCounters = new HashSet<CounterId>(l.size());
				for(Iterator<Node> iter = l.iterator(); iter.hasNext();) {
					n = iter.next();
					fCounters.add(new CounterId(XMLUtils.getText(n)));
				}
			}
		}
	}

	/**
	 * @see com.ixora.common.xml.XMLExternalizable#toXML(org.w3c.dom.Node)
	 */
	public void toXML(Node parent) throws XMLException {
		super.toXML(parent);
		Node config = XMLUtils.findChild(parent, "config");
		Document doc = parent.getOwnerDocument();
		Element el = doc.createElement("counters");
		config.appendChild(el);
		Element el2;
		CounterId counterId;
		if(fCounters != null) {
			for(Iterator<CounterId> iter = fCounters.iterator(); iter.hasNext();) {
				counterId = iter.next();
				el2 = doc.createElement("counter");
				el2.appendChild(doc.createTextNode(counterId.toString()));
				el.appendChild(el2);
			}
		}
	}

	/**
	 * @param counters the counters to monitor.
	 */
	public void setMonitoredCountersIds(Set<CounterId> counters) {
		if(counters == null) {
			this.fCounters = null;
		} else {
			this.fCounters = new HashSet<CounterId>(counters);
		}
	}

	/**
	 * @see java.lang.Object#clone()
	 */
	public Object clone() {
		EntityConfiguration conf =
			(EntityConfiguration)super.clone();
		return conf;
	}

	/**
	 * Applies the valid parameters found in <code>dtls</code>
	 * to this configuration.
	 * @param dtls the delta configuration to apply
	 * @return the bitset specifying the parameters that
	 * have changed
	 */
	public BitSet applyDelta(EntityConfiguration dtls) {
		BitSet ret = new BitSet(9);
		if(dtls.fCounters != null
				&& !dtls.fCounters.equals(this.fCounters)) {
			this.fCounters = dtls.fCounters;
			ret.set(BIT_COUNTERS);
		}
		BitSet superRet = super.applyDelta(dtls);
		int size = superRet.length();
		for(int i = 0; i < size; i++) {
			ret.set(i, superRet.get(i));
		}
		return ret;
	}

	/**
	 * Returns a configuration that represents the
	 * difference between this one and the given one.
	 * @param conf
	 * @return
	 */
	public EntityConfiguration getDelta(EntityConfiguration conf) {
		if(this.equals(conf)) {
			return null;
		}
		EntityConfiguration ret = new EntityConfiguration();
		if(conf.fCounters != null
				&& !conf.fCounters.equals(fCounters)) {
			ret.fCounters = conf.fCounters;
		}
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
		if(!(obj instanceof EntityConfiguration)) {
			return false;
		}
		EntityConfiguration that = (EntityConfiguration)obj;
		if(super.equals(obj)
				&& Utils.equals(fCounters, that.fCounters)) {
			return true;
		}
		return false;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		int ret = super.hashCode();
		if(fCounters != null) {
			ret ^= fCounters.hashCode();
		}
		return ret;
	}
}
