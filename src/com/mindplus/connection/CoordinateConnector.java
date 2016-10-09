package com.mindplus.connection;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import com.mindplus.command.Command;
import com.mindplus.command.CoordinateCmdHandler;
import com.mindplus.command.ServerServerCmd;
import com.mindplus.configuration.Configuration;
import com.mindplus.configuration.ServerConfig;
import com.mindplus.listener.CommandListener;
import com.mindplus.listener.CoordinateListener;
import com.mindplus.messagequeue.MessageQueue;
import com.mindplus.model.ChatRoomListController;
import com.mindplus.model.ServerListController;

public class CoordinateConnector extends Connector implements Runnable {
	private static HashMap<String, Socket> serverList = null;
	private MessageQueue serverMQ = new MessageQueue(new CoordinateCmdHandler(this));

	protected CoordinateConnector(ConnectController controller) {
		super(controller);
		serverList = new HashMap<String, Socket>();
	}

	public void run() {
		new Thread(serverMQ).start();

		try {
			if (Configuration.isRouter()) {
				System.out.println("Start Accepting Servers");
			} else {
				System.out.println("Server " + Configuration.getServerId() + " is On.");
			}
			keepListenPortAndAcceptMultiClient(Configuration.getCoordinationPort());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean broadcastAndGetResult(String cmd) {
		return broadcastAndGetResult(new ArrayList<Socket>(serverList.values()), cmd);
	}

	public void broadcast(String cmd) {
		broadcast(new ArrayList<Socket>(serverList.values()), cmd);
	}

	public void checkOtherServers() {
		for (ServerConfig server : ServerListController.getInstance().getList()) {
			tryConnectServer(server, true);
		}
	}

	public void tryConnectServer(ServerConfig server, boolean isNewServer) {
		SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
		SSLSocket another = null;
		try {
			another = (SSLSocket) factory.createSocket(server.getHost(), server.getCoordinationPort());
			if (another.isConnected()) {
				if (isNewServer) {
					sendOutOwnId(another);
				} else {
					ServerListController.getInstance().addServer(server);
				}
				addBroadcastList(server.getId(), another);
				ChatRoomListController.getInstance().addRoom(ChatRoomListController.getMainHall(server.getId()),
						server.getId(), null);
			}
		} catch (Exception e) {
			System.out.println("Fail to connect server-" + server.getId());
		} finally {
			close(another);
		}
	}

	private void sendOutOwnId(Socket socket) throws IOException {
		if (socket == null || socket.isClosed())
			return;
		write(socket, ServerServerCmd.getServerOnCmd());
	}

	public void addBroadcastList(String serverId, Socket socket) {
		synchronized (serverList) {
			serverList.put(serverId, socket);
		}
	}

	public void removeBroadcastList(String id) {
		synchronized (serverList) {
			serverList.remove(id);
		}
	}

	@Override
	protected CommandListener getListener(Socket socket) {
		return new CoordinateListener(this, socket);
	}

	public void runInternalRequest(Command cmd) {
		this.serverMQ.addCmd(cmd);
	}

	public MessageQueue getMQ() {
		return this.serverMQ;
	}

	public void requestTheOther(Command command) {
		getController().requestClient(command);
	}
}