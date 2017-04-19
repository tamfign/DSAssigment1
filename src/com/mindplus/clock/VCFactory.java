package com.mindplus.clock;

import com.mindplus.configuration.Configuration;

public class VCFactory {
	private static VCFactory _instance;
	private VectorClock vc;

	private VCFactory() {
		this.vc = new VectorClock(Configuration.getServerId());
	}

	public static VectorClock getVectorClock() {
		if (_instance == null) {
			_instance = new VCFactory();
		}
		return _instance.vc;
	}
}
