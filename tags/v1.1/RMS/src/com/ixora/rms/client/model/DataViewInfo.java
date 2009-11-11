/*
 * Created on 12-Oct-2004
 */
package com.ixora.rms.client.model;

import com.ixora.rms.repository.DataView;

/**
 * Info on a data view query client.
 * @author Daniel Moraru
 */
public interface DataViewInfo extends ArtefactInfo {
	/**
     * @return
     */
    DataView getDataView();
    /**
     * @return true if the data view has reactions defined
     */
    boolean hasReactions();
}
