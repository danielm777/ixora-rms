/**
 * 05-Mar-2006
 */
package com.ixora.rms.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import com.ixora.common.utils.Utils;

/**
 * @author Daniel Moraru
 */
public class CheckStartupScripts {
	private static final String AIX = "ppc-aix";
	private static final String SOLARIS_X86 = "x86-solaris";
	private static final String SOLARIS_SPARC = "sparc-solaris";
	private static final String WINDOWS = "x86-win32";
	private static final String LINUX = "x86-linux";

	private static class ALL {
		private Map<String, RMSLIB> fRMSLIB;
		private Map<String, SYSOPTS> fSYSOPTS;
		private Map<String, MAINCLASS> fMAINCLASS;

		ALL(String scriptName, boolean appendSuffix) throws IOException {
			fRMSLIB = new HashMap<String, RMSLIB>();
			fSYSOPTS = new HashMap<String, SYSOPTS>();
			fMAINCLASS = new HashMap<String, MAINCLASS>();

			File file = new File(Utils.getPath("bin/" + AIX + "/" + scriptName + (appendSuffix ? ".sh" : "")));
			loadFor(AIX, file);
			file = new File(Utils.getPath("bin/" + SOLARIS_SPARC + "/" + scriptName + (appendSuffix ? ".sh" : "")));
			loadFor(SOLARIS_SPARC, file);
			file = new File(Utils.getPath("bin/" + SOLARIS_X86 + "/" + scriptName + (appendSuffix ? ".sh" : "")));
			loadFor(SOLARIS_X86, file);
			file = new File(Utils.getPath("bin/" + WINDOWS + "/" + scriptName + (appendSuffix ? ".bat" : "")));
			loadFor(WINDOWS, file);
			file = new File(Utils.getPath("bin/" + LINUX + "/" + scriptName + (appendSuffix ? ".sh" : "")));
			loadFor(LINUX, file);
		}

		/**
		 * @param os
		 * @param file
		 * @throws FileNotFoundException
		 * @throws IOException
		 */
		private void loadFor(String os, File file) throws FileNotFoundException, IOException {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line;
			while((line = reader.readLine()) != null) {
				int eq = line.indexOf("=");
				if(eq < 0) {
					if(line.indexOf("com.ixora") >= 0) {
						MAINCLASS main = new MAINCLASS(line);
						fMAINCLASS.put(os, main);
					}
					continue;
				}
				String left = line.substring(0, eq);
				String right = line.substring(eq + 1);
				if(left.indexOf("RMSLIB") >= 0) {
					RMSLIB rmslib = new RMSLIB(right);
					fRMSLIB.put(os, rmslib);
				} else if(left.indexOf("SYSOPTS") >= 0) {
					SYSOPTS sysopts = new SYSOPTS(right);
					fSYSOPTS.put(os, sysopts);
				}
 			}
		}

		public String toString() {
			StringBuilder buff = new StringBuilder();
			buff.append("RMSLIB===================").append(Utils.getNewLine());
			buff.append("AIX    : ").append(fRMSLIB.get(AIX)).append(Utils.getNewLine());
			buff.append("WINDOWS: ").append(fRMSLIB.get(WINDOWS)).append(Utils.getNewLine());
			buff.append("LINUX  : ").append(fRMSLIB.get(LINUX)).append(Utils.getNewLine());
			buff.append("SOL_SPA: ").append(fRMSLIB.get(SOLARIS_SPARC)).append(Utils.getNewLine());
			buff.append("SOL_X86: ").append(fRMSLIB.get(SOLARIS_X86)).append(Utils.getNewLine());
			buff.append("SYSOPTS===================").append(Utils.getNewLine());
			buff.append("AIX    : ").append(fSYSOPTS.get(AIX)).append(Utils.getNewLine());
			buff.append("WINDOWS: ").append(fSYSOPTS.get(WINDOWS)).append(Utils.getNewLine());
			buff.append("LINUX  : ").append(fSYSOPTS.get(LINUX)).append(Utils.getNewLine());
			buff.append("SOL_SPA: ").append(fSYSOPTS.get(SOLARIS_SPARC)).append(Utils.getNewLine());
			buff.append("SOL_X86: ").append(fSYSOPTS.get(SOLARIS_X86)).append(Utils.getNewLine());
			buff.append("MAINCLASS===================").append(Utils.getNewLine());
			buff.append("AIX    : ").append(fMAINCLASS.get(AIX)).append(Utils.getNewLine());
			buff.append("WINDOWS: ").append(fMAINCLASS.get(WINDOWS)).append(Utils.getNewLine());
			buff.append("LINUX  : ").append(fMAINCLASS.get(LINUX)).append(Utils.getNewLine());
			buff.append("SOL_SPA: ").append(fMAINCLASS.get(SOLARIS_SPARC)).append(Utils.getNewLine());
			buff.append("SOL_X86: ").append(fMAINCLASS.get(SOLARIS_X86)).append(Utils.getNewLine());
			return buff.toString();
		}

