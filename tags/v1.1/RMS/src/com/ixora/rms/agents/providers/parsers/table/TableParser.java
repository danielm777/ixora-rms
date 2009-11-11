/*
 * Created on 03-Jan-2005
 */
package com.ixora.rms.agents.providers.parsers.table;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import com.ixora.common.utils.Utils;
import com.ixora.rms.CounterDescriptor;
import com.ixora.rms.CounterId;
import com.ixora.rms.CounterType;
import com.ixora.rms.EntityDescriptor;
import com.ixora.rms.EntityDescriptorImpl;
import com.ixora.rms.EntityId;
import com.ixora.rms.agents.providers.parsers.AbstractMonitoringDataParser;
import com.ixora.rms.data.CounterValueDouble;
import com.ixora.rms.data.CounterValueString;
import com.ixora.rms.providers.parsers.ParsingRulesDefinition;
import com.ixora.rms.providers.parsers.exception.InvalidData;
import com.ixora.rms.providers.parsers.exception.ParserException;

/**
 * Parser that handles table data.
 * @author Daniel Moraru
 */
// NOTE: a non-final entity is an entity whose id conains tokens that must be replaced with
// data from other columns
// eg. root/Process/{0#1} where all numbers will be replaced with data from the column at
// the same index
public final class TableParser extends AbstractMonitoringDataParser {
	// substitution tokens
	private static final char TOK_BEGIN = '{';
	private static final char TOK_END = '}';
	/** Columns */
	private Map<Integer, ColumnDefinition> fColumns;
	/** Column separator */
	private String fColumnSeparator;
	/** No of columns */
	private int fNoOfColumns;
	/** Pattern to use to find line to be ignored */
	private Pattern fIgnoreLinesPattern;
	/**
	 * When there are more columns in the table data and the number of columns
	 * specified in <code>fColumns</code> the columns at this indexes will be discarded
	 * and the data sample used.<br>
	 * If null any such samples will be discarded.
	 */
	private Integer[] fColumnsToIgnoreWhenMoreThanExpected;
	/** The id of the non final entity */
	private EntityId fNonFinalEntityId;
	/** True if required to convert single column data to a row */
	private boolean fConvertColumnToRow;

	/**
	 * Constructor.
	 */
	public TableParser() {
		super();
	}

	/**
	 * Replaces the tokens in the given entity string with
	 * @param entity
	 * @param tokens
	 * @return
	 * @throws InvalidData
	 */
	private String replaceTokens(String entity, String[] tokens) throws InvalidData {
		int idxStart = entity.indexOf(TOK_BEGIN);
		if(idxStart < 0) {
			return entity;
		}
		int idxFinish = entity.indexOf(TOK_END);
		if(idxFinish < 0 || idxFinish < idxStart) {
			return entity;
		}
		StringBuffer result = new StringBuffer();
		result.append(entity.substring(0, idxStart));
		String seg = entity.substring(idxStart + 1, idxFinish);
		int len = seg.length();
		StringBuffer tmp = new StringBuffer();
		for(int i =  0; i < len; ++i) {
			char c = seg.charAt(i);
			if(Character.isDigit(c)) {
				tmp.append(c);
				// look ahead for more digits
				for(int j = i + 1; j < len; ++j) {
					c = seg.charAt(j);
					if(Character.isDigit(c)) {
						tmp.append(c);
					} else {
						break;
					}
				}
				int idx = Integer.parseInt(tmp.toString());
				tmp.delete(0, tmp.length());
				if(idx < 0 || idx >= fNoOfColumns) {
					throw new InvalidData("Invalid entity in the parser definition " + entity +
							". Index " + idx + " is invalid.");
				}
                String tok = tokens[idx];
                if(tok == null) {
                    throw new InvalidData("Invalid data. Substitution value is null at index " + idx + ".");
                }
				result.append(EntityId.escapePathComponent(tok));
			} else {
				result.append(c);
			}
		}
		result.append(entity.substring(idxFinish, entity.length() - 1));
		return result.toString();
	}

	/**
	 * @see com.ixora.rms.providers.parsers.Parser#setRules(com.ixora.rms.providers.parsers.ParsingRulesDefinition)
	 */
	public void setRules(ParsingRulesDefinition rules) {
		TableRulesDefinition trules = (TableRulesDefinition)rules;
		String regex = trules.getIgnoreLines();
        if(!Utils.isEmptyString(regex)) {
            fIgnoreLinesPattern = Pattern.compile(regex);
        }
		fColumns = new HashMap<Integer, ColumnDefinition>();
		ColumnDefinition[] cds = trules.getColumns();
		for(ColumnDefinition cd : cds) {
			int idx = cd.getColumnIndex();
			if(fNoOfColumns <= idx) {
				fNoOfColumns = idx;
			}
			fColumns.put(new Integer(idx), cd);
			if(cd.isEntity()
					&& !isEntityFinal(cd.getEntityId())) {
				fHasVolatileEntities = true;
				fNonFinalEntityId = cd.getEntityId();
			}
		}
		fNoOfColumns++;
		fColumnSeparator = trules.getColumnSeparator();
		fAccumulateVolatileEntities = trules.accumulateVolatileEntities();
		fConvertColumnToRow = trules.convertColumnToRow();
		fColumnsToIgnoreWhenMoreThanExpected = trules.getColumnsToIgnoreWhenMoreThanExpected();
		if(!Utils.isEmptyArray(fColumnsToIgnoreWhenMoreThanExpected)) {
			// sort this array as it will be binary searched
			Arrays.sort(fColumnsToIgnoreWhenMoreThanExpected);
		}
	}

