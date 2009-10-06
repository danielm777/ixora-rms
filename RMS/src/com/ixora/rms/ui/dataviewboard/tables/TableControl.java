/*
 * Created on 17-Oct-2004
 */
package com.ixora.rms.ui.dataviewboard.tables;

import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JToggleButton;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import com.ixora.rms.ResourceId;
import com.ixora.common.ConfigurationMgr;
import com.ixora.common.MessageRepository;
import com.ixora.common.ui.UIConfiguration;
import com.ixora.common.ui.UIExceptionMgr;
import com.ixora.common.ui.UIFactoryMgr;
import com.ixora.common.ui.UIUtils;
import com.ixora.common.utils.Utils;
import com.ixora.rms.CounterDescriptor;
import com.ixora.rms.client.locator.SessionArtefactInfoLocator;
import com.ixora.rms.dataengine.Cube;
import com.ixora.rms.dataengine.QueryResult;
import com.ixora.rms.dataengine.definitions.FunctionDef;
import com.ixora.rms.dataengine.definitions.ParamDef;
import com.ixora.rms.dataengine.definitions.QueryDef;
import com.ixora.rms.dataengine.definitions.ResourceDef;
import com.ixora.rms.dataengine.definitions.ValueFilterDef;
import com.ixora.rms.dataengine.definitions.ValueFilterRuleDef;
import com.ixora.rms.exception.RMSException;
import com.ixora.rms.repository.DataView;
import com.ixora.rms.services.ReactionLogService;
import com.ixora.rms.ui.dataviewboard.DataViewControlContext;
import com.ixora.rms.ui.dataviewboard.charts.ChartStyle;
import com.ixora.rms.ui.dataviewboard.charts.definitions.RendererDef;
import com.ixora.rms.ui.dataviewboard.exception.FailedToCreateControl;
import com.ixora.rms.ui.dataviewboard.tables.definitions.TableDef;
import com.ixora.rms.ui.dataviewboard.tables.messages.Msg;
import com.ixora.rms.ui.dataviewboard.utils.TableBasedControl;
import com.ixora.rms.ui.dataviewboard.utils.TableBasedControlCellEditor;
import com.ixora.rms.ui.dataviewboard.utils.TableBasedControlHTMLProvider;
import com.ixora.rms.ui.dataviewboard.utils.TableBasedControlTableModel;

/**
 * @author Daniel Moraru
 */
