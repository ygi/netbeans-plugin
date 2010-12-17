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

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.api.phpmodule.PhpModuleProperties;
import org.netbeans.modules.php.spi.commands.FrameworkCommandSupport;
import org.netbeans.modules.php.spi.editor.EditorExtender;
import org.netbeans.modules.php.spi.phpmodule.PhpFrameworkProvider;
import org.netbeans.modules.php.spi.phpmodule.PhpModuleActionsExtender;
import org.netbeans.modules.php.spi.phpmodule.PhpModuleExtender;
import org.netbeans.modules.php.spi.phpmodule.PhpModuleIgnoredFilesExtender;
import org.openide.util.NbBundle;

/**
 *
 * @author Radek Ježdík
 */
public class NettePhpFrameworkProvider extends PhpFrameworkProvider {
    
    private static final NettePhpFrameworkProvider INSTANCE = new NettePhpFrameworkProvider();

    public NettePhpFrameworkProvider() {
		super(NbBundle.getMessage(NettePhpFrameworkProvider.class, "OpenIDE-Module-Name"), 
				NbBundle.getMessage(NettePhpFrameworkProvider.class, "OpenIDE-Module-Short-Description"));
    }
    
    public static NettePhpFrameworkProvider getInstance() {
        return INSTANCE;
    }

    @Override
    public boolean isInPhpModule(PhpModule pm) {
        return true;
    }

    @Override
    public File[] getConfigurationFiles(PhpModule pm) {
        return new File[0];
    }

    @Override
    public PhpModuleExtender createPhpModuleExtender(PhpModule pm) {
        return new NettePhpModuleExtender();
    }

    @Override
    public PhpModuleProperties getPhpModuleProperties(PhpModule pm) {
        return new PhpModuleProperties();
    }

    @Override
    public PhpModuleActionsExtender getActionsExtender(PhpModule pm) {
        return null;
    }

    /**
     * Determines what files or directories should be hidden in Projects window (but they exist)
     * @param pm
     * @return
     */
    @Override
    public PhpModuleIgnoredFilesExtender getIgnoredFilesExtender(final PhpModule pm) {
        return new PhpModuleIgnoredFilesExtender() {

            @Override
            public Set<File> getIgnoredFiles() {
                HashSet<File> set = new HashSet<File>();
                //set.add(new File(pm.getSourceDirectory().getPath()+"/app/temp"));
                //set.add(new File(pm.getSourceDirectory().getPath()+"/app/sessions"));
                //set.add(new File(pm.getSourceDirectory().getPath()+"/app/logs"));
                //set.add(new File(pm.getSourceDirectory().getPath()+"/app/.htaccess"));
                return set;
            }
        };
    }

    @Override
    public FrameworkCommandSupport getFrameworkCommandSupport(PhpModule pm) {
        return null;
    }

    @Override
    public EditorExtender getEditorExtender(PhpModule pm) {
        return new NetteEditorExtender();
    }
    
}
