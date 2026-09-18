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

    /**
     * Конструктор, который инициализирует объект ввода и вывода, счет дилара и игрока, номер раунда и приветствует игрока.
     */
    public Game() {
        input = new ConsoleInput();
        view = new ConsoleView();
        playerScore = 0;
        dealerScore = 0;

        roundNumber = 1;

        view.printMessage("Добро пожаловать в Блэкджек!");
    }

    /**
     * Метод, который запускает игру BlackJack.
     */
    public void play() {
        while (true) {
            view.printMessage("\n\n\nРаунд " + roundNumber + "\n");
            roundNumber += 1;

            Round round = createRound();
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

    /**
     * Создаёт следующий раунд с общими вводом и выводом.
     *
     * @return новый раунд
     */
    Round createRound() {
        return new Round(input, view);
    }
}