public class TableControl extends TableBasedControl
			implements Observer {
	/** View as bar chart popup menu item */
	private JMenuItem fMenuItemViewAsBarChart;
	/** View as chart popup menu item */
	private JMenuItem fMenuItemViewAsChart;
	/** View selected roews as bar chart popup menu item */
	private JMenuItem fMenuItemViewSelectedAsBarChart;
	/** View selected as chart popup menu item */
	private JMenuItem fMenuItemViewSelectedAsChart;
	/** Clears the table selection */
	private JMenuItem fMenuItemClearSelection;
    /** Event handler */
    private EventHandler fEventHandler;
    /** Toggle remove stale entities button */
    private JToggleButton fRemoveStaleEntriesButton;

// actions
    private Action fActionViewAsBarChart;
    private Action fActionViewAsChart;
    private Action fActionViewSelectedAsBarChart;
    private Action fActionViewSelectedAsChart;
    private Action fActionClearSelection;
    private Action fActionRemoveStaleEntries;

    /** Table model */
    private TableControlModel fTableModel;

    /**
	 * Event handler.
	 */
	private final class EventHandler
				implements ListSelectionListener {
		/**
		 * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
		 */
		public void valueChanged(ListSelectionEvent e) {
			handleTableSelectionEvent(e);
		}
	}

    /**
     * ActionViewAsBarChart.
     */
    private final class ActionViewAsBarChart extends AbstractAction {
		/**
		 * Constructor.
		 */
		public ActionViewAsBarChart() {
			super();
			UIUtils.setUsabilityDtls(MessageRepository.get(TablesBoardComponent.NAME, Msg.TABLES_ACTONS_VIEW_AS_BAR_CHART), this);
			ImageIcon icon = UIConfiguration.getIcon("tables_view_bar_chart.gif");
			if(icon != null) {
				putValue(Action.SMALL_ICON, icon);
			}
		}
		/**
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent ev) {
			try {
				handleCreateBarChart();
			} catch(Exception e) {
				UIExceptionMgr.userException(e);
			}
		}
    }

    /**
     * ActionViewAsTimeSeriesChart.
     */
    private final class ActionViewAsTimeSeriesChart extends AbstractAction {
		/**
		 * Constructor.
		 */
		public ActionViewAsTimeSeriesChart() {
			super();
			UIUtils.setUsabilityDtls(MessageRepository.get(TablesBoardComponent.NAME, Msg.TABLES_ACTONS_VIEW_AS_TIMESERIES_CHART), this);
			ImageIcon icon = UIConfiguration.getIcon("tables_view_chart.gif");
			if(icon != null) {
				putValue(Action.SMALL_ICON, icon);
			}
		}
		/**
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent ev) {
			try {
				handleCreateTimeSeriesChart();
			} catch(Exception e) {
				UIExceptionMgr.userException(e);
			}
		}
    }

    /**
     * ActionViewSelectedAsBarChart.
     */
    private final class ActionViewSelectedAsBarChart extends AbstractAction {
		/**
		 * Constructor.
		 */
		public ActionViewSelectedAsBarChart() {
			super();
			UIUtils.setUsabilityDtls(MessageRepository.get(TablesBoardComponent.NAME, Msg.TABLES_ACTONS_VIEW_SELECTED_AS_BAR_CHART), this);
			ImageIcon icon = UIConfiguration.getIcon("tables_view_sel_bar_chart.gif");
			if(icon != null) {
				putValue(Action.SMALL_ICON, icon);
			}
		}
		/**
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent ev) {
			try {
				handleCreateBarChartForSelectedRows();
			} catch(Exception e) {
				UIExceptionMgr.userException(e);
			}
		}
    }

    /**
     * ActionViewSelectedAsTimeSeriesChart.
     */
    private final class ActionViewSelectedAsTimeSeriesChart extends AbstractAction {
		/**
		 * Constructor.
		 */
		public ActionViewSelectedAsTimeSeriesChart() {
			super();
			UIUtils.setUsabilityDtls(MessageRepository.get(TablesBoardComponent.NAME, Msg.TABLES_ACTONS_VIEW_SELECTED_AS_TIMESERIES_CHART), this);
			ImageIcon icon = UIConfiguration.getIcon("tables_view_sel_chart.gif");
			if(icon != null) {
				putValue(Action.SMALL_ICON, icon);
			}
		}
		/**
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent ev) {
			try {
				handleCreateTimeSeriesChartForSelectedRows();
			} catch(Exception e) {
				UIExceptionMgr.userException(e);
			}
		}
    }

    /**
     * ActionClearSelection.
     */
    private final class ActionClearSelection extends AbstractAction {
		/**
		 * Constructor.
		 */
		public ActionClearSelection() {
			super();
			UIUtils.setUsabilityDtls(MessageRepository.get(TablesBoardComponent.NAME, Msg.TABLES_ACTONS_CLEAR_SELECTION), this);
			ImageIcon icon = UIConfiguration.getIcon("tables_clear_selection.gif");
			if(icon != null) {
				putValue(Action.SMALL_ICON, icon);
			}
		}
		/**
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent ev) {
			try {
				handleClearSelection();
			} catch(Exception e) {
				UIExceptionMgr.userException(e);
			}
		}
    }

    /**
     * ActionRemoteStaleEntries.
     */
    private final class ActionRemoveStaleEntries extends AbstractAction {
        /**
         * Constructor.
         */
        public ActionRemoveStaleEntries() {
            super();
            UIUtils.setUsabilityDtls(MessageRepository.get(TablesBoardComponent.NAME, Msg.TABLES_ACTIONS_REMOVE_STALE_ENTRIES), this);
            ImageIcon icon = UIConfiguration.getIcon("tables_remove_stale_entries.gif");
            if(icon != null) {
            	putValue(Action.SMALL_ICON, icon);
            }
        }
        /**
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent ev) {
            try {
                handleRemoveStaleEntries();
            } catch(Exception e) {
                UIExceptionMgr.userException(e);
            }
        }
    }

    /**
     * Constructor.
     * @param owner
     * @param listener
     * @param context
     * @param locator
     * @param resourceContext
     * @param dvi
     * @throws FailedToCreateControl
     */
    public TableControl(TablesBoard owner,
    		Listener listener,
			DataViewControlContext context,
    		SessionArtefactInfoLocator locator,
    		ReactionLogService rls,
			ResourceId resourceContext,
			DataView dv) throws FailedToCreateControl {
        super(owner, listener, context, locator, rls, resourceContext, dv);
        this.fEventHandler = new EventHandler();

        fActionViewAsBarChart = new ActionViewAsBarChart();
        fActionViewAsChart = new ActionViewAsTimeSeriesChart();
        fActionViewSelectedAsBarChart = new ActionViewSelectedAsBarChart();
        fActionViewSelectedAsChart = new ActionViewSelectedAsTimeSeriesChart();
        fActionClearSelection = new ActionClearSelection();
        fActionRemoveStaleEntries = new ActionRemoveStaleEntries();
		fActionViewSelectedAsBarChart.setEnabled(false);
		fActionViewSelectedAsChart.setEnabled(false);
		fActionClearSelection.setEnabled(false);

        this.fTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        this.fTable.setCellSelectionEnabled(false);
        this.fTable.setRowSelectionAllowed(true);

		this.fTopPanel.setVisible(ConfigurationMgr.getBoolean(TablesBoardComponent.NAME,
                TablesBoardConfigurationConstants.TABLESBOARD_SHOW_TITLE));
        JButton b = UIFactoryMgr.createButton(fActionViewAsBarChart);
        b.setText(null);
        fToolBar.add(b, 0);
        b = UIFactoryMgr.createButton(fActionViewAsChart);
        b.setText(null);
        fToolBar.add(b, 1);
        b = UIFactoryMgr.createButton(fActionViewSelectedAsBarChart);
        b.setText(null);
        fToolBar.add(b, 2);
        b = UIFactoryMgr.createButton(fActionViewSelectedAsChart);
        b.setText(null);
        fToolBar.add(b, 3);
        fToolBar.addSeparator();
        b = UIFactoryMgr.createButton(fActionClearSelection);
        b.setText(null);
        fToolBar.add(b, 4);
        fRemoveStaleEntriesButton = UIFactoryMgr.createToggleButton(fActionRemoveStaleEntries);
        fRemoveStaleEntriesButton.setText(null);
        fToolBar.add(fRemoveStaleEntriesButton, 5);

        //this.table.addMouseListener(this.popupEventHandler);
        this.fTable.getSelectionModel().addListSelectionListener(this.fEventHandler);

        // add to the popup menu
        fMenuItemViewAsBarChart = UIFactoryMgr.createMenuItem(fActionViewAsBarChart);
        fMenuItemViewAsChart = UIFactoryMgr.createMenuItem(fActionViewAsChart);
        fMenuItemViewSelectedAsBarChart = UIFactoryMgr.createMenuItem(fActionViewSelectedAsBarChart);
        fMenuItemViewSelectedAsChart = UIFactoryMgr.createMenuItem(fActionViewSelectedAsChart);
        fMenuItemClearSelection = UIFactoryMgr.createMenuItem(fActionClearSelection);

        fPopupMenu.insert(fMenuItemViewAsBarChart, 0);
        fPopupMenu.insert(fMenuItemViewAsChart, 1);
        fPopupMenu.insert(new JPopupMenu.Separator(), 2);
        fPopupMenu.insert(fMenuItemViewSelectedAsBarChart, 3);
        fPopupMenu.insert(fMenuItemViewSelectedAsChart, 4);
        fPopupMenu.insert(new JPopupMenu.Separator(), 5);
        fPopupMenu.insert(fMenuItemClearSelection, 6);
        fPopupMenu.insert(new JPopupMenu.Separator(), 7);

        TableDef tableDef = (TableDef)dv;
		if(tableDef.removeStaleCategories()) {
			fRemoveStaleEntriesButton.setSelected(true);
		}

        ConfigurationMgr.get(TablesBoardComponent.NAME).addObserver(this);

        buildLegend();
    }

	/**
     * @see com.ixora.rms.ui.dataviewboard.DataViewControl#cleanup()
     */
    protected void cleanup() {
    	super.cleanup();
        ConfigurationMgr.get(TablesBoardComponent.NAME).deleteObserver(this);
    }

	/**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    public void update(Observable o, Object arg) {
        if(TablesBoardConfigurationConstants.TABLESBOARD_SHOW_TITLE.equals(arg)) {
    		this.fTopPanel.setVisible(ConfigurationMgr.getBoolean(TablesBoardComponent.NAME,
                    TablesBoardConfigurationConstants.TABLESBOARD_SHOW_TITLE));
    		revalidate();
    		repaint();
    		return;
        }
        if(TablesBoardConfigurationConstants.TABLESBOARD_COLOR_UP.equals(arg)
            	|| TablesBoardConfigurationConstants.TABLESBOARD_COLOR_DOWN.equals(arg)
            		|| TablesBoardConfigurationConstants.TABLESBOARD_DELTA_DEPTH.equals(arg)) {
            ((TableControlCellRenderer)this.fCellRenderer).getFormatter().setColorsForHistoryScale(
                    ConfigurationMgr.getColor(TablesBoardComponent.NAME,
                    		TablesBoardConfigurationConstants.TABLESBOARD_COLOR_UP),
                    ConfigurationMgr.getColor(TablesBoardComponent.NAME,
                            TablesBoardConfigurationConstants.TABLESBOARD_COLOR_DOWN),
                    ConfigurationMgr.getInt(TablesBoardComponent.NAME,
                            TablesBoardConfigurationConstants.TABLESBOARD_DELTA_DEPTH));
            getOwnCellRenderer().getFormatter().setDeltaHistorySize(ConfigurationMgr.getInt(TablesBoardComponent.NAME,
                    TablesBoardConfigurationConstants.TABLESBOARD_DELTA_DEPTH));
            return;
        }
        if(TablesBoardConfigurationConstants.TABLESBOARD_USE_ROW_STRIPING.equals(arg)
				|| TablesBoardConfigurationConstants.TABLESBOARD_COLOR_ROW_STRIPE.equals(arg)) {
    		boolean useRowStriping = ConfigurationMgr.getBoolean(TablesBoardComponent.NAME,
                    TablesBoardConfigurationConstants.TABLESBOARD_USE_ROW_STRIPING);
    		useRowStriping(useRowStriping);
    		getOwnCellRenderer().getFormatter().setRowStripeColor(
                    useRowStriping ? ConfigurationMgr.getColor(TablesBoardComponent.NAME,
                            TablesBoardConfigurationConstants.TABLESBOARD_COLOR_ROW_STRIPE) : null);
            return;
        }
        if(TablesBoardConfigurationConstants.TABLESBOARD_DEFAULT_NUMBER_FORMAT.equals(arg)) {
        	getOwnCellRenderer().getFormatter().setDefaultNumberFormat(ConfigurationMgr.getString(TablesBoardComponent.NAME,
                TablesBoardConfigurationConstants.TABLESBOARD_DEFAULT_NUMBER_FORMAT));
        	return;
        }
    }

    /**
     * @return
     */
    private TableControlCellRenderer getOwnCellRenderer() {
    	return (TableControlCellRenderer)this.fCellRenderer;
    }

	/**
	 * Creates a bar chart view from resources found in the visible rows in the table.
	 */
	private void handleCreateBarChart() {
        createBarChartForRows(null);
    }

	/**
     * Creates a bar chart from resources found at the selected rows in the table.
	 * @param b
	 */
	private void handleCreateBarChartForSelectedRows() {
		int[] sels = fTable.getSelectedRows();
		if(sels == null || sels.length == 0) {
			return;
		}
        createBarChartForRows(sels);
	}

    /**
     * Creates a bar chart from resources found at the given rows.
     * @param rows
     */
    private void createBarChartForRows(int[] rows) {
        String viewName = this.fControlContext.createValidDataViewName(fContextId);
        if(viewName == null) {
            return;
        }

        // get chart properties
        QuickChartProperties props = getChartPropertiesForBar();
        if(props == null) {
            return;
        }

        // Put all items on range axis
        List<String> ranges = Arrays.asList(props.getItems());
        if (ranges.size() == 0) {
            return;
        }

        // Create a copy of the original query
        QueryDef origQuery = this.fDataView.getQueryDef();
        List<ResourceDef> listResources = new LinkedList<ResourceDef>();
        listResources.addAll(origQuery.getResources());
        List<FunctionDef> listFunctions = new LinkedList<FunctionDef>();
        listFunctions.addAll(origQuery.getFunctions());

        if(rows != null) {
	        // Create a filter on category resource, allowing only selected values
	        List<ParamDef> listFilterParam = new LinkedList<ParamDef>();
	        listFilterParam.add(new ParamDef(getTableDefinition().getCategory()));
	        List<ValueFilterRuleDef> listFilterRules = new LinkedList<ValueFilterRuleDef>();
	        for(int i = 0; i < rows.length; i++) {
	            String categoryValueAtRow = fTableModel.getCategoryValueAtRow(
	                    fTableModelSorter.modelIndex(rows[i]));
	            listFilterRules.add(new ValueFilterRuleDef(categoryValueAtRow));
	        }
	        ValueFilterDef filterDef = new ValueFilterDef(listFilterParam, listFilterRules);
	        // Note: every resource/function must have an unique ID. If ID is not set
	        // then a function will inherit the ID of its first param (like Identity)
	        filterDef.setID("hiddenFilterFunction");
	        listFunctions.add(filterDef);
        }

        // Create a duplicate of the original query with the filter added
        QueryDef queryDef = new QueryDef(viewName, listResources, listFunctions);

        // Add a single renderer
        List<RendererDef> listRenderers = new LinkedList<RendererDef>();
        listRenderers.add(new RendererDef(
                props.getStyle().getRenderer(),
                getTableDefinition().getCategory(), ranges));


        // Create chart definition from domain, ranges, query and renderer
        QuickChartDataView chartdef = new QuickChartDataView(
                viewName,
                viewName,
                queryDef, listRenderers, fDataView.getAuthor());
        // Plot chart
        this.fControlContext.getDataViewPlotter().plot(fContextId, chartdef);
    }

	/**
	 * Creates a time series chart for view from all the rows visible in the table.
	 * @throws RMSException
	 */
	private void handleCreateTimeSeriesChart() throws RMSException {
        createTimeSeriesChartForRows(null);
    }

	/**
	 * Creates a time series chart for all selected rows in the table.
	 * @throws RMSException
	 */
	private void handleCreateTimeSeriesChartForSelectedRows() throws RMSException {
		int[] sels = fTable.getSelectedRows();
		if(sels == null || sels.length == 0) {
			return;
		}
		createTimeSeriesChartForRows(sels);
	}

    /**
     * Creates a time series chart for resources represented by the given rows.
     * @param rows if not null a filter function will be used to allow only
     * given rows to be part of the query
     * @throws RMSException
     */
    private void createTimeSeriesChartForRows(int[] rows) throws RMSException {

        String viewName = this.fControlContext.createValidDataViewName(fContextId);
        if(viewName == null) {
        	// cancelled, don't throw exception
            return;
        }

        QuickChartProperties props = getChartPropertiesForTimeSeries();
        if(props == null) {
        	// cancelled, don't throw exception
            return;
        }
        List<String> ranges = Arrays.asList(props.getItems());
        if(ranges.size() == 0) {
        	// TODO localize
            throw new RMSException("No ranges have been defined for this data view");
        }

        // Create a copy of the original query
        QueryDef origQuery = this.fDataView.getQueryDef();

        List<ResourceDef> listResources = new LinkedList<ResourceDef>();

        // Create and add a ResourceId for timestamp
        ResourceId  ridTimestamp = null;
        for(ResourceDef resource : origQuery.getResources()) {
            int rep = resource.getResourceId().getRelativeRepresentation();
        	// set the timestamp for the first counter resource
            if(rep == ResourceId.COUNTER) {
	            if(ridTimestamp == null) {
	                // Create a ResourceId for timestamp
	                ResourceId rid = resource.getResourceId();
	                ridTimestamp = new ResourceId(
	                        rid.getHostId(),
	                        rid.getAgentId(),
	                        rid.getEntityId(),
	                        CounterDescriptor.TIMESTAMP_ID);
	            }
        	}
           	listResources.add(resource);
        }

        // no timestamp? that means that no counter resources
        // were defined in the query
        if (ridTimestamp == null) {
        	// TODO localize
            throw new RMSException("At least one resource in this data view must be a counter");
        }
        listResources.add(new ResourceDef(ridTimestamp));

        // Add only functions specified
        List<FunctionDef> usedFunctions = new LinkedList<FunctionDef>();
        List<FunctionDef> allFunctions = origQuery.getFunctions();
        if(!Utils.isEmptyCollection(allFunctions)) {
        	for(FunctionDef fdef : allFunctions) {
        		if(ranges.contains(fdef.getID())) {
        			usedFunctions.add(fdef);
        		}
        	}
        }

        if(rows != null) {
	        // Create a filter on category resource, allowing only selected values
	        List<ParamDef> listFilterParam = new LinkedList<ParamDef>();
	        listFilterParam.add(new ParamDef(getTableDefinition().getCategory()));
	        List<ValueFilterRuleDef> listFilterRules = new LinkedList<ValueFilterRuleDef>();
	        for(int i = 0; i < rows.length; i++) {
	            String categoryValueAtRow = fTableModel.getCategoryValueAtRow(
	                    fTableModelSorter.modelIndex(rows[i]));
	            listFilterRules.add(new ValueFilterRuleDef(categoryValueAtRow));
	        }
	        ValueFilterDef filterDef = new ValueFilterDef(listFilterParam, listFilterRules);
	        // Note: every resource/function must have an unique ID. If ID is not set
	        // then a function will inherit the ID of its first param (like Identity)
	        filterDef.setID("hiddenFilterFunction");
	        usedFunctions.add(filterDef);
        }

        QueryDef queryDef = new QueryDef(viewName, listResources, usedFunctions);

        // Add a single renderer
        List<RendererDef> listRenderers = new LinkedList<RendererDef>();
        listRenderers.add(new RendererDef(
            props.getStyle().getRenderer(), ridTimestamp.toString(), ranges));

        // Create chart definition from domain, ranges, query and renderer
        QuickChartDataView chartdef = new QuickChartDataView(
            viewName,
            viewName,
            queryDef, listRenderers, fDataView.getAuthor());
        // Plot chart
        this.fControlContext.getDataViewPlotter().plot(fContextId, chartdef);
    }

	/**
	 * @return
	 */
	private TableDef getTableDefinition() {
		return (TableDef)this.fDataView;
	}

	/**
	 * @return
	 */
	private QuickChartProperties getChartPropertiesForBar() {
		QuickChartPropertiesDialog dlg = new QuickChartPropertiesDialog(
				this.fControlContext.getViewContainer().getAppFrame(),
				new ChartStyle[] {
					ChartStyle.BAR_2D,
					ChartStyle.STACKED_BAR_2D,
					ChartStyle.BAR_3D,
					ChartStyle.STACKED_BAR_3D,
					ChartStyle.CATEGORY_LINE,
					ChartStyle.CATEGORY_AREA,
					ChartStyle.CATEGORY_STACKED_AREA,
				},
				getChartItemsFromDataView(),
				getChartItemsDisplayNamesFromDataView()
		);
		UIUtils.centerDialogAndShow(this.fControlContext.getViewContainer().getAppFrame(), dlg);
		return dlg.getQuickChartProperties();
	}

	/**
	 * @return
	 */
	private QuickChartProperties getChartPropertiesForTimeSeries() {
		QuickChartPropertiesDialog dlg = new QuickChartPropertiesDialog(
				this.fControlContext.getViewContainer().getAppFrame(),
				new ChartStyle[] {
					ChartStyle.XY_LINE,
					ChartStyle.XY_AREA,
					ChartStyle.STACKED_XY_AREA,
				},
				getChartItemsFromDataView(),
				getChartItemsDisplayNamesFromDataView()
		);
		UIUtils.centerDialogAndShow(this.fControlContext.getViewContainer().getAppFrame(), dlg);
		return dlg.getQuickChartProperties();
	}

	/**
	 * @return
	 */
	private String[] getChartItemsFromDataView() {
		Cube query = this.getRealizedQuery();
		List<String> ret = new LinkedList<String>();
        for(QueryResult queryResult : query.getQueryResults()) {
			String id = queryResult.getStyle().getID();
			if (!getTableDefinition().getCategory().equals(id)
                    && getTableDefinition().isIdAccepted(id)) {
				ret.add(id);
			}
		}
		return ret.toArray(new String[ret.size()]);
	}

	/**
	 * @return
	 */
	private String[] getChartItemsDisplayNamesFromDataView() {
		Cube query = this.getRealizedQuery();
		List<String> ret = new LinkedList<String>();
		for(QueryResult queryResult : query.getQueryResults()) {
			String id = queryResult.getStyle().getID();
			if (!getTableDefinition().getCategory().equals(id)
                    && getTableDefinition().isIdAccepted(id)) {
				String name = queryResult.getStyle().getName();
				if(name == null) {
					name = id;
				}
				ret.add(name);
			}
		}
		return ret.toArray(new String[ret.size()]);
	}

	/**
	 * When rows are selected we must enable the actions for representing the selected items
	 * as charts.
	 */
	private void handleTableSelectionEvent(ListSelectionEvent ev) {
		try {
			int sel = fTable.getSelectedRowCount();
			if(sel == 0) {
				fActionViewSelectedAsBarChart.setEnabled(false);
				fActionViewSelectedAsChart.setEnabled(false);
				fActionClearSelection.setEnabled(false);
				return;
			}
			fActionViewSelectedAsBarChart.setEnabled(true);
			fActionViewSelectedAsChart.setEnabled(true);
			fActionClearSelection.setEnabled(true);
		}catch(Exception e) {
			UIExceptionMgr.userException(e);
		}
	}

	/**
	 * Clear the selection in the table.
	 */
	private void handleClearSelection() {
		try {
			this.fTable.clearSelection();
		} catch(Exception e) {
			UIExceptionMgr.userException(e);
		}
	}

    /**
     *
     */
    private void handleRemoveStaleEntries() {
        boolean orig = fRemoveStaleEntriesButton.isSelected();
        try {
            this.fTableModel.setRemoveStaleCategories(orig);
        } catch(Exception e) {
            fRemoveStaleEntriesButton.setSelected(!orig);
            UIExceptionMgr.userException(e);
        }
    }

	/**
	 * @see com.ixora.rms.ui.dataviewboard.utils.TableBasedControl#createTableModel()
	 */
	protected TableBasedControlTableModel createTableModel() {
		TableDef tableDef = (TableDef)this.fDataView;
		fTableModel = new TableControlModel(
                this.fLocator,
                tableDef,
        		getRealizedQuery(),
        		getOwnCellRenderer().getFormatter()
        );
		return fTableModel;
	}

	/**
	 * @see com.ixora.rms.ui.dataviewboard.utils.TableBasedControl#createHTMLProvider()
	 */
	protected TableBasedControlHTMLProvider createHTMLProvider() {
		return new TableBasedControlHTMLProvider(fTableModelSorter,
				getOwnCellRenderer().getFormatter());
	}

	/**
	 * @see com.ixora.rms.ui.dataviewboard.utils.TableBasedControl#createTableCellRenderer()
	 */
	protected TableCellRenderer createTableCellRenderer() {
		boolean useRowStriping = ConfigurationMgr.getBoolean(TablesBoardComponent.NAME,
                TablesBoardConfigurationConstants.TABLESBOARD_USE_ROW_STRIPING);
		return new TableControlCellRenderer(
                useRowStriping ? ConfigurationMgr.getColor(TablesBoardComponent.NAME,
                        TablesBoardConfigurationConstants.TABLESBOARD_COLOR_ROW_STRIPE) : null,
                ConfigurationMgr.getColor(TablesBoardComponent.NAME,
                        TablesBoardConfigurationConstants.TABLESBOARD_COLOR_UP),
                ConfigurationMgr.getColor(TablesBoardComponent.NAME,
                        TablesBoardConfigurationConstants.TABLESBOARD_COLOR_DOWN),
                ConfigurationMgr.getInt(TablesBoardComponent.NAME,
                        TablesBoardConfigurationConstants.TABLESBOARD_DELTA_DEPTH));
	}

	/**
	 * @see com.ixora.rms.ui.dataviewboard.utils.TableBasedControl#createTableCellEditor()
	 */
	protected TableCellEditor createTableCellEditor() {
		return new TableBasedControlCellEditor(getOwnCellRenderer().getFormatter());
	}
}
