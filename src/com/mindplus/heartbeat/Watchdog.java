package com.mindplus.heartbeat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;
import org.json.simple.parser.ParseException;

import com.mindplus.command.Command;

public class Watchdog {
	private HashMap<String, SocketStamp> socketMap;
	private int interval;
	private int port;
	private Callback callBack;

	public Watchdog(int interval, Callback callBack, int port) {
		this.socketMap = null;
		this.interval = interval;
		this.callBack = callBack;
		this.port = port;
	}

	public void run() throws IOException, ParseException {
		BeatAccepter beatAccepter = new BeatAccepter(port, socketMap);
		beatAccepter.run();

		checkBeater();
	}

	private void checkBeater() throws UnsupportedEncodingException, IOException {
		BufferedReader reader = null;

		while (true) {
			if (!socketMap.isEmpty()) {
				Set<String> bservers = socketMap.keySet();

				for (String bserver : bservers) {
					Socket bsocket = socketMap.get(bserver).getSocket();
					Date bsstamp = socketMap.get(bserver).getSocketStamp();
					reader = new BufferedReader(new InputStreamReader(bsocket.getInputStream(), "UTF-8"));

					String income = reader.readLine();
					if (reader.ready() && income != null) {
						callBack.update(Command.getCmdObject(income));
						socketMap.get(bserver).setSocketStamp(new Date(System.currentTimeMillis()));
					} else if (System.currentTimeMillis() - bsstamp.getTime() > interval) {
						callBack.reportDown(bserver);
					}
				}
			}
		}
	}
}
