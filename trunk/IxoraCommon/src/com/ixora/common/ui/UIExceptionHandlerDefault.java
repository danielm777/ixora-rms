/*
 * Created on Feb 22, 2004
 */
package com.ixora.common.ui;

import java.awt.Frame;

import com.ixora.common.logging.AppLogger;
import com.ixora.common.logging.AppLoggerFactory;

/**
 * @author Daniel Moraru
 */
public final class UIExceptionHandlerDefault implements UIExceptionHandler {
	/** Logger */
	private static final AppLogger logger = AppLoggerFactory.getLogger(UIExceptionHandlerDefault.class);

	/**
	 *
	 */
	public UIExceptionHandlerDefault() {
		super();
	}

	/**
	 * @see com.ixora.common.ui.UIExceptionHandler#exception(java.lang.Throwable)
	 */
	public void exception(Throwable e) {
		logger.error(e);
	}
	/**
	 * @see com.ixora.common.ui.UIExceptionHandler#userException(java.lang.Throwable)
	 */
	public void userException(Throwable e) {
		//logger.error(e);
	}

    /**
     * @see com.ixora.common.ui.UIExceptionHandler#userException(java.awt.Frame, java.lang.Throwable)
     */
    public void userException(Frame owner, Throwable e) {
       // logger.error(e);
    }
}
