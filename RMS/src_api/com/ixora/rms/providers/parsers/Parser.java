/*
 * Created on 30-Dec-2004
 */
package com.ixora.rms.providers.parsers;

import com.ixora.rms.providers.parsers.exception.InvalidParsingRules;
import com.ixora.rms.providers.parsers.exception.ParserException;


/**
 * Provider data parser.
 * @author Daniel Moraru
 */
public interface Parser {
	/**
	 * Passes the object to parse.
	 * @param obj
	 * @throws ParserException
	 */
	void parse(Object obj) throws ParserException;
	/**
	 * Sets the rules used by the parser.
	 * @param rules
     * @throws InvalidParsingRules
	 */
	void setRules(ParsingRulesDefinition rules) throws InvalidParsingRules;
}
