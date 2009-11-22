/*
 * Created on 21-Aug-2005
 */
package com.ixora.rms.agents.snmp;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.percederberg.mibble.Mib;
import net.percederberg.mibble.MibLoader;
import net.percederberg.mibble.MibLoaderException;
import net.percederberg.mibble.MibSymbol;
import net.percederberg.mibble.MibType;
import net.percederberg.mibble.MibValue;
import net.percederberg.mibble.MibValueSymbol;
import net.percederberg.mibble.MibLoaderLog.LogEntry;
import net.percederberg.mibble.value.ObjectIdentifierValue;

import com.ixora.common.logging.AppLogger;
import com.ixora.common.logging.AppLoggerFactory;
import com.ixora.common.utils.Utils;
import com.ixora.rms.agents.AgentExecutionContext;
import com.ixora.rms.agents.snmp.exceptions.SNMPMIBParseException;

/**
 * MIBTree
 * Holds definitions loaded from multiple MIB files
 */
public class MIBTree implements Serializable {
	private static final AppLogger logger = AppLoggerFactory.getLogger(MIBTree.class);
    private static final long serialVersionUID = 262448826763781285L;

	private MIBNode	fRootNode;
	private Map<MibValueSymbol, MIBNode> fMapNodes = new HashMap<MibValueSymbol, MIBNode>();

	/** Maps a user MIB to its last modified date */
	private Map<String, Long> fMapUserMIBs = new HashMap<String, Long>();

	public MIBTree() {
		fRootNode = new MIBNode("", null, null);
	}

    /**
     * Adds a MIB symbol to a MIB tree model.
     */
    private void addSymbol(MIBNode parentNode, MibSymbol symbol) {
        if (symbol instanceof MibValueSymbol) {
        	MibValueSymbol mvs = (MibValueSymbol)symbol;
        	MibValue value = mvs.getValue();
        	MibType type = mvs.getType();

            if (value instanceof ObjectIdentifierValue) {
            	ObjectIdentifierValue oid = (ObjectIdentifierValue) value;
                addToTree(parentNode, oid, type);
            }
        }
    }
    /**
     * Adds an object identifier value to a MIB tree model.
     * @return the MIB tree node added
     */
    private MIBNode addToTree(MIBNode parentNode, ObjectIdentifierValue oid, MibType mibType) {
    	MIBNode  parent;
    	MIBNode  node;
        String   name;

        // Add parent node to tree (if needed)
        if (hasParent(oid)) {
            parent = addToTree(parentNode, oid.getParent(), mibType);
        } else {
            parent = parentNode;
        }

        // Check if node already added
        for (int i = 0; i < parent.getChildCount(); i++) {
            node = parent.getChild(i);
            if (node.getValue().equals(oid)) {
                return node;
            }
        }

        // Create new node
        name = oid.getName();
        node = new MIBNode(name, mibType, oid);
        parent.add(node);
        fMapNodes.put(oid.getSymbol(), node);
        return node;
    }

    /**
     * @param symbol
     * @return the MIB node associated with the given symbol. Uses
     * a hash map for fast access.
     */
    public MIBNode getNode(MibSymbol symbol) {
        return fMapNodes.get(symbol);
    }

    /**
     * @return the root node in the MIB tree
     */
    public MIBNode getRootNode() {
    	return fRootNode;
    }

    /**
     * Checks if the specified object identifier has a parent.
     * @return true if the object identifier has a parent, or
     *         false otherwise
     */
    private boolean hasParent(ObjectIdentifierValue oid) {
        ObjectIdentifierValue  parent = oid.getParent();

        return oid.getSymbol() != null
            && oid.getSymbol().getMib() != null
            && parent != null
            && parent.getSymbol() != null
            && parent.getSymbol().getMib() != null
            && parent.getSymbol().getMib().equals(oid.getSymbol().getMib());
    }

