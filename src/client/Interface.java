package client;

import java.util.Scanner;

public class Interface {
    private Scanner scanner;

    public Interface() {
        scanner = new Scanner(System.in);
    }

    public String readServerIp() {
        System.out.print("Informe o IP do servidor(localhost): ");

        try {
            String ipAddress = scanner.nextLine();
            if (ipAddress.isEmpty()) return "localhost";
            return ipAddress;
        } catch (Exception e) {
            throw new RuntimeException("Something went wrong trying to read IP address!");
        }
    }

    public String readUser() {
        System.out.print("Informe o seu nome: ");

        try {
            String name = scanner.nextLine();
            return name;
        } catch (Exception e) {
            throw new RuntimeException("Something went wrong trying to read name!");
        }
    }

    private int readInt(String errorMsg) {
        while (true) {
            try {
                int result = scanner.nextInt();
                return result;
            } catch (Exception e) {
                System.out.print(errorMsg);
            } finally {
                scanner.nextLine();
            }
        }
    }

    public void welcome(String user) {
        System.out.println("\nBem-vindo, " + user + "!\n");
    }

    public void bye(String user) {
        if (user == null) System.out.println("\nAte a proxima!");
        else System.out.println("\nAte a proxima, " + user + "!");
    }

    public int printMenu() {
        System.out.println("---------- MENU -----------");
        System.out.println("- 1. Jogar sozinho        -");
        System.out.println("- 2. Dois jogadores       -");
        System.out.println("- 3. Contra o computador  -");
        System.out.println("- 4. Sair                 -");
        System.out.println("---------------------------");
        System.out.print("Escolha: ");

        while (true) {
            String errorMsg = "Valor invalido!\nEscolha: ";
            int option = readInt(errorMsg);

            if (validateMenuOption(option)) return option;
            else System.out.print(errorMsg);
        }
    }

    private Boolean validateMenuOption(int option) {
        switch (option) {
            case 1:
            case 2:
            case 3:
            case 4:
                return true;
            default: return false;
        }
    }

    public void printServerUnavailable() {
        System.out.println("Servidor indisponivel, tente novamente mais tarde.");
    }

    public void printOption1(int numberToGuess) {
        System.out.println("\nJogar sozinho - " + numberToGuess);
        System.out.println("Informe 0 para desistir\n");
    }

    public int readGuess(String guessesHistoric, Boolean isMultiplayer) {
        if (!guessesHistoric.isEmpty()) {
            if (isMultiplayer) System.out.println("Lista de palpites:\n(Voce)     /     (Adversario)\n" + guessesHistoric);
            else System.out.println("Lista de palpites:\n" + guessesHistoric);
        }

        System.out.print("Informe o seu palpite: ");

        while (true) {
            String errorMsg = "Informe um valor de 100 a 999!\nInforme o seu palpite: ";
            int guess = readInt(errorMsg);

            if (validateGuess(guess)) return guess;
            else System.out.print(errorMsg);
        }
    }

    private Boolean validateGuess(int guess) {
        if ((guess >= 100 && guess <= 999) || guess == 0) return true;
        return false;
    }

    public void printOption1EndGame(int lastGuess, int numberToGuess, int totalRounds) {
        if (lastGuess == numberToGuess)
            System.out.println("\nAcertou em " + totalRounds + " tentativa(s), o numero era: " + numberToGuess + "\n");
        else
            System.out.println("\nDesistencia! O numero era: " + numberToGuess + "\n");
    }

    public void printOption2() {
        System.out.print("\nDois jogadores\nAguardando adversario...");
    }

    public void printEnemyFound(String player1, String player2) {
        System.out.println(" adversario encontrado!");
        System.out.println("Partida " + player1 + " X " + player2);
    }

    public String readEnemyNumberToGuess() {
        System.out.print("\nInforme o numero do adversario: ");

        while (true) {
            String errorMsg = "Valor invalido!\nInforme o numero do adversario: ";
            int numberToGuess = readInt(errorMsg);

            if (validateNumberToGuess(numberToGuess)) return String.valueOf(numberToGuess);
            else System.out.print(errorMsg);
        }
    }

    public Boolean validateNumberToGuess(int numberToGuess) {
        String[] numberToGuessStr = String.valueOf(numberToGuess).split("");

        if (numberToGuess < 100) return false;
        if (numberToGuess > 999) return false;
        if (numberToGuessStr[0].equals(numberToGuessStr[1])) return false;
        if (numberToGuessStr[1].equals(numberToGuessStr[2])) return false;
        if (numberToGuessStr[0].equals(numberToGuessStr[2])) return false;
        return true;
    }

    public void printWaitEnemy() {
        System.out.println("Aguardando adversario...\n");
    }

    public void printOption2EndGame(int lastGuess, Boolean didIWin, Boolean didILost, int totalRounds, String player1Victory, String player2Victory) {
        if (didIWin) System.out.println("\nParabens! O numero era: " + lastGuess + "\nVoce acertou primeiro em " + totalRounds + " tentativas!");
        if (didILost) System.out.println("Que pena... o seu adversario acertou primeiro!");

        System.out.println("\nVoce tem " + player1Victory + " vitoria(s)");
        System.out.println("O adversario tem " + player2Victory + " vitoria(s)\n");
    }

    public Boolean keepPlaying() {
        System.out.println("Deseja continuar jogando em multiplayer? (Sim - 1 / Nao - 0)");
        System.out.print("Informe: ");

        int choice;
        while (true) {
            String errorMsg = "Informe 0 ou 1!\nInforme: ";
            choice = readInt(errorMsg);

            if (validateChoice(choice)) break;
            else System.out.print(errorMsg);
        }

        if (choice == 0) return false;
        else return true;
    }

    private Boolean validateChoice(int choice) {
        if (choice == 1 || choice == 0) return true;
        return false;
    }

    public void printNotFound() {
        System.out.println("\nModo de jogo nao implementado!\n");
    }

    public void printEnemyDisconnected() {
        System.out.println("O seu adversario saiu do modo multiplayer. Voltando ao menu...\n");
    }

}
