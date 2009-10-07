/*
 * Created on 22-Dec-2004
 */
package com.ixora.rms.agents.websphere.v50;

import com.ibm.websphere.pmi.client.PerfLevelSpec;
import com.ixora.rms.MonitoringLevel;


/** Entity data */
public final class EntityData {
	PerfLevelSpec spec;
	int waslevel;
	MonitoringLevel level;

	EntityData(PerfLevelSpec spec, int waslevel, MonitoringLevel level) {
		this.spec = spec;
		this.waslevel = waslevel;
		this.level = level;
	}
}