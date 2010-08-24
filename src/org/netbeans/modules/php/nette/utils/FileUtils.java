
package org.netbeans.modules.php.nette.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.openide.util.Exceptions;

/**
 *
 * @author Ondřej Brejla
 */
public class FileUtils {

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

}
