package com.ixora.common.utils;

import java.awt.Color;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import com.ixora.common.MessageRepository;
import com.ixora.common.Stopper;
import com.ixora.common.exception.AppException;
import com.ixora.common.exception.AppRuntimeException;
import com.ixora.common.os.OSUtils;
import com.ixora.common.process.LocalProcessWrapper;

/**
 * Utils class.
 * @author: Daniel Moraru
 */
// Note that this class MUST NOT use a logger
public class Utils {
    private static String strAbsoluteRootPath;
    private static Random rand;
    private static String newLine;

    static {
		strAbsoluteRootPath = System.getProperty("application.home");
		if(strAbsoluteRootPath == null) {
			System.out.print("The system property 'application.home' is not set, default to local dir.");
			strAbsoluteRootPath = "";
		}
		else {
			strAbsoluteRootPath = strAbsoluteRootPath.replace('\\', '/');
			if(!strAbsoluteRootPath.endsWith("/")) {
				strAbsoluteRootPath = strAbsoluteRootPath.concat("/");
			}
		}
		rand = new Random();
		newLine = System.getProperty("line.separator");
    }

    /**
     * Utils constructor comment.
     */
    private Utils() {
        super();
    }

	/**
	 * @param e
	 * @return the message of the first application exception or if no application exception
	 * is found the message of the passed in exception
	 */
	public static String getApplicationErrorMessage(Throwable e) {
		int i = 0; // to avoid infinite recursion
		Throwable cause = e;
		do {
			if(cause instanceof AppException) {
				return e.getLocalizedMessage();
			}
			cause = e.getCause();
			++i;
		} while(cause != null && i < 30);
		return e.getLocalizedMessage();
	}

    /**
     * @return the absolute path to the given file or folder
     */
    public static String getPath(String path) {
    	path = path.replace('\\', '/');
        if(path.charAt(0) == '/') {
            path = path.substring(1);
        }
        return strAbsoluteRootPath.concat(path);
    }

    /**
     * Returns a random positive integer. It is guarranteed to be unique
     * for this program run.
     * @return int
     */
    public static int getRandomInt() {
        return Math.abs(rand.nextInt());
    }

    /**
     * Returns a random integer i with the property that <code>left <= i <= right</code>
     * @param left
     * @param right
     * @return int
     * @throws IllegalArgumentException if right < left
     */
    public static int getRandomInt(int left, int right) {
        if((right - left) < 0) {
            throw new IllegalArgumentException();
        }

        return left + (int)Math.abs((right - left) * Math.random());
    }

    /**
     * Performs a deep cloning of <b>obj</b> using serialization.
     * @param obj Serializable object to clone.
     * @throws java.io.IOException
     * @throws java.lang.ClassNotFoundException
     */
    public static Object deepClone(Serializable obj) throws IOException,
                                                            ClassNotFoundException {
        ByteArrayOutputStream buff = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(buff);
        oos.writeObject(obj);

        ObjectInputStream ois = new ObjectInputStream(
                                        new ByteArrayInputStream(
                                                buff.toByteArray()));

        return ois.readObject();
    }

    /**
     * Packs a serializable object into a byte buffer.
     * @param obj
      * @throws java.io.IOException
     */
    public static byte[] packObject(Serializable obj) throws IOException {
        ByteArrayOutputStream buff = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(buff);
        oos.writeObject(obj);

        return buff.toByteArray();
    }

    /**
     * Packs a serializable object into a byte buffer.
     * @param obj
      * @throws java.io.IOException
      * @throws java.lang.ClassNotFoundException
     */
    public static Serializable unpackObject(byte[] buff) throws IOException,
                                                          ClassNotFoundException {
        ObjectInputStream ois = new ObjectInputStream(
                                        new ByteArrayInputStream(buff));

        return (Serializable)ois.readObject();
    }

