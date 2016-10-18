package com.mindplus.model;

import java.util.ArrayList;
import java.util.HashMap;

import com.mindplus.configuration.ServerConfig;

public class ServerListController {
	private ArrayList<ServerConfig> serverList = null;
	private static ServerListController _instance = null;
	private HashMap<String, Long> serverVolumeMap = null;

	private ServerListController() {
		this.serverList = new ArrayList<ServerConfig>();
		this.serverVolumeMap = new HashMap<String, Long>();
	}

	public static ServerListController getInstance() {
		if (_instance == null) {
			_instance = new ServerListController();
		}
		return _instance;
	}

	public void updateVolume(String serverId, long volume) {
		synchronized (serverVolumeMap) {
			serverVolumeMap.put(serverId, volume);
		}
	}

	public ServerConfig getBestServer() {
		synchronized (serverVolumeMap) {
			ServerConfig ret = serverList.get(0);
			long min = 0;

			for (ServerConfig server : serverList) {
				if (serverVolumeMap.get(server.getId()) <= min) {
					ret = server;
					min = serverVolumeMap.get(server.getId());
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
			boolean notExist = false;
			ServerConfig newServer = new ServerConfig(stream);

			for (ServerConfig server : serverList) {
				if (server.getId().equals(newServer.getId())) {
					notExist = true;
				}
			}
			if (!notExist) {
				serverList.add(newServer);
			}
		}
	}

	public synchronized void removeServer(String id) {
		if (id == null || "".equals(id))
			return;

		int index = -1;
		for (int i = 0; i < serverList.size(); i++) {
			if (id.equals(serverList.get(i).getId())) {
				index = i;
			}
		}
		if (index >= 0)
			serverList.remove(index);
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
