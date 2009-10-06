/*
 * Created on 20-Oct-2004
 */
package com.ixora.rms.ui.dataviewboard.tables;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ixora.common.logging.AppLogger;
import com.ixora.common.logging.AppLoggerFactory;
import com.ixora.common.ui.filter.FilterEditorDialogDate;
import com.ixora.common.ui.filter.FilterEditorDialogString;
import com.ixora.common.utils.Utils;
import com.ixora.rms.CounterType;
import com.ixora.rms.client.locator.SessionArtefactInfoLocator;
import com.ixora.rms.data.CounterValue;
import com.ixora.rms.data.CounterValueDouble;
import com.ixora.rms.data.CounterValueString;
import com.ixora.rms.dataengine.Cube;
import com.ixora.rms.dataengine.QueryResult;
import com.ixora.rms.dataengine.QueryResultData;
import com.ixora.rms.dataengine.Style;
import com.ixora.rms.dataengine.external.QueryData;
import com.ixora.rms.dataengine.external.QuerySeries;
import com.ixora.rms.ui.dataviewboard.charts.RMSDefaultCategoryDataset;
import com.ixora.rms.ui.dataviewboard.tables.definitions.TableDef;
import com.ixora.rms.ui.dataviewboard.utils.FilterNumericValueDeltaHandlerEditorDialog;
import com.ixora.rms.ui.dataviewboard.utils.NumericValueDeltaHandler;
import com.ixora.rms.ui.dataviewboard.utils.NumericValueDeltaHandlerContext;
import com.ixora.rms.ui.dataviewboard.utils.TableBasedControlTableModel;


/**
 * @author Daniel Moraru
 */
public class TableControlModel extends TableBasedControlTableModel implements NumericValueDeltaHandlerContext {
	/** Logger */
	private static final AppLogger logger = AppLoggerFactory.getLogger(RMSDefaultCategoryDataset.class);
    /** Table definition */
    private TableDef fDefinition;
    /**
     * Key: CategoryId
     * Value: Map of DataNumber and String keyed by their id
     */
    private LinkedHashMap<String, CategoryData> fModelData;
    /** Column names */
    private List<String> fColumnNames;
    /** Column descriptions */
    private List<String> fColumnDescriptions;
    /** Column ids */
    private List<String> fColumnIds;
    /** List of filter UI classes, one for every column */
    private Class[] fColumnFilterUIClasses;
    /**
     * If true, categories that haven't been updated during the last
     * call to <code>inspectData(QueryData)</code> will be removed.
     * This is to support scenarios with fast volatile entities;
     * this flag should not be true in scenarios where some categories are
     * provided by different agent data buffers, so by default this will be set
     * to false.
     */
    private boolean fRemoveStaleCategories;
    /**
     * True if the columns have been translated, false otherwise; if false
     * the columns are translated when a new data sample arrives.
     */
    private boolean fColumnsAreTranslated;
    /** Cached realized query, needed for localization of column data */
    private Cube fRealizedQuery;
    /**
     * Table data formatter.
     */
    private TableControlFormatter fFormatter;

    /**
     * Holds category data.
     */
    private final static class CategoryData {
    	/** Values for this category */
    	private LinkedHashMap<String, Object> fData;
    	/** The value for this category */
    	private String fValue;
    	/** True if the category data hasn't been updated */
        private boolean fStale;

        /**
         * @param categoryValue
         * @param ids Used to create the category values in the order specified in the given list
         */
		CategoryData(String categoryValue, List<String> ids) {
			super();
			this.fData = new LinkedHashMap<String, Object>();
            for(String id : ids) {
                this.fData.put(id, null);
            }
			this.fValue = categoryValue;
		}

        void setValueForColumn(String id, Object val) {
            fData.put(id, val);
            fStale = false;
        }

        void reset() {
            fStale = true;
        }

        Object getValueForColumn(String id) {
            return fData.get(id);
        }

        String getCategory() {
            return fValue;
        }

        Iterator<Object> values() {
            return fData.values().iterator();
        }

        boolean isStalled() {
            return fStale;
        }
    }

