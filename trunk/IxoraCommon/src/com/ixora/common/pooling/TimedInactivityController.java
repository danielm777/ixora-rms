package com.ixora.common.pooling;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Inactivity controller that used a timer to remove objects that have not been used for more than
 * a certain amount of time.
 * @author Daniel Moraru
 */
final class TimedInactivityController extends AbstractInactivityController {
	/**
	 * Time out value. Objects that have been unused for
	 * more than <code>timeOut</code> milliseconds will
	 * be removed from the pool.
	 */
	private long timeOut;
	/** Entries */
	private Map entries;
	/** Timer to clean the pool */
	private Timer timer;

	/**
	 * Entry for objects in the pool.
	 */
	private static final class ObjectPoolEntry {
		/**
		 * Last time the object was retrieved from the pool.
		 */
		private long timeStamp;
		/**
		 * Pooled object.
		 */
		private Object object;

		/**
		 * Constructor.
		 * @param object
		 */
		public ObjectPoolEntry(Object object) {
			this.object = object;
		}
		/**
		 * Returns the object.
		 * @return Object
		 */
		public Object getObject() {
			return object;
		}

		/**
		 * Returns the timeStamp.
		 * @return long
		 */
		public long getTimeStamp() {
			return timeStamp;
		}

		/**
		 * Sets the timeStamp.
		 * @param timeStamp The timeStamp to set
		 */
		public void setTimeStamp(long timeStamp) {
			this.timeStamp = timeStamp;
		}

		/**
		 * @see java.lang.Object#equal(Object)
		 */
		public boolean equals(Object obj) {
			if(this == obj) {
				return true;
			}
			if(obj == null) {
				return false;
			}
			if(!(obj instanceof ObjectPoolEntry)) {
				return false;
			}

			ObjectPoolEntry that = (ObjectPoolEntry)obj;
			if(this.object == that.object) {
				return true;
			}
			return false;
		}

        /** @see java.lang.Object#hashCode() */
        public int hashCode() {
            return this.object.hashCode();
        }

		/**
		 * @see java.lang.Object#toString()
		 */
		public String toString() {
			return "(" + this.timeStamp + ":" + this.object + ")";
		}
	}

	/**
	 * Constructor for TimedInactivityController.
	 * @param timeout
	 * @param cleanupInterval in seconds
	 */
	public TimedInactivityController(long timeout, int cleanupInterval) {
		super();
		this.timeOut = timeout;
		this.entries = new HashMap();
		this.timer = new Timer(true);
		this.timer.schedule(new TimerTask(){
				public void run() {
					maintainPool();
				}
			}, 0, cleanupInterval * 1000);
	}

	/**
	 * @see bktoolkit.infra.utils.pooling.InactivityController#objectOut(Object)
	 */
	public void objectOut(Object obj) {
		synchronized(this.entries) {
			this.entries.remove(new ObjectPoolEntry(obj));
		}
	}

	/**
	 * @see bktoolkit.infra.utils.pooling.InactivityController#objectIn(Object)
	 */
	public void objectIn(Object obj) {
		ObjectPoolEntry entry = new ObjectPoolEntry(obj);
		entry.setTimeStamp(System.currentTimeMillis());
		synchronized (this.entries) {
			this.entries.put(entry, entry);
		}
	}

	/**
	 * @see bktoolkit.infra.utils.pooling.InactivityController#objectCreated(Object)
	 */
	public void objectCreated(Object obj) {
		;
	}

	/**
	 * @see bktoolkit.infra.utils.pooling.InactivityController#objectRemoved(Object)
	 */
	public void objectRemoved(Object obj) {
		synchronized (this.entries) {
			this.entries.remove(new ObjectPoolEntry(obj));
		}
	}

	/**
	 * Maintains the pool.
	 */
	private void maintainPool() {
		if(this.timeOut > 0) {
			long currTime = System.currentTimeMillis();
			List ejected = new LinkedList();
			synchronized (this.entries) {
				Iterator itr = this.entries.keySet().iterator();
				while(itr.hasNext()) {
					ObjectPoolEntry entry = (ObjectPoolEntry)itr.next();
					if(currTime - entry.getTimeStamp() > this.timeOut) {
						if(this.pool != null) {
							ejected.add(entry);
						}
					}
				}
			}

			for(Iterator iter = ejected.iterator(); iter.hasNext();) {
				ObjectPoolEntry entry = (ObjectPoolEntry)iter.next();
				this.pool.objectExpired(entry.getObject());

				// the object should have been removed by now from entries
				// but do it just to be clear
				synchronized(this.entries) {
					this.entries.remove(entry);
				}
			}
		}
	}
}
