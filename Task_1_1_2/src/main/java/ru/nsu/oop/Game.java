package ru.nsu.oop;

/**
 * Класс, отвечающий за последовательсть раундов игры и общий счет.
 */
public class Game {
    private final ConsoleInput input;
    private final ConsoleView view;

    private int playerScore;
    private int dealerScore;

    private int roundNumber;

    public Game() {
        input = new ConsoleInput();
        view = new ConsoleView();
        playerScore = 0;
        dealerScore = 0;

        roundNumber = 1;

        view.printMessage("Добро пожаловать в Блэкджек!");
    }

    public void play() {
        while (true) {
            view.printMessage("\n\n\nРаунд " + roundNumber + "\n");
            roundNumber += 1;

            Round round = new Round(input, view);
            RoundResult result = round.play();

            if (result == RoundResult.DEALER_WIN) {
                dealerScore += 1;
            } else if (result == RoundResult.PLAYER_WIN) {
                playerScore += 1;
            }

            view.printScore(dealerScore, playerScore);

            view.printMessage("Enter — следующий раунд, 0 — выход");
            if (input.readCommand().equals("0")) {
                return;
            }
        }
    }
}
