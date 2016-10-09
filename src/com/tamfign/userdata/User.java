package com.tamfign.userdata;

import com.tamfign.security.PasswordVerification;

public class User {
	private String name = null;
	private String pwd = null;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public boolean verify(String pwd) {
		boolean ret = false;

		if (pwd != null || this.pwd != null) {
			ret = PasswordVerification.getInstance().decrypt(pwd)
					.equals(PasswordVerification.getInstance().decrypt(this.pwd));
		}
		return ret;
	}
}
