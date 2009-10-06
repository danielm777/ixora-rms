/**
 * 22-Jun-2006
 */
package com.ixora.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import com.ixora.common.logging.AppLogger;
import com.ixora.common.logging.AppLoggerFactory;
import com.ixora.common.utils.Utils;

/**
 * @author Daniel Moraru
 */
public class Product {
	/** Logger */
	private static final AppLogger logger = AppLoggerFactory.getLogger(Product.class);
	private static Product product;

	/** Product version */
	private ComponentVersion fVersion;
	/** Product name */
	private String fName;

	/** Product installation folder */
	private File fInstallationFolder;

	static {
		try {
			product = new Product();
		} catch (IOException e) {
			logger.error(e);
		}
	}

	/**
	 * @throws IOException
	 *
	 */
	private Product(File file) throws IOException {
		fInstallationFolder = file;
		read(file);
	}

	private Product() throws IOException {
		this(new File(Utils.getPath("/")));
	}

	/**
	 * @return
	 */
	public ComponentVersion getVersion() {
		return fVersion;
	}

	/**
	 * @return
	 */
	public String getName() {
		return fName;
	}

	/**
	 * @return the installation folder for the product
	 */
	public File getInstallationFolder() {
		return fInstallationFolder;
	}

	/**
	 * @param productRoot
	 * @return
	 * @throws IOException
	 */
	private void read(File productRoot) throws IOException {
		File file = new File(new File(productRoot,"config"), "product");
		if(!file.exists()) {
			throw new IOException("Version file missing: " + file.getAbsolutePath());
		}
		BufferedReader reader = new BufferedReader(new FileReader(file));
		fName = reader.readLine();
		fVersion = new ComponentVersion(reader.readLine());
	}

	/**
	 * @return the product info
	 */
	public static Product getProductInfo() {
		return product;
	}

	/**
	 * @return the version of the product
	 * @throws IOException
	 */
	public static Product getProductInfo(File productRoot) throws IOException {
		return new Product(productRoot);
	}
}
