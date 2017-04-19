package com.mindplus.listener;

import java.net.Socket;

import com.mindplus.command.Command;
import com.mindplus.connection.CoordinateConnector;
import com.mindplus.message.Message;

public class CoordinateListener extends MsgListener {

	public CoordinateListener(CoordinateConnector connetor, Socket socket) {
		super(connetor, socket);
	}

	@Override
	protected void handleDisconnect() {
		// TODO what to do?
	}

	@Override
	protected void handleRequest(String cmdLine) {
		System.out.println("Server: " + cmdLine);
		Message msg = new Message(cmdLine);

		((CoordinateConnector) getConnector()).getMQ().addCmd(new Command(getSocket(), msg.getCMDObj(), null));
	}
}