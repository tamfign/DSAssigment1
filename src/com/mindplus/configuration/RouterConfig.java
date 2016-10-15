package com.mindplus.configuration;

public class RouterConfig extends ServerConfig {
	private boolean isBackUp = false;
	private String userDataPath = null;

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
}
