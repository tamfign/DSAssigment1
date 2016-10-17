package com.mindplus.heartbeat;

public interface Callback {
	public abstract void reportDown(Object obj);

	public abstract void update(Object obj);
}
