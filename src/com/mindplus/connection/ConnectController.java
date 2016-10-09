package com.mindplus.connection;

import com.mindplus.command.Command;
import com.mindplus.configuration.Configuration;

public class ConnectController {
	private ClientConnector clients = null;
	private CoordinateConnector servers = null;

	private ConnectController() {
		this.clients = new ClientConnector(this);
		this.servers = new CoordinateConnector(this);
	}

	public static ConnectController getInstance() {
		return new ConnectController();
	}

	public void run() {
		if (!Configuration.isRouter()) {
			new RouterConnector(this).run();
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
}
