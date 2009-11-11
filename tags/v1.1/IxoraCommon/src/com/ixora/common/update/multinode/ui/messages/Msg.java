/*
 * Created on 20-Mar-2004
 */
package com.ixora.common.update.multinode.ui.messages;

/**
 * @author Daniel Moraru
 */
public interface Msg {
	public static final String UPDATE_UI_TEXT_NODES =
		"update.ui.text.nodes";
	public static final String UPDATE_UI_ACTION_ADDNODE =
		"update.ui.action.add_node";
	public static final String UPDATE_UI_ACTION_REMOVENODE =
		"update.ui.action.remove_node";
	public static final String UPDATE_UI_TEXT_STATUS_NODE_NOT_UPDATED =
		"update.ui.text.status.node_not_updated";
	public static final String UPDATE_UI_TEXT_STATUS_NODE_UPDATED =
		"update.ui.text.status.node_updated";
	public static final String UPDATE_UI_TEXT_STATUS_NODE_UPDATE_FAILED =
		"update.ui.text.status.node_update_failed";
}
