/*
 * Created on Feb 22, 2004
 */
package com.ixora.common.ui;

import java.awt.Frame;

/**
 * @author Daniel Moraru
 */
public final class UIExceptionMgr {
	/** Installed exception handler */
	private static UIExceptionHandler eh = new UIExceptionHandlerDefault();
	/**
	 * Private constructor.
	 */
	private UIExceptionMgr() {
		super();
	}

	/**
	 * This method should be called before any of the other
	 * methods are invoked.
	 * @param h exception handler to install
	 */
	public static void installExceptionHandler(UIExceptionHandler h) {
		if(eh == null) {
			throw new IllegalArgumentException("null exeption handler");
		}
		eh = h;
	}

	/**
	 * Invoked when the exception handler must
	 * be notified of the given exception.
	 * @param e
	 */
	public static void exception(Throwable e) {
		eh.exception(e);
	}

	/**
	 * Signals a user exception. A user exception is generated
	 * by a user interaction with the GUI.
	 * @param e
	 */
	public static void userException(Throwable e) {
		eh.userException(e);
	}

    /**
     * Signals a user exception. A user exception is generated
     * by a user interaction with the GUI.
     * @param owner
     * @param e
     */
    public static void userException(Frame owner, Throwable e) {
        eh.userException(owner, e);
    }
}
