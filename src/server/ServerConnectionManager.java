package server;

import utils.ConnectionManager;

import java.net.Socket;
import java.util.Date;

public class ServerConnectionManager extends ConnectionManager implements Runnable {
    private Server server;
    private Game game;
    private Game enemy;

    public ServerConnectionManager(Socket connection, Server server) {
        super(connection);
        this.server = server;
    }

    @Override
    public void run() {
        try {
            while (true) {
                String request = read();
                String response = processRequest(request);

                if (request.contains("disconnect")) break;
                else send(response);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String processRequest(String request) {
        String user, action, param;

        user = request.split(";")[0];
        action = request.split(";")[1];
        param = request.split(";")[2];

        return execRequest(user, action, param);
    }

    public String execRequest(String user, String action, String param) {
        String response = "";

        switch (action) {
            case "disconnect": {
                disconnect(user);
                break;
            }
            case "onePlayer": {
                game.resetGame(user);
                response = game.initOnePlayer();
                break;
            }
            case "getGuessesHistoric": {
                response = game.guessesHistoricToString();
                break;
            }
            case "makeGuess": {
                response = String.valueOf(game.makeGuess(Integer.parseInt(param), enemy));
                Boolean isGuessCorrect = Boolean.valueOf(response);

                if (isGuessCorrect) server.storeMatch(game);
                if (game.getGameMode() == 2 && enemy.getGameMode() == 2) changeTurns();

                break;
            }
            case "getTotalRounds": {
                response = String.valueOf(game.getTotalRounds());
                break;
            }
            case "finishGame": {
                if (game.getGameMode() == 2) {
                    enemy.resetGame(enemy.getUser());

                    changeTurns();
                }

                game.resetGame(user);

                break;
            }
            case "multiplayer": {
                game.resetGame(user);
                response = game.multiplayer(enemy);

                if (response.contains("ready")) {
                    game.setMyTurn(false);
                    enemy.setMyTurn(true);
                };

                break;
            }
            case "isMyTurn": {
                response = String.valueOf(game.getMyTurn());
                break;
            }
            case "setEnemyGuess": {
                enemy.setNumberToGuess(Integer.parseInt(param));
                changeTurns();
                break;
            }
            case "getMultiplayerGuessesHistoric": {
                response = getMultiplayerGuessesHistoric();
                break;
            }
            case "didILost": {
                response = String.valueOf(game.getLoser());

                if (game.getLoser()) changeTurns();

                break;
            }
            case "getNumberOfVictory": {
                response = game.getNumberOfVictory() + ";" + enemy.getNumberOfVictory();
                break;
            }
            case "playAgain": {
                changeTurns();
                if (enemy.getGameMode() == 0) response = "finishGame";

                break;
            }
            case "resetNumberOfVictory": {
                game.setNumberOfVictory(0);
                break;
            }
            default: response = "'" + action + "' not recognized!";
        }

        return response;
    }

    public void disconnect(String user) {
        try {
            log("Disconnecting user '" + user + "', IP: " + getIp());

            if (getConnection() != null) getConnection().close();
            setGame(null);

            server.onDisconnectPlayer();
        } catch (Exception e) {
            throw new RuntimeException("Something went wrong trying to disconnect a player!");
        }
    }

    public void log(String msg) {
        System.out.println(new Date().toString() + "\n" + msg + "\n");
    }

    public void setGame(Game game) {
        if (game == null) this.game.setAvailable(true);
        else game.setAvailable(false);

        this.game = game;
    }

    public void setEnemy(Game enemy) {
        this.enemy = enemy;
    }

    private void changeTurns() {
        Boolean isMyTurn = game.getMyTurn();

        game.setMyTurn(!isMyTurn);
        enemy.setMyTurn(isMyTurn);
    }

    public String getMultiplayerGuessesHistoric() {
        String response = "";

        for (Integer guess: game.getGuessesHistoric())
            response += guess + " - " + game.countShot(guess) + "t" + game.countFly(guess) + "m       enemy";

        if (enemy.getGuessesHistoric().size() == 0) response = response.replaceFirst("enemy", ";");
        for (Integer guess: enemy.getGuessesHistoric())
            response = response.replaceFirst("enemy", guess + " - " + game.countShot(guess) + "t" + game.countFly(guess) + "m;");

        return response;
    }

}
