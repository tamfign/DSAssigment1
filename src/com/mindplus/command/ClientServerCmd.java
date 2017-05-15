package com.mindplus.command;

import java.net.Socket;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.mindplus.security.UserVerification;

@SuppressWarnings("unchecked")
public class ClientServerCmd extends Command {

	public ClientServerCmd(Socket socket, JSONObject cmd, String owner) {
		super(socket, cmd, owner);
	}

	public static JSONObject joinServerRs(String identity, boolean result) {
		JSONObject root = new JSONObject();
		root.put(TYPE, TYPE_JOIN_SERVER);
		root.put(P_APPROVED, Boolean.toString(result));
		return root;
	}

	public static JSONObject createRoomRs(String roomId, boolean result) {
		JSONObject root = new JSONObject();
		root.put(TYPE, TYPE_CREATE_ROOM);
		root.put(P_ROOM_ID, roomId);
		root.put(P_APPROVED, Boolean.toString(result));
		return root;
	}

	public static JSONObject deleteRoomRs(String roomId, boolean result) {
		JSONObject root = new JSONObject();
		root.put(TYPE, TYPE_DELETE_ROOM);
		root.put(P_ROOM_ID, roomId);
		root.put(P_APPROVED, Boolean.toString(result));
		return root;
	}

	public static JSONObject roomChangeRq(String identity, String former, String newRoom) {
		JSONObject root = new JSONObject();
		root.put(TYPE, TYPE_CHANGE_ROOM);
		root.put(P_IDENTITY, identity);
		root.put(P_FORMER, former);
		root.put(P_ROOM_ID, newRoom);
		return root;
	}

	public static JSONObject serverChangeRs(boolean result, String serverId) {
		JSONObject root = new JSONObject();
		root.put(TYPE, TYPE_SERVER_CHANGE);
		root.put(P_APPROVED, Boolean.toString(result));
		root.put(P_SERVER_ID, serverId);
		return root;
	}

	public static JSONObject listRs(ArrayList<String> arrayList) {
		JSONObject root = new JSONObject();
		JSONArray jRoomList = new JSONArray();
		root.put(TYPE, TYPE_ROOM_LIST);
		jRoomList.addAll(arrayList);
		root.put(P_ROOMS, jRoomList);
		return root;
	}

	public static JSONObject whoRs(String roomId, ArrayList<String> idList, String owner) {
		JSONObject root = new JSONObject();
		JSONArray jList = new JSONArray();

		for (String member : idList) {
			if (member != null) {
				jList.add(member);
			}
		}

		root.put(TYPE, TYPE_ROOM_CONTENTS);
		root.put(P_ROOM_ID, roomId);
		root.put(P_IDENTITIES, jList);
		root.put(P_OWNER, owner == null ? "" : owner);
		return root;
	}

	public static JSONObject messageCmd(String id, String content) {
		JSONObject root = new JSONObject();
		root.put(TYPE, TYPE_MESSAGE);
		root.put(P_IDENTITY, id);
		root.put(P_CONTENT, content);
		return root;
	}

	public static JSONObject routeRq(String roomId, String host, int port) {
		JSONObject root = new JSONObject();
		root.put(TYPE, TYPE_ROUTE);
		root.put(P_ROOM_ID, roomId);
		root.put(P_HOST, host);
		root.put(P_PORT, String.valueOf(port));
		return root;
	}

	public static JSONObject newIdentityRq(boolean result, String serverId, String host, int port) {
		JSONObject root = new JSONObject();
		root.put(TYPE, TYPE_NEW_ID);
		root.put(P_APPROVED, Boolean.toString(result));
		if (result) {
			root.put(P_SERVER_ID, serverId);
			root.put(P_HOST, host);
			root.put(P_PORT, String.valueOf(port));
			root.put(P_PWD, UserVerification.getInstance().getTicket());
		}
		return root;
	}

	public static JSONObject quit() {
		JSONObject root = new JSONObject();
		root.put(TYPE, TYPE_QUIT);
		return root;
	}
}
