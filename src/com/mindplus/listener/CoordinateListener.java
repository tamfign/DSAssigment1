package com.mindplus.listener;


import java.net.Socket;

import org.json.simple.JSONObject;

import com.mindplus.command.Command;
import com.mindplus.connection.CoordinateConnector;

public class CoordinateListener extends CommandListener {

	public CoordinateListener(CoordinateConnector connetor, Socket socket) {
		super(connetor, socket);
	}

	@Override
	protected void handleDisconnect() {
		//TODO if server crash clean its data.
	}

	@Override
	protected void handleRequest(String cmdLine) {
		System.out.println("Server: " + cmdLine);
		JSONObject cmdObject = Command.getCmdObject(cmdLine);
		((CoordinateConnector) getConnector()).getMQ().addCmd(new Command(getSocket(), cmdObject, null));
	}
}