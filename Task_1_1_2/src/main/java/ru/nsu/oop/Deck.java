package ru.nsu.oop;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Класс, отвечающий за создание набора карт, перемешивание и выдачу карт.
 */
public class Deck {
    private final List<Card> cards;

    /**
     * Конструктор, который создает колоду и перемешивает в ней карты.
     */
    public Deck() {
        cards = new ArrayList<>();

        for (Suit suit : Suit.values()) {
            for (Rank rank : Rank.values()) {
                Card card = new Card(suit, rank);
                cards.add(card);
            }
        }

        Collections.shuffle(cards);
    }

    /**
     * Метод, который достает из колоды одну карту, удаляя её.
     *
     * @return Одна карта из колоды.
     */
    public Card dealCard() {

        Card card = cards.remove(cards.size() - 1);

        return card;
    }
}
