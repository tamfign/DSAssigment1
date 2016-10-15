package com.mindplus.model;

import java.util.ArrayList;

import com.mindplus.configuration.ServerConfig;

public class ServerListController {
	private ArrayList<ServerConfig> serverList = null;
	private static ServerListController _instance = null;

	private ServerListController() {
		this.serverList = new ArrayList<ServerConfig>();
	}

	public static ServerListController getInstance() {
		if (_instance == null) {
			_instance = new ServerListController();
		}
		return _instance;
	}

	public synchronized void addServer(ServerConfig server) {
		serverList.add(server);
	}

	public synchronized void addServers(ArrayList<String> list) {
		if (list == null)
			return;

		for (String stream : list) {
			serverList.add(new ServerConfig(stream));
		}
	}

	public synchronized void removeServer(String id) {
		if (id == null || "".equals(id))
			return;

		for (ServerConfig server : serverList) {
			if (id.equals(server.getId())) {
				System.out.println("Delete server");
				serverList.remove(server);
			}
		}
	}

	public synchronized ArrayList<String> getStringList() {
		ArrayList<String> ret = new ArrayList<String>();

		for (ServerConfig config : serverList) {
			ret.add(config.toString());
		}
		return ret;
	}

	public synchronized ArrayList<ServerConfig> getList() {
		return this.serverList;
	}

	public ServerConfig get(String serverId) {
		ServerConfig ret = null;
		for (ServerConfig config : serverList) {
			if (serverId != null && serverId.equals(config.getId())) {
				ret = config;
				break;
			}
		}
		return ret;
	}

	public synchronized int size() {
		return this.serverList.size();
	}
}
