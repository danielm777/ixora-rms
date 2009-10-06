/*
 * Created on 07-Jun-2004
 */
package com.ixora.rms.logging.xml;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.ixora.common.utils.HexConverter;
import com.ixora.common.utils.Utils;
import com.ixora.common.xml.XMLExternalizable;
import com.ixora.common.xml.XMLUtils;
import com.ixora.rms.CounterId;
import com.ixora.rms.EntityDataBuffer;
import com.ixora.rms.EntityDescriptor;
import com.ixora.rms.EntityDescriptorImpl;
import com.ixora.rms.EntityId;
import com.ixora.rms.RecordDefinition;
import com.ixora.rms.agents.AgentDataBuffer;
import com.ixora.rms.agents.AgentDescriptor;
import com.ixora.rms.agents.AgentDescriptorImpl;
import com.ixora.rms.client.session.MonitoringSessionDescriptor;
import com.ixora.rms.data.CounterValue;
import com.ixora.rms.data.CounterValueDouble;
import com.ixora.rms.data.CounterValueObject;
import com.ixora.rms.data.CounterValueString;
import com.ixora.rms.logging.DataLogWriter;
import com.ixora.rms.logging.LogComponent;
import com.ixora.rms.logging.exception.DataLogException;
import com.ixora.rms.logging.exception.InvalidLogRepository;
import com.ixora.rms.logging.messages.Msg;

/**
 * @author Daniel Moraru
 */
