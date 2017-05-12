package com.mindplus.model;

import org.json.simple.JSONObject;

public class Client {
	private String identity = null;
	private String serverId = null;
	private String roomId = null;
	private String ownRoom = null;

	public Client(String identity, String serverId, String roomId) {
		this.identity = identity;
		this.serverId = serverId;
		this.roomId = roomId;
	}

	public Client(JSONObject obj) {
		this.identity = (String) obj.get("identity");
		this.serverId = (String) obj.get("serverId");
		this.roomId = (String) obj.get("roomId");
	}

	public String getOwnRoom() {
		return ownRoom;
	}

	public void setOwnRoom(String ownRoom) {
		this.ownRoom = ownRoom;
	}

	public String getIdentity() {
		return identity;
	}

	public String getServerId() {
		return serverId;
	}

	public void setRoomId(String roomId) {
		this.roomId = roomId;
	}

	public String getRoomId() {
		return roomId;
	}

	@SuppressWarnings("unchecked")
	public JSONObject toJSON() {
		JSONObject ret = new JSONObject();
		ret.put("identity", this.identity);
		ret.put("serverId", this.serverId);
		ret.put("roomId", this.roomId);

		if (ownRoom != null) {
			ret.put("ownRoom", this.ownRoom);
		}
		return ret;
	}
}
