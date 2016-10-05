package com.tamfign.main;

import org.kohsuke.args4j.Option;

public class ServerArguments {
	@Option(required = true, name = "-n", usage = "the name of the server")
	private String serverId = null;

	@Option(required = true, name = "-l", usage = "the path to the configuration of servers")
	private String serverConfigPath = null;

	@Option(required = true, name = "-c", usage = "the path to the cert key of servers")
	private String certPath = null;

	@Option(required = true, name = "-u", usage = "the path of user data")
	private String userDataPath = null;

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

	public String getServerConfigPath() {
		return serverConfigPath;
	}

	public void setServerConfigPath(String serverConfigPath) {
		this.serverConfigPath = serverConfigPath;
	}
}
