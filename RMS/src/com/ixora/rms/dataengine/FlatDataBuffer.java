/*
 * Created on Aug 18, 2004
 */
package com.ixora.rms.dataengine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.ixora.rms.HostId;
import com.ixora.rms.ResourceId;
import com.ixora.common.logging.AppLogger;
import com.ixora.common.logging.AppLoggerFactory;
import com.ixora.rms.CounterDescriptor;
import com.ixora.rms.CounterId;
import com.ixora.rms.CounterType;
import com.ixora.rms.EntityDataBuffer;
import com.ixora.rms.EntityId;
import com.ixora.rms.RecordDefinition;
import com.ixora.rms.agents.AgentDataBuffer;
import com.ixora.rms.agents.AgentId;
import com.ixora.rms.data.CounterValue;
import com.ixora.rms.data.CounterValueDouble;
import com.ixora.rms.data.CounterValueString;
import com.ixora.rms.exception.InvalidDataException;

/**
 * FlatDataBuffer
 * Flat list containing resources and histories for each one, as extracted
 * from an AgentDataBufferImpl. A ResourceId/CounterHistry pair is encapsulated
 * in a ResourceData object.
 *
 * This list can be filtered using a list of ResourceIds, with or without
 * regular expressions.
 */
public class FlatDataBuffer  {
	/** Logger */
	private static final AppLogger logger = AppLoggerFactory.getLogger(FlatDataBuffer.class);
	/** Holds the actual contents of the flat buffer */
	private List<ResourceData> fResourceData = new ArrayList<ResourceData>(30);
	/** Maps resource ID to index into the above list, for fast searching */
	private Map<ResourceId, Integer> fResourceIDs = new HashMap<ResourceId, Integer>();

	/**
	 * Will create an entry for each counter in the buffer.
	 *
	 * @param buff AgentDataBuffer to parse and extract counters from
	 * @throws InvalidDataException if data in the buffer is inconsistent
	 */
	public FlatDataBuffer(AgentDataBuffer buff) throws InvalidDataException {
		// Use the host and the agent from the DataBuffer
		HostId	hostId = buff.getHost();
		AgentId agentId = buff.getAgent();

		// Iterate through each EntityDataBufferImpl
		EntityDataBuffer[] listEDB = buff.getBuffers();
		for (int idxEntity = 0; idxEntity < listEDB.length; idxEntity++) {
			EntityId entityId = null;
			// make sure that one error wan't spoil the whole party
			try {
			    EntityDataBuffer edb = listEDB[idxEntity];
			    if (edb == null) {
			        throw new InvalidDataException(
			                "Null EntityDataBufferImpl from agent " + buff.getAgent());
			    }

				// Use the entity of the EntityDataBufferImpl
				entityId = edb.getEntityId();
				RecordDefinition rd = edb.getDefinition();
				if (rd == null) {
			        throw new InvalidDataException("No RecordDefinition in " +
			        		"EntityDataBufferImpl from agent " + buff.getAgent());
				}

				// List of counters sent by the agent
				CounterId[] counterIds = rd.getFields();
				if (counterIds == null) {
			        throw new InvalidDataException(
			                "Null CounterIDs in data from agent " + buff.getAgent());
				}

				// Go through each counter and add them to internal list
				CounterValue[][] data = edb.getBuffer();
				if (data == null) {
			        throw new InvalidDataException(
			                "Null counter history in data from agent " + buff.getAgent());
				}
				int samplesSize = 0;
				for(int idxCounter = 0; idxCounter < counterIds.length; idxCounter++) {
					// Get the counter id from the RecordDefinition
					CounterId counterId = counterIds[idxCounter];
					ResourceId rid = new ResourceId(hostId, agentId, entityId, counterId);
					CounterValue[] vals = data[idxCounter];
					// get counter type
					CounterType counterType = rd.getEntityDescriptor().getCounterDescriptor(counterId).getType();
					add(new ResourceData(rid, counterType, vals, null));
					samplesSize = vals.length; // either counter will do
				}

				// Add one timestamp counter
				ResourceId rid = new ResourceId(hostId, agentId, entityId, CounterDescriptor.TIMESTAMP_ID);
				CounterValue[] valTimestamp = new CounterValueDouble[1];
				valTimestamp[0] = new CounterValueDouble(edb.getTimestamp());
				add(new ResourceData(rid, CounterType.DOUBLE, valTimestamp, null));
			} catch(Throwable t) {
				logger.error("Data buffer for entity " + new ResourceId(hostId, agentId, entityId, null) + " was ignored due to error.", t);
			}
		}
	}

