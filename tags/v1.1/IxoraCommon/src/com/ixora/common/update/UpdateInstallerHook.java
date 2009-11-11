package com.ixora.common.update;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.ixora.common.utils.Utils;

/*
 * Created on Feb 16, 2004
 */
/**
 * @author Daniel Moraru
 */
public final class UpdateInstallerHook {
	private static final String appRoot = System.getProperty("application.home");

	/**
	 * This class contains some methods usually available
	 * in commons Utils.
	 */
	private static final class Internals {
		/**
		 * Moves the source folder into the destination folder.
		 * @param src
		 * @param dest
		 * @throws IOException
		 */
		public static void moveFolder(File src, File dest) throws IOException {
			if(src == null || !src.isDirectory() || dest == null || !dest.isDirectory()) {
				throw new IllegalArgumentException("src and dest must be non null and folders");
			}
			copyMoveFolderRecursive(src, dest, true);
		}

		/**
		 * Copies/moves the <code>src</code> folder into
		 * the <code>dest</code> folder recursively.
		 * @param src
		 * @param dest
		 * @param move
		 * @throws IOException
		 */
		private static void copyMoveFolderRecursive(File src, File dest, boolean move) throws IOException {
			File[] fs = Utils.listFilesForFolder(src);
			File f;
			for(int i = 0; i < fs.length; i++) {
				f = fs[i];
				if(!f.canRead()) {
					throw new IOException("no read access to " + f.getAbsolutePath());
				}
				if(f.isFile()) {
					copyFile(f, new File(dest, f.getName()));
				} else if(f.isDirectory()) {
					File destFolder = new File(dest, f.getName());
					if(!destFolder.exists()) {
						destFolder.mkdir();
					}
					copyMoveFolderRecursive(f, destFolder, move);
				}
				if(move && f.canWrite()) {
					f.delete();
				}
			}
		}

		/**
		 * Copies the source file into destination file.
		 * @param source Source file
		 * @param destination Destination file
		 */
		public static void copyFile(File source, File destination)
			throws IOException {
			BufferedInputStream in = new BufferedInputStream(
					new FileInputStream(source));
			BufferedOutputStream out = new BufferedOutputStream(
					new FileOutputStream(destination));
			byte[] buffer = new byte[4096];
			int read = -1;

			while((read = in.read(buffer)) != -1) {
				out.write(buffer, 0, read);
			}

			out.flush();
			out.close();
			in.close();
		}

		/**
		 * @return the system temporary folder
		 */
		public static File getSystemTempFolder() {
			return new File(System.getProperty("java.io.tmpdir"));
		}
	}

	/**
	 * Constructor.
	 */
	private UpdateInstallerHook() {
		super();
	}

	/**
	 * Launched the main application class.
	 * @param appMainClass
	 * @param appParams
	 * @throws ClassNotFoundException
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	private static void launch(
			String appMainClass,
			String[] appParams)
		throws ClassNotFoundException,
			SecurityException,
			NoSuchMethodException,
			IllegalArgumentException,
			IllegalAccessException,
			InvocationTargetException {
		Class<?> app = Class.forName(appMainClass);
		Method main = app.getMethod("main", new Class[]{String[].class});
		main.invoke(null, new Object[]{appParams});
	}

	/**
	 * Installs the updates from the local repository.
	 * @throws IOException
	 */
	private static void installUpdates() throws IOException {
		File app = new File(appRoot);
		Internals.moveFolder(
			new File(Internals.getSystemTempFolder(), "update"),
			app);
	}

	/**
	 * param1 application main class
	 * param2-n application parameters
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			if(appRoot == null) {
				throw new RuntimeException("application.home system property must be set");
			}
			String[] appArgs = new String[args.length - 1];
			System.arraycopy(args, 1, appArgs, 0, appArgs.length);
			installUpdates();
			launch(args[0], appArgs);
		} catch(Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
}
