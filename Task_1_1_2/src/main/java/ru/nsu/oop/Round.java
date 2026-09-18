package ru.nsu.oop;

/**
 * Класс, отвечающий за проведение одного раунда по правилам.
 */
public class Round {
    private final Deck deck;
    private final Hand playerHand;
    private final Hand dealerHand;
    private final ConsoleView view;
    private final ConsoleInput input;

    private static final int DEALER_STAND_POINTS = 17;

    /**
     * Конструктор раунда, который инициализирует колоду, руку дилера и руку игрока.
     */
    public Round(ConsoleInput input, ConsoleView view) {
        deck = new Deck();
        playerHand = new Hand();
        dealerHand = new Hand();
        this.input = input;
        this.view = view;
    }

    private void dealInitialCard() {

        view.printMessage("Дилер раздал карты");

        for (int i = 0; i < 2; i++) {
            playerHand.addCard(deck.dealCard());
            dealerHand.addCard(deck.dealCard());
        }
    }

    /**
     * Метод, который запускает один раунд.
     *
     * @return кто выиграл.
     */
    public RoundResult play() {
        dealInitialCard();
        view.printDealerInitialHand(dealerHand);
        view.printHand("Ваши карты", playerHand);

        if (playerHand.isBlackjack() || dealerHand.isBlackjack()) {
            view.printHand("Карты дилера", dealerHand);
            return printResult();
        }

        playerTurn();

        if (!playerHand.isBust()) {
            dealerTurn();
        }

        return printResult();
    }

    private void playerTurn() {
        view.printMessage("--------\nВаш ход\n--------");

        while (!playerHand.isBust()) {
            view.printMessage("Введите 1, чтобы взять карту, или 0, чтобы остановиться");
            String command = input.readCommand();

            if (command.equals("1")) {
                playerHand.addCard(deck.dealCard());
                view.printHand("Ваши карты", playerHand);
            }
            else if (command.equals("0")) {
                return;
            }
            else {
                view.printMessage("Неизвестная команда");
            }
        }

        view.printMessage("Перебор!");
    }

    private void dealerTurn() {
        view.printMessage("--------\nХод дилера\n--------");

        view.printHand("Карты дилера", dealerHand);

        while (dealerHand.getPoints() < DEALER_STAND_POINTS) {
            dealerHand.addCard(deck.dealCard());
            view.printHand("Карты дилера", dealerHand);
        }

        if (dealerHand.isBust()) {
            view.printMessage("У дилера перебор!");
        }
        else {
            view.printMessage("Дилер остановился");
        }
    }

    private RoundResult printResult() {
        int playerPoints = playerHand.getPoints();
        int dealerPoints = dealerHand.getPoints();

        if (playerHand.isBust()) {
            view.printMessage("Вы проиграли!");
            return RoundResult.DEALER_WIN;
        }
        else if (dealerHand.isBust()) {
            view.printMessage("Вы выиграли!");
            return RoundResult.PLAYER_WIN;
        }
        else if (playerHand.isBlackjack() && !dealerHand.isBlackjack()) {
            view.printMessage("Блэкджек! Вы выиграли! >:(");
            return RoundResult.PLAYER_WIN;
        }
        else if (!playerHand.isBlackjack() && dealerHand.isBlackjack()) {
            view.printMessage("Блэкджек у дилера. Вы проиграли!! XD XD");
            return RoundResult.DEALER_WIN;
        }
        else if (playerHand.getPoints() > dealerHand.getPoints()) {
            view.printMessage("Вы выиграли!");
            return RoundResult.PLAYER_WIN;
        }
        else if (playerHand.getPoints() < dealerHand.getPoints()) {
            view.printMessage("Вы проиграли!");
            return RoundResult.DEALER_WIN;
        }
        else {
            view.printMessage("Ничья");
            return RoundResult.DRAW;
        }
    }
}
