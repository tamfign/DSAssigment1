package com.mindplus.connection;

import java.io.IOException;
import java.net.Socket;

import com.mindplus.command.Command;

public interface ConnectorInf {

	public abstract void broadcast(String cmd);

	public abstract String readCmd(Socket socket) throws IOException;

	public abstract void write(Socket socket, String cmd);

	public abstract void close(Socket socket);

	public abstract void requestTheOther(Command command);
}
