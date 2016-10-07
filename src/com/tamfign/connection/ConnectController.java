package com.tamfign.connection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import org.json.simple.JSONObject;

import com.tamfign.command.Command;
import com.tamfign.command.ServerServerCmd;
import com.tamfign.configuration.Configuration;
import com.tamfign.configuration.RouterConfig;
import com.tamfign.model.ServerListController;

public class ConnectController {
	private ClientConnector clients = null;
	private CoordinateConnector servers = null;

	private ConnectController() {
		this.clients = new ClientConnector(this);
		this.servers = new CoordinateConnector(this);
	}

	public static ConnectController getInstance() {
		return new ConnectController();
	}

	public void run() {
		if (!Configuration.isRouter()) {
			JSONObject result = contactRouter(Configuration.getRouterConfig());
			if (result == null) {
				System.out.println("Router can not be reached yet.");
				return;
			}

			boolean approved = Boolean.parseBoolean((String) result.get(Command.P_APPROVED));
			if (!approved) {
				System.out.println("Rejected by router.");
				return;
			}

			@SuppressWarnings("unchecked")
			ArrayList<String> list = (ArrayList<String>) result.get(Command.P_SERVERS);
			ServerListController.getInstance().addServers(list);
		}

		new Thread(this.servers).start();
		new Thread(this.clients).start();

		this.servers.checkOtherServers();
	}

	// TODO refactory
	private void write(Socket socket, String cmd) {
		try {
			PrintWriter os = new PrintWriter(socket.getOutputStream());
			os.println(cmd);
			os.flush();
		} catch (IOException e) {
			// e.printStackTrace();
		}
	}

	// TODO refactory
	private void close(Socket socket) {
		if (socket != null) {
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	// TODO refactory
	private String readCmd(Socket socket) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		return br.readLine();
	}

	private JSONObject contactRouter(RouterConfig router) {
		JSONObject ret = null;
		SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
		SSLSocket rSocket = null;

		try {
			rSocket = (SSLSocket) factory.createSocket(router.getHost(), router.getCoordinationPort());
			if (rSocket.isConnected()) {
				write(rSocket, ServerServerCmd.getNewServerCmd());
				ret = Command.getCmdObject(readCmd(rSocket));
			}
		} catch (Exception e) {
			System.out.println("Fail to connect router");
		} finally {
			close(rSocket);
		}
		return ret;
	}

	public void requestServer(Command cmd) {
		servers.runInternalRequest(cmd);
	}

	public void requestClient(Command cmd) {
		clients.runInternalRequest(cmd);
	}
}
