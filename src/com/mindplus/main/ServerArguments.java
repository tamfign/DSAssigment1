package com.mindplus.main;

import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.spi.BooleanOptionHandler;

public class ServerArguments {

	@Option(required = false, handler = BooleanOptionHandler.class, name = "-r", usage = "this is a server")
	private boolean isRouter = false;

	@Option(required = false, name = "-l", usage = "the path of server configuration")
	private String serverConfigPath = null;

	public String getServerConfigPath() {
		return serverConfigPath;
	}

	public void setServerConfigPath(String serverConfigPath) {
		this.serverConfigPath = serverConfigPath;
	}

	public boolean isRouter() {
		return isRouter;
	}

	public void setRouter(boolean isRouter) {
		this.isRouter = isRouter;
	}
}
