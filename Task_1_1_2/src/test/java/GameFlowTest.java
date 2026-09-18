package ru.nsu.oop;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

/**
 * Проверяет счёт и цикл игры независимо от правил отдельного раунда.
 */
class GameFlowTest extends BlackjackTestSupport {
    @TestFactory
    List<DynamicTest> allThreeRoundOutcomeSequences() {
        List<DynamicTest> tests = new ArrayList<>();
        for (RoundResult first : RoundResult.values()) {
            for (RoundResult second : RoundResult.values()) {
                for (RoundResult third : RoundResult.values()) {
                    tests.add(DynamicTest.dynamicTest(first + "/" + second + "/" + third, () -> {
                        clearOutput();
                        setInput("\n\n0\n");
                        ScenarioGame game = new ScenarioGame(first, second, third);
                        game.play();
                        int dealer = 0;
                        int player = 0;
                        StringBuilder expectedScores = new StringBuilder();
                        for (RoundResult result : List.of(first, second, third)) {
                            if (result == RoundResult.DEALER_WIN) {
                                dealer++;
                            } else if (result == RoundResult.PLAYER_WIN) {
                                player++;
                            }
                            expectedScores.append(dealer).append(":").append(player).append(";");
                        }
                        assertEquals(expectedScores.toString(), scores(output()));
                        assertEquals(3, game.created);
                        assertTrue(output().contains("Раунд 1"));
                        assertTrue(output().contains("Раунд 2"));
                        assertTrue(output().contains("Раунд 3"));
                        assertFalse(output().contains("Раунд 4"));
                    }));
                }
            }
        }
        return tests;
    }

    @Test
    void zeroExitsAfterFirstRound() {
        setInput("0\n");
        ScenarioGame game = new ScenarioGame(RoundResult.PLAYER_WIN);
        game.play();
        assertEquals(1, game.created);
        assertEquals("0:1;", scores(output()));
        assertFalse(output().contains("Раунд 2"));
    }

    @Test
    void spacesAroundExitAreAccepted() {
        setInput("  0  \n");
        ScenarioGame game = new ScenarioGame(RoundResult.DRAW);
        game.play();
        assertEquals(1, game.created);
        assertEquals("0:0;", scores(output()));
    }

    @Test
    void nonzeroTextCurrentlyStartsNextRound() {
        setInput("abc\n0\n");
        ScenarioGame game = new ScenarioGame(RoundResult.DRAW, RoundResult.DEALER_WIN);
        game.play();
        assertEquals(2, game.created);
        assertEquals("0:0;1:0;", scores(output()));
    }

    @Test
    void welcomeIsPrintedOnceForSeveralRounds() {
        setInput("\n\n0\n");
        ScenarioGame game = new ScenarioGame(
                RoundResult.DRAW, RoundResult.DRAW, RoundResult.DRAW);
        game.play();
        assertEquals(1, output().split("Добро пожаловать", -1).length - 1);
        assertEquals(3, output().split("Enter — следующий раунд", -1).length - 1);
    }

    @Test
    void separateGamesStartWithZeroScores() {
        setInput("0\n");
        new ScenarioGame(RoundResult.PLAYER_WIN).play();
        clearOutput();
        setInput("0\n");
        new ScenarioGame(RoundResult.DEALER_WIN).play();
        assertEquals("1:0;", scores(output()));
        assertTrue(output().contains("Раунд 1"));
        assertFalse(output().contains("Раунд 2"));
    }

    @Test
    void manyRoundsPreserveScoreAndNumbering() {
        RoundResult[] results = new RoundResult[1000];
        StringBuilder commands = new StringBuilder();
        for (int i = 0; i < results.length; i++) {
            results[i] = RoundResult.values()[i % 3];
            commands.append(i == results.length - 1 ? "0\n" : "\n");
        }
        setInput(commands.toString());
        ScenarioGame game = new ScenarioGame(results);
        game.play();
        assertEquals(1000, game.created);
        assertTrue(output().contains("Раунд 1000\n"));
        assertFalse(output().contains("Раунд 1001\n"));
        assertTrue(scores(output()).endsWith("333:334;"));
    }

    private String scores(String text) {
        StringBuilder result = new StringBuilder();
        for (String line : text.split("\\R")) {
            if (line.startsWith("Счёт ")) {
                String[] words = line.split(" ");
                result.append(words[1]).append(":");
                result.append(words[3].replace(".", "")).append(";");
            }
        }
        return result.toString();
    }

    private static class ScenarioGame extends Game {
        private final RoundResult[] results;
        private int created;

        ScenarioGame(RoundResult... results) {
            this.results = results.clone();
        }

        @Override
        Round createRound() {
            if (created >= results.length) {
                throw new AssertionError("Игра запустила лишний раунд");
            }
            RoundResult result = results[created++];
            return new Round(new ConsoleInput(), new ConsoleView()) {
                @Override
                public RoundResult play() {
                    return result;
                }
            };
        }
    }
}
