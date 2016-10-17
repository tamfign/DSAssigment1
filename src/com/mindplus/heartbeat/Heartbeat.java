package au.edu.unimelb.Heartbeat;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;
import org.json.simple.JSONObject;


public class Heartbeat {
    private int interval;
    private Timer timer;
    private JSONObject msg;
    private BufferedWriter writer;
    private TimerTask beatTask;

    public Heartbeat(Socket socket, int interval, JSONObject msg) throws UnsupportedEncodingException, IOException {
        this.interval = interval;
        this.timer = new Timer();
        this.msg = msg;
        this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));
        this.beatTask = getTimerTask();
    }

    private TimerTask getTimerTask() {
        return new TimerTask() {
            @Override
            public void run() {
                String msgS = msg.toJSONString();
                write(msgS);
            }
        };
    }
    public void run() {
        timer.schedule(beatTask, interval);
    }

    private void write(String msg) {
        try {
            writer.write(msg + "\n");
            writer.flush();
        } catch (IOException e) {
        }
    }

}
