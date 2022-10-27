package client;

import utils.ConnectionManager;

import java.net.Socket;

public class ClientConnectionManager extends ConnectionManager {
    private String user;

    public ClientConnectionManager(Socket connection) {
        super(connection);
    }

    public String sendRequest(String action, String param) {
        try {
            String paramStr = (param.isEmpty()) ? " " : param;

            send(user + ";" + action + ";" + paramStr);

            if (action.equals("disconnect")) return "";

            String response = read();

            return response;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }


}
