package com.mindplus.heartbeat;

import java.net.Socket;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;

class BeaterList {
	private ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
	private HashMap<String, SocketStamp> socketMap;
	private static BeaterList _instance = null;

	private BeaterList() {
		this.socketMap = new HashMap<String, SocketStamp>();
	}

	protected static BeaterList getInstance() {
		if (_instance == null) {
			_instance = new BeaterList();
		}
		return _instance;
	}

	protected boolean isEmpty() {
		boolean ret = false;
		rwl.readLock().lock();
		ret = socketMap.isEmpty();
		rwl.readLock().unlock();
		return ret;
	}

	protected Set<String> getKeySet() {
		Set<String> ret = null;
		rwl.readLock().lock();
		ret = socketMap.keySet();
		rwl.readLock().unlock();
		return ret;
	}

	protected void add(String beater, SocketStamp ss) {
		rwl.writeLock().lock();
		socketMap.put(beater, ss);
		rwl.writeLock().unlock();
	}

	protected Socket getSocket(String beater) {
		Socket ret = null;
		rwl.readLock().lock();
		ret = socketMap.get(beater).getSocket();
		rwl.readLock().unlock();
		return ret;
	}

	protected void setTimeStamp(String beater, Date timestamp) {
		rwl.writeLock().lock();
		socketMap.get(beater).setTimeStamp(timestamp);
		rwl.writeLock().unlock();
	}

	protected Date getTimeStamp(String beater) {
		Date ret = null;
		rwl.readLock().lock();
		ret = socketMap.get(beater).getTimeStamp();
		rwl.readLock().unlock();
		return ret;
	}

	protected void remove(String beater) {
		rwl.writeLock().lock();
		socketMap.remove(beater);
		rwl.writeLock().unlock();
	}
}
