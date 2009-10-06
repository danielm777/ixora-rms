/*
 * Created on 24-Feb-2005
 */
package com.ixora.common.ui;

import com.ixora.common.typedproperties.TypedProperties;

/**
 * @author Daniel Moraru
 */
public final class AppFrameParameters extends TypedProperties {
	public static final String LOOK_AND_FEEL_CLASS = "look_and_feel_class";
    public static final String NON_FATAL_ERRORS_BUFFER_SIZE = "non_fatal_errors_buffer_size";
    public static final String FEEDBACK_URL = "feedback_url";

	/**
	 * Constructor.
	 */
	public AppFrameParameters() {
		super();
		setProperty(LOOK_AND_FEEL_CLASS, TYPE_STRING, false, false);
        setProperty(NON_FATAL_ERRORS_BUFFER_SIZE, TYPE_INTEGER, false, true);
        setProperty(FEEDBACK_URL, TYPE_STRING, false, false);
        setInt(NON_FATAL_ERRORS_BUFFER_SIZE, 50);
	}
}
