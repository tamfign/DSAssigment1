package com.mindplus.command;

import org.json.simple.JSONObject;

@SuppressWarnings("unchecked")
public class InternalCmd extends Command {

	public InternalCmd(Command oldCmd, JSONObject obj) {
		super(oldCmd, obj);
	}

	public static Command getInternMarkerCmd(Command oldCmd, String cmd) {
		JSONObject obj = new JSONObject();
		obj.put(CMD, cmd);
		return new InternalCmd(oldCmd, obj);
	}

	public static Command getInternRoomCmd(Command oldCmd, String cmd, String roomId) {
		JSONObject obj = new JSONObject();
		obj.put(CMD, cmd);
		obj.put(P_ROOM_ID, roomId);
		return new InternalCmd(oldCmd, obj);
	}

	public static Command getInternRoomResultCmd(Command oldCmd, String cmd, String roomId, boolean result) {
		JSONObject obj = new JSONObject();
		obj.put(CMD, cmd);
		obj.put(P_ROOM_ID, roomId);
		obj.put(P_APPROVED, result);
		return new InternalCmd(oldCmd, obj);
	}

	public static Command getInternRoomResultCmd(Command oldCmd, String cmd, JSONObject obj) {
		obj.put(CMD, cmd);
		return new InternalCmd(oldCmd, obj);
	}

	public static Command getInternIdCmd(Command oldCmd, String cmd, String identity) {
		JSONObject obj = new JSONObject();
		obj.put(CMD, cmd);
		obj.put(P_IDENTITY, identity);
		return new InternalCmd(oldCmd, obj);
	}

	public static Command getLockIdentityResultCmd(Command oldCmd, boolean approved) {
		JSONObject obj = new JSONObject();
		obj.put(CMD, CMD_LOCK_IDENTITY);
		obj.put(P_IDENTITY, oldCmd.getObj().get(Command.P_IDENTITY));
		obj.put(P_APPROVED, approved);
		return new InternalCmd(oldCmd, obj);
	}

	public static Command getLockRoomResultCmd(Command oldCmd, String roomId, boolean approved) {
		JSONObject obj = new JSONObject();
		obj.put(CMD, CMD_LOCK_ROOM);
		obj.put(P_ROOM_ID, roomId);
		obj.put(P_APPROVED, approved);
		return new InternalCmd(oldCmd, obj);
	}
}
