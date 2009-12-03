/*
 * Created on 08-Sep-2004
 */
package com.ixora.rms.logging.xml;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import com.ixora.common.logging.AppLogger;
import com.ixora.common.logging.AppLoggerFactory;
import com.ixora.common.utils.HexConverter;
import com.ixora.common.xml.XMLExternalizable;
import com.ixora.common.xml.XMLUtils;
import com.ixora.common.xml.exception.XMLException;
import com.ixora.rms.CounterId;
import com.ixora.rms.CounterType;
import com.ixora.rms.EntityDataBufferImpl;
import com.ixora.rms.EntityDescriptor;
import com.ixora.rms.EntityDescriptorImpl;
import com.ixora.rms.EntityId;
import com.ixora.rms.RecordDefinition;
import com.ixora.rms.RecordDefinitionCache;
import com.ixora.rms.agents.AgentDataBufferImpl;
import com.ixora.rms.agents.AgentDescriptor;
import com.ixora.rms.agents.AgentDescriptorImpl;
import com.ixora.rms.agents.AgentId;
import com.ixora.rms.data.CounterValue;
import com.ixora.rms.data.CounterValueDouble;
import com.ixora.rms.data.CounterValueObject;
import com.ixora.rms.data.CounterValueString;
import com.ixora.rms.data.ValueObject;
import com.ixora.rms.exception.AgentDescriptorNotFound;
import com.ixora.rms.exception.RecordDefinitionNotFound;
import com.ixora.rms.logging.BoundedTimeInterval;

/**
 * XML data log parser.
 * @author Daniel Moraru
 */
final class LogParser extends DefaultHandler implements Tags {
	/** Logger */
	private static final AppLogger sLogger = AppLoggerFactory.getLogger(LogParser.class);
	/** The interval in milliseconds at which progress report is triggered */
	private static final long PROGRESS_REPORT_INTERVAL = 30000;
	/** Parser */
	private XMLReader fParser;
	/** Data source */
	private BufferedReader fSource;
	/** Stopped flag */
	private volatile boolean fStopped;
	/** Current data buffer */
	private AgentDataBufferImpl fCurrentDataBuffer;
	/** Current entity data buffer */
	private EntityDataBufferImpl fCurrentEntityDataBuffer;
	/** Current record definition */
	private RecordDefinition fCurrentRecordDefinition;
	/** Current entity descriptor */
	private EntityDescriptor fCurrentEntityDescriptor;
	/** True if parsing a VALUE */
	private boolean fParsingValue;
	/** True if parsing a TIMESTAMP */
	private boolean fParsingTimestamp;
	/** True if parsing an ENTITY_DESCRIPTOR */
	private boolean fParsingEntityDescriptor;
	/** Current value */
	private StringBuffer fCurrentValue;
	/** List of counter ids used to assign it to the current record definition */
	private List<CounterId> fCounterIds;
	/** List of counter values used to assign it to the current entity buffer */
	private List<String> fCounterValues;
	/** List of entity data buffers used to assign it to the current data buffer */
	private List<EntityDataBufferImpl> fEntityDataBuffers;
	/** Record definition cache */
	private RecordDefinitionCache fRecordDefCache;
	/** Callback */
	private Callback fCallback;
	private AgentDescriptor fCurrentAgentDescriptor;
	private boolean fParsingAgentDescriptor;
	private long fTimestampBegin;
	private long fTimestampEnd;
	private long fTimestampLast;
	private long fTimestampLastProgress;

    /**
     * Callback.
     */
    public interface Callback {
        /**
         * Invoked after a new data buffer has been read.
         * @param buff
         */
        void handleDataBuffer(AgentDataBufferImpl buff);
        /**
         * Reached end of log file.
         */
        void finishedParsing();
        /**
         * @param time
         */
        void readProgress(long time);
    }

