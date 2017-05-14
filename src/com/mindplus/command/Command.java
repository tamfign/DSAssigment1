package com.mindplus.command;

import java.net.Socket;
import java.util.ArrayList;

import org.json.simple.JSONObject;

import com.mindplus.model.ChatRoomListController;

@SuppressWarnings("unchecked")
public class Command {
	protected final static String TYPE = "type";
	protected final static String CMD = "type";
	protected final static String TYPE_NEW_ID = "newidentity";
	protected final static String TYPE_JOIN_SERVER = "join_server";
	protected final static String TYPE_LOCK_ID = "lockidenity";
	protected final static String TYPE_VOTE_ID = "vote_idenity";
	protected final static String TYPE_RELEASE_ID = "releaseidentity";
	protected final static String TYPE_WHO = "who";

	protected final static String TYPE_LIST = "list";
	protected final static String TYPE_ROOM_LIST = "roomlist";
	protected final static String TYPE_CHANGE_ROOM = "roomchange";
	protected final static String TYPE_ROOM_CONTENTS = "roomcontents";
	protected final static String TYPE_CREATE_ROOM = "createroom";
	protected final static String TYPE_LOCK_ROOM = "lockroomid";
	protected final static String TYPE_VOTE_ROOM = "vote_roomid";
	protected final static String TYPE_RELEASE_ROOM = "releaseroomid";
	protected final static String TYPE_JOIN = "join";
	protected final static String TYPE_DELETE_ROOM = "deleteroom";
	protected final static String TYPE_ROUTE = "route";
	protected final static String TYPE_MOVE_JOIN = "movejoin";
	protected final static String TYPE_SERVER_CHANGE = "serverchange";

	protected final static String TYPE_RECONNECT = "reconnect";

	protected final static String TYPE_MESSAGE = "message";
	protected final static String TYPE_QUIT = "quit";

	protected final static String TYPE_SERVER_ON = "server_on";
	protected final static String TYPE_NEW_SERVER = "new_server";

	protected final static String TYPE_HEART_BEAT = "heart_beat";
	protected final static String TYPE_MAINTENANCE = "maintenance";

	protected final static String TYPE_MARKER = "marker";

	public final static String P_UUID = "uuid";
	protected final static String P_IDENTITY = "identity";
	protected final static String P_PWD = "pwd";
	public final static String P_SERVER_ID = "serverid";
	protected final static String P_LOCKED = "locked";
	protected final static String P_APPROVED = "approved";
	protected final static String P_FORMER = "former";
	protected final static String P_ROOM_ID = "roomid";
	protected final static String P_ROOMS = "rooms";
	protected final static String P_IDENTITIES = "identities";
	protected final static String P_OWNER = "owner";
	protected final static String P_HOST = "host";
	protected final static String P_PORT = "port";
	protected final static String P_COORDINATE_PORT = "coordinate_port";
	protected final static String P_CLIENT_PORT = "client_port";
	protected final static String P_SERVER = "server";
	protected final static String P_SERVERS = "servers";
	protected final static String P_CONTENT = "content";
	protected final static String P_CLIENT_VOLUME = "client_volume";

	protected final static String CMD_LOCK_IDENTITY = "CMD_LOCK_IDENTITY";
	protected final static String CMD_RELEASE_IDENTITY = "CMD_RELEASE_IDENTITY";
	protected final static String CMD_LOCK_ROOM = "CMD_LOCK_ROOM";
	protected final static String CMD_RELEASE_ROOM = "CMD_RELEASE_ROOM";
	protected final static String CMD_DELETE_ROOM = "CMD_DELETE_ROOM";
	protected final static String CMD_MARKER = "CMD_MARKER";

	private String owner = null;
	private JSONObject obj = null;
	private Socket socket = null;

	public Command(Socket socket, JSONObject cmd, String owner) {
		this.owner = owner;
		this.socket = socket;
		this.obj = cmd;
	}

	public Command(Command oldCmd, JSONObject obj) {
		this.owner = oldCmd.getOwner();
		this.socket = oldCmd.getSocket();
		this.obj = obj;
	}

	public Socket getSocket() {
		return socket;
	}

	public String getOwner() {
		return owner;
	}

	public JSONObject getObj() {
		return obj;
	}

	public static boolean getResult(JSONObject obj) {
		boolean ret = false;

		switch ((String) obj.get(TYPE)) {
		case (TYPE_VOTE_ID):
		case (TYPE_VOTE_ROOM):
			ret = Boolean.parseBoolean((String) obj.get(P_LOCKED));
			break;
		default:
			ret = Boolean.parseBoolean((String) obj.get(P_APPROVED));
		}
		return ret;
	}

	public static ArrayList<String> getRooms(JSONObject obj) {
		return (ArrayList<String>) obj.get(P_ROOMS);
	}

	public static ArrayList<String> getServers(JSONObject obj) {
		return (ArrayList<String>) obj.get(P_SERVERS);
	}

	public static boolean isNewId(JSONObject obj) {
		String cmdType = (String) obj.get(TYPE);
		return TYPE_MOVE_JOIN.equals(cmdType) || TYPE_JOIN_SERVER.equals(cmdType) || TYPE_RECONNECT.equals(cmdType);
	}

	public static String getNewId(JSONObject obj) {
		return (String) obj.get(P_IDENTITY);
	}

	public static String getServerId(JSONObject obj) {
		return (String) obj.get(P_SERVER_ID);
	}

	public static boolean isClosing(JSONObject obj) {
		String cmdType = (String) obj.get(TYPE);
		return TYPE_QUIT.equals(cmdType);
	}

	public static boolean isMoving(JSONObject obj) {
		boolean ret = false;

		String cmdType = (String) obj.get(TYPE);
		if (TYPE_JOIN.equals(cmdType)) {
			String roomId = (String) obj.get(P_ROOM_ID);
			ret = !ChatRoomListController.getInstance().isRoomExists(roomId);
		}
		return ret;
	}

	public static boolean isMarker(JSONObject obj) {
		String cmdType = (String) obj.get(TYPE);
		return TYPE_MARKER.equals(cmdType);
	}

	public static JSONObject getMaintenance() {
		JSONObject root = new JSONObject();
		root.put(TYPE, TYPE_MAINTENANCE);
		return root;
	}
}
