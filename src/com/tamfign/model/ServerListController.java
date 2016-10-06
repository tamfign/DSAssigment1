package com.tamfign.model;

import java.util.ArrayList;

import com.tamfign.configuration.ServerConfig;

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
		for (String stream : list) {
			serverList.add(new ServerConfig(stream));
		}
	}

	public synchronized ArrayList<String> getStringList() {
		ArrayList<String> ret = new ArrayList<String>();

		for (ServerConfig config : serverList) {
			ret.add(config.toString());
		}
		return ret;
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
}
