package com.mindplus.userdata;

import java.util.HashMap;

public class UserDataController {
	private static UserDataController _instance = null;
	private HashMap<String, User> userList = null;

	private UserDataController() {
		this.userList = new HashMap<String, User>();
	}

	public static UserDataController getInstance() {
		if (_instance == null) {
			_instance = new UserDataController();
		}
		return _instance;
	}

	public void add(User user) {
		userList.put(user.getName().toLowerCase(), user);
	}

	public User getUser(String userName) {
		return userList.get(userName.toLowerCase());
	}
}
