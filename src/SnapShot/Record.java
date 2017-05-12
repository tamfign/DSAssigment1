package SnapShot;

import java.util.ArrayList;

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

	public Record(String uid) {
		this.uid = uid;
		this.finalRecord = new JSONObject();
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

	public void endChannelRecording(String serverId) {
		this.recordChannels.remove(serverId);
	}

	public void recordMsg() {

	}

	public boolean isRecording() {
		return this.isRecording;
	}
}
