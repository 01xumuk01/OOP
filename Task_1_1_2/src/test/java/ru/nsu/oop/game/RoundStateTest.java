package ru.nsu.oop.game;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import ru.nsu.oop.model.Rank;
import ru.nsu.oop.support.BlackjackTestSupport;

/** Проверяет правила переходов раунда без консольного сценария Game. */
class RoundStateTest extends BlackjackTestSupport {
    @Test
    void constructorDoesNotDealCardsOrPrint() {
        FixedDeck deck = new FixedDeck();
        Round round = new Round(deck);
        assertEquals(RoundState.NEW, round.getState());
        assertEquals(0, deck.dealtCount());
        assertTrue(round.getPlayer().getCards().isEmpty());
        assertTrue(round.getDealer().getCards().isEmpty());
        assertEquals("", output());
    }

    @Test
    void actionsAndResultAreUnavailableBeforeStart() {
        Round round = new Round(new FixedDeck());
        assertThrows(IllegalStateException.class, round::playerHit);
        assertThrows(IllegalStateException.class, round::playerStand);
        assertThrows(IllegalStateException.class, round::dealerStep);
        assertThrows(IllegalStateException.class, round::getResult);
    }

    @Test
    void startDealsTwoCardsToEachInAlternatingOrder() {
        Round round = new Round(new FixedDeck(Rank.TEN, Rank.KING, Rank.NINE, Rank.SEVEN));
        round.start();
        assertEquals(RoundState.PLAYER_TURN, round.getState());
        assertEquals(2, round.getPlayer().getCards().size());
        assertEquals(2, round.getDealer().getCards().size());
        assertEquals(19, round.getPlayer().getPoints());
        assertEquals(17, round.getDealer().getPoints());
        assertEquals(Rank.TEN, round.getPlayer().getCards().get(0).getRank());
        assertEquals(Rank.KING, round.getDealer().getCards().get(0).getRank());
    }

    @Test
    void startedRoundCannotBeStartedAgain() {
        Round round = ordinaryRound();
        assertThrows(IllegalStateException.class, round::start);
        assertEquals(2, round.getPlayer().getCards().size());
    }

    @Test
    void dealerCannotActBeforePlayerStands() {
        Round round = ordinaryRound();
        assertThrows(IllegalStateException.class, round::dealerStep);
        assertThrows(IllegalStateException.class, round::getResult);
    }

    @Test
    void standingPassesTurnAndForbidsFurtherPlayerActions() {
        Round round = ordinaryRound();
        round.playerStand();
        assertEquals(RoundState.DEALER_TURN, round.getState());
        assertThrows(IllegalStateException.class, round::playerHit);
        assertThrows(IllegalStateException.class, round::playerStand);
        assertThrows(IllegalStateException.class, round::getResult);
    }

    @Test
    void standingDealerFinishesWithoutTakingCard() {
        Round round = ordinaryRound();
        round.playerStand();
        assertFalse(round.dealerStep());
        assertEquals(RoundState.FINISHED, round.getState());
        assertEquals(RoundResult.PLAYER_WIN, round.getResult());
        assertEquals(2, round.getDealer().getCards().size());
    }

    @Test
    void dealerStepTakesAtMostOneCardAndCanNeedAnotherStep() {
        Round round = new Round(new FixedDeck(
                Rank.TEN, Rank.TWO, Rank.NINE, Rank.THREE, Rank.FOUR, Rank.EIGHT));
        round.start();
        round.playerStand();
        assertTrue(round.dealerStep());
        assertEquals(9, round.getDealer().getPoints());
        assertEquals(RoundState.DEALER_TURN, round.getState());
        assertTrue(round.dealerStep());
        assertEquals(17, round.getDealer().getPoints());
        assertEquals(RoundState.FINISHED, round.getState());
        assertEquals(RoundResult.PLAYER_WIN, round.getResult());
    }

    @Test
    void ordinaryHitKeepsPlayerTurn() {
        Round round = new Round(new FixedDeck(
                Rank.FIVE, Rank.TEN, Rank.SIX, Rank.SEVEN, Rank.NINE));
        round.start();
        round.playerHit();
        assertEquals(20, round.getPlayer().getPoints());
        assertEquals(RoundState.PLAYER_TURN, round.getState());
        assertThrows(IllegalStateException.class, round::getResult);
    }

    @Test
    void bustFinishesImmediately() {
        Round round = new Round(new FixedDeck(
                Rank.TEN, Rank.TWO, Rank.QUEEN, Rank.THREE, Rank.FIVE));
        round.start();
        round.playerHit();
        assertEquals(RoundState.FINISHED, round.getState());
        assertEquals(RoundResult.DEALER_WIN, round.getResult());
        assertThrows(IllegalStateException.class, round::dealerStep);
    }

    @Test
    void finishedRoundRejectsAllFurtherActions() {
        Round round = new Round(new FixedDeck(Rank.ACE, Rank.TEN, Rank.KING, Rank.NINE));
        round.start();
        assertEquals(RoundState.FINISHED, round.getState());
        assertThrows(IllegalStateException.class, round::start);
        assertThrows(IllegalStateException.class, round::playerHit);
        assertThrows(IllegalStateException.class, round::playerStand);
        assertThrows(IllegalStateException.class, round::dealerStep);
        assertEquals(RoundResult.PLAYER_WIN, round.getResult());
    }

    @Test
    void fullRoundNeedsNoConsoleInputAndProducesNoConsoleOutput() {
        Round round = ordinaryRound();
        round.playerStand();
        round.dealerStep();
        assertEquals(RoundResult.PLAYER_WIN, round.getResult());
        assertEquals("", output());
    }

    private Round ordinaryRound() {
        Round round = new Round(new FixedDeck(Rank.TEN, Rank.KING, Rank.NINE, Rank.SEVEN));
        round.start();
        return round;
    }
}
