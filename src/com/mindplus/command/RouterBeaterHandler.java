package com.mindplus.command;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import org.json.simple.JSONObject;

import com.mindplus.configuration.ServerConfig;
import com.mindplus.heartbeat.Callback;
import com.mindplus.model.ChatRoomListController;
import com.mindplus.model.ServerListController;

public class RouterBeaterHandler implements Callback {

	@Override
	public void reportDown(Object obj) {
		System.out.println("====Main Router is DOWN====");
		takeover();
	}

	@Override
	public void update(Object obj) {
		ServerListController.getInstance().addServers(Command.getServers((JSONObject) obj));
	}

	private void takeover() {
		System.out.println("Backup Router begins to takeover...");
		updateChatRoomList();
	}

	public void updateChatRoomList() {
		System.out.println("Begin to refresh existing chat room list.");
		SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
		SSLSocket socket = null;

		for (ServerConfig server : ServerListController.getInstance().getList()) {
			try {
				socket = (SSLSocket) factory.createSocket(server.getHost(), server.getCoordinationPort());
				write(socket, ServerServerCmd.getRoomListStreamRq());
				updateChatRoomList(readCmd(socket));
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				close(socket);
			}
		}
	}

	private void updateChatRoomList(String cmd) {
		if (cmd == null || "".equals(cmd))
			return;
		JSONObject obj = Command.getCmdObject(cmd);
		if (obj != null && Command.isRoomLisStream(obj)) {
			ChatRoomListController.getInstance().addRooms(Command.getRooms(obj));
		}
	}

	// TODO refractory
	public String readCmd(Socket socket) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		return br.readLine();
	}

	public void write(Socket socket, String cmd) {
		if (!socket.isConnected()) {
			System.out.println("Fail to send [Socket is closed].");
			return;
		}

		try {
			System.out.println("Sending: " + cmd);
			PrintWriter os = new PrintWriter(socket.getOutputStream());
			os.println(cmd);
			os.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void close(Socket socket) {
		if (socket != null) {
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
