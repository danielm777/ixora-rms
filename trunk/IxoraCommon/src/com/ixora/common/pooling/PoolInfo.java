package com.ixora.common.pooling;

/**
 * Pool information.
 * @author Daniel Moraru
 */
public final class PoolInfo {
	/** Objects in use */
	private int objectsInUse;
	/** Objects available */
	private int objectsAvailable;
	/** Objects created */
	private int objectsCreated;
	/** Objects destroyed */
	private int objectsDestroyed;

	/**
	 * @param objectsInUse
	 * @param objectsAvailable
	 * @param objectsCreated
	 * @param objectsDestroyed
	 */
	public PoolInfo(int objectsInUse,
						int objectsAvailable,
							int objectsCreated,
								int objectsDestroyed) {
		super();
		this.objectsInUse = objectsInUse;
		this.objectsAvailable = objectsAvailable;
		this.objectsCreated = objectsCreated;
		this.objectsDestroyed = objectsDestroyed;
	}

	/**
	 * Returns the objectsAvailable.
	 * @return int
	 */
	public int getObjectsAvailable() {
		return objectsAvailable;
	}

	/**
	 * Returns the objectsCreated.
	 * @return int
	 */
	public int getObjectsCreated() {
		return objectsCreated;
	}

	/**
	 * Returns the objectsDestroyed.
	 * @return int
	 */
	public int getObjectsDestroyed() {
		return objectsDestroyed;
	}

	/**
	 * Returns the objectsInUse.
	 * @return int
	 */
	public int getObjectsInUse() {
		return objectsInUse;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer buff = new StringBuffer(50);
		buff.append("[in use|available|created|destroyed]:[");
		buff.append(this.objectsInUse);
		buff.append("|");
		buff.append(this.objectsAvailable);
		buff.append("|");
		buff.append(this.objectsCreated);
		buff.append("|");
		buff.append(this.objectsDestroyed);
		buff.append("]");
		return buff.toString();
	}
}
