package SnapShot;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.mindplus.configuration.ServerConfig;
import com.mindplus.model.ChatRoomListController;
import com.mindplus.model.ClientListController;
import com.mindplus.model.ServerListController;

public class Record {

	private String uid = null;
	private JSONObject finalRecord = null;
	private ArrayList<String> recordChannels = null;
	private boolean isRecording = true;

	private JSONArray msgRecord = null;
	private SnapShotController callback = null;

	public Record(String uid, SnapShotController callback) {
		this.callback = callback;
		this.uid = uid;
		this.finalRecord = new JSONObject();
		this.msgRecord = new JSONArray();
		this.recordChannels = new ArrayList<String>();

		for (ServerConfig server : ServerListController.getInstance().getList()) {
			this.recordChannels.add(server.getId());
		}
	}

	@SuppressWarnings("unchecked")
	public void recordState() {
		this.finalRecord.put("clients", ClientListController.getInstance().toJSON());
		this.finalRecord.put("chatrooms", ChatRoomListController.getInstance().toJSON());
	}

	public String uid() {
		return this.uid;
	}

	@SuppressWarnings("unchecked")
	public void endChannelRecording(String serverId) {
		this.recordChannels.remove(serverId);
		if (this.recordChannels.isEmpty()) {
			// End Current Record.
			this.finalRecord.put("message", this.msgRecord);
			saveFile();
			callback.recordFinished();
		}
	}

	private void saveFile() {
		try (FileWriter file = new FileWriter("test.json")) {
			file.write(this.finalRecord.toJSONString());
			file.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public void recordMsg(JSONObject msg) {
		msgRecord.add(msg);
	}

	public boolean isRecording() {
		return this.isRecording;
	}
}
