package com.mindplus.security;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.UUID;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;

public class Verification {
	private static final BigInteger RSA_PUBLIC_EXPONENT = new BigInteger("65537");

	private RSAPublicKey pubKey = null;
	private RSAPrivateKey priKey = null;
	private Cipher cipher = null;

	private static final int UUID_LENGTH = 36;

	protected Verification(BigInteger modulus, BigInteger privateExponent) {
		RSAPrivateKeySpec privateSepc = new RSAPrivateKeySpec(modulus, privateExponent);
		RSAPublicKeySpec publicSpec = new RSAPublicKeySpec(modulus, RSA_PUBLIC_EXPONENT);

		try {
			priKey = (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(privateSepc);
			pubKey = (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(publicSpec);
			cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		} catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		}
	}

	private String unsign(String encypted) {
		String ret = null;

		try {
			cipher.init(Cipher.DECRYPT_MODE, pubKey);
			ret = new String(cipher.doFinal(Base64.getDecoder().decode(encypted)));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

	private String sign(String str) {
		String ret = null;

		try {
			cipher.init(Cipher.ENCRYPT_MODE, priKey);
			ret = new String(Base64.getEncoder().encode(cipher.doFinal(str.getBytes())));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

	public String decrypt(String str) {
		String ret = null;

		try {
			cipher.init(Cipher.DECRYPT_MODE, priKey);
			ret = new String(cipher.doFinal(Base64.getDecoder().decode(str.getBytes())));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

	public String encrypt(String str) {
		String ret = null;

		try {
			cipher.init(Cipher.ENCRYPT_MODE, pubKey);
			ret = new String(Base64.getEncoder().encode(cipher.doFinal(str.getBytes())));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
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

	public String getTicket() {
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
