package com.mindplus.clock;

import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;

import com.mindplus.message.Message;

public class MessageBuffer {
	private static MessageBuffer _instance;
	private HashMap<String, Item> map = null;
	private LinkedBlockingQueue<Message> queue = null;

	private MessageBuffer() {
		this.map = new HashMap<String, Item>();
		this.queue = new LinkedBlockingQueue<Message>();
	}

	public static MessageBuffer getIntance() {
		if (_instance == null) {
			_instance = new MessageBuffer();
		}
		return _instance;
	}

	public void putMessage(Message msg) {
		if (msg == null)
			return;

		VectorClock vc = msg.getVC();
		long gap = VCController.getInstance().compareVC(vc);
		System.out.println(gap);
		if (gap > 1) {
			Item it = map.get(vc.getOwnId());
			if (it == null) {
				it = new Item();
			}
			it.buffer.put(vc.getOwnClk(), msg);
		} else {
			try {
				this.queue.put(msg);

				Item it = map.get(vc.getOwnId());
				if (it == null) {
					it = new Item();
					map.put(vc.getOwnId(), it);
				} else {
					putMessage(map.get(vc.getOwnId()).buffer.get(vc.getOwnClk() + 1));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public Message getMessage() {
		Message msg = null;
		if (!this.queue.isEmpty()) {
			try {
				msg = queue.take();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		return msg;
	}

	private class Item {
		HashMap<Long, Message> buffer = null;

		Item() {
			buffer = new HashMap<Long, Message>();
		}
	}
}
