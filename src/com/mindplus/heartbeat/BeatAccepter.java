package com.mindplus.heartbeat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Date;
import java.util.HashMap;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

import org.json.simple.JSONObject;

import com.mindplus.command.Command;

public class BeatAccepter implements Runnable {
	private HashMap<String, SocketStamp> socketMap;
	private int port;

	public BeatAccepter(int port, HashMap<String, SocketStamp> socketMap) {
		this.port = port;
		this.socketMap = socketMap;
	}

	@Override
	public void run() {
		SSLServerSocketFactory factory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
		SSLServerSocket server;
		try {
			server = (SSLServerSocket) factory.createServerSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		acceptBeater(server);
	}

	private void acceptBeater(SSLServerSocket server) {
		SSLSocket socket = null;
		BufferedReader reader = null;

		while (true) {
			try {
				socket = (SSLSocket) server.accept();
				reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));

				JSONObject comingJsonObj = Command.getCmdObject(reader.readLine());
				socketMap.put(Command.getServerId(comingJsonObj),
						new SocketStamp(socket, new Date(System.currentTimeMillis())));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
