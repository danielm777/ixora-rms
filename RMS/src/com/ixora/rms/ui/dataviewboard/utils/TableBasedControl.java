/**
 * 08-Feb-2006
 */
package com.ixora.rms.ui.dataviewboard.utils;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.Comparator;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import com.ixora.common.MessageRepository;
import com.ixora.common.ui.TableSorter;
import com.ixora.common.ui.UIConfiguration;
import com.ixora.common.ui.UIExceptionMgr;
import com.ixora.common.ui.UIFactoryMgr;
import com.ixora.common.ui.UIUtils;
import com.ixora.common.ui.filter.FilterDefinitionDialog;
import com.ixora.common.ui.filter.FilteredTableModel;
import com.ixora.common.ui.filter.RowFilter;
import com.ixora.common.ui.filter.FilteredTableModel.FilterEvent;
import com.ixora.rms.ResourceId;
import com.ixora.rms.dataengine.external.QueryData;
import com.ixora.rms.repository.DataView;
import com.ixora.rms.ui.dataviewboard.DataViewBoard;
import com.ixora.rms.ui.dataviewboard.DataViewControl;
import com.ixora.rms.ui.dataviewboard.DataViewControlContext;
import com.ixora.rms.ui.dataviewboard.DataViewControlDescriptor;
import com.ixora.rms.ui.dataviewboard.exception.FailedToCreateControl;
import com.ixora.rms.ui.messages.Msg;

/**
 * @author Daniel Moraru
 */
public abstract class TableBasedControl extends DataViewControl {
	private static final long serialVersionUID = -4271115091003631834L;
	/** Table */
	protected JTable fTable;
    /** Table sorter */
	protected TableSorter fTableModelSorter;
	/** Label holding the name of the data view */
    protected JLabel fTitleLabel;
    /** Top panel, hosting the title and the buttons */
    protected JPanel fTopPanel;
    /** Label holding the number of rows */
    protected JLabel fRowCountLabel;
    /** Table cell renderer */
    protected TableCellRenderer fCellRenderer;
    /** Table cell editor */
    protected TableCellEditor fCellEditor;
    /** Toggle filter button */
    protected JToggleButton fFilterButton;
    /** Tool bar */
    protected JToolBar fToolBar;
    /** Label to display the context of the data view */
    protected JLabel fLabelContext;
    /** Table model */
	protected FilteredTableModel fFilteredTableModel;

// actions
    protected Action fActionToggleFilter;
    protected Action fActionPrintTable;

// the following must be private
    /** Event handler */
    private EventHandler fEventHandler;
    /** Table to HTML converter */
	private TableBasedControlHTMLProvider fHTMLProvider;

    /**
     * Custom table sorter that always uses a comparable comparator.
     */
    private static final class CustomTableSorter extends TableSorter {
		private static final long serialVersionUID = 328162392954620394L;
		public CustomTableSorter(TableModel tableModel) {
			super(tableModel);
		}
    	protected Comparator<?> getComparator(int column) {
			return COMPARABLE_COMAPRATOR;
		}
    }

    /**
     * ActionSetFilter.
     */
    private final class ActionToggleFilter extends AbstractAction {
		private static final long serialVersionUID = 6166777712710317883L;
		/**
		 * Constructor.
		 */
		public ActionToggleFilter() {
			super();
			UIUtils.setUsabilityDtls(MessageRepository.get(Msg.DATAVIEWBOARD_ACTONS_TOGGLE_FILTER), this);
			ImageIcon icon = UIConfiguration.getIcon("toggle_filter.gif");
			if(icon != null) {
				putValue(Action.SMALL_ICON, icon);
			}
		}
		/**
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent ev) {
			try {
				handleToggleFilter();
			} catch(Exception e) {
				UIExceptionMgr.userException(e);
			}
		}
    }

   /**
     * ActionPrint.
     */
    private final class ActionPrint extends AbstractAction {
    	private static final long serialVersionUID = -5762484356092134122L;
		/**
		 * Constructor.
		 */
		public ActionPrint() {
			super();
			UIUtils.setUsabilityDtls(MessageRepository.get(Msg.DATAVIEWBOARD_ACTONS_PRINT), this);
			ImageIcon icon = UIConfiguration.getIcon("print.gif");
			if(icon != null) {
				putValue(Action.SMALL_ICON, icon);
			}
		}
		/**
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent ev) {
			try {
				handlePrintTable();
			} catch(Exception e) {
				UIExceptionMgr.userException(e);
			}
		}
    }

    /**
	 * Event handler.
	 */
	private final class EventHandler extends MouseAdapter
				implements ListSelectionListener, FilteredTableModel.Listener {
		/**
		 * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
		 */
		public void valueChanged(ListSelectionEvent e) {
			//handleTableSelectionEvent(e);
		}
		/**
		 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
		 */
		public void mouseEntered(MouseEvent e) {
			fireControlInFocus();
		}
		/**
		 * @see com.ixora.common.ui.filter.FilteredTableModel.Listener#filterEvent(com.ixora.common.ui.filter.FilteredTableModel.FilterEvent)
		 */
		public void filterEvent(FilterEvent event) {
			handleFilterEvent(event);
		}
		/**
		 * @see com.ixora.common.ui.filter.FilteredTableModel.Listener#readyToFilter()
		 */
		public void readyToFilter() {
			handleReadyToFilter();
		}
	}

