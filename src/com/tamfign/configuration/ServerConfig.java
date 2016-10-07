package com.tamfign.configuration;

public class ServerConfig {
	private String id = null;
	private String host = null;
	private int clientPort = 0;
	private int coordinationPort = 0;
	private boolean isItselft = false;
	private boolean isActived = false;
	private String cerPath = null;
	private String cerPwd = null;

	public ServerConfig() {
	};

	public ServerConfig(String stream) {
		String[] args = stream.split("\\|");
		this.id = args[0];
		this.host = args[1];
		this.coordinationPort = Integer.parseInt(args[2]);
		this.clientPort = Integer.parseInt(args[3]);
	}

	public ServerConfig(String id, String host, String coordinationPort, String clientPort) {
		this.id = id;
		this.host = host;
		this.coordinationPort = Integer.parseInt(coordinationPort);
		this.clientPort = Integer.parseInt(clientPort);
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public void setClientPort(int clientPort) {
		this.clientPort = clientPort;
	}

	public void setCoordinationPort(int coordinationPort) {
		this.coordinationPort = coordinationPort;
	}

	public boolean isItselft() {
		return isItselft;
	}

	public void setItselft(boolean isItselft) {
		this.isItselft = isItselft;
	}

	public String getId() {
		return id;
	}

	public String getHost() {
		return host;
	}

	public int getClientPort() {
		return clientPort;
	}

	public int getCoordinationPort() {
		return coordinationPort;
	}

	public boolean isActived() {
		return isActived;
	}

	public void setActived(boolean isActived) {
		this.isActived = isActived;
	}

	public String getCerPath() {
		return cerPath;
	}

	public void setCerPath(String cerPath) {
		this.cerPath = cerPath;
	}

	public String getCerPwd() {
		return cerPwd;
	}

	public void setCerPwd(String cerPwd) {
		this.cerPwd = cerPwd;
	}

	public String toString() {
		return id + "|" + host + "|" + coordinationPort + "|" + clientPort;
	}
}
