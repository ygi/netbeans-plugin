/*
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

    private static final String NETTE_LIBS_DIR = "/libs/Nette";

    private static final String NETTE_DOCUMENT_ROOT_DIR = "/document_root";

    private static final String NETTE_APP_DIR = "/app";

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
        File folder = new File(d + NETTE_DOCUMENT_ROOT_DIR);
        FileObject doc_root = FileUtil.createFolder(folder);
        set.add(doc_root);
        
        set.add(FileUtil.createFolder(new File(doc_root.getPath() + "/css")));
        set.add(FileUtil.createFolder(new File(doc_root.getPath() + "/js")));
        set.add(FileUtil.createFolder(new File(doc_root.getPath() + "/images")));

        FileUtils.copyFile(getClass().getResourceAsStream("/org/netbeans/modules/php/nette/resources/index.php"), new File(d + NETTE_DOCUMENT_ROOT_DIR + "/index.php"));
        
        FileUtils.copyFile(getClass().getResourceAsStream("/org/netbeans/modules/php/nette/resources/.htaccess-DOCUMENT_ROOT"), new File(d + NETTE_DOCUMENT_ROOT_DIR + "/.htaccess"));
    }

    private void createApp(String d) throws IOException {
        File folder = new File(d + NETTE_APP_DIR);
        FileObject app = FileUtil.createFolder(folder);
        set.add(app);

        //TODO FrontModule, AdminModule, .. presenters, templates, ..
        //make user choose what names
        //set.add(FileUtil.createFolder(new File(doc_root.getPath() + "/FrontModule")));
        
        set.add(FileUtil.createFolder(new File(app.getPath() + "/temp")));
        set.add(FileUtil.createFolder(new File(app.getPath() + "/sessions")));
        set.add(FileUtil.createFolder(new File(app.getPath() + "/logs")));
        
        FileUtils.copyFile(getClass().getResourceAsStream("/org/netbeans/modules/php/nette/resources/bootstrap.php"), new File(d + NETTE_APP_DIR + "/bootstrap.php"));

        FileUtils.copyFile(getClass().getResourceAsStream("/org/netbeans/modules/php/nette/resources/.htaccess-APP"), new File(d + NETTE_APP_DIR + "/.htaccess"));

        FileUtils.copyFile(getClass().getResourceAsStream("/org/netbeans/modules/php/nette/resources/config.ini"), new File(d + NETTE_APP_DIR + "/config.ini"));
    }

    private void createLibs(String d) throws IOException {
        File folder = new File(d + NETTE_LIBS_DIR);
        FileObject app = FileUtil.createFolder(folder);
        set.add(app);
    }

    private void copyNetteFiles(String projectDir) {
        FileUtils.copyDirectory(new File(NetteOptions.getInstance().getNettePath()), new File(projectDir, NETTE_LIBS_DIR));
    }

    private NewNetteProjectPanel getPanel() {
        if (netteProjectPanel == null) {
            netteProjectPanel = new NewNetteProjectPanel();
        }

        return netteProjectPanel;
    }

}
