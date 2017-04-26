package com.mindplus.listener;

import java.net.Socket;

import com.mindplus.connection.Connector;
import com.mindplus.connection.ConnectorInf;
import com.mindplus.message.Message;

public abstract class MsgListener implements Runnable {
	private Socket socket = null;
	private ConnectorInf connetor = null;

	public MsgListener(Connector connetor, Socket socket) {
		this.connetor = connetor;
		this.socket = socket;
	}

	@Override
	public void run() {
		Message cmd;
		try {
			while (socket.isConnected()) {
				cmd = getConnector().readCmd(socket);
				if (cmd != null) {
					handleRequest(cmd);
				}
			}
			throw new Exception("Socket closed.");
		} catch (Exception e) {
			handleDisconnect();
		} finally {
			getConnector().close(socket);
		}
	}

	protected abstract void handleDisconnect();

	protected abstract void handleRequest(Message cmd);

	protected Socket getSocket() {
		return this.socket;
	}

	protected ConnectorInf getConnector() {
		return this.connetor;
	}
}
