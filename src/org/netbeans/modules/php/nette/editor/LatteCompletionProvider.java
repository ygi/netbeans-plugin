/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.php.nette.editor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import javax.swing.text.StyledDocument;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.modules.php.api.util.Pair;
import org.netbeans.modules.php.nette.lexer.LatteTokenId;
import org.netbeans.modules.php.nette.lexer.LatteTopTokenId;
import org.netbeans.modules.php.nette.macros.LatteCommentMacro;
import org.netbeans.modules.php.nette.macros.LatteMacro;
import org.netbeans.modules.php.nette.macros.LatteParamMacro;
import org.netbeans.modules.php.nette.macros.LatteSingleMacro;
import org.netbeans.modules.php.nette.utils.EditorUtils;
import org.netbeans.spi.editor.completion.CompletionProvider;
import org.netbeans.spi.editor.completion.CompletionResultSet;
import org.netbeans.spi.editor.completion.CompletionTask;
import org.netbeans.spi.editor.completion.support.AsyncCompletionQuery;
import org.netbeans.spi.editor.completion.support.AsyncCompletionTask;
import org.openide.util.Exceptions;

/**
 * Provides completion window
 * Is context-dependent (in-macro completion, out-side macro completion)
 * by token where caret is positioned at (LatteTopTokenId and LatteTokenId)
 * @author redhead
 */
public class LatteCompletionProvider implements CompletionProvider {

    public static LatteMacro[] macros = {
        new LatteCommentMacro(),
        new LatteSingleMacro("$"),
        new LatteSingleMacro("!$"),
        new LatteSingleMacro("="),
        new LatteSingleMacro("!="),
        new LatteSingleMacro("?"),
        new LatteSingleMacro("_"),
        new LatteSingleMacro("!_"),
        new LatteParamMacro("link", false),
        new LatteParamMacro("plink", false),
        new LatteParamMacro("if", true),
        new LatteParamMacro("ifset", true, "if"),
        new LatteParamMacro("ifcurrent", true, "if"),
        //new LatteParamMacro("elseif", false),
        //new LatteParamMacro("elseifset", false),
        //new LatteMacro("else"),
        new LatteParamMacro("for", true),
        new LatteParamMacro("foreach", true),
        new LatteParamMacro("while", true),
        new LatteParamMacro("include", false),
        new LatteParamMacro("extends", false),
        new LatteParamMacro("layout", false),
        new LatteParamMacro("widget", false),
        new LatteParamMacro("control", false),
        new LatteParamMacro("cache", true),
        new LatteParamMacro("snippet", true),
        new LatteParamMacro("attr", false),
        new LatteParamMacro("block", true),
        new LatteParamMacro("contentType", false),
        new LatteParamMacro("status", false),
        new LatteParamMacro("capture", false),
        new LatteParamMacro("assign", false),
        new LatteParamMacro("default", false),
        new LatteParamMacro("var", false),
        new LatteParamMacro("dump", false),
        new LatteMacro("debugbreak", false),
    };

    public final static HashMap<String, LatteMacro[]> friendMacros = new HashMap<String, LatteMacro[]>();
    static {
        friendMacros.put("if", new LatteMacro[] {
            new LatteMacro("else"),
            new LatteParamMacro("elseif", false)
        });
        friendMacros.put("ifset", new LatteMacro[] {
            new LatteMacro("else"),
            new LatteParamMacro("elseif", false),
            new LatteParamMacro("elseifset", false)
        });
        friendMacros.put("ifcurrent", new LatteMacro[] {
            new LatteMacro("else"),
            new LatteParamMacro("elseif", false)
        });
    };

    public final static HashMap<String, String[]> tagMacros = new HashMap<String, String[]>();
    static {
        tagMacros.put("assign", new String[] { "" });
        tagMacros.put("include", new String[] { "block" });
        tagMacros.put("for", new String[] { "each" });
        tagMacros.put("block", new String[] { "name" });
        tagMacros.put("if", new String[] { "cond" });
        tagMacros.put("elseif", new String[] { "cond" });
    };

