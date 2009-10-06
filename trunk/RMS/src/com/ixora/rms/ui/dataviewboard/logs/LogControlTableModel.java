/**
 * 07-Feb-2006
 */
package com.ixora.rms.ui.dataviewboard.logs;

import java.util.Date;
import java.util.List;

import com.ixora.common.MessageRepository;
import com.ixora.common.collections.CircullarLinkedList;
import com.ixora.common.logging.AppLogger;
import com.ixora.common.logging.AppLoggerFactory;
import com.ixora.common.ui.filter.FilterEditorDialogDate;
import com.ixora.common.ui.filter.FilterEditorDialogNumber;
import com.ixora.common.ui.filter.FilterEditorDialogString;
import com.ixora.common.utils.Utils;
import com.ixora.rms.agents.impl.logfile.LogRecord;
import com.ixora.rms.agents.impl.logfile.LogRecordBatch;
import com.ixora.rms.client.locator.SessionArtefactInfoLocator;
import com.ixora.rms.data.CounterValue;
import com.ixora.rms.data.CounterValueObject;
import com.ixora.rms.dataengine.Cube;
import com.ixora.rms.dataengine.QueryResultData;
import com.ixora.rms.dataengine.external.QueryData;
import com.ixora.rms.dataengine.external.QuerySeries;
import com.ixora.rms.ui.dataviewboard.logs.definitions.LogDef;
import com.ixora.rms.ui.dataviewboard.logs.messages.Msg;
import com.ixora.rms.ui.dataviewboard.utils.TableBasedControlTableModel;

/**
 * @author Daniel Moraru
 */
public class LogControlTableModel extends TableBasedControlTableModel {
	private static final AppLogger logger = AppLoggerFactory.getLogger(LogControlTableModel.class);
	private static final String[] columnNames = new String[]{
		MessageRepository.get(LogBoardComponent.NAME, Msg.LOGBOARD_COLUMN_TIMESTAMP),
		MessageRepository.get(LogBoardComponent.NAME, Msg.LOGBOARD_COLUMN_SEQ),
		MessageRepository.get(LogBoardComponent.NAME, Msg.LOGBOARD_COLUMN_SEVERITY),
		MessageRepository.get(LogBoardComponent.NAME, Msg.LOGBOARD_COLUMN_THREAD_ID),
		MessageRepository.get(LogBoardComponent.NAME, Msg.LOGBOARD_COLUMN_COMPONENT),
		MessageRepository.get(LogBoardComponent.NAME, Msg.LOGBOARD_COLUMN_CLASS),
		MessageRepository.get(LogBoardComponent.NAME, Msg.LOGBOARD_COLUMN_METHOD),
		MessageRepository.get(LogBoardComponent.NAME, Msg.LOGBOARD_COLUMN_LINE),
		MessageRepository.get(LogBoardComponent.NAME, Msg.LOGBOARD_COLUMN_MESSAGE),
		""
	};
	private static final String[] columnDescriptions = new String[]{
		MessageRepository.get(LogBoardComponent.NAME, Msg.LOGBOARD_COLUMN_TIMESTAMP + ".desc"),
		MessageRepository.get(LogBoardComponent.NAME, Msg.LOGBOARD_COLUMN_SEQ + ".desc"),
		MessageRepository.get(LogBoardComponent.NAME, Msg.LOGBOARD_COLUMN_SEVERITY + ".desc"),
		MessageRepository.get(LogBoardComponent.NAME, Msg.LOGBOARD_COLUMN_THREAD_ID + ".desc"),
		MessageRepository.get(LogBoardComponent.NAME, Msg.LOGBOARD_COLUMN_COMPONENT + ".desc"),
		MessageRepository.get(LogBoardComponent.NAME, Msg.LOGBOARD_COLUMN_CLASS + ".desc"),
		MessageRepository.get(LogBoardComponent.NAME, Msg.LOGBOARD_COLUMN_METHOD + ".desc"),
		MessageRepository.get(LogBoardComponent.NAME, Msg.LOGBOARD_COLUMN_LINE + ".desc"),
		MessageRepository.get(LogBoardComponent.NAME, Msg.LOGBOARD_COLUMN_MESSAGE + ".desc"),
		""
	};
	private static final Class[] filterUIClasses = new Class[]{
		FilterEditorDialogDate.class, // timestamp
		FilterEditorDialogNumber.class, // seq no
		FilterEditorDialogString.class, // severity
		FilterEditorDialogString.class, // thread id
		FilterEditorDialogString.class, // component
		FilterEditorDialogString.class, // class
		FilterEditorDialogString.class, // method
		FilterEditorDialogNumber.class, // line no
		FilterEditorDialogString.class, // message
		null
	};