	/**
	 * @param owner
	 * @param listener
	 * @param context
	 * @param locator
	 * @param rls
	 * @param resourceContext
	 * @param dataView
	 * @throws FailedToCreateControl
	 */
	public TableBasedControl(DataViewBoard owner, Listener listener, DataViewControlContext context, ResourceId resourceContext, DataView dataView) throws FailedToCreateControl {
		super(owner, listener, context, resourceContext, dataView);
		fEventHandler = new EventHandler();
		fActionPrintTable = new ActionPrint();
		fActionToggleFilter = new ActionToggleFilter();

		fCellRenderer = createTableCellRenderer();
		fCellEditor = createTableCellEditor();

		TableBasedControlTableModel model = createTableModel();
		fFilteredTableModel = new FilteredTableModel(model, model.getFilterUIClasses());

        fTableModelSorter = createTableSorter();

        fTable = new JTable(this.fTableModelSorter);

        // install tool tip header for both the table and the model sorted
        ToolTipHeader header = new ToolTipHeader(model, this.fTable.getColumnModel());
        fTable.setTableHeader(header);
        fTableModelSorter.setTableHeader(header);

        fTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        fTable.setCellSelectionEnabled(false);
        fTable.setRowSelectionAllowed(true);

        setColumnsWitdh();
        useRowStriping(true);
        setTableCellRendererAndEditor();

        JPanel topLeft = createTopLeftPanel();
        fTopPanel = new JPanel(new BorderLayout());
        fTopPanel.add(topLeft, BorderLayout.CENTER);

		fToolBar = UIFactoryMgr.createToolBar();
		fToolBar.setFloatable(false);
	    fFilterButton = UIFactoryMgr.createToggleButton(fActionToggleFilter);
        fFilterButton.setText(null);
        fFilterButton.setEnabled(fFilteredTableModel.readyToFilter());
        fToolBar.add(fFilterButton);
        JButton b = UIFactoryMgr.createButton(fActionPrintTable);
        b.setText(null);
        fToolBar.add(b);

        this.fTopPanel.add(fToolBar, BorderLayout.EAST);

        JScrollPane sp = UIFactoryMgr.createScrollPane();
        sp.setViewportView(fTable);
        sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        sp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));

        getDisplayPanel().setLayout(new BorderLayout());
        getDisplayPanel().add(sp, BorderLayout.CENTER);
        getDisplayPanel().add(this.fTopPanel, BorderLayout.NORTH);

        fFilteredTableModel.addListener(fEventHandler);
      // add mouse enter listener to all major components of this control
        this.fTable.addMouseListener(fEventHandler);
        sp.addMouseListener(fEventHandler);
        addMouseListener(fEventHandler);
        addMouseListener(fPopupEventHandler);
        fTopPanel.addMouseListener(fEventHandler);
        fTopPanel.addMouseListener(fPopupEventHandler);
        sp.addMouseListener(fPopupEventHandler);

        fHTMLProvider = createHTMLProvider();
	}

	/**
	 * @return
	 */
	protected JPanel createTopLeftPanel() {
        JPanel north = new JPanel();
        north.setLayout(new BoxLayout(north,BoxLayout.X_AXIS));
        fTitleLabel = UIFactoryMgr.createLabel("");
        fTitleLabel.setFont(fTitleLabel.getFont().deriveFont(Font.BOLD));
        north.add(fTitleLabel);
        fRowCountLabel = UIFactoryMgr.createLabel("");
        fRowCountLabel.setFont(fRowCountLabel.getFont().deriveFont(Font.BOLD));
        north.add(Box.createHorizontalStrut(5));
        north.add(fRowCountLabel);

        JPanel panel = new JPanel(new BorderLayout());
		panel.add(north, BorderLayout.NORTH);
		fLabelContext = UIFactoryMgr.createLabel(" ");
		panel.add(fLabelContext, BorderLayout.CENTER);
        return panel;
	}

