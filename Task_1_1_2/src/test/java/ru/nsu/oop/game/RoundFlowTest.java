package ru.nsu.oop.game;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import ru.nsu.oop.model.Rank;
import ru.nsu.oop.support.BlackjackTestSupport;

/**
 * Проверяет взаимодействие Game и Round на заранее заданных раздачах.
 */
class RoundFlowTest extends BlackjackTestSupport {
    private FixedDeck deck;
    private Round round;

    private RoundResult play(String commands, Rank... ranks) {
        setInput(commands + "0\n");
        deck = new FixedDeck(ranks);
        round = new Round(deck);
        Game game = new Game() {
            @Override
            protected Round createRound() {
                return round;
            }
        };
        game.play();
        return round.getResult();
    }

    @TestFactory
    List<DynamicTest> allStandingTotals() {
        List<DynamicTest> tests = new ArrayList<>();
        for (int player = 12; player <= 20; player++) {
            for (int dealer = 17; dealer <= 20; dealer++) {
                int playerTotal = player;
                int dealerTotal = dealer;
                tests.add(DynamicTest.dynamicTest(
                        "Игрок " + player + ", дилер " + dealer, () -> {
                            clearOutput();
                            RoundResult expected = playerTotal == dealerTotal
                                    ? RoundResult.DRAW
                                    : playerTotal > dealerTotal
                                            ? RoundResult.PLAYER_WIN : RoundResult.DEALER_WIN;
                            assertEquals(expected, play("0\n",
                                    Rank.TEN, Rank.KING,
                                    numeric(playerTotal - 10), numeric(dealerTotal - 10)));
                            assertEquals(4, deck.dealtCount());
                            assertTrue(output().contains("Дилер остановился"));
                        }));
            }
        }
        return tests;
    }

    @TestFactory
    List<DynamicTest> allBlackjackFaceCards() {
        List<DynamicTest> tests = new ArrayList<>();
        for (Rank ten : List.of(Rank.TEN, Rank.JACK, Rank.QUEEN, Rank.KING)) {
            for (boolean aceFirst : List.of(true, false)) {
                Rank first = aceFirst ? Rank.ACE : ten;
                Rank second = aceFirst ? ten : Rank.ACE;
                tests.add(DynamicTest.dynamicTest("Блэкджек игрока " + first + second, () -> {
                    clearOutput();
                    assertEquals(RoundResult.PLAYER_WIN,
                            play("", first, Rank.NINE, second, Rank.SEVEN));
                    assertEquals(4, deck.dealtCount());
                    assertFalse(output().contains("Введите 1"));
                    assertFalse(output().contains("Ход дилера"));
                }));
                tests.add(DynamicTest.dynamicTest("Блэкджек дилера " + first + second, () -> {
                    clearOutput();
                    assertEquals(RoundResult.DEALER_WIN,
                            play("", Rank.TEN, first, Rank.NINE, second));
                    assertEquals(4, deck.dealtCount());
                    assertFalse(output().contains("Введите 1"));
                }));
            }
        }
        return tests;
    }

    @TestFactory
    List<DynamicTest> dealerDrawsBelowSeventeen() {
        List<DynamicTest> tests = new ArrayList<>();
        for (int total = 4; total <= 16; total++) {
            int dealerTotal = total;
            tests.add(DynamicTest.dynamicTest("Дилер добирает с " + total, () -> {
                clearOutput();
                List<Rank> cards = new ArrayList<>(List.of(
                        Rank.TEN, numeric(dealerTotal / 2),
                        Rank.NINE, numeric(dealerTotal - dealerTotal / 2)));
                int sum = dealerTotal;
                while (sum < 17) {
                    int points = Math.min(10, 17 - sum);
                    cards.add(points == 1 ? Rank.ACE : numeric(points));
                    sum += points;
                }
                assertEquals(RoundResult.PLAYER_WIN,
                        play("0\n", cards.toArray(new Rank[0])));
                assertEquals(cards.size(), deck.dealtCount());
                assertTrue(output().contains("=> 17"));
            }));
        }
        return tests;
    }

    @Test
    void simultaneousBlackjacksAreDraw() {
        assertEquals(RoundResult.DRAW,
                play("", Rank.ACE, Rank.KING, Rank.QUEEN, Rank.ACE));
        assertEquals(4, deck.dealtCount());
        assertTrue(output().contains("Ничья"));
        assertFalse(output().contains("Введите 1"));
    }

    @Test
    void playerBustStopsRoundBeforeDealerDraws() {
        assertEquals(RoundResult.DEALER_WIN,
                play("1\n", Rank.KING, Rank.TWO, Rank.QUEEN, Rank.THREE, Rank.TWO));
        assertEquals(5, deck.dealtCount());
        assertTrue(output().contains("Перебор!"));
        assertFalse(output().contains("Ход дилера"));
    }

    @Test
    void dealerBustGivesPlayerWin() {
        assertEquals(RoundResult.PLAYER_WIN,
                play("0\n", Rank.TEN, Rank.TEN, Rank.EIGHT, Rank.SIX, Rank.KING));
        assertEquals(5, deck.dealtCount());
        assertTrue(output().contains("У дилера перебор!"));
    }

