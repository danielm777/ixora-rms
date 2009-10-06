package com.ixora.common.thread;

import java.util.LinkedList;
import java.util.List;

/**
 * Producer consumer queue.
 * @author Daniel Moraru
 */
public final class BlockingQueue {
	/**
	 * Queue entries.
	 */
	private List entries;
	/**
	 * The maximum size of the queue. If this value is
	 * reached the thread trying to place an object on the
	 * queue will be blocked untils space will be freed.
	 * If this value is 0 the queue will have no limit.
	 */
	private int maxSize;
	/**
	 * Whether or not the consumers were interrupted.
	 */
	private boolean consumersInterrupted;

	/**
	 * Constructor for PCQueue.
	 */
	public BlockingQueue() {
	    this(0);
	}

	/**
	 * Constructor for PCQueue.
	 * @param maxSize the maximum size of the queue. If this value is
	 * reached the thread trying to place an object on the
	 * queue will be blocked untils space will be freed.
	 * If this value is 0 the queue will have no limit.
	 */
	public BlockingQueue(int maxSize) {
		super();
		this.entries = new LinkedList();
		this.maxSize = maxSize;
	}

	/**
	 * Places object on the queue.
	 * @param r
	 * @throws InterruptedException only for queues with a max size
	 */
	public void put(Object r) throws InterruptedException {
		synchronized (this) {
		    if(this.maxSize > 0) {
		        while(this.entries.size() >= this.maxSize) {
		            wait();
		        }
		    }
			this.entries.add(r);
			notify();
		}
	}

	/**
	 * @return An object from the queue.
	 * @throws InterruptedException If the current thread waiting
	 * for a queue entry has been interrupted.
	 */
	public Object get() throws InterruptedException {
		synchronized (this) {
			while(this.entries.size() == 0) {
				wait();
	            if(consumersInterrupted) {
	                throw new InterruptedException();
	            }
			}
			return this.entries.remove(0);
		}
	}

	/**
	 * @return The number of elements in the queue.
	 */
	public int size() {
		synchronized (this) {
			return this.entries.size();
		}
	}

    /**
     * Interrupts all threads waiting on <code>get()</code>.
     * The queue becomes unusable once this method has been called;
     * It's used to signal the end of the processing involving this queue.
     */
    public void interruptConsumers() {
        synchronized(this) {
            consumersInterrupted = true;
            notifyAll();
        }
    }
}
