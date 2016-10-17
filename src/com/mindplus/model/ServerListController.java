package com.mindplus.model;

import java.util.ArrayList;
import java.util.HashMap;

import com.mindplus.configuration.ServerConfig;

public class ServerListController {
	private ArrayList<ServerConfig> serverList = null;
	private static ServerListController _instance = null;
	private HashMap<String, Integer> serverVolumeMap = null;

	private ServerListController() {
		this.serverList = new ArrayList<ServerConfig>();
		this.serverVolumeMap = new HashMap<String, Integer>();
	}

	public static ServerListController getInstance() {
		if (_instance == null) {
			_instance = new ServerListController();
		}
		return _instance;
	}

	public void updateVolume(String serverId, int volume) {
		synchronized (serverVolumeMap) {
			serverVolumeMap.put(serverId, volume);
		}
	}

	public ServerConfig getBestServer() {
		synchronized (serverVolumeMap) {
			ServerConfig ret = null;
			int min = 0;

			for (ServerConfig server : serverList) {
				if (serverVolumeMap.get(server.getId()) <= min) {
					ret = server;
					min = serverVolumeMap.get(server);
				}
			}
			return ret;
		}
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

	public synchronized boolean isExists(String serverId) {
		return get(serverId) != null;
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
