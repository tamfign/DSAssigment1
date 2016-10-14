package com.mindplus.security;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.UUID;

public class ServerVerification extends Verification {

	private static final BigInteger RSA_MODULUS = new BigInteger(
			"8701751833897617210534741675873524307239379253584788401460826640859086893409308022178925700437547250280383417757917559236719752721115349891232682616061981");
	private static final BigInteger RSA_PRIVATE_EXPONENT = new BigInteger(
			"7620024379318312582397559009084663014670613170624700647875808183452141489734798328018905077943891498691663742924656158671456795747898696727804577199231553");

	private static final int UUID_LENGTH = 36;
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

	private String getMD5(String str) {
		String ret = null;

		try {
			ret = Base64.getEncoder().encodeToString(MessageDigest.getInstance("MD5").digest(str.getBytes("UTF-8")));
		} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return ret;
	}

	private String getUUID() {
		return UUID.randomUUID().toString();
	}

	private String getSignature(String str) {
		return sign(getMD5(str));
	}

	public String getContextAndSignature() {
		String uuid = getUUID();
		return uuid + getSignature(uuid);
	}

	public boolean verify(String contextWithSignature) {
		boolean ret = false;

		if (contextWithSignature != null && contextWithSignature.length() > UUID_LENGTH) {
			String uuid = contextWithSignature.substring(0, UUID_LENGTH);
			String signature = contextWithSignature.substring(UUID_LENGTH);

			ret = getMD5(uuid).equals(unsign(signature));
		}
		return ret;
	}
}
