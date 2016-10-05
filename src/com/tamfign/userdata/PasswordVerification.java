package com.tamfign.userdata;

import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;

public class PasswordVerification {
	private static final BigInteger RSA_MODULUS = new BigInteger(
			"7932256876245211831305471694980816353772807058845472143350725200232837782620305236300375122459619270791845124873177017259219756830083397042436119798829093");
	private static final BigInteger RSA_PUBLIC_EXPONENT = new BigInteger("65537");

	private Cipher cipher = null;
	private static PasswordVerification _instance;

	private PasswordVerification() {
		RSAPublicKeySpec publicSpec = new RSAPublicKeySpec(RSA_MODULUS, RSA_PUBLIC_EXPONENT);
		try {
			RSAPublicKey pub = (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(publicSpec);
			cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			cipher.init(Cipher.DECRYPT_MODE, pub);
		} catch (InvalidKeySpecException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e) {
			e.printStackTrace();
		}
	}

	public static PasswordVerification getInstance() {
		if (_instance == null) {
			_instance = new PasswordVerification();
		}
		return _instance;
	}

	public String decrypt(String encypted) {
		String ret = null;
		try {
			ret = new String(cipher.doFinal(Base64.getDecoder().decode(encypted)));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}
}
