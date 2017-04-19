package com.mindplus.connection;

import org.json.simple.JSONObject;

import com.mindplus.command.Command;

public class ServerController implements ConnectController {
	private ClientConnector clients = null;
	private CoordinateConnector servers = null;

	private ServerController() {
		this.clients = new ClientConnector(this);
		this.servers = new CoordinateConnector(this);
	}

	public static ConnectController getInstance() {
		return new ServerController();
	}

	public void run() throws Exception {
		new Thread(this.servers).start();
		this.servers.checkOtherServers();
		new Thread(this.clients).start();
	}

	public void requestServer(Command cmd) {
		servers.runInternalRequest(cmd);
	}

	public void requestClient(Command cmd) {
		clients.runInternalRequest(cmd);
	}

	@Override
	public JSONObject requestRouter(JSONObject cmd, boolean needResponse) {
		return null;
	}

	@Override
	public void takeover() {
		return;
	}
}
