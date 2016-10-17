package au.edu.unimelb.Heartbeat;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Date;
import java.util.HashMap;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLSocket;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class NewBeats implements Runnable{
    private HashMap <String,SocketStamp> socketMap;
    private SSLServerSocket server;
    
    public NewBeats(SSLServerSocket server,HashMap <String,SocketStamp> socketMap){
        this.server=server;
        this.socketMap=socketMap;
    }
    
    @Override
    public void run() {
        SSLSocket socket = null;
        BufferedReader reader=null;
        while(true)
        {
            try {
                socket = (SSLSocket) server.accept();
                reader= new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
                JSONParser parser = new JSONParser();
                JSONObject comingJsonObj = (JSONObject) parser.parse(reader.readLine());
                String servername=(String) comingJsonObj.get("n");
                socketMap.put(servername, new SocketStamp(socket,new Date(System.currentTimeMillis())));
            } catch (IOException | ParseException e) {
                
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        
    }

}
