package ru.nsu.oop.model;

import java.util.List;

/**
 * Участник игры, владеющий одной рукой.
 */
public class Player {
    private final Hand hand = new Hand();

    /**
     * Добавляет выданную карту в руку участника.
     *
     * @param card выданная карта
     */
    public void receiveCard(Card card) {
        hand.addCard(card);
    }

    /**
     * Возвращает снимок списка карт участника.
     *
     * @return неизменяемый список карт
     */
    public List<Card> getCards() {
        return hand.getCards();
    }

    /**
     * Возвращает сумму очков с учётом тузов.
     *
     * @return очки участника
     */
    public int getPoints() {
        return hand.getPoints();
    }

    /**
     * Проверяет перебор.
     *
     * @return true, если сумма превышает 21
     */
    public boolean isBust() {
        return hand.isBust();
    }

    /**
     * Проверяет начальный блэкджек.
     *
     * @return true, если две карты дают 21 очко
     */
    public boolean isBlackjack() {
        return hand.isBlackjack();
    }
}
