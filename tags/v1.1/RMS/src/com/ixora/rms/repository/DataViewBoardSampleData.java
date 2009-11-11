/*
 * Created on 23-Mar-2005
 */
package com.ixora.rms.repository;

import java.io.File;
import java.io.IOException;

import com.ixora.common.utils.Utils;
import com.ixora.common.xml.XMLUtils;
import com.ixora.common.xml.exception.XMLException;

/**
 * @author Daniel Moraru
 */
public class DataViewBoardSampleData {
	/** Sample file name */
	private String fSampleFile;
	/** Data view */
	private DataView fDataView;

	/**
	 * Constructor.
	 */
	public DataViewBoardSampleData(String sampleFile) {
		super();
		fSampleFile = sampleFile;
	}

	/**
	 * @return
	 */
	public DataView getDataView() {
		return fDataView;
	}

//package access
	void buildDataView(File installationFolder) throws XMLException, IOException {
		File f = new File(installationFolder, fSampleFile);
		if(!f.exists()) {
			return;
		}
		StringBuffer buff = Utils.readFileContent(f);
		fDataView = (DataView)XMLUtils.fromXMLBuffer(null, buff, "view");
	}
}