	/**
	 * Loads a text MIB file and merges it with the tree
	 * @param fileName
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public void loadMIB(MibLoader mibLoader, File mibFile) throws SNMPMIBParseException {
		try {
	        Mib mib = mibLoader.load(mibFile);

	        Iterator   iter = mib.getAllSymbols().iterator();
	        MibSymbol  symbol;
	        MIBNode    node;

	        // Create value sub tree
	        node = new MIBNode(mib.getName(), null, null);
	        while (iter.hasNext()) {
	            symbol = (MibSymbol) iter.next();
	            addSymbol(node, symbol);
	        }

	        // Add sub tree root to MIB tree
	        fRootNode.add(node);

		} catch (MibLoaderException e) {
			// Extract parsing errors from the exception
			StringBuffer sbErrors = new StringBuffer();
			for (Iterator it = e.getLog().entries(); it.hasNext();) {
				LogEntry logEntry = (LogEntry) it.next();
				if (logEntry.getFile() != null) {
					sbErrors.append(MessageFormat.format("{0}({1}): {2}", new Object[] {
							logEntry.getFile(),
							logEntry.getLineNumber(),
							logEntry.getMessage()
					}));
				} else {
					sbErrors.append(MessageFormat.format("line {0}: {1}", new Object[] {
							logEntry.getLineNumber(),
							logEntry.getMessage()
					}));
				}
				sbErrors.append("<br>");
			}
			throw new SNMPMIBParseException(mibFile.getAbsolutePath(), sbErrors.toString());
		} catch (IOException e) {
			throw new SNMPMIBParseException(mibFile.getAbsolutePath(), e.getMessage());
		}
	}

	/**
	 * Loads the contents of the whole tree from the specified file.
	 * Only one compiled file can be loaded at a time.
	 * @param fileName
	 * @throws IOException
	 */
	public static MIBTree loadCompiledMIB(AgentExecutionContext context) throws IOException {
		// Get the absolute path to our compiled MIB
		Configuration cfg = (Configuration)context.getAgentConfiguration().getAgentCustomConfiguration();
		String compiledMIBPath = Utils.getPath(cfg.getString(Configuration.COMPILED_MIB_FILE_NAME));

		boolean shouldSave = false;
		MIBTree	retTree = null;
		try {
			retTree = (MIBTree) Utils.readObjectFromFile(compiledMIBPath);
		} catch (IOException e){
			logger.error(e);
			// Not found, or corrupt, create a new one.
			shouldSave = true;
			retTree = new MIBTree();
		} catch (ClassNotFoundException e) {
			logger.error(e);
			// Not found, or corrupt, create a new one.
			shouldSave = true;
			retTree = new MIBTree();
		}

		// Merge with the user-added MIBs
		if (retTree.mergeUserMIBs(context)) {
			shouldSave = true;
		}

		// Save if new or modified
		if (shouldSave) {
			retTree.saveCompiledMIB(compiledMIBPath);
		}

		return retTree;
	}

	/**
	 * Saves the result of merging multiple MIBs into a compiled tree
	 * @param fileName
	 * @throws IOException
	 */
	public void saveCompiledMIB(String fileName) throws IOException {
		Utils.writeObjectToFile(fileName, this);
	}

	/**
	 * Adds the MIBs in this folder, and recurses to all subfolders
	 * @param mibLoader
	 * @param mibDir
	 */
	private boolean mergeSubfolder(AgentExecutionContext context, MibLoader mibLoader, File mibDir) {
		boolean mergedAnything = false;

		// Merge all new or modified files in this tree
		File[] listMIBs = Utils.listFilesForFolder(mibDir);
		if (listMIBs != null) {
			for (int i = 0; i < listMIBs.length; i++) {
				File mibFile = listMIBs[i];
				if (mibFile.isFile()) {
					long lastModified = mibFile.lastModified();
					Long lastLoaded = fMapUserMIBs.get(mibFile.getName());

					// If not loaded already or it was changed since last loaded
					if (lastLoaded == null || lastLoaded.longValue() < lastModified) {
						try {
							loadMIB(mibLoader, mibFile);
							fMapUserMIBs.put(mibFile.getName(), new Long(lastModified));
							mergedAnything = true;
						} catch (Exception e) {
							// failed to load one MIB, continue with others
							context.error(e);
						}
					}
				} else if (mibFile.isDirectory()) {
					mergedAnything = mergeSubfolder(context, mibLoader, mibFile) || mergedAnything;
				}
			}
		}

		return mergedAnything;
	}

	/**
	 * Looks for user MIBs dropped in some folder and merges them into the
	 * big compiled MIB.
	 * @return true if at least one mib was merged, so the compiled mib
	 * will be saved again
	 */
	private boolean mergeUserMIBs(AgentExecutionContext context) {
		// Enumerate all user files in the configured folder
		Configuration cfg = (Configuration)context.getAgentConfiguration().getAgentCustomConfiguration();
		String userMibsPath = cfg.getString(Configuration.USER_MIBS_FOLDER);
		if (Utils.isEmptyString(userMibsPath)) {
			return false;
		}

		File mibDir = new File(userMibsPath);

		// Add all subfolders to search path
		MibLoader  mibLoader = new MibLoader();
		mibLoader.addAllDirs(mibDir);

		// Add the MIBs in this folder, and recurse to all subfolders
		return mergeSubfolder(context, mibLoader, mibDir);
	}
}
