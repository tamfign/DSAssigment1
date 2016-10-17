package com.mindplus.command;

import org.json.simple.JSONObject;

import com.mindplus.heartbeat.Callback;
import com.mindplus.model.ChatRoomListController;
import com.mindplus.model.ServerListController;

public class ServerBeaterHandler implements Callback {

	@Override
	public void reportDown(Object obj) {
		String serverId = (String) obj;
		System.out.println("====" + serverId + " DOWN====");

		ChatRoomListController.getInstance().removeRoomByServerId(serverId);
		ServerListController.getInstance().removeServer(serverId);
	}

	@Override
	public void update(Object obj) {
		String serverId = Command.getServerId((JSONObject) obj);
		long volume = (long) ((JSONObject) obj).get(Command.P_CLIENT_VOLUME);
		ServerListController.getInstance().updateVolume(serverId, volume);
	}

}
