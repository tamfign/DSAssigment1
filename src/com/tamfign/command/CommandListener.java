package com.tamfign.command;

import java.io.IOException;
import java.net.Socket;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.tamfign.connection.Connector;
import com.tamfign.connection.ConnectorInf;

public abstract class CommandListener implements Runnable {
	private Socket socket = null;
	private ConnectorInf connetor = null;

	public CommandListener(Connector connetor, Socket socket) {
		this.connetor = connetor;
		this.socket = socket;
	}

	@Override
	public void run() {
		String cmd;
		try {
			while (socket.isConnected()) {
				cmd = getConnector().readCmd(socket);
				if (cmd != null && !"".equals(cmd)) {
					handleRequest(cmd);
				}
			}
		} catch (IOException e) {
			handleDisconnect();
		} finally {
			getConnector().close(socket);
		}
	}

	protected abstract void handleDisconnect();

	protected abstract void handleRequest(String cmd);

	protected Socket getSocket() {
		return this.socket;
	}

	protected ConnectorInf getConnector() {
		return this.connetor;
	}

	protected void terminate(String id) {
		getConnector().close(socket);
		connetor.removeBroadcastList(id);
	}

	protected void response(String cmd) {
		getConnector().write(this.socket, cmd);
	}

	protected JSONObject getCmdObject(String cmd) {
		JSONObject ret = null;
		try {
			ret = (JSONObject) new JSONParser().parse(cmd);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return ret;
	}
}