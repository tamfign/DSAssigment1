package com.mindplus.connection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

import org.json.simple.parser.ParseException;

import com.mindplus.command.Command;
import com.mindplus.listener.CommandListener;

public abstract class Connector implements ConnectorInf {

	private ConnectController controller = null;

	protected Connector(ConnectController controller) {
		this.controller = controller;
	}

	protected ConnectController getController() {
		return this.controller;
	}

	protected abstract CommandListener getListener(Socket socket);

	private SSLServerSocket generateSSLServerSocket(int port) throws Exception {
		SSLServerSocketFactory factory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
		return (SSLServerSocket) factory.createServerSocket(port);
	}

	protected void keepListenPortAndAcceptMultiClient(int port) throws Exception {

		SSLSocket socket = null;
		SSLServerSocket server = generateSSLServerSocket(port);
		try {
			while (true) {
				socket = (SSLSocket) server.accept();
				Thread handleThread = new Thread(getListener(socket));
				handleThread.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			server.close();
		}
	}

	protected boolean broadcastAndGetResult(List<Socket> listenerList, String cmd) {
		boolean ret = true;
		if (listenerList != null) {
			for (Socket socket : listenerList) {
				try {
					write(socket, cmd);
					ret &= readResult(socket);
				} catch (Exception e) {
					// If any exception happens, consider it's false.
					ret = false;
					e.printStackTrace();
				}
			}
		}
		return ret;
	}

	protected void broadcast(List<Socket> listenerList, String cmd) {
		if (listenerList != null) {
			for (Socket socket : listenerList) {
				write(socket, cmd);
			}
		}
	}

	private boolean readResult(Socket socket) throws ParseException, IOException {
		boolean ret = false;
		String cmd = readCmd(socket);

		// If read nothing back, consider it's false.
		if (cmd != null && !"".equals(cmd)) {
			ret = Command.getResult(Command.getCmdObject((cmd)));
		}
		return ret;
	}

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