package ru.nsu.oop;

/**
 * Класс, отвечающий за вывод сообщений и карт.
 */
public class ConsoleView {
    public void printHand(String owner, Hand hand) {
        System.out.println(owner + ": " + hand.getCards()
                + " => " + hand.getPoints());
    }

    public void printMessage(String message) {
        System.out.println(message);
    }

    /**
     * Метод, который показывает руку дилера, скрывая вторую карту.
     */
    public void printDealerInitialHand(Hand hand) {
        Card openCard = hand.getCards().get(0);

        System.out.println(
                "Карты дилера: [" + openCard + ", <закрытая карта>]"
        );
    }

    /**
     * Метод, который печатает счёт игры.
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
}
