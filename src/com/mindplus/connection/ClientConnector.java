package com.mindplus.connection;

import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import com.mindplus.command.ClientCmdHandler;
import com.mindplus.command.Command;
import com.mindplus.configuration.Configuration;
import com.mindplus.listener.ClientListener;
import com.mindplus.listener.CommandListener;
import com.mindplus.messagequeue.MessageQueue;
import com.mindplus.model.ChatRoomListController;

public class ClientConnector extends Connector implements Runnable {
	private HashMap<String, Socket> clientSocketsList = null;
	private MessageQueue clientMQ = new MessageQueue(new ClientCmdHandler(this));

	protected ClientConnector(ConnectController controller) {
		super(controller);
		this.clientSocketsList = new HashMap<String, Socket>();
	}

	public void run() {
		new Thread(clientMQ).start();

		while (true) {
			try {
				keepListenPortAndAcceptMultiClient(Configuration.getClientPort());
			} catch (Exception e) {
				e.printStackTrace();
				break;
			}
		}
	}

	@Override
	protected CommandListener getListener(Socket socket) {
		return new ClientListener(this, socket);
	}

	public void requestTheOther(Command command) {
		getController().requestServer(command);
	}

	public void broadcast(String cmd) {
		broadcast(new ArrayList<Socket>(clientSocketsList.values()), cmd);
	}

	public boolean broadcastAndGetResult(String cmd) {
		return broadcastAndGetResult(new ArrayList<Socket>(clientSocketsList.values()), cmd);
	}

	public void broadcastWithinRoom(String former, String roomId, String cmd) {
		ArrayList<Socket> list = new ArrayList<Socket>();
		ArrayList<String> memberList = new ArrayList<String>();

		if (former != null) {
			memberList.addAll(ChatRoomListController.getInstance().getMemberList(former));
		}
		if (roomId != null) {
			memberList.addAll(ChatRoomListController.getInstance().getMemberList(roomId));
		}

		Iterator<Entry<String, Socket>> it = clientSocketsList.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, Socket> entry = it.next();
			for (String identity : memberList) {
				if (identity != null && identity.equals(entry.getKey())) {
					list.add(entry.getValue());
				}
			}
		}
		broadcast(list, cmd);
	}

	public void addBroadcastList(String id, Socket socket) {
		clientSocketsList.put(id, socket);
	}

	public void removeBroadcastList(String id) {
		clientSocketsList.remove(id);
	}

	public MessageQueue getMQ() {
		return this.clientMQ;
	}

	public void runInternalRequest(Command cmd) {
		this.clientMQ.addCmd(cmd);
	}
}
