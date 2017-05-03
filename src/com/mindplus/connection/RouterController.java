package com.mindplus.connection;

import org.json.simple.JSONObject;

import com.mindplus.command.Command;

public class RouterController implements ConnectController {
	private ClientConnector clients = null;
	private CoordinateConnector servers = null;

	private RouterController() {
		this.clients = new ClientConnector(this);
		this.servers = new CoordinateConnector(this);
	}

	public static ConnectController getInstance() {
		return new RouterController();
	}

	public void run() throws Exception {
		new ServerHeartbeatController().start();
		new Thread(this.servers).start();
		new Thread(this.clients).start();
	}

	public void requestServer(Command cmd) {
		servers.runInternalRequest(cmd);
	}

	public void requestClient(Command cmd) {
		clients.runInternalRequest(cmd);
	}

	public JSONObject requestRouter(JSONObject cmd, boolean needResponse) {
		return null;
	}
}
