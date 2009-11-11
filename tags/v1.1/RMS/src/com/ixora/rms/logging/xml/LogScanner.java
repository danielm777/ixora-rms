/*
 * Created on 08-Sep-2004
 */
package com.ixora.rms.logging.xml;

import java.io.BufferedReader;
import java.io.IOException;

import com.ixora.rms.HostId;
import com.ixora.common.logging.AppLogger;
import com.ixora.common.logging.AppLoggerFactory;
import com.ixora.common.utils.HexConverter;
import com.ixora.common.xml.XMLUtils;
import com.ixora.common.xml.exception.XMLException;
import com.ixora.rms.EntityDescriptor;
import com.ixora.rms.EntityDescriptorImpl;
import com.ixora.rms.agents.AgentDescriptor;
import com.ixora.rms.agents.AgentDescriptorImpl;
import com.ixora.rms.agents.AgentId;

/**
 * Log scanner.
 * @author Daniel Moraru
 */
final class LogScanner implements Tags {
	/** Logger */
	private static final AppLogger sLogger = AppLoggerFactory.getLogger(LogScanner.class);
	private static final String ED_START = "<" + ENTITY_DESCRIPTOR + ">";
	private static final String ED_END = "</" + ENTITY_DESCRIPTOR + ">";
    private static final String AD_START = "<" + AGENT_DESCRIPTOR + ">";
    private static final String AD_END = "</" + AGENT_DESCRIPTOR + ">";
	private static final String BA_START = "<" + BUFFER_AGENT;
	private static final String TIMESTAMP_START = "<" + TIMESTAMP + ">";
	private static final String TIMESTAMP_END = "</" + TIMESTAMP + ">";

	/** Data source */
	private BufferedReader fSource;
	/** Stopped flag */
	private volatile boolean fStopped;
	/** Callback */
	private Callback fCallback;
	/** Current host */
	private String fCurrentHost;
	/** Current agent */
	private String fCurrentAgent;

    /**
     * Callback.
     */
    public interface Callback {
        /**
         * Invoked after a new entity has been read.
         * @param hid
         * @param aid
         * @param entity
         */
        void handleNewEntity(HostId hid, AgentId aid, EntityDescriptor entity);
        /**
         * Invoked after a new entity has been read.
         * @param hid
         * @param agent
         */
        void handleNewAgent(HostId hid, AgentDescriptor agent);
        /**
         * Reached end of log file.
         * @param beginTimestamp
         * @param endTimestamp
         */
        void finishedScanning(long beginTimestamp, long endTimestamp);
    }

    /**
     * Constructor.
     * @param reader
     * @param cb
     */
    public LogScanner(BufferedReader reader, Callback cb) {
        super();
        if(reader == null) {
            throw new IllegalArgumentException("null reader");
        }
        if(cb == null) {
            throw new IllegalArgumentException("null callback");
        }
        this.fCallback = cb;
        this.fSource = reader;
    }

    /**
     * Start scanning.
     * @return false on failure
     * @throws IOException
     * @throws XMLException
     */
    public void scan() throws IOException, XMLException {
        try {
        	String line;
        	long beginTimestamp = 0;
        	long endTimestamp = 0;
        	while((line = fSource.readLine()) != null) {
        		int tmp;
        		if((tmp = line.indexOf(TIMESTAMP_START)) >= 0) {
        			int idx = line. indexOf(TIMESTAMP_END);
        			if(idx >= 0) {
        				// "<s>long</s>"
        				//  ^      ^
        				String s = line.substring(tmp + 3, idx);
	        			long ts = Long.parseLong(s);
	        			if(beginTimestamp == 0) {
	        				beginTimestamp = ts;
	        			} else {
	        				endTimestamp = ts;
	        			}
        			}
        		} else if(line.indexOf(BA_START) >= 0) {
        			// <ba h="localhost" a="apache">
        			int idx = line.indexOf("h=\"");
        			idx += 3;
        			int idx2 = line.indexOf('"', idx);
        			fCurrentHost = line.substring(idx, idx2);
        			idx = line.indexOf("a=\"", idx2);
        			idx += 3;
        			idx2 = line.indexOf('"', idx);
        			fCurrentAgent = line.substring(idx, idx2);
        		} else if(line.indexOf(ED_START) >= 0) {
        			StringBuffer buff = new StringBuffer(line.length());
        	     	buff.append(line.substring(ED_START.length()));
                	buff.delete(buff.length() - ED_END.length(), buff.length());
        		    buff = new StringBuffer(new String(HexConverter.decode(buff.toString()), XMLUtils.ENCODING));
        		    EntityDescriptor ed = (EntityDescriptor)XMLUtils.fromXMLBuffer(
        					EntityDescriptorImpl.class, buff, null);
                	fCallback.handleNewEntity(new HostId(fCurrentHost), new AgentId(fCurrentAgent), ed);
        		} else if(line.indexOf(AD_START) >= 0) {
                    StringBuffer buff = new StringBuffer(line.length());
                    buff.append(line.substring(AD_START.length()));
                    buff.delete(buff.length() - AD_END.length(), buff.length());
                    buff = new StringBuffer(new String(HexConverter.decode(buff.toString()), XMLUtils.ENCODING));
                    AgentDescriptor ad = (AgentDescriptor)XMLUtils.fromXMLBuffer(
                            AgentDescriptorImpl.class, buff, "agentDescriptor");
                    fCallback.handleNewAgent(new HostId(fCurrentHost), ad);
                }
        	}
        	fCallback.finishedScanning(beginTimestamp, endTimestamp);
        } catch(IOException e) {
            if(!fStopped) {
                throw e;
            }
        }
    }

    /**
     * Stop parsing
     */
    public void stop() {
        try {
            // the parser will stop with an exception,
            // this flag is used to decide whether or not
            // to propagate the error, if an exception occured
            // after this flag was set to true, it will not
            // be propagated
            fStopped = true;
            // close source, that will stop the parser
            this.fSource.close();
        } catch(Exception e) {
            sLogger.error(e);
        }
    }
}
