/*
 */
package org.netbeans.modules.php.nette;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;
import javax.swing.JComponent;
import javax.swing.event.ChangeListener;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.spi.phpmodule.PhpModuleExtender;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import org.openide.util.HelpCtx;

/**
 *
 * @author Radek Ježdík
 */
public class NettePhpModuleExtender extends PhpModuleExtender {

    @Override
    public void addChangeListener(ChangeListener cl) {
    }

    @Override
    public void removeChangeListener(ChangeListener cl) {
    }

    @Override
    public JComponent getComponent() {
        return null;
    }

    @Override
    public HelpCtx getHelp() {
        return null;
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public String getErrorMessage() {
        return null;
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

            return set;
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
            throw new ExtendingException("Something's wrong");
        }
    }

    public void createDocumentRoot(String d) throws IOException {
        File folder = new File(d + "/document_root");
        FileObject doc_root = FileUtil.createFolder(folder);
        set.add(doc_root);
        
        set.add(FileUtil.createFolder(new File(doc_root.getPath() + "/css")));
        set.add(FileUtil.createFolder(new File(doc_root.getPath() + "/js")));
        set.add(FileUtil.createFolder(new File(doc_root.getPath() + "/images")));

        File f = new File(folder, "index.php");
        f.createNewFile();
        FileObject fo = FileUtil.toFileObject(f);
        if (fo != null) {
            FileLock lock = fo.lock();
            PrintWriter w = new PrintWriter(fo.getOutputStream(lock));
            w.println("<?php");
            w.println("");
            w.println("define('WWW_DIR', dirname(__FILE__));");
            w.println("");
            w.println("define('APP_DIR', WWW_DIR . '/../app');");
            w.println("");
            w.println("define('LIBS_DIR', WWW_DIR . '/../libs');");
            w.println("");
            w.println("require APP_DIR . '/bootstrap.php';");
            w.println("");
            w.close();
            lock.releaseLock();
            //set.add(fo);
        }
        
        f = new File(folder, ".htaccess");
        f.createNewFile();

        fo = FileUtil.toFileObject(f);
        if (fo != null) {
            FileLock lock = fo.lock();
            PrintWriter w = new PrintWriter(fo.getOutputStream(lock));
            w.println("# disable directory listing");
            w.println("Options -Indexes");
            w.println("");
            w.println("# mod_rewrite");
            w.println("<IfModule mod_rewrite.c>");
                w.println("\tRewriteEngine On");
                w.println("\tRewriteBase /");
                w.println("\t");
                w.println("\t# front controller");
                w.println("\tRewriteCond %{REQUEST_FILENAME} !-f");
                w.println("\tRewriteCond %{REQUEST_FILENAME} !-d");
                w.println("\tRewriteRule !\\.(pdf|js|ico|gif|jpg|png|css|rar|zip|tar\\.gz)$ index.php [L]");
            w.println("</IfModule>");
            w.println("");
            w.close();
            lock.releaseLock();
            //set.add(fo);
        }
    }


    public void createApp(String d) throws IOException {
        File folder = new File(d + "/app");
        FileObject app = FileUtil.createFolder(folder);
        set.add(app);

        //TODO FrontModule, AdminModule, .. presenters, templates, ..
        //make user choose what names
        //set.add(FileUtil.createFolder(new File(doc_root.getPath() + "/FrontModule")));
        
        set.add(FileUtil.createFolder(new File(app.getPath() + "/temp")));
        set.add(FileUtil.createFolder(new File(app.getPath() + "/sessions")));
        set.add(FileUtil.createFolder(new File(app.getPath() + "/logs")));
        
        File f = new File(folder, "bootstrap.php");
        f.createNewFile();
        FileObject fo = FileUtil.toFileObject(f);
        if (fo != null) {
            FileLock lock = fo.lock();
            PrintWriter w = new PrintWriter(fo.getOutputStream(lock));
            w.println("<?php");
            w.println("");
            w.println("require_once LIBS_DIR . '/Nette/loader.php';");
            w.println("");
            w.println("Debug::enable();");
            w.println("");
            w.println("Environment::loadConfig();");
            w.println("");
            w.println("$session = Environment::getSession();");
            w.println("$session->setSavePath(APP_DIR . '/sessions/');");
            w.println("");
            w.println("$application = Environment::getApplication();");
            w.println("//$application->errorPresenter = 'Error';");
            w.println("//$application->catchExceptions = TRUE;");
            w.println("");
            w.println("$router = $application->getRouter();");
            w.println("");
            w.println("$router[] = new Route('index.php', array(");
                w.println("\t'presenter' => 'Homepage',");
                w.println("\t'action' => 'default',");
            w.println("), Route::ONE_WAY);");
            w.println("");
            w.println("$router[] = new Route('<presenter>/<action>/<id>', array(");
                w.println("\t'presenter' => 'Homepage',");
                w.println("\t'action' => 'default',");
                w.println("\t'id' => NULL,");
            w.println("));");

            w.println("");
            w.println("$application->run();");
            w.close();
            lock.releaseLock();
            //set.add(fo);
        }

        f = new File(folder, ".htaccess");
        f.createNewFile();
        fo = FileUtil.toFileObject(f);
        if (fo != null) {
            FileLock lock = fo.lock();
            PrintWriter w = new PrintWriter(fo.getOutputStream(lock));
            w.println("Order Allow,Deny");
            w.println("Deny from all");
            w.close();
            lock.releaseLock();
            //set.add(fo);
        }

        f = new File(folder, "config.ini");
        f.createNewFile();
        fo = FileUtil.toFileObject(f);
        if (fo != null) {
            FileLock lock = fo.lock();
            PrintWriter w = new PrintWriter(fo.getOutputStream(lock));
            w.println("[common]");
            w.println("; PHP configuration");
            w.println("php.date.timezone = \"Europe/Prague\"");
            w.println("php.iconv.internal_encoding = \"%encoding%\"");
            w.println("php.mbstring.internal_encoding = \"%encoding%\"");
            w.println("");
            w.println("; services");
            w.println(";service.Nette-Security-IAuthenticator = Model\\Users");
            w.println("");
            w.println("service.Nette-Loaders-RobotLoader.option.directory[] = %appDir%");
            w.println("service.Nette-Loaders-RobotLoader.option.directory[] = %libsDir%");
            w.println("service.Nette-Loaders-RobotLoader.run = TRUE");
            w.println("");
            w.println("[production < common]");
            w.println("");
            w.println("");
            w.println("[development < production]");
            w.close();
            lock.releaseLock();
            set.add(fo);
        }
    }
}
