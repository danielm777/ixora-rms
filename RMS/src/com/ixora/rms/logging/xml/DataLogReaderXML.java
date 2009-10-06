/*
 * Created on 07-Jun-2004
 */
package com.ixora.rms.logging.xml;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import com.ixora.rms.HostId;
import com.ixora.common.logging.AppLogger;
import com.ixora.common.logging.AppLoggerFactory;
import com.ixora.common.utils.HexConverter;
import com.ixora.common.xml.XMLUtils;
import com.ixora.common.xml.exception.XMLException;
import com.ixora.rms.EntityDescriptor;
import com.ixora.rms.agents.AgentDataBuffer;
import com.ixora.rms.agents.AgentDataBufferImpl;
import com.ixora.rms.agents.AgentDescriptor;
import com.ixora.rms.agents.AgentId;
import com.ixora.rms.client.session.MonitoringSessionDescriptor;
import com.ixora.rms.logging.DataLogReader;
import com.ixora.rms.logging.LogComponent;
import com.ixora.rms.logging.exception.DataLogException;
import com.ixora.rms.logging.exception.InvalidLog;
import com.ixora.rms.logging.exception.InvalidLogRepository;
import com.ixora.rms.logging.messages.Msg;

/**
 * @author Daniel Moraru
 */
public final class DataLogReaderXML implements DataLogReader {
	/** Logger */
	private static final AppLogger sLogger = AppLoggerFactory.getLogger(DataLogReaderXML.class);
    /** Log file */
    private File fLogFile;
    /** Reader */
    private BufferedReader fBufferRead;
    /** Reader */
    private BufferedReader fBufferScan;
    /** Log parser */
	private LogParser fParser;
	/** Log scanner */
	private LogScanner fScanner;
	/** Read callback */
    private ReadCallback fReadCallback;
	/** Scan callback */
    private ScanCallback fScanCallback;
    /** Reader thread */
    private Thread fReaderThread;
    /** Scanner thread */
    private Thread fScannerThread;
    /** Event handler */
    private EventHandler fEventHandler;
    /** The begining of the log data */
    private long fTimestampBegin;
    /** Last timestamp in the log */
    private long fTimestampEnd;

    /**
     * Event handler.
     */
    private final class EventHandler implements LogParser.Callback, LogScanner.Callback {
        /**
         * @see com.ixora.rms.logging.xml.LogParser.Callback#handleDataBuffer(AgentDataBuffer)
         */
        public void handleDataBuffer(AgentDataBufferImpl buff) {
            DataLogReaderXML.this.handleDataBuffer(buff);
        }
        /**
         * @see com.ixora.rms.logging.xml.LogParser.Callback#finishedParsing()
         */
        public void finishedParsing() {
            DataLogReaderXML.this.handleFinishedParsing();
        }
		/**
		 * @see com.ixora.rms.logging.xml.LogScanner.Callback#handleNewEntity(com.ixora.rms.internal.HostId, com.ixora.rms.internal.agents.AgentId, com.ixora.rms.EntityDescriptor)
		 */
		public void handleNewEntity(HostId hid, AgentId aid, EntityDescriptor entity) {
			DataLogReaderXML.this.handleNewEntity(hid, aid, entity);
		}
        /**
         * @see com.ixora.rms.logging.xml.LogScanner.Callback#handleNewAgent(com.ixora.rms.HostId, com.ixora.rms.agents.AgentDescriptor)
         */
        public void handleNewAgent(HostId hid, AgentDescriptor agent) {
            DataLogReaderXML.this.handleNewAgent(hid, agent);
        }
		/**
		 * @see com.ixora.rms.logging.xml.LogScanner.Callback#finishedScanning(long, long)
		 */
		public void finishedScanning(long beginTimestamp, long endTimestamp) {
			DataLogReaderXML.this.handleFinishedScanning(beginTimestamp, endTimestamp);
		}
		/**
		 * @see com.ixora.rms.logging.xml.LogParser.Callback#readProgress(long)
		 */
		public void readProgress(long time) {
			DataLogReaderXML.this.handleReadProgress(time);
		}
    }

	/**
	 * @param rep
	 */
	DataLogReaderXML(String rep)
			throws InvalidLogRepository, DataLogException {
		super();
		fLogFile = new File(rep);
		if(!fLogFile.canRead()) {
			throw new InvalidLogRepository(
					LogComponent.NAME,
					Msg.LOGGING_CANT_OPEN_FILE_FOR_READING,
					new String[] {rep});
		}
		try {
			this.fEventHandler = new EventHandler();
		    this.fBufferRead = new BufferedReader(new FileReader(fLogFile));
		    this.fBufferScan = new BufferedReader(new FileReader(fLogFile));
		    this.fParser = new LogParser(fBufferRead, fEventHandler);
		    this.fScanner = new LogScanner(fBufferScan, fEventHandler);
		} catch(Exception e) {
		    throw new DataLogException(e);
		}
	}

