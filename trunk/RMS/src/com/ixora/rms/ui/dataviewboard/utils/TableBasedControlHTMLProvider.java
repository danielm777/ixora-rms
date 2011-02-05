/**
 * 19-Feb-2006
 */
package com.ixora.rms.ui.dataviewboard.utils;

import java.awt.Color;
import java.io.File;
import java.io.IOException;

import com.ixora.common.ui.TableSorter;
import com.ixora.rms.exporter.html.HTMLProvider;

/**
 * @author Daniel Moraru
 */
public class TableBasedControlHTMLProvider implements HTMLProvider {
	private TableSorter fModel;
	private TableBasedControlFormatter fFormatter;

	/**
	 * @param model
	 * @param formatter
	 */
	public TableBasedControlHTMLProvider(TableSorter model, TableBasedControlFormatter formatter) {
		super();
		fModel = model;
		fFormatter = formatter;
	}

	/**
	 * @see com.ixora.rms.exporter.html.HTMLProvider#toHTML(java.lang.StringBuilder, java.io.File)
	 */
	public void toHTML(StringBuilder buff, File root) throws IOException {
		buff.append("<table class='tableControl'>");
		// header
		int cols = this.fModel.getColumnCount();
		for(int i = 0; i < cols; i++) {
			int sortMode = fModel.getSortingStatus(i);
			buff.append("<th class='tableControlHeader'>");
			if(sortMode == TableSorter.NOT_SORTED) {
				buff.append(this.fModel.getColumnName(i));
			} else if(sortMode == TableSorter.DESCENDING) {
				buff.append(this.fModel.getColumnName(i)).append(" [DESC]");
			} else if(sortMode == TableSorter.ASCENDING) {
				buff.append(this.fModel.getColumnName(i)).append(" [ASC]");
			}
			buff.append("</th>");
		}
		buff.append("</th>");
		int rows = this.fModel.getRowCount();
		for(int i = 0; i < rows; i++) {
			buff.append("<tr class='tableControlRow");
			if(i % 2 == 0) {
				buff.append("Even'>");
			} else {
				buff.append("Odd'>");
			}
			for(int j = 0; j < cols; j++) {
				buff.append("<td>");
				Object obj = fModel.getValueAt(i, j);
				if(fFormatter == null) {
					buff.append(String.valueOf(obj));
				} else {
					buff.append(fFormatter.format(obj, i, j, Color.WHITE).fText);
				}
				buff.append("</td>");
			}
			buff.append("</tr>");
		}
		buff.append("</table>");
	}
}
