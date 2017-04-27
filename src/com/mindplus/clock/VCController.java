package com.mindplus.clock;

import com.mindplus.configuration.Configuration;

public class VCController {
	private static VCController _instance;
	private VectorClock vc;

	private VCController() {
		this.vc = new VectorClock(Configuration.getServerId());
	}

	public static VCController getInstance() {
		if (_instance == null) {
			_instance = new VCController();
		}
		return _instance;
	}

	public VectorClock getVC() {
		return this.vc;
	}

	public long compareVC(VectorClock vc) {
		return this.vc.compare(vc);
	}
}
