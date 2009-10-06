/*
 * Created on 11-Jun-2005
 */
package com.ixora.rms.agents.providers.parsers.property;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ixora.common.utils.Utils;
import com.ixora.rms.CounterDescriptor;
import com.ixora.rms.CounterId;
import com.ixora.rms.CounterType;
import com.ixora.rms.EntityDescriptor;
import com.ixora.rms.EntityId;
import com.ixora.rms.agents.providers.parsers.AbstractMonitoringDataParser;
import com.ixora.rms.data.CounterValueDouble;
import com.ixora.rms.data.CounterValueString;
import com.ixora.rms.providers.parsers.ParsingRulesDefinition;
import com.ixora.rms.providers.parsers.exception.InvalidData;
import com.ixora.rms.providers.parsers.exception.InvalidParsingRules;

/**
 * PropertiesParser. Parses a properties style chunk of data, i.e data whose rows
 * are mostly of the form
 * <p>
 * property[separator]value
 * </p>
 * with certain rows, rows that only have one string representing entities; we support
 * a hierarchy of entities by checking the identation of this entity rows.
 */
public final class PropertyParser extends AbstractMonitoringDataParser {
    /** Parsing rules */
    private PropertyRulesDefinition fRules;
    /** Pattern to use to find line to be ignored */
    private Pattern fPatternIgnoreLines;
    /** Pattern to use to find entities */
    private Pattern fPatternEntity;
    /** Pattern to use to find counter and values */
    private Pattern fPatternCounterValue;
    /** Base entity */
    private EntityId fBaseEntity;

    /**
     * Constructor.
     */
    public PropertyParser() {
        super();
        fHasVolatileEntities = false;
    }

    /**
     * @see com.ixora.rms.agents.providers.parsers.MonitoringDataParser#isEntityFinal(com.ixora.rms.EntityId)
     */
    public boolean isEntityFinal(EntityId eid) {
        // this parser doesn't use non-final entities
        return true;
    }

    /**
     * @throws InvalidParsingRules
     * @see com.ixora.rms.providers.parsers.Parser#setRules(com.ixora.rms.providers.parsers.ParsingRulesDefinition)
     */
    public void setRules(ParsingRulesDefinition rules) throws InvalidParsingRules {
        this.fRules = (PropertyRulesDefinition)rules;
        this.fBaseEntity = this.fRules.getBaseEntity();
        if(this.fBaseEntity == null) {
            this.fBaseEntity = new EntityId("root");
        }
        String tmp = this.fRules.getIgnoreLines();
        if(!Utils.isEmptyString(tmp)) {
            this.fPatternIgnoreLines = Pattern.compile(tmp);
        } else {
            this.fPatternIgnoreLines = null;
        }
        tmp = this.fRules.getPropertyValueRegex();
        if(!Utils.isEmptyString(tmp)) {
            this.fPatternCounterValue = Pattern.compile(tmp);
        } else {
            this.fPatternCounterValue = null;
        }
        tmp = this.fRules.getEntityRegex();
        if(!Utils.isEmptyString(tmp)) {
            this.fPatternEntity = Pattern.compile(tmp);
        } else {
            this.fPatternEntity = null;
        }
    }

    /**
     * @param line
     * @return true if the given line must be ignored
     */
    private boolean ignoreLine(String line) {
        if(line == null || line.length() == 0) {
            return true;
        }
        if(this.fPatternIgnoreLines == null) {
            return false;
        }
        return fPatternIgnoreLines.matcher(line).find();
    }

    /**
     * @see com.ixora.rms.agents.providers.parsers.AbstractMonitoringDataParser#doParsing(java.lang.Object)
     */
    protected void doParsing(Object obj) throws InvalidData {
        EntityId currentEntity = fBaseEntity;
        Map<EntityId, String[][]> tables = new HashMap<EntityId, String[][]>();
        // build the property value table to work on
        if(obj instanceof String[]) {
            if(fPatternCounterValue == null) {
                // TODO localize
                throw new InvalidData("Pattern Counter/Value not set");
            }
            String[][] table;
            // build table from rows
            List<String[]> lst = new LinkedList<String[]>();
            String[] rows = (String[])obj;
            // skip counters when the current entity is not accepted
            boolean skipCounters = false;
            for(int i = 0; i < rows.length; i++) {
                String row = rows[i];
                if(ignoreLine(row)) {
                    continue;
                }
                boolean isCounterRow = true;
                if(fPatternEntity != null) {
                    Matcher matcher = fPatternEntity.matcher(row);
                    if(matcher.matches()) {
                        isCounterRow = false;
                        String token = matcher.group(1);
                        if(token.length() > 0) {
                            if(lst.size() > 0) {
                                // assign table built so far
                                table = lst.toArray(new String[lst.size()][]);
                                tables.put(currentEntity, table);
                                lst.clear();
                            }
                            // set new entity if it has descriptors
                            currentEntity = new EntityId(fBaseEntity, token);
                            if(fDescriptors.get(currentEntity) == null) {
                                // out as the descriptors are used as
                                // an extra filter
                                skipCounters = true;
                            } else {
                                skipCounters = false;
                            }
                        }
                    }
                }
                if(isCounterRow && !skipCounters) {
                    Matcher matcher = fPatternCounterValue.matcher(row);
                    if(matcher.matches() && matcher.groupCount() == 2) {
                        String[] tokens = new String[2];
                        // in tokens counter is expected to be first
                        if(fRules.isValueSecondMatch()) {
                            tokens[1] = matcher.group(2).trim();
                            tokens[0] = matcher.group(1).trim();
                        } else {
                            tokens[1] = matcher.group(1).trim();
                            tokens[0] = matcher.group(2).trim();
                        }
                        lst.add(tokens);
                    }
                }
            }
            // assign last table
            if(lst.size() > 0) {
                table = lst.toArray(new String[lst.size()][]);
                tables.put(currentEntity, table);
                lst.clear();
            }
        } else {
            // cast straight away
            tables.put(currentEntity, (String[][])obj);
        }

        // create counters and assign values from tables for every entity discovered
        for(Map.Entry<EntityId, String[][]> entry : tables.entrySet()) {
            EntityId entityId = entry.getKey();
            String[][] table = entry.getValue();
            EntityData entityData = fPerCycleEntityData.get(entityId);
            if(entityData == null) {
                entityData = new EntityData(entityId);
                fPerCycleEntityData.put(entityId, entityData);
            }

            for(int i = 0; i < table.length; i++) {
                String[] row = table[i];
                String counter = row[0];
                String value = row[1];

                CounterId counterId = new CounterId(counter);
                EntityDescriptor edesc = fDescriptors.get(entityId);
                if(edesc != null) {
                    CounterDescriptor cdesc = edesc.getCounterDescriptor(counterId);
                    if(cdesc != null) {
                        entityData.counterIds.add(counterId);
                        if(cdesc.getType() != CounterType.STRING) {
                            entityData.values.add(new CounterValueDouble(
                                    value == null ? 0 : Double.parseDouble(value)));
                        } else {
                            entityData.values.add(new CounterValueString(
                                    value == null ? "" : value));
                        }
                    }
                }
            }
        }
    }
}
