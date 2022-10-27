package server;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Game {
    private String user;
    private int numberToGuess;
    private int totalRounds;
    private ArrayList<Integer> guessesHistoric;
    private Boolean isAvailable = true;
    private int gameMode = 0;
    private Boolean isMyTurn;
    private Boolean loser;
    private int numberOfVictory;

    public Game() { }

    public void resetGame(String user) {
        this.user = user;
        numberToGuess = -1;
        totalRounds = 0;
        guessesHistoric = new ArrayList<Integer>();
        gameMode = 0;
        loser = false;
    }

    public String initOnePlayer() {
        gameMode = 1;
        numberToGuess = generateRandomNumber();

        return String.valueOf(numberToGuess);
    }

    private int generateRandomNumber() {
        int number = ThreadLocalRandom.current().nextInt(100, 999);
        String[] numberStr = String.valueOf(number).split("");

        while (!(
                !numberStr[0].equals(numberStr[1])
                && !numberStr[1].equals(numberStr[2])
                && !numberStr[0].equals(numberStr[2])
        )) {
            number = ThreadLocalRandom.current().nextInt(100, 999);
            numberStr = String.valueOf(number).split("");
        }


        return number;
    }

    public String guessesHistoricToString() {
        String result = "";

        for (Integer guess: guessesHistoric)
            result += guess + " - " + countShot(guess) + "t" + countFly(guess) + "m;";

        return result;
    }

    public int countShot(int guess) {
        int result = 0;
        String guessStr = String.valueOf(guess);
        String numberToGuessStr = String.valueOf(numberToGuess);

        if (guessStr.contains(numberToGuessStr.split("")[0])) result++;
        if (guessStr.contains(numberToGuessStr.split("")[1])) result++;
        if (guessStr.contains(numberToGuessStr.split("")[2])) result++;

        return result - countFly(guess);
    }

    public int countFly(int guess) {
        int result = 0;
        String guessStr = String.valueOf(guess);
        String numberToGuessStr = String.valueOf(numberToGuess);

        if (guessStr.split("")[0].equals(numberToGuessStr.split("")[0])) result++;
        if (guessStr.split("")[1].equals(numberToGuessStr.split("")[1])) result++;
        if (guessStr.split("")[2].equals(numberToGuessStr.split("")[2])) result++;

        return result;
    }

    public Boolean makeGuess(int guess, Game enemy) {
        if (guess != 0) {
            totalRounds++;
            guessesHistoric.add(guess);
        }

        if (guess == numberToGuess) {
            numberOfVictory++;
            enemy.setLoser(true);
            return true;
        } return false;
    }

    public int getTotalRounds() {
        return totalRounds;
    }

    public String getUser() {
        return user;
    }

    public Boolean getAvailable() {
        return isAvailable;
    }

    public void setAvailable(Boolean available) {
        isAvailable = available;
    }

    public String multiplayer(Game enemy) {
        gameMode = 2;

        if (enemy.getGameMode() == 2) return "ready;" + user + ";" + enemy.getUser();
        else return "wait";
    }

    public int getGameMode() {
        return gameMode;
    }

    public void setNumberToGuess(int numberToGuess) {
        this.numberToGuess = numberToGuess;
    }

    public Boolean getMyTurn() {
        return isMyTurn;
    }

    public void setMyTurn(Boolean myTurn) {
        isMyTurn = myTurn;
    }

    public ArrayList<Integer> getGuessesHistoric() {
        return guessesHistoric;
    }

    public Boolean getLoser() {
        return loser;
    }

    public void setLoser(Boolean loser) {
        this.loser = loser;
    }

    public int getNumberOfVictory() {
        return numberOfVictory;
    }

    public void setNumberOfVictory(int numberOfVictory) {
        this.numberOfVictory = numberOfVictory;
    }

}
