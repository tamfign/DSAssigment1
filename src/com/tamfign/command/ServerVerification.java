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
			"11304902361440178399849310752809875678633352933236456281703139992108646538417746805884193781989544783080162234832272326248167761067800720964896488070509907");
	private static final BigInteger RSA_PUBLIC_EXPONENT = new BigInteger("65537");
	private static final BigInteger RSA_PRIVATE_EXPONENT = new BigInteger(
			"588333773046737209032547174643463127349661669734494734882660170149456959405842237280979624655474417348652891822794941630634213913773918564942977113989243312345");

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
