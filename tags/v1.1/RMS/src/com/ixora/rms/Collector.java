/*
 * Created on 28-Dec-2004
 */
package com.ixora.rms;

import com.ixora.common.logging.AppLogger;
import com.ixora.common.logging.AppLoggerFactory;

/**
  * @author Daniel Moraru
 */
public abstract class Collector {
	/** Logger */
	private static final AppLogger logger = AppLoggerFactory.getLogger(Collector.class);
	/**
	 * Data collector thread.
	 */
	protected Thread fCollector;
	/**
	 * Whether or not to keep running the data collection thread.
	 */
	protected boolean fRunCollectionThread;
	/**
	 * Collection interval.
	 */
	protected int fCollectionInterval;
	/**
	 * The name of the collector thread.
	 */
	private String fCollectorThreadName;

	/**
	 * Constructor.
	 * @param collectorName the name of the collector thread
	 */
	protected Collector(String collectorName) {
		super();
		if(collectorName == null) {
			throw new IllegalArgumentException("null collector name");
		}
		this.fCollectorThreadName = collectorName;
		this.fCollectionInterval = 1000;
	}

	/**
	 * Starts the collector thread.
	 * @return
	 */
	public synchronized void startCollector() {
		if(this.fCollector == null
				|| !this.fCollector.isAlive()) {
			this.fCollector = new Thread(
					new Runnable() {
						public void run() {
							runCollection();
						}
					},
					this.fCollectorThreadName);
			this.fRunCollectionThread = true;
			this.fCollector.start();
		}
	}

	/**
	 * Stops the collector thread.
	 * @return
	 */
	public synchronized void stopCollector() {
		this.fRunCollectionThread = false;
		if(this.fCollector != null) {
			this.fCollector.interrupt();
			this.fCollector = null;
		}
	}

	/**
	 * Recalibrated the collector thread.
	 * @param si new interval in milliseconds
	 */
	public synchronized void recalibrateCollector(int si) {
		this.fCollectionInterval = Math.max(1000, Math.min(fCollectionInterval, Math.abs(si)));
	}

	/**
	 * Sets the collection interval.
	 * @param si collection interval in milliseconds
	 */
	public synchronized void setCollectionInterval(int si) {
		if(si < 1000) {
			throw new IllegalArgumentException("Collection interval must be greater than 1000ms: " + si);
		}
		fCollectionInterval = si;
	}

	/**
	 * Runs the collection.
	 */
	protected void runCollection() {
		try {
			while(isRunning()) {
				try {
					collect();
				} catch(InterruptedException e) {
					// propagate this
					throw e;
				} catch(Throwable e) {
					logger.error(e);
				}
				Thread.sleep(getCollectionInterval());
			}
		} catch(InterruptedException e) {
			; // ignore
		} catch(Exception e) {
			logger.error(e);
		}
	}

	/**
	 * Subclasses must implement here the collection code.
	 * @throws Throwable
	 */
	protected abstract void collect() throws Throwable;

	/**
	 * @return the collection interval.
	 */
	public synchronized int getCollectionInterval() {
		return fCollectionInterval;
	}
	/**
	 * @return true if the collection thread is running.
	 */
	public synchronized boolean isRunning() {
		return fRunCollectionThread;
	}

	/**
	 * @param thread
	 * @return
	 */
	public boolean ownsThread(Thread thread) {
		return this.fCollector == thread;
	}
}
