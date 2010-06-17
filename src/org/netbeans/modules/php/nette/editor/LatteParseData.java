
package org.netbeans.modules.php.nette.editor;

import java.util.ArrayList;
import javax.swing.text.Document;
import org.netbeans.modules.php.nette.utils.EditorUtils;

/**
 * Class for storing variables sent from presenters into templates.
 * @author Martin Fousek
 */
public class LatteParseData {

    /**
     * List of variables for actual template.
     */
    public ArrayList<String> variables = new ArrayList<String>();

    /**
     * Parse file for sent variables.
     * @param doc document which should be scanned
     */
    public LatteParseData(Document doc) {
        variables = EditorUtils.getKeywordsForView(doc);
    }

    /**
     * Get stored variables.
     * @return list of variables
     */
    public ArrayList<String> getVariables() {
        return variables;
    }

}
