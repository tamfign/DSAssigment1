package com.mindplus.message;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.mindplus.clock.VectorClock;

public class Message {
	private JSONObject cmdObj = null;
	private VectorClock vc = null;

	private static final String MSG_COMMAND = "MCMD";
	private static final String MSG_VC = "MVC";

	public Message(String str) {
		JSONObject obj = getObject(str);

		this.cmdObj = (JSONObject) obj.get(MSG_COMMAND);
		this.vc = new VectorClock((JSONObject) obj.get(MSG_VC));
	}

	public static JSONObject getObject(String cmd) {
		JSONObject ret = null;
		try {
			ret = (JSONObject) new JSONParser().parse(cmd);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return ret;
	}

	public JSONObject getCMDObj() {
		return this.cmdObj;
	}

	public VectorClock getVC() {
		return this.vc;
	}
}
