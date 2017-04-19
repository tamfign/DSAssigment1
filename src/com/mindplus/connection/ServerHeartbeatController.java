package com.mindplus.connection;

import com.mindplus.command.ServerBeaterHandler;
import com.mindplus.command.ServerServerCmd;
import com.mindplus.configuration.Configuration;
import com.mindplus.heartbeat.Heartbeat;
import com.mindplus.heartbeat.Watchdog;
import com.mindplus.message.Message;

public class ServerHeartbeatController implements HeartbeatController {
	private final int HEART_BEAT_INTERVAL = 10000;
	private final int WATCH_DOG_INTERVAL = 11000;

	public void start() {
		if (Configuration.isRouter()) {
			new Thread(new Watchdog(WATCH_DOG_INTERVAL, new ServerBeaterHandler(), Configuration.getHeartbeatPort()))
					.start();
		} else {
			try {
				new Heartbeat(Configuration.getRouterConfig().getHost(),
						Configuration.getRouterConfig().getHeartbeatPort(), HEART_BEAT_INTERVAL, this).run();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public String getHeartBeatMsg() {
		return new Message(ServerServerCmd.getHeartBeat()).toString();
	}
}
