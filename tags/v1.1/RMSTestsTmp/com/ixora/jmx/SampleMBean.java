package com.ixora.jmx;

import javax.management.openmbean.CompositeData;
import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.TabularData;

public interface SampleMBean {
	TabularData getStatsAsTabularData() throws OpenDataException;
	CompositeData[] getStatsAsArray() throws OpenDataException;
}
