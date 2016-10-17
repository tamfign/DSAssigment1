package com.mindplus.heartbeat;

import java.util.Date;

import org.json.simple.JSONObject;

class TimeStampAndCmd {
	private Date timestamp;
	private JSONObject cmd;

	public TimeStampAndCmd(Date timestamp, JSONObject cmd) {
		this.cmd = cmd;
		this.timestamp = timestamp;
	}

	Date getTimeStamp() {
		return this.timestamp;
	}

	JSONObject getCmd() {
		return this.cmd;
	}
}
