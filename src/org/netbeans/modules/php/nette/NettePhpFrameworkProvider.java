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
import java.io.FilenameFilter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.netbeans.modules.php.api.phpmodule.BadgeIcon;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.api.phpmodule.PhpModuleProperties;
import org.netbeans.modules.php.nette.utils.EditorUtils;
import org.netbeans.modules.php.nette.utils.FileUtils;
import org.netbeans.modules.php.spi.commands.FrameworkCommandSupport;
import org.netbeans.modules.php.spi.editor.EditorExtender;
import org.netbeans.modules.php.spi.phpmodule.PhpFrameworkProvider;
import org.netbeans.modules.php.spi.phpmodule.PhpModuleActionsExtender;
import org.netbeans.modules.php.spi.phpmodule.PhpModuleExtender;
import org.netbeans.modules.php.spi.phpmodule.PhpModuleIgnoredFilesExtender;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;

/**
 *
 * @author Radek Ježdík
 */
public class NettePhpFrameworkProvider extends PhpFrameworkProvider {
	
    private static final NettePhpFrameworkProvider INSTANCE = new NettePhpFrameworkProvider();
	
	private static final String ICON_PATH = "org/netbeans/modules/php/nette/resources/badge_icon.png";
    
	BadgeIcon badge;

    public NettePhpFrameworkProvider() {
		super(NbBundle.getMessage(NettePhpFrameworkProvider.class, "OpenIDE-Module-Name"), 
				NbBundle.getMessage(NettePhpFrameworkProvider.class, "OpenIDE-Module-Short-Description"));
		
		badge = new BadgeIcon(
			ImageUtilities.loadImage(ICON_PATH),
			NettePhpFrameworkProvider.class.getResource("/" + ICON_PATH)); // NOI18N
    }
    
    public static NettePhpFrameworkProvider getInstance() {
        return INSTANCE;
    }

    @Override
    public boolean isInPhpModule(PhpModule pm) {
        FilenameFilter ff = new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return dir.isDirectory() && name.equals("Nette");
			}
			
		};
		
		List<FileObject> files = FileUtils.getFilesRecursive(pm.getSourceDirectory(), ff);
		
		for(FileObject fo : files) {
			if(new File(fo.getPath() + "/loader.php").exists())
				return true;
		}
		return false;
    }

    @Override
    public File[] getConfigurationFiles(PhpModule pm) {
        FilenameFilter ff = new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return name.equals("bootstrap.php") || name.endsWith(".ini")
						|| name.endsWith(".neon") || name.equals("index.php");
			}
			
		};
		
		List<FileObject> files = FileUtils.getFilesRecursive(pm.getSourceDirectory(), ff);
		
		File[] confs = new File[files.size()];
		for(int i = 0; i < files.size(); i++) {
			confs[i] = FileUtil.toFile(files.get(i));
		}
		return confs;
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

	@Override
	public BadgeIcon getBadgeIcon() {
		return badge;
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
        return new NetteEditorExtender();
    }
    
}