    /**
     * Used internally when constructing a filter.
     */
	private FlatDataBuffer() {
	}

	/**
	 * Only used internally to add to the list and update the
	 * search index.
	 * @param rData
	 */
	private void add(ResourceData rData) {
		fResourceData.add(rData);
		int index = fResourceData.size() - 1;
		fResourceIDs.put(rData.getResourceId(), new Integer(index));
	}

	/**
	 * Only called internally. Adds all contents from the other
	 * data buffer, updating the fast search index here.
	 */
	private void addAll(FlatDataBuffer rhs) {
		for (ResourceData rData : rhs.fResourceData) {
			add(rData);
		}
	}

	/**
	 * Mimics a List.size() method
	 */
	public int size() {
		return fResourceData.size();
	}

	/**
	 * Returns the ResourceData (ResourceId/counter history pair) at
	 * specified index.
	 *
	 * @param index known index of the resource/value pair to return
	 * @return
	 */
	public ResourceData get(int index) {
		return fResourceData.get(index);
	}

	/**
	 * Returns the resource/history pair, searching by ResourceId. Returns
	 * null if no such ResourceId exists.
	 *
	 * @param rid the ResourceId to search for
	 * @return
	 */
	public ResourceData getData(ResourceId rid) {
		Integer index = fResourceIDs.get(rid);
		if (index != null) {
			return fResourceData.get(index.intValue());
		}

		return null; // not found
	}

