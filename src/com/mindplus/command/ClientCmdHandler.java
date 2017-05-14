package com.mindplus.command;

import java.net.Socket;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.mindplus.configuration.Configuration;
import com.mindplus.configuration.ServerConfig;
import com.mindplus.connection.ClientConnector;
import com.mindplus.model.ChatRoom;
import com.mindplus.model.ChatRoomListController;
import com.mindplus.model.Client;
import com.mindplus.model.ClientListController;
import com.mindplus.model.ServerListController;
import com.mindplus.security.UserVerification;
import com.mindplus.userdata.User;
import com.mindplus.userdata.UserDataController;

public class ClientCmdHandler extends CmdHandler implements CmdHandlerInf {
	private final static Pattern r = Pattern.compile("^[a-zA-Z]([a-z]|[A-Z]|[0-9]){2,15}");

	private Socket tmpSocket = null; // Ad hoc fix
	private String tmpClientId = null;

	public ClientCmdHandler(ClientConnector connector) {
		super(connector);
	}

	public void cmdAnalysis(Command cmd) {
		switch ((String) cmd.getObj().get(Command.TYPE)) {
		case Command.TYPE_MESSAGE:
			handleMessage(cmd);
			break;
		case Command.TYPE_QUIT:
			handleQuit(cmd);
			break;
		case Command.TYPE_NEW_ID:
			handleNewIdentity(cmd);
			break;
		case Command.TYPE_JOIN_SERVER:
			handleLockIdentiy(cmd);
			break;
		case Command.CMD_LOCK_IDENTITY:
			handleJoinServer(cmd);
			break;
		case Command.TYPE_LIST:
			handleList(cmd);
			break;
		case Command.TYPE_WHO:
			handleWho(cmd);
			break;
		case Command.TYPE_CREATE_ROOM:
			handleLockRoomId(cmd);
			break;
		case Command.CMD_LOCK_ROOM:
			handleCreateRoom(cmd);
			break;
		case Command.TYPE_DELETE_ROOM:
			handleDeleteRoom(cmd);
			break;
		case Command.TYPE_JOIN:
			handleJoinRoom(cmd);
			break;
		case Command.TYPE_MOVE_JOIN:
			handlerMoveJoin(cmd);
			break;
		case Command.TYPE_MAINTENANCE:
			handleMaintenance(cmd);
			break;
		case Command.TYPE_MARKER:
			handleMarker(cmd);
			break;
		case Command.TYPE_RECONNECT:
			handleReconnect(cmd);
			break;
		default:
		}
	}

	/**
	 * For identity
	 */

	private void handleJoinServer(Command cmd) {
		boolean approved = (Boolean) cmd.getObj().get(Command.P_APPROVED);
		String identity = (String) cmd.getObj().get(Command.P_IDENTITY);

		releaseIdentity(identity, cmd);
		if (approved) {
			createIdentity(identity, tmpSocket, ChatRoomListController.getLocalMainHall());
			approveJoinServer(tmpSocket, identity);
			((ClientConnector) connector).broadcastWithinRoom(null, ChatRoomListController.getLocalMainHall(),
					ClientServerCmd.roomChangeRq(identity, "", ChatRoomListController.getLocalMainHall()));
		} else {
			sendDisapproveJoinServer(tmpSocket, identity);
		}
		tmpSocket = null;
	}

	private void handleLockIdentiy(Command cmd) {
		String id = (String) cmd.getObj().get(Command.P_IDENTITY);
		String ticket = (String) cmd.getObj().get(Command.P_PWD);

		// Only one server
		if (ServerListController.getInstance().size() < 1) {
			createIdentity(id, cmd.getSocket(), ChatRoomListController.getLocalMainHall());
			approveJoinServer(cmd.getSocket(), id);
			((ClientConnector) connector).broadcastWithinRoom(null, ChatRoomListController.getLocalMainHall(),
					ClientServerCmd.roomChangeRq(id, "", ChatRoomListController.getLocalMainHall()));
		} else {
			if (isClientAuthorized(ticket) && isIdValid(id)) {
				tmpSocket = cmd.getSocket();
				lockIdentity(id, cmd);
			} else {
				sendDisapproveJoinServer(cmd.getSocket(), id);
			}
		}
	}

	private boolean isClientAuthorized(String ticket) {
		return UserVerification.getInstance().verify(ticket);
	}

	private void lockIdentity(String identity, Command cmd) {
		if (!ClientListController.getInstance().isIdentityExist(identity)) {
			connector.requestTheOther(InternalCmd.getInternIdCmd(cmd, Command.CMD_LOCK_IDENTITY, identity));
		} else {
			sendDisapproveJoinServer(cmd.getSocket(), identity);
		}
	}

