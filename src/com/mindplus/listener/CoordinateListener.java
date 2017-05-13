package com.mindplus.listener;

import java.net.Socket;

import com.mindplus.command.Command;
import com.mindplus.connection.CoordinateConnector;
import com.mindplus.message.Message;

import SnapShot.SnapShotController;

public class CoordinateListener extends MsgListener {

	public CoordinateListener(CoordinateConnector connetor, Socket socket) {
		super(connetor, socket);
		SnapShotController.getInstance().setConnector(connetor);
	}

	@Override
	protected void handleDisconnect() {
		// TODO what to do?
	}

	@Override
	protected void handleRequest(Message msg) {
		System.out.println("Server: " + msg.toString());

		//TODO refactory later.
		if (SnapShotController.getInstance().isRecording()) {
			if (Command.isMarker(msg.getCMDObj())) {
				if (SnapShotController.getInstance().isCurrentRecord(msg.getCMDObj())) {
					SnapShotController.getInstance().handleMarkerMsg(msg.getCMDObj());
				} else {
					SnapShotController.getInstance().recordMsg(msg.getCMDObj());
				}
			} else {
				SnapShotController.getInstance().recordMsg(msg.getCMDObj());
				((CoordinateConnector) getConnector()).getMQ().addCmd(new Command(getSocket(), msg.getCMDObj(), null));
			}
		} else {
			if (Command.isMarker(msg.getCMDObj())) {
				SnapShotController.getInstance().handleMarkerMsg(msg.getCMDObj());
			}else {
				((CoordinateConnector) getConnector()).getMQ().addCmd(new Command(getSocket(), msg.getCMDObj(), null));
			}
		}
	}
}