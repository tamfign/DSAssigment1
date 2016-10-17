package com.mindplus.connection;

import org.json.simple.JSONObject;

import com.mindplus.command.Command;
import com.mindplus.configuration.Configuration;
import com.mindplus.configuration.RouterConfig;

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
		if (Configuration.isRouter()) {
			new RouterHeartbeatController().start();
		}

		if (!isBackupRouter()) {
			new ServerHeartbeatController().start();
		}

		if (!Configuration.isRouter()) {
			this.router.contactRouter();
			new Thread(this.servers).start();
			this.servers.checkOtherServers();
		} else if (!isBackupRouter()) {
			new Thread(this.servers).start();
		}

		if (!isBackupRouter()) {
			new Thread(this.clients).start();
		}
	}

	private boolean isBackupRouter() {
		return Configuration.isRouter() && ((RouterConfig) Configuration.getConfig()).isBackUp();
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
