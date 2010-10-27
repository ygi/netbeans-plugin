/*
 *  The MIT License
 * 
 *  Copyright (c) 2010 Radek Ježdík <redhead@email.cz>, Ondřej Brejla <ondrej@brejla.cz>
 * 
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 * 
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 * 
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  THE SOFTWARE.
 */

package org.netbeans.modules.php.nette.utils;

import java.util.concurrent.atomic.AtomicReference;

/**
 * 
 * @author Radek Ježdík
 */
public class MacroSyntaxUtils {

	/**
	 * Translates short array syntax ([s,s]) and short ternary operator ($x ? $y) to full php
	 * @param s string to translate
	 * @return string of full php syntax
	 */
	public static String replaceCommonSyntax(String s) {
		return replaceCommonSyntax(s, new IRef(0), "");
	}

	/**
	 * Translates short array syntax ([s,s]) and short ternary operator ($x ? $y) to full php
	 * @param s string to parse
	 * @param i string position (passed as reference integer)
	 * @param closingChars	(closing chararcters of current "sub-block")
	 * @return
	 */
	private static String replaceCommonSyntax(String s, IRef i, String closingChars) {
		if(s.startsWith("<?php") || s.endsWith("?>"))
			return s;

		boolean closed = false;

		StringBuilder sb = new StringBuilder();
		for(; i.get() < s.length(); i.inc()) {
			char c = s.charAt(i.get());
			if(c == '?') {	// start of short ternary
				sb.append(c);
				sb.append(replaceCommonSyntax(s, i.inc(), ":)],"));
				continue;
			}
			if(c == '(') {	// start of simple brackets (function, array)
				sb.append(c);
				sb.append(replaceCommonSyntax(s, i.inc(), ")"));
				continue;
			}
			if(c == '[') {	// start of possible short array syntax
				// if there is $variable before, treat as array index ($var[...])
				if(s.substring(0, i.get()).matches(".*\\$[a-zA-Z0-9_]+")) {
					sb.append(c);
					continue;
				}
				sb.append("array(");
				sb.append(replaceCommonSyntax(s, i.inc(), "]"));
				continue;
			}

			// if current char is a closing char
			if(closingChars.indexOf(c) != -1) {
				if(closingChars.indexOf(':') != -1) {
					if(c != ':') {	// if it is not : char then close short ternary operator
						sb.append(":null");
						i.set(i.get()-1);
					} else {		// in case of : found, ternary is full
						sb.append(c);
					}
				}
				if(c == ']' && closingChars.equals("]")) {
					sb.append(")");		// closes short array syntax
				}
				if(c == ')' && closingChars.equals(")")) {
					sb.append(')');		// closes normal bracket block
				}
				closed = true;
				break;
			}
			sb.append(c);
		}
		// in case there was no closing char (ie end of string), close short ternary operator
		if(!closed && closingChars.equals(":)],")) {
			sb.append(":null");
		}

		return sb.toString();
	}

	/**
	 * Class which stores integer reference used in recursive calls of replaceCommonSyntax() method
	 */
	static class IRef extends AtomicReference<Integer> {

		public IRef(Integer i) {
			super(i);
		}

		public IRef inc() {
			this.set(this.get()+1);
			return this;
		}

	}

}
