package com.mindplus.command;

import java.net.Socket;
import java.util.ArrayList;

import org.json.simple.JSONObject;

import com.mindplus.configuration.Configuration;
import com.mindplus.configuration.ServerConfig;
import com.mindplus.connection.CoordinateConnector;
import com.mindplus.model.ChatRoom;
import com.mindplus.model.ChatRoomListController;
import com.mindplus.model.ClientListController;
import com.mindplus.model.ServerListController;
import com.mindplus.security.ServerVerification;

public class CoordinateCmdHandler extends CmdHandler implements CmdHandlerInf {

	public CoordinateCmdHandler(CoordinateConnector connector) {
		super(connector);
	}

	public void cmdAnalysis(Command cmd) {
		switch ((String) cmd.getObj().get(Command.TYPE)) {
		case Command.TYPE_NEW_SERVER:
			handleNewServer(cmd);
			break;
		case Command.TYPE_SERVER_ON:
			handleServerOn(cmd);
			break;
		case Command.TYPE_LOCK_ID:
			handleLockId(cmd);
			break;
		case Command.TYPE_RELEASE_ID:
			handleReleaseId(cmd);
			break;
		case Command.TYPE_LOCK_ROOM:
			handleLockRoom(cmd);
			break;
		case Command.TYPE_RELEASE_ROOM:
			handleReleaseRoom(cmd);
			break;
		case Command.TYPE_DELETE_ROOM:
			handleDeleteRoom(cmd);
			break;
		case Command.TYPE_GET_CHATROOM_LOCATION:
			handleGetRoomLocation(cmd);
			break;
		case Command.TYPE_GET_FULL_ROOM_LIST:
			handlGetFullRoomList(cmd);
			break;
		case Command.TYPE_ROOM_LIST_STREAM:
			handRoomListStream(cmd);
			break;
		case Command.CMD_LOCK_IDENTITY:
			broadcastLockIdentity(cmd);
			break;
		case Command.CMD_RELEASE_IDENTITY:
			broadcastReleaseIdentity(cmd);
			break;
		case Command.CMD_LOCK_ROOM:
			askRouterLockRoomId(cmd);
			break;
		case Command.CMD_DELETE_ROOM:
			askRouterDeleteRoomId(cmd);
			break;
		case Command.CMD_RELEASE_ROOM:
			askRouterReleaseRoomId(cmd);
			break;
		case Command.CMD_ROUTE_ROOM:
			askRouterRoomServerId(cmd);
			break;
		case Command.CMD_ROOM_LIST:
			askRouterFullRooomList(cmd);
			break;
		default:
		}
	}

	private void handRoomListStream(Command cmd) {
		response(cmd.getSocket(), ServerServerCmd.getRoomListStreamRs(ChatRoomListController.getInstance().getRooms()));
	}

	private void askRouterFullRooomList(Command cmd) {
		JSONObject responseObj = ((CoordinateConnector) connector).requestRouter(ServerServerCmd.getRoomListCmdRq(),
				true);

		if (responseObj == null) {
			responseObj = ServerServerCmd.getRoomListObjRs(ChatRoomListController.getInstance().getList());
		}
		connector.requestTheOther(InternalCmd.getInternRoomResultCmd(cmd, Command.CMD_ROOM_LIST, responseObj));
	}

	private void handlGetFullRoomList(Command cmd) {
		response(cmd.getSocket(), ServerServerCmd.getRoomListCmdRs(ChatRoomListController.getInstance().getList()));
	}

	private void askRouterRoomServerId(Command cmd) {
		String roomId = (String) cmd.getObj().get(Command.P_ROOM_ID);
		JSONObject responseObj = ((CoordinateConnector) connector)
				.requestRouter(ServerServerCmd.getRoomLocationRq(roomId), true);

		if (responseObj == null) {
			connector.requestTheOther(new InternalCmd(cmd, Command.getMaintenance()));
		} else {
			connector.requestTheOther(InternalCmd.getInternRoomResultCmd(cmd, Command.CMD_ROUTE_ROOM, responseObj));
		}
	}

