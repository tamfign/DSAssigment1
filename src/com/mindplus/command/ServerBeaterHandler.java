package com.mindplus.command;

import com.mindplus.heartbeat.Callback;

public class ServerBeaterHandler implements Callback {

	@Override
	public void reportDown(Object obj) {
		System.out.println("====DOWN====");
	}

	@Override
	public void update(Object obj) {
		System.out.println("====Update====");
	}

}