	private void releaseIdentity(String identity, Command cmd) {
		connector.requestTheOther(InternalCmd.getInternIdCmd(cmd, Command.CMD_RELEASE_IDENTITY, identity));
	}

	private void approveJoinServer(Socket socket, String id) {
		response(socket, ClientServerCmd.joinServerRs(id, true));
	}

	private void sendDisapproveJoinServer(Socket socket, String id) {
		response(socket, ClientServerCmd.joinServerRs(id, false));
	}

	private boolean isClientAuthorised(Command cmd) {
		boolean ret = false;
		String id = (String) cmd.getObj().get(Command.P_IDENTITY);
		String pwd = (String) cmd.getObj().get(Command.P_PWD);

		User user = UserDataController.getInstance().getUser(id);
		if (user != null) {
			ret = user.verify(pwd);
		}
		return ret;
	}

	private void handleNewIdentity(Command cmd) {
		// Server does not support unauthorized client.
		if (!Configuration.isRouter()) {
			disapproveNewIdentity(cmd.getSocket());
		}

		String id = (String) cmd.getObj().get(Command.P_IDENTITY);
		if (ServerListController.getInstance().size() <= 0) {
			disapproveNewIdentity(cmd.getSocket());
			return;
		}

		if (isIdValid(id) && isClientAuthorised(cmd)) {
			ServerConfig server = ServerListController.getInstance().getBestServer();
			if (server == null) {
				disapproveNewIdentity(cmd.getSocket());
			} else {
				approveNewIdentity(cmd.getSocket(), server);
			}
		} else {
			disapproveNewIdentity(cmd.getSocket());
		}
	}

	private void approveNewIdentity(Socket socket, ServerConfig server) {
		response(socket, ClientServerCmd.newIdentityRq(true, server.getId(), server.getHost(), server.getClientPort()));
	}

	private void disapproveNewIdentity(Socket socket) {
		response(socket, ClientServerCmd.newIdentityRq(false, null, null, 0));
	}

	private boolean isIdValid(String id) {
		boolean ret = false;

		if (id != null && !("").equals(id)) {
			Matcher m = r.matcher(id);
			if (m.matches()) {
				ret = true;
			}
		}
		return ret;
	}

	private void createIdentity(String identity, Socket socket, String roomId) {
		ClientListController.getInstance().addIndentity(identity, Configuration.getServerId(), roomId);
		ChatRoomListController.getInstance().addMember(roomId, identity);
		((ClientConnector) connector).addBroadcastList(identity, socket);
	}

	private void handlerMoveJoin(Command cmd) {
		String id = (String) cmd.getObj().get(Command.P_IDENTITY);
		String formerRoom = (String) cmd.getObj().get(Command.P_FORMER);
		String roomId = (String) cmd.getObj().get(Command.P_ROOM_ID);

		if (ClientListController.getInstance().isIdentityExist(id)) {
			response(cmd.getSocket(), ClientServerCmd.serverChangeRs(false, Configuration.getServerId()));
			return;
		}
		String newRoom = null;
		if (ChatRoomListController.getInstance().isRoomExists(roomId)) {
			newRoom = roomId;
		} else {
			newRoom = ChatRoomListController.getLocalMainHall();
		}
		createIdentity(id, cmd.getSocket(), newRoom);
		response(cmd.getSocket(), ClientServerCmd.serverChangeRs(true, Configuration.getServerId()));
		((ClientConnector) connector).broadcastWithinRoom(null, newRoom,
				ClientServerCmd.roomChangeRq(id, formerRoom, newRoom));
	}

	private void handleQuit(Command cmd) {
		String currentRoom = getCurrentRoomId(cmd.getOwner());

		if (isOwnerOfRoom(cmd.getOwner())) {
			currentRoom = ChatRoomListController.getLocalMainHall();
			deleteRoomAndBroadcastRoomChange(cmd);
		}
		removeFromClientList(cmd.getOwner());
		responseChangeRoomAndTerminate(cmd, currentRoom);
	}

	private void removeFromClientList(String clientId) {
		ChatRoomListController.getInstance().removeMember(getCurrentRoomId(clientId), clientId);
		ClientListController.getInstance().removeIndentity(clientId);
	}

	private void handleWho(Command cmd) {
		if (Configuration.isRouter()) {
			return;
		}
		ChatRoom room = ChatRoomListController.getInstance().getChatRoom(getCurrentRoomId(cmd.getOwner()));
		if (room != null) {
			response(cmd.getSocket(), ClientServerCmd.whoRs(room.getName(), room.getMemberList(), room.getOwner()));
		}
	}

	private void routeClient(String clientId, String roomId, ServerConfig server, Socket socket) {
		String formerRoom = getCurrentRoomId(clientId);
		response(socket, ClientServerCmd.routeRq(roomId, server.getHost(), server.getClientPort()));
		removeFromClientList(clientId);
		((ClientConnector) connector).broadcastWithinRoom(formerRoom, null,
				ClientServerCmd.roomChangeRq(clientId, formerRoom, roomId));
		terminate(socket, clientId);
	}

