package com.mindplus.connection;

import com.mindplus.command.RouterBeaterHandler;
import com.mindplus.command.ServerServerCmd;
import com.mindplus.configuration.Configuration;
import com.mindplus.configuration.RouterConfig;
import com.mindplus.heartbeat.Heartbeat;
import com.mindplus.heartbeat.Watchdog;

public class RouterHeartbeatController implements HeartbeatController {
	private final int HEART_BEAT_INTERVAL = 5000;
	private final int WATCH_DOG_INTERVAL = 6000;

	private ConnectController cController = null;
	private Watchdog routerDog = null;

	public RouterHeartbeatController(ConnectController cController) {
		this.cController = cController;
		this.routerDog = new Watchdog(WATCH_DOG_INTERVAL, new RouterBeaterHandler(this.cController),
				Configuration.getRouterBackupPort());
	}

	public void start() {
		if (((RouterConfig) Configuration.getConfig()).isBackUp()) {
			System.out.println("Backup Router is on");
			new Thread(this.routerDog).start();
		} else {
			try {
				new Heartbeat(Configuration.getHost(), Configuration.getRouterBackupPort(), HEART_BEAT_INTERVAL, this)
						.run();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void cancel() {
		this.routerDog.cancel();
	}

	@Override
	public String getHeartBeatMsg() {
		return ServerServerCmd.getRouterBeat();
	}
}