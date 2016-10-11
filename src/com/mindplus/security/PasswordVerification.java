package com.mindplus.security;

import java.math.BigInteger;

public class PasswordVerification extends Verification {
	private static final BigInteger RSA_MODULUS = new BigInteger(
			"7932256876245211831305471694980816353772807058845472143350725200232837782620305236300375122459619270791845124873177017259219756830083397042436119798829093");
	private static final BigInteger RSA_PRIVATE_EXPONENT = new BigInteger(
			"5996668552627813604254537057354235717955868070410591228501635412752123959594031373322012421291037919539318469423098606083940775199330476313204224795419373");

	private static PasswordVerification _instance;

	private PasswordVerification() {
		super(RSA_MODULUS, RSA_PRIVATE_EXPONENT);
	}

	public static PasswordVerification getInstance() {
		if (_instance == null) {
			_instance = new PasswordVerification();
		}
		return _instance;
	}
}