    @Test
    void playerCanHitAndThenStand() {
        assertEquals(RoundResult.PLAYER_WIN,
                play("1\n0\n", Rank.FIVE, Rank.TEN, Rank.SIX, Rank.SEVEN, Rank.NINE));
        assertEquals(5, deck.dealtCount());
        assertTrue(output().contains("=> 20"));
    }

    @Test
    void playerCanTakeSeveralCards() {
        assertEquals(RoundResult.PLAYER_WIN,
                play("1\n1\n1\n0\n", Rank.TWO, Rank.TEN, Rank.THREE, Rank.SEVEN,
                        Rank.FOUR, Rank.FIVE, Rank.SIX));
        assertEquals(7, deck.dealtCount());
    }

    @Test
    void invalidCommandsDoNotDealCards() {
        assertEquals(RoundResult.PLAYER_WIN,
                play("hello\n2\n-1\n\n0\n", Rank.TEN, Rank.KING, Rank.NINE, Rank.SEVEN));
        assertEquals(4, deck.dealtCount());
        assertEquals(4, output().split("Неизвестная команда", -1).length - 1);
    }

    @Test
    void commandSpacesAreTrimmed() {
        assertEquals(RoundResult.PLAYER_WIN,
                play("  1  \n\t0\t\n", Rank.FIVE, Rank.TEN, Rank.SIX, Rank.SEVEN, Rank.NINE));
        assertEquals(5, deck.dealtCount());
        assertFalse(output().contains("Неизвестная команда"));
    }

    @Test
    void dealerStandsOnSoftSeventeen() {
        assertEquals(RoundResult.PLAYER_WIN,
                play("0\n", Rank.TEN, Rank.ACE, Rank.EIGHT, Rank.SIX));
        assertEquals(4, deck.dealtCount());
    }

    @Test
    void dealerDrawsOnSoftSixteen() {
        assertEquals(RoundResult.PLAYER_WIN,
                play("0\n", Rank.TEN, Rank.ACE, Rank.NINE, Rank.FIVE, Rank.TWO));
        assertEquals(5, deck.dealtCount());
        assertTrue(output().contains("=> 18"));
    }

    @Test
    void dealerAceCanChangeFromElevenToOne() {
        assertEquals(RoundResult.PLAYER_WIN,
                play("0\n", Rank.TEN, Rank.ACE, Rank.NINE, Rank.FIVE, Rank.TEN, Rank.TWO));
        assertEquals(6, deck.dealtCount());
        assertTrue(output().contains("=> 16"));
        assertTrue(output().contains("=> 18"));
    }

    @Test
    void playerAcePreventsBustAfterHit() {
        assertEquals(RoundResult.DEALER_WIN,
                play("1\n0\n", Rank.ACE, Rank.TEN, Rank.SIX, Rank.SEVEN, Rank.EIGHT));
        assertEquals(5, deck.dealtCount());
        assertTrue(output().contains("=> 15"));
        assertFalse(output().contains("Перебор!"));
    }

    @Test
    void ordinaryTwentyOneBeatsTwenty() {
        assertEquals(RoundResult.PLAYER_WIN,
                play("1\n0\n", Rank.SEVEN, Rank.TEN, Rank.SIX, Rank.KING, Rank.EIGHT));
        assertTrue(output().contains("=> 21"));
        assertFalse(round.getPlayer().isBlackjack());
    }

    @Test
    void bothOrdinaryTwentyOnesAreDraw() {
        assertEquals(RoundResult.DRAW,
                play("1\n0\n", Rank.SEVEN, Rank.TEN, Rank.SIX, Rank.SIX,
                        Rank.EIGHT, Rank.FIVE));
        assertEquals(6, deck.dealtCount());
    }

    @Test
    void dealerCanReachTwentyOne() {
        assertEquals(RoundResult.DEALER_WIN,
                play("0\n", Rank.TEN, Rank.TEN, Rank.NINE, Rank.SIX, Rank.FIVE));
        assertTrue(output().contains("=> 21"));
    }

    @Test
    void dealerCardIsHiddenUntilPlayerFinishes() {
        play("0\n", Rank.TEN, Rank.KING, Rank.NINE, Rank.SEVEN);
        String text = output();
        int dealerTurn = text.indexOf("Ход дилера");
        assertTrue(dealerTurn > 0);
        assertTrue(text.substring(0, dealerTurn).contains("<закрытая карта>"));
        assertFalse(text.substring(0, dealerTurn).contains("Семёрка"));
        assertTrue(text.substring(dealerTurn).contains("Семёрка"));
    }

    @Test
    void blackjackRevealsDealerCards() {
        play("", Rank.ACE, Rank.NINE, Rank.KING, Rank.SEVEN);
        assertTrue(output().contains("Семёрка"));
        assertTrue(output().contains("=> 16"));
    }

    private Rank numeric(int points) {
        for (Rank rank : Rank.values()) {
            if (rank != Rank.ACE && rank.getPoints() == points) {
                return rank;
            }
        }
        throw new IllegalArgumentException("Нет числовой карты: " + points);
    }
}
