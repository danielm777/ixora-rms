/**
 * 26-May-2006
 */
package com.ixora.zip;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import com.ixora.common.utils.Utils;

/**
 * @author Daniel Moraru
 */
public class ZipTest {

	/**
	 * @param args
	 * @throws FileNotFoundException
	 */
	public static void main(String[] args) {
		try {
			String root = "C:\\Documents and Settings\\Daniel\\My Documents\\agent715465092";
			ZipOutputStream zo = new ZipOutputStream(new FileOutputStream("C:\\out.zip"));
			File rootFile = new File(root);
			File[] files = rootFile.listFiles();
			for(File f : files) {
				zip(zo, rootFile, f);
			}
			zo.close();

			String source = "C:\\out.zip";
			File destination = new File("C:\\Documents and Settings\\Daniel\\My Documents\\out");
			ZipInputStream zi = new ZipInputStream(new FileInputStream(source));
			unzip(zi, destination);
			zi.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param zi
	 * @param destination
	 * @throws IOException
	 */
	private static void unzip(ZipInputStream zi, File destination) throws IOException {
		String sdest = destination.getAbsolutePath();
		ZipEntry entry;
		while((entry = zi.getNextEntry()) != null) {
			String name = entry.getName();
			if(name.startsWith("/") || name.startsWith("\\")) {
				name = name.substring(1);
			}
			String dest = sdest + File.separator + name;
			File f = new File(dest);
			if(entry.isDirectory()) {
				f.mkdirs();
			} else {
				f.getParentFile().mkdirs();
				BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(f));
				byte[] buff = new byte[4096];
				int read;
				while((read = zi.read(buff)) > 0) {
					os.write(buff, 0, read);
				}
				os.close();
			}
		}
	}

	private static void zip(ZipOutputStream zo, File root, File file) throws IOException {
		String rootPath = root.getAbsolutePath();
		String path = file.getAbsolutePath();
		path = path.replace('\\', '/');
		if(file.isDirectory()) {
			path = path + "/";
		}
		String name = path.substring(rootPath.length());
		if(Utils.isEmptyString(name)) {
			name = file.getName();
		}
		ZipEntry entry = new ZipEntry(name);
		if(file.isFile()) {
			zo.putNextEntry(entry);
			BufferedInputStream is = new BufferedInputStream(new FileInputStream(file));
			byte[] buff = new byte[4096];
			int read;
			while((read = is.read(buff)) > 0) {
				zo.write(buff, 0, read);
			}
			zo.closeEntry();
		} else if(file.isDirectory()) {
			zo.putNextEntry(entry);
			zo.closeEntry();
			File[] files = file.listFiles();
			if(files.length > 0) {
				for(File f : files) {
					zip(zo, root, f);
				}
			}
		}
	}
}
