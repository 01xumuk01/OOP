package ru.nsu.oop;

import java.util.ArrayList;
import java.util.List;

/**
 * Класс, отвечающий за карты на руках, сумму очков, проверку перебора и блэкджека.
 */
public class Hand {
    private static final int MAX_POINTS = 21;
    private static final int BLACKJACK_CARD_COUNT = 2;
    private static final int ACE_REDUCTION = 10;

    private final List<Card> cards;

    public Hand() {
        cards = new ArrayList<>();
    }

    public void addCard(Card card) {
        cards.add(card);
    }

    /**
     * Метод, который считает очки на руке.
     *
     * @return Количество очков на руке.
     */
    public int getPoints() {
        int points = 0;
        int aceCount = 0;

        for (Card card : cards) {
            points += card.getRank().getPoints();

            if (card.getRank() == Rank.ACE) {
                aceCount += 1;
            }
        }

        while (points > MAX_POINTS && aceCount > 0) {
            points -= ACE_REDUCTION;
            aceCount -= 1;
        }

        return points;
    }

    public boolean isBust() {
        return (getPoints() > MAX_POINTS);
    }

    public boolean isBlackjack() {
        return ((getPoints() == MAX_POINTS) && (cards.size() == BLACKJACK_CARD_COUNT));
    }

    public List<Card> getCards() {
        return List.copyOf(cards);
    }
}