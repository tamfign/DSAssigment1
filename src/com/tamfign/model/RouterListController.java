package com.tamfign.model;

import java.util.ArrayList;

import com.tamfign.configuration.ServerConfig;

public class RouterListController {
	private ArrayList<ServerConfig> routerList = null;
	private static RouterListController _instance = null;

	private RouterListController() {
		this.routerList = new ArrayList<ServerConfig>();
	}

	public static RouterListController getInstance() {
		if (_instance == null) {
			_instance = new RouterListController();
		}
		return _instance;
	}

	public synchronized void addRouter(ServerConfig server) {
		routerList.add(server);
	}

	public int size() {
		return routerList.size();
	}

	public ServerConfig get(int index) {
		return routerList.get(index);
	}

	public ServerConfig get(String serverId) {
		ServerConfig ret = null;
		for (ServerConfig config : routerList) {
			if (serverId != null && serverId.equals(config.getId())) {
				ret = config;
				break;
			}
		}
		return ret;
	}
}
