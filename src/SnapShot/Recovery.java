package SnapShot;

import java.io.File;
import java.io.FileReader;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class Recovery {
	private static Recovery _instance = null;
	private static final String RECORD_PATH = "test.json";

	private Recovery() {

	}

	public static Recovery getInstance() {
		if (_instance == null)
			_instance = new Recovery();
		return _instance;
	}

	public void recoveryIfNeeded() {
		if (new File(RECORD_PATH).exists()) {
			SnapShot record = new SnapShot(readFile());
			record.recoverState();
		}
	}

	private JSONObject readFile() {
		JSONObject obj = null;
		try {
			obj = (JSONObject) new JSONParser().parse(new FileReader(RECORD_PATH));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return obj;
	}
}
