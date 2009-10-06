/*
 * Created on 21-Nov-2004
 */
package com.ixora.rms.ui.dataviewboard.legend;




/**
 * Contains counter info for all plotted counters.
 */
public class LegendItemInfo {
	/** Translated item name */
	protected String itemName;
	/** Translated item description */
	protected String itemDescription;
	/** Item value */
	protected Number itemValue;

	/**
	 * Constructor.
	 * @param name
	 * @param desc
	 */
	public LegendItemInfo(String name, String desc) {
		itemName = name;
		itemDescription = desc;
	}
	/**
	 * @return the translated counter description
	 */
	public String getItemDescription() {
		return itemDescription;
	}
	/**
	 * @return the translated counter name
	 */
	public String getItemName() {
		return itemName;
	}
	/**
	 * @return current value
	 */
	public Number getItemValue() {
		return itemValue;
	}
	/**
	 * @param value
	 */
	public void setValue(Number value) {
		itemValue = value;
	}
}