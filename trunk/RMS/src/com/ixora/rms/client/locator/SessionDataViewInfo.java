/*
 * Created on 26-Feb-2005
 */
package com.ixora.rms.client.locator;

import com.ixora.rms.client.model.DataViewInfo;
import com.ixora.rms.repository.DataView;

/**
 * @author Daniel Moraru
 */
public class SessionDataViewInfo extends AbstractSessionArtefactInfo {
	private DataView fDataView;

	/**
	 * Constructor.
	 * @param vi
	 */
	public SessionDataViewInfo(DataViewInfo vi) {
		super();
		fTranslatedName = vi.getTranslatedName();
		fTranslatedDescription =  vi.getTranslatedDescription();
		fDataView = vi.getDataView();
	}

	/**
	 * Constructor.
	 * @param counterInfo
	 * @param dataView
	 */
	public SessionDataViewInfo(SessionCounterInfo counterInfo, DataView dataView) {
		this.fDataView = dataView;
		this.fTranslatedName = counterInfo.getTranslatedName();
		this.fTranslatedDescription = counterInfo.getTranslatedDescription();
	}

	/**
	 * Constructor.
	 * @param dataView
	 */
	public SessionDataViewInfo(DataView dataView) {
		this.fDataView = dataView;
		this.fTranslatedName = dataView.getName();
		this.fTranslatedDescription = dataView.getDescription();
	}

	/**
	 * Constructor.
	 * @param dataView
	 */
	public SessionDataViewInfo(DataView dataView,
			String translatedName, String translatedDescription) {
		this.fDataView = dataView;
		this.fTranslatedName = translatedName;
		this.fTranslatedDescription = translatedDescription;
	}

	/**
	 * @return the data view definition
	 */
	public DataView getDataView() {
		return fDataView;
	}
}