    /**
    * Copies the source file into destination file.
    * @param source Source file
    * @param destination Destination file
    */
    public static void copyFile(File source, File destination)
                         throws IOException {
    	destination.getParentFile().mkdirs();
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
     * Zips the content of the given folder and stores the zipped file.
     * @param source Source file
     * @param destination Destination file
     * @throws IOException
     */
    public static void zipFolder(File source, File destination) throws IOException {
    	// check this to avoid infinite looping
    	String ssource = source.getAbsolutePath();
    	String sdest = destination.getAbsolutePath();
    	if(sdest.contains(ssource)) {
    		throw new AppRuntimeException("The destination file cannot be a child of the source folder");
    	}
		ZipOutputStream zo = new ZipOutputStream(new FileOutputStream(destination));
		if(source.isFile()) {
			zip(zo, source, source);
		} else {
			File[] files = source.listFiles();
			for(File f : files) {
				zip(zo, source, f);
			}
		}
		zo.close();
    }

    /**
     * Unzips the content of the given zipped file and stores the content
     * in the destination folder.
     * @param source Source file
     * @param destination Destination file
     * @throws IOException
     */
    public static void unzipToFolder(File source, File destination) throws IOException {
    	if(!source.isFile()) {
    		throw new AppRuntimeException("Source must be a file");
    	}
		ZipInputStream zi = new ZipInputStream(new FileInputStream(source));
		unzip(zi, destination);
		zi.close();
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

	/**
	 * Reads the content from <code>is</code> stream, zipps it and places it
	 * in the returned buffer.
	 * @return The buffer containing the zipped data.
	 * @param is
	 * @throws IOException
	 */
	public static byte[] zipToBuffer(InputStream is) throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        ZipOutputStream zips = new ZipOutputStream(bytes);
        zips.putNextEntry(new ZipEntry("main"));
        byte[] tmp = new byte[4096];
        int nread;
        while((nread = is.read(tmp)) > 0) {
            zips.write(tmp, 0, nread);
        }
        zips.closeEntry();
        zips.close();
        return bytes.toByteArray();
	}

	/**
	 * Writes the content from the zipped buffer <code>buff</code>
	 * to the <code>os</code> output stream.
	 * @param buff
	 * @param os
	 * @throws IOException
	 */
	public static void unzipToStream(byte[] buff, OutputStream os) throws IOException {
        ZipInputStream zips = new ZipInputStream(
                                      new ByteArrayInputStream(buff));
        zips.getNextEntry();
        byte[] readBuffer = new byte[4096];
        int nread;
        while((nread = zips.read(readBuffer)) > 0) {
            os.write(readBuffer, 0, nread);
        }
        zips.closeEntry();
        zips.close();
	}

    /**
     * Reads a line from the given input stream.
     * @param is The input stream
     * @return The read line.
     */
    public static String readLine(InputStream is) throws IOException {
        StringBuffer buff = new StringBuffer();
        int b = 0;

        while((b = is.read()) != '\n') {
            if(b != -1) {
                buff.append((char)b);
            } else {
                break;
            }
        }

        int len = buff.length();

        if((len > 0) && (buff.charAt(len - 1) == '\r')) {
            return buff.substring(0, len - 1);
        } else {
            return buff.toString();
        }
    }

    /**
     * Gets the stack trace of an exception as a string
     * @param e
     * @return StringBuff
     */
    public static StringBuffer getTrace(Throwable e) {
        StringWriter swriter = new StringWriter(200);
        PrintWriter pwriter = new PrintWriter(swriter);
        e.printStackTrace(pwriter);
        pwriter.flush();

        return swriter.getBuffer();
    }

    /**
     * Writes an object to the given file.
     * @param file A file path relative from the application
     * installation point.
     * @param object
     * @throws IOException
     */
    public static void writeObjectToFile(String file, Object object) throws IOException {
       ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(
					new FileOutputStream(file));
            oos.writeObject(object);
        } catch(IOException e) {
            throw e;
        } finally {
            try {
                if(oos != null) {
                    oos.close();
                }
            } catch(IOException e) {
            }
        }
    }

    /**
     * @return An object read from the given file.
     * @param file A file path relative from the application
     * installation point.
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static Object readObjectFromFile(String file) throws ClassNotFoundException, IOException {
       ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(
					new FileInputStream(file));
            return ois.readObject();
        } catch(IOException e) {
            throw e;
        } finally {
            try {
                if(ois != null) {
                    ois.close();
                }
            } catch(IOException e) {
            }
        }
    }

    /**
	 * @return the extension of a file
	 */
	public static String getFileExtension(File f) {
		String ext = null;
		String s = f.getName();
		int i = s.lastIndexOf('.');
		if (i > 0 &&  i < s.length() - 1) {
			ext = s.substring(i+1).toLowerCase();
		}
		return ext;
	}

	/**
	 * @return the name part of a file name (without the extension)
	 */
	public static String getFileName(File f) {
		String name = f.getName();
		int i = name.lastIndexOf('.');
		if (i > 0 &&  i < name.length() - 1) {
			name = name.substring(0, i).toLowerCase();
		} else {
			// no extension
			return name;
		}
		return name;
	}

	/**
	 * Shrinks a long file path by selecting a first and last part
	 * and introducing three dots in between.
	 * @param path
	 * @return
	 */
	public static String shrinkFilePath(String path) {
		return path.substring(0, 10) + "..." + path.substring(path.length() - 10);
	}

	/**
	 * Copies the source folder into the destination folder.
	 * @param src
	 * @param dest
	 * @throws IOException
	 */
	public static void copyFolder(File src, File dest) throws IOException {
		if(src == null || !src.isDirectory() || dest == null || !dest.isDirectory()) {
			throw new IllegalArgumentException("src and dest must be non null and folders");
		}
		copyMoveFolderRecursive(src, dest, false);
	}

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
		File[] fs = src.listFiles();
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
	 * @return the system temporary folder
	 */
	public static File getSystemTempFolder() {
		return new File(System.getProperty("java.io.tmpdir"));
	}

