/**
 * 25-Dec-2005
 */
package com.ixora.rms.agents.file;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;

import com.ixora.common.utils.Utils;
import com.ixora.rms.CounterId;
import com.ixora.rms.CounterType;
import com.ixora.rms.EntityId;
import com.ixora.rms.agents.AgentExecutionContext;
import com.ixora.rms.agents.file.messages.Msg;
import com.ixora.rms.agents.impl.Counter;
import com.ixora.rms.agents.impl.Entity;
import com.ixora.rms.data.CounterValueDouble;

/**
 * @author Daniel Moraru
 */
public class FileEntity extends Entity implements Comparable<FileEntity>{
	private static final long serialVersionUID = -682661178298561860L;
	private File fFile;
	/**
	 * @param id
	 * @param file
	 * @param c
	 */
	public FileEntity(EntityId parent, File file, AgentExecutionContext c) {
		super(new EntityId(parent, file.getName()), c);
		fFile = file;
		if(fFile.isDirectory()) {
			fHasChildren = true;
		} else {
			fHasChildren = false;
			addCounter(new Counter(Msg.COUNTER_SIZE_BYTES,
				Msg.COUNTER_SIZE_BYTES + ".description", CounterType.LONG));
			addCounter(new Counter(Msg.COUNTER_LAST_MODIFIED,
					Msg.COUNTER_LAST_MODIFIED + ".description", CounterType.DATE));
		}
	}

	/**
	 * @see com.ixora.rms.agents.impl.Entity#retrieveCounterValues()
	 */
	protected void retrieveCounterValues() throws Throwable {
		if(fFile.isFile()) {
			getCounter(new CounterId(Msg.COUNTER_SIZE_BYTES))
				.dataReceived(new CounterValueDouble(fFile.length()));
			getCounter(new CounterId(Msg.COUNTER_LAST_MODIFIED))
				.dataReceived(new CounterValueDouble(fFile.lastModified()));
		}
	}

	/**
	 * @see com.ixora.rms.agents.impl.Entity#updateChildrenEntities(boolean)
	 */
	public void updateChildrenEntities(boolean recursive) throws Throwable {
		if(fFile.isDirectory()) {
			Configuration conf = (Configuration)fContext.getAgentConfiguration().getAgentCustomConfiguration();
			final boolean ignoreSubfolders = conf.getBoolean(Configuration.IGNORE_FOLDERS);
			String fileNameRegex = conf.getString(Configuration.FILE_NAME_PATTERN);
			final Pattern pattern = Pattern.compile(fileNameRegex);
			File[] files = fFile.listFiles(new FileFilter(){
				public boolean accept(File file) {
					if(file.isDirectory()) {
						return !ignoreSubfolders;
					}
					String fname = file.getName();
					if(pattern.matcher(fname).find()) {
						return true;
					}
					return false;
				}
			});
			Set<FileEntity> orderedFolders = new TreeSet<FileEntity>();
			Set<FileEntity> orderedFiles = new TreeSet<FileEntity>();
			// put the current children in the sets above
			for(Entity child : fChildrenEntities.values()) {
				FileEntity fe = (FileEntity)child;
				if(fe.fFile.exists()) {
					if(fe.fFile.isDirectory()) {
						orderedFolders.add(fe);
					} else {
						orderedFiles.add(fe);
					}
				}
			}
			fChildrenEntities.clear();
			List<EntityId> newChildren = new LinkedList<EntityId>();
			if(!Utils.isEmptyArray(files)) {
				List<File> filesLst = Arrays.asList(files);
				// add new, update existing
				for(File file : filesLst) {
					EntityId eid = new EntityId(fEntityId, file.getName());
					FileEntity child = (FileEntity)fChildrenEntities.get(eid);
					if(child == null) {
						FileEntity newChild = new FileEntity(fEntityId, file, fContext);
						newChildren.add(newChild.getId());
						if(file.isDirectory()) {
							orderedFolders.add(newChild);
						} else {
							orderedFiles.add(newChild);
						}
					}
				}
				// rebuild fChildrenEntities
				for(FileEntity child : orderedFolders) {
					if(newChildren.contains(child.getId())) {
						addChildEntity(child);
					} else {
						fChildrenEntities.put(child.getId(), child);
					}
				}
				for(FileEntity child : orderedFiles) {
					if(newChildren.contains(child.getId())) {
						addChildEntity(child);
					} else {
						fChildrenEntities.put(child.getId(), child);
					}
				}
			}
			if(recursive) {
				for(Entity child : fChildrenEntities.values()) {
					child.updateChildrenEntities(recursive);
				}
			}
		}
	}

	/**
	 * @see java.lang.Comparable#compareTo(T)
	 */
	public int compareTo(FileEntity o) {
		return fFile.compareTo(o.fFile);
	}
}
