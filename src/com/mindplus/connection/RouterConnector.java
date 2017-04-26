package com.mindplus.connection;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import org.json.simple.JSONObject;

import com.mindplus.command.Command;
import com.mindplus.configuration.Configuration;
import com.mindplus.configuration.RouterConfig;
import com.mindplus.listener.MsgListener;
import com.mindplus.message.Message;

public class RouterConnector extends Connector {
	private RouterConfig config = Configuration.getRouterConfig();

	protected RouterConnector(ConnectController controller) {
		super(controller);
	}

	private Socket getRouterSocket() throws UnknownHostException, IOException {
		SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
		return factory.createSocket(config.getHost(), config.getCoordinationPort());
	}

	public JSONObject sendAndGet(JSONObject cmd) {
		JSONObject ret = null;
		SSLSocket rSocket = null;

		try {
			rSocket = (SSLSocket) getRouterSocket();
			rSocket.setSoTimeout(2000);
			if (rSocket.isConnected()) {
				write(rSocket, new Message(cmd));

				Message response = readCmd(rSocket);
				if (response != null) {
					ret = response.getCMDObj();
				}
			}
		} catch (Exception e) {
			System.out.println("Fail to connect router");
		} finally {
			close(rSocket);
		}
		return ret;
	}

	private void sendOnly(JSONObject cmd) {
		SSLSocket rSocket = null;

		try {
			rSocket = (SSLSocket) getRouterSocket();
			if (rSocket.isConnected()) {
				write(rSocket, new Message(cmd));
			}
		} catch (Exception e) {
			System.out.println("Fail to connect router");
		} finally {
			close(rSocket);
		}
	}

	public JSONObject runInternalRequest(JSONObject cmd, boolean needResponse) {
		JSONObject ret = null;

		if (needResponse) {
			ret = sendAndGet(cmd);
		} else {
			sendOnly(cmd);
		}
		return ret;
	}

	@Override
	public void broadcast(JSONObject cmd) {
	}

	@Override
	public void requestTheOther(Command command) {
	}

	@Override
	protected MsgListener getListener(Socket socket) {
		return null;
	}
}