    /**
     * Constructor.
     * @param reader
     * @param cb
     * @throws SAXException
     */
    public LogParser(BufferedReader reader, Callback cb) throws SAXException {
        super();
        if(reader == null) {
            throw new IllegalArgumentException("null reader");
        }
        if(cb == null) {
            throw new IllegalArgumentException("null callback");
        }
        this.fCallback = cb;
        this.fSource = reader;
        this.fRecordDefCache = new RecordDefinitionCache();
        this.fParser = XMLReaderFactory.createXMLReader();
        this.fCounterIds = new LinkedList<CounterId>();
        this.fCounterValues = new LinkedList<String>();
        this.fEntityDataBuffers = new LinkedList<EntityDataBufferImpl>();
        this.fCurrentValue = new StringBuffer();
    }

    /**
     * Start parsing.
     * @param ti
     * @return false on failure
     * @throws SAXException
     * @throws IOException
     */
    public void parse(BoundedTimeInterval ti) throws IOException, SAXException {
        try {
        	this.fTimestampBegin = ti.getStart();
        	this.fTimestampEnd = ti.getEnd();
        	this.fTimestampLastProgress = ti.getStart();
	        this.fParser.setContentHandler(this);
	        this.fParser.setErrorHandler(this);
	        this.fParser.parse(new InputSource(this.fSource));
        } catch(IOException e) {
            if(!fStopped) {
                throw e;
            }
        } catch(SAXException e) {
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
            reset();
            // close source, that will stop the parser
            this.fSource.close();
        } catch(Exception e) {
            sLogger.error(e);
        }
    }

// handler methods
    /**
     * @see org.xml.sax.ContentHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
     */
    public void endElement(String uri, String localName, String qName)
            throws SAXException {
        // finish building the objects and clean up temporary data
        if(RECORD_DEFINITION.equals(localName)) {
            fCurrentRecordDefinition.setFields(
                    fCounterIds.toArray(
                            new CounterId[fCounterIds.size()]));
            fCurrentRecordDefinition.setEntityDescriptor(fCurrentEntityDescriptor);
            // save this record definition in the cache
            this.fRecordDefCache.putRecordDefinition(fCurrentDataBuffer.getHost(),
                    fCurrentDataBuffer.getAgent(),
                    fCurrentEntityDataBuffer.getEntityId(),
                    fCurrentRecordDefinition);
            fCounterIds.clear();
            fCurrentEntityDescriptor = null;
        } else if(BUFFER_ENTITY.equals(localName)) {
        	if(fCurrentRecordDefinition != null) {
                fCurrentEntityDataBuffer.setDefinition(fCurrentRecordDefinition);
            }
            // check that record definition is in the cache
            RecordDefinition rd = this.fRecordDefCache.getRecordDefinition(
                    fCurrentDataBuffer.getHost(),
                    fCurrentDataBuffer.getAgent(),
            		fCurrentEntityDataBuffer.getEntityId());
            if(rd == null) {
                throw new SAXException(new RecordDefinitionNotFound(fCurrentEntityDataBuffer.getEntityId()));
            } else {
            	// complete entity data buffer with definitions
            	fCurrentEntityDataBuffer.setDefinition(rd);
            }
            CounterValue[][] vals = new CounterValue[fCounterValues.size()][];
            CounterId[] cids = rd.getFields();
            int i = 0;
        	try {
	            for(Iterator<String> iter = fCounterValues.iterator(); iter.hasNext(); ++i) {
	                String sample = iter.next();
					vals[i] = parseCounterSamples(rd.getEntityDescriptor()
									.getCounterDescriptor(cids[i]).getType(), sample);
	            }
			} catch(SAXException e) {
				throw e;
			} catch(Exception e) {
				throw new SAXException(e);
			}
            fCurrentEntityDataBuffer.setBuffer(vals);
            fTimestampLast = fCurrentEntityDataBuffer.getTimestamp();
            if(fTimestampLastProgress > 0
            		&& fTimestampLast - fTimestampLastProgress >= PROGRESS_REPORT_INTERVAL) {
            	fCallback.readProgress(fTimestampLast);
            	fTimestampLastProgress = fTimestampLast;
            }
            fEntityDataBuffers.add(fCurrentEntityDataBuffer);
            // clean up
            fCounterValues.clear();
            fCurrentRecordDefinition = null;
        } else if(BUFFER_AGENT.equals(localName)) {
            fCurrentDataBuffer.setBuffers(
                    fEntityDataBuffers.toArray(
                            new EntityDataBufferImpl[fEntityDataBuffers.size()]));
            // update agent data buffer with agent descriptor
            if(fCurrentAgentDescriptor != null) {
            	fCurrentDataBuffer.setAgentDescriptor(fCurrentAgentDescriptor);
            }
            // check that agent descriptor is in the cache
            AgentDescriptor ad = this.fRecordDefCache.getAgentDescriptor(
                    fCurrentDataBuffer.getHost(),
                    fCurrentDataBuffer.getAgent());
            if(ad == null) {
                throw new SAXException(new AgentDescriptorNotFound(fCurrentDataBuffer.getAgent()));
            } else {
            	fCurrentDataBuffer.setAgentDescriptor(ad);
            }
            // fire agent data only if in the studied interval
            if(fTimestampLast >= fTimestampBegin) {
	            fCallback.handleDataBuffer(fCurrentDataBuffer);
            }

            // clear entity data buffers
            fEntityDataBuffers.clear();

            // stop here if required
            if(fTimestampLast > fTimestampEnd) {
            	fStopped = true;
            	reset();
            	fCallback.finishedParsing();
            	throw new SAXException();
            }
        } else if(VALUES.equals(localName)) {
            fParsingValue = false;
            fCounterValues.add(fCurrentValue.toString());
            fCurrentValue.delete(0, fCurrentValue.length());
        } else if(TIMESTAMP.equals(localName)) {
            fParsingTimestamp = false;
            fCurrentEntityDataBuffer.setTimestamp(Long.parseLong(fCurrentValue.toString()));
			fCurrentValue.delete(0, fCurrentValue.length());
        }  else if(ENTITY_DESCRIPTOR.equals(localName)) {
        	fParsingEntityDescriptor = false;
        	try {
            	StringBuffer buff = new StringBuffer(
            			new String(HexConverter.decode(fCurrentValue.toString()), XMLUtils.ENCODING));
        		this.fCurrentEntityDescriptor = (EntityDescriptor)XMLUtils.fromXMLBuffer(
					EntityDescriptorImpl.class, buff, null);
        		fCurrentValue.delete(0, fCurrentValue.length());
			} catch (Exception e) {
				throw new SAXException(e);
			}
        } else if(AGENT_DESCRIPTOR.equals(localName)) {
        	fParsingAgentDescriptor = false;
        	try {
            	StringBuffer buff = new StringBuffer(
            			new String(HexConverter.decode(fCurrentValue.toString()), XMLUtils.ENCODING));
        		this.fCurrentAgentDescriptor = (AgentDescriptor)XMLUtils.fromXMLBuffer(
					AgentDescriptorImpl.class, buff, "agentDescriptor");
        		// save this record definition in the cache
                this.fRecordDefCache.putAgentDescriptor(
                		fCurrentDataBuffer.getHost(),
                        fCurrentDataBuffer.getAgent(),
                        fCurrentAgentDescriptor);
        		fCurrentValue.delete(0, fCurrentValue.length());
			} catch (Exception e) {
				throw new SAXException(e);
			}
        }
    }