    /**
     * Constructor.
     * @param locator
     * @param tabledef
     * @param deltaHistorySize
     * @param defaultNumberFormat
     */
    public TableControlModel(SessionArtefactInfoLocator locator,
            TableDef tabledef, Cube realizedQuery,
    		TableControlFormatter formatter) {
        super();
        fDefinition = tabledef;
        fRealizedQuery = realizedQuery;
        fFormatter = formatter;
        fColumnNames = new LinkedList<String>();
        fColumnDescriptions = new LinkedList<String>();
        fColumnIds = new LinkedList<String>();
        fRemoveStaleCategories = tabledef.removeStaleCategories();

        setUpColumns(realizedQuery);

        fModelData = new LinkedHashMap<String, CategoryData>();

        fFormatter.addListener(new TableControlFormatter.Listener(){
			public void formattingChanged() {
				fireTableDataChanged();
			}});
    }

    /**
     * Sets up the columns in the table.
     * @param realizedQuery
     */
    private void setUpColumns(Cube realizedQuery) {
        fColumnNames.clear();
        fColumnDescriptions.clear();
        fColumnIds.clear();
        fFormatter.reset();

        realizedQuery.localize();
        List<QueryResult> lst = realizedQuery.getQueryResults();

        String categoryIdentifier = this.fDefinition.getCategory();
        Set<String> columnIdentifiers = this.fDefinition.getColumns();

        List<NumberFormat> columnNumberFormats = new LinkedList<NumberFormat>();
        List<DateFormat> columnDateFormats = new LinkedList<DateFormat>();

        // if no columns defined, drive from the query results
        // and make sure the category will be the first column
        fColumnsAreTranslated = true;
        if(columnIdentifiers.size() == 0) {
            for(QueryResult qr : lst) {
            	if(!qr.isLocalized()) {
            		fColumnsAreTranslated = false;
            	}
                String identifier = qr.getStyle().getID();
                String tName = qr.getStyle().getName();
                String tDesc = qr.getStyle().getDescription();
                if(Utils.isEmptyString(tDesc)) {
                    tDesc = tName;
                }
                if(categoryIdentifier.equals(identifier)) {
                    fColumnNames.add(0, tName);
                    fColumnDescriptions.add(0, tDesc);
                    fColumnIds.add(0, identifier);
                    String f = qr.getStyle().getFormat();
                    if(f != null) {
                        columnNumberFormats.add(0, new DecimalFormat(f));
                    } else {
                    	columnNumberFormats.add(0, fFormatter.getDefaultNumberFormat());
                    }
                    String t = qr.getStyle().getType();
                    if(Style.TYPE_DATE.equals(t)) {
                    	columnDateFormats.add(fFormatter.getDefaultDateFormat());
                    } else {
                    	columnDateFormats.add(null);
                    }
                } else {
                    fColumnNames.add(tName);
                    fColumnDescriptions.add(tDesc);
                    fColumnIds.add(identifier);
                    String f = qr.getStyle().getFormat();
                    if(f != null) {
                        columnNumberFormats.add(new DecimalFormat(f));
                    } else {
                        columnNumberFormats.add(fFormatter.getDefaultNumberFormat());
                    }
                    String t = qr.getStyle().getType();
                    if(Style.TYPE_DATE.equals(t)) {
                    	columnDateFormats.add(fFormatter.getDefaultDateFormat());
                    } else {
                    	columnDateFormats.add(null);
                    }
                }
            }
        } else { // columns defined, so must keep column order as specified therefor drive
            // from the columnIdentitifers
            for(String columnId : columnIdentifiers) {
                // find query result that matches
                QueryResult match = null;
                for(QueryResult qr : lst) {
                    String identifier = qr.getStyle().getID();
                    if(identifier.equals(columnId)) {
                        match = qr;
                        break;
                    }
                }
                if(match != null) {
                	if(!match.isLocalized()) {
                		fColumnsAreTranslated = false;
                	}
                    String tName = match.getStyle().getName();
                    String tDesc = match.getStyle().getDescription();
                    if(Utils.isEmptyString(tDesc)) {
                        tDesc = tName;
                    }
                    fColumnNames.add(tName);
                    fColumnDescriptions.add(tDesc);
                    fColumnIds.add(columnId);
                    String f = match.getStyle().getFormat();
                    if(f != null) {
                        columnNumberFormats.add(new DecimalFormat(f));
                    } else {
                    	columnNumberFormats.add(fFormatter.getDefaultNumberFormat());
                    }
                    String t = match.getStyle().getType();
                    if(Style.TYPE_DATE.equals(t)) {
                    	columnDateFormats.add(fFormatter.getDefaultDateFormat());
                    } else {
                    	columnDateFormats.add(null);
                    }
                }
            }
            // now insert at first position the category
            for(QueryResult qr : lst) {
                String identifier = qr.getStyle().getID();
                if(identifier.equals(categoryIdentifier)) {
                    String tName = qr.getStyle().getName();
                    String tDesc = qr.getStyle().getDescription();
                    if(Utils.isEmptyString(tDesc)) {
                        tDesc = tName;
                    }
                    fColumnNames.add(0, tName);
                    fColumnDescriptions.add(0, tDesc);
                    fColumnIds.add(0, identifier);
                    String f = qr.getStyle().getFormat();
                    if(f != null) {
                        columnNumberFormats.add(0, new DecimalFormat(f));
                    } else {
                    	columnNumberFormats.add(0, fFormatter.getDefaultNumberFormat());
                    }
                    columnDateFormats.add(0, null);
                    break;
                }
            }
        }
        if(!Utils.isEmptyCollection(columnDateFormats)) {
        	fFormatter.setFormatsForColumns(columnNumberFormats, columnDateFormats);
        }
    }

