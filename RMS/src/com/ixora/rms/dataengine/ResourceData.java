/*
 * Created on 20-Aug-2004
 *
 */
package com.ixora.rms.dataengine;

import com.ixora.rms.ResourceId;
import com.ixora.rms.CounterType;
import com.ixora.rms.data.CounterValue;
import com.ixora.rms.data.CounterValueDouble;
import com.ixora.rms.data.CounterValueObject;
import com.ixora.rms.data.CounterValueString;

/**
 * ResourceData
 * Contains the history data (counter values) for a resourceID
 */
public final class ResourceData {
	private ResourceId fResourceId;
	private CounterValue[] fSamples;
	private String[] fCaptures;
	private CounterType fCounterType;

	/**
	 * @param rid
	 * @param ct
	 * @param samples
	 * @param captures
	 */
	public ResourceData(ResourceId rid, CounterType ct, CounterValue[] samples, String[] captures) {
	    if (samples == null)
	        throw new IllegalArgumentException("counter history is null for " + rid.toString());
		this.fResourceId = rid;
		this.fCaptures = captures;
		this.fCounterType = ct;
		// This version aggregates all samples into one
		if(samples.length > 0) {
		    this.fSamples = new CounterValue[1];
		    // We're assuming all samples are of the same type.
		    if(samples[0] instanceof CounterValueString) {
		        // Strings: any one will do, get the first one
		        this.fSamples[0] = samples[0];
		    } else if(samples[0] instanceof CounterValueDouble) {
		        // Numbers, calculate an average. Ignores null values.
		        double avg = 0;
		        double avgCount = 0;
		        for(int i = 0; i < samples.length; i++) {
		            CounterValueDouble cvd = (CounterValueDouble)samples[i];
		            if (cvd != null) {
		                avg += cvd.getDouble();
		                avgCount++;
		            }
		        }
		        if(avgCount > 0) {
		            avg /= avgCount;
		        }
		        this.fSamples[0] = new CounterValueDouble(avg);
		    } else if(samples[0] instanceof CounterValueObject) {
		    	fSamples[0] = samples[0];
		    }
		} else {
		    // Just 1 or 0 samples, use whatever is in there
		    this.fSamples = samples;
		}

	}

	/**
	 * @return rid.
	 */
	public ResourceId getResourceId() {
		return fResourceId;
	}

	/**
	 * @return counter type
	 */
	public CounterType getCounterType() {
		return fCounterType;
	}

	/**
	 * @return counter samples
	 */
	public CounterValue[] getCounterSamples() {
		return fSamples;
	}

	/**
	 * @return captures.
	 */
	public String[] getCaptures() {
		return fCaptures;
	}

	/**
	 * @param strings
	 */
	public void setCaptures(String[] strings) {
		fCaptures = strings;
	}
}
