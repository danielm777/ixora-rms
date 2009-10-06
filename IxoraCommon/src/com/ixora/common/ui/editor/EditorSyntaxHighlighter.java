/*
 * Created on 20-Jan-2005
 */
package com.ixora.common.ui.editor;

import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

/**
 * EditorSyntaxHilite
 */
public abstract class EditorSyntaxHighlighter {

	protected static class ParsedToken {
		public int	start;
		public int	length;
		public int	type;

		public ParsedToken(int start, int length, int type) {
			this.start = start;
			this.length = length;
			this.type = type;
		}
	}

	/** Default text attribute for regular text */
	protected abstract AttributeSet getDefaultStyle();

	/** Text attributes to match token types */
	protected abstract AttributeSet[] getStyles();

	/** Regular expression that defines this language */
	protected abstract Pattern getRELang();

	/** Parses text and returns tokens */
	protected LinkedList<ParsedToken> parse(StyledDocument doc, int start, int length) {
		LinkedList<ParsedToken> tokens = new LinkedList<ParsedToken>();

		try {
			String text = doc.getText(start, length);
			AttributeSet[] styles = getStyles();

			// Regular expression search for tokens
			// TODO: look for multiline comments separately
			Matcher matcher = getRELang().matcher(text);
			while (matcher.find()) {
				for (int i = 0; i < styles.length; i++) {
					int tokStart = matcher.start(i+1);
					if (tokStart != -1) {
						int tokEnd = matcher.end(i+1);
						tokens.add(new ParsedToken(start + tokStart, tokEnd - tokStart, i));
						break;
					}
				}
			}
		} catch (BadLocationException e) {
			// ignore; if this happens we just don't highlight anything
		}
		return tokens;
	}

	/**
	 * Removes existing highlight, parses text looking for tokens and
	 * sets their styles.
	 */
	public void applyHighlight(StyledDocument doc, int start, int length) {

		// First set a default style for whole document
		doc.setCharacterAttributes(start, length,
				getDefaultStyle(), true);

		// Parse range looking for tokens to highlight
		LinkedList<ParsedToken>	pts = parse(doc, start, length);

		// Now set colors for found tokens
		AttributeSet[] styles = getStyles();
		for (ParsedToken pt : pts) {
			doc.setCharacterAttributes(pt.start, pt.length,
					styles[pt.type], false);
		}
	}
}
