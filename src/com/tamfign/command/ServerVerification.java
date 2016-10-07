package com.tamfign.command;

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

//TODO refactory
public class ServerVerification {

	private static final BigInteger RSA_MODULUS = new BigInteger(
			"8701751833897617210534741675873524307239379253584788401460826640859086893409308022178925700437547250280383417757917559236719752721115349891232682616061981");
	private static final BigInteger RSA_PUBLIC_EXPONENT = new BigInteger("65537");
	private static final BigInteger RSA_PRIVATE_EXPONENT = new BigInteger(
			"7620024379318312582397559009084663014670613170624700647875808183452141489734798328018905077943891498691663742924656158671456795747898696727804577199231553");

	RSAPublicKey pubKey = null;
	RSAPrivateKey priKey = null;
	private Cipher cipher = null;
	private static ServerVerification _instance;

	private ServerVerification() {
		RSAPrivateKeySpec privateSepc = new RSAPrivateKeySpec(RSA_MODULUS, RSA_PRIVATE_EXPONENT);
		RSAPublicKeySpec publicSpec = new RSAPublicKeySpec(RSA_MODULUS, RSA_PUBLIC_EXPONENT);
		try {
			priKey = (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(privateSepc);
			pubKey = (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(publicSpec);
			cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		} catch (InvalidKeySpecException | NoSuchAlgorithmException | NoSuchPaddingException e) {
			e.printStackTrace();
		}
	}

	public static ServerVerification getInstance() {
		if (_instance == null) {
			_instance = new ServerVerification();
		}
		return _instance;
	}

	public String decrypt(String encypted) {
		String ret = null;
		try {
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
			cipher.init(Cipher.ENCRYPT_MODE, priKey);
			ret = new String(Base64.getEncoder().encode(cipher.doFinal(str.getBytes())));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}
}
