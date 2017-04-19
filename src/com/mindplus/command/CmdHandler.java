package com.mindplus.command;

import java.net.Socket;

import org.json.simple.JSONObject;

import com.mindplus.connection.ConnectorInf;
import com.mindplus.message.Message;

public abstract class CmdHandler {
	protected ConnectorInf connector = null;

	public CmdHandler(ConnectorInf connector) {
		this.connector = connector;
	}

	protected void response(Socket socket, JSONObject cmd) {
		Message msg = new Message(cmd);
		connector.write(socket, msg.toString());
	}
}
