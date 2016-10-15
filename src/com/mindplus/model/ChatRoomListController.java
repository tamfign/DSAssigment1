package com.mindplus.model;

import java.util.ArrayList;
import java.util.HashMap;

import com.mindplus.configuration.Configuration;

public class ChatRoomListController {
	private HashMap<String, ChatRoom> roomList = null;
	private static ChatRoomListController _instance = null;
	private static final String MAIN_HALL = "MainHall-";

	private ChatRoomListController() {
		this.roomList = new HashMap<String, ChatRoom>();

		// Router does not have chat room.
		if (!Configuration.isRouter())
			addRoom(getLocalMainHall(), Configuration.getServerId(), null);
	}

	public static ChatRoomListController getInstance() {
		if (_instance == null) {
			_instance = new ChatRoomListController();
		}
		return _instance;
	}

	public synchronized String getRoomLocation(String roomId) {
		String ret = null;
		ChatRoom room = roomList.get(roomId);

		if (room != null) {
			ret = room.getServerId();
		}
		return ret;
	}

	public synchronized void addRoom(String roomId, String serverId, String owner) {
		ChatRoom newRoom = new ChatRoom(roomId, serverId, owner);
		newRoom.addMember(owner);
		roomList.put(roomId, newRoom);
	}

	public synchronized void changeRoom(String former, String newRoom, String identity) {
		roomList.get(former).removeMember(identity);
		roomList.get(newRoom).addMember(identity);
	}

	public synchronized void deleteRoom(String roomId) {
		// Router no need to handle memebers
		if (!Configuration.isRouter())
			roomList.get(getLocalMainHall()).addMembers(roomList.get(roomId).getMemberList());
		roomList.remove(roomId);
	}

	public synchronized void removeRoom(String roomId) {
		roomList.remove(roomId);
	}

	public synchronized void removeRoomByServerId(String serverId) {
		if (serverId == null || "".equals(serverId))
			return;

		for (ChatRoom room : roomList.values()) {
			if (serverId.equals(room.getServerId())) {
				roomList.remove(room.getName());
			}
		}
	}

	public synchronized boolean isRoomExists(String roomId) {
		return roomList.containsKey(roomId);
	}

	public synchronized ArrayList<String> getList() {
		ArrayList<String> ret = new ArrayList<String>();
		ret.addAll(roomList.keySet());
		return ret;
	}

	public synchronized ChatRoom getChatRoom(String roomId) {
		return roomList.get(roomId);
	}

	public synchronized void addMember(String roomId, String identity) {
		if (roomList.get(roomId) != null) {
			roomList.get(roomId).addMember(identity);
		}
	}

	public synchronized void removeMember(String roomId, String identity) {
		if (roomList.get(roomId) != null) {
			roomList.get(roomId).removeMember(identity);
		}
	}

	public synchronized ArrayList<String> getMemberList(String roomId) {
		return roomList.get(roomId).getMemberList();
	}

	public static String getLocalMainHall() {
		return MAIN_HALL + Configuration.getServerId();
	}

	public static String getMainHall(String serverId) {
		return MAIN_HALL + serverId;
	}
}