	private void terminate(Socket socket, String id) {
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		connector.close(socket);// Will close socket and terminal this thread.
		((ClientConnector) connector).removeBroadcastList(id);
	}

	/**
	 * For message
	 */

	private void handleMessage(Command cmd) {
		String clientId = cmd.getOwner();
		String content = (String) cmd.getObj().get(Command.P_CONTENT);
		((ClientConnector) connector).broadcastWithinRoom(null, getCurrentRoomId(clientId),
				ClientServerCmd.messageCmd(clientId, content));
	}

	/**
	 * For room
	 */

	private void handleJoinRoom(Command cmd) {
		String roomId = (String) cmd.getObj().get(Command.P_ROOM_ID);

		if (!isOwnerOfRoom(cmd.getOwner()) && isRoomAvailable(roomId)) {
			ServerConfig server = getServer(roomId);
			if (server == null) {
				disapproveJoin(cmd.getSocket(), cmd.getOwner(), roomId);
				return;
			}
			if (!server.isItselft()) {
				routeClient(cmd.getOwner(), roomId, server, cmd.getSocket());
			} else {
				approveJoin(cmd.getOwner(), roomId);
			}
		} else {
			disapproveJoin(cmd.getSocket(), cmd.getOwner(), roomId);
		}
	}

	private ServerConfig getServer(String roomId) {
		return ServerListController.getInstance().get(getRoomServerId(roomId));
	}

	private String getRoomServerId(String roomId) {
		return ChatRoomListController.getInstance().getChatRoom(roomId).getServerId();
	}

	private boolean isRoomAvailable(String roomId) {
		return (ChatRoomListController.getInstance().isRoomExists(roomId));
	}

	private void handleDeleteRoom(Command cmd) {
		String roomId = (String) cmd.getObj().get(Command.P_ROOM_ID);

		if (isRoomCanBeDel(cmd.getOwner(), roomId)) {
			ArrayList<String> currentMemberList = ChatRoomListController.getInstance().getMemberList(roomId);
			cleanOwnership(cmd.getOwner());
			deleteRoom(roomId);
			broadcastDeleteRoom(cmd, roomId, currentMemberList);
			approveDeleteRoom(cmd.getSocket(), roomId);
		} else {
			disapproveDeleteRoom(cmd.getSocket(), roomId);
		}
	}

	private void cleanOwnership(String clientId) {
		Client client = ClientListController.getInstance().getClient(clientId);
		if (client != null) {
			client.setOwnRoom(null);
		}
	}

	private void handleLockRoomId(Command cmd) {
		String roomId = (String) cmd.getObj().get(Command.P_ROOM_ID);

		if (!isOwnerOfRoom(cmd.getOwner()) && isIdValid(roomId)
				&& !ChatRoomListController.getInstance().isRoomExists(roomId)) {
			// Only one server
			if (ServerListController.getInstance().size() < 1) {
				String currentRoomId = getCurrentRoomId(cmd.getOwner());
				createChatRoom(cmd.getOwner(), roomId);
				approveChatRoom(cmd.getSocket(), roomId);
				broadcastRoomChange(cmd.getOwner(), currentRoomId, roomId);
			} else {
				connector.requestTheOther(InternalCmd.getInternRoomCmd(cmd, Command.CMD_LOCK_ROOM, roomId));
				tmpSocket = cmd.getSocket();
				tmpClientId = cmd.getOwner();
			}
		} else {
			disapproveChatRoom(cmd.getSocket(), roomId);
		}
	}

	private void handleCreateRoom(Command cmd) {
		String roomId = (String) cmd.getObj().get(Command.P_ROOM_ID);
		boolean approved = (Boolean) cmd.getObj().get(Command.P_APPROVED);

		releaseRoomId(cmd, roomId, approved);
		if (approved) {
			String currentRoomId = getCurrentRoomId(tmpClientId);
			createChatRoom(tmpClientId, roomId);
			approveChatRoom(tmpSocket, roomId);
			broadcastRoomChange(tmpClientId, currentRoomId, roomId);
		} else {
			disapproveChatRoom(tmpSocket, roomId);
		}
		tmpSocket = null;
		tmpClientId = null;
	}

	private void responseChangeRoomAndTerminate(Command cmd, String perviousRoom) {
		response(cmd.getSocket(), ClientServerCmd.roomChangeRq(cmd.getOwner(), "", ""));
		((ClientConnector) connector).broadcastWithinRoom(null, perviousRoom,
				ClientServerCmd.roomChangeRq(cmd.getOwner(), "", ""));
		terminate(cmd.getSocket(), cmd.getOwner());
	}

