/*
 * Created on Nov 7, 2004
 */
package com.ixora.rms.ui.dataviewboard.handler;

import com.ixora.rms.ResourceId;
import com.ixora.rms.repository.DataView;
import com.ixora.rms.ui.EntityConfigurationPanel;
import com.ixora.rms.ui.artefacts.dashboard.DashboardSelectorPanel;
import com.ixora.rms.ui.artefacts.dataview.DataViewSelectorPanel;

/**
 * @author Daniel Moraru
 */
public interface DataViewPlotter extends
	DataViewSelectorPanel.Callback,
		DashboardSelectorPanel.Callback,
			EntityConfigurationPanel.Callback {
	/**
	 * Plots the given view. Used for views which are not
	 * found in the locator (i.e. created on the fly).
	 * @param context
	 * @param view
	 */
	void plot(ResourceId context, DataView view);

}
