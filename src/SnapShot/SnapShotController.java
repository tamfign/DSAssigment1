package SnapShot;

import java.util.UUID;

import org.json.simple.JSONObject;

import com.mindplus.command.Command;
import com.mindplus.command.ServerServerCmd;
import com.mindplus.connection.CoordinateConnector;

public class SnapShotController {
	// Chandy and Lamport’s Snapshot Algorithm

	private Record currentRecord = null;
	private CoordinateConnector connector = null;
	private static SnapShotController _instance = null;

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

		if (!isRecord()) {
			this.currentRecord = new Record(uuid, this);
			this.currentRecord.recordState();
			broadcastMarkerMsg(uuid);
		}
		this.currentRecord.endChannelRecording(serverId);
	}

	private boolean isRecord() {
		boolean ret = false;
		if (this.currentRecord != null)
			ret = this.currentRecord.isRecording();
		return ret;
	}

	public void broadcastMarkerMsg(String uuid) {
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
		String uuid = UUID.randomUUID().toString();
		this.currentRecord = new Record(uuid, this);
		this.currentRecord.recordState();
		broadcastMarkerMsg(uuid);
	}
}
