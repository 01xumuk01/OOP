package ru.nsu.oop.model;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.Set;
import org.junit.jupiter.api.Test;

class CardTest {

    @Test
    void constructorPreservesEverySuitAndRank() {
        for (Suit suit : Suit.values()) {
            for (Rank rank : Rank.values()) {
                Card card = new Card(suit, rank);

                assertSame(suit, card.getSuit());
                assertSame(rank, card.getRank());
            }
        }
    }

    @Test
    void differentCardsAreIndependent() {
        Card first = new Card(Suit.SPADES, Rank.ACE);
        Card second = new Card(Suit.HEARTS, Rank.TWO);

        assertSame(Suit.SPADES, first.getSuit());
        assertSame(Rank.ACE, first.getRank());
        assertSame(Suit.HEARTS, second.getSuit());
        assertSame(Rank.TWO, second.getRank());
    }

    @Test
    void queenToString() {
        Card card = new Card(Suit.SPADES, Rank.QUEEN);

        assertEquals("Дама пик (10)", card.toString());
    }

    @Test
    void aceToString() {
        Card card = new Card(Suit.HEARTS, Rank.ACE);

        assertEquals("Туз червей (11)", card.toString());
    }

    @Test
    void numericCardToString() {
        Card card = new Card(Suit.DIAMONDS, Rank.FIVE);

        assertEquals("Пятёрка бубен (5)", card.toString());
    }

    @Test
    void exactlyFourExpectedSuits() {
        assertEquals(4, Suit.values().length);
        assertEquals(
                Set.of(Suit.SPADES, Suit.DIAMONDS, Suit.CLUBS, Suit.HEARTS),
                Set.of(Suit.values())
        );
    }

    @Test
    void exactlyThirteenRanks() {
        assertEquals(13, Rank.values().length);
    }

    @Test
    void everyRankHasCorrectPoints() {
        assertAll(
                () -> assertEquals(11, Rank.ACE.getPoints()),
                () -> assertEquals(2, Rank.TWO.getPoints()),
                () -> assertEquals(3, Rank.THREE.getPoints()),
                () -> assertEquals(4, Rank.FOUR.getPoints()),
                () -> assertEquals(5, Rank.FIVE.getPoints()),
                () -> assertEquals(6, Rank.SIX.getPoints()),
                () -> assertEquals(7, Rank.SEVEN.getPoints()),
                () -> assertEquals(8, Rank.EIGHT.getPoints()),
                () -> assertEquals(9, Rank.NINE.getPoints()),
                () -> assertEquals(10, Rank.TEN.getPoints()),
                () -> assertEquals(10, Rank.JACK.getPoints()),
                () -> assertEquals(10, Rank.QUEEN.getPoints()),
                () -> assertEquals(10, Rank.KING.getPoints())
        );
    }
}
