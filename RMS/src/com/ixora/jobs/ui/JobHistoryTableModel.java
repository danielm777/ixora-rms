/*
 * Created on 24-Dec-2003
 */
package com.ixora.jobs.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import com.ixora.jobs.JobHistoryDetails;
import com.ixora.jobs.JobId;
import com.ixora.jobs.JobsComponent;
import com.ixora.jobs.ui.messages.Msg;
import com.ixora.common.MessageRepository;

/**
 * The model for the job history table.
 * @author Daniel Moraru
 */
public final class JobHistoryTableModel extends AbstractTableModel {
	/** Column1 */
	private final String[] columns = {
		MessageRepository.get(JobsComponent.NAME, Msg.MODELS_TABLES_JOBHISTORY_COL1),
		MessageRepository.get(JobsComponent.NAME, Msg.MODELS_TABLES_JOBHISTORY_COL2),
		MessageRepository.get(JobsComponent.NAME, Msg.MODELS_TABLES_JOBHISTORY_COL3),
		MessageRepository.get(JobsComponent.NAME, Msg.MODELS_TABLES_JOBHISTORY_COL4),
		MessageRepository.get(JobsComponent.NAME, Msg.MODELS_TABLES_JOBHISTORY_COL5),
		MessageRepository.get(JobsComponent.NAME, Msg.MODELS_TABLES_JOBHISTORY_COL6)
	};
	/** List of JobHistoryDetails */
	private List dtls;

	/**
	 * HostInfoTableModel.
	 * @param jobs collection of JobHistoryDetails
	 */
	public JobHistoryTableModel(Collection jobs) {
		super();
		dtls = new ArrayList(jobs);
	}

	/**
	 * @see javax.swing.table.AbstractTableModel#gtColumnName(int)
	 */
	public String getColumnName(int arg1) {
		return columns[arg1];
	}

	/**
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	public int getRowCount() {
		return dtls.size();
	}

	/**
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	public int getColumnCount() {
		return columns.length;
	}

	/**
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	public Object getValueAt(int arg0, int arg1) {
	    JobHistoryDetails jhd = (JobHistoryDetails)dtls.get(arg0);
		switch(arg1) {
			case 0: // job id
				return jhd.getJobId().toString();
			case 1: // job name
			    return jhd.getJobDefinition().getName();
			case 2: // job status
			    return jhd.getJobState().toString();
			case 3: // Date scheduled
			    Date sched = jhd.getJobDefinition().getDate();
			    if(sched == null) {
			        return "";
			    }
				return sched.toString();
			case 4: // Date started
			    Date started = jhd.getDateStarted();
			    if(started == null) {
			        return "";
			    }
				return started.toString();
			case 5: // Date ended
			    Date ended = jhd.getDateEnded();
			    if(ended == null) {
			        return "";
			    }
				return ended.toString();
		}
		return null;
	}

	/**
	 * Sets the job history details
	 * @param dtls
	 */
	public void setJobHistoryDetails(Collection dtls) {
	    this.dtls = new ArrayList(dtls);
	    fireTableDataChanged();
	}

	/**
	 * @param row
	 * @return
	 */
	public JobHistoryDetails getJobHistoryDetailsAtRow(int row) {
	    return (JobHistoryDetails)this.dtls.get(row);
	}

	/**
	 * @param jid
	 * @return
	 */
	public int getRowForJob(JobId jid) {
	    JobHistoryDetails jd;
	    int i = 0;
	    for(Iterator iter = dtls.iterator(); iter.hasNext();++i) {
        	jd = (JobHistoryDetails)iter.next();
        	if(jd.getJobId().equals(jid)) {
        	    return i;
        	}
	   }
	   return -1;
	}
}
