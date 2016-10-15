package com.mindplus.listener;

import java.net.Socket;

import org.json.simple.JSONObject;

import com.mindplus.command.Command;
import com.mindplus.connection.CoordinateConnector;
import com.mindplus.model.ChatRoomListController;
import com.mindplus.model.ServerListController;

public class CoordinateListener extends CommandListener {
	private String serverId = null;

	public CoordinateListener(CoordinateConnector connetor, Socket socket) {
		super(connetor, socket);
	}

	private void catchServerId(JSONObject cmdObj) {
		if (Command.isWithServerId(cmdObj)) {
			this.serverId = Command.getNewServerId(cmdObj);
		}
	}

	@Override
	protected void handleDisconnect() {
		if (this.serverId != null) {
			ServerListController.getInstance().removeServer(serverId);
			ChatRoomListController.getInstance().removeRoomByServerId(serverId);
		} else {
			// TODO what if no id at all.
		}
		this.serverId = null;
	}

	@Override
	protected void handleRequest(String cmdLine) {
		System.out.println("Server: " + cmdLine);
		JSONObject cmdObject = Command.getCmdObject(cmdLine);

		catchServerId(cmdObject);
		((CoordinateConnector) getConnector()).getMQ().addCmd(new Command(getSocket(), cmdObject, null));
	}
}