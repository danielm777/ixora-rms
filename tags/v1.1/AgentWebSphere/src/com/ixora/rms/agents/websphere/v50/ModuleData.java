/*
 * Created on 22-Dec-2004
 */
package com.ixora.rms.agents.websphere.v50;

import com.ibm.websphere.pmi.PmiModuleConfig;


/** Pmi module data */
public final class ModuleData {
	PmiModuleConfig config;

	ModuleData(PmiModuleConfig c) {
		config = c;
	}
}