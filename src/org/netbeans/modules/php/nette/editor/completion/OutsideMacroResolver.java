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
import java.util.List;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.StyledDocument;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.modules.php.nette.editor.completion.items.LatteCompletionItem;
import org.netbeans.modules.php.nette.editor.completion.items.MacroCompletionItem;
import org.netbeans.modules.php.nette.lexer.LatteTopTokenId;
import org.netbeans.modules.php.nette.macros.LatteMacro;
import org.netbeans.modules.php.nette.macros.LatteParamMacro;
import org.netbeans.modules.php.nette.macros.MacroDefinitions;
import org.netbeans.spi.editor.completion.CompletionItem;
import org.netbeans.spi.editor.completion.CompletionResultSet;

/**
 *
 * @author Radek Ježdík
 */
public class OutsideMacroResolver {

	public static void resolve(CompletionResultSet completionResultSet, TokenSequence<LatteTopTokenId> sequence,
			Document document, int caretOffset, List<LatteMacro> endMacros) {

		String filter = getFilter(document, caretOffset);
		int startOffset = caretOffset - filter.length();

		if(filter.equals("")) {
			return;
		}
		
		// end macro and friend macro completion
		completionResultSet.addAllItems(getFriendMacroCompletion(endMacros, startOffset, caretOffset, filter));

		// macro completion
		completionResultSet.addAllItems(getMacroCompletion(startOffset, caretOffset, filter));
		
		// n:attribute completion
		completionResultSet.addAllItems(getNAttributeCompletion(startOffset, caretOffset, filter));

		// <n:tag completion
		completionResultSet.addAllItems(getNTagCompletion(startOffset, caretOffset, filter));
	}

	private static List<CompletionItem> getFriendMacroCompletion(List<LatteMacro> endMacros,
			int startOffset, int caretOffset, String filter) {

		List<CompletionItem> list = new ArrayList<CompletionItem>();
		for(LatteMacro macro : endMacros) {
			String m = macro.isPair() ? macro.getEndMacro() : macro.getMacro();
			if(macro.isPair()) {
				macro = new LatteMacro("/" + macro.getEndMacroName());
			}
			if(m.startsWith(filter)) {
				list.add(new LatteCompletionItem(macro, startOffset, caretOffset));
			}
		}
		return list;
	}

	private static List<CompletionItem> getMacroCompletion(int startOffset, int caretOffset, String filter) {
		List<CompletionItem> list = new ArrayList<CompletionItem>();
		for(LatteMacro macro : MacroDefinitions.macros) {
			if(macro.getMacro().startsWith(filter)) {
				list.add(new LatteCompletionItem(macro, startOffset, caretOffset));
			}
		}
		return list;
	}

	private static List<CompletionItem> getNAttributeCompletion(int startOffset, int caretOffset, String filter) {
		List<CompletionItem> list = new ArrayList<CompletionItem>();

		if(!filter.startsWith("n:")) {
			return list;
		}
		
		for(LatteMacro macro : MacroDefinitions.macros) {
			if(!(macro instanceof LatteParamMacro) || !macro.isPair()) {
				continue;
			}
			String name = macro.getMacroName();
			String tag = "n:" + name;
			if(tag.startsWith(filter)) {
				list.add(new MacroCompletionItem(tag, startOffset, caretOffset, true));
			}
			tag = "n:inner-" + name;
			if(tag.startsWith(filter)) {
				list.add(new MacroCompletionItem(tag, startOffset, caretOffset, true));
			}
			tag = "n:tag-" + name;
			if(tag.startsWith(filter)) {
				list.add(new MacroCompletionItem(tag, startOffset, caretOffset, true));
			}
		}

		for(LatteMacro macro : MacroDefinitions.nAttrs) {
			String tag = "n:" + macro.getMacroName();
			if(tag.startsWith(filter)) {
				list.add(new MacroCompletionItem(tag, startOffset, caretOffset, true));
			}
		}
		
		return list;
	}

	private static List<CompletionItem> getNTagCompletion(int startOffset, int caretOffset, String filter) {
		List<CompletionItem> list = new ArrayList<CompletionItem>();

		if(!filter.startsWith("<n:")) {
			return list;
		}

		for(LatteMacro macro : MacroDefinitions.macros) {
			boolean hasParam = MacroDefinitions.tagMacros.containsKey(macro.getMacroName());
			if((macro.getClass() == LatteMacro.class || hasParam)
					&& Character.isLetter(macro.getMacroName().charAt(0))) {
				String name = macro.getMacroName();
				if(("<n:" + name).startsWith(filter)) {
					if(!hasParam) {
						list.add(new MacroCompletionItem("<n:" + name, startOffset, caretOffset, true, name.length() + 3));
					} else {
						String tag = "<n:" + name;
						for(String s : MacroDefinitions.tagMacros.get(macro.getMacroName())) {
							tag += " " + s + "=\"\"";
						}
						list.add(new MacroCompletionItem(tag, startOffset, caretOffset, true, tag.length() - 1));
					}
				}
			}
		}
		return list;
	}

	private static String getFilter(Document document, int caretOffset) {
		String filter = "";
		
		// determining what was written:
		try {
			final StyledDocument bDoc = (StyledDocument) document;
			final Element lineElement = bDoc.getParagraphElement(caretOffset);
			int start = caretOffset;
			int macroStart = start;

			String filterX = "";
			while(start >= lineElement.getStartOffset()) {
				char c = bDoc.getText(start - 1, 1).charAt(0);
				if(c == '{') {
					macroStart = start - 1;
					break;
				}
				if(Character.isWhitespace(c) || c == '}' || c == '<') {
					if(filterX.startsWith("n:")) {
						if(c == '<') {
							macroStart = start - 1;
						} else {
							macroStart = start;
						}
					}
					break;
				}
				filterX = c + filterX;
				start--;
			}
			filter = bDoc.getText(macroStart, caretOffset - macroStart).trim();
		} catch(BadLocationException e) {
		}
		
		return filter;
	}

}
