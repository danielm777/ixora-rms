/*
 * Created on Jun 1, 2004
 */
package com.ixora.common.security.license;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.prefs.Preferences;

import com.ixora.common.ComponentVersion;
import com.ixora.common.Product;
import com.ixora.common.security.SecurityHandlerDefault;
import com.ixora.common.security.license.exception.InvalidLicense;
import com.ixora.common.security.license.exception.LicenseException;
import com.ixora.common.security.license.exception.LicenseExpired;
import com.ixora.common.utils.Utils;

/**
 * @author Daniel Moraru
 */
public final class LicenseMgr {
	private static final String FILE_PATH = "/license";
	/** License */
	private static License license;
	/**
	 * The license provider will be enquired for the text of the license if the current
	 * license does not exists or is invalid/expired.
	 */
	private static LicenseProvider licenseProvider;
	/** Listeners */
	private static List<Listener> listeners;

	static {
		listeners = new LinkedList<Listener>();
		readLicense();
		try {
			checkLicense();
		} catch(Throwable t) {
			license = null;
		}
	}

	public interface Listener {
		/**
		 * Invoked when the license is updated.
		 */
		void licenseUpdated();
	}

	/**
	 * @param listener
	 */
	public static void addListener(Listener listener) {
		if(listener != null && !listeners.contains(listener)) {
			listeners.add(listener);
		}
	}

	/**
	 *
	 */
	private static void fireLicenseUpdated() {
		for(Listener list : listeners) {
			list.licenseUpdated();
		}
	}

	/**
	 * @param listener
	 */
	public static void removeListener(Listener listener) {
		listeners.remove(listener);
	}

	/**
	 * @return the license
	 */
	public static License getLicense() {
		return license;
	}

	/**
	 * Updates the current license.
	 * @param lic
	 */
	public static void updateLicense(String lic) {
		// try to write license
		try {
			writeLicense(lic);
			readLicense();
			fireLicenseUpdated();
		} catch(LicenseException e) {
			throw e;
		} catch(Throwable t) {
			throw new InvalidLicense();
		}
	}

	/**
	 * @param prov
	 */
	public static void installProvider(LicenseProvider prov) {
		licenseProvider = prov;
	}

	/**
	 * Checks the licence file.
	 * @return
	 * @throws InvalidLicense
	 * @throws LicenseExpired
	 */
	public static void checkLicense() {
		try {
			if(license == null) {
				if(licenseProvider != null) {
					askForLicense();
					// try again
					checkLicense();
				} else {
					throw new InvalidLicense();
				}
			}
			if(!new SecurityHandlerDefault().verify(
				license.toString(false).getBytes(License.ENCODING),
				license.getSignature())) {
				if(licenseProvider != null) {
					askForLicense();
					// try again
					checkLicense();
				} else {
					throw new InvalidLicense();
				}
			}
			Date expired = license.getExpiryDate();
			if(expired != null && expired.compareTo(new Date()) < 0) {
				if(licenseProvider != null) {
					askForLicense();
					// try again
					checkLicense();
				}
				throw new LicenseExpired();
			}
			if(new ComponentVersion(license.getVersion()).compareTo(
					Product.getProductInfo().getVersion()) < 0) {
				if(licenseProvider != null) {
					askForLicense();
					// try again
					checkLicense();
				}
				throw new LicenseExpired();
			}
		} catch(LicenseException e) {
			throw e;
		} catch(Throwable t) {
			throw new InvalidLicense();
		}
		return;
	}

	/**
	 * @throws IOException
	 * @throws CertificateException
	 */
	private static void askForLicense() throws IOException, CertificateException {
		String lic = licenseProvider.getLicense();
		if(Utils.isEmptyString(lic)) {
			throw new InvalidLicense();
		}
		writeLicense(lic);
		readLicense();
		if(license == null) {
			throw new InvalidLicense();
		}
	}

	/**
	 *
	 */
	private static void readLicense() {
		try {
			// load license
			license = new License(new FileInputStream(Utils.getPath(FILE_PATH)));
			storeFirstEvaluationLicenseExpiredDate(license, license.getExpiryDate());
		}catch(Throwable e) {
			; // ignore
		}
	}

	/**
	 * @param lic
	 * @throws IOException
	 * @throws CertificateException
	 *
	 */
	private static void writeLicense(String lic) throws IOException, CertificateException {
		// check license before writing it
		License newLicense = new License(lic);
		if(!new SecurityHandlerDefault().verify(
				newLicense.toString(false).getBytes(License.ENCODING),
				newLicense.getSignature())) {
			throw new InvalidLicense();
		}
		Date expired = newLicense.getExpiryDate();
		if(expired != null && expired.compareTo(new Date()) < 0) {
			throw new LicenseExpired();
		}
		// license ok, replace the existing one
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(new File(Utils.getPath(FILE_PATH))));
			writer.write(lic);
		} finally {
			if(writer != null) {
				writer.close();
			}
		}
	}

	/**
	 * @param date
	 */
	private static void storeFirstEvaluationLicenseExpiredDate(License license, Date date) {
		String key = "fle" + license.getVersion();
		if(!license.isEvaluation()) {
			// clean up
			Preferences.systemRoot().remove(key);
			return;
		}
		// write it only if not already there so that we still keep
		// the first expiration date
		if(getFirstEvaluationLicenseExpiredDate(license) == null) {
			Preferences.systemRoot().putLong(key, date.getTime());
		}
	}

	/**
	 * @return
	 */
	private static Date getFirstEvaluationLicenseExpiredDate(License license) {
		if(!license.isEvaluation()) {
			return null;
		}
		long time = Preferences.systemRoot().getLong("fle" + license.getVersion(), 0);
		if(time == 0) {
			return null;
		}
		return new Date(time);
	}

	/**
	 * @return
	 */
	public static Date isFirstEvaluationFinished() {
		if(license == null) {
			return null;
		}
		Date expDate = getFirstEvaluationLicenseExpiredDate(license);
		if(expDate == null) {
			return null;
		}
		if(expDate.after(new Date())) {
			return null;
		}
		return expDate;
	}
}
