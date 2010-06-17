/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.php.nette.editor;

import java.util.ArrayList;
import java.util.List;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import javax.swing.text.StyledDocument;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.api.lexer.TokenSequence;
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
 *
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
        new LatteParamMacro("elseif", false),
        new LatteParamMacro("elseifset", false),
        new LatteMacro("else"),
        new LatteParamMacro("for", true),
        new LatteParamMacro("foreach", true),
        new LatteParamMacro("while", true),
        new LatteParamMacro("include", false),
        new LatteParamMacro("extends", false),
        new LatteParamMacro("layout", false),
        new LatteParamMacro("ifCurrent", true, "if"),
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

    public static String[] helpers = {
        "escapeHtml", "escapeHtmlComment", "escapeXML", "escapeCss", "escapeHtmlCss", "escapeJs",
        "escapeHtmlJs", "strip", "indent", "indentCb", "date", "bytes", "length",
        "replace", "nl2br", "stripTags", "translate", "number",
    };

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

                sequence.move(caretOffset);

                List<List<String>> foreaches = new ArrayList<List<String>>();
                List<String> dynamicVars = new ArrayList<String>();

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
                            if(token2.id() == LatteTokenId.MACRO 
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
                            }
                        }
                    }
                }
                if(numOfForeaches > 0) {
                    for(List<String> vars : foreaches) {
                        for(String var : vars) {
                            if(!dynamicVars.contains(var))
                                dynamicVars.add(var);
                        }
                    }
                    if(!dynamicVars.contains("$iterator"))
                        dynamicVars.add("$iterator");
                }

                sequence.move(caretOffset);

                if (sequence.moveNext() || sequence.movePrevious()) {
                    Token<LatteTopTokenId> token = sequence.token();
                    if (token.id() == LatteTopTokenId.LATTE
                            || token.id() == LatteTopTokenId.LATTE_ATTR) {
                        //inside macro completion
                        TokenHierarchy<CharSequence> th2 = TokenHierarchy.create(token.text(), LatteTokenId.language());
                        TokenSequence<LatteTokenId> sequence2 = th2.tokenSequence(LatteTokenId.language());
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
                                    String written = "";
                                    String whole = "";
                                    int whiteOffset = -1, whiteLength = 0, whiteNum = 0;
                                    boolean ok = false;
                                    while (sequence2.moveNext()) {
                                        token2 = sequence2.token();
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
                        sequence2.move(caretOffset - sequence.offset());
                        if (sequence2.movePrevious() || sequence2.moveNext()) {
                            Token<LatteTokenId> token2 = sequence2.token();
                            if(sequence.offset() + sequence2.offset() + token2.length() < caretOffset) {
                                if(sequence2.moveNext()) {
                                    token2 = sequence2.token();
                                }
                            }
                            if ((token2.id() == LatteTokenId.ERROR && token2.text().toString().startsWith("$"))
                                    || token2.id() == LatteTokenId.VARIABLE || token2.id() == LatteTokenId.WHITESPACE
                                    /*|| token2.id() == LatteTokenId.MACRO || token2.id() == LatteTokenId.LD*/) {
                                try {
                                    int pos = caretOffset - sequence2.offset() - sequence.offset();
                                    String written = document.getText(sequence2.offset() + sequence.offset(), pos).trim();
                                    completionResultSet.addAllItems(EditorUtils.parseVariable(document, written, caretOffset, dynamicVars));
                                    //completionResultSet.addAllItems(EditorUtils.parseDynamic(dynamicVars, written, caretOffset));
                                } catch (Exception ex) {
                                    
                                }
                            }
                            if (token2.id() == LatteTokenId.MACRO || token2.id() == LatteTokenId.LD) {
                                String written = "";
                                if (token2.id() == LatteTokenId.LD) {
                                    sequence2.moveNext();
                                    token2 = sequence2.token();
                                }
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
                            if(token2.id() == LatteTokenId.PIPE || token2.id() == LatteTokenId.TEXT) {
                                Token<LatteTokenId> token3 = token2;
                                if(token2.id() == LatteTokenId.TEXT) {
                                    sequence2.movePrevious();
                                    token3 = sequence2.token();
                                }
                                sequence2.moveNext();
                                if(token3 != null && token3.id() == LatteTokenId.PIPE) {
                                    String written = token2.text().toString();
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
                    } else {
                        // macro completion
                        try {
                            final StyledDocument bDoc = (StyledDocument) document;
                            final Element lineElement = bDoc.getParagraphElement(caretOffset);
                            int start = caretOffset;
                            int macroStart = start;

                            while (start >= lineElement.getStartOffset()) {
                                char c = bDoc.getText(start - 1, 1).charAt(0);
                                if (c == '{') {
                                    macroStart = start - 1;
                                    break;
                                }
                                if (Character.isWhitespace(c) || c == '}') {
                                    break;
                                }
                                start--;
                            }
                            filter = bDoc.getText(macroStart, caretOffset - macroStart).trim();
                            startOffset = caretOffset - filter.length();
                        } catch (BadLocationException e) {
                        }

                        for (LatteMacro macro : macros) {
                            if (macro.getMacro().startsWith(filter)) {
                                completionResultSet.addItem(new LatteCompletionItem(macro, startOffset, caretOffset));
                            }
                        }
                    }
                }
                completionResultSet.finish();
            }
        }, jtc);
    }

    public int getAutoQueryTypes(JTextComponent jtc, String string) {
        jtc.getCaretPosition();

        return string.startsWith("{") ? COMPLETION_QUERY_TYPE : 0;
    }
}
