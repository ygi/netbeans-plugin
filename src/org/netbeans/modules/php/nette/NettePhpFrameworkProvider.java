/*
 */

package org.netbeans.modules.php.nette;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import javax.tools.FileObject;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.api.phpmodule.PhpModuleProperties;
import org.netbeans.modules.php.spi.commands.FrameworkCommandSupport;
import org.netbeans.modules.php.spi.editor.EditorExtender;
import org.netbeans.modules.php.spi.phpmodule.PhpFrameworkProvider;
import org.netbeans.modules.php.spi.phpmodule.PhpModuleActionsExtender;
import org.netbeans.modules.php.spi.phpmodule.PhpModuleExtender;
import org.netbeans.modules.php.spi.phpmodule.PhpModuleIgnoredFilesExtender;

/**
 *
 * @author redhead
 */
public class NettePhpFrameworkProvider extends PhpFrameworkProvider {
    
    private static final NettePhpFrameworkProvider INSTANCE = new NettePhpFrameworkProvider();

    public NettePhpFrameworkProvider() {
        super("Nette Framework", "Nette Framework is a powerful PHP framework for rapid and easy creation of high quality and innovative\nweb applications. It eliminates security risks, supports AJAX, DRY, KISS, MVC and code reusability.");
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
                set.add(new File(pm.getSourceDirectory().getPath()+"/app/.htaccess"));
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
        return null;
    }
    
}
