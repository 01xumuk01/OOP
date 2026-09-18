package ru.nsu.oop;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

class HandTest {

    // Вспомогательный метод: создаёт руку с заданными достоинствами.
    private Hand hand(Rank... ranks) {
        Hand hand = new Hand();
        for (Rank rank : ranks) {
            hand.addCard(new Card(Suit.SPADES, rank));
        }
        return hand;
    }

    @Test
    void emptyHand() {
        Hand hand = new Hand();

        assertEquals(0, hand.getPoints());
        assertTrue(hand.getCards().isEmpty());
        assertFalse(hand.isBust());
        assertFalse(hand.isBlackjack());
    }

    @Test
    void addingCardPreservesItsIdentity() {
        Hand hand = new Hand();
        Card card = new Card(Suit.HEARTS, Rank.FIVE);

        hand.addCard(card);

        assertEquals(1, hand.getCards().size());
        assertSame(card, hand.getCards().get(0));
        assertEquals(5, hand.getPoints());
    }

    @Test
    void cardsPreserveInsertionOrder() {
        Hand hand = hand(Rank.THREE, Rank.ACE, Rank.KING);

        assertEquals(Rank.THREE, hand.getCards().get(0).getRank());
        assertEquals(Rank.ACE, hand.getCards().get(1).getRank());
        assertEquals(Rank.KING, hand.getCards().get(2).getRank());
    }

    @Test
    void ordinaryCardsAreSummed() {
        assertEquals(17, hand(Rank.TWO, Rank.SEVEN, Rank.EIGHT).getPoints());
    }

    @Test
    void faceCardsCostTen() {
        for (Rank rank : List.of(Rank.JACK, Rank.QUEEN, Rank.KING)) {
            assertEquals(10, hand(rank).getPoints(), rank.name());
        }
    }

    @Test
    void singleAceCostsEleven() {
        Hand hand = hand(Rank.ACE);

        assertEquals(11, hand.getPoints());
        assertFalse(hand.isBlackjack());
    }

    @Test
    void aceRemainsElevenWhenPossible() {
        assertEquals(20, hand(Rank.ACE, Rank.NINE).getPoints());
    }

    @Test
    void aceBecomesOneToAvoidBust() {
        assertEquals(15, hand(Rank.ACE, Rank.SIX, Rank.EIGHT).getPoints());
    }

    @Test
    void twoAcesCostTwelve() {
        assertEquals(12, hand(Rank.ACE, Rank.ACE).getPoints());
    }

    @Test
    void threeAcesCostThirteen() {
        assertEquals(13, hand(Rank.ACE, Rank.ACE, Rank.ACE).getPoints());
    }

    @Test
    void fourAcesCostFourteen() {
        assertEquals(14,
                hand(Rank.ACE, Rank.ACE, Rank.ACE, Rank.ACE).getPoints());
    }

    @Test
    void twoAcesAndNineGiveTwentyOneWithoutBlackjack() {
        Hand hand = hand(Rank.ACE, Rank.ACE, Rank.NINE);

        assertEquals(21, hand.getPoints());
        assertFalse(hand.isBust());
        assertFalse(hand.isBlackjack());
    }

    @Test
    void bothAcesCanBecomeOne() {
        assertEquals(12, hand(Rank.ACE, Rank.ACE, Rank.KING).getPoints());
    }

    @Test
    void allFourAcesCanBecomeOne() {
        assertEquals(13,
                hand(Rank.ACE, Rank.ACE, Rank.ACE, Rank.ACE,
                        Rank.NINE).getPoints());
    }

    @Test
    void acesDoNotAlwaysSaveFromBust() {
        Hand hand = hand(Rank.ACE, Rank.ACE, Rank.KING, Rank.QUEEN);

        assertEquals(22, hand.getPoints());
        assertTrue(hand.isBust());
        assertFalse(hand.isBlackjack());
    }

    @Test
    void blackjackWithEveryTenPointRankInBothOrders() {
        for (Rank rank : List.of(Rank.TEN, Rank.JACK, Rank.QUEEN, Rank.KING)) {
            Hand aceFirst = hand(Rank.ACE, rank);
            Hand aceLast = hand(rank, Rank.ACE);

            assertTrue(aceFirst.isBlackjack(), "ACE + " + rank);
            assertTrue(aceLast.isBlackjack(), rank + " + ACE");
            assertFalse(aceFirst.isBust());
            assertEquals(21, aceFirst.getPoints());
        }
    }

    @Test
    void twentyPointsAreNotBlackjack() {
        Hand hand = hand(Rank.KING, Rank.QUEEN);

        assertEquals(20, hand.getPoints());
        assertFalse(hand.isBlackjack());
        assertFalse(hand.isBust());
    }

    @Test
    void twentyOneWithThreeCardsIsNotBlackjack() {
        Hand hand = hand(Rank.SEVEN, Rank.SEVEN, Rank.SEVEN);

        assertEquals(21, hand.getPoints());
        assertFalse(hand.isBlackjack());
        assertFalse(hand.isBust());
    }

    @Test
    void twentyTwoIsBust() {
        Hand hand = hand(Rank.KING, Rank.QUEEN, Rank.TWO);

        assertEquals(22, hand.getPoints());
        assertTrue(hand.isBust());
    }

    @Test
    void largeTotalWithoutAcesIsNotReduced() {
        Hand hand = hand(Rank.KING, Rank.QUEEN, Rank.JACK, Rank.TEN);

        assertEquals(40, hand.getPoints());
        assertTrue(hand.isBust());
    }

    @Test
    void addingCardRecalculatesAceValue() {
        Hand hand = hand(Rank.ACE, Rank.SIX);
        assertEquals(17, hand.getPoints());

        hand.addCard(new Card(Suit.HEARTS, Rank.EIGHT));
        assertEquals(15, hand.getPoints());
    }

    @Test
    void addingThirdCardRemovesBlackjackStatus() {
        Hand hand = hand(Rank.ACE, Rank.KING);
        assertTrue(hand.isBlackjack());

        hand.addCard(new Card(Suit.HEARTS, Rank.TEN));

        assertEquals(21, hand.getPoints());
        assertFalse(hand.isBlackjack());
    }

    @Test
    void repeatedCalculationsDoNotChangeHand() {
        Hand hand = hand(Rank.ACE, Rank.ACE, Rank.NINE);
        List<Card> before = hand.getCards();

        for (int i = 0; i < 10; i++) {
            assertEquals(21, hand.getPoints());
            assertFalse(hand.isBust());
            assertFalse(hand.isBlackjack());
        }

        assertEquals(before, hand.getCards());
    }

    @Test
    void calculationDoesNotChangeAceRankPoints() {
        Hand hand = hand(Rank.ACE, Rank.KING, Rank.FIVE);

        assertEquals(16, hand.getPoints());
        assertEquals(11, Rank.ACE.getPoints());
        assertEquals(11, hand.getCards().get(0).getRank().getPoints());
    }

    @Test
    void returnedCardsCannotBeModified() {
        Hand hand = hand(Rank.FIVE);
        List<Card> cards = hand.getCards();

        assertThrows(UnsupportedOperationException.class,
                () -> cards.add(new Card(Suit.CLUBS, Rank.KING)));
        assertThrows(UnsupportedOperationException.class,
                () -> cards.remove(0));
        assertThrows(UnsupportedOperationException.class,
                () -> cards.set(0, new Card(Suit.CLUBS, Rank.TWO)));

        assertEquals(1, hand.getCards().size());
        assertEquals(5, hand.getPoints());
    }

    @Test
    void returnedCardsAreSnapshot() {
        Hand hand = hand(Rank.FIVE);
        List<Card> snapshot = hand.getCards();

        hand.addCard(new Card(Suit.HEARTS, Rank.SIX));

        assertEquals(1, snapshot.size());
        assertEquals(2, hand.getCards().size());
    }

    @Test
    void differentHandsAreIndependent() {
        Hand first = hand(Rank.ACE);
        Hand second = hand(Rank.TWO);

        first.addCard(new Card(Suit.HEARTS, Rank.KING));

        assertEquals(21, first.getPoints());
        assertEquals(2, second.getPoints());
        assertEquals(1, second.getCards().size());
    }

    @Test
    void allTwoCardCombinations() {
        for (Rank first : Rank.values()) {
            for (Rank second : Rank.values()) {
                Hand hand = hand(first, second);

                int expected = first == Rank.ACE && second == Rank.ACE
                        ? 12
                        : first.getPoints() + second.getPoints();

                boolean blackjack =
                        (first == Rank.ACE && second.getPoints() == 10)
                                || (second == Rank.ACE && first.getPoints() == 10);

                String description = first + " + " + second;

                assertEquals(expected, hand.getPoints(), description);
                assertEquals(blackjack, hand.isBlackjack(), description);
                assertFalse(hand.isBust(), description);
            }
        }
    }

    @Test
    void cardOrderDoesNotAffectPoints() {
        for (Rank first : Rank.values()) {
            for (Rank second : Rank.values()) {
                for (Rank third : Rank.values()) {
                    int expected = hand(first, second, third).getPoints();

                    assertEquals(expected, hand(third, first, second).getPoints());
                    assertEquals(expected, hand(second, third, first).getPoints());
                }
            }
        }
    }
}