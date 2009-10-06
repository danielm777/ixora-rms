package com.ixora.common.pooling;

/**
 * AbstractInactivityController.
 * @author Daniel Moraru
 */
public abstract class AbstractInactivityController implements InactivityController {
	/**
	 * Pool.
	 */
	protected Pool pool;

	/**
	 * Constructor for AbstractInactivityController.
	 */
	public AbstractInactivityController() {
		super();
	}

	/**
	 * @see com.ixora.common.pooling.InactivityController#setPool(com.ixora.common.pooling.Pool)
	 */
	public void setPool(Pool pool) {
		this.pool = pool;
	}
}
