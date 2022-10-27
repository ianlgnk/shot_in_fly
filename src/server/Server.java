package server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

public class Server implements Runnable {
    private static int PORT = 3000;
    private static int MAX_CONNECTIONS = 2;
    private static int numberConnections;
    private static Storage storage = new Storage();

    private boolean running;
    private ServerSocket serverSocket;
    private Socket connection;
    private ServerConnectionManager cm;
    private Game player1;
    private Game player2;

    public Server() {
        running = false;
        serverSocket = null;
        connection = null;
        numberConnections = 0;
        cm = null;
        player1 = new Game();
        player2 = new Game();
    }

    public static void main(String[] args) {
        Server server = new Server();

        try {
            server.run();
        } catch (Exception e) {
            e.printStackTrace();
            server.log(e.getMessage());
        } finally {
            server.shutdown();
        }
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(PORT);
            running = true;

            log("Server is running at port " + PORT);

            while (running) {
                log(MAX_CONNECTIONS - numberConnections + " connection(s) remaining");

                connection = serverSocket.accept();
                String ip = connection.getInetAddress().getHostAddress();

                cm = new ServerConnectionManager(connection, this);

                Boolean isServerAvailable = (MAX_CONNECTIONS != numberConnections);
                cm.send(isServerAvailable.toString());

                if (!isServerAvailable) log(ip + " not allowed to connect, there are no remaining connections");
                else {
                    Game availableInstance = resolveInstance();
                    cm.setGame(availableInstance);

                    Game enemyInstance = (availableInstance == player1) ? player2 : player1;
                    cm.setEnemy(enemyInstance);

                    Thread user = new Thread(cm);
                    user.start();
                    numberConnections++;

                    log(ip + " is connected");
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Something went wrong during the execution!");
        }
    }

    private void log(String msg) {
        System.out.println(new Date().toString() + "\n" + msg + "\n");
    }

    private void shutdown() {
        try {
            log("Shutting down server...");

            if (serverSocket != null) {
                serverSocket.close();
                running = false;
            }
        } catch (Exception e) {
            throw new RuntimeException("Something went wrong trying to shut down the server!");
        } finally {
            log("Server shutted down");
        }
    }

    public void onDisconnectPlayer() {
        numberConnections--;
        log(MAX_CONNECTIONS - numberConnections + " connection(s) remaining");
    }

    private Game resolveInstance() {
        if (player1.getAvailable()) return player1;
        if (player2.getAvailable()) return player2;
        throw new RuntimeException("Could not resolve instance.");
    }

    public void storeMatch(Game game) {
        String content = "Player: " + game.getUser() + "\n\n";
        content += "Lista de palpites:\n" + game.guessesHistoricToString().replaceAll(";", "\n") + "\n";
        content += "Tentativas: " + game.getTotalRounds();

        storage.storeMatch(content);
    }

}
