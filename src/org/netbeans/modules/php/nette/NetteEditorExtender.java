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

package org.netbeans.modules.php.nette;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.netbeans.modules.csl.spi.ParserResult;
import org.netbeans.modules.parsing.api.ParserManager;
import org.netbeans.modules.parsing.api.ResultIterator;
import org.netbeans.modules.parsing.api.Source;
import org.netbeans.modules.parsing.api.UserTask;
import org.netbeans.modules.parsing.spi.ParseException;
import org.netbeans.modules.php.api.editor.PhpBaseElement;
import org.netbeans.modules.php.api.editor.PhpClass;
import org.netbeans.modules.php.api.editor.PhpVariable;
import org.netbeans.modules.php.editor.CodeUtils;
import org.netbeans.modules.php.editor.model.ModelUtils;
import org.netbeans.modules.php.editor.model.TypeScope;
import org.netbeans.modules.php.editor.parser.PHPParseResult;
import org.netbeans.modules.php.editor.parser.api.Utils;
import org.netbeans.modules.php.editor.parser.astnodes.visitors.DefaultVisitor;
import org.netbeans.modules.php.editor.parser.astnodes.Assignment;
import org.netbeans.modules.php.editor.parser.astnodes.FieldAccess;
import org.netbeans.modules.php.editor.parser.astnodes.Variable;
import org.netbeans.modules.php.nette.utils.EditorUtils;
import org.netbeans.modules.php.spi.editor.EditorExtender;
import org.openide.filesystems.FileObject;

/**
 * Should parse out variables presenter sents to template
 * FIXME dodelat!
 * @author Radek Ježdík
 */
public class NetteEditorExtender extends EditorExtender {

    @Override
    public List<PhpBaseElement> getElementsForCodeCompletion(FileObject fo) {
        return new ArrayList<PhpBaseElement>(parseAction(fo));
    }

    private Set<PhpVariable> parseAction(final FileObject template) {
        final Set<PhpVariable> phpVariables = new HashSet<PhpVariable>();

        FileObject presenter = EditorUtils.getPresenterFile(template);
        if (presenter == null) {
            return phpVariables;
		}
        
        try {
            ParserManager.parse(Collections.singleton(Source.create(presenter)), new UserTask() {

                @Override
                public void run(ResultIterator ri) throws Exception {
                    ParserResult parseResult = (ParserResult) ri.getParserResult();
                    final NettePresenterVisitor visitor = new NettePresenterVisitor(template, (PHPParseResult) parseResult);
                    visitor.scan(Utils.getRoot(parseResult));
                    phpVariables.addAll(visitor.getVars());
                }

            });
        } catch (ParseException ex) {
            
        }

        return phpVariables;
    }

    private static final class NettePresenterVisitor extends DefaultVisitor {

        private final FileObject action = null;
        private final PHPParseResult actionParseResult;
        private final PhpVariable view = new PhpVariable("$this", "Template"); // NOI18N

        private final List<PhpVariable> vars = new ArrayList<PhpVariable>();
        
        public NettePresenterVisitor(FileObject view, PHPParseResult result) {
            this.actionParseResult = result;
        }

        @Override
        public void visit(Assignment assign) {
            super.visit(assign);
            if(assign.getLeftHandSide() instanceof FieldAccess) {
                FieldAccess field = (FieldAccess) assign.getLeftHandSide();

                Variable var = field.getField();

                String name = null;
                String fqn = null;
                for (TypeScope typeScope : ModelUtils.resolveType(actionParseResult.getModel(), assign)) {
                    name = typeScope.getName();
                    fqn = typeScope.getFullyQualifiedName().toString();
                    break;
                }

                PhpClass type = new PhpClass(view.getName(), view.getFullyQualifiedName());
                vars.add(new PhpVariable(CodeUtils.extractVariableName(var),
                        name != null ? new PhpClass(name, fqn).getFullyQualifiedName() : null));
            }
        }

        public PhpVariable getView() {
            return view;
        }

        public List<PhpVariable> getVars() {
            return vars;
        }

    }
    
}