    /**
     * @param data
     * @return true if the category set has been changed
     */
     public boolean inspectData(QueryData data) {
        try {
        	// if columns are not yet translated, try again...
        	if(!fColumnsAreTranslated) {
        		setUpColumns(fRealizedQuery);
        		fireTableStructureChanged();
        	}

            // reset all entries first in order to detect stale entries
            if(fRemoveStaleCategories) {
                for(CategoryData cd : fModelData.values()) {
                    cd.reset();
                }
            }

	    	// flag set to true when a new category will be added
	    	boolean newCategoryAdded = false;
			for(Iterator itS = data.iterator(); itS.hasNext();) {
				QuerySeries series = (QuerySeries)itS.next();
				QueryResultData qrdCtg = series.getData(fDefinition.getCategory());
				String category = qrdCtg.getQueryResult().getStyle().getIName();
				String categoryValue = qrdCtg.getValue().toString();

				// create category in model data
				CategoryData categoryData = fModelData.get(category);
				if(categoryData == null) {
				    categoryData = new CategoryData(categoryValue, fColumnIds);
				    fModelData.put(category, categoryData);
                    newCategoryAdded = true;
				}

				boolean setUpFilterUIClasses = false;
				if(fColumnFilterUIClasses == null) {
					setUpFilterUIClasses = true;
					fColumnFilterUIClasses = new Class[fColumnIds.size()];
				}
				// update data set
				for(Iterator itD = series.iterator(); itD.hasNext();) {
					QueryResultData qrd = (QueryResultData) itD.next();
					String id = qrd.getQueryResult().getStyle().getID();
					if(id.equals(fDefinition.getCategory())) {
						categoryData.setValueForColumn(id, category);
						if(setUpFilterUIClasses) {
							fColumnFilterUIClasses[fColumnIds.indexOf(id)]
							                       = FilterEditorDialogString.class;
						}
					} else {
						int idx = fColumnIds.indexOf(id);
						if(idx >= 0) {
						    Object obj = categoryData.getValueForColumn(id);
						    CounterValue cv = qrd.getValue();
						    CounterType ctype = qrd.getType();
						    if(cv instanceof CounterValueDouble) {
						    	DateFormat dateFormat = fFormatter.getDateFormatForColumn(idx);
					    		if(dateFormat == null && ctype == CounterType.DATE) {
				    				dateFormat = fFormatter.getDefaultDateFormat();
				    				fFormatter.setDateFormatForColumn(idx, dateFormat);
					    		}
						    	if(dateFormat == null) {
							        if(obj == null) {
							            obj = new NumericValueDeltaHandler(this, cv);
							            categoryData.setValueForColumn(id, obj);
							        } else {
							            ((NumericValueDeltaHandler)obj).setValue(cv);
							        }
									if(setUpFilterUIClasses) {
										fColumnFilterUIClasses[idx] = FilterNumericValueDeltaHandlerEditorDialog.class;
									}
						    	} else {
						    		// date type so convert the double to long then to date string
						    		categoryData.setValueForColumn(id, new Date((long)cv.getDouble()));
									if(setUpFilterUIClasses) {
										fColumnFilterUIClasses[idx] = FilterEditorDialogDate.class;
									}
						    	}
						    } else if(cv instanceof CounterValueString) {
					            categoryData.setValueForColumn(id, cv.toString());
								if(setUpFilterUIClasses) {
									fColumnFilterUIClasses[idx] = FilterEditorDialogString.class;
								}
						    } else { // CounterValueObject
					            categoryData.setValueForColumn(id, cv.toString());
								if(setUpFilterUIClasses) {
									fColumnFilterUIClasses[idx] = FilterEditorDialogString.class;
								}
						    }
						}
					}
				}
            }

            // remove stale entries
            if(fRemoveStaleCategories) {
                for(Iterator<Map.Entry<String, CategoryData>> iterator = fModelData.entrySet().iterator(); iterator.hasNext();) {
                    Map.Entry<String, CategoryData> me = iterator.next();
                    if(me.getValue().isStalled()) {
                        // remove from model
                        iterator.remove();
                    }
                }
            }

            fireTableDataChanged();
			return newCategoryAdded;
	    } catch (Exception e) {
	        // QueryNoSuchResult should not happen
            if(logger.isTraceEnabled()) {
                logger.error("Error in inspectData", e);
            }
	        return false;
	    }
	}

