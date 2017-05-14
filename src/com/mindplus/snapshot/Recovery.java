package com.mindplus.snapshot;

import java.io.File;
import java.io.FileReader;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class Recovery {
	private static Recovery _instance = null;

	private Recovery() {

	}

	public static Recovery getInstance() {
		if (_instance == null)
			_instance = new Recovery();
		return _instance;
	}

	public void recoveryIfNeeded() {
		File file = new File(SnapShotController.RECORD_PATH);
		if (file.exists()) {
			SnapShot record = new SnapShot(readFile());
			record.recoverState();
		}
	}

	private JSONObject readFile() {
		JSONObject obj = null;
		try {
			FileReader reader = new FileReader(SnapShotController.RECORD_PATH);
			obj = (JSONObject) new JSONParser().parse(reader);
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return obj;
	}
}
