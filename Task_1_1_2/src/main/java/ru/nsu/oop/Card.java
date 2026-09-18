package ru.nsu.oop;

/**
 * Класс, отвечающий за данные карту: масть и достоинство.
 */
public class Card {

    private final Suit suit;
    private final Rank rank;

    public Card(Suit suit, Rank rank) {
        this.suit = suit;
        this.rank = rank;
    }

    public Suit getSuit() {
        return suit;
    }

    public Rank getRank() {
        return rank;
    }

    @Override
    public String toString() {
        return rank + " " + suit + " (" + rank.getPoints() + ")";
    }
}
