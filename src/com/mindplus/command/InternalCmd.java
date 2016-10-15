package com.mindplus.command;

import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

@SuppressWarnings("unchecked")
public class InternalCmd extends Command {
	public InternalCmd(Command oldCmd, JSONObject obj) {
		super(oldCmd, obj);
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

	public static Command getRoomListCmdRq(Command oldCmd) {
		JSONObject obj = new JSONObject();
		obj.put(CMD, CMD_ROOM_LIST);
		return new InternalCmd(oldCmd, obj);
	}

	public static Command getRoomListCmdRs(Command oldCmd, ArrayList<String> roomList) {
		JSONArray jRoomList = new JSONArray();
		JSONObject obj = new JSONObject();
		obj.put(CMD, CMD_ROOM_LIST);
		jRoomList.addAll(roomList);
		obj.put(P_ROOMS, jRoomList);
		return new InternalCmd(oldCmd, obj);
	}
}
