/**
 * 14-Jul-2005
 */
package com.ixora.rms.ui.views.logreplay;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.DefaultBoundedRangeModel;

import com.ixora.rms.ui.RMSViewContainer;

/**
 * @author Daniel Moraru
 */
class LogReplayProgressHandler {
	private static DateFormat sFormat = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
	private long fTimestampBegin;
	private long fTimestampEnd;
	private long fTime;
	private long fLogSpanInSeconds;
	private String fStaticText;
	private RMSViewContainer fViewContainer;

	LogReplayProgressHandler(RMSViewContainer viewContainer, String prefix, long bt, long et) {
		fTimestampBegin = bt;
		fTimestampEnd = et;
		fViewContainer = viewContainer;
		fLogSpanInSeconds = (fTimestampEnd - fTimestampBegin)/1000;
		fStaticText = prefix + " ["
			+ sFormat.format(new Date(fTimestampBegin))
			+ " -> "
			+ sFormat.format(new Date(fTimestampEnd))
			+ "]";
		viewContainer.setStateMessage(fStaticText);
		reset();
	}

	/**
	 * @param time
	 */
	public void setProgress(long time) {
		fTime = time;
		String progressString = (fTime - fTimestampBegin)/1000 + "sec/" + fLogSpanInSeconds + "sec";
		fViewContainer.getStatusBar().getProgressBar().setValue((int)time);
		fViewContainer.getStatusBar().getProgressBar().setString(progressString);
	}

	/**
	 *
	 */
	public void finshed() {
		String progressString = (fTimestampEnd - fTimestampBegin)/1000 + "sec/" + fLogSpanInSeconds + "sec";
		fViewContainer.getStatusBar().getProgressBar().setValue((int)fTimestampEnd);
		fViewContainer.getStatusBar().getProgressBar().setString(progressString);
		//reset();
	}

	/**
	 * Cleans up the common areas.
	 */
	public void cleanup() {
		fViewContainer.getStatusBar().getProgressBar().setStringPainted(false);
		fViewContainer.getStatusBar().getProgressBar().setString(null);
		fViewContainer.getStatusBar().getProgressBar().setModel(new DefaultBoundedRangeModel());
	}

	/**
	 * Prepares for a new log replay session.
	 */
	public void reset() {
		fViewContainer.getStatusBar().getProgressBar().setModel(new DefaultBoundedRangeModel());
		fViewContainer.getStatusBar().getProgressBar().setStringPainted(true);
		fViewContainer.getStatusBar().getProgressBar().setMinimum((int)fTimestampBegin);
		fViewContainer.getStatusBar().getProgressBar().setMaximum((int)fTimestampEnd);
		setProgress(fTimestampBegin);
	}
}
