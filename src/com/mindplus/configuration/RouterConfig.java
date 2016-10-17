package com.mindplus.configuration;

public class RouterConfig extends ServerConfig {
	private boolean isBackUp = false;
	private String userDataPath = null;
	private int heartbeatPort;
	private int routerBackupPort;

	public int getHeartbeatPort() {
		return heartbeatPort;
	}

	public void setHeartbeatPort(int heartbeatPort) {
		this.heartbeatPort = heartbeatPort;
	}

	public String getUserDataPath() {
		return userDataPath;
	}

	public void setUserDataPath(String userDataPath) {
		this.userDataPath = userDataPath;
	}

	public boolean isBackUp() {
		return isBackUp;
	}

	public void setBackUp(boolean isMain) {
		this.isBackUp = isMain;
	}

	public int getRouterBackupPort() {
		return routerBackupPort;
	}

	public void setRouterBackupPort(int routerBackupPort) {
		this.routerBackupPort = routerBackupPort;
	}
}
