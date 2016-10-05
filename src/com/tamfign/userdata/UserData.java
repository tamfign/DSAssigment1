package com.tamfign.userdata;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class UserData extends DefaultHandler {
	private static final String USER = "user";
	private static final String NAME = "name";
	private static final String PWD = "pwd";

	private User user = null;
	private String tag = null;

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		String data = new String(ch, start, length);

		if (NAME.equalsIgnoreCase(this.tag)) {
			this.user.setName(data);
		} else if (PWD.equalsIgnoreCase(this.tag)) {
			this.user.setPwd(data);
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if (USER.equals(qName)) {
			UserDataController.getInstance().add(user);
			this.user = null;
		}
		this.tag = null;
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attri) throws SAXException {
		switch (qName) {
		case USER:
			this.user = new User();
			break;
		case NAME:
			this.tag = NAME;
			break;
		case PWD:
			this.tag = PWD;
			break;
		default:
			this.tag = null;
		}
	}
}
