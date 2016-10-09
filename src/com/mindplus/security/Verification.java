package com.mindplus.security;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;

public class Verification {
	private static final BigInteger RSA_PUBLIC_EXPONENT = new BigInteger("65537");

	private RSAPublicKeySpec publicSpec = null;
	private RSAPrivateKeySpec privateSepc = null;
	private Cipher cipher = null;

	protected Verification(BigInteger modulus, BigInteger privateExponent) {
		privateSepc = new RSAPrivateKeySpec(modulus, privateExponent);
		publicSpec = new RSAPublicKeySpec(modulus, RSA_PUBLIC_EXPONENT);

		try {
			cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		} catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
			e.printStackTrace();
		}
	}

	public String decrypt(String encypted) {
		String ret = null;
		try {
			RSAPublicKey pubKey = (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(publicSpec);
			cipher.init(Cipher.DECRYPT_MODE, pubKey);

			ret = new String(cipher.doFinal(Base64.getDecoder().decode(encypted)));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

	public String encrypt(String str) {
		String ret = null;
		try {
			RSAPrivateKey priKey = (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(privateSepc);
			cipher.init(Cipher.ENCRYPT_MODE, priKey);

			ret = new String(Base64.getEncoder().encode(cipher.doFinal(str.getBytes())));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}
}
