/**
 * 25-Dec-2005
 */
package com.ixora.rms.agents.logfile;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.RandomAccessFile;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import org.codehaus.janino.ScriptEvaluator;

import com.ixora.common.utils.Utils;
import com.ixora.rms.CounterId;
import com.ixora.rms.CounterType;
import com.ixora.rms.EntityId;
import com.ixora.rms.agents.AgentExecutionContext;
import com.ixora.rms.agents.impl.Counter;
import com.ixora.rms.agents.impl.Entity;
import com.ixora.rms.agents.impl.logfile.LogRecord;
import com.ixora.rms.agents.impl.logfile.LogRecordBatch;
import com.ixora.rms.agents.logfile.exception.InvalidPath;
import com.ixora.rms.agents.logfile.messages.Msg;
import com.ixora.rms.data.CounterValueDouble;
import com.ixora.rms.data.CounterValueObject;
import com.ixora.rms.exception.RMSException;

/**
 * @author Daniel Moraru
 */
public class LogFileEntity extends Entity {
	private static final long serialVersionUID = 2877446020758936831L;
	/** Log file */
	private File fFile;
	/** The bytes read index */
	private long fLastByteCount;
	/** Input stream */
	private RandomAccessFile fInputStream;
	/** Encoding */
	private String fEncoding;
	/** Regex used to find the begining of the log record */
	private Pattern fLogRecordBeginRegex;
	/** Regex used to find the end of the log record. It could be null. */
	private Pattern fLogRecordEndRegex;
	/** Script evaluator */
    private ScriptEvaluator fScriptEvaluator;
	/** Parser context */
    private LogParserScriptContextImpl fParserContext;
    /** The regex used to find the start of a log record */
	private String fRecordBeginRegex;
	/** Whether or not this is the first date collection cycle */
	private boolean fFirstCycle = true;

	/**
	 * @param id
	 * @param file
	 * @param c
	 * @throws RMSException
	 */
	public LogFileEntity(EntityId parent, File file, AgentExecutionContext c) throws RMSException {
		super(new EntityId(parent, file.getAbsolutePath()), c);
		fFile = file;
		if(fFile.isDirectory()) {
			throw new InvalidPath(file.getAbsolutePath());
		}
		fHasChildren = false;

		addCounter(new Counter(Msg.COUNTER_LOG_RECORDS, // id
						null, // alternate name
						Msg.COUNTER_LOG_RECORDS + ".description", // desc
						CounterType.OBJECT, // type
						false, // discreete
						null, // level
						"com.ixora.rms.ui.dataviewboard.logs.LogBoard"));
		addCounter(new Counter(Msg.COUNTER_SIZE_BYTES,
			Msg.COUNTER_SIZE_BYTES + ".description", CounterType.LONG));
		addCounter(new Counter(Msg.COUNTER_LAST_MODIFIED,
				Msg.COUNTER_LAST_MODIFIED + ".description", CounterType.DATE));

		Configuration conf = (Configuration)c.getAgentConfiguration().getAgentCustomConfiguration();
		LogParserDefinition parserConfig = (LogParserDefinition)conf.getObject(Configuration.CURRENT_LOG_PARSER);
		if(parserConfig == null) {
			throw new RMSException("Log parser information missing.");
		}
		fEncoding = conf.getString(Configuration.FILE_ENCODING);
		fRecordBeginRegex = parserConfig.getString(LogParserDefinition.LOG_RECORD_BEGIN_REGEX);
		try {
			fLogRecordBeginRegex = Pattern.compile(fRecordBeginRegex);
		} catch(Exception e) {
			throw new RMSException("Invalid regular expression for record start.", e);
		}
		String endRegex = parserConfig.getString(LogParserDefinition.LOG_RECORD_END_REGEX);
		if(!Utils.isEmptyString(endRegex)) {
			try {
				fLogRecordEndRegex = Pattern.compile(endRegex);
			} catch(Exception e) {
				throw new RMSException("Invalid regular expression for record end.", e);
			}
		}
		fParserContext = new LogParserScriptContextImpl();
		String parserCode = parserConfig.getString(LogParserDefinition.PARSER_CODE);
		try {
			fScriptEvaluator = new ScriptEvaluator(
					parserCode, LogRecord.class,
					new String[]{"context", "lines"},
					new Class[]{LogParserScriptContext.class, String[].class});
		} catch(Exception e) {
			throw new RMSException("Error evaluating log record parser.", e);
		}
	}