    /**
     * @see org.xml.sax.ContentHandler#characters(char[], int, int)
     */
    public void characters(char[] ch, int start, int length)
            throws SAXException {
    	if(fParsingTimestamp || fParsingAgentDescriptor
    			|| fParsingEntityDescriptor || fParsingValue) {
	        char[] buff = new char[length];
	        for(int i = 0; i < buff.length; i++) {
	           buff[i] = ch[start + i];
	        }
	        String val = new String(buff);
	        this.fCurrentValue.append(val);
    	}
    }

    /**
     * @see org.xml.sax.ContentHandler#endDocument()
     */
    public void endDocument() throws SAXException {
    	reset();
        fCallback.finishedParsing();
    }

    /**
     * @see org.xml.sax.ContentHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
     */
    public void startElement(String uri, String localName, String qName,
            Attributes attributes) throws SAXException {
        if(BUFFER_AGENT.equals(localName)) {
            fCurrentDataBuffer = new AgentDataBufferImpl();
            String host = attributes.getValue(HOST);
            if(host == null) {
                throw new SAXException("host attr missing");
            }
            fCurrentDataBuffer.setHost(host);
            String agent = attributes.getValue(AGENT);
            if(agent == null) {
                throw new SAXException("agent attr missing");
            }
            fCurrentDataBuffer.setAgent(new AgentId(agent));
        } else if(BUFFER_ENTITY.equals(localName)) {
            fCurrentEntityDataBuffer = new EntityDataBufferImpl();
            String entity = attributes.getValue(ENTITY);
            if(entity == null) {
                throw new SAXException("entity attr missing");
            }
            fCurrentEntityDataBuffer.setEntityId(new EntityId(entity));
        } else if(RECORD_DEFINITION.equals(localName)) {
            fCurrentRecordDefinition = new RecordDefinition();
        } else if(COUNTER.equals(localName)) {
            String cid = attributes.getValue(ID);
            if(cid == null) {
                throw new SAXException("id attr missing");
            }
            fCounterIds.add(new CounterId(cid));
	    } else if(VALUES.equals(localName)) {
	        fParsingValue = true;
	    } else if(TIMESTAMP.equals(localName)) {
	        fParsingTimestamp = true;
	    } else if(ENTITY_DESCRIPTOR.equals(localName)) {
	    	fParsingEntityDescriptor = true;
	    } else if(AGENT_DESCRIPTOR.equals(localName)) {
	    	fParsingAgentDescriptor = true;
	    }
    }

