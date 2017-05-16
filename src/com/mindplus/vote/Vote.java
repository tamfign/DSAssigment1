package com.mindplus.vote;

public class Vote {
	private boolean result = true;
	private boolean allVoted = false;
	private int membersCnt = 0;
	private final int memberTotal;

	private boolean isTimeout = false;

	protected Vote(int memberTotal) {
		this.memberTotal = memberTotal;
		new Thread(new TimeoutThread()).start();
	}

	public synchronized void vote(boolean vote) {
		this.result &= vote;
		this.membersCnt++;

		if (this.membersCnt >= memberTotal) {
			this.allVoted = true;
		}
	}

	public synchronized boolean isAllVoted() {
		return this.allVoted;
	}

	public synchronized boolean getResult() {
		if (this.isTimeout)
			return false;
		return this.result;
	}

	class TimeoutThread implements Runnable {

		@Override
		public void run() {
			try {
				Thread.sleep(20000);
				isTimeout = true;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
