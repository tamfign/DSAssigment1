package com.mindplus.main;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.xml.sax.SAXException;

import com.mindplus.configuration.Configuration;
import com.mindplus.connection.ConnectController;
import com.mindplus.userdata.UserData;

public class Main {

	public static void main(String[] args) {
		ServerArguments arguments = new ServerArguments();
		CmdLineParser parser = new CmdLineParser(arguments);
		ConnectController controller = null;

		try {
			parser.parseArgument(args);
			Configuration.init(arguments);

			if (Configuration.isRouter()) {
				readUserData();
			}

			controller = ConnectController.getInstance();
			controller.run();
		} catch (CmdLineException e) {
			System.err.println("Example: java -jar server.jar [-r] -l server_config");
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}

	private static void readUserData() throws IOException {
		UserData userDataHandler = new UserData();
		SAXParser parser = null;

		try {
			parser = SAXParserFactory.newInstance().newSAXParser();
			InputStream is = new FileInputStream(Configuration.getUserDataPath());
			parser.parse(is, userDataHandler);
		} catch (ParserConfigurationException | SAXException e) {
			e.printStackTrace();
		}
	}
}
