/**
 * 12-Jul-2005
 */
package com.ixora.rms.logging.data;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import com.ixora.common.exception.AppRuntimeException;
import com.ixora.common.utils.Utils;
import com.ixora.rms.CounterId;
import com.ixora.rms.EntityDataBuffer;
import com.ixora.rms.EntityDataBufferImpl;
import com.ixora.rms.EntityDescriptor;
import com.ixora.rms.EntityId;
import com.ixora.rms.RecordDefinition;
import com.ixora.rms.data.CounterValue;
import com.ixora.rms.data.CounterValueDouble;
import com.ixora.rms.data.CounterValueObject;
import com.ixora.rms.data.CounterValueString;

/**
 * @author Daniel Moraru
 */
public class AggEntityDataBuffer {
	/** Record definition */
	private RecordDefinition fDefinition;
	/** Monitored entity */
	private EntityId fEntity;
	/** Counter values */
	private List<AggCounterValue>[] fBuffer;
	/** The timestamp of the snapshot */
	private long fTimestamp;

	public AggEntityDataBuffer() {
		super();
	}

	/**
	 * @param buff
	 * @return false if the given entity buffer cannot be aggregated
	 */
	public boolean addEntityDataBuffer(EntityDataBuffer buff) {
		// check if the passed in buffer can be aggregated
		// it can't if the record definition is different
		if(fDefinition != null) {
			// update definition if they are equivalent
			RecordDefinition rdef = buff.getDefinition();
			if(rdef != null) {
				CounterId[] newCids = rdef.getFields();
				CounterId[] cids = fDefinition.getFields();
				if(!Arrays.equals(cids, newCids)) {
					return false;
				} else {
					fDefinition = rdef;
				}
			}
		} else {
			// first entity added
			fEntity = buff.getEntityId();
			fDefinition = buff.getDefinition();
			if(fDefinition == null) {
				throw new AppRuntimeException("Record definition missing for buffer of : " + buff.getEntityId());
			}
		}
		EntityDescriptor newEd = fDefinition.getEntityDescriptor();
		if(newEd != null) {
			// update definition
			fDefinition.setEntityDescriptor(newEd);
		}
		// update counter values
		// it is guarranteed that values.length is the same during
		// this aggregation
		CounterValue[][] values = buff.getBuffer();
		if(!Utils.isEmptyArray(values)) {
			if(fBuffer == null) {
				fBuffer = new List[values.length];
			}
			int i = 0;
			for(CounterValue[] cvs: values) {
				List<AggCounterValue> cvals = fBuffer[i];
				if(cvals == null) {
					cvals = new LinkedList<AggCounterValue>();
					fBuffer[i] = cvals;
				}
				if(cvs != null) {
					int cvalsSize = cvals.size();
					int j = 0;
					for(CounterValue cv : cvs) {
						AggCounterValue aggc;
						if(j >= cvalsSize) {
							if(cv instanceof CounterValueDouble) {
								aggc = new AggCounterValueDouble();
							} else if(cv instanceof CounterValueString) {
								aggc = new AggCounterValueString();
							} else if(cv instanceof CounterValueObject){
								aggc = new AggCounterValueObject();
							} else {
								aggc = new AggCounterValueDouble();
							}
							cvals.add(aggc);
						} else {
							aggc = cvals.get(j);
						}
						aggc.addCounterValue(cv);
						++j;
					}
				}
				++i;
			}
		}
		// update timestamp
		fTimestamp = buff.getTimestamp();
		return true;
	}

	/**
	 * @return
	 */
	public EntityDataBuffer geEntityDataBuffer() {
		CounterValue[][] values = null;
		if(!Utils.isEmptyArray(fBuffer)) {
			values = new CounterValue[fBuffer.length][];
			int i = 0;
			for(List<AggCounterValue> acvs : fBuffer) {
				if(acvs == null) {
					return null;
				}
				CounterValue[] cv = new CounterValue[acvs.size()];
				int j = 0;
				for(AggCounterValue acv : acvs) {
					cv[j] = acv.getCounterValue();
					++j;
				}
				values[i] = cv;
				++i;
			}
		}
		return new EntityDataBufferImpl(fDefinition, fEntity, values, fTimestamp);
	}
}