	private void askRouterReleaseRoomId(Command cmd) {
		String roomId = (String) cmd.getObj().get(Command.P_ROOM_ID);
		boolean result = (boolean) cmd.getObj().get(Command.P_APPROVED);
		((CoordinateConnector) connector)
				.requestRouter(ServerServerCmd.releaseRoom(Configuration.getServerId(), roomId, result), false);
	}

	private void askRouterDeleteRoomId(Command cmd) {
		String roomId = (String) cmd.getObj().get(Command.P_ROOM_ID);
		((CoordinateConnector) connector)
				.requestRouter(ServerServerCmd.deleteRoomBc(Configuration.getServerId(), roomId), false);
	}

	private void askRouterLockRoomId(Command cmd) {
		String roomId = (String) cmd.getObj().get(Command.P_ROOM_ID);

		try {
			connector.requestTheOther(InternalCmd.getLockRoomResultCmd(cmd, roomId, getLockRoomResult(roomId)));
		} catch (Exception e) {
			connector.requestTheOther(new InternalCmd(cmd, Command.getMaintenance()));
		}
	}

	private boolean getLockRoomResult(String roomId) throws Exception {
		boolean ret = false;
		JSONObject result = ((CoordinateConnector) connector)
				.requestRouter(ServerServerCmd.lockRoomRq(Configuration.getServerId(), roomId), true);

		if (result != null) {
			ret = Command.getResult(result);
		} else {
			throw new Exception("Lock room failed.");
		}
		return ret;
	}

	private void broadcastReleaseIdentity(Command cmd) {
		String identity = (String) cmd.getObj().get(Command.P_IDENTITY);
		connector.broadcast(ServerServerCmd.releaseIdentityRq(Configuration.getServerId(), identity));
	}

	private void broadcastLockIdentity(Command cmd) {
		String identity = (String) cmd.getObj().get(Command.P_IDENTITY);
		connector.requestTheOther(InternalCmd.getLockIdentityResultCmd(cmd, getLockIdentityResult(identity)));
	}

	private boolean getLockIdentityResult(String identity) {
		return ((CoordinateConnector) connector)
				.broadcastAndGetResult(ServerServerCmd.lockIdentityRq(Configuration.getServerId(), identity));
	}

	private void handleGetRoomLocation(Command cmd) {
		String roomId = (String) cmd.getObj().get(Command.P_ROOM_ID);

		String serverId = ChatRoomListController.getInstance().getRoomLocation(roomId);
		if (serverId == null)
			serverId = "";

		response(cmd.getSocket(), ServerServerCmd.getRoomLocationRs(roomId, serverId));
	}

	private void handleDeleteRoom(Command cmd) {
		String serverId = (String) cmd.getObj().get(Command.P_SERVER_ID);
		String roomId = (String) cmd.getObj().get(Command.P_ROOM_ID);
		ChatRoom room = ChatRoomListController.getInstance().getChatRoom(roomId);

		if (room != null && room.getServerId().equals(serverId)) {
			ChatRoomListController.getInstance().deleteRoom(roomId);
		}
	}

	private void handleNewServer(Command cmd) {
		String serverId = (String) cmd.getObj().get(Command.P_SERVER_ID);
		String pwd = (String) cmd.getObj().get(Command.P_PWD);
		String host = (String) cmd.getObj().get(Command.P_HOST);
		String coPort = (String) cmd.getObj().get(Command.P_COORDINATE_PORT);
		String clPort = (String) cmd.getObj().get(Command.P_CLIENT_PORT);

		if (verifyServer(pwd) && !ServerListController.getInstance().isExists(serverId)) {
			ArrayList<String> currentServerList = ServerListController.getInstance().getStringList();
			ServerListController.getInstance().addServer(new ServerConfig(serverId, host, coPort, clPort));
			ChatRoomListController.getInstance().addRoom(ChatRoomListController.getMainHall(serverId), serverId, null);
			sendApproveServer(cmd.getSocket(), serverId, currentServerList);
		} else {
			sendDisapproveServer(cmd.getSocket(), serverId);
		}
	}

