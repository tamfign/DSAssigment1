package com.mindplus.command;

import org.json.simple.JSONObject;

import com.mindplus.heartbeat.Callback;

public class RouterBeaterHandler implements Callback {

	@Override
	public void reportDown(Object obj) {
		System.out.println("====Main Router is DOWN====");
	}

	@Override
	public void update(Object obj) {
		System.out.println(((JSONObject) obj).toJSONString());
	}

}
