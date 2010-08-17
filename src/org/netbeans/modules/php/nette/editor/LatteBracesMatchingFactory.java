/*
 */
package org.netbeans.modules.php.nette.editor;

import java.util.List;
import javax.swing.text.Document;
import org.netbeans.api.lexer.Language;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.modules.php.nette.lexer.LatteTopTokenId;
import org.netbeans.spi.editor.bracesmatching.BracesMatcher;
import org.netbeans.spi.editor.bracesmatching.BracesMatcherFactory;
import org.netbeans.spi.editor.bracesmatching.MatcherContext;

/**
 *
 * @author Radek Ježdík
 */
public class LatteBracesMatchingFactory implements BracesMatcherFactory {

    public BracesMatcher createMatcher(final MatcherContext context) {
        final LatteBracesMatching[] ret = {null};
        context.getDocument().render(new Runnable() {

            public void run() {
                TokenHierarchy<Document> hierarchy = TokenHierarchy.get(context.getDocument());

                if(hierarchy.tokenSequence().language() == LatteTopTokenId.language()) {
                    ret[0] = new LatteBracesMatching(context);
                    return;
                }

                List<TokenSequence<?>> ets = hierarchy.embeddedTokenSequences(context.getSearchOffset(), context.isSearchingBackward());
                for(TokenSequence ts : ets) {
                    Language language = ts.language();
                    if(language == LatteTopTokenId.language()) {
                        ret[0] = new LatteBracesMatching(context);
                        return;
                    }
                }
            }
        });
        return ret[0];
    }
}
