package client;

import java.net.Socket;

public class Client implements Runnable {
    private static int PORT = 3000;
    private static Interface appInterface = new Interface();

    private ClientConnectionManager cm;

    public Client() {
        cm = null;
    }

    public static void main(String[] args) {
        Client client = new Client();

        try {
            client.run();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.println("\nFinalizando app...");
        }
    }

    @Override
    public void run() {
        String serverIp = appInterface.readServerIp();

        try {
            Socket connection = connect(serverIp);
            cm = new ClientConnectionManager(connection);

            Boolean isServerAvailable = Boolean.valueOf(cm.read());
            if (isServerAvailable) {
                cm.setUser(appInterface.readUser());
                appInterface.welcome(cm.getUser());

                do {
                    int option = appInterface.printMenu();

                    if (option == 1) execOption1();
                    if (option == 2) execOption2();
                    if (option == 3) execOption3();
                    if (option == 4) break;
                } while(true);
            } else appInterface.printServerUnavailable();

            disconnect();
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Something went wrong during the execution!");
        }
    }

    private Socket connect(String serverIp) {
        try {
            return new Socket(serverIp, PORT);
        } catch (Exception e) {
            throw new RuntimeException("Server was not found!");
        }
    }

    public void disconnect() {
        try {
            appInterface.bye(cm.getUser());
            cm.sendRequest("disconnect", "");

            if (cm.getConnection() != null) cm.getConnection().close();
        } catch (Exception e) {
            throw new RuntimeException("Something went wrong trying to disconnect!");
        }
    }

    public void execOption1() {
        int numberToGuess = Integer.valueOf(cm.sendRequest("onePlayer", ""));
        int guess;

        appInterface.printOption1(numberToGuess);

        do {
            String guessesHistoric = cm.sendRequest("getGuessesHistoric", "");
            guess = appInterface.readGuess(guessesHistoric.replaceAll(";", "\n"), false);

            Boolean isGuessCorrect = Boolean.valueOf(cm.sendRequest("makeGuess", String.valueOf(guess)));

            if (isGuessCorrect || guess == 0) break;
        } while(true);

        int totalRounds = Integer.valueOf(cm.sendRequest("getTotalRounds", ""));
        appInterface.printOption1EndGame(guess, numberToGuess, totalRounds);

        cm.sendRequest("finishGame", "");
    }

    public void execOption2() {
        Boolean isGuessCorrect = false, didILost = false;
        int guess = 0;

        do {
            appInterface.printOption2();
            awaitEnemy();
            awaitTurn();

            String enemyNumber = appInterface.readEnemyNumberToGuess();
            cm.sendRequest("setEnemyGuess", enemyNumber);

            do {
                appInterface.printWaitEnemy();
                awaitTurn();

                didILost = Boolean.valueOf(cm.sendRequest("didILost", ""));

                if (!didILost) {
                    String multiplayerGuessesHistoric = cm.sendRequest("getMultiplayerGuessesHistoric", "");
                    guess = appInterface.readGuess(multiplayerGuessesHistoric.replaceAll(";", "\n"), true);

                    isGuessCorrect = Boolean.valueOf(cm.sendRequest("makeGuess", String.valueOf(guess)));
                }

                if (isGuessCorrect || didILost) {
                    int totalRounds = Integer.valueOf(cm.sendRequest("getTotalRounds", ""));
                    String[] numberOfVictory = cm.sendRequest("getNumberOfVictory", "").split(";");
                    appInterface.printOption2EndGame(guess, isGuessCorrect, didILost, totalRounds, numberOfVictory[0], numberOfVictory[1]);

                    break;
                }
            } while (true);

            if (didILost) {
                appInterface.printWaitEnemy();
                awaitTurn();
            }

            Boolean keepPlaying = appInterface.keepPlaying();
            System.out.println();

            if (keepPlaying) {
                String response = cm.sendRequest("playAgain", "");

                if (response.equals("finishGame")) {
                    cm.sendRequest("finishGame", "");
                    appInterface.printEnemyDisconnected();
                    break;
                } else {
                    appInterface.printWaitEnemy();
                    awaitTurn();

                    response = cm.sendRequest("playAgain", "");
                    if ((response).equals("finishGame")) {
                        cm.sendRequest("finishGame", "");
                        appInterface.printEnemyDisconnected();
                        break;
                    }
                }
            } else {
                cm.sendRequest("finishGame", "");
                break;
            }
        } while(true);

        cm.sendRequest("resetNumberOfVictory", "");
    }

    public void execOption3() {
        appInterface.printNotFound();
    }

    private void awaitEnemy() {
        String response;
        while (true) {
            response = cm.sendRequest("multiplayer", "");
            if (response.contains("ready")) break;
        }

        String player1 = response.split(";")[1], player2 = response.split(";")[2];
        appInterface.printEnemyFound(player1, player2);
    }

    private void awaitTurn() {
        String response;
        while (true) {
            response = cm.sendRequest("isMyTurn", "");
            if (Boolean.valueOf(response)) break;
        }
    }

}
