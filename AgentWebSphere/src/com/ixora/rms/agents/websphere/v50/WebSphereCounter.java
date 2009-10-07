/*
 * Created on 22-May-2004
 */
package com.ixora.rms.agents.websphere.v50;

import com.ibm.websphere.pmi.PmiConstants;
import com.ibm.websphere.pmi.PmiDataInfo;
import com.ibm.websphere.pmi.client.PerfDescriptor;
import com.ixora.rms.CounterType;
import com.ixora.rms.agents.impl.Counter;

/**
 * @author Daniel Moraru
 */
public class WebSphereCounter extends Counter
		implements PmiConstants {
	/** Execution context */
	private WebSphereAgentContext context;
	/** WAS Id for this counter */
	private int wasId;

	/**
	 * @param pd
	 * @param di
	 * @param c
	 */
	public WebSphereCounter(PerfDescriptor pd,
			PmiDataInfo di, WebSphereAgentContext c) {
		super(di.getName(),
			c.getTranslatedText(di.getDescription(), pd.getModuleName()),
			getRmsCounterType(di.getType()),
			c.mapLevel(di.getLevel()));
		this.fAlternateName = c.getTranslatedText(di.getName(), pd.getModuleName());
		this.context = c;
		this.wasId = di.getId();
	}

	/**
	 * @param pd
	 * @param di
	 * @param c
	 */
	public WebSphereCounter(WebSphereCounter c) {
		super(c.fCounterId.toString(), c.fDescription, c.fType, c.fLevel);
		this.fAlternateName = c.fAlternateName;
		this.fDiscreet = c.fDiscreet;
		this.context = c.context;
		this.wasId = c.wasId;
	}

	/**
	 * @return the wasId.
	 */
	public int getWasId() {
		return wasId;
	}

	/**
	 * Maps the WAS counter type to RMS type
	 * @param type WAS counter type
	 * @return
	 */
	private static CounterType getRmsCounterType(int type) {
		switch(type) {
			case TYPE_INT:
				return CounterType.LONG;
			case TYPE_LONG:
				return CounterType.LONG;
			case TYPE_DOUBLE:
			default:
				return CounterType.DOUBLE;
		}
	}
}
