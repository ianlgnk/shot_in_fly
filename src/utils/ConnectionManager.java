package utils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class ConnectionManager {
    private Socket connection;
    private DataInputStream in = null;
    private DataOutputStream out = null;

    public ConnectionManager(Socket connection) {
        this.connection = connection;

        try {
            in = new DataInputStream(connection.getInputStream());
            out = new DataOutputStream(connection.getOutputStream());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String read() throws Exception {
        return in.readUTF();
    }

    public void send(String content) throws Exception {
        out.writeUTF(content);
    }

    public String getIp() {
        return connection.getInetAddress().getHostAddress();
    }

    public Socket getConnection() {
        return connection;
    }

}