	/**
	 * @return
	 */
	protected abstract TableCellRenderer createTableCellRenderer();

	/**
	 * @return
	 */
	protected abstract TableCellEditor createTableCellEditor();

	/**
	 * @return
	 */
	protected abstract TableBasedControlHTMLProvider createHTMLProvider();

	/**
	 * Overriden to regenerate the tooltip for the title label and to set
	 * the translated context.
	 * @see com.ixora.rms.ui.dataviewboard.DataViewControl#buildLegend()
	 */
	protected void buildLegend() {
		super.buildLegend();
		if(fTitleLabel != null) {
			fTitleLabel.setText(getTranslatedViewName());
			fTitleLabel.setToolTipText(createHmlDescriptionFormatted());
		}
		if(fLabelContext != null) {
			fLabelContext.setText("<html><font color='#FFFFFF'>"
					+ getTranslatedContextPath()
					+ "</font></html>");
		}
	}

	/**
	 * @return
	 */
	protected abstract TableBasedControlTableModel createTableModel();

	/**
	 * @return
	 */
	protected TableSorter createTableSorter() {
		return new CustomTableSorter(fFilteredTableModel);
	}

    /**
	 * Sets the width of columns.
	 */
	protected void setColumnsWitdh() {
        TableColumnModel columnModel = this.fTable.getColumnModel();
        int columns = columnModel.getColumnCount();
        for(int i = 1; i < columns; ++i) {
            TableColumn column = columnModel.getColumn(i);
            Component comp = fTable.getTableHeader().getDefaultRenderer().getTableCellRendererComponent(
                    fTable, column.getHeaderValue(), false, false, 0, 0);
            int headerWidth = comp.getPreferredSize().width;
            column.setPreferredWidth(headerWidth);
            column.setWidth(headerWidth);
        }
	}

	/**
     * @see com.ixora.rms.ui.dataviewboard.DataViewControl#handleExpired()
     */
    protected void handleExpired() {
    	if(fTitleLabel != null) {
	        fTitleLabel.setText("[Expired]" + fTitleLabel.getText());
	        fTitleLabel.setForeground(Color.RED);
    	}
    }

    /**
	 * Adds a filter.
	 */
	protected void handleToggleFilter() {
		try {
			if(this.fFilterButton.isSelected()) {
				// set filter
				FilterDefinitionDialog dlg = new FilterDefinitionDialog(
						this.fControlContext.getViewContainer().getAppFrame(),
						this.fFilteredTableModel);
				UIUtils.centerDialogAndShow(this.fControlContext.getViewContainer().getAppFrame(), dlg);
				fFilterButton.setSelected(fFilteredTableModel.isFilterOn());
			} else {
				fFilteredTableModel.removeFilters();
			}
			updateRowCountLabel();
		} catch(Exception e) {
			UIExceptionMgr.userException(e);
		}
	}

	/**
	 * Updates the label showing the filtered and unfilterred row count.
	 */
	protected void updateRowCountLabel() {
		if(fRowCountLabel != null) {
			fRowCountLabel.setText("("
					+ this.fFilteredTableModel.getRowCountUnfiltered()
					+ "/"
					+ this.fFilteredTableModel.getRowCountFiltered()
					+ ")");
		}
	}

	/**
     * @param b
     */
    protected void useRowStriping(boolean b) {
		if(b) {
			this.fTable.setShowGrid(false);
			this.fTable.setIntercellSpacing(new Dimension(0, 0));
		} else {
			this.fTable.setShowGrid(true);
			this.fTable.setIntercellSpacing(new Dimension(2, 2));
		}
    }

	/**
	 * @throws IOException
	 * @see com.ixora.rms.ui.exporter.HTMLProvider#toHTML(java.lang.StringBuilder, java.io.File)
	 */
	public void toHTML(StringBuilder buff, File root) throws IOException {
		super.toHTML(buff, root);
		fHTMLProvider.toHTML(buff, root);
	}

