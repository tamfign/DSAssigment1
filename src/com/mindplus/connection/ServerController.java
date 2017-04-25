package com.mindplus.connection;

import org.json.simple.JSONObject;

import com.mindplus.command.Command;
import com.mindplus.command.ServerServerCmd;
import com.mindplus.model.ServerListController;

public class ServerController implements ConnectController {
	private ClientConnector clients = null;
	private CoordinateConnector servers = null;
	private RouterConnector router = null;

	private ServerController() {
		this.clients = new ClientConnector(this);
		this.servers = new CoordinateConnector(this);
		this.router = new RouterConnector(this);
	}

	public static ConnectController getInstance() {
		return new ServerController();
	}

	public void run() throws Exception {
		new ServerHeartbeatController().start();
		contactRouter();
		new Thread(this.servers).start();
		this.servers.checkOtherServers();
		new Thread(this.clients).start();
	}

	private void contactRouter() throws Exception {
		JSONObject result = router.sendAndGet(ServerServerCmd.getNewServerCmd());
		if (result == null) {
			throw new Exception("Router can not be reached.");
		}

		boolean approved = Command.getResult(result);
		if (!approved) {
			throw new Exception("Rejected by router.");
		}
		ServerListController.getInstance().addServers(Command.getServers(result));
	}

	public void requestServer(Command cmd) {
		servers.runInternalRequest(cmd);
	}

	public void requestClient(Command cmd) {
		clients.runInternalRequest(cmd);
	}

	@Override
	public JSONObject requestRouter(JSONObject cmd, boolean needResponse) {
		return router.runInternalRequest(cmd, needResponse);
	}

	@Override
	public void takeover() {
		return;
	}
}
