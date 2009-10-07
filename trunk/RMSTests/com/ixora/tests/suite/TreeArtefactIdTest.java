/*
 * Created on 06-Jun-2005
 */
package com.ixora.tests.suite;

import junit.framework.TestCase;

import com.ixora.rms.repository.DataViewId;

public class TreeArtefactIdTest extends TestCase {
    private static final String[] DATAVIEWID = new String[] {
        "localhost/websphere/:Per server/:/: committed/rolledback transactions:",
        "l/h/:view:"
    };
    private static final String[] EXP_DATAVIEWID_NAME = new String[] {
        "Per server/: committed/rolledback transactions",
        "view"
    };
//    private static final String[] EXP_DATAVIEWID_CTXT = new String[] {
//        "localhost/websphere",
//        "l/h"
//    };

    private DataViewId[] dataViewId;

    protected void setUp() throws Exception {
        super.setUp();
        dataViewId = new DataViewId[DATAVIEWID.length];
        for(int i = 0; i < dataViewId.length; i++) {
            dataViewId[i] = new DataViewId(DATAVIEWID[i]);
        }
    }

    public void testGetName() {
        for (int i = 0; i < dataViewId.length; i++) {
            assertTrue(dataViewId[i].getName().equals(EXP_DATAVIEWID_NAME[i]));
        }
    }

    public void testGetContext() {
        for (int i = 0; i < dataViewId.length; i++) {
            //assertTrue(dataViewId[i].getContext().equals(EXP_DATAVIEWID_CTXT[i]));
        }
    }

    /*
     * Class under test for String toString()
     */
    public void testToString() {
        for (int i = 0; i < dataViewId.length; i++) {
            assertTrue(dataViewId[i].toString().equals(DATAVIEWID[i]));
        }
    }
}