	/**
	 * @see com.ixora.rms.ui.dataviewboard.DataViewControl#getDescriptor()
	 */
	public DataViewControlDescriptor getDescriptor() {
		int sortedColIdx = -1;
		boolean sortedDesc = false;
		if(this.fTableModelSorter.isSorting()) {
			int cols = this.fTableModelSorter.getColumnCount();
			for(int i = 0; i < cols; i++) {
				int status = this.fTableModelSorter.getSortingStatus(i);
				if(status == TableSorter.ASCENDING) {
					sortedColIdx = i;
				} else if(status == TableSorter.DESCENDING) {
					sortedColIdx = i;
					sortedDesc = true;
				}
			}
		}
		TableBasedControlDescriptor desc = createDescriptor(sortedColIdx, sortedDesc,
				fFilteredTableModel.getRowFilter());
		prepareDescriptor(desc);
		return desc;
	}

	/**
	 * Subclasses should return here there own descriptor if needed.
	 * @param sortedColIdx
	 * @param sortedDesc
	 * @param filter
	 * @return
	 */
	protected TableBasedControlDescriptor createDescriptor(int sortedColIdx, boolean sortedDesc, RowFilter filter) {
		return new TableBasedControlDescriptor(sortedColIdx, sortedDesc, filter);
	}

	/**
	 * @see com.ixora.rms.ui.dataviewboard.DataViewControl#setUpFromDescriptor(com.ixora.rms.ui.dataviewboard.DataViewControlDescriptor)
	 */
	public void setUpFromDescriptor(DataViewControlDescriptor desc) {
		TableBasedControlDescriptor tdesc = (TableBasedControlDescriptor)desc;
		if(tdesc.getSortedColumnIdx() >= 0) {
			this.fTableModelSorter.setSortingStatus(tdesc.getSortedColumnIdx(),
					tdesc.isSortDirectionDesc() ? TableSorter.DESCENDING : TableSorter.ASCENDING);
		}
		RowFilter rowFilter = tdesc.getRowFilter();
		if(rowFilter != null) {
			this.fFilteredTableModel.setRowFilter(rowFilter);
		}
	}

	/**
	 * @see com.ixora.rms.ui.dataviewboard.DataViewControl#reset()
	 */
	protected void reset() {
		((TableBasedControlTableModel)fFilteredTableModel.getAdaptedModel()).reset();
	}

	/**
	 *
	 */
	protected void handlePrintTable() {
        try {
        	this.fTable.print();
        } catch(Exception e) {
            UIExceptionMgr.userException(e);
        }
	}

	/**
	 * @see com.ixora.rms.ui.dataviewboard.DataViewControl#handleDataAvailable(com.ixora.rms.dataengine.DataQueryExecutor.Data)
	 */
	protected void handleDataAvailable(QueryData data) {
        // before we update the model, save the curren selection
        // and restore it afterwards
        int idxMin = this.fTable.getSelectionModel().getMinSelectionIndex();
        int idxMax = this.fTable.getSelectionModel().getMaxSelectionIndex();
        TableBasedControlTableModel adaptedModel = (TableBasedControlTableModel)fFilteredTableModel.getAdaptedModel();
        if(adaptedModel.inspectData(data)) {
			; // don't update legend as we are only intersted for
			// the moment in name and description of the query

			// columns might have changed
	        setColumnsWitdh();
			setTableCellRendererAndEditor();

			if(!fFilteredTableModel.readyToFilter()) {
				fFilteredTableModel.setFilterUIClasses(adaptedModel.getFilterUIClasses());
			}
		}
        // restore selection
        if(idxMin >= 0 || idxMax >= 0) {
            this.fTable.getSelectionModel().setSelectionInterval(idxMin, idxMax);
        }
		updateRowCountLabel();
	}

   /**
     * Sets up the cell renderer for the table.
     */
    protected void setTableCellRendererAndEditor() {
        TableColumnModel columnModel = this.fTable.getColumnModel();
        int cols = columnModel.getColumnCount();
        TableColumn col;
        for(int i = 0; i < cols; i++) {
            col = columnModel.getColumn(i);
            col.setCellRenderer(fCellRenderer);
            col.setCellEditor(fCellEditor);
        }
    }

	/**
	 * @param event
	 */
	protected void handleFilterEvent(FilterEvent event) {
		try {
			if(fFilteredTableModel.isFilterOn()) {
				fFilterButton.setSelected(true);
			} else {
				fFilterButton.setSelected(false);
			}
		} catch(Exception e) {
			UIExceptionMgr.userException(e);
		}
	}

	/**
	 *
	 */
	protected void handleReadyToFilter() {
		try {
			fFilterButton.setEnabled(true);
		} catch(Exception e) {
			UIExceptionMgr.userException(e);
		}
	}
}
