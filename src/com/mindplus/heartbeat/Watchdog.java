package com.mindplus.heartbeat;

import java.io.IOException;
import java.util.Date;
import java.util.Set;

public class Watchdog implements Runnable {
	private int interval;
	private int port;
	private Callback callBack;
	private String tobeRevmove = null;

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
				if (tobeRevmove != null) {
					BeaterList.getInstance().remove(tobeRevmove);
					tobeRevmove = null;
				}
			}
			// Sleep half of the interval.
			Thread.sleep(interval / 2);
		}
	}

	private void checkBeater(String beater) throws IOException {
		Date bsstamp = BeaterList.getInstance().getTimeStamp(beater);

		if (System.currentTimeMillis() - bsstamp.getTime() <= interval) {
			callBack.update(BeaterList.getInstance().getCmd(beater));
		} else {
			callBack.reportDown(beater);
			tobeRevmove = beater;
		}
	}
}
