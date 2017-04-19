package com.mindplus.connection;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import org.json.simple.JSONObject;

import com.mindplus.command.Command;
import com.mindplus.command.ServerServerCmd;
import com.mindplus.configuration.Configuration;
import com.mindplus.configuration.RouterConfig;
import com.mindplus.listener.MsgListener;
import com.mindplus.message.Message;
import com.mindplus.model.ServerListController;

public class RouterConnector extends Connector {
	private RouterConfig config = Configuration.getRouterConfig();

	protected RouterConnector(ConnectController controller) {
		super(controller);
	}

	private Socket getRouterSocket() throws UnknownHostException, IOException {
		SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
		return factory.createSocket(config.getHost(), config.getCoordinationPort());
	}

	public void contactRouter() throws Exception {
		JSONObject result = sendAndGet(ServerServerCmd.getNewServerCmd());
		if (result == null) {
			throw new Exception("Router can not be reached.");
		}

		boolean approved = Command.getResult(result);
		if (!approved) {
			throw new Exception("Rejected by router.");
		}
		ServerListController.getInstance().addServers(Command.getServers(result));
	}

	private JSONObject sendAndGet(String cmd) {
		JSONObject ret = null;
		SSLSocket rSocket = null;

		try {
			rSocket = (SSLSocket) getRouterSocket();
			rSocket.setSoTimeout(2000);
			if (rSocket.isConnected()) {
				write(rSocket, cmd);

				String response = readCmd(rSocket);
				if (response != null) {
					ret = Message.getObject(response);
				}
			}
		} catch (Exception e) {
			System.out.println("Fail to connect router");
		} finally {
			close(rSocket);
		}
		return ret;
	}

	private void sendOnly(String cmd) {
		SSLSocket rSocket = null;

		try {
			rSocket = (SSLSocket) getRouterSocket();
			if (rSocket.isConnected()) {
				write(rSocket, cmd);
			}
		} catch (Exception e) {
			System.out.println("Fail to connect router");
		} finally {
			close(rSocket);
		}
	}

	public JSONObject runInternalRequest(String cmd, boolean needResponse) {
		JSONObject ret = null;

		if (needResponse) {
			ret = sendAndGet(cmd);
		} else {
			sendOnly(cmd);
		}
		return ret;
	}

	@Override
	public void broadcast(String cmd) {
	}

	@Override
	public void requestTheOther(Command command) {
	}

	@Override
	protected MsgListener getListener(Socket socket) {
		return null;
	}
}
