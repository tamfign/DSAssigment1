package com.mindplus.security;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;

public class Verification {
	private static final BigInteger RSA_PUBLIC_EXPONENT = new BigInteger("65537");

	private RSAPublicKey pubKey = null;
	private RSAPrivateKey priKey = null;
	private Cipher cipher = null;

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

	public String unsign(String encypted) {
		String ret = null;

		try {
			cipher.init(Cipher.DECRYPT_MODE, pubKey);
			ret = new String(cipher.doFinal(Base64.getDecoder().decode(encypted)));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

	public String sign(String str) {
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
}
