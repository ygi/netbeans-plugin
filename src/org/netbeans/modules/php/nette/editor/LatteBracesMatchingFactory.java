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
