package com.mindplus.heartbeat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Date;
import java.util.Set;

import com.mindplus.command.Command;

public class Watchdog implements Runnable {
	private int interval;
	private int port;
	private Callback callBack;

	public Watchdog(int interval, Callback callBack, int port) {
		this.interval = interval;
		this.callBack = callBack;
		this.port = port;
	}

	public void run() {
		new Thread(new BeatAccepter(port)).start();

		try {
			checkBeaters();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void checkBeaters() throws InterruptedException {
		while (true) {
			if (!BeaterList.getInstance().isEmpty()) {
				Set<String> beaters = BeaterList.getInstance().getKeySet();

				for (String beater : beaters) {
					try {
						checkBeater(beater);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			// Sleep half of the interval.
			Thread.sleep(interval / 2);
		}
	}

	private void checkBeater(String beater) throws IOException {
		Socket bsocket = BeaterList.getInstance().getSocket(beater);
		Date bsstamp = BeaterList.getInstance().getTimeStamp(beater);
		BufferedReader reader = new BufferedReader(new InputStreamReader(bsocket.getInputStream(), "UTF-8"));

		if (reader.ready()) {
			String cmd = reader.readLine();
			callBack.update(Command.getCmdObject(cmd));
			BeaterList.getInstance().setTimeStamp(beater, new Date(System.currentTimeMillis()));
		} else if (System.currentTimeMillis() - bsstamp.getTime() > interval) {
			callBack.reportDown(beater);
			bsocket.close();
			BeaterList.getInstance().remove(beater);
		}
	}
}
