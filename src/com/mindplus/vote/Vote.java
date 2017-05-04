package com.mindplus.vote;

public class Vote {
	private boolean result = true;
	private boolean allVoted = false;
	private int membersCnt = 0;
	private final int memberTotal;

	protected Vote(int memberTotal) {
		this.memberTotal = memberTotal;
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
		return this.result;
	}
}
