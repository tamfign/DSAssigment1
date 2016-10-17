package com.mindplus.connection;

import com.mindplus.command.RouterBeaterHandler;
import com.mindplus.command.ServerServerCmd;
import com.mindplus.configuration.Configuration;
import com.mindplus.configuration.RouterConfig;
import com.mindplus.heartbeat.Heartbeat;
import com.mindplus.heartbeat.Watchdog;

public class RouterHeartbeatController {
	private final int HEART_BEAT_INTERVAL = 5000;
	private final int WATCH_DOG_INTERVAL = 6000;

	public void start() {
		if (((RouterConfig) Configuration.getConfig()).isBackUp()) {
			System.out.println("Backup Router is on");
			new Thread(new Watchdog(WATCH_DOG_INTERVAL, new RouterBeaterHandler(), Configuration.getRouterBackupPort()))
					.start();
		} else {
			try {
				new Heartbeat(Configuration.getHost(), Configuration.getRouterBackupPort(), HEART_BEAT_INTERVAL,
						ServerServerCmd.getRouterBeat()).run();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}