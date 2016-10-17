package com.mindplus.connection;

import com.mindplus.command.ServerBeaterHandler;
import com.mindplus.command.ServerServerCmd;
import com.mindplus.configuration.Configuration;
import com.mindplus.heartbeat.Heartbeat;
import com.mindplus.heartbeat.Watchdog;

public class HeartbeatController {
	private final int HEART_BEAT_INTERVAL = 5000;
	private final int WATCH_DOG_INTERVAL = 6000;

	public void start() {
		if (Configuration.isRouter()) {
			System.out.println("Watchdog " + Configuration.getHeartbeatPort());
			new Thread(new Watchdog(WATCH_DOG_INTERVAL, new ServerBeaterHandler(), Configuration.getHeartbeatPort()))
					.start();
		} else {
			System.out.println("Heartbeat");
			try {
				new Heartbeat(Configuration.getRouterConfig().getHost(),
						Configuration.getRouterConfig().getHeartbeatPort(), HEART_BEAT_INTERVAL,
						ServerServerCmd.getHeartBeat()).run();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