	private void sendApproveServer(Socket socket, String id, ArrayList<String> serverList) {
		response(socket, ServerServerCmd.newServerRs(id, true, serverList));
	}

	private void sendDisapproveServer(Socket socket, String id) {
		response(socket, ServerServerCmd.newServerRs(id, false, null));
	}

	private boolean verifyServer(String contextWithSignature) {
		return ServerVerification.getInstance().verify(contextWithSignature);
	}

	protected void handleServerOn(Command cmd) {
		String stream = (String) cmd.getObj().get(Command.P_SERVER);

		ServerConfig sConfig = new ServerConfig(stream);
		((CoordinateConnector) connector).tryConnectServer(sConfig, false);
	}

	private void handleReleaseRoom(Command cmd) {
		String serverId = (String) cmd.getObj().get(Command.P_SERVER_ID);
		String roomId = (String) cmd.getObj().get(Command.P_ROOM_ID);
		boolean approved = Boolean.parseBoolean((String) cmd.getObj().get(Command.P_APPROVED));

		ChatRoom room = ChatRoomListController.getInstance().getChatRoom(roomId);
		if (room != null && room.getServerId().equals(serverId)) {
			if (ChatRoomListController.getInstance().isRoomExists(roomId)) {
				if (!approved) {
					ChatRoomListController.getInstance().removeRoom(roomId);
				}
			}
		}
	}

	private void handleLockRoom(Command cmd) {
		String serverId = (String) cmd.getObj().get(Command.P_SERVER_ID);
		String roomId = (String) cmd.getObj().get(Command.P_ROOM_ID);

		if (checkLocalChatRoomList(serverId, roomId)) {
			approveLockRoom(cmd.getSocket(), roomId);
		} else {
			disapproveLockRoom(cmd.getSocket(), roomId);
		}
	}

	private void disapproveLockRoom(Socket socket, String roomId) {
		response(socket, ServerServerCmd.lockRoomRs(Configuration.getServerId(), roomId, false));
	}

	private void approveLockRoom(Socket socket, String roomId) {
		response(socket, ServerServerCmd.lockRoomRs(Configuration.getServerId(), roomId, true));
	}

	private boolean checkLocalChatRoomList(String serverId, String roomId) {
		boolean ret = false;
		if (!ChatRoomListController.getInstance().isRoomExists(roomId)) {
			ChatRoomListController.getInstance().addRoom(roomId, serverId, null);
			ret = true;
		}
		return ret;
	}

	private void handleLockId(Command cmd) {
		String serverId = (String) cmd.getObj().get(Command.P_SERVER_ID);
		String identity = (String) cmd.getObj().get(Command.P_IDENTITY);

		if (checkLocalIdentityList(serverId, identity)) {
			approveLockId(cmd.getSocket(), identity);
		} else {
			disapproveLockId(cmd.getSocket(), identity);
		}
	}

	private void handleReleaseId(Command cmd) {
		String serverId = (String) cmd.getObj().get(Command.P_SERVER_ID);
		String identity = (String) cmd.getObj().get(Command.P_IDENTITY);

		if (ClientListController.getInstance().isIdentityExist(identity)) {
			ClientListController.getInstance().releaseId(serverId, identity);
		}
	}

	private void disapproveLockId(Socket socket, String identity) {
		response(socket, ServerServerCmd.lockIdentityRs(Configuration.getServerId(), identity, false));
	}

	private void approveLockId(Socket socket, String identity) {
		response(socket, ServerServerCmd.lockIdentityRs(Configuration.getServerId(), identity, true));
	}

	private boolean checkLocalIdentityList(String serverId, String identity) {
		boolean ret = false;
		if (!ClientListController.getInstance().isIdentityExist(identity)) {
			ClientListController.getInstance().addIndentity(identity, serverId, null);
			ret = true;
		}
		return ret;
	}
}
