/**
 * 29-Jan-2006
 */
package com.ixora.rms.ui.dataviewboard.logs;

import java.awt.Color;
import java.util.Observable;
import java.util.Observer;

import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import com.ixora.rms.ResourceId;
import com.ixora.common.ConfigurationMgr;
import com.ixora.common.ui.filter.RowFilter;
import com.ixora.rms.client.locator.SessionArtefactInfoLocator;
import com.ixora.rms.repository.DataView;
import com.ixora.rms.services.ReactionLogService;
import com.ixora.rms.ui.dataviewboard.DataViewBoard;
import com.ixora.rms.ui.dataviewboard.DataViewControlContext;
import com.ixora.rms.ui.dataviewboard.exception.FailedToCreateControl;
import com.ixora.rms.ui.dataviewboard.logs.definitions.LogDef;
import com.ixora.rms.ui.dataviewboard.utils.ButtonCellEditor;
import com.ixora.rms.ui.dataviewboard.utils.ButtonCellRenderer;
import com.ixora.rms.ui.dataviewboard.utils.TableBasedControl;
import com.ixora.rms.ui.dataviewboard.utils.TableBasedControlCellEditor;
import com.ixora.rms.ui.dataviewboard.utils.TableBasedControlDescriptor;
import com.ixora.rms.ui.dataviewboard.utils.TableBasedControlHTMLProvider;
import com.ixora.rms.ui.dataviewboard.utils.TableBasedControlTableModel;

/**
 * @author Daniel Moraru
 */
public class LogControl extends TableBasedControl implements Observer {
	private static final long serialVersionUID = -5883738187209821989L;
	/** Table model */
	private LogControlTableModel fTableModel;
	/** Editor for the third column */
	private ButtonCellEditor fValueCellEditor;
	/** Renderer for the third column */
	private ButtonCellRenderer fValueCellRenderer;

    /**
	 * Event handler.
	 */
//	private final class EventHandler implements ListSelectionListener {
//		/**
//		 * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
//		 */
//		public void valueChanged(ListSelectionEvent e) {
//			//handleTableSelectionEvent(e);
//		}
//	}

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
	public LogControl(DataViewBoard owner, Listener listener, DataViewControlContext context, SessionArtefactInfoLocator locator, ReactionLogService rls, ResourceId resourceContext, DataView dataView) throws FailedToCreateControl {
		super(owner, listener, context, locator, rls, resourceContext, dataView);
		buildLegend();
		ConfigurationMgr.get(LogBoardComponent.NAME).addObserver(this);
	}

	/**
	 * @see com.ixora.rms.ui.dataviewboard.utils.TableBasedControl#createDescriptor(int, boolean, com.ixora.common.ui.filter.RowFilter)
	 */
	protected TableBasedControlDescriptor createDescriptor(int sortedColIdx, boolean sortedDesc, RowFilter filter) {
		return new LogControlDescriptor(sortedColIdx, sortedDesc, filter);
	}

	/**
	 * @see com.ixora.rms.ui.dataviewboard.utils.TableBasedControl#createTableModel()
	 */
	protected TableBasedControlTableModel createTableModel() {
		fTableModel = new LogControlTableModel(fLocator,
				(LogDef)fDataView, getRealizedQuery(),
				ConfigurationMgr.getInt(LogBoardComponent.NAME,
						LogBoardConfigurationConstants.LOGBOARD_LOG_RECORD_BUFFER_SIZE));
		return fTableModel;
	}

	/**
	 * @see com.ixora.rms.ui.dataviewboard.utils.TableBasedControl#createTableCellRenderer()
	 */
	protected TableCellRenderer createTableCellRenderer() {
		return new LogControlTableCellRenderer(ConfigurationMgr.getColor(LogBoardComponent.NAME,
				LogBoardConfigurationConstants.LOGBOARD_COLOR_ROW_STRIPE));
	}

	/**
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	public void update(Observable o, Object arg) {
		if(LogBoardConfigurationConstants.LOGBOARD_COLOR_ROW_STRIPE.equals(arg)) {
			Color color = ConfigurationMgr.getColor(LogBoardComponent.NAME, LogBoardConfigurationConstants.LOGBOARD_COLOR_ROW_STRIPE);
			getOwnCellRenderer().getFormatter().setRowStripeColor(color);
			return;
		}
		if(LogBoardConfigurationConstants.LOGBOARD_LOG_RECORD_BUFFER_SIZE.equals(arg)) {
			fTableModel.setBufferSize(
				ConfigurationMgr.getInt(LogBoardComponent.NAME, LogBoardConfigurationConstants.LOGBOARD_LOG_RECORD_BUFFER_SIZE));
			return;
		}
	}

	/**
	 * @see com.ixora.rms.ui.dataviewboard.utils.TableBasedControl#createHTMLProvider()
	 */
	protected TableBasedControlHTMLProvider createHTMLProvider() {
		return new TableBasedControlHTMLProvider(fTableModelSorter, null);
	}

	/**
	 * @see com.ixora.rms.ui.dataviewboard.utils.TableBasedControl#createTableCellEditor()
	 */
	protected TableCellEditor createTableCellEditor() {
		return new TableBasedControlCellEditor(getOwnCellRenderer().getFormatter());
	}

	/**
	 * @see com.ixora.rms.ui.dataviewboard.DataViewControl#cleanup()
	 */
	protected void cleanup() {
		ConfigurationMgr.get(LogBoardComponent.NAME).deleteObserver(this);
		super.cleanup();
	}

	/**
	 * Overriden to assign a different editor to the last table column which will launch
	 * the dialog to view log messages.
	 * @see com.ixora.rms.ui.dataviewboard.utils.TableBasedControl#setTableCellRendererAndEditor()
	 */
    protected void setTableCellRendererAndEditor() {
    	if(fValueCellEditor == null) {
    		fValueCellEditor = new ButtonCellEditor(
    				getOwnCellRenderer().getFormatter(),
    				fControlContext.getViewContainer().getAppFrame(),
    				fTableModelSorter) {
						private static final long serialVersionUID = 6227888713316461137L;

						/**
    					 * @see com.ixora.rms.ui.dataviewboard.utils.ButtonCellEditor#mapColumnForValue(int)
    					 */
						protected int mapColumnForValue(int column) {
							return column - 1;
						}
    				};
    	}
    	if(fValueCellRenderer == null) {
    		fValueCellRenderer = new ButtonCellRenderer();
    	}
        TableColumnModel columnModel = this.fTable.getColumnModel();
        int cols = columnModel.getColumnCount();
        TableColumn col;
        for(int i = 0; i < cols; i++) {
            col = columnModel.getColumn(i);
            if(i != cols - 1) {
            	col.setCellEditor(fCellEditor);
            	col.setCellRenderer(fCellRenderer);
            } else {
            	col.setCellRenderer(fValueCellRenderer);
            	col.setCellEditor(fValueCellEditor);
            }
        }
    }

	/**
	 * Overriden to set the size of the last column and to resize the 'Property' column.
	 * @see com.ixora.rms.ui.dataviewboard.utils.TableBasedControl#setColumnsWitdh()
	 */
	protected void setColumnsWitdh() {
		super.setColumnsWitdh();
        TableColumnModel columnModel = this.fTable.getColumnModel();
        TableColumn column = columnModel.getColumn(columnModel.getColumnCount() - 1);
        column.setMaxWidth(16);
	}

	/**
	 * @return
	 */
	private LogControlTableCellRenderer getOwnCellRenderer() {
		return (LogControlTableCellRenderer)fCellRenderer;
	}
}
