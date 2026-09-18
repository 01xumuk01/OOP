package ru.nsu.oop;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DeckTest {

    @Test
    void canDealFiftyTwoNonNullCards() {
        Deck deck = new Deck();

        for (int i = 0; i < 52; i++) {
            assertNotNull(deck.dealCard(), "Карта номер " + (i + 1));
        }
    }

    @Test
    void everySuitRankCombinationAppearsExactlyOnce() {
        Deck deck = new Deck();
        Set<String> combinations = new HashSet<>();

        for (int i = 0; i < 52; i++) {
            Card card = deck.dealCard();
            String key = card.getSuit().name() + ":" + card.getRank().name();

            assertTrue(combinations.add(key), "Повтор карты: " + key);
        }

        for (Suit suit : Suit.values()) {
            for (Rank rank : Rank.values()) {
                String key = suit.name() + ":" + rank.name();
                assertTrue(combinations.contains(key), "Нет карты: " + key);
            }
        }

        assertEquals(52, combinations.size());
    }

    @Test
    void eachSuitHasThirteenCards() {
        Deck deck = new Deck();
        int[] counts = new int[Suit.values().length];

        for (int i = 0; i < 52; i++) {
            counts[deck.dealCard().getSuit().ordinal()]++;
        }

        for (Suit suit : Suit.values()) {
            assertEquals(13, counts[suit.ordinal()], suit.name());
        }
    }

    @Test
    void eachRankHasFourCards() {
        Deck deck = new Deck();
        int[] counts = new int[Rank.values().length];

        for (int i = 0; i < 52; i++) {
            counts[deck.dealCard().getRank().ordinal()]++;
        }

        for (Rank rank : Rank.values()) {
            assertEquals(4, counts[rank.ordinal()], rank.name());
        }
    }

    @Test
    void totalBasePointsAreThreeHundredEighty() {
        Deck deck = new Deck();
        int total = 0;

        for (int i = 0; i < 52; i++) {
            total += deck.dealCard().getRank().getPoints();
        }

        assertEquals(380, total);
    }

    @Test
    void exhaustedDeckThrowsException() {
        Deck deck = new Deck();

        for (int i = 0; i < 52; i++) {
            deck.dealCard();
        }

        // Именно такое поведение сейчас у твоего dealCard().
        assertThrows(IndexOutOfBoundsException.class, deck::dealCard);
    }

    @Test
    void decksAreIndependent() {
        Deck first = new Deck();
        Deck second = new Deck();

        for (int i = 0; i < 52; i++) {
            first.dealCard();
        }

        for (int i = 0; i < 52; i++) {
            assertNotNull(second.dealCard());
        }
    }
}