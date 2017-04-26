package com.mindplus.listener;

import java.net.Socket;

import org.json.simple.JSONObject;

import com.mindplus.command.ClientServerCmd;
import com.mindplus.command.Command;
import com.mindplus.connection.ClientConnector;
import com.mindplus.message.Message;

public class ClientListener extends MsgListener {
	private String clientId = null;
	private boolean mayAboutClosed = false;

	public ClientListener(ClientConnector connector, Socket socket) {
		super(connector, socket);
	}

	@Override
	protected void handleDisconnect() {
		// In case of quit again.
		if (!mayAboutClosed) {
			handleQuit();
		}
		clientId = null;
	}

	private void handleQuit() {
		handleRequest(ClientServerCmd.quit());
	}

	private void catchClientId(JSONObject cmd) {
		if (Command.isNewId(cmd)) {
			this.clientId = Command.getNewId(cmd);
		}
	}

	private void checkIfClosing(JSONObject cmd) {
		if (!mayAboutClosed && (Command.isClosing(cmd) || (Command.isMoving(cmd)))) {
			mayAboutClosed = true;
		} else if (mayAboutClosed) {
			mayAboutClosed = false;
		}
	}

	private void handleRequest(JSONObject cmdObj) {
		catchClientId(cmdObj);
		checkIfClosing(cmdObj);
		((ClientConnector) getConnector()).getMQ().addCmd(new Command(getSocket(), cmdObj, clientId));
	}

	@Override
	protected void handleRequest(Message msg) {
		System.out.println(msg.toString());
		handleRequest(msg.getCMDObj());
	}
}
