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

	public synchronized void addIndentity(String id, String serverId, String roomId) {
		clientList.put(id, new Client(id, serverId, roomId));
	}

	public synchronized void removeIndentity(String id) {
		clientList.remove(id);
	}

	public synchronized boolean isIdentityExist(String id) {
		return clientList.containsKey(id);
	}

	public synchronized Client getClient(String id) {
		return clientList.get(id);
	}

	public synchronized void releaseId(String serverId, String id) {
		// Release only if it's the same as the lock one
		if (serverId != null && serverId.equals(clientList.get(id).getServerId())) {
			clientList.remove(id);
		}
	}

	public synchronized long size() {
		return clientList.size();
	}
}