	/**
	 * Returns a list of resource/history pairs for all resources matching
	 * specified input. Multiple results are possible because regular
	 * expressions are allowed.
	 *
	 * @param ridQuery ResourceId(s) to search for
	 * @return list of resource/history pairs matching ridQuery
	 */
	public FlatDataBuffer getMatchingResourceIds(ResourceId ridQuery) {

		FlatDataBuffer fdb = new FlatDataBuffer();
		int cntSamplesCount = getCounterSamplesCount();

		// No regexp => just one match
		if (!ridQuery.isRegex()) {
			// If the resource id is incomplete then we must fabricate its history,
			// otherwise we just get it from the databuffer
			if (ridQuery.getRepresentation() != ResourceId.COUNTER) {
				// Fabricate a history with the same entity name
				CounterValueString cvsEntity = new CounterValueString(ridQuery.toString());
				CounterValue[] fHist = new CounterValue[cntSamplesCount];
				for (int i = 0; i < cntSamplesCount; i++)
					fHist[i] = cvsEntity;

				ResourceData fab = new ResourceData(ridQuery, CounterType.STRING, fHist, null);
				fdb.add(fab);
			} else {
			    ResourceData rdh = getData(ridQuery);
			    // Queries with functions spanning multiple DataBuffers are not yet supported.
			    // Therefore if at least one ResourceId is missing from here, return null.
			    if (rdh == null) {
			        return null;
				}
				fdb.add(rdh);
			}
		} else {
		    boolean bFound = false;
			// Get counter history from the databuffer, for each match
			for(Iterator it = fResourceData.iterator(); it.hasNext();) {
				ResourceData data = (ResourceData) it.next();

				// If the resource id is incomplete then we must fabricate its history,
				// otherwise we just get it from the databuffer
				List<String> captures = new LinkedList<String>();
				if(ridQuery.matchesSubpathIn(data.getResourceId(), captures)) {
					bFound = true;
					// The regex matches must return captured groups, if any
				    //data.captures = (String[])captures.toArray();
				    if(captures.size() > 0) {
				    	data.setCaptures(captures.toArray(new String[captures.size()]));
				    }
				    // For host, agent and entity-level resource IDs we must fabricate
				    // the history (just put the host / agent or entity name)
					if(ridQuery.getRepresentation() != ResourceId.COUNTER) {
						// Use only the relevant part(s) (up to entity for example)
					    ResourceId mID = null;
					    if (ridQuery.getRepresentation() == ResourceId.HOST) {
							mID = new ResourceId(
									data.getResourceId().getHostId(),
									null,
									null,
									null);
						} else if (ridQuery.getRepresentation() == ResourceId.AGENT) {
							mID = new ResourceId(
									data.getResourceId().getHostId(),
									data.getResourceId().getAgentId(),
									null,
									null);
						} else if (ridQuery.getRepresentation() == ResourceId.ENTITY) {
							mID = new ResourceId(
									data.getResourceId().getHostId(),
									data.getResourceId().getAgentId(),
									data.getResourceId().getEntityId(),
									null);
						}

						// Check if already added
						if (fdb.getData(mID) == null) {
							// Fabricate a history with the same entity name
							CounterValueString cvsEntity = new CounterValueString(mID.toString());
							CounterValue[] fHist = new CounterValue[cntSamplesCount];
							for (int i = 0; i < cntSamplesCount; i++) {
								fHist[i] = cvsEntity;
							}

							ResourceData fab = new ResourceData(mID, CounterType.STRING, fHist, data.getCaptures());
							fdb.add(fab);
						}
					} else {
						// Just add the counter history from the databuffer
						if (fdb.getData(data.getResourceId()) == null) {
							fdb.add(data);
						}
					}
				}
			}

		    // Queries with functions spanning multiple DataBuffers are not yet supported.
		    // Therefore if at least one ResourceId is missing from here, return null.
			if(!bFound) {
				if(logger.isTraceEnabled()) {
					logger.error("Resource " + ridQuery.toString() + " was not found in the data buffer");
				}
		        return null;
			}
		}

		return fdb;
	}

	/**
	 * Creates and returns a FlatDataBuffer containing data for all
	 * resources matching the ones specified in the input list. Regular
	 * expression resource ids are allowed.
	 *
	 * @param ridsFilter list of ResourceIds to include in result
	 * @return new FlatDataBuffer with filtered resources
	 */
	public FlatDataBuffer filter(List<ResourceId> ridsFilter) {
		FlatDataBuffer fdb = new FlatDataBuffer();
		// Note: Iterate through all rids in filter as the log message
		// in case of n rid which has no data is useful for debugging data views
		boolean invalidBuff = false;
		for(ResourceId ridQuery : ridsFilter) {
			 // This resourceId can contain regular expressions, so it can
			// match more than one counter.
			FlatDataBuffer filterFDB = getMatchingResourceIds(ridQuery);
		    // Queries with functions spanning multiple DataBuffers are not yet supported.
		    // Therefore if at least one ResourceId is missing from here, return null.
			if (filterFDB == null) {
				// This can happen under normal circumstances...
		    	//logger.error("Skipping data buffer because cannot find resource " + ridQuery);
				invalidBuff = true;
				break;
			} else {
				fdb.addAll(filterFDB);
			}
		}
		if(invalidBuff) {
			return null;
		}
		return fdb;
	}

	/**
	 * Returns the depth of the counter samples buffer.
	 * Assumes all counters in here have the same samples length.
	 */
	public int getCounterSamplesCount() {
		Iterator it = fResourceData.iterator();
		if (!it.hasNext()) {
			return 0;
		}
		ResourceData rd = (ResourceData)it.next();
		if(rd == null) {
			return 0;
		}
		CounterValue[] samples = rd.getCounterSamples();
		if(samples == null) {
			return 0;
		}
		return samples.length;
	}
}
