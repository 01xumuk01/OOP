package ru.nsu.oop.model;

/**
 * Неизменяемая карта с заданными мастью и достоинством.
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
        return rank.getRussianName() + " " + suit.getRussianName()
                + " (" + rank.getPoints() + ")";
    }
}