	/**
	 * Deletes all the content (folders and files) of the given
	 * folder.
	 * @param folder
	 * @throws IOException
	 */
	public static void deleteFolderContent(File folder) throws IOException {
		File[] fs = folder.listFiles();
		if(Utils.isEmptyArray(fs)) {
			return;
		}
		for(int i = 0; i < fs.length; i++) {
			File f = fs[i];
			if(!f.canWrite()) {
				throw new IOException("No write access to " + f.getAbsolutePath());
			}
			if(f.isFile()) {
				f.delete();
			} else if(f.isDirectory()) {
				deleteFolderContent(f);
				f.delete();
			}
		}
	}

	/**
	 * Transfers the content from the <code>is</code> input stream
	 * into the <code>os</code> output stream.
	 * @param is
	 * @param os
	 * @param stopper
	 * @throws IOException
	 */
	public static void transferContent(InputStream is, OutputStream os, Stopper stopper) throws IOException {
		byte[] buff = new byte[4096];
		int count;
		while((count = is.read(buff)) > 0) {
			if(stopper != null) {
				if(stopper.stop()) {
					break;
				}
			}
			os.write(buff, 0, count);
		}
		os.flush();
	}

	/**
	 * Launches the external browser (iexplorer on windows and
	 * netscape on everything else)
	 * @param url
	 * @throws StartableError
	 */
	public static void launchBrowser(URL url) throws Throwable {
		String cmd = (OSUtils.isOs(OSUtils.WINDOWS)
			? "cmd.exe /c start " + url.toString()
			: "netscape -remote 'openURL(" + url.toString() + ")'");
		new LocalProcessWrapper(cmd, null).start();
	}

    /**
     * @param start
     * @param obj
     * @return
     */
    // NEEDS IMPROVEMENT
    public static int hashCode(int start, Object obj) {
        if(obj != null) {
            return start ^ obj.hashCode();
        }
        return start;
    }

   /**
     * @param start
     * @param obj
     * @return
     */
    public static int hashCode(int start, Object[] obj) {
        if(obj != null) {
            return start ^ Arrays.hashCode(obj);
        }
        return start;
    }

	/**
	 * Returns true if the given objects are equals.
	 * @param obj1
	 * @param obj2
	 * @return true if the given object are equals.
	 */
	public static boolean equals(Object obj1, Object obj2) {
		if(obj1 == obj2
				|| (obj1 != null && obj1.equals(obj2))) {
			return true;
		}
		return false;
	}

	/**
	 * Returns true if the given arrays are equals.
	 * @param obj1
	 * @param obj2
	 * @return true if the given arays are equals.
	 */
	public static boolean equals(Object[] obj1, Object[] obj2) {
		if(obj1 == obj2
				|| (obj1 != null && Arrays.equals(obj1, obj2))) {
			return true;
		}
		return false;
	}

	/**
     * @return the new line characters.
     */
    public static String getNewLine() {
        return newLine;
    }

	/**
     * @return the new line characters.
     */
    public static String getHTMLNewLine() {
        return "</br>";
    }

	/**
	 * @param component
	 * @param msg
	 * @return the translated message
	 */
	public static String getTranslatedMessage(String component, String msg) {
    	if(component == null || component.length() == 0) {
    	    return MessageRepository.get(msg);
    	} else {
    	    return MessageRepository.get(component, msg);
    	}
	}

    /**
     * Returns all fields (public, private, protected, package)
     * declared by the specified class and all its superclasses.
     * @param clazz Class to search for fields
     * @return Array of fields
     */
    public static Field[] getAllFields(Class<?> clazz) {
        LinkedList<Field> fields = new LinkedList<Field>();

        while (clazz != null) {
            // Add all fields of this class
            Field[] locals = clazz.getDeclaredFields();
            for (int i=0; i<locals.length; i++) {
                fields.add(i, locals[i]);
            }
            // Move to superclass
            clazz = clazz.getSuperclass();
        }

        return fields.toArray(new Field[fields.size()]);
    }

    /**
     * Convert a byte to an unsigned byte, represented on a short int.
     * @param b byte to be made unsigned
     */
    public static short ubyte(byte b) {
    	if (b < 0) {
    		return (short) (b + 256);
    	} else {
    		return b;
    	}
    }

    /**
     * @param s
     * @return true if s is null or is made up of white spaces only
     */
    public static boolean isEmptyString(String s) {
    	return (s == null || s.trim().length() == 0) ? true : false;
    }

