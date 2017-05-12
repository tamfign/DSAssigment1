package com.mindplus.model;

import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class ChatRoom {
	private String name = null;
	private String owner = null;
	private String serverId = null;
	private ArrayList<String> members = null;

	public ChatRoom(String chatRoom, String serverId, String owner) {
		this.name = chatRoom;
		this.serverId = serverId;
		this.owner = owner;
		this.members = new ArrayList<String>();
	}

	public ChatRoom(JSONObject obj) {
		this.name = (String) obj.get("name");
		this.serverId = (String) obj.get("serverId");

		String owner = (String) obj.get("owner");
		if (owner != null)
			this.owner = owner;

		this.members = new ArrayList<String>();
		JSONArray jList = (JSONArray) obj.get("members");
		for (int i = 0; i < jList.size(); i++) {
			this.members.add((String) jList.get(i));
		}
	}

	public ChatRoom(String stream) {
		String[] args = stream.split("\\|");
		this.name = args[0];
		this.serverId = args[1];
	}

	public String getName() {
		return name;
	}

	public String getServerId() {
		return serverId;
	}

	public String getOwner() {
		return owner;
	}

	protected void addMember(String identity) {
		this.members.add(identity);
	}

	protected void addMembers(ArrayList<String> members) {
		this.members.addAll(members);
	}

	protected void removeMember(String identity) {
		this.members.remove(identity);
	}

	public ArrayList<String> getMemberList() {
		return this.members;
	}

	public String toString() {
		return this.name + "|" + this.serverId;
	}

	@SuppressWarnings("unchecked")
	public JSONObject toJSON() {
		JSONObject ret = new JSONObject();
		ret.put("name", this.name);
		ret.put("serverId", this.serverId);
		ret.put("owner", this.owner);

		JSONArray jList = new JSONArray();
		jList.addAll(this.members);
		ret.put("members", jList);

		return ret;
	}
}
