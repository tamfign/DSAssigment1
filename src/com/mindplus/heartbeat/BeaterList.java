package com.mindplus.heartbeat;

import java.util.Date;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.json.simple.JSONObject;

class BeaterList {
	private ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
	private HashMap<String, TimeStampAndCmd> beaterMap;
	private static BeaterList _instance = null;

	private BeaterList() {
		this.beaterMap = new HashMap<String, TimeStampAndCmd>();
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
		ret = beaterMap.isEmpty();
		rwl.readLock().unlock();
		return ret;
	}

	protected Set<String> getKeySet() {
		Set<String> ret = null;
		rwl.readLock().lock();
		ret = beaterMap.keySet();
		rwl.readLock().unlock();
		return ret;
	}

	protected void add(String beater, TimeStampAndCmd ss) {
		rwl.writeLock().lock();
		beaterMap.put(beater, ss);
		rwl.writeLock().unlock();
	}

	protected JSONObject getCmd(String beater) {
		JSONObject ret = null;
		rwl.readLock().lock();
		ret = beaterMap.get(beater).getCmd();
		rwl.readLock().unlock();
		return ret;
	}

	protected Date getTimeStamp(String beater) {
		Date ret = null;
		rwl.readLock().lock();
		ret = beaterMap.get(beater).getTimeStamp();
		rwl.readLock().unlock();
		return ret;
	}

	protected void remove(String beater) {
		rwl.writeLock().lock();
		beaterMap.remove(beater);
		rwl.writeLock().unlock();
	}
}
