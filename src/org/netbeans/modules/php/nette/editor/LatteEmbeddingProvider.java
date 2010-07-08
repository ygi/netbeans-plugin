package org.netbeans.modules.php.nette.editor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.swing.text.Document;
import org.netbeans.api.lexer.InputAttributes;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.modules.parsing.api.Embedding;
import org.netbeans.modules.parsing.api.Snapshot;
import org.netbeans.modules.parsing.spi.EmbeddingProvider;
import org.netbeans.modules.parsing.spi.SchedulerTask;
import org.netbeans.modules.parsing.spi.TaskFactory;
import org.netbeans.modules.php.nette.lexer.LatteTokenId;
import org.netbeans.modules.php.nette.lexer.LatteTopTokenId;
/**
 * Provides embedded languages for HTML or LATTE tokens (language is denoted by mime-type)
 * @author redhead
 */
public class LatteEmbeddingProvider extends EmbeddingProvider {

    private static final List<String> arrayMacros = new ArrayList<String>();
    static {
        arrayMacros.add("var");
        arrayMacros.add("default");
        arrayMacros.add("assign");
    };

    private static final List<String> specialMacros = new ArrayList<String>();
    static {
        specialMacros.add("plink");
        specialMacros.add("link");
        specialMacros.add("widget");
        specialMacros.add("control");
        specialMacros.add("include");
        specialMacros.add("extends");
    };

    private static final List<String> blockMacros = new ArrayList<String>();
    static {
        blockMacros.add("foreach");
        blockMacros.add("for");
        blockMacros.add("if");
        blockMacros.add("ifset");
        blockMacros.add("ifcurrent");
        blockMacros.add("while");
        blockMacros.add("block");
        blockMacros.add("snippet");
    };

    private static final List<String> latteMacros = new ArrayList<String>();

    static {
        latteMacros.add(""); // pokracuj tu
    };

