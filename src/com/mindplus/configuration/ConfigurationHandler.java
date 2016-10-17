package com.mindplus.configuration;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class ConfigurationHandler extends DefaultHandler {
	private static final String SERVER = "server";
	private static final String ROUTER = "router";
	private static final String SERVER_ID = "id";
	private static final String HOST = "host";
	private static final String CLIENT_PORT = "client_port";
	private static final String COODINATE_PORT = "coordinate_port";
	private static final String CER_PATH = "cer_path";
	private static final String CER_PWD = "cer_pwd";
	private static final String USER_DATA = "user_data";
	private static final String IS_BACKUP = "is_backup";
	private static final String HEARTBEAT_PORT = "heartbeat_port";

	private String tag = null;
	private ServerConfig config = null;
	private ServerConfig serverConfig = null;
	private RouterConfig routerConfig = null;

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		String data = new String(ch, start, length);

		if (SERVER_ID.equalsIgnoreCase(this.tag)) {
			this.config.setId(data);
		} else if (HOST.equalsIgnoreCase(this.tag)) {
			this.config.setHost(data);
		} else if (CLIENT_PORT.equalsIgnoreCase(this.tag)) {
			this.config.setClientPort(Integer.parseInt(data));
		} else if (COODINATE_PORT.equalsIgnoreCase(this.tag)) {
			this.config.setCoordinationPort(Integer.parseInt(data));
		} else if (IS_BACKUP.equalsIgnoreCase(this.tag)) {
			((RouterConfig) this.config).setBackUp(Boolean.parseBoolean(data));
		} else if (CER_PATH.equalsIgnoreCase(this.tag)) {
			this.config.setCerPath(data);
		} else if (CER_PWD.equalsIgnoreCase(this.tag)) {
			this.config.setCerPwd(data);
		} else if (USER_DATA.equalsIgnoreCase(this.tag)) {
			((RouterConfig) this.config).setUserDataPath(data);
		} else if (HEARTBEAT_PORT.equalsIgnoreCase(this.tag)) {
			((RouterConfig) this.config).setHeartbeatPort(Integer.parseInt(data));
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if (SERVER.equals(qName)) {
			serverConfig = this.config;
			this.config = null;
		} else if (ROUTER.equals(qName)) {
			routerConfig = (RouterConfig) this.config;
			this.config = null;
		}
		this.tag = null;
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attri) throws SAXException {
		switch (qName) {
		case SERVER:
			this.config = new ServerConfig();
			break;
		case ROUTER:
			this.config = new RouterConfig();
			break;
		case SERVER_ID:
		case HOST:
		case CLIENT_PORT:
		case COODINATE_PORT:
		case CER_PWD:
		case CER_PATH:
		case USER_DATA:
		case IS_BACKUP:
		case HEARTBEAT_PORT:
			this.tag = qName;
			break;
		default:
			this.tag = null;
		}
	}

	public ServerConfig getServerConfig() {
		return this.serverConfig;
	}

	public RouterConfig getRouterConfig() {
		return this.routerConfig;
	}
}
