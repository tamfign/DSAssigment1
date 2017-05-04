package com.mindplus.connection;

import java.io.IOException;
import java.net.Socket;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import org.json.simple.JSONObject;

import com.mindplus.command.Command;
import com.mindplus.command.CoordinateCmdHandler;
import com.mindplus.command.ServerServerCmd;
import com.mindplus.configuration.Configuration;
import com.mindplus.configuration.ServerConfig;
import com.mindplus.listener.MsgListener;
import com.mindplus.message.Message;
import com.mindplus.listener.CoordinateListener;
import com.mindplus.messagequeue.MessageQueue;
import com.mindplus.model.ServerListController;

public class CoordinateConnector extends Connector implements Runnable {
	private MessageQueue serverMQ = new MessageQueue(new CoordinateCmdHandler(this));

	protected CoordinateConnector(ConnectController controller) {
		super(controller);
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

	public void broadcast(JSONObject cmd) {
		SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
		SSLSocket socket = null;

		for (ServerConfig server : ServerListController.getInstance().getList()) {
			try {
				socket = (SSLSocket) factory.createSocket(server.getHost(), server.getCoordinationPort());
				if (socket.isConnected()) {
					write(socket, new Message(cmd));
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				close(socket);
			}
		}
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
					sendOutRooms(another);
				}
			} else {
				close(another);
			}
		} catch (Exception e) {
			System.out.println("Fail to connect server-" + server.getId());
		}
	}

	public void shortResponse(String serverId, JSONObject cmd) {
		SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
		SSLSocket socket = null;
		ServerConfig server = ServerListController.getInstance().get(serverId);

		if (server != null) {
			try {
				socket = (SSLSocket) factory.createSocket(server.getHost(), server.getCoordinationPort());
				if (socket.isConnected()) {
					write(socket, new Message(cmd));
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				close(socket);
			}
		}
	}

	private void sendOutRooms(Socket socket) throws IOException {
		if (socket == null || socket.isClosed())
			return;

		write(socket, new Message(ServerServerCmd.getRoomListCmd()));
	}

	private void sendOutOwnId(Socket socket) throws IOException {
		if (socket == null || socket.isClosed())
			return;

		write(socket, new Message(ServerServerCmd.getServerOnCmd()));
	}

	@Override
	protected MsgListener getListener(Socket socket) {
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