	private String getCurrentRoomId(String clientId) {
		String ret = null;

		Client client = ClientListController.getInstance().getClient(clientId);
		if (client != null) {
			ret = client.getRoomId();
		}
		return ret;
	}

	private void deleteRoomAndBroadcastRoomChange(Command cmd) {
		String roomId = ClientListController.getInstance().getClient(cmd.getOwner()).getOwnRoom();
		if (isRoomCanBeDel(cmd.getOwner(), roomId)) {
			ArrayList<String> currentMemberList = ChatRoomListController.getInstance().getMemberList(roomId);
			currentMemberList.remove(cmd.getOwner());
			deleteRoom(roomId);
			broadcastDeleteRoom(cmd, roomId, currentMemberList);
		}
	}

	private boolean isOwnerOfRoom(String clientId) {
		boolean ret = false;
		Client client = ClientListController.getInstance().getClient(clientId);
		if (client != null) {
			ret = client.getOwnRoom() != null;
		}

		return ret;
	}

	private void broadcastDeleteRoom(Command cmd, String roomId, ArrayList<String> currentMemberList) {
		connector.requestTheOther(InternalCmd.getInternRoomCmd(cmd, Command.CMD_DELETE_ROOM, roomId));

		for (String id : currentMemberList) {
			((ClientConnector) connector).broadcastWithinRoom(null, ChatRoomListController.getLocalMainHall(),
					ClientServerCmd.roomChangeRq(id, roomId, ChatRoomListController.getLocalMainHall()));
		}
	}

	private void deleteRoom(String roomId) {
		// Change room id in the client list.
		for (String identity : ChatRoomListController.getInstance().getMemberList(roomId)) {
			ClientListController.getInstance().getClient(identity).setRoomId(ChatRoomListController.getLocalMainHall());
		}
		ChatRoomListController.getInstance().deleteRoom(roomId);
	}

	private void disapproveDeleteRoom(Socket socket, String roomId) {
		response(socket, ClientServerCmd.deleteRoomRs(roomId, false));
	}

	private void approveDeleteRoom(Socket socket, String roomId) {
		response(socket, ClientServerCmd.deleteRoomRs(roomId, true));
	}

	private boolean isRoomCanBeDel(String clientId, String roomId) {
		boolean ret = false;

		ChatRoom room = ChatRoomListController.getInstance().getChatRoom(roomId);
		if (room != null && clientId.equals(room.getOwner())) {
			ret = true;
		}
		return ret;
	}

	private void disapproveJoin(Socket socket, String clientId, String roomId) {
		response(socket,
				ClientServerCmd.roomChangeRq(clientId, getCurrentRoomId(clientId), getCurrentRoomId(clientId)));
	}

	private void approveJoin(String clientId, String roomId) {
		String former = getCurrentRoomId(clientId);
		ClientListController.getInstance().getClient(clientId).setRoomId(roomId);
		ChatRoomListController.getInstance().changeRoom(former, roomId, clientId);
		broadcastRoomChange(clientId, former, roomId);
	}

	private void disapproveChatRoom(Socket socket, String roomId) {
		response(socket, ClientServerCmd.createRoomRs(roomId, false));
	}

	private void approveChatRoom(Socket socket, String roomId) {
		response(socket, ClientServerCmd.createRoomRs(roomId, true));
	}

	private void createChatRoom(String clientId, String roomId) {
		ChatRoomListController.getInstance().addRoom(roomId, Configuration.getServerId(), clientId);
		ChatRoomListController.getInstance()
				.removeMember(ClientListController.getInstance().getClient(clientId).getRoomId(), clientId);
		ClientListController.getInstance().getClient(clientId).setRoomId(roomId);
		ClientListController.getInstance().getClient(clientId).setOwnRoom(roomId);
	}

	private void releaseRoomId(Command cmd, String roomId, boolean result) {
		connector.requestTheOther(InternalCmd.getInternRoomResultCmd(cmd, Command.CMD_RELEASE_ROOM, roomId, result));
	}

	private void handleList(Command cmd) {
		response(cmd.getSocket(), ClientServerCmd.listRs(ChatRoomListController.getInstance().getList()));
	}

	private void broadcastRoomChange(String clientId, String former, String newRoom) {
		((ClientConnector) connector).broadcastWithinRoom(former, newRoom,
				ClientServerCmd.roomChangeRq(clientId, former, newRoom));
	}

	private void handleMaintenance(Command cmd) {
		response(cmd.getSocket(), cmd.getObj());
	}

	private void handleMarker(Command cmd) {
		connector.requestTheOther(InternalCmd.getInternMarkerCmd(cmd, Command.CMD_MARKER));
	}

	private void handleReconnect(Command cmd) {
		((ClientConnector) connector).addBroadcastList(cmd.getOwner(), cmd.getSocket());
	}
}
