package com.mindplus.heartbeat;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

public class Heartbeat {
	private int interval;
	private Timer timer;
	private String msg;
	private TimerTask beatTask;
	private Socket socket;

	public Heartbeat(Socket socket, int interval, String msg) throws UnsupportedEncodingException, IOException {
		this.interval = interval;
		this.timer = new Timer();
		this.msg = msg;
		this.socket = socket;
		this.beatTask = getTimerTask();
	}

	public void run() {
		timer.schedule(beatTask, 0, interval);
	}

	private TimerTask getTimerTask() {
		return new TimerTask() {
			@Override
			public void run() {
				write(msg);
			}
		};
	}

	private void write(String msg) {
		try {
			if (socket.isConnected()) {
				BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));
				writer.write(msg + "\n");
				writer.flush();
			} else {
				socket.close();
			}
		} catch (IOException e) {
			timer.cancel();
			e.printStackTrace();
		}
	}

}
