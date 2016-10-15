package com.mindplus.connection;

import org.json.simple.JSONObject;

import com.mindplus.command.Command;
import com.mindplus.configuration.Configuration;

public class ConnectController {
	private ClientConnector clients = null;
	private CoordinateConnector servers = null;
	private RouterConnector router = null;

	private ConnectController() {
		this.clients = new ClientConnector(this);
		this.servers = new CoordinateConnector(this);
		this.router = new RouterConnector(this);
	}

	public static ConnectController getInstance() {
		return new ConnectController();
	}

	public void run() throws Exception {
		if (!Configuration.isRouter()) {
			this.router.contactRouter();
			new Thread(this.servers).start();
			this.servers.checkOtherServers();
		} else {
			new Thread(this.servers).start();
		}

		new Thread(this.clients).start();
	}

	public void requestServer(Command cmd) {
		servers.runInternalRequest(cmd);
	}

	public void requestClient(Command cmd) {
		clients.runInternalRequest(cmd);
	}

	public JSONObject requestRouter(String cmd, boolean needResponse) {
		return router.runInternalRequest(cmd, needResponse);
	}
}
