package com.ixora.common;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.ixora.common.logging.AppLogger;
import com.ixora.common.logging.AppLoggerFactory;


/**
 * Used to manage task progress notification. The max value can be safely readjusted
 * while the task is in progress.
 * @author Daniel Moraru
 */
public final class Progress implements ProgressProvider, Reusable, ProgressMonitor {
    /** Logger */
    private static final AppLogger logger = AppLoggerFactory.getLogger(Progress.class);
	/** Max value */
	private long max;
	/** Processed */
	private int processed;
	/** Processed before the last notification */
	private int processedLast;
	/** Task name */
	private String task = "";
	/** Notification step */
	private long step;
	/** Step in percentages */
	private float pct;
	/** Listeners */
	private List listeners;

	/**
	 * Constructor for ProgressModel.
	 */
	public Progress() {
		super();
		this.listeners = new LinkedList();
	}

	/**
	 * @see com.ixora.common.ProgressMonitor#setMax(long)
	 */
	public void setMax(long max) {
		this.max = max;
		// calculate step in units if pct already given
		if(this.pct != 0) {
			calculateStepUnits();
		}
	}

	/**
	 * @see com.ixora.common.ProgressMonitor#setTask(java.lang.String)
	 */
	public void setTask(String task) {
		this.task = task;
		fireTaskStarted(task);
	}

    /**
	 * @see com.ixora.common.ProgressMonitor#nonFatalError(java.lang.String, java.lang.Throwable)
	 */
    public void nonFatalError(String error, Throwable t) {
        fireNonFatalError(error, t);
    }

	/**
	 * @see com.ixora.common.ProgressMonitor#setDelta(long)
	 */
	public void setDelta(long delta) {
		if(this.max == 0) {
			return;
		}
		if(this.processed == 0) {
			fireUpdate(0);
		}
		this.processed += delta;
		if(this.processed - this.processedLast >= this.step) {
			this.processedLast = this.processed;
			fireUpdate(100*(float)this.processed/this.max);
		}
	}

	/**
	 * @see com.ixora.common.ProgressMonitor#done()
	 */
	public void done() {
		setTask("");
		fireUpdate(100);
	}

	/**
	 * Sets the progress listener.
	 * @param listener
	 */
	public void addListener(Listener listener) {
		synchronized(this.listeners) {
			this.listeners.add(listener);
		}
	}

	/**
	 * Sets the progress listener.
	 * @param listener
	 */
	public void removeListener(Listener listener) {
		synchronized(this.listeners) {
			this.listeners.remove(listener);
		}
	}

	/**
	 * Returns the max.
	 * @return int
	 */
	public long getMax() {
		return this.max;
	}

	/**
	 * @see com.ixora.common.ProgressMonitor#reset()
	 */
	public void reset() {
		this.max = 0;
		this.processed = 0;
		this.pct = 0;
		this.task = "";
	}

	/**
	 * Returns the task or stage name.
	 * @return String
	 */
	public String getTask() {
		return task;
	}

	/**
	 * Fires the progress update event.
	 * @param pct
	 */
	private void fireUpdate(float pct) {
		synchronized(this.listeners) {
			for(Iterator iter = this.listeners.iterator(); iter.hasNext();) {
				try {
				    ((Listener)iter.next()).progress(pct);
                } catch(Exception e) {
                    logger.error(e);
                }
			}
		}
	}

    /**
     * Fires the progress update event.
     * @param pct
     * @param t
     */
    private void fireNonFatalError(String error, Throwable t) {
        synchronized(this.listeners) {
            for(Iterator iter = this.listeners.iterator(); iter.hasNext();) {
                try {
                    ((Listener)iter.next()).nonFatalError(error, t);
                } catch(Exception e) {
                    logger.error(e);
                }
            }
        }
    }

	/**
	 * Fires the task started event.
	 * @param pct
	 */
	private void fireTaskStarted(String task) {
		synchronized(this.listeners) {
			for(Iterator iter = this.listeners.iterator(); iter.hasNext();) {
				try {
				    ((Listener)iter.next()).taskStarted(task);
                } catch(Exception e) {
                    logger.error(e);
                }
			}
		}
	}

	/**
	 * Calculates step in units.
	 */
	private void calculateStepUnits() {
		this.step = (int)((this.pct/100) * this.max);
	}

}