public final class DataLogWriterXML
	implements DataLogWriter, Tags {
	/** Writer to the temporary log file */
	private BufferedWriter fWriter;
	/** Final destination for the log data */
	private File fLogFile;
	/**
	 * Cache holding the xml normalized string representation
	 * of entity ids.
	 */
	private Map<EntityId, String> fCacheNormalized;
	/** Session descriptor */
	private MonitoringSessionDescriptor fSessionDescriptor;

	/**
	 * @param rep
	 */
	DataLogWriterXML(String rep) throws InvalidLogRepository {
		super();
		fLogFile = new File(rep);
		// check everithing ok with the destination log file
		if(!fLogFile.exists()) {
			try {
				fLogFile.createNewFile();
			} catch (IOException e) {
				throw new InvalidLogRepository(
					LogComponent.NAME,
					Msg.LOGGING_CANT_OPEN_FILE_FOR_WRITING,
					new String[] {rep});
			}
		}
		if(!fLogFile.canWrite()) {
			throw new InvalidLogRepository(
				LogComponent.NAME,
				Msg.LOGGING_CANT_OPEN_FILE_FOR_WRITING,
				new String[] {rep});
		}
		this.fCacheNormalized = new HashMap<EntityId, String>();
	}

	/**
	 * @see com.ixora.rms.logging.DataLogWriter#writeBuffer(AgentDataBuffer)
	 */
	public void writeBuffer(AgentDataBuffer db) throws DataLogException {
		try {
		    if(fWriter == null) {
				fWriter = new BufferedWriter(new FileWriter(this.fLogFile));
				// write the monitoring session first
				StringBuffer buff = XMLUtils.toXMLBuffer(
						MonitoringSessionDescriptor.class,
	                    fSessionDescriptor, "session", false);
				StringBuffer schemeContent = new StringBuffer(
						HexConverter.encode(buff.toString().trim().getBytes(XMLUtils.ENCODING)));
				schemeContent.insert(0, "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
						+ Utils.getNewLine() + "<" + RMSLOG + ">"
						+ Utils.getNewLine() + "<session>");
				schemeContent.insert(schemeContent.length(), "</session>" + Utils.getNewLine());
				fWriter.append(schemeContent);
		    }
			fWriter.write("<");
			fWriter.write(BUFFER_AGENT);
			fWriter.write(" ");
			fWriter.write(HOST);
			fWriter.write("=\"");
			fWriter.write(db.getHost().toString());
			fWriter.write("\" ");
			fWriter.write(AGENT);
			fWriter.write("=\"");
			fWriter.write(db.getAgent().toString());
			fWriter.write("\">");
			fWriter.newLine();
            AgentDescriptor adesc = db.getAgentDescriptor();
            if(adesc != null) {
                fWriter.write("<");
                fWriter.write(AGENT_DESCRIPTOR);
                fWriter.write(">");
                StringBuffer buff = XMLUtils.toXMLBuffer(
                        AgentDescriptorImpl.class, adesc, AGENT_DESCRIPTOR, false);
                fWriter.write(HexConverter.encode(buff.toString().trim().getBytes(XMLUtils.ENCODING)));
                fWriter.write("</");
                fWriter.write(AGENT_DESCRIPTOR);
                fWriter.write(">");
                fWriter.newLine();
            }

			EntityDataBuffer[] ebs = db.getBuffers();
			EntityDataBuffer eb;
			RecordDefinition rd;
			for(int i = 0; i < ebs.length; i++) {
				eb = ebs[i];
				if(eb != null) {
					// entity buffer
					fWriter.write("<");
					fWriter.write(BUFFER_ENTITY);
					fWriter.write(" ");
					fWriter.write(ENTITY);
					fWriter.write("=\"");
					EntityId eid = eb.getEntityId();
					// try to get normalized text from the cache
					String norm = this.fCacheNormalized.get(eid);
					if(norm == null) {
						norm = XMLUtils.normalize(eid.toString());
						this.fCacheNormalized.put(eid, norm);
					}
					fWriter.write(norm);
					fWriter.write("\">");
					fWriter.newLine();

					// record definition if any
					rd = eb.getDefinition();
					if(rd != null) {
						fWriter.write("<");
						fWriter.write(RECORD_DEFINITION);
						fWriter.write(">");
						fWriter.newLine();

						EntityDescriptor edesc = rd.getEntityDescriptor();
						if(edesc != null) {
							fWriter.write("<");
							fWriter.write(ENTITY_DESCRIPTOR);
							fWriter.write(">");
							StringBuffer buff = XMLUtils.toXMLBuffer(
									EntityDescriptorImpl.class, edesc, ENTITY_DESCRIPTOR, false);
							fWriter.write(HexConverter.encode(buff.toString().trim().getBytes(XMLUtils.ENCODING)));
							fWriter.write("</");
							fWriter.write(ENTITY_DESCRIPTOR);
							fWriter.write(">");
							fWriter.newLine();
						}
						CounterId[] cids = rd.getFields();
						if(cids != null) {
							for(int j = 0; j < cids.length; j++) {
								fWriter.write("<");
								fWriter.write(COUNTER);
								fWriter.write(" ");
								fWriter.write(ID);
								fWriter.write("=\"");
								fWriter.write(cids[j].toString());
								fWriter.write("\"/>");
								fWriter.newLine();
							}
						}
						fWriter.write("</");
						fWriter.write(RECORD_DEFINITION);
						fWriter.write(">");
						fWriter.newLine();
					}

					// counter values
					CounterValue[][] values = eb.getBuffer();
					CounterValue[] history;
					for(int j = 0; j < values.length; j++) {
						fWriter.write("<");
						fWriter.write(VALUES);
						fWriter.write(">");

						history = values[j];
						for(int k = 0; k < history.length; k++) {
							CounterValue val = history[k];
							String sval = "";
							if(val instanceof CounterValueDouble) {
								sval = val.toString();
							} else if(val instanceof CounterValueString) {
								sval = HexConverter.encode(val.toString().getBytes(XMLUtils.ENCODING));
							} else if(val instanceof CounterValueObject) {
								XMLExternalizable obj = ((CounterValueObject)val).getValue();
								sval = XMLUtils.toXMLBuffer(null, obj, RMS, false).toString();
								sval = HexConverter.encode(sval.getBytes(XMLUtils.ENCODING));
							}
							fWriter.write(sval);
							if(k != history.length - 1) {
								fWriter.write(VALUE_DELIMITER);
							}
						}

						fWriter.write("</");
						fWriter.write(VALUES);
						fWriter.write(">");
					}

					// timestamp
					fWriter.write("<");
					fWriter.write(TIMESTAMP);
					fWriter.write(">");
					fWriter.write(String.valueOf(eb.getTimestamp()));
					fWriter.write("</");
					fWriter.write(TIMESTAMP);
					fWriter.write(">");

					fWriter.write("</");
					fWriter.write(BUFFER_ENTITY);
					fWriter.write(">");
					fWriter.newLine();
				}
			}

			fWriter.write("</");
			fWriter.write(BUFFER_AGENT);
			fWriter.write(">");
			fWriter.newLine();
		} catch(Exception e) {
			throw new DataLogException(e);
		}
	}

	/**
	 * @see com.ixora.rms.logging.DataLogWriter#writeSessionDescriptor(com.ixora.rms.scheme.MonitoringSessionDescriptor)
	 */
	public void writeSessionDescriptor(MonitoringSessionDescriptor session) throws DataLogException {
	    // store it for the moment, it will be written
		// when the first data buffer will be written
		this.fSessionDescriptor = session;
	}

    /**
     * @see com.ixora.rms.logging.DataLogWriter#close()
     */
    public void close() throws DataLogException {
        if(fWriter != null) {
            try {
			    fWriter.write("</" + RMSLOG + ">");
                fWriter.close();
            } catch (IOException e) {
                throw new DataLogException(e);
            }
        }
    }
}
