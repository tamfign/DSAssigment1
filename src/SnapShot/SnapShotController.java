package SnapShot;

import java.util.UUID;

import org.json.simple.JSONObject;

import com.mindplus.command.Command;
import com.mindplus.command.ServerServerCmd;
import com.mindplus.configuration.Configuration;
import com.mindplus.connection.CoordinateConnector;
import com.mindplus.model.ServerListController;

public class SnapShotController {
	// Chandy and Lamport’s Snapshot Algorithm

	private SnapShot currentRecord = null;
	private CoordinateConnector connector = null;
	private static SnapShotController _instance = null;
	public static final String RECORD_PATH = Configuration.getServerId() + "_SnapShot.json";

	public static SnapShotController getInstance() {
		if (_instance == null) {
			_instance = new SnapShotController();
		}
		return _instance;
	}

	public void setConnector(CoordinateConnector connector) {
		this.connector = connector;
	}

	public synchronized void handleMarkerMsg(JSONObject msg) {
		String serverId = (String) msg.get(Command.P_SERVER_ID);
		String uuid = (String) msg.get(Command.P_UUID);

		if (!isRecording()) {
			System.out.println("Create Snapshot");
			this.currentRecord = new SnapShot(uuid, this);
			this.currentRecord.recordState();
			broadcastMarkerMsg(uuid);
		}
		this.currentRecord.endChannelRecording(serverId);
	}

	public boolean isRecording() {
		boolean ret = false;
		if (this.currentRecord != null)
			ret = this.currentRecord.isRecording();
		return ret;
	}

	public boolean isCurrentRecord(JSONObject msg) {
		boolean ret = false;
		String uuid = (String) msg.get(Command.P_UUID);
		if (this.currentRecord != null) {
			ret = this.currentRecord.uid().equals(uuid);
		}
		return ret;
	}

	public void broadcastMarkerMsg(String uuid) {
		if (ServerListController.getInstance().size() > 0)
			connector.broadcast(ServerServerCmd.getMarker(uuid));
	}

	public void recordMsg(JSONObject msg) {
		if (this.currentRecord != null)
			this.currentRecord.recordMsg(msg);
	}

	protected void recordFinished() {
		this.currentRecord = null;
	}

	public void startSnapShot() {
		System.out.println("Start SnapShot");
		String uuid = UUID.randomUUID().toString();
		this.currentRecord = new SnapShot(uuid, this);
		this.currentRecord.recordState();

		if (ServerListController.getInstance().size() > 0) {
			broadcastMarkerMsg(uuid);
		} else {
			this.currentRecord.saveLocalOnly();
			this.currentRecord = null;
		}
	}
}
