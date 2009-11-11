/*
 * Created on 31-Oct-2004
 */
package com.ixora.rms.ui;

import java.awt.LayoutManager;

import javax.swing.JPanel;

import com.ixora.rms.ui.artefacts.dashboard.DashboardSelectorPanel;
import com.ixora.rms.ui.artefacts.dataview.DataViewSelectorPanel;

/**
 * @author Daniel Moraru
 */
public abstract class OperationsPanel extends JPanel {
	private static final long serialVersionUID = 8651626456047246203L;
	protected DataViewSelectorPanel panelViews;
	protected DashboardSelectorPanel panelDashboards;

    /**
     * Constructor.
     * @param layout
     */
    protected OperationsPanel(LayoutManager layout) {
        super(layout);
    }

	/**
	 * @return the data views panel
	 */
	public DataViewSelectorPanel getPanelViews() {
		return panelViews;
	}

	/**
	 * @return the entity query groups panel
	 */
	public DashboardSelectorPanel getPanelDashboards() {
		return panelDashboards;
	}
}
