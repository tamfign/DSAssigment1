package au.edu.unimelb.Heartbeat;
import java.net.Socket;
import java.util.Date;

public class SocketStamp {
        private Socket socket;
        private Date timestamp;
        public SocketStamp(Socket socket,Date timestamp){
            this.socket=socket;
            this.timestamp=timestamp;
        }
        Date getSocketStamp(){
           return this.timestamp;
        }
        void setSocketStamp(Date timestamp){
            this.timestamp=timestamp;
        }
        
        Socket getSocket(){
            return this.socket;
        }
    }


