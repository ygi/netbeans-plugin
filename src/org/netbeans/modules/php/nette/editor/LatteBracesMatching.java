/*
 */
package org.netbeans.modules.php.nette.editor;

import java.util.ArrayList;
import java.util.List;
import javax.swing.text.BadLocationException;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.modules.php.nette.lexer.LatteTokenId;
import org.netbeans.modules.php.nette.lexer.LatteTopTokenId;
import org.netbeans.modules.php.nette.macros.LatteMacro;
import org.netbeans.spi.editor.bracesmatching.BracesMatcher;
import org.netbeans.spi.editor.bracesmatching.MatcherContext;
import org.openide.util.Exceptions;

/**
 *
 * @author redhead
 */
public class LatteBracesMatching implements BracesMatcher {

    private MatcherContext context;

    String macroName;
    boolean isEndMacro;

    int mStart;
    int mLength;

    public LatteBracesMatching(MatcherContext context) {
        this.context = context;
    }

    public int[] findOrigin() throws InterruptedException, BadLocationException {
        int searchOffset = context.isSearchingBackward() ? context.getSearchOffset() : context.getSearchOffset() + 1;
        //((AbstractDocument)context.getDocument()).readLock();

        if(MatcherContext.isTaskCanceled())
            return null;

        TokenHierarchy<String> th = TokenHierarchy.create(context.getDocument().getText(0, context.getDocument().getLength()), LatteTopTokenId.language());
        TokenSequence<LatteTopTokenId> ts = th.tokenSequence(LatteTopTokenId.language());

        ts.move(searchOffset);
        if(ts.moveNext()) {
            if (context.isSearchingBackward() && ts.offset() + ts.token().length() < searchOffset) {
                //check whether the searched position doesn't overlap the token boundaries
                return null;
            }
            Token<LatteTopTokenId> t = ts.token();
            if(t.id() == LatteTopTokenId.LATTE) {
                if(t.text().charAt(0) != '{') {
                    return null;
                }
                TokenHierarchy<CharSequence> th2 = TokenHierarchy.create(t.text(), LatteTokenId.language());
                TokenSequence<LatteTokenId> ts2 = th2.tokenSequence(LatteTokenId.language());
                ts2.moveStart();
                int i = 0;
                while(ts2.moveNext() && i < 3) {
                    Token<LatteTokenId> t2 = ts2.token();
                    // macro has name
                    if(t2.id() == LatteTokenId.MACRO) {
                        macroName = t2.toString();
                        mStart = ts.offset();
                        mLength = t.length();
                        int[] ints = new int[] {
                            ts.offset(), ts.offset() + t.length(),                  //whole area
                            ts.offset(), ts.offset() + ts2.offset() + t2.length(),  //left delimiter + macro
                            ts.offset() + t.length() - 1, ts.offset() + t.length()  //right delimiter
                        };
                        return ints;
                    }
                    if(t2.id() == LatteTokenId.SLASH && i == 1) {
                        isEndMacro = true;
                    }
                    i++;
                }
                // macro doesn't have name, hi-light just delimiters
                return new int[] {
                    ts.offset(), ts.offset() + t.length(),                          //whole area
                    ts.offset(), ts.offset() + 1,                                   //left delimiter
                    ts.offset() + t.length() - 1, ts.offset() + t.length()          //right delimiter
                };
            }
        }
        
        return null;
    }

    public int[] findMatches() throws InterruptedException, BadLocationException {
        try {
            final int searchOffset = context.isSearchingBackward() ? context.getSearchOffset() : context.getSearchOffset() + 1;

            if(MatcherContext.isTaskCanceled()) {
                return new int[] { searchOffset, searchOffset };
            }
            
            TokenHierarchy<String> th = TokenHierarchy.create(context.getDocument().getText(0, context.getDocument().getLength()), LatteTopTokenId.language());
            TokenSequence<LatteTopTokenId> ts = th.tokenSequence(LatteTopTokenId.language());

            List<String> friends = new ArrayList<String>();
            List<String> endMacros = new ArrayList<String>();
            int embeddedMacros = 0;

            ts.move(searchOffset);

            if(!isEndMacro) {
                boolean isPair = false;
                for(LatteMacro m : LatteCompletionProvider.macros) {
                    if(m.getMacroName().equals(macroName) && m.isPair()) {
                        isPair = true;
                        break;
                    }
                }
                for(String m : LatteCompletionProvider.friendMacros.keySet()) {
                    for(LatteMacro f : LatteCompletionProvider.friendMacros.get(m)) {
                        if(f.getMacroName().equals(macroName) || m.equals(macroName)) {
                            if(!m.equals(macroName)) {
                                if(!friends.contains(m)) {
                                    friends.add(m);
                                }
                            }
                            for(LatteMacro macro : LatteCompletionProvider.friendMacros.get(m)) {
                                if(!friends.contains(macro.getMacroName())) {
                                    friends.add(macro.getMacroName());
                                }
                                if(!friends.contains(macro.getEndMacroName())) {
                                    friends.add(macro.getEndMacroName());
                                }
                            }
                            isPair = true;
                            break;
                        }
                    }
                }
                if(!isPair) {
                    // it is not pair macro, return zero length offset = hack against matching error (red hi-light)
                    return new int[] { searchOffset, searchOffset };
                }
            }
            for(LatteMacro m : LatteCompletionProvider.getMacrosByEnd(macroName)) {
                if(!endMacros.contains(m.getMacroName())) {
                    endMacros.add(m.getMacroName());
                }
            }
            if(!isEndMacro) {
                ts.moveNext();
                if(ts.offset() >= mStart + mLength) {
                    ts.movePrevious();
                }
            } else {
                ts.movePrevious();
                if(ts.offset() < mStart) {
                    ts.moveNext();
                }
            }

            while(isEndMacro ? ts.movePrevious() : ts.moveNext()) {
                Token<LatteTopTokenId> t = ts.token();
                if(t.id() == LatteTopTokenId.LATTE) {
                    if(t.text().charAt(0) != '{') {
                        continue;
                    }
                    TokenHierarchy<CharSequence> th2 = TokenHierarchy.create(t.text(), LatteTokenId.language());
                    TokenSequence<LatteTokenId> ts2 = th2.tokenSequence(LatteTokenId.language());
                    ts2.moveStart();

                    boolean isEndMacro2 = false;
                    int i = 0;
                    while(ts2.moveNext() && i < 3) {
                        Token<LatteTokenId> t2 = ts2.token();
                        if(t2.id() == LatteTokenId.MACRO) {
                            if(embeddedMacros == 0) {
                                if((endMacros.contains(t2.text().toString()) && isEndMacro != isEndMacro2)
                                        || friends.contains(t2.text().toString())) {
                                    return new int[]{
                                                ts.offset(), ts.offset() + ts2.offset() + t2.length(),
                                                ts.offset() + t.length() - 1, ts.offset() + t.length()
                                            };
                                }
                            }
                            LatteMacro m = LatteCompletionProvider.getMacro(t2.text().toString());
                            if(m != null && m.isPair()) {
                                if(!isEndMacro2) {
                                    embeddedMacros++;

                                } else {
                                    embeddedMacros--;
                                }
                            }
                        }
                        if(t2.id() == LatteTokenId.SLASH && i == 1) {
                            isEndMacro2 = true;
                        }
                        i++;
                    }
                }
            }

            if(macroName.equals("block") && !isEndMacro) // {/block} can be ommited
            {
                return new int[]{searchOffset, searchOffset};

            }
        } catch(Exception e) {
            Exceptions.printStackTrace(e);
        }

        return null; // no matching found => matching error (red hi-light)
    }
}
