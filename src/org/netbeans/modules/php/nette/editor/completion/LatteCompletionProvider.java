/*
 * The MIT license
 *
 * Copyright (c) 2010 Radek Ježdík <redhead@email.cz>, Ondřej Brejla <ondrej@brejla.cz>
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */

package org.netbeans.modules.php.nette.editor.completion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.modules.php.api.util.Pair;
import org.netbeans.modules.php.nette.lexer.LatteTokenId;
import org.netbeans.modules.php.nette.lexer.LatteTopTokenId;
import org.netbeans.modules.php.nette.macros.LatteMacro;
import org.netbeans.modules.php.nette.macros.MacroDefinitions;
import org.netbeans.modules.php.nette.utils.LexUtils;
import org.netbeans.spi.editor.completion.CompletionProvider;
import org.netbeans.spi.editor.completion.CompletionResultSet;
import org.netbeans.spi.editor.completion.CompletionTask;
import org.netbeans.spi.editor.completion.support.AsyncCompletionQuery;
import org.netbeans.spi.editor.completion.support.AsyncCompletionTask;

/**
 * Provides completion window
 * Is context-dependent (in-macro completion, out-side macro completion)
 * by token where caret is positioned at (LatteTopTokenId and LatteTokenId)
 * @author Radek Ježdík
 */
public class LatteCompletionProvider implements CompletionProvider {

	/**
	 * Stores hash map of <String, Pair<LatteMacro, Integer>> of pair macros
	 * (used for finding unmatched macros for completion of their end/friend macros)
	 */
	MacroCounterMap paired;

	/**
	 * Stores text written by user for auto-showing completion box (see getAutoQueryTypes method)
	 */
	private String autoShowText;

	@Override
	public CompletionTask createTask(int type, JTextComponent jtc) {

		if(type != CompletionProvider.COMPLETION_QUERY_TYPE) {
			return null;
		}

		return new AsyncCompletionTask(new AsyncCompletionQuery() {

			@Override
			protected void query(CompletionResultSet completionResultSet, Document document, int caretOffset) {
				TokenSequence<LatteTopTokenId> sequence = LexUtils.getTopSequence(document);

				sequence.move(caretOffset);

				// pair macros (Int > 0 == unclosed macro) see below
				preprocessUnclosedMacros(sequence);

				sequence.move(caretOffset);
				if(sequence.moveNext() || sequence.movePrevious()) {
					Token<LatteTopTokenId> token = sequence.token();
					
					if(token.id() == LatteTopTokenId.LATTE) {
						//inside macro completion
						InsideMacroResolver.resolve(completionResultSet, sequence, document, caretOffset);
					} else {
						// fills up list with possible endMacros and their friend macros
						List<LatteMacro> endMacros = getFriendMacros();
						// outside macro completion
						OutsideMacroResolver.resolve(completionResultSet, sequence, document, caretOffset, endMacros);
					}
				}
				// must be called before return;
				completionResultSet.finish();
			}
		}, jtc);
	}

	/**
	 * Shows completion box automaticaly,
	 * if text written starts with opening Latte delimiter or with n: chars
	 *
	 * @param JTextComponent
	 * @param written text
	 * @return
	 */
	@Override
	public int getAutoQueryTypes(JTextComponent jtc, String string) {
		if(string.equals(":") && autoShowText != null) {
			autoShowText += string;
		} else {
			autoShowText = null;
		}
		if(string.equals("n")) {
			autoShowText = string;
		}

		return (string.startsWith("{") || string.startsWith("n:")
				|| (autoShowText != null && autoShowText.equals("n:"))) ? COMPLETION_QUERY_TYPE : 0;
	}

	/**
	 * Returns map of macros which are not closed (int > 0) until caret position
	 * @param sequence
	 * @return
	 */
	private MacroCounterMap preprocessUnclosedMacros(TokenSequence<LatteTopTokenId> sequence) {
		paired = new MacroCounterMap();
		
		// find all pair macros
		for(LatteMacro macro : MacroDefinitions.macros) {
			if(macro.isPair()) {
				paired.put(macro.getMacroName(), Pair.of(macro, 0));
			}
		}

		while(sequence.movePrevious()) {
			Token<LatteTopTokenId> token = sequence.token();

			if(token.id() == LatteTopTokenId.LATTE) {
				TokenHierarchy<CharSequence> th2 = TokenHierarchy.create(token.text(), LatteTokenId.language());
				TokenSequence<LatteTokenId> sequence2 = th2.tokenSequence(LatteTokenId.language());

				sequence2.moveStart();

				boolean isEndMacro = false;

				while(sequence2.moveNext()) {
					Token<LatteTokenId> token2 = sequence2.token();

					// is end macro?
					if(token2.id() == LatteTokenId.SLASH && sequence2.offset() <= 2) {
						isEndMacro = true;
					}

					String text = token2.text().toString();
					//for comletion of end macros (preparation)
					if(token2.id() == LatteTokenId.MACRO && paired.containsKey(text)) {
						Pair<LatteMacro, Integer> p = paired.get(text);
						Pair newP = null;
						Integer i;

						if(!isEndMacro) {
							// increment with open pair macro
							i = (p.second == null ? 1 : p.second + 1);
							newP = Pair.of(p.first, i);
						} else {
							// decrement with close pair macro
							i = (p.second == null ? -1 : p.second - 1);
							newP = Pair.of(p.first, i);
						}
						paired.put(text, newP);
					}
				}
			}
		}
		return paired;
	}

	private ArrayList<LatteMacro> getFriendMacros() {
		ArrayList<LatteMacro> endMacros = new ArrayList<LatteMacro>();
		for(String key : paired.keySet()) {
			Pair<LatteMacro, Integer> p = paired.get(key);
			if(p.second != null && p.second > 0) {
				endMacros.add(p.first);
				if(MacroDefinitions.friendMacros.containsKey(key)) {
					endMacros.addAll(Arrays.asList(MacroDefinitions.friendMacros.get(key)));
				}
			}
		}
		return endMacros;
	}

	private class MacroCounterMap extends HashMap<String, Pair<LatteMacro, Integer>> {
	}
}