	/**
	 * @see com.ixora.rms.agents.providers.parsers.MonitoringDataParser#isEntityFinal(com.ixora.rms.EntityId)
	 */
	public boolean isEntityFinal(EntityId eid) {
		String eids = eid.toString();
		return eids.indexOf(TOK_BEGIN) < 0;
	}

	/**
	 * @param line
	 * @return true if the given line must be ignored
	 */
	private boolean ignoreLine(String line) {
        if(line == null || line.length() == 0) {
            return true;
        }
		if(this.fIgnoreLinesPattern == null) {
			return false;
		}
		return fIgnoreLinesPattern.matcher(line).find();
	}

    /**
     * @throws ParserException
     * @see com.ixora.rms.agents.providers.parsers.AbstractMonitoringDataParser#doParsing(java.lang.Object)
     */
    protected void doParsing(Object obj) throws ParserException {
        // build the table to work on
        String[][] table;
        if(obj instanceof String[]) {
            // build table from rows
            List<String[]> lst = new LinkedList<String[]>();
            String[] rows = (String[])obj;
            for(int i = 0; i < rows.length; i++) {
                String row = rows[i];
                if(ignoreLine(row)) {
                    continue;
                }
                StringTokenizer tok;
                if(Utils.isEmptyString(fColumnSeparator)) {
                    tok = new StringTokenizer(row);
                } else {
                    tok = new StringTokenizer(row, fColumnSeparator);
                }
                int ctok = tok.countTokens();
                String[] tokens = new String[ctok];
                for(int j = 0; j < ctok; ++j) {
                    if(tok.hasMoreTokens()) {
                        tokens[j] = tok.nextToken().trim();
                    } else {
                        tokens[j] = null;
                    }
                }
                lst.add(tokens);
            }
            table = lst.toArray(new String[lst.size()][]);
        } else {
            // cast straight away
            table = (String[][])obj;
        }
        if(table.length == 0) {
            return;
        }

        // check if we need to rotate data before processing it
		if(fConvertColumnToRow) {
			int columnCount = table[0].length;
			int rowCount = table.length;
			if(columnCount != 1) {
				throw new InvalidData(
						"Cannot convert single column data to row " +
						"because the number of columns is not 1 but " + columnCount,
						tableToString(table));
			}
			String[][] converted = new String[columnCount][rowCount];
			for(int i = 0; i < table.length; i++) {
				String[] row = table[i];
				converted[0][i] = row[0];
			}
			table = converted;
		}

        // capture all unexpected errors in order to
        // add contextual information
        try {
	        // for non-final entities
	        EntityDescriptor nfEntityDescriptor = null;
	        EntityData nfEntityData = null;

	        for(int i = 0; i < table.length; i++) {
	            String[] columns = table[i];
	            if(Utils.isEmptyArray(columns)) {
	            	continue;
	            }
	            if(fNoOfColumns != columns.length) {
	            	if(fNoOfColumns < columns.length) {
	            		// check if there are columns to ignore
	            		if(Utils.isEmptyArray(fColumnsToIgnoreWhenMoreThanExpected)){
	                		// just use the first fNoOfColumns
	            			String[] newColumms = new String[fNoOfColumns];
	            			for(int j = 0; j < fNoOfColumns; j++) {
								newColumms[j] = columns[j];
							}
	            		} else {
	            			// adjust columns
	            			int size = columns.length - fColumnsToIgnoreWhenMoreThanExpected.length;
	            			// sanity check
	            			if(size != fNoOfColumns) {
	            				throw new InvalidData(
										"Invalid data specified for columns to ignore. There are too many indexes." +
										"Descriptor size: " + fColumns.size()
										+ " Data size: " + columns.length,
										tableToString(table));
	            			}
	            			String[] newColumns = new String[size];
	            			int k = 0;
	            			for(int j = 0; j < columns.length && k < size; j++) {
								if(Arrays.binarySearch(fColumnsToIgnoreWhenMoreThanExpected, new Integer(j)) < 0){
									newColumns[k++] = columns[j];
								}
	            			}
	            			columns = newColumns;
	            		}
	            	} else {
	            		throw new InvalidData("There are less column in the table data than specified in the parsing rules and there are no columns to ignore in the parsing rules." +
	                        "Descriptor size: " + fColumns.size() + " Data size: " + columns.length,
	                        columnsToString(columns));
	            	}
	            }
	            boolean nfEntityDone = false;
	            for(int j = 0; j < columns.length; ++j) {
	                String column = columns[j];
	                ColumnDefinition columnDef = fColumns.get(new Integer(j));
	                if(columnDef == null) {
	                	continue;
	                }
	                if(!fHasVolatileEntities) {
	                    // simple case first
	                    EntityId eid = columnDef.getEntityId();
	                    // NOTE: a column can have a null entity id when it must be ignored
	                    if(eid != null) {
		                    EntityData edata = fPerCycleEntityData.get(eid);
		                    if(edata == null) {
		                        edata = new EntityData(eid);
		                        fPerCycleEntityData.put(eid, edata);
		                    }
		                    if(columnDef.isCounter()) {
		                        CounterId cid = columnDef.getCounterId();
		                        edata.counterIds.add(cid);
		                        EntityDescriptor edesc = fDescriptors.get(eid);
		                        CounterDescriptor cdesc = edesc.getCounterDescriptor(cid);
		                        if(cdesc.getType() != CounterType.STRING) {
		                            edata.values.add(new CounterValueDouble(
		                                    column == null ? 0 : Double.parseDouble(column)));
		                        } else {
		                            edata.values.add(new CounterValueString(
		                                    column == null ? "" : column));
		                        }
		                    }
	                    }
	                } else {
	                    // for final entities we can only have one entity per row
	                    // find out the entity name
	                    if(!nfEntityDone) {
	                        nfEntityDone = true;
	                        EntityId eid = new EntityId(replaceTokens(fNonFinalEntityId.toString(), columns));
	                        nfEntityData = new EntityData(eid);
	                        fPerCycleEntityData.put(eid, nfEntityData);
	                        nfEntityDescriptor = fDescriptors.get(eid);
	                        if(nfEntityDescriptor == null) {
	                            // find the descriptor for the original non-final entity
	                            nfEntityDescriptor = fDescriptors.get(fNonFinalEntityId);
	                            if(nfEntityDescriptor == null) {
	                                throw new InvalidData("Entity "
	                                		+ fNonFinalEntityId + " has no descriptor.",
	                                		tableToString(table));
	                            }
	                        }
	                        if(fCurrentEntities.get(eid) == null) {
	                            // new entity added, store descriptor for the event firing
	                            fCurrentEntities.put(eid, NEW);
	                            fPerCycleNewEntityDescriptors.put(eid,
	                                    new EntityDescriptorImpl(eid, nfEntityDescriptor));
	                        } else {
	                            fCurrentEntities.put(eid, SAME);
	                        }
	                    }
	                    if(columnDef.isCounter()) {
	                        CounterId cid = columnDef.getCounterId();
	                        nfEntityData.counterIds.add(cid);
	                        CounterDescriptor cdesc = nfEntityDescriptor.getCounterDescriptor(cid);
	                        if(cdesc.getType() != CounterType.STRING) {
	                        	try {
	                        		nfEntityData.values.add(new CounterValueDouble(
	                                    Utils.isEmptyString(column) ? 0 : Double.parseDouble(column)));
	                        	} catch(NumberFormatException e) {
	                        		// TODO localize
	                        		throw new InvalidData("Column "
	                        				+ column + " is not a number.",
	                        				columnsToString(columns));
	                        	}
	                        } else {
	                            nfEntityData.values.add(new CounterValueString(
	                                    column == null ? "" : column));
	                        }
	                    }
	                }
	            }
	        }
        } catch(InvalidData e) {
        	// invalid data errors should provide enough contextual info
			throw e;
        } catch(Exception e) {
			// add some contextual info to help diagnosing
        	// add the table data
        	throw new ParserException(e, tableToString(table));
		}
    }

    /**
     * @param arr
     * @return
     */
    private static String columnsToString(String[] arr) {
    	if(arr == null) {
    		return null;
    	}
    	StringBuffer buff = new StringBuffer();
    	for(int i = 0; i < arr.length; i++) {
			buff.append(arr[i]);
			if(i != arr.length - 1) {
				buff.append("|");
			}
		}
    	return buff.toString();
    }

    /**
     * @param table
     * @return
     */
    private static String tableToString(String[][] table) {
    	if(table == null) {
    		return null;
    	}
    	StringBuffer buff = new StringBuffer();
    	for(int i = 0; i < table.length; i++) {
			buff.append(columnsToString(table[i]));
			if(i != table.length - 1) {
				buff.append(Utils.getNewLine());
			}
		}
    	return buff.toString();
    }
}
