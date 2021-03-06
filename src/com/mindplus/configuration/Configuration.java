package com.mindplus.configuration;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

import com.mindplus.main.ServerArguments;

public class Configuration {

	private static Configuration _instance = null;
	private ServerConfig itself = null;

	private boolean isRouter = false;
	private RouterConfig router = null;

	private Configuration(ServerArguments arguments) throws IOException {
		ConfigurationHandler configHandler = getConfigHandler(arguments);

		isRouter = arguments.isRouter();
		if (!isRouter) {
			itself = configHandler.getServerConfig();
			router = configHandler.getRouterConfig();
		} else {
			itself = configHandler.getRouterConfig();
		}

		System.setProperty("javax.net.ssl.trustStore", itself.getCerPath());
		System.setProperty("javax.net.ssl.keyStore", itself.getCerPath());
		System.setProperty("javax.net.ssl.keyStorePassword", itself.getCerPwd());
	}

	private ConfigurationHandler getConfigHandler(ServerArguments args) throws IOException {
		ConfigurationHandler configHandler = new ConfigurationHandler();
		SAXParser parser = null;

		try {
			parser = SAXParserFactory.newInstance().newSAXParser();
			InputStream is = new FileInputStream(args.getServerConfigPath());
			parser.parse(is, configHandler);
		} catch (ParserConfigurationException | SAXException e) {
			e.printStackTrace();
		}
		return configHandler;
	}

	public static boolean isRouter() {
		return _instance.isRouter;
	}

	public static ServerConfig getConfig() {
		return _instance.itself;
	}

	public static String getServerId() {
		return _instance.itself.getId();
	}

	public static String getHost() {
		return _instance.itself.getHost();
	}

	public static int getClientPort() {
		return _instance.itself.getClientPort();
	}

	public static int getCoordinationPort() {
		return _instance.itself.getCoordinationPort();
	}

	public static int getHeartbeatPort() {
		return ((RouterConfig) _instance.itself).getHeartbeatPort();
	}

	public static int getRouterBackupPort() {
		return ((RouterConfig) _instance.itself).getRouterBackupPort();
	}

	public static Configuration init(ServerArguments arguments) throws IOException {
		if (_instance == null) {
			_instance = new Configuration(arguments);
		}
		return _instance;
	}

	public static String getUserDataPath() {
		return ((RouterConfig) _instance.itself).getUserDataPath();
	}

	public static RouterConfig getRouterConfig() {
		return _instance.router;
	}
}
