/**
 * 30-Jul-2005
 */
package com.ixora.rms.reactions;

import com.ixora.jobs.library.JobLibraryId;

/**
 * @author Daniel Moraru
 */
public final class ReactionDeliveryInfoJob extends ReactionDeliveryInfo {
	private JobLibraryId fJobLibrayId;

	public ReactionDeliveryInfoJob(JobLibraryId id, String msg) {
		super(msg);
		fJobLibrayId = id;
	}

	/**
	 * @return JobLibrayId.
	 */
	public JobLibraryId getJobLibrayId() {
		return fJobLibrayId;
	}
}
