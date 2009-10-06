/*
 * Created on 20-Jan-2005
 */
package com.ixora.common.ui.editor;

import java.awt.Color;
import java.util.regex.Pattern;

import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

/**
 * SyntaxHighlightXML
 */
public class SyntaxHighlightXML extends EditorSyntaxHighlighter {

	private static final int STYLE_STRING		= 0;
	private static final int STYLE_NUMBER		= 1;
	private static final int STYLE_TAG 			= 2;
	private static final int STYLE_ATTRIBUTE	= 3;
	private static final int STYLE_COMMENT		= 4;
	private static final int STYLE_CDATA		= 5;

	private static final int STYLES_COUNT = 6;

	private AttributeSet	fDefaultStyle;
	private AttributeSet[] fTokenStyles;

	private static final Pattern reLang = Pattern.compile(
			"(\\\"[^\"\\n]*\\\")|" + // strings
			"((?:[0-9]+)|(?:[0-9]+[.][0-9]*)|(?:[0-9]*[.][0-9]+)|(?:0x[a-fA-F0-9]))|" + // numbers
			"((?:\\<\\s*[/?]?\\s*\\w+\\b)|(?:[/]?\\s*>))|" + // tags
			"(?:(\\w+)\\s*=)|" + // attributes
			"((?ms:\\<!--.*-->))|" + // comments
			"(<\\!\\[CDATA\\[.*\\]\\]>)" // cdata
		);

	/**
	 * @see com.ixora.common.ui.EditorSyntaxHighlighter#getDefaultStyle()
	 */
	protected AttributeSet getDefaultStyle() {
		if (fDefaultStyle == null) {
			SimpleAttributeSet sas = new SimpleAttributeSet();
	        StyleConstants.setFontFamily(sas, "Monospaced");
	        StyleConstants.setBold(sas, false);
	        StyleConstants.setForeground(sas, Color.BLACK);
			fDefaultStyle = sas;
		}
		return fDefaultStyle;
	}

	/**
	 * @see com.ixora.common.ui.EditorSyntaxHighlighter#getStyles()
	 */
	protected AttributeSet[] getStyles() {
		if (fTokenStyles == null) {
			fTokenStyles = new AttributeSet[STYLES_COUNT];
			SimpleAttributeSet sas;

			// Style for strings
			sas = new SimpleAttributeSet();
	        StyleConstants.setForeground(sas, Color.BLUE);
			fTokenStyles[STYLE_STRING] = sas;

			// Style for numbers
			sas = new SimpleAttributeSet();
	        StyleConstants.setForeground(sas, Color.MAGENTA);
			fTokenStyles[STYLE_NUMBER] = sas;

			// Style for keywords
			sas = new SimpleAttributeSet();
	        StyleConstants.setForeground(sas, Color.RED.darker());
	        StyleConstants.setBold(sas, true);
			fTokenStyles[STYLE_TAG] = sas;

			// Style for attributes
			sas = new SimpleAttributeSet();
	        StyleConstants.setForeground(sas, Color.GREEN.darker());
	        StyleConstants.setItalic(sas, true);
			fTokenStyles[STYLE_ATTRIBUTE] = sas;

			// Style for comments
			sas = new SimpleAttributeSet();
	        StyleConstants.setForeground(sas, Color.GRAY);
			fTokenStyles[STYLE_COMMENT] = sas;

			// Style for cdata
			sas = new SimpleAttributeSet();
	        StyleConstants.setForeground(sas, Color.GRAY);
			fTokenStyles[STYLE_CDATA] = sas;
		}
		return fTokenStyles;
	}

	/**
	 * @see com.ixora.common.ui.EditorSyntaxHighlighter#getRELang()
	 */
	protected Pattern getRELang() {
		return reLang;
	}

}
