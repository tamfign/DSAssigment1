package com.mindplus.model;

import java.util.HashMap;

public class ClientListController {
	private HashMap<String, Client> clientList = null;
	private static ClientListController _instance = null;

	private ClientListController() {
		this.clientList = new HashMap<String, Client>();
	}

	public static ClientListController getInstance() {
		if (_instance == null) {
			_instance = new ClientListController();
		}
		return _instance;
	}

	public void addIndentity(String id, String serverId, String roomId) {
		synchronized (this) {
			clientList.put(id.toLowerCase(), new Client(id, serverId, roomId));
		}
	}

	public void removeIndentity(String id) {
		synchronized (this) {
			clientList.remove(id.toLowerCase());
		}
	}

	public boolean isIdentityExist(String id) {
		synchronized (this) {
			return clientList.containsKey(id.toLowerCase());
		}
	}

	public Client getClient(String id) {
		synchronized (this) {
			return clientList.get(id.toLowerCase());
		}
	}

	public void releaseId(String serverId, String id) {
		synchronized (this) {
			// Release only if it's the same as the lock one
			if (serverId != null && serverId.equalsIgnoreCase(clientList.get(id.toLowerCase()).getServerId())) {
				clientList.remove(id.toLowerCase());
			}
		}
	}
}