	private class LogRecordData {
		LogRecord fRecord;
		Date fTimestampDate;

		LogRecordData(LogRecord rec) {
			fRecord = rec;
			fTimestampDate = new Date(rec.getTimestamp());
		}
	}

	/** Data buffer */
	private CircullarLinkedList<LogRecordData> fRecords;

	/**
	 * @param locator
	 * @param def
	 * @param cube
	 * @param buffSize
	 */
	public LogControlTableModel(
			SessionArtefactInfoLocator locator,
			LogDef def,
			Cube cube, int buffSize) {
		super();
		fRecords = new CircullarLinkedList<LogRecordData>(buffSize);
	}

	/**
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	public int getRowCount() {
		return fRecords.size();
	}

	/**
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	public int getColumnCount() {
		return columnNames.length;
	}


	/**
	 * @see javax.swing.table.AbstractTableModel#getColumnName(int)
	 */
	public String getColumnName(int column) {
		return columnNames[column];
	}

	/**
	 * @see com.ixora.common.ui.filter.TableModellWithFilterSupport#getColumnDescription(int)
	 */
	public String getColumnDescription(int column) {
		return columnDescriptions[column];
	}

	/**
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	public Object getValueAt(int rowIndex, int columnIndex) {
		// return last record first (to be at the top of the table)
		LogRecordData record = fRecords.get(fRecords.size() - 1 - rowIndex);
		Object ret = getColumnValueForRecord(columnIndex, record);
		if(ret == null) {
			return "-";
		}
		return ret;
	}

	/**
	 * @param columnIndex
	 * @param record
	 * @return
	 */
	private Object getColumnValueForRecord(int columnIndex, LogRecordData record) {
		switch(columnIndex) {
			case 0:
				return record.fTimestampDate;
			case 1:
				return record.fRecord.getSequenceNumber();
			case 2:
				return record.fRecord.getSeverity();
			case 3:
				return record.fRecord.getThread();
			case 4:
				return record.fRecord.getSourceComponent();
			case 5:
				return record.fRecord.getSourceClass();
			case 6:
				return record.fRecord.getSourceMethod();
			case 7:
				return record.fRecord.getSourceLineNumber();
			case 8:
				return record.fRecord.getMessage();
			case 9:
				return record.fRecord.getMessage();
		}
		return null;
	}

	/**
	 * @see com.ixora.rms.ui.dataviewboard.utils.TableBasedControlTableModel#inspectData(com.ixora.rms.dataengine.external.QueryData)
	 */
	public boolean inspectData(QueryData qd) {
		try {
			// extract LogRecord
			if(qd.size() == 0) {
				return false;
			}
			QuerySeries series = qd.getFirst();
			if(series.size() == 0) {
				return false;
			}
			for(QueryResultData qrd : series) {
				CounterValue val = qrd.getValue();
				if(!(val instanceof CounterValueObject)) {
					continue;
				}
				CounterValueObject valObj = (CounterValueObject)val;
				List<LogRecord> recs = ((LogRecordBatch)valObj.getValue()).getLogRecords();
				if(!Utils.isEmptyCollection(recs)) {
					for(LogRecord lr : recs) {
						LogRecordData data = new LogRecordData(lr);
						fRecords.add(data);
					}
				}
				fireTableDataChanged();
			}
		} catch(Exception e) {
			logger.error(e);
		}
		return false;
	}

	/**
	 *
	 */
	public void setBufferSize(int buffSize) {
		fRecords.resize(buffSize);
		fireTableDataChanged();
	}

	/**
	 * @see com.ixora.rms.ui.dataviewboard.utils.TableBasedControlTableModel#reset()
	 */
	public void reset() {
		fRecords.clear();
		fireTableDataChanged();
	}

	/**
	 * @see com.ixora.common.ui.filter.TableModellWithFilterSupport#getColumnNames()
	 */
	public String[] getColumnNames() {
		return columnNames;
	}

	/**
	 * @see com.ixora.rms.ui.dataviewboard.utils.TableBasedControlTableModel#getFilterUIClasses()
	 */
	public Class[] getFilterUIClasses() {
		return filterUIClasses;
	}
}
