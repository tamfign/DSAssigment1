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

import org.json.simple.JSONObject;

import com.mindplus.clock.MessageBuffer;
import com.mindplus.listener.MsgListener;
import com.mindplus.message.Message;

public abstract class Connector implements ConnectorInf {

	private ConnectController controller = null;
	private MessageBuffer msgBuf = null;

	protected Connector(ConnectController controller) {
		this.controller = controller;
		this.msgBuf = new MessageBuffer();
	}

	protected ConnectController getController() {
		return this.controller;
	}

	protected abstract MsgListener getListener(Socket socket);

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

	protected void broadcast(List<Socket> listenerList, JSONObject cmd) {
		if (listenerList != null) {
			for (Socket socket : listenerList) {
				write(socket, new Message(cmd));
			}
		}
	}

	private Message readMessage(Socket socket) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		String str = br.readLine();
		Message msg = null;

		if (str != null && !"".equals(str)) {
			msg = new Message(str);
		}
		return msg;
	}

	public Message readCmd(Socket socket) throws IOException {
		Message msg;
		this.msgBuf.putMessage(readMessage(socket));
		do {
			msg = this.msgBuf.getMessage();
		} while (msg == null);
		return msg;
	}

	public void write(Socket socket, Message cmd) {
		if (!socket.isConnected()) {
			System.out.println("Fail to send [Socket is closed].");
			return;
		}

		System.out.println(socket.getPort()); //TODO check why this line can avoid error.
		try {
			System.out.println("Sending: " + cmd.toString());
			PrintWriter os = new PrintWriter(socket.getOutputStream());
			os.println(cmd.toString());
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