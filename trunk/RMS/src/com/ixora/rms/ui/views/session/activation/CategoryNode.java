/**
 * 03-Feb-2006
 */
package com.ixora.rms.ui.views.session.activation;

import javax.swing.tree.DefaultMutableTreeNode;

import com.ixora.common.MessageRepository;
import com.ixora.rms.repository.AgentCategory;

/**
 * @author Daniel Moraru
 */
public class CategoryNode extends DefaultMutableTreeNode {
	private static final long serialVersionUID = 4065577275842422704L;
	private String fTranslatedCategory;

	/**
	 * @param category
	 */
	public CategoryNode(String category) {
		super(category, true);
		AgentCategory ac = AgentCategory.resolve(category);
		if(ac == null) {
			fTranslatedCategory = MessageRepository.get(category);
		} else {
			fTranslatedCategory = ac.toString();
		}
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return fTranslatedCategory;
	}
}
