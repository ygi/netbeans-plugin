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

package org.netbeans.modules.php.nette.utils;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import javax.swing.text.JTextComponent;
import org.netbeans.modules.parsing.api.Source;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;

/**
 *
 * @author Ondřej Brejla
 */
public final class FileUtils {

	private FileUtils() {
	}

	public static void copyDirectory(File srcDir, File dstDir) {
		if (srcDir.isDirectory()) {
            if (!dstDir.exists()) {
                dstDir.mkdir();
            }

            String[] children = srcDir.list();
            for (int i = 0; i < children.length; i++) {
                copyDirectory(new File(srcDir, children[i]), new File(dstDir, children[i]));
            }
        } else {
            try {
                copyFile(srcDir, dstDir);
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
	}

	public static void copyFile(File src, File dst) throws IOException {
        InputStream fis = new FileInputStream(src);
        OutputStream fos = new FileOutputStream(dst);

        byte[] buf = new byte[1024];
        int len;
        while ((len = fis.read(buf)) > 0) {
            fos.write(buf, 0, len);
        }

        fis.close();
        fos.close();
    }

	public static void copyFile(InputStream is, File dst) throws IOException {
		OutputStream fos = new FileOutputStream(dst);

		byte[] buf = new byte[1024];
		int len;
		while ((len = is.read(buf)) > 0) {
			fos.write(buf, 0, len);
		}

		is.close();
		fos.close();
	}

	public static File getFile(JTextComponent textComp) {
		Source source = Source.create(textComp.getDocument());

		return FileUtil.toFile(source.getFileObject());
	}

    /**
     * Searches for all files recursively (children folders including)
     * @param fp folder which to start search from
     * @param filter filter denoting what files should be returned
     * @return list of files found
     */
    public static List<FileObject> getFilesRecursive(FileObject fp, FilenameFilter filter) {
        List<FileObject> list = new ArrayList<FileObject>();
        
        for(FileObject child : fp.getChildren()) {
            if(child.getName().equals("temp") || child.getName().equals("sessions") ||
                    child.getName().equals("logs"))
                continue;
            File f = FileUtil.toFile(child);
            if(f.isDirectory()) {
                File[] folders = f.listFiles(new FileFilter() {
					@Override
                    public boolean accept(File file) {
                        return file.isDirectory();
                    }
                });
                if(folders != null && folders.length > 0) {
                    for(File folder : folders) {
                        list.addAll(getFilesRecursive(FileUtil.toFileObject(folder), filter));
                    }
                }
                File[] files = f.listFiles(filter);
                for(File file : files) {
                    list.add(FileUtil.toFileObject(file));
                }
            } else {
                if(filter.accept(f.getParentFile(), f.getName()))
                    list.add(FileUtil.toFileObject(f));
            }
        }
        return list;
    }
	
}
