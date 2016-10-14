package com.mindplus.connection;

import java.net.Socket;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import org.json.simple.JSONObject;

import com.mindplus.command.Command;
import com.mindplus.command.ServerServerCmd;
import com.mindplus.configuration.Configuration;
import com.mindplus.configuration.RouterConfig;
import com.mindplus.listener.CommandListener;
import com.mindplus.model.ServerListController;

public class RouterConnector extends Connector {

	protected RouterConnector(ConnectController controller) {
		super(controller);
	}

	public void run() throws Exception {
		JSONObject result = contactRouter(Configuration.getRouterConfig());
		if (result == null) {
			throw new Exception("Router can not be reached yet.");
		}

		boolean approved = Command.getResult(result);
		if (!approved) {
			throw new Exception("Rejected by router.");
		}
		ServerListController.getInstance().addServers(Command.getServers(result));
	}

	private JSONObject contactRouter(RouterConfig router) {
		JSONObject ret = null;
		SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
		SSLSocket rSocket = null;

		try {
			rSocket = (SSLSocket) factory.createSocket(router.getHost(), router.getCoordinationPort());
			if (rSocket.isConnected()) {
				write(rSocket, ServerServerCmd.getNewServerCmd());
				ret = Command.getCmdObject(readCmd(rSocket));
			}
		} catch (Exception e) {
			System.out.println("Fail to connect router");
		} finally {
			close(rSocket);
		}
		return ret;
	}

	@Override
	public boolean broadcastAndGetResult(String cmd) {
		return false;
	}

	@Override
	public void broadcast(String cmd) {
	}

	@Override
	public void addBroadcastList(String id, Socket socket) {
	}

	@Override
	public void removeBroadcastList(String id) {
	}

	@Override
	public void requestTheOther(Command command) {
	}

	@Override
	protected CommandListener getListener(Socket socket) {
		return null;
	}
}
