package com.mindplus.connection;

import org.json.simple.JSONObject;

import com.mindplus.command.Command;
import com.mindplus.configuration.Configuration;
import com.mindplus.configuration.RouterConfig;

public class RouterController implements ConnectController {
	private ClientConnector clients = null;
	private CoordinateConnector servers = null;
	private RouterConnector router = null;
	private RouterHeartbeatController rHeatbeat = null;

	private RouterController() {
		this.clients = new ClientConnector(this);
		this.servers = new CoordinateConnector(this);
		this.router = new RouterConnector(this);
	}

	public static ConnectController getInstance() {
		return new RouterController();
	}

	public void run() throws Exception {
		// rHeatbeat = new RouterHeartbeatController(this);
		// rHeatbeat.start();

		if (!isBackupRouter()) {
			new ServerHeartbeatController().start();
			new Thread(this.servers).start();
			new Thread(this.clients).start();
		}
	}

	private boolean isBackupRouter() {
		return Configuration.isRouter() && ((RouterConfig) Configuration.getConfig()).isBackUp();
	}

	public void takeover() {
		System.out.println("Backup Router begins to takeover...");
		rHeatbeat.cancel();
		new Thread(this.servers).start();
		new ServerHeartbeatController().start();
		servers.updateChatRoomList();
		new Thread(this.clients).start();
	}

	public void requestServer(Command cmd) {
		servers.runInternalRequest(cmd);
	}

	public void requestClient(Command cmd) {
		clients.runInternalRequest(cmd);
	}

	public JSONObject requestRouter(JSONObject cmd, boolean needResponse) {
		return router.runInternalRequest(cmd, needResponse);
	}
}
