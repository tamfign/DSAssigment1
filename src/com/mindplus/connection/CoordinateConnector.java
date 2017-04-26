package com.mindplus.connection;

import java.io.IOException;
import java.net.Socket;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import com.mindplus.command.Command;
import com.mindplus.command.CoordinateCmdHandler;
import com.mindplus.command.ServerServerCmd;
import com.mindplus.configuration.Configuration;
import com.mindplus.configuration.ServerConfig;
import com.mindplus.listener.MsgListener;
import com.mindplus.message.Message;
import com.mindplus.listener.CoordinateListener;
import com.mindplus.messagequeue.MessageQueue;
import com.mindplus.model.ChatRoomListController;
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

	public boolean broadcastAndGetResult(JSONObject cmd) {
		return broadcastAndGetResult(cmd, true);
	}

	public void broadcast(JSONObject cmd) {
		broadcastAndGetResult(cmd, false);
	}

	private boolean broadcastAndGetResult(JSONObject cmd, boolean needResult) {
		SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
		SSLSocket socket = null;
		boolean ret = true;

		for (ServerConfig server : ServerListController.getInstance().getList()) {
			try {
				socket = (SSLSocket) factory.createSocket(server.getHost(), server.getCoordinationPort());
				if (socket.isConnected()) {
					write(socket, new Message(cmd));
					if (needResult)
						ret &= readResult(socket);
				}
			} catch (Exception e) {
				// e.printStackTrace();
			} finally {
				close(socket);
			}
		}
		return ret;
	}

	private boolean readResult(Socket socket) throws ParseException, IOException {
		boolean ret = false;
		Message cmd = readCmd(socket);

		// If read nothing back, consider it's false.
		if (cmd != null) {
			ret = Command.getResult(cmd.getCMDObj());
		}
		return ret;
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
			} else {
				close(another);
			}
		} catch (Exception e) {
			System.out.println("Fail to connect server-" + server.getId());
		}
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

	public JSONObject requestRouter(JSONObject cmd, boolean needResponse) {
		return getController().requestRouter(cmd, needResponse);
	}

	public void updateChatRoomList() {
		System.out.println("Begin to refresh existing chat room list.");
		SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
		SSLSocket socket = null;

		for (ServerConfig server : ServerListController.getInstance().getList()) {
			try {
				socket = (SSLSocket) factory.createSocket(server.getHost(), server.getCoordinationPort());
				write(socket, new Message(ServerServerCmd.getRoomListStreamRq()));
				updateChatRoomList(readCmd(socket));
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				close(socket);
			}
		}
	}

	private void updateChatRoomList(Message cmd) {
		if (cmd == null)
			return;
		System.out.println("Updating chat room list: " + cmd);
		JSONObject obj = cmd.getCMDObj();
		if (obj != null && Command.isRoomLisStream(obj)) {
			ChatRoomListController.getInstance().addRooms(Command.getRooms(obj));
		}
	}
}