    /**
     * @see org.xml.sax.ErrorHandler#error(org.xml.sax.SAXParseException)
     */
    public void error(SAXParseException e) throws SAXException {
        sLogger.error(e);
        throw e;
    }

    /**
     * @see org.xml.sax.ErrorHandler#fatalError(org.xml.sax.SAXParseException)
     */
    public void fatalError(SAXParseException e) throws SAXException {
        sLogger.error(e);
        throw e;
    }

    /**
     * Parses the counter history string.
     * @param history
     * @return
     * @throws SAXException
     * @throws UnsupportedEncodingException
     * @throws XMLException
     */
    private CounterValue[] parseCounterSamples(CounterType type,
            			String history) throws SAXException, UnsupportedEncodingException, XMLException {
        StringTokenizer tok = new StringTokenizer(history, VALUE_DELIMITER);
        int size = tok.countTokens();
        CounterValue[] values = new CounterValue[size];
        String val;
        int i = 0;
        while(tok.hasMoreTokens()) {
            val = tok.nextToken();
            if(type == CounterType.LONG || type == CounterType.DOUBLE || type == CounterType.DATE) {
            	values[i] = new CounterValueDouble(Double.parseDouble(val));
            } else if(type == CounterType.STRING) {
            	values[i] = new CounterValueString(
           			new String(HexConverter.decode(val), XMLUtils.ENCODING));
            } else if(type == CounterType.OBJECT) {
            	StringBuffer str = new StringBuffer(new String(HexConverter.decode(val), XMLUtils.ENCODING));
            	stripRMSTag(str);
            	XMLExternalizable obj = XMLUtils.fromXMLBuffer(null, str, null);
            	values[i] = new CounterValueObject((ValueObject)obj);
            } else {
            	throw new SAXException("Unrecognized counter type");
	        }
            i++;
        }
        return values;
    }

    /**
     * Strips the given buffer from the RMS root tag.
     * @param buff a buffer which must have as the first node the RMS tag
     */
    private void stripRMSTag(StringBuffer buff) {
    	String rmsTagStart = "<" + RMS + ">";
    	int idx1 = buff.indexOf(rmsTagStart);
    	if(idx1 < 0) {
    		throw new IllegalArgumentException(rmsTagStart + " is missing");
    	}
    	buff.delete(idx1, idx1 + rmsTagStart.length());
    	String rmsTagEnd = "</" + RMS + ">";
    	int idx2 = buff.lastIndexOf(rmsTagEnd);
    	if(idx2 < 0) {
    		throw new IllegalArgumentException(rmsTagEnd + " is missing");
    	}
    	buff.delete(idx2, idx2 + rmsTagEnd.length());
    }

    /**
     *
     */
    private void reset() {
    	fTimestampLastProgress = fTimestampBegin;
    	fTimestampLast = 0;
    }
}
