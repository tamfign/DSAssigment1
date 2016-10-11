package com.mindplus.command;

import java.net.Socket;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.mindplus.configuration.Configuration;
import com.mindplus.security.ServerVerification;

@SuppressWarnings("unchecked")
public class ServerServerCmd extends Command {

	public ServerServerCmd(Socket socket, JSONObject cmd, String owner) {
		super(socket, cmd, owner);
	}

	public static String lockRoomRq(String serverId, String roomId) {
		JSONObject root = new JSONObject();
		root.put(TYPE, TYPE_LOCK_ROOM);
		root.put(P_SERVER_ID, serverId);
		root.put(P_ROOM_ID, roomId);
		return root.toJSONString();
	}

	public static String lockRoomRs(String serverId, String roomId, boolean result) {
		JSONObject root = new JSONObject();
		root.put(TYPE, TYPE_LOCK_ROOM);
		root.put(P_SERVER_ID, serverId);
		root.put(P_ROOM_ID, roomId);
		root.put(P_LOCKED, Boolean.toString(result));
		return root.toJSONString();
	}

	public static String joinRoomRq(String roomId) {
		JSONObject root = new JSONObject();
		root.put(TYPE, TYPE_JOIN);
		root.put(P_ROOM_ID, roomId);
		return root.toJSONString();
	}

	public static String moveJoinRq(String former, String roomId, String id) {
		JSONObject root = new JSONObject();
		root.put(TYPE, TYPE_MOVE_JOIN);
		root.put(P_FORMER, former);
		root.put(P_ROOM_ID, roomId);
		root.put(P_IDENTITY, id);
		return root.toJSONString();
	}

	public static String deleteRoomRq(String roomId) {
		JSONObject root = new JSONObject();
		root.put(TYPE, TYPE_DELETE_ROOM);
		root.put(P_ROOM_ID, roomId);
		return root.toJSONString();
	}

	public static String deleteRoomBc(String serverId, String roomId) {
		JSONObject root = new JSONObject();
		root.put(TYPE, TYPE_DELETE_ROOM);
		root.put(P_SERVER_ID, serverId);
		root.put(P_ROOM_ID, roomId);
		return root.toJSONString();
	}

	public static String releaseRoom(String serverId, String roomId, boolean result) {
		JSONObject root = new JSONObject();
		root.put(TYPE, TYPE_RELEASE_ROOM);
		root.put(P_SERVER_ID, serverId);
		root.put(P_ROOM_ID, roomId);
		root.put(P_APPROVED, Boolean.toString(result));
		return root.toJSONString();
	}

	public static String lockIdentityRq(String serverId, String identity) {
		JSONObject root = new JSONObject();
		root.put(TYPE, TYPE_LOCK_ID);
		root.put(P_SERVER_ID, serverId);
		root.put(P_IDENTITY, identity);
		return root.toJSONString();
	}

	public static String lockIdentityRs(String serverId, String identity, boolean result) {
		JSONObject root = new JSONObject();
		root.put(TYPE, TYPE_LOCK_ID);
		root.put(P_SERVER_ID, serverId);
		root.put(P_IDENTITY, identity);
		root.put(P_LOCKED, Boolean.toString(result));
		return root.toJSONString();
	}

	public static String releaseIdentityRq(String serverId, String identity) {
		JSONObject root = new JSONObject();
		root.put(TYPE, TYPE_RELEASE_ID);
		root.put(P_SERVER_ID, serverId);
		root.put(P_IDENTITY, identity);
		return root.toJSONString();
	}

	public static String getServerOnCmd() {
		JSONObject obj = new JSONObject();
		obj.put(TYPE, TYPE_SERVER_ON);
		obj.put(P_SERVER_ID, Configuration.getServerId());
		obj.put(P_SERVER, Configuration.getConfig().toString());
		return obj.toJSONString();
	}

	public static String getNewServerCmd() {
		JSONObject obj = new JSONObject();
		obj.put(TYPE, TYPE_NEW_SERVER);
		obj.put(P_SERVER_ID, Configuration.getServerId());
		obj.put(P_PWD, ServerVerification.getInstance().sign(SERVER_AGREEMENT));
		obj.put(P_HOST, Configuration.getHost());
		obj.put(P_COORDINATE_PORT, String.valueOf(Configuration.getCoordinationPort()));
		obj.put(P_CLIENT_PORT, String.valueOf(Configuration.getClientPort()));
		return obj.toJSONString();
	}

	public static String newServerRs(String serverId, boolean result, ArrayList<String> serverList) {
		JSONObject root = new JSONObject();
		root.put(TYPE, TYPE_NEW_SERVER);
		root.put(P_APPROVED, Boolean.toString(result));

		if (result) {
			JSONArray jList = new JSONArray();
			jList.addAll(serverList);
			root.put(P_SERVERS, jList);
		}
		return root.toJSONString();
	}
}
