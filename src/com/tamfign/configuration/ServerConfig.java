package com.tamfign.configuration;


public class ServerConfig {
	private String id = null;
	private String host = null;
	private int clientPort = 0;
	private int coordinationPort = 0;
	private boolean isItselft = false;
	private boolean isActived = false;

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
}
