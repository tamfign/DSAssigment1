package com.mindplus.connection;

import java.util.ArrayList;

import org.json.simple.JSONObject;

import com.mindplus.command.Command;
import com.mindplus.configuration.Configuration;
import com.mindplus.configuration.RouterConfig;
import com.mindplus.model.ServerListController;

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
		if (isBackupRouter()) {
			// TODO remove just for testing.
			ArrayList<String> list = new ArrayList<String>();
			list.add("s1|13.73.115.93|5557|4445");
			list.add("s2|13.73.115.93|5558|4446");
			list.add("s3|13.73.115.93|5559|4447");
			ServerListController.getInstance().addServers(list);

			takeover();
		}

		if (!Configuration.isRouter()) {
			this.router.contactRouter();
			new Thread(this.servers).start();
			this.servers.checkOtherServers();
		} else {
			new Thread(this.servers).start();
		}

		new Thread(this.clients).start();
	}

	private boolean isBackupRouter() {
		return Configuration.isRouter() && ((RouterConfig) Configuration.getConfig()).isBackUp();
	}

	// TODO heartbeat before takeover
	private void takeover() {
		System.out.println("Backup Router begins to takeover...");
		servers.updateChatRoomList();
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