    /**
     * @param o
     * @return true if o is null or is empty
     */
    public static boolean isEmptyArray(Object[] o) {
    	return (o == null || o.length == 0) ? true : false;
    }

    /**
     * @param c
     * @return true if c is null or is empty
     */
    public static boolean isEmptyCollection(Collection<?> c) {
    	return (c == null || c.size() == 0) ? true : false;
    }

    /**
     * @param m
     * @return true if m is null or is empty
     */
    public static boolean isEmptyMap(Map<?, ?> m) {
    	return (m == null || m.size() == 0) ? true : false;
    }

    /**
     * @param file
     * @return
     * @throws IOException
     */
    public static StringBuffer readFileContent(File file) throws IOException {
    	BufferedReader reader = new BufferedReader(new FileReader(file));
    	StringBuffer buff = new StringBuffer();
    	String line;
    	while((line = reader.readLine()) != null) {
    		buff.append(line);
    		buff.append(getNewLine());
    	}
    	return buff;
    }

    /**
     * @param file
     * @return
     * @throws IOException
     */
    public static byte[] readFileContentAsBytes(File file) throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        FileInputStream is = new FileInputStream(file);
        try {
        	Utils.transferContent(is, bytes, null);
        } finally {
        	is.close();
        }
        return bytes.toByteArray();

    }

    /**
     * Returns the boolean value of the string passed. Replaces the
     * JRE 1.5 method.
     * @param s
     * @return
     */
    public static boolean parseBoolean(String s) {
		return Boolean.valueOf(s).booleanValue();
    }

    /**
     * Strings used when replacing regexp matches have to be escaped
     * so that '$' and '\' don't have a special meaning
     * @param s
     * @return
     */
    public static String quoteReplacement(String s) {
        if ((s.indexOf('\\') == -1) && (s.indexOf('$') == -1))
            return s;
        StringBuffer sb = new StringBuffer();
        for (int i=0; i<s.length(); i++) {
            char c = s.charAt(i);
            if (c == '\\') {
                sb.append('\\'); sb.append('\\');
            } else if (c == '$') {
                sb.append('\\'); sb.append('$');
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * Searches for 'target' in the input string and replaces it with 'replacement'.
     * Unlike String.replaceAll it does not take regular expressions. This function
     * emulates String.replace in JRE 1.5
     * @param input
     * @param target
     * @param replacement
     * @return
     */
    public static String replace(String input, String target, String replacement) {
        StringBuffer    sbOut = new StringBuffer(input);
        int index = 0;
        int lenTarget = target.length();
        int lenReplacement = replacement.length();

        do {
            index = sbOut.indexOf(target, index);
            if (index != -1) {
                sbOut = sbOut.replace(index, index + lenTarget, replacement);
                index += lenReplacement;
            }
        } while (index != -1 && index < sbOut.length());
        return sbOut.toString();
    }

    /**
     * Returns a hash code for a whole array of elements
     * @param a
     * @return
     */
    public static int hashCode(Object a[]) {
        if (a == null)
            return 0;

        int result = 1;

        for (Object element : a)
            result = 31 * result + (element == null ? 0 : element.hashCode());

        return result;
    }

	/**
	 * Checks whether the specified host refers to localhost
	 * @param host name or address to check
	 * @return true if this is the local host
	 */
	public static boolean isLocalHost(String host) {
		try {
			// Try some well known names first
			if (host.equalsIgnoreCase("localhost") || host.equals("127.0.0.1"))
				return true;

			String	remoteHost = InetAddress.getByName(host).getHostAddress();
			String	localHost = InetAddress.getLocalHost().getHostAddress();
			if (localHost != null && localHost.equalsIgnoreCase(remoteHost))
				return true;
		} catch (UnknownHostException e) {
			// ignore
		}
		return false;
	}

	/**
	 * @param locations
	 * @param selLocation
	 */
	public static boolean arrayContains(Object[] arr, Object obj) {
		if(arr == null) {
			return false;
		}
		for(Object o : arr) {
			if(o != null) {
				if(o.equals(obj)) {
					return true;
				}
			} else {
				return obj == null;
			}
		}
		return false;
	}

	/**
	 * @param zo
	 * @param root
	 * @param file
	 * @throws IOException
	 */
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

	/**
	 * @param color
	 * @return
	 */
	public static String getWebColor(Color color) {
		String sret = Integer.toHexString(color.getRed());
		String sgreen = Integer.toHexString(color.getGreen());
		String sblue = Integer.toHexString(color.getBlue());
		return sret + sgreen + sblue;
	}

	/**
	 * @param caller
	 * @return A class loader suitable for loading classes from the <code>caller</code>.
	 */
	public static ClassLoader getClassLoader(Class<?> caller) {
		ClassLoader ret = Thread.currentThread().getContextClassLoader();
		if(ret == null) {
			ret = caller.getClassLoader();
		}
		return ret;
	}
}
