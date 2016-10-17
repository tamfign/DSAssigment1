package com.mindplus.heartbeat;

import java.net.Socket;
import java.util.Date;

class SocketStamp {
	private Socket socket;
	private Date timestamp;

	public SocketStamp(Socket socket, Date timestamp) {
		this.socket = socket;
		this.timestamp = timestamp;
	}

	Date getTimeStamp() {
		return this.timestamp;
	}

	void setTimeStamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	Socket getSocket() {
		return this.socket;
	}
}
