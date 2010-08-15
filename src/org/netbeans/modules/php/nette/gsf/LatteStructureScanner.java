
package org.netbeans.modules.php.nette.gsf;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.netbeans.modules.csl.api.OffsetRange;
import org.netbeans.modules.csl.api.StructureItem;
import org.netbeans.modules.csl.api.StructureScanner;
import org.netbeans.modules.csl.spi.ParserResult;

/**
 * just fake class - is needed to enable navigator of embedded languages in tmpl
 *
 * @author Martin Fousek
 */
public class LatteStructureScanner implements StructureScanner {

    @Override
    public List<? extends StructureItem> scan(ParserResult info) {
        return Collections.emptyList();
    }

    @Override
    public Map<String,List<OffsetRange>> folds(ParserResult info) {
        return  Collections.emptyMap();
    }

    @Override
    public Configuration getConfiguration() {
        return null;
    }


}
