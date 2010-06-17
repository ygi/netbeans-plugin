
package org.netbeans.modules.php.nette.lexer;

import java.util.Collections;
import java.util.List;
import javax.swing.event.ChangeListener;
import org.netbeans.modules.csl.api.Error;
import org.netbeans.modules.csl.spi.ParserResult;
import org.netbeans.modules.parsing.api.Snapshot;
import org.netbeans.modules.parsing.api.Task;
import org.netbeans.modules.parsing.spi.ParseException;
import org.netbeans.modules.parsing.spi.Parser;
import org.netbeans.modules.parsing.spi.SourceModificationEvent;


/**
 * just fake class - is needed to enable navigator of embedded languages in tmpl
 *
 * @author Martin Fousek
 */
public class LatteGSFParser extends Parser {

    private Result fakeResult;

    @Override
    public void parse(Snapshot snapshot, Task task, SourceModificationEvent event) throws ParseException {
        fakeResult = new TmplFakeParserResult(snapshot);
    }

    @Override
    public Result getResult(Task task) throws ParseException {
        return fakeResult;
    }

    @Override
    public void cancel() {
        //do nothing
    }

    @Override
    public void addChangeListener(ChangeListener changeListener) {
        //do nothing
    }

    @Override
    public void removeChangeListener(ChangeListener changeListener) {
        //do nothing
    }

    private static class TmplFakeParserResult extends ParserResult {

        public TmplFakeParserResult(Snapshot s) {
            super(s);
        }

        @Override
        public List<? extends Error> getDiagnostics() {
            return Collections.emptyList();
        }

        @Override
        protected void invalidate() {
            //do nothing
        }

    }

}
