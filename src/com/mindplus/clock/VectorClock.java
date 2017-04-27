package com.mindplus.clock;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.simple.JSONObject;

public class VectorClock {
	private static final String VC_ID = "VC_ID";
	private static final String VC_CLK = "VC_CLK";

	private HashMap<String, Long> processMap = null;
	private String id;

	protected VectorClock(String id) {
		processMap = new HashMap<String, Long>();
		this.id = id;
		processMap.put(id, (long) 1);
	}

	@SuppressWarnings("unchecked")
	public VectorClock(JSONObject obj) {
		this.id = (String) obj.get(VC_ID);
		this.processMap = new HashMap<String, Long>();

		JSONObject objMap = (JSONObject) obj.get(VC_CLK);
		for (Iterator<String> iterator = objMap.keySet().iterator(); iterator.hasNext();) {
			String key = iterator.next();
			this.processMap.put(key, (Long) objMap.get(key));
		}
	}

	public void tick() {
		long newClock = processMap.get(id) + 1;
		processMap.put(id, newClock);
	}

	public void tick(VectorClock vc) {
		for (Map.Entry<String, Long> entry : vc.processMap.entrySet()) {
			if (!this.processMap.containsKey(entry.getKey())
					|| this.processMap.get(entry.getKey()) < entry.getValue()) {
				this.processMap.put(entry.getKey(), entry.getValue());
			}
		}
	}

	protected long getOwnClk() {
		return processMap.get(id);
	}

	protected String getOwnId() {
		return id;
	}

	public long compare(VectorClock vc) {
		Long ret = vc.getOwnClk();

		if (this.processMap.containsKey(vc.getOwnId())) {
			ret -= processMap.get(vc.getOwnId());
		}
		return ret;
	}

	@SuppressWarnings("unchecked")
	public JSONObject toJSON() {
		JSONObject root = new JSONObject();
		JSONObject clk = new JSONObject();
		for (Map.Entry<String, Long> entry : this.processMap.entrySet()) {
			clk.put(entry.getKey(), entry.getValue());
		}

		root.put(VC_ID, this.id);
		root.put(VC_CLK, clk);
		return root;
	}
}