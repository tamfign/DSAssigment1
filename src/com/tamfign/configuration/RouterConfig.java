package com.tamfign.configuration;

public class RouterConfig extends ServerConfig {
	private boolean isMain = false;

	public boolean isMain() {
		return isMain;
	}

	public void setMain(boolean isMain) {
		this.isMain = isMain;
	}
}
