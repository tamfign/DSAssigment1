package com.tamfign.security;

import java.math.BigInteger;

public class ServerVerification extends Verification {

	private static final BigInteger RSA_MODULUS = new BigInteger(
			"8701751833897617210534741675873524307239379253584788401460826640859086893409308022178925700437547250280383417757917559236719752721115349891232682616061981");

	private static final BigInteger RSA_PRIVATE_EXPONENT = new BigInteger(
			"7620024379318312582397559009084663014670613170624700647875808183452141489734798328018905077943891498691663742924656158671456795747898696727804577199231553");

	private static ServerVerification _instance;

	private ServerVerification() {
		super(RSA_MODULUS, RSA_PRIVATE_EXPONENT);
	}

	public static ServerVerification getInstance() {
		if (_instance == null) {
			_instance = new ServerVerification();
		}
		return _instance;
	}
}
