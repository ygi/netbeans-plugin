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
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import javax.swing.JComponent;
import javax.swing.event.ChangeListener;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.nette.options.NetteOptions;
import org.netbeans.modules.php.nette.utils.FileUtils;
import org.netbeans.modules.php.nette.wizards.NewNetteProjectPanel;
import org.netbeans.modules.php.spi.phpmodule.PhpModuleExtender;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import org.openide.util.HelpCtx;

/**
 *
 * @author Radek Ježdík, Ondřej Brejla
 */
public class NettePhpModuleExtender extends PhpModuleExtender {

    private NewNetteProjectPanel netteProjectPanel;

    @Override
    public void addChangeListener(ChangeListener cl) {
        getPanel().addChangeListener(cl);
    }

    @Override
    public void removeChangeListener(ChangeListener cl) {
        getPanel().removeChangeListener(cl);
    }

    @Override
    public JComponent getComponent() {
        return getPanel();
    }

    @Override
    public HelpCtx getHelp() {
        return null;
    }

    @Override
    public boolean isValid() {
        return getErrorMessage() == null;
    }

    @Override
    public String getErrorMessage() {
        return getPanel().getErrorMessage();
    }

    @Override
    public String getWarningMessage() {
        return null;
    }
    
    private HashSet<FileObject> set = new HashSet<FileObject>();

    @Override
    public Set<FileObject> extend(PhpModule pm) throws ExtendingException {
        try {
            String projectDir = pm.getSourceDirectory().getPath();

            createDocumentRoot(projectDir);
            createApp(projectDir);
            createLibs(projectDir);
            createTemp(projectDir);
            createLog(projectDir);

            if (getPanel().isCopyNetteCheckboxSelected()) {
                copyNetteFiles(projectDir);
            }

            return set;
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
            throw new ExtendingException("Something's wrong");
        }
    }

    private void createDocumentRoot(String d) throws IOException {
        File folder = new File(d + NetteFramework.NETTE_DOCUMENT_ROOT_DIR);
        FileObject doc_root = FileUtil.createFolder(folder);
        set.add(doc_root);
        
        set.add(FileUtil.createFolder(new File(doc_root.getPath() + "/css")));
        set.add(FileUtil.createFolder(new File(doc_root.getPath() + "/js")));
        set.add(FileUtil.createFolder(new File(doc_root.getPath() + "/images")));

        FileUtils.copyFile(getClass().getResourceAsStream("/org/netbeans/modules/php/nette/resources/index.php"), new File(d + NetteFramework.NETTE_DOCUMENT_ROOT_DIR + "/index.php"));
        
        FileUtils.copyFile(getClass().getResourceAsStream("/org/netbeans/modules/php/nette/resources/.htaccess-DOCUMENT_ROOT"), new File(d + NetteFramework.NETTE_DOCUMENT_ROOT_DIR + "/.htaccess"));
    }

    private void createApp(String d) throws IOException {
        File folder = new File(d + NetteFramework.NETTE_APP_DIR);
        FileObject app = FileUtil.createFolder(folder);
        set.add(app);

        //TODO FrontModule, AdminModule, .. presenters, templates, ..
        //make user choose what names
        //set.add(FileUtil.createFolder(new File(doc_root.getPath() + "/FrontModule")));
        
        //set.add(FileUtil.createFolder(new File(app.getPath() + "/temp")));
        //set.add(FileUtil.createFolder(new File(app.getPath() + "/sessions")));
        //set.add(FileUtil.createFolder(new File(app.getPath() + "/logs")));
        
        set.add(FileUtil.createFolder(new File(app.getPath() + "/presenters")));
        set.add(FileUtil.createFolder(new File(app.getPath() + "/templates")));
        
        FileUtils.copyFile(getClass().getResourceAsStream("/org/netbeans/modules/php/nette/resources/bootstrap.php"), new File(d + NetteFramework.NETTE_APP_DIR + "/bootstrap.php"));

        FileUtils.copyFile(getClass().getResourceAsStream("/org/netbeans/modules/php/nette/resources/.htaccess-APP"), new File(d + NetteFramework.NETTE_APP_DIR + "/.htaccess"));

        FileUtils.copyFile(getClass().getResourceAsStream("/org/netbeans/modules/php/nette/resources/config.ini"), new File(d + NetteFramework.NETTE_APP_DIR + "/config.ini"));
    }

    private void createLibs(String d) throws IOException {
        File folder = new File(d + NetteFramework.NETTE_LIBS_DIR);
        FileObject app = FileUtil.createFolder(folder);
        set.add(app);
    }

    private void createTemp(String d) throws IOException {
        File folder = new File(d + NetteFramework.NETTE_TEMP_DIR);
        FileObject app = FileUtil.createFolder(folder);
        set.add(app);
    }

    private void createLog(String d) throws IOException {
        File folder = new File(d + NetteFramework.NETTE_LOG_DIR);
        FileObject app = FileUtil.createFolder(folder);
        set.add(app);
    }

    private void copyNetteFiles(String projectDir) {
        FileUtils.copyDirectory(new File(NetteOptions.getInstance().getNettePath()), new File(projectDir, NetteFramework.NETTE_LIBS_DIR));
    }

    private NewNetteProjectPanel getPanel() {
        if (netteProjectPanel == null) {
            netteProjectPanel = new NewNetteProjectPanel();
        }

        return netteProjectPanel;
    }

}