    @Override
    public List<Embedding> getEmbeddings(Snapshot snapshot) {
        // for sending atributes for LatteLexer (dynamic variables)
        // may be not necessary
        Document doc = snapshot.getSource().getDocument(true);
        InputAttributes inputAttributes = new InputAttributes();
        LatteParseData tmplParseData = new LatteParseData(doc);
        inputAttributes.setValue(LatteTokenId.language(), LatteParseData.class, tmplParseData, false);
        //inputAttributes.setValue(LatteTokenId.language(), "document", doc, false);
        doc.putProperty(InputAttributes.class, inputAttributes);


        TokenHierarchy<CharSequence> th = TokenHierarchy.create(snapshot.getText(), LatteTopTokenId.language());
        TokenSequence<LatteTopTokenId> sequence = th.tokenSequence(LatteTopTokenId.language());

        //TODO: neprochazet celou sekvenci (ale par radku pred a po caret)
        // jestli je to vubec mozny...

        sequence.moveStart();
        List<Embedding> embeddings = new ArrayList<Embedding>();
        List<Embedding> htmlEmbeddings = new ArrayList<Embedding>();

        int numOfBlocks = 0;
        List<Integer> tags = new ArrayList<Integer>();
        
        String prop = null;

        //String macro = null;
        while (sequence.moveNext()) {
            Token t = sequence.token();
            if (t.id() == LatteTopTokenId.LATTE) {
                //virtualni kod @@@ zamezuje chybam v syntaxi v css, js
                /*if(t.text().charAt(0) == '{')
                htmlEmbeddings.add(snapshot.create("@@@", "text/x-php5"));
                embeddings.add(snapshot.create(sequence.offset(), t.length(), "text/latte"));*/

                String props = (String) t.getProperty("macro");
                if(props != null)
                    prop = props;
                else if(t.text().charAt(0) == '{')
                    prop = null;

                TokenHierarchy<CharSequence> th2 = TokenHierarchy.create(t.text(), LatteTokenId.language());
                TokenSequence<LatteTokenId> sequence2 = th2.tokenSequence(LatteTokenId.language());

                boolean endMacro = false;
                String macro = prop != null ? prop : "";

                sequence2.moveStart();
                while (sequence2.moveNext()) {
                    Token<LatteTokenId> t2 = sequence2.token();
                    if (t2.id() == LatteTokenId.SLASH && sequence2.offset() <= 2) {
                        endMacro = true;
                    }
                    if (t2.id() == LatteTokenId.MACRO) {
                        macro = t2.text().toString();
                        prop = null;
                        continue;
                    }
                    int start = sequence2.offset() + sequence.offset();
                    int length = 0;
                    if (!macro.equals("")) {
                        if (arrayMacros.contains(macro)) {
                            // {var var => null} ->  $var = null;
                            List<Integer> starts = new ArrayList<Integer>();
                            List<Integer> lengths = new ArrayList<Integer>();
                            byte state = -1;
                            int numOfBrackets = 0;
                            String var = "";
                            do {
                                t2 = sequence2.token();
                                if (state == -1 || state == 0) {
                                    if (state == -1 && t2.id() != LatteTokenId.WHITESPACE) {
                                        start = sequence2.offset() + sequence.offset();
                                        state = 0;
                                        length = 0;
                                        var = "";
                                    }
                                    if (t2.id() == LatteTokenId.ASSIGN) {
                                        starts.add(var.trim().startsWith("$") ? start : -start);
                                        lengths.add(length);
                                        length = 0;
                                        state = 1;
                                        var = "";
                                        continue;
                                    }
                                    if (t2.id() != LatteTokenId.WHITESPACE) {
                                        length += t2.length();
                                        var += t2.text();
                                    }
                                }
                                if (state == 1 || state == 2) {
                                    if (state == 1) {
                                        start = sequence2.offset() + sequence.offset();
                                        length = 0;
                                        state = 2;
                                    }
                                    if (t2.id() == LatteTokenId.LNB) {
                                        numOfBrackets++;
                                    }
                                    if (t2.id() == LatteTokenId.RNB) {
                                        numOfBrackets--;
                                    }
                                    if (t2.id() == LatteTokenId.RD || (t2.id() == LatteTokenId.COMA
                                            && numOfBrackets == 0)) {
                                        starts.add(start);
                                        lengths.add(length);
                                        length = 0;
                                        state = -1;
                                        continue;
                                    }
                                    length += t2.length();
                                    var += t2.text();
                                }
                            } while (sequence2.moveNext());
                            htmlEmbeddings.add(snapshot.create("<?php ", "text/x-php5"));
                            String text = snapshot.getText().toString();
                            for (int i = 0; i < starts.size(); i += 2) {
                                start = starts.get(i) > 0 ? starts.get(i) : -starts.get(i);
                                var = text.substring(start, start + lengths.get(i));
                                var = starts.get(i) > 0 ? var : "$" + var;
                                if (macro.equals("default")) {
                                    htmlEmbeddings.add(snapshot.create("if(!isset(" + var + ")) {", "text/x-php5"));
                                }
                                if (starts.get(i) < 0) {
                                    htmlEmbeddings.add(snapshot.create("$", "text/x-php5"));
                                    htmlEmbeddings.add(snapshot.create(-starts.get(i), lengths.get(i), "text/x-php5"));
                                } else {
                                    htmlEmbeddings.add(snapshot.create(starts.get(i), lengths.get(i), "text/x-php5"));
                                }
                                htmlEmbeddings.add(snapshot.create("=", "text/x-php5"));
                                htmlEmbeddings.add(snapshot.create(starts.get(i + 1), lengths.get(i + 1), "text/x-php5"));
                                htmlEmbeddings.add(snapshot.create(";", "text/x-php5"));
                                if (macro.equals("default")) {
                                    htmlEmbeddings.add(snapshot.create("}", "text/x-php5"));
                                }
                                if (starts.get(i) < 0) {
                                    htmlEmbeddings.add(snapshot.create("$", "text/x-php5"));
                                    htmlEmbeddings.add(snapshot.create(-starts.get(i), lengths.get(i), "text/x-php5"));
                                } else {
                                    htmlEmbeddings.add(snapshot.create(starts.get(i), lengths.get(i), "text/x-php5"));
                                }
                                htmlEmbeddings.add(snapshot.create(";", "text/x-php5"));
                            }
                            htmlEmbeddings.add(snapshot.create(" ?>", "text/x-php5"));
                            break;
                        }
                        if (specialMacros.contains(macro)) {
                            // {link default var => $val} -> "default"; array(var => $val);
                            int firstStart = start;
                            int whiteSpace = 0;
                            do {
                                t2 = sequence2.token();
                                if (whiteSpace < 2) {
                                    if (t2.id() == LatteTokenId.WHITESPACE
                                            || t2.id() == LatteTokenId.RD
                                            || (whiteSpace == 1 && t2.id() == LatteTokenId.COMA)) {
                                        whiteSpace++;
                                        start = sequence.offset() + sequence2.offset() + t2.length();
                                    }
                                    continue;
                                }
                                if (t2.id() == LatteTokenId.RD) {
                                    break;
                                }
                                length += t2.length();
                            } while (sequence2.moveNext());
                            htmlEmbeddings.add(snapshot.create("<?php \"", "text/x-php5"));
                            htmlEmbeddings.add(snapshot.create(firstStart, start - firstStart, "text/x-php5"));
                            htmlEmbeddings.add(snapshot.create("\"", "text/x-php5"));
                            htmlEmbeddings.add(snapshot.create("; array( ", "text/x-php5"));
                            htmlEmbeddings.add(snapshot.create(start, length, "text/x-php5"));
                            htmlEmbeddings.add(snapshot.create(")?>", "text/x-php5"));
                            break;
                        }
                        if (latteMacros.contains(macro)) {
                            do {
                                t2 = sequence2.token();
                                if (t2.id() == LatteTokenId.RD) {
                                    break;
                                }
                                length += t2.length();
                            } while (sequence2.moveNext());
                            htmlEmbeddings.add(snapshot.create("<?php array( ", "text/x-php5"));
                            htmlEmbeddings.add(snapshot.create(start, length, "text/x-php5"));
                            htmlEmbeddings.add(snapshot.create(" ) ?>", "text/x-php5"));
                            break;
                        }
                        if (blockMacros.contains(macro)) {
                            if (!endMacro) {
                                boolean pipe = false;
                                do {
                                    t2 = sequence2.token();
                                    if(pipe && t2.id() != LatteTokenId.PIPE) {
                                        length--;
                                        break;
                                    } else {
                                        if(!pipe && t2.id() == LatteTokenId.PIPE) {      //is start of helper?
                                            pipe = true;
                                        } else {
                                            pipe = false;
                                        }
                                    }
                                    if (t2.id() == LatteTokenId.RD) {
                                        break;
                                    }
                                    length += t2.length();
                                } while (sequence2.moveNext());
                                //htmlEmbeddings.add(snapshot.create(snapshot.getText().subSequence(start, start+length), "text/x-php5"));
                                if(macro.equals("block") || macro.equals("snippet")) {
                                    htmlEmbeddings.add(snapshot.create("<?php \"", "text/x-php5"));
                                    htmlEmbeddings.add(snapshot.create(start, length, "text/x-php5"));
                                    htmlEmbeddings.add(snapshot.create("\";{?>", "text/x-php5"));
                                    if (prop == null && macro.equals("block"))
                                        numOfBlocks++;
                                } else {
                                    htmlEmbeddings.add(snapshot.create("<?php " + macro + "(", "text/x-php5"));
                                    htmlEmbeddings.add(snapshot.create(start, length, "text/x-php5"));
                                    htmlEmbeddings.add(snapshot.create(");{", "text/x-php5"));
                                    if (macro.equals("foreach")) {
                                        htmlEmbeddings.add(snapshot.create("$iterator=new SmartCachingIterator;", "text/x-php5"));
                                    }
                                    htmlEmbeddings.add(snapshot.create("?>", "text/x-php5"));
                                }
                                if (prop != null) {
                                    int i = tags.remove(tags.size()-1);
                                    tags.add(i+1);
                                }
                            } else {
                                htmlEmbeddings.add(snapshot.create("<?php } ?>", "text/x-php5"));
                                if(macro.equals("block"))
                                    numOfBlocks--;
                            }
                            break;
                        }
                        if (macro.equals("attr")) {
                            int numOfBrackets = 0;
                            int whiteSpace = 0;
                            List<Integer> starts = new ArrayList<Integer>();
                            List<Integer> lengths = new ArrayList<Integer>();
                            do {
                                t2 = sequence2.token();
                                if (whiteSpace == 0) {
                                    if (t2.id() == LatteTokenId.WHITESPACE
                                            || t2.id() == LatteTokenId.RD) {
                                        whiteSpace++;
                                        starts.add(sequence.offset() + sequence2.offset() + t2.length());
                                        length = 0;
                                    }
                                    continue;
                                } else { 
                                    if(t2.id() == LatteTokenId.LNB)
                                        numOfBrackets++;
                                    if(t2.id() == LatteTokenId.RNB)
                                        numOfBrackets--;
                                    if((t2.id() == LatteTokenId.WHITESPACE || t2.id() == LatteTokenId.RD)
                                            && numOfBrackets == 0) {
                                        lengths.add(length);
                                        if(t2.id() != LatteTokenId.RD)
                                            starts.add(sequence.offset() + sequence2.offset() + t2.length());
                                        else
                                            break;
                                        length = 0;
                                        continue;
                                    }
                                }
                                length += t2.length();
                            } while (sequence2.moveNext());
                            if(numOfBrackets != 0)
                                lengths.add(length);
                            htmlEmbeddings.add(snapshot.create("<?php $v", "text/x-php5"));
                            for(int i = 0; i < lengths.size(); i++) {
                                if(snapshot.getText().subSequence(starts.get(i), starts.get(i)+lengths.get(i)).toString().trim().equals(""))
                                    continue;
                                htmlEmbeddings.add(snapshot.create("->", "text/x-php5"));
                                htmlEmbeddings.add(snapshot.create(starts.get(i), lengths.get(i), "text/x-php5"));
                            }
                            htmlEmbeddings.add(snapshot.create(";?>", "text/x-php5"));
                            break;
                        }
                        do {
                            t2 = sequence2.token();
                            if (t2.id() == LatteTokenId.RD) {
                                break;
                            }
                            length += t2.length();
                        } while (sequence2.moveNext());
                        htmlEmbeddings.add(snapshot.create("<?php \"", "text/x-php5"));
                        htmlEmbeddings.add(snapshot.create(start, length, "text/x-php5"));
                        htmlEmbeddings.add(snapshot.create("\" ?>", "text/x-php5"));
                        break;
                    }
                    if(t2.id() == LatteTokenId.VARIABLE 
                            || (t2.id() == LatteTokenId.ERROR && t2.text().equals("$")))
                    {
                        do {
                            t2 = sequence2.token();
                            if (t2.id() == LatteTokenId.RD || t2.id() == LatteTokenId.PIPE) {
                                break;
                            }
                            length += t2.length();
                        } while (sequence2.moveNext());
                        htmlEmbeddings.add(snapshot.create("<?php ", "text/x-php5"));
                        htmlEmbeddings.add(snapshot.create(start, length, "text/x-php5"));
                        htmlEmbeddings.add(snapshot.create(" ?>", "text/x-php5"));
                    }
                }
            } else {
                //jinak html resp. php
                LatteTopTokenId id = (LatteTopTokenId)t.id();
                if (t.id() == LatteTopTokenId.HTML_TAG || t.id() == LatteTopTokenId.LATTE_TAG) {
                    String tag = t.text().toString();
                    if(tag.startsWith("<") && tag.charAt(1) != '/') {
                        if(t.id() == LatteTopTokenId.LATTE_TAG)
                            prop = (String) t.getProperty("macro");
                        String tagName = tag.substring(1);
                        tags.add(0);
                    } else if(tag.equals("/>") || tag.startsWith("</")) {
                        //String tagName = tag.substring(2);
                        if(tags.size() > 0) {
                            int c = tags.remove(tags.size()-1);
                            if(c > 0) {
                                htmlEmbeddings.add(snapshot.create("<?php ", "text/x-php5"));
                                for(int i = 0; i < c; i++) {
                                    htmlEmbeddings.add(snapshot.create("}", "text/x-php5"));
                                }
                                htmlEmbeddings.add(snapshot.create(" ?>", "text/x-php5"));
                            }
                        }
                        prop = null;
                    } else if(tag.equals(">") && t.id() == LatteTopTokenId.LATTE_TAG) {
                        prop = null;
                    }
                }
                htmlEmbeddings.add(snapshot.create(sequence.offset(), t.length(), "text/x-php5"));
            }
        }
        if(numOfBlocks == 1) {
            htmlEmbeddings.add(snapshot.create("<?php } ?>", "text/x-php5"));
        }

        //sjednoti text/x-php5 embeddingy (prechody mezi jazyky)
        if (!htmlEmbeddings.isEmpty()) {
            embeddings.add(Embedding.create(htmlEmbeddings));
        }


        if (embeddings.isEmpty()) {
            return Collections.emptyList();
        } else {
            return embeddings;
        }
    }

    
    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public void cancel() {
        //do nothing
    }

    /**
     * Factory for creating new LatteEmbeddingProvider.
     */
    public static final class Factory extends TaskFactory {

        @Override
        public Collection<SchedulerTask> create(final Snapshot snapshot) {
            return Collections.<SchedulerTask>singletonList(new LatteEmbeddingProvider());
        }
    }
}