    /**
     * @see javax.swing.table.TableModel#getColumnCount()
     */
    public int getColumnCount() {
        return fColumnNames.size();
    }

    /**
     * @see javax.swing.table.TableModel#getColumnName(int)
     */
    public String getColumnName(int column) {
        return fColumnNames.get(column);
    }

    /**
     * @see javax.swing.table.TableModel#getRowCount()
     */
    public int getRowCount() {
        return fModelData.size();
    }

    /**
     * @see javax.swing.table.TableModel#getValueAt(int, int)
     */
    public Object getValueAt(int rowIndex, int columnIndex) {
        int i = 0;
        for(CategoryData row : fModelData.values()) {
            if(i == rowIndex) {
                int j = 0;
                for(Iterator iterator = row.values(); iterator.hasNext(); ++j) {
                    Object tmp = iterator.next();
                    // because of this bit here (instead of comparing strings compare numbers)
                    // we must make sure the category data keeps it's data sorted in the same order as columnNames
                    if(j == columnIndex) {
                        return tmp;
                    }
                }
            }
            ++i;
        }
        return null;
    }

	/**
	 * @see com.ixora.common.ui.filter.TableModellWithFilterSupport#getColumnDescription(int)
	 */
    public String getColumnDescription(int colIndex) {
        return this.fColumnDescriptions.get(colIndex);
    }

	/**
	 * @see com.ixora.rms.ui.dataviewboard.utils.NumericValueDeltaHandlerContext#getDeltaHistorySize()
	 */
	public int getDeltaHistorySize() {
		return fFormatter.getDeltaHandler().getDeltaHistoryDepth();
	}

    /**
     * @param idx
     * @return the value for the category at the given row
     */
    public String getCategoryValueAtRow(int rowIndex) {
        int i = 0;
        for(String key : fModelData.keySet()) {
            if(i == rowIndex) {
            	return fModelData.get(key).getCategory();
            }
            ++i;
        }
        return null;
    }

    /**
     * @param id
     * @return true if the given id matches the id of a column
     */
    public boolean isColumn(String id) {
    	return fColumnIds.contains(id);
    }

    /**
     * @see com.ixora.rms.ui.dataviewboard.utils.TableBasedControlTableModel#reset()
     */
	public void reset() {
    	fModelData.clear();
    	fireTableDataChanged();
	}

	/**
	 * @see com.ixora.common.ui.filter.TableModellWithFilterSupport#getColumnNames()
	 */
	public String[] getColumnNames() {
		return fColumnNames.toArray(new String[fColumnNames.size()]);
	}

	/**
	 * @see com.ixora.rms.ui.dataviewboard.utils.TableBasedControlTableModel#getFilterUIClasses()
	 */
	public Class[] getFilterUIClasses() {
		return fColumnFilterUIClasses;
	}

// package
    /**
     * @param remove
     */
    void setRemoveStaleCategories(boolean remove) {
       this.fRemoveStaleCategories = remove;
    }
}
