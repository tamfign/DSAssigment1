package com.mindplus.clock;

import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;

import com.mindplus.message.Message;

public class MessageBuffer {
	private HashMap<String, Item> map = null;
	private LinkedBlockingQueue<Message> queue = null;

	public MessageBuffer() {
		this.map = new HashMap<String, Item>();
		this.queue = new LinkedBlockingQueue<Message>();
	}

	public void putMessage(Message msg) {
		if (msg == null)
			return;

		VectorClock vc = msg.getVC();
		if (vc.getClk() == 0) {
			clean(msg, vc);
		} else {
			long gap = VCController.getInstance().compareVC(vc);
			if (gap > 1) {
				Item it = map.get(vc.getOwnId());
				if (it == null) {
					it = new Item();
				}
				it.buffer.put(vc.getClk(), msg);
			} else {
				try {
					this.queue.put(msg);
					VCController.getInstance().tick(vc);

					Item it = map.get(vc.getOwnId());
					if (it == null) {
						it = new Item();
						map.put(vc.getOwnId(), it);
					} else {
						putMessage(map.get(vc.getOwnId()).buffer.get(vc.getClk() + 1));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void clean(Message msg, VectorClock vc) {
		try {
			this.queue.put(msg);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		VCController.getInstance().tick(vc);
		map.put(vc.getOwnId(), new Item());
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
