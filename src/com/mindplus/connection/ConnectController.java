package com.mindplus.connection;

import org.json.simple.JSONObject;

import com.mindplus.command.Command;

public interface ConnectController {

	public abstract void run() throws Exception;

	public abstract void requestServer(Command cmd);

	public abstract void requestClient(Command cmd);

	public abstract JSONObject requestRouter(JSONObject cmd, boolean needResponse);
}
