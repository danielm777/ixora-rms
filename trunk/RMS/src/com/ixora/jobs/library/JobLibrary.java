/**
 * 30-Jul-2005
 */
package com.ixora.jobs.library;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.ixora.jobs.exception.JobNameAlreadyExists;
import com.ixora.jobs.services.JobLibraryService;
import com.ixora.common.SafeOverwrite;
import com.ixora.common.utils.Utils;
import com.ixora.common.xml.XMLExternalizable;
import com.ixora.common.xml.XMLUtils;
import com.ixora.common.xml.exception.XMLException;
import com.ixora.rms.exception.RMSException;

/**
 * @author Daniel Moraru
 */
public class JobLibrary implements JobLibraryService {
	/** Jobs */
	private HashMap<JobLibraryId, JobLibraryDefinition> fJobs;
	/** Store */
	private String fStore;


	/**
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws XMLException
	 */
	public JobLibrary() throws ClassNotFoundException, IOException, XMLException, InstantiationException, IllegalAccessException {
		super();
		fStore = Utils.getPath("/config/jobs/library");
		if(new File(fStore).length() != 0) {
			loadJobs();
		} else {
			fJobs = new HashMap<JobLibraryId, JobLibraryDefinition>();
		}
	}

	/**
	 * @see com.ixora.rms.services.JobLibraryService#getJob(com.ixora.jobs.library.JobLibraryId)
	 */
	public JobLibraryDefinition getJob(JobLibraryId id) {
		synchronized(this) {
			return fJobs.get(id);
		}
	}

	/**
	 * @throws RMSException
	 * @see com.ixora.rms.services.JobLibraryService#getAllJobs()
	 */
	public Map<JobLibraryId, JobLibraryDefinition> getAllJobs() throws RMSException {
		try {
			synchronized(this) {
				return (Map<JobLibraryId, JobLibraryDefinition>)Utils.deepClone(fJobs);
			}
		} catch(Exception e) {
			throw new RMSException(e);
		}
	}

	/**
	 * @throws RMSException
	 * @see com.ixora.rms.services.JobLibraryService#storeJob(com.ixora.jobs.library.JobLibraryDefinition)
	 */
	public JobLibraryId storeJob(JobLibraryDefinition job) throws JobNameAlreadyExists, RMSException {
		if(job == null) {
			throw new IllegalArgumentException("Null job definition");
		}
		JobLibraryId newId = new JobLibraryId(job.getName());
		if(fJobs.get(newId) != null) {
			throw new JobNameAlreadyExists(job.getName());
		}
		fJobs.put(newId, job);
		try {
			saveJobs();
		} catch(Exception e) {
			throw new RMSException(e);
		}
		return newId;
	}

	/**
	 * @throws RMSException
	 * @see com.ixora.rms.services.JobLibraryService#deleteJob(com.ixora.jobs.library.JobLibraryId)
	 */
	public void deleteJob(JobLibraryId id) throws RMSException {
		if(fJobs.remove(id) != null) {
			try {
				saveJobs();
			} catch(Exception e) {
				throw new RMSException(e);
			}
		}
	}

	/**
	 * @see com.ixora.rms.services.JobLibraryService#deleteJobs(java.util.Collection)
	 */
	public void deleteJobs(Collection<JobLibraryId> ids) throws RMSException {
		if(Utils.isEmptyCollection(ids)) {
			return;
		}
		for(JobLibraryId id : ids) {
			fJobs.remove(id);
		}
		try {
			saveJobs();
		} catch (Exception e) {
			throw new RMSException(e);
		}
	}

	/**
	 * Persists the job map.
	 * @throws XMLException
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws IOException
	 */
	private void saveJobs() throws XMLException, InstantiationException, IllegalAccessException, ClassNotFoundException, IOException {
		Document doc = XMLUtils.createEmptyDocument(null);
		Element root = doc.createElement("jobs");
		doc.appendChild(root);
		XMLUtils.writeObjects(JobLibraryDefinition.class, root,
				fJobs.values().toArray(new XMLExternalizable[fJobs.size()]));

		BufferedOutputStream os = null;
		SafeOverwrite so = new SafeOverwrite(fStore);
		try {
			so.backup();
			os = new BufferedOutputStream(new FileOutputStream(fStore));
			XMLUtils.write(doc, os);
			so.commit(os);
		} catch(Exception e) {
			so.rollback(os);
		}
	}

	/**
	 * Loads jobs.
	 * @throws XMLException
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	private void loadJobs() throws XMLException, InstantiationException, IllegalAccessException, ClassNotFoundException, FileNotFoundException {
		BufferedInputStream bs = null;
		try {
			bs = new BufferedInputStream(new FileInputStream(fStore));
			Document doc = XMLUtils.read(bs);
			Node root = XMLUtils.findChild(doc, "jobs");
			if(root != null) {
				XMLExternalizable[] objs = XMLUtils.readObjects(JobLibraryDefinition.class, root, "job");
				this.fJobs = new LinkedHashMap<JobLibraryId, JobLibraryDefinition>();
				if(!Utils.isEmptyArray(objs)) {
					for(XMLExternalizable obj : objs) {
						JobLibraryDefinition def = (JobLibraryDefinition)obj;
						JobLibraryId id = new JobLibraryId(def.getName());
						fJobs.put(id, def);
					}
				}
			}
		} finally {
			if(bs != null) {
				try {
					bs.close();
				} catch(IOException e) {}
			}
		}
	}
}