	/**
	 * @see com.ixora.rms.logging.DataLogReader#readSessionDescriptor()
	 */
	public MonitoringSessionDescriptor readSessionDescriptor() throws DataLogException {
		try {
			BufferedReader buffReader = new BufferedReader(new FileReader(fLogFile));
			StringBuffer buff = new StringBuffer(2048);
			String line;
		    while((line = buffReader.readLine()) != null) {
                if(line.indexOf("<session>") >= 0) {
                	buff.append(line.substring("<session>".length()));
                	buff.delete(buff.length() - "</session>".length(), buff.length());
                	break;
                }
            }
		    buff = new StringBuffer(new String(HexConverter.decode(buff.toString()), XMLUtils.ENCODING));
			MonitoringSessionDescriptor ret = (MonitoringSessionDescriptor)XMLUtils.fromXMLBuffer(
					MonitoringSessionDescriptor.class, buff, "session");
			return ret;
        } catch (IOException e) {
            throw new DataLogException(e);
        } catch (XMLException e) {
            throw new InvalidLog(e);
        }
	}

	/**
     * @see com.ixora.rms.logging.DataLogReader#read(com.ixora.rms.logging.DataLogReader.ReadCallback, long, long)
     */
    public void read(ReadCallback cb, long beginTimestamp, long endTimestamp) throws DataLogException {
        if(cb == null) {
            throw new IllegalArgumentException("null callback");
        }
        this.fTimestampBegin = beginTimestamp;
        this.fTimestampEnd = endTimestamp;
        this.fReadCallback = cb;
        this.fReaderThread = new Thread(new Runnable(){
            public void run() {
                try {
                    fParser.parse(fTimestampBegin, fTimestampEnd);
                } catch(Exception e) {
                    fReadCallback.handleReadFatalError(e);
                }
            }
        });
        this.fReaderThread.start();
    }

	/**
	 * @see com.ixora.rms.logging.DataLogReader#scan(com.ixora.rms.logging.DataLogReader.ScanCallback)
	 */
	public void scan(ScanCallback cb) throws DataLogException {
        if(cb == null) {
            throw new IllegalArgumentException("null callback");
        }
        fTimestampBegin = 0;
        fTimestampEnd = 0;
        this.fScanCallback = cb;
        this.fScannerThread = new Thread(new Runnable(){
            public void run() {
                try {
                    fScanner.scan();
                } catch(Exception e) {
                    fScanCallback.handleScanFatalError(e);
                }
            }
        });
        this.fScannerThread.start();
	}

	/**
	 * @see com.ixora.rms.logging.DataLogReader#close()
	 */
	public void close() {
		this.fScanner.stop();
	    this.fParser.stop();
	    try {
            this.fBufferRead.close();
        } catch (IOException e) {
           sLogger.error(e);
        }
	    try {
            this.fBufferScan.close();
        } catch (IOException e) {
           sLogger.error(e);
        }
	}

    /**
     * Handles new data buffer event.
     */
    private void handleDataBuffer(AgentDataBufferImpl buff) {
        try {
            this.fReadCallback.handleDataBuffer(buff);
        } catch(Exception e) {
            sLogger.error(e);
        }
    }

    /**
     * Handles end of log event.
     */
    private void handleFinishedParsing() {
        try {
            this.fReadCallback.handleDataBuffer(null);
        } catch(Exception e) {
            sLogger.error(e);
        }
    }

	/**
	 * Handles end of scanning.
	 * @param beginTimestamp
	 * @param endTimestamp
	 */
	private void handleFinishedScanning(long beginTimestamp, long endTimestamp) {
        try {
            this.fScanCallback.finishedScanning(beginTimestamp, endTimestamp);
            fTimestampBegin = beginTimestamp;
            fTimestampEnd = endTimestamp;
        } catch(Exception e) {
            sLogger.error(e);
        }
	}

	/**
	 * @param hid
	 * @param aid
	 * @param entity
	 */
	private void handleNewEntity(HostId hid, AgentId aid, EntityDescriptor entity) {
        try {
            this.fScanCallback.handleEntity(hid, aid, entity);
        } catch(Exception e) {
            sLogger.error(e);
        }
	}

    /**
     * @param hid
     * @param ad
     */
    private void handleNewAgent(HostId hid, AgentDescriptor agent) {
        try {
            this.fScanCallback.handleAgent(hid, agent);
        } catch(Exception e) {
            sLogger.error(e);
        }
    }

	/**
	 * @param time
	 */
	private void handleReadProgress(long time) {
        try {
            this.fReadCallback.handleReadProgress(time);
        } catch(Exception e) {
            sLogger.error(e);
        }
	}
}
