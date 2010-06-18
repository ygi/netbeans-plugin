package org.netbeans.modules.php.nette.editor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import org.netbeans.api.lexer.InputAttributes;
import org.netbeans.api.lexer.LanguagePath;
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

    @Override
    public List<Embedding> getEmbeddings(Snapshot snapshot) {
        // for sending atributes for LatteLexer (dynamic variables)
        Document doc = snapshot.getSource().getDocument(true);
        InputAttributes inputAttributes = new InputAttributes();
        LatteParseData tmplParseData = new LatteParseData(doc);
        inputAttributes.setValue(LatteTokenId.language(), LatteParseData.class, tmplParseData, false);
        //inputAttributes.setValue(LatteTokenId.language(), "document", doc, false);
        doc.putProperty(InputAttributes.class, inputAttributes);
        
        
        TokenHierarchy<CharSequence> th = TokenHierarchy.create(snapshot.getText(), LatteTopTokenId.language());
        TokenSequence<LatteTopTokenId> sequence = th.tokenSequence(LatteTopTokenId.language());

        //TODO: neprochazet celou sekvenci (ale par radku pred a po caret)

        sequence.moveStart();
        List<Embedding> embeddings = new ArrayList<Embedding>();
        List<Embedding> htmlEmbeddings = new ArrayList<Embedding>();
        List<Embedding> latteEmbeddings = new ArrayList<Embedding>();

        String macro = null;
        while (sequence.moveNext()) {
            Token t = sequence.token();
            if (t.id() == LatteTopTokenId.LATTE) {
                //virtualni kod @@@ zamezuje chybam v syntaxi v css, js
                if(t.text().charAt(0) == '{')
                    htmlEmbeddings.add(snapshot.create("@@@", "text/x-php5"));
                embeddings.add(snapshot.create(sequence.offset(), t.length(), "text/latte"));
            }/* else if(t.id() == LatteTopTokenId.LATTE_OPEN) {
                latteEmbeddings.add(snapshot.create("{"+macro+" ", "text/latte"));
            } else if(t.id() == LatteTopTokenId.LATTE_CLOSE) {
                inputAttributes.setValue(LanguagePath.get(LanguagePath.get(LatteTopTokenId.language()), LatteTokenId.language()), "macro", "}", false);
                latteEmbeddings.add(snapshot.create("}", "text/latte"));
                embeddings.add(Embedding.create(latteEmbeddings));
            }*//* else if(t.id() == LatteTopTokenId.LATTE_ATTR) {
                //embeddings.add(snapshot.create("@@@", "text/latte"));
                //macro = (String) t.getProperty("macro");

                htmlEmbeddings.add(snapshot.create("@@@", "text/x-php5"));
                embeddings.add(snapshot.create(sequence.offset(), t.length(), "text/latte"));
                //macro = null;
                //list.add(snapshot.create(sequence.offset(), t.length(), "text/latte"));
                //list.add(snapshot.create("}", "text/latte"));
                //embeddings.add(Embedding.create(list));
            }*/ else {
                //jinak html resp. php
                htmlEmbeddings.add(snapshot.create(sequence.offset(), t.length(), "text/x-php5"));
            }
        }

        //sjednoti text/x-php5 embeddingy (prechody mezi jazyky)
        if(!htmlEmbeddings.isEmpty())
            embeddings.add(Embedding.create(htmlEmbeddings));

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