    public static String[] helpers = {
        "escapeHtml", "escapeHtmlComment", "escapeXML", "escapeCss", "escapeHtmlCss", "escapeJs",
        "escapeHtmlJs", "strip", "indent", "indentCb", "date", "bytes", "length",
        "replace", "nl2br", "stripTags", "translate", "number",
    };


    public static LatteMacro getMacro(String name) {
        for(LatteMacro m : macros) {
            if(m.getMacroName().equals(name)) {
                return m;
            }
        }
        return null;
    }

    public static List<LatteMacro> getMacrosByEnd(String name) {
        List<LatteMacro> list = new ArrayList<LatteMacro>();
        for(LatteMacro m : macros) {
            if(m.getEndMacroName().equals(name)) {
                list.add(m);
            }
        }
        return list;
    }

    public CompletionTask createTask(int type, JTextComponent jtc) {
        if (type != CompletionProvider.COMPLETION_QUERY_TYPE) {
            return null;
        }


        return new AsyncCompletionTask(new AsyncCompletionQuery() {

            protected void query(CompletionResultSet completionResultSet, Document document, int caretOffset) {
                String filter = "";
                int startOffset = caretOffset - 1;

                TokenHierarchy<String> th = null;
                try {
                    th = TokenHierarchy.create(document.getText(0, document.getLength()), LatteTopTokenId.language());
                } catch (BadLocationException ex) {
                    Exceptions.printStackTrace(ex);
                }
                TokenSequence<LatteTopTokenId> sequence = th.tokenSequence(LatteTopTokenId.language());


                /*List<List<String>> foreaches = new ArrayList<List<String>>();
                List<String> dynamicVars = new ArrayList<String>();*/
                
                HashMap<String, Pair<LatteMacro, Integer>> paired = new HashMap<String, Pair<LatteMacro, Integer>>();
                for(LatteMacro macro : macros) {
                    if(macro.isPair()) {
                        paired.put(macro.getMacroName(), Pair.of(macro, 0));
                    }
                }
                
                sequence.move(caretOffset);

                int numOfForeaches = 0;
                while(sequence.movePrevious()) {
                    Token<LatteTopTokenId> token = sequence.token();
                    if(token.id() == LatteTopTokenId.LATTE) {
                        TokenHierarchy<CharSequence> th2 = TokenHierarchy.create(token.text(), LatteTokenId.language());
                        TokenSequence<LatteTokenId> sequence2 = th2.tokenSequence(LatteTokenId.language());
                        sequence2.moveStart();
                        boolean isForeach = false;
                        boolean isEndMacro = false;
                        while(sequence2.moveNext()) {
                            Token<LatteTokenId> token2 = sequence2.token();
                            if(token2.id() == LatteTokenId.SLASH && sequence2.offset() <= 2) {
                                isEndMacro = true;
                            }
                            String text = token2.text().toString();
                            //for comletion of end macros (preparation)
                            if(token2.id() == LatteTokenId.MACRO && paired.containsKey(text)) {
                                Pair<LatteMacro, Integer> p = paired.get(text);
                                if(!isEndMacro) {
                                    paired.put(text, Pair.of(p.first, p.second == null ? 1 : p.second + 1));
                                } else {
                                    paired.put(text, Pair.of(p.first, p.second == null ? -1 : p.second - 1));
                                }
                            }
                            // for parsing out macros which create new variables in scope
                            /*if(token2.id() == LatteTokenId.MACRO
                                    && (text.equals("default") || text.equals("var")
                                    || text.equals("assign") || text.equals("capture")))
                            {
                                boolean skipNext = false;
                                while(sequence2.moveNext()) {
                                    token2 = sequence2.token();
                                    if(!skipNext && (token2.id() == LatteTokenId.VARIABLE
                                            || token2.id() == LatteTokenId.TEXT))
                                    {
                                        text = token2.text().toString();
                                        if(token2.id() == LatteTokenId.TEXT)
                                            text = "$"+text;
                                        if(!dynamicVars.contains(text))
                                            dynamicVars.add(text);
                                        skipNext = true;
                                    }
                                    if(skipNext && token2.id() == LatteTokenId.COMA) {
                                        skipNext = false;
                                    }
                                }
                                continue;
                            }
                            // for parsing variables out of for and foreach
                            if(token2.id() == LatteTokenId.MACRO 
                                    && (text.equals("foreach") || text.equals("for")))
                            {
                                if(!isEndMacro) {
                                    numOfForeaches++;
                                    isForeach = true;
                                }
                                else {
                                    isForeach = false;
                                    isEndMacro = false;
                                    numOfForeaches--;
                                }
                            }
                            if(token2.id() == LatteTokenId.VARIABLE && isForeach && numOfForeaches > 0) {
                                if(foreaches.size() >= numOfForeaches) {
                                    List<String> vars = foreaches.get(numOfForeaches - 1);
                                    if(!vars.contains(text))
                                        vars.add(text);
                                } else {
                                    List<String> vars = new ArrayList<String>();
                                    vars.add(text);
                                    foreaches.add(numOfForeaches - 1, vars);
                                }
                            }*/
                        }
                    }
                }
                /*
                if(numOfForeaches > 0) {
                    for(List<String> vars : foreaches) {
                        for(String var : vars) {
                            if(!dynamicVars.contains(var))
                                dynamicVars.add(var);
                        }
                    }
                    if(!dynamicVars.contains("$iterator"))
                        dynamicVars.add("$iterator");
                }*/

                sequence.move(caretOffset);
                if (sequence.moveNext() || sequence.movePrevious()) {
                    Token<LatteTopTokenId> token = sequence.token();
                    if (token.id() == LatteTopTokenId.LATTE
                            || token.id() == LatteTopTokenId.LATTE_ATTR) {
                        // inside macro completion
                        TokenHierarchy<CharSequence> th2 = TokenHierarchy.create(token.text(), LatteTokenId.language());
                        TokenSequence<LatteTokenId> sequence2 = th2.tokenSequence(LatteTokenId.language());

                        // determining if caret is positioned in specially treated macros:
                        // (p)link, widget/control, extends, include
                        // which provide uncommon completion (presenter names, components, layouts)
                        sequence2.moveStart();
                        while (sequence2.moveNext()) {
                            Token<LatteTokenId> token2 = sequence2.token();
                            if (sequence2.offset() + sequence.offset() > caretOffset) {
                                break;
                            }
                            if (token2.id() == LatteTokenId.MACRO) {
                                String ttext = token2.text().toString();
                                if (ttext.equals("plink") || ttext.equals("link")
                                        || ttext.equals("widget") || ttext.equals("control")
                                        || ttext.equals("extends") || ttext.equals("include"))
                                {
                                    String written = "";    // text written to caret pos
                                    String whole = "";      // whole text of the param (overwritten by completion)
                                    int whiteOffset = -1, whiteLength = 0, whiteNum = 0;
                                    boolean ok = false;
                                    while (sequence2.moveNext()) {
                                        token2 = sequence2.token();
                                        //if processing token after caret position just update whole
                                        if (sequence2.offset() + sequence.offset() >= caretOffset) {
                                            if(token2.id() != LatteTokenId.COLON && token2.id() != LatteTokenId.TEXT)
                                                break;
                                            whole += token2.text();
                                        }
                                        if (whiteNum == 1 && sequence2.offset() + sequence.offset() < caretOffset) {
                                            written += token2.text();
                                            whole = written;
                                            ok = true;
                                        } else if (whiteNum > 1) {
                                            ok = false;
                                            break;
                                        }
                                        // counts whitespaces, this completion is used in first param only
                                        if (token2.id() == LatteTokenId.WHITESPACE) {
                                            whiteOffset = sequence2.offset() + sequence.offset();
                                            whiteLength = token2.length();
                                            whiteNum++;
                                            if(whiteNum == 1)
                                                ok = true;
                                        }
                                    }
                                    if (ok && (ttext.equals("plink") || ttext.equals("link"))) {
                                        completionResultSet.addAllItems(
                                                EditorUtils.parseLink(document, written, whiteOffset + whiteLength, whole.length()));
                                    }
                                    if (ok && (ttext.equals("widget") || ttext.equals("control"))) {
                                        completionResultSet.addAllItems(
                                                EditorUtils.parseControl(document, written, whiteOffset + whiteLength, whole.length()));
                                    }
                                    if (ok && (ttext.equals("extends") || ttext.equals("include"))) {
                                        completionResultSet.addAllItems(
                                                EditorUtils.parseLayout(document, written, whiteOffset + whiteLength, whole.length()));
                                    }
                                }
                            }
                        }
                        
                        // moving sequence for inside macro completion
                        sequence2.move(caretOffset - sequence.offset());
                        if (sequence2.movePrevious() || sequence2.moveNext()) {
                            Token<LatteTokenId> token2 = sequence2.token();
                            if(sequence.offset() + sequence2.offset() + token2.length() < caretOffset) {
                                if(sequence2.moveNext()) {
                                    token2 = sequence2.token();
                                }
                            }
                            // for variable completion (parses dynamically created variables too; see dynamicVars)
                            // if only dolar char is written => ERROR
                            // which following condition takes into account
                            // (also after whitespace)
                            /*if ((token2.id() == LatteTokenId.ERROR && token2.text().toString().startsWith("$"))
                                    || token2.id() == LatteTokenId.VARIABLE || token2.id() == LatteTokenId.WHITESPACE
                                    //|| token2.id() == LatteTokenId.MACRO || token2.id() == LatteTokenId.LD
                            ) {
                                try {
                                    int pos = caretOffset - sequence2.offset() - sequence.offset();
                                    String written = document.getText(sequence2.offset() + sequence.offset(), pos).trim();
                                    completionResultSet.addAllItems(EditorUtils.parseVariable(document, written, caretOffset, dynamicVars));
                                    //completionResultSet.addAllItems(EditorUtils.parseDynamic(dynamicVars, written, caretOffset));
                                } catch (Exception ex) {
                                }
                            }*/
                            // for macro name completion ( {macro| ..} )
                            if (token2.id() == LatteTokenId.MACRO || token2.id() == LatteTokenId.LD) {
                                String written = "";
                                // if caret is position just after left delimiter
                                if (token2.id() == LatteTokenId.LD) {
                                    sequence2.moveNext();
                                    token2 = sequence2.token();
                                }
                                // when only {} is written
                                if (token2.id() != LatteTokenId.RD) {
                                    written = token2.text().toString().replace("}", "");
                                    written = written.substring(0, caretOffset - sequence2.offset() - sequence.offset());
                                }
                                for (LatteMacro macro : macros) {
                                    if (macro.getMacroName().startsWith(written)) {
                                        completionResultSet.addItem(
                                            new MacroCompletionItem(
                                                macro.getMacroName(),
                                                sequence2.offset() + sequence.offset(),
                                                sequence2.offset() + sequence.offset() + token2.length()
                                            ));
                                    }
                                }
                            }
                            //helper completion
                            if(token2.id() == LatteTokenId.PIPE || token2.id() == LatteTokenId.TEXT) {
                                Token<LatteTokenId> token3 = token2;
                                // preceding token of helper name should be PIPE token
                                if(token2.id() == LatteTokenId.TEXT) {
                                    sequence2.movePrevious();
                                    token3 = sequence2.token();
                                }
                                sequence2.moveNext();
                                // is it PIPE token
                                if(token3 != null && token3.id() == LatteTokenId.PIPE) {
                                    String written = token2.text().toString();
                                    // if caret is position right after pipe char (don't overwrite it)
                                    if(written.equals("|"))
                                        written = "";
                                    for(String helper : helpers) {
                                        if(helper.startsWith(written)) {
                                            completionResultSet.addItem(
                                                new HelperCompletionItem(
                                                    helper,
                                                    sequence2.offset() + sequence.offset(),
                                                    sequence2.offset() + sequence.offset() + written.length()
                                                ));
                                        }
                                    }
                                }
                            }
                        }
                    } else {   /************* outside-macro completion *************/

                        // fills up list with possible endMacros and their friend macros
                        // see below
                        List<LatteMacro> endMacros = new ArrayList<LatteMacro>();
                        for(String key : paired.keySet()) {
                            Pair<LatteMacro, Integer> p = paired.get(key);
                            if(p.second != null && p.second > 0) {
                                endMacros.add(p.first);
                                if(friendMacros.containsKey(key)) {
                                    endMacros.addAll(Arrays.asList(friendMacros.get(key)));
                                }
                            }
                        }

                        // determining what was written:
                        try {
                            final StyledDocument bDoc = (StyledDocument) document;
                            final Element lineElement = bDoc.getParagraphElement(caretOffset);
                            int start = caretOffset;
                            int macroStart = start;

                            String filterX = "";
                            while (start >= lineElement.getStartOffset()) {
                                char c = bDoc.getText(start - 1, 1).charAt(0);
                                if (c == '{') {
                                    macroStart = start - 1;
                                    break;
                                }
                                if (Character.isWhitespace(c) || c == '}' || c == '<') {
                                    if(filterX.startsWith("n:")) {
                                        if(c == '<')
                                            macroStart = start - 1;
                                        else
                                            macroStart = start;
                                    }
                                    break;
                                }
                                filterX = c + filterX;
                                start--;
                            }
                            filter = bDoc.getText(macroStart, caretOffset - macroStart).trim();
                            startOffset = caretOffset - filter.length();
                        } catch (BadLocationException e) {
                        }
                        
                        if(!filter.equals("")) {
                            // end macro and friend macro completion
                            // FIXME: should use LatteMacro object (for caret position after macro name for param)
                            for (LatteMacro macro : endMacros) {
                                String m = macro.isPair() ? macro.getEndMacro() : macro.getMacro();
                                if(macro.isPair()) {
                                    macro = new LatteMacro("/"+macro.getEndMacroName());
                                }
                                if (m.startsWith(filter)) {
                                    completionResultSet.addItem(new LatteCompletionItem(macro, startOffset, caretOffset));
                                }
                            }
                            // macro completion
                            for (LatteMacro macro : macros) {
                                if (macro.getMacro().startsWith(filter)) {
                                    completionResultSet.addItem(new LatteCompletionItem(macro, startOffset, caretOffset));
                                }
                            }
                            // n:attribute completion
                            if(filter.startsWith("n:")) {
                                for (LatteMacro macro : macros) {
                                    if(!(macro instanceof LatteParamMacro) || !macro.isPair()) {
                                        continue;
                                    }
                                    String name = macro.getMacroName();
                                    String tag = "n:"+name;
                                    if (tag.startsWith(filter)) {
                                        completionResultSet.addItem(new MacroCompletionItem(tag, startOffset, caretOffset, true));
                                    }
                                    tag = "n:inner-"+name;
                                    if (tag.startsWith(filter)) {
                                        completionResultSet.addItem(new MacroCompletionItem(tag, startOffset, caretOffset, true));
                                    }
                                    tag = "n:tag-"+name;
                                    if (tag.startsWith(filter)) {
                                        completionResultSet.addItem(new MacroCompletionItem(tag, startOffset, caretOffset, true));
                                    }
                                }
                            }
                            // <n:tag completion
                            if(filter.startsWith("<n:")) {
                                for (LatteMacro macro : macros) {
                                    boolean hasParam = tagMacros.containsKey(macro.getMacroName());
                                    if((macro.getClass() == LatteMacro.class || hasParam)
                                            && Character.isLetter(macro.getMacroName().charAt(0)))
                                    {
                                        String name = macro.getMacroName();
                                        if (("<n:"+name).startsWith(filter)) {
                                            if(!hasParam) {
                                                completionResultSet.addItem(new MacroCompletionItem("<n:"+name, startOffset, caretOffset, true, name.length()+3));
                                            } else {
                                                String tag = "<n:"+name;
                                                for(String s : tagMacros.get(macro.getMacroName())) {
                                                    tag += " "+s+"=\"\"";
                                                }
                                                completionResultSet.addItem(new MacroCompletionItem(tag, startOffset, caretOffset, true, tag.length()-1));
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                // must be called before return;
                completionResultSet.finish();
            }
        }, jtc);
    }

    private String auto;

    /**
     * If text written starts with opening Latte delimiter, show completion
     * @param JTextComponent
     * @param written text
     * @return
     */
    public int getAutoQueryTypes(JTextComponent jtc, String string) {
        if(string.equals(":") && auto != null)
            auto += string;
        else
            auto = null;
        if(string.equals("n"))
            auto = string;
        
        return (string.startsWith("{") || string.startsWith("n:")
                || (auto != null && auto.equals("n:"))) ? COMPLETION_QUERY_TYPE : 0;
    }
}
