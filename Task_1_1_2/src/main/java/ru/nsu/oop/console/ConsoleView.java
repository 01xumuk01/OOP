package ru.nsu.oop.console;

import ru.nsu.oop.game.RoundResult;
import ru.nsu.oop.model.Card;
import ru.nsu.oop.model.Dealer;
import ru.nsu.oop.model.Player;

/**
 * Формирует и выводит все сообщения консольной игры.
 */
public class ConsoleView {
    /** Выводит приветствие. */
    public void printWelcome() {
        printMessage("Добро пожаловать в Блэкджек!");
    }

    /**
     * Выводит номер начавшегося раунда.
     *
     * @param number номер раунда
     */
    public void printRoundNumber(int number) {
        printMessage("\nРаунд " + number);
    }

    /** Сообщает о завершении начальной раздачи. */
    public void printCardsDealt() {
        printMessage("Дилер раздал карты");
    }

    /**
     * Выводит карты и сумму игрока.
     *
     * @param player игрок
     */
    public void printPlayerHand(Player player) {
        printHand("Ваши карты", player);
    }

    /**
     * Выводит открытую руку дилера.
     *
     * @param dealer дилер
     */
    public void printDealerHand(Dealer dealer) {
        printHand("Карты дилера", dealer);
    }

    /**
     * Выводит только первую карту дилера, без общей суммы.
     *
     * @param dealer дилер после начальной раздачи
     */
    public void printDealerInitialHand(Dealer dealer) {
        Card openCard = dealer.getCards().get(0);
        printMessage("Карты дилера: [" + openCard + ", <закрытая карта>]");
    }

    /** Объявляет ход игрока. */
    public void printPlayerTurn() {
        printMessage("--------\nВаш ход\n--------");
    }

    /** Объясняет команды хода игрока. */
    public void printPlayerPrompt() {
        printMessage("Введите 1, чтобы взять карту, или 0, чтобы остановиться");
    }

    /** Сообщает о неизвестной команде. */
    public void printUnknownCommand() {
        printMessage("Неизвестная команда");
    }

    /** Сообщает о переборе игрока. */
    public void printPlayerBust() {
        printMessage("Перебор!");
    }

    /** Объявляет ход дилера. */
    public void printDealerTurn() {
        printMessage("--------\nХод дилера\n--------");
    }

    /** Сообщает о переборе дилера. */
    public void printDealerBust() {
        printMessage("У дилера перебор!");
    }

    /** Сообщает об остановке дилера. */
    public void printDealerStand() {
        printMessage("Дилер остановился");
    }

    /** Объясняет продолжение и завершение игры. */
    public void printContinuePrompt() {
        printMessage("Enter — следующий раунд, 0 — выход");
    }

    /**
     * Выводит уже определённый правилами результат.
     *
     * @param result итог раунда
     */
    public void printResult(RoundResult result) {
        switch (result) {
            case PLAYER_WIN -> printMessage("Вы выиграли!");
            case DEALER_WIN -> printMessage("Вы проиграли!");
            case DRAW -> printMessage("Ничья");
        }
    }

    /**
     * Выводит общий счёт.
     *
     * @param dealerScore число побед дилера
     * @param playerScore число побед игрока
     */
    public void printScore(int dealerScore, int playerScore) {
        if (dealerScore > playerScore) {
            printMessage("Счёт " + dealerScore + " : " + playerScore + " в пользу дилера.");
        } else if (dealerScore < playerScore) {
            printMessage("Счёт " + dealerScore + " : " + playerScore + " в Вашу пользу.");
        } else {
            printMessage("Счёт " + dealerScore + " : " + playerScore + ".");
        }
    }

    private void printHand(String owner, Player player) {
        printMessage(owner + ": " + player.getCards() + " => " + player.getPoints());
    }

    private void printMessage(String message) {
        System.out.println(message);
    }
}
