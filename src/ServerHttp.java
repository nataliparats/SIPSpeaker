
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.BindException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ServerHttp implements Runnable {

    private ArrayList<String> status = new ArrayList<String>();
    private int port = 8081;
    private ServerSocket s;
    private conf_reader conf;

    public ServerHttp(conf_reader conf) {
        this.port = conf.getHttp_bind_port();
        this.conf = conf;
    }

    public void Start_Server() {
        try {
            try {
                s = new ServerSocket(port);
            } catch (BindException e) {
                System.out.println("Failed to bind the port for the Http server");
                System.exit(-1);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void Close_Server() {
        try {
            s.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public void Listen() {
        try {
            while (true) {
                final Socket conn = s.accept();
                Thread t = new Thread(new HttpListener(conn, status, conf));
                t.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void run() {
        Start_Server();
        Listen();
        Close_Server();
    }
}