	/**
	 * @see com.ixora.rms.agents.impl.Entity#retrieveCounterValues()
	 */
	protected void retrieveCounterValues() throws Throwable {
		try {
			if(!fFile.exists()) {
				// this could happen for applications that roll the log files
				throw new RMSException("File " + fFile.getAbsolutePath() + " not found");
			}
			// get file size
			getCounter(new CounterId(Msg.COUNTER_SIZE_BYTES)).dataReceived(
					new CounterValueDouble(fFile.length()));
			// get last changed
			getCounter(new CounterId(Msg.COUNTER_LAST_MODIFIED)).dataReceived(
					new CounterValueDouble(fFile.lastModified()));

			Counter counter = getCounter(new CounterId(Msg.COUNTER_LOG_RECORDS));
			if(counter.isEnabled()) {
				// get log record
				// init stream
				fInputStream = new RandomAccessFile(fFile, "r");
				long flen = fFile.length();
				if(fFirstCycle) {
					// first cycle, start at the end of the file
					fLastByteCount = flen;
					fFirstCycle = false;
				} else {
					if(fLastByteCount > flen) {
						// the file was rolled, start from the start
						fLastByteCount = 0;
					} else {
						// do one more check for file rolling, compare the content from the
						// last fLastByteCount index with the saved content
						//fInputStream.seek(fLastByteCount);
						// TODO
					}
				}
				// read the bytes added since last sampling
				byte[] data = new byte[4096];
				ByteArrayOutputStream bytes = new ByteArrayOutputStream(4096);
				fInputStream.seek(fLastByteCount);
				//fInputStream.seek(0);
				int read;
				int readTotal = 0;
				double readMax = Math.pow(2, 20); // one megabyte at a time
				while((read = fInputStream.read(data)) > 0) {
					fLastByteCount += read;
					readTotal += read;
					bytes.write(data, 0, read);
					if(readTotal > readMax) {
						break;
					}
				}
				// the list of records to return
				LinkedList<LogRecord> logRecords = new LinkedList<LogRecord>();
				// convert to string using the given encoding
				String txt = bytes.toString(fEncoding);
				BufferedReader reader = new BufferedReader(new StringReader(txt));
				// build the log records
				List<String> lines = new LinkedList<String>();
				String line;
				boolean recordStarted = false;
				boolean recordComplete = false;
				while((line = reader.readLine()) != null) {
					if(line.length() == 0) {
						continue;
					}
					boolean recordBeginLine = false;
					if(fLogRecordBeginRegex.matcher(line).find(0)) {
						if(recordStarted || recordComplete) {
							// close existing record, a new record will start
							LogRecord record = createLogRecord(lines);
							if(record != null) {
								logRecords.add(record);
							}
							// clear builder
							lines.clear();
						}
						recordBeginLine = true;
						recordStarted = true;
						lines.add(line);
					} else {
						// accumulate lines for the current record, if
						// no record is currently being build ignore
						if(recordStarted) {
							lines.add(line);
						}
					}
					if(fLogRecordEndRegex != null) {
						// check for end of record
						String testForRecordEnd = line;
						if(recordBeginLine) {
							testForRecordEnd = line.substring(fRecordBeginRegex.length());
						}
						if(fLogRecordEndRegex.matcher(testForRecordEnd).find()) {
							recordComplete = true;
							recordStarted = false;
						}
					}
				}

				// try to make a log record from the last lines as well
				if(!Utils.isEmptyCollection(lines)) {
					LogRecord record = createLogRecord(lines);
					if(record != null) {
						logRecords.add(record);
					}
				}
				// send all records found
				counter.dataReceived(
						new CounterValueObject(new LogRecordBatch(logRecords)));
			}
		} finally {
			if(fInputStream != null) {
				fInputStream.close();
			}
		}
	}

	/**
	 * @param lines
	 * @return
	 */
	private LogRecord createLogRecord(List<String> lines) {
		if(Utils.isEmptyCollection(lines)) {
			return null;
		}
		try {
			return (LogRecord)fScriptEvaluator.evaluate(new Object[]{fParserContext, lines.toArray(new String[lines.size()])});
		} catch(Throwable e) {
			fContext.error(e);
		}
		return null;
	}
}
