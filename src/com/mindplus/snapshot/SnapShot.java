package com.mindplus.snapshot;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.mindplus.command.Command;
import com.mindplus.configuration.ServerConfig;
import com.mindplus.connection.CoordinateConnector;
import com.mindplus.model.ChatRoomListController;
import com.mindplus.model.ClientListController;
import com.mindplus.model.ServerListController;

public class SnapShot {

	private String uid = null;
	private JSONObject finalRecord = null;
	private ArrayList<String> recordChannels = null;
	private boolean isRecording = true;

	private JSONArray msgRecord = null;
	private SnapShotController callback = null;

	public SnapShot(String uid, SnapShotController callback) {
		this.callback = callback;
		this.uid = uid;
		this.finalRecord = new JSONObject();
		this.msgRecord = new JSONArray();
		this.recordChannels = new ArrayList<String>();

		for (ServerConfig server : ServerListController.getInstance().getList()) {
			this.recordChannels.add(server.getId());
		}
	}

	public SnapShot(JSONObject obj) {
		this.finalRecord = obj;
	}

	public void recoverState(CoordinateConnector servers) {
		System.out.println("Recovering from last snap shot.");
		recoverClients();
		recoverChatRooms();
		recoverMessages(servers);
	}

	private void recoverMessages(CoordinateConnector servers) {
		JSONArray obj = (JSONArray) this.finalRecord.get("message");
		if (obj != null) {
			for (int i = 0; i < obj.size(); i++) {
				// TODO
				servers.getMQ().addCmd(new Command(null, (JSONObject) obj.get(i), null));
			}
		}
	}

	private void recoverChatRooms() {
		JSONArray obj = (JSONArray) this.finalRecord.get("chatrooms");
		if (obj != null) {
			ChatRoomListController.getInstance().updata(obj);
		}
	}

	private void recoverClients() {
		JSONArray obj = (JSONArray) this.finalRecord.get("clients");
		if (obj != null) {
			ClientListController.getInstance().update(obj);
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

	public void saveLocalOnly() {
		saveFile();
	}

	private void saveFile() {
		try (FileWriter file = new FileWriter(SnapShotController.RECORD_PATH)) {
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
