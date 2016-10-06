package com.tamfign.main;

import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.spi.BooleanOptionHandler;

public class ServerArguments {
	@Option(required = true, name = "-n", usage = "the name of the server")
	private String serverId = null;

	@Option(required = false, handler = BooleanOptionHandler.class, name = "-r", usage = "this is a server")
	private boolean isRouter = false;

	@Option(required = true, name = "-c", usage = "the path to the cert key of servers")
	private String certPath = null;

	@Option(required = false, name = "-u", usage = "the path of user data for router")
	private String userDataPath = null;

	@Option(required = false, name = "-l", usage = "the path of server configuration")
	private String serverConfigPath = null;

	@Option(required = false, name = "-h", aliases = "--host", usage = "router host address")
	private String host = "localhost";

	@Option(required = false, name = "-p", aliases = "--port", usage = "router port number")
	private int port = 4444;

	public String getServerConfigPath() {
		return serverConfigPath;
	}

	public void setServerConfigPath(String serverConfigPath) {
		this.serverConfigPath = serverConfigPath;
	}

	public String getUserDataPath() {
		return userDataPath;
	}

	public void setUserDataPath(String userDataPath) {
		this.userDataPath = userDataPath;
	}

	public String getCertPath() {
		return certPath;
	}

	public void setCertPath(String certPath) {
		this.certPath = certPath;
	}

	public String getServerId() {
		return serverId;
	}

	public void setServerId(String serverId) {
		this.serverId = serverId;
	}

	public boolean isRouter() {
		return isRouter;
	}

	public void setRouter(boolean isRouter) {
		this.isRouter = isRouter;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}
}
