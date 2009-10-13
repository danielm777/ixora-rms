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
		viewContainer.getAppStatusBar().setStateMessage(fStaticText);
		reset();
	}

	/**
	 * @param time
	 */
	public void setProgress(long time) {
		fTime = time;
		String progressString = (fTime - fTimestampBegin)/1000 + "sec/" + fLogSpanInSeconds + "sec";
		fViewContainer.getAppStatusBar().getProgressBar().setValue((int)time);
		fViewContainer.getAppStatusBar().getProgressBar().setString(progressString);
	}

	/**
	 *
	 */
	public void finshed() {
		String progressString = (fTimestampEnd - fTimestampBegin)/1000 + "sec/" + fLogSpanInSeconds + "sec";
		fViewContainer.getAppStatusBar().getProgressBar().setValue((int)fTimestampEnd);
		fViewContainer.getAppStatusBar().getProgressBar().setString(progressString);
		//reset();
	}

	/**
	 * Cleans up the common areas.
	 */
	public void cleanup() {
		fViewContainer.getAppStatusBar().getProgressBar().setStringPainted(false);
		fViewContainer.getAppStatusBar().getProgressBar().setString(null);
		fViewContainer.getAppStatusBar().getProgressBar().setModel(new DefaultBoundedRangeModel());
	}

	/**
	 * Prepares for a new log replay session.
	 */
	public void reset() {
		fViewContainer.getAppStatusBar().getProgressBar().setModel(new DefaultBoundedRangeModel());
		fViewContainer.getAppStatusBar().getProgressBar().setStringPainted(true);
		fViewContainer.getAppStatusBar().getProgressBar().setMinimum((int)fTimestampBegin);
		fViewContainer.getAppStatusBar().getProgressBar().setMaximum((int)fTimestampEnd);
		setProgress(fTimestampBegin);
	}
}
