package com.mindplus.heartbeat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Date;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;

import org.json.simple.JSONObject;

import com.mindplus.command.Command;

public class BeatAccepter implements Runnable {
	private int port;

	public BeatAccepter(int port) {
		this.port = port;
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

	private void acceptBeater(ServerSocket server) {
		Socket socket = null;
		BufferedReader reader = null;

		while (true) {
			try {
				socket = server.accept();
				reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));

				String msg = reader.readLine();
				System.out.println("First connect :" + msg);
				JSONObject comingJsonObj = Command.getCmdObject(msg);
				BeaterList.getInstance().add(Command.getServerId(comingJsonObj), new TimeStampAndCmd(new Date(System.currentTimeMillis()), comingJsonObj));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
