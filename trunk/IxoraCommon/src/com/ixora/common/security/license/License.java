/*
 * Created on Jun 1, 2004
 */
package com.ixora.common.security.license;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import com.ixora.common.security.license.exception.InvalidLicense;
import com.ixora.common.utils.HexConverter;
import com.ixora.common.utils.Utils;

/**
 * @author Daniel Moraru
 */
public class License {
	public static final String ENCODING = "UTF-8";
	public static final String ENTRY_VERSION = "version";
	public static final String ENTRY_ISSUE_DATE = "issued";
	public static final String ENTRY_EXPIRY_DATE = "expires";
	public static final String ENTRY_TYPE = "type";
	public static final String ENTRY_KEY = "key";
	public static final String ENTRY_MACHINE = "machine";
	public static final String ENTRY_UNITS = "units";
	public static final String ENTRY_SIGNATURE = "signature";
	public static final String ENTRY_MODULES = "modules";

	public static final int IDX_ENTRY_ISSUE_DATE = 0;
	public static final int IDX_ENTRY_EXPIRY_DATE = 1;
	public static final int IDX_ENTRY_TYPE = 2;
	public static final int IDX_ENTRY_KEY = 3;
	public static final int IDX_ENTRY_MACHINE = 4;
	public static final int IDX_ENTRY_UNITS = 5;
	public static final int IDX_ENTRY_SIGNATURE = 6;
	public static final int IDX_ENTRY_MODULES = 7;
	public static final int IDX_ENTRY_VERSION = 8;

	protected static final String DELIM = ":";
	protected static final String MODULES_DELIM = ",";

	protected Date fIssueDate;
	protected Date fExpiryDate;
	protected String fType;
	protected byte[] fSignature;
	protected String fMachine;
	protected String fKey;
	protected int fUnits;
	protected List<String> fModules;
	protected String fVersion;

	protected static final DateFormat formatter =
		new SimpleDateFormat("yyMMddHHmmss", Locale.US);

	/**
	 * Constructor.
	 */
	protected License() {
	}

	/**
	 * Constructor.
	 * @param is
	 * @throws IOException
	 * @throws InvalidLicense
	 */
	protected License(InputStream is) throws IOException, InvalidLicense {
		this(new BufferedReader(new InputStreamReader(is)).readLine());
	}

	/**
	 * @param lic
	 */
	License(String lic) {
		try {
			String line = lic;
			if(Utils.isEmptyString(line)) {
				throw new InvalidLicense();
			}
			String[] tok = line.split(DELIM);
			if(tok != null && tok.length == 9) {
				String e = tok[IDX_ENTRY_ISSUE_DATE].trim();
				fIssueDate = formatter.parse(e);
				e = tok[IDX_ENTRY_EXPIRY_DATE].trim();
                if(!Utils.isEmptyString(e)) {
                    fExpiryDate = formatter.parse(e);
                }
                e = tok[IDX_ENTRY_UNITS].trim();
				fUnits = Integer.parseInt(e);
			    e = tok[IDX_ENTRY_KEY].trim();
				if(Utils.isEmptyString(e)) {
					throw new InvalidLicense();
				}
				fKey = e;
			    e = tok[IDX_ENTRY_TYPE].trim();
				if(Utils.isEmptyString(e)) {
					throw new InvalidLicense();
				}
				fType = e;
			    e = tok[IDX_ENTRY_MACHINE].trim();
				if(e.length() == 0) {
					fMachine = null;
				}
			    e = tok[IDX_ENTRY_SIGNATURE].trim();
				if(Utils.isEmptyString(e)) {
					throw new InvalidLicense();
				}
				fSignature = HexConverter.decode(e);
			    e = tok[IDX_ENTRY_MODULES].trim();
				if(Utils.isEmptyString(e)) {
					throw new InvalidLicense();
				}
				fModules = new ArrayList<String>(Arrays.asList(e.split(MODULES_DELIM)));
			    e = tok[IDX_ENTRY_VERSION].trim();
				if(Utils.isEmptyString(e)) {
					throw new InvalidLicense();
				}
				fVersion = e;
			} else {
				throw new InvalidLicense();
			}
		} catch(ParseException e) {
			throw new InvalidLicense();
		} catch(NumberFormatException e) {
			throw new InvalidLicense();
		} catch(Exception e) {
			throw new InvalidLicense();
		}
	}

    /**
     * @return
     * @throws UnsupportedEncodingException
     */
    public String toHexString() throws UnsupportedEncodingException {
       return HexConverter.encode(toString(true).getBytes("UTF-8"));
    }

	/**
	 * @see java.lang.Object#toString()
	 * @param signature
	 */
	public String toString(boolean signature) {
		StringBuffer buff = new StringBuffer();
		buff.append(formatter.format(fIssueDate));
		buff.append(DELIM);
        if(fExpiryDate != null) {
            buff.append(formatter.format(fExpiryDate));
        }
		buff.append(DELIM);
		buff.append(String.valueOf(fType));
		buff.append(DELIM);
		buff.append(fKey);
		buff.append(DELIM);
		if(fMachine != null) {
			buff.append(fMachine);
		}
		buff.append(DELIM);
		buff.append(String.valueOf(fUnits));
		buff.append(DELIM);
		if(signature) {
			buff.append(HexConverter.encode(fSignature));
			buff.append(DELIM);
		}
		for(Iterator<String> iter = fModules.iterator(); iter.hasNext();) {
			buff.append(iter.next());
			if(iter.hasNext()) {
				buff.append(MODULES_DELIM);
			}
		}
		buff.append(DELIM);
		buff.append(fVersion);
		return buff.toString();
	}

	/**
	 * @return the fIssueDate.
	 */
	public Date getIssueDate() {
		return fIssueDate;
	}
	/**
	 * @return the fExpiryDate.
	 */
	public Date getExpiryDate() {
		return fExpiryDate;
	}
	/**
	 * @return the fKey.
	 */
	public String getKey() {
		return fKey;
	}
	/**
	 * @return the fUnits.
	 */
	public int getUnits() {
		return fUnits;
	}
	/**
	 * @return the fMachine.
	 */
	public String getMachine() {
		return fMachine;
	}
	/**
	 * @return the fSignature.
	 */
	public byte[] getSignature() {
		return fSignature;
	}
	/**
	 * @return the fType.
	 */
	public String getType() {
		return fType;
	}

	/**
	 * @return
	 */
	public List<String> getModules() {
		return Collections.unmodifiableList(fModules);
	}

	/**
	 * @return the license version
	 */
	public String getVersion() {
		return fVersion;
	}

	/**
	 * @return
	 */
	public boolean isEvaluation() {
		return "evaluation".equals(fType);
	}
}
