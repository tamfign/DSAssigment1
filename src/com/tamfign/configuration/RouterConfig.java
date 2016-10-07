package com.tamfign.configuration;

public class RouterConfig extends ServerConfig {
	private boolean isMain = false;
	private String userDataPath = null;

	public String getUserDataPath() {
		return userDataPath;
	}

	public void setUserDataPath(String userDataPath) {
		this.userDataPath = userDataPath;
	}

	public boolean isMain() {
		return isMain;
	}

	public void setMain(boolean isMain) {
		this.isMain = isMain;
	}
}
