package com.mindplus.command;

import org.json.simple.JSONObject;

import com.mindplus.connection.ConnectController;
import com.mindplus.heartbeat.Callback;
import com.mindplus.model.ServerListController;

public class RouterBeaterHandler implements Callback {

	private ConnectController cController = null;

	public RouterBeaterHandler(ConnectController cController) {
		this.cController = cController;
	}

	@Override
	public void reportDown(Object obj) {
		System.out.println("====Main Router is DOWN====");
		this.cController.takeover();
	}

	@Override
	public void update(Object obj) {
		ServerListController.getInstance().addServers(Command.getServers((JSONObject) obj));
	}
}