		public void check() {
			RMSLIB rmslib = null;
			for(RMSLIB e : fRMSLIB.values()) {
				if(rmslib == null) {
					rmslib = e;
				} else {
					assert(rmslib.equals(e));
				}
			}
			SYSOPTS sysopt = null;
			for(SYSOPTS e : fSYSOPTS.values()) {
				if(sysopt == null) {
					sysopt = e;
				} else {
					assert(sysopt.equals(e));
				}
			}
			MAINCLASS mainc = null;
			for(MAINCLASS e : fMAINCLASS.values()) {
				if(mainc == null) {
					mainc = e;
				} else {
					assert(mainc.equals(e));
				}
			}
		}
	}

	private static class RMSLIB {
		private List<String> fEntries;

		RMSLIB(String line) {
			fEntries = new LinkedList<String>();
			StringTokenizer tok = new StringTokenizer(line, ":;");
			while(tok.hasMoreTokens()) {
				fEntries.add(tok.nextToken().trim());
			}
		}
		public String toString() {
			StringBuilder buff = new StringBuilder();
			for(String en : fEntries) {
				buff.append(en).append("|");
			}
			return buff.toString();
		}
		public boolean equals(Object obj) {
			return fEntries.equals(((RMSLIB)obj).fEntries);
		}
	}

	private static class SYSOPTS {
		private Map<String, String> fEntries;

		SYSOPTS(String line) {
			fEntries = new LinkedHashMap<String, String>();
			StringTokenizer tok = new StringTokenizer(line, " ");
			while(tok.hasMoreTokens()) {
				String entry = tok.nextToken().trim();
				int idx = entry.indexOf('=');
				if(idx < 0) {
					continue;
				}
				fEntries.put(entry.substring(0, idx), entry.substring(idx + 1, entry.length()));
			}
		}
		public String toString() {
			StringBuilder buff = new StringBuilder();
			for(Map.Entry<String, String> en : fEntries.entrySet()) {
				buff.append(en.getKey()).append("=").append(en.getValue()).append("|");
			}
			return buff.toString();
		}
		public boolean equals(Object obj) {
			return fEntries.equals(((SYSOPTS)obj).fEntries);
		}
	}

	private static class MAINCLASS {
		private String fClass;
		MAINCLASS(String line) {
			StringTokenizer tok = new StringTokenizer(line, " ");
			while(tok.hasMoreTokens()) {
				String entry = tok.nextToken().trim();
				if(entry.indexOf("com.ixora") >= 0) {
					fClass = entry;
					break;
				}
			}
		}
		public boolean equals(Object obj) {
			return fClass.equals(((MAINCLASS)obj).fClass);
		}
		public String toString() {
			return fClass;
		}
	}

	/**
	 *
	 */
	private CheckStartupScripts() {
		super();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
		String[] files = new String[]{
				"console",
				"console.launch",
				"hmStart",
				"hmStop",
				"hm.launch",
		};
		ALL all = new ALL(files[3], true);
		System.out.println(all.toString());
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

}
