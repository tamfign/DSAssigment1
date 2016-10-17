package au.edu.unimelb.Heartbeat;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Watchdog {
    private HashMap<String, SocketStamp> socketMap;
    private int interval;
    private int port;
    private Callback callBack;

    public Watchdog(int interval, Callback callBack) {
        this.socketMap = null;
        this.interval = interval;
        this.callBack = callBack;
    }

    public void run() throws IOException, ParseException {
        BufferedReader reader = null;
        SSLServerSocketFactory factory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
        SSLServerSocket server = (SSLServerSocket) factory.createServerSocket(port);
        NewBeats newBeater = new NewBeats(server, socketMap);
        newBeater.run();
        while (true) {
            if (!socketMap.isEmpty()) {
                Set<String> bservers = socketMap.keySet();
                for (String bserver : bservers) {
                    Socket bsocket = socketMap.get(bserver).getSocket();
                    Date bsstamp = socketMap.get(bserver).getSocketStamp();
                    reader = new BufferedReader(new InputStreamReader(bsocket.getInputStream(), "UTF-8"));
                    if (reader.ready()) {
                        String coming = reader.readLine();
                        // To-Do
                        socketMap.get(bserver).setSocketStamp(new Date(System.currentTimeMillis()));
                    } else {
                        if (System.currentTimeMillis() - bsstamp.getTime() > interval) {
                            callBack.Call(bserver);
                        }
                    }
                }

            }

        }

    }

}
