package com.mindplus.clock;

import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;

public class VectorClock {
	private HashMap<String, Integer> processMap = null;
	private String id;

	protected VectorClock(String id) {
		processMap = new HashMap<String, Integer>();
		this.id = id;
		processMap.put(id, 1);
	}

	@SuppressWarnings("unchecked")
	public VectorClock(JSONObject obj) {
		processMap = obj;
	}

	public void tick() {
		int newClock = processMap.get(id) + 1;
		processMap.put(id, newClock);
	}

	public void tick(VectorClock vc) {
		for (Map.Entry<String, Integer> entry : vc.processMap.entrySet()) {
			if (!this.processMap.containsKey(entry.getKey())
					|| this.processMap.get(entry.getKey()) < entry.getValue()) {
				this.processMap.put(entry.getKey(), entry.getValue());
			}
		}
		tick();
	}

	public String toString() {
		return processMap.toString();
	}
}