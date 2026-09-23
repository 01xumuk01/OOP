package ru.nsu.oop.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

/** Проверяет владение рукой и правило добора дилера. */
class PlayerDealerTest {
    @Test
    void playerStartsWithEmptyHand() {
        Player player = new Player();
        assertEquals(0, player.getPoints());
        assertTrue(player.getCards().isEmpty());
        assertFalse(player.isBlackjack());
        assertFalse(player.isBust());
    }

    @Test
    void playerReceivesTheGivenCard() {
        Player player = new Player();
        Card card = new Card(Suit.HEARTS, Rank.ACE);
        player.receiveCard(card);
        assertSame(card, player.getCards().get(0));
        assertEquals(11, player.getPoints());
    }

    @Test
    void playersHaveIndependentHands() {
        Player first = new Player();
        Player second = new Player();
        give(first, Rank.ACE, Rank.KING);
        assertTrue(first.isBlackjack());
        assertEquals(0, second.getPoints());
        assertTrue(second.getCards().isEmpty());
    }

    @Test
    void cardsCannotBeRemovedThroughReturnedList() {
        Player player = new Player();
        give(player, Rank.TEN);
        List<Card> snapshot = player.getCards();
        assertThrows(UnsupportedOperationException.class, snapshot::clear);
        give(player, Rank.SEVEN);
        assertEquals(1, snapshot.size());
        assertEquals(2, player.getCards().size());
    }

    @Test
    void playerUsesAdjustedAceValue() {
        Player player = new Player();
        give(player, Rank.ACE, Rank.SIX, Rank.EIGHT);
        assertEquals(15, player.getPoints());
        assertFalse(player.isBust());
        assertFalse(player.isBlackjack());
    }

    @Test
    void playerRecognizesBust() {
        Player player = new Player();
        give(player, Rank.KING, Rank.QUEEN, Rank.TWO);
        assertTrue(player.isBust());
        assertFalse(player.isBlackjack());
    }

    @Test
    void dealerHitsAtSixteenAndStandsAtSeventeen() {
        Dealer dealer = new Dealer();
        give(dealer, Rank.TEN, Rank.SIX);
        assertTrue(dealer.shouldHit());
        give(dealer, Rank.ACE);
        assertEquals(17, dealer.getPoints());
        assertFalse(dealer.shouldHit());
    }

    @Test
    void dealerStandsOnSoftSeventeen() {
        Dealer dealer = new Dealer();
        give(dealer, Rank.ACE, Rank.SIX);
        assertFalse(dealer.shouldHit());
    }

    @Test
    void dealerHitsAfterAceChangesToOne() {
        Dealer dealer = new Dealer();
        give(dealer, Rank.ACE, Rank.FIVE, Rank.TEN);
        assertEquals(16, dealer.getPoints());
        assertTrue(dealer.shouldHit());
    }

    @Test
    void dealerStopsAfterBust() {
        Dealer dealer = new Dealer();
        give(dealer, Rank.TEN, Rank.SIX, Rank.KING);
        assertTrue(dealer.isBust());
        assertFalse(dealer.shouldHit());
    }

    private void give(Player player, Rank... ranks) {
        for (Rank rank : ranks) {
            player.receiveCard(new Card(Suit.SPADES, rank));
        }
    }
}
