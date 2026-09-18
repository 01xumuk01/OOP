package ru.nsu.oop;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.NoSuchElementException;
import org.junit.jupiter.api.Test;

/**
 * Проверяет настоящий ввод и точный текст консольного вывода.
 */
class ConsoleIoTest extends BlackjackTestSupport {
    @Test
    void readsHitCommand() {
        setInput("1\n");
        assertEquals("1", new ConsoleInput().readCommand());
    }

    @Test
    void readsStandCommand() {
        setInput("0\n");
        assertEquals("0", new ConsoleInput().readCommand());
    }

    @Test
    void trimsSpacesAndTabs() {
        setInput(" \t 1 \t \n");
        assertEquals("1", new ConsoleInput().readCommand());
    }

    @Test
    void readsEmptyLine() {
        setInput("\n");
        assertEquals("", new ConsoleInput().readCommand());
    }

    @Test
    void whitespaceOnlyBecomesEmptyString() {
        setInput(" \t  \n");
        assertEquals("", new ConsoleInput().readCommand());
    }

    @Test
    void readsSeveralCommandsInOrder() {
        setInput("1\n0\n\nabc\n");
        ConsoleInput input = new ConsoleInput();
        assertEquals("1", input.readCommand());
        assertEquals("0", input.readCommand());
        assertEquals("", input.readCommand());
        assertEquals("abc", input.readCommand());
    }

    @Test
    void supportsWindowsLineEndings() {
        setInput("1\r\n0\r\n");
        ConsoleInput input = new ConsoleInput();
        assertEquals("1", input.readCommand());
        assertEquals("0", input.readCommand());
    }

    @Test
    void lastLineDoesNotNeedNewline() {
        setInput("1");
        assertEquals("1", new ConsoleInput().readCommand());
    }

    @Test
    void unknownCommandIsReturnedUnchanged() {
        setInput("abc 123\n");
        assertEquals("abc 123", new ConsoleInput().readCommand());
    }

    @Test
    void emptyInputCurrentlyThrowsException() {
        assertThrows(NoSuchElementException.class, () -> new ConsoleInput().readCommand());
    }

    @Test
    void exhaustedInputCurrentlyThrowsException() {
        setInput("0\n");
        ConsoleInput input = new ConsoleInput();
        assertEquals("0", input.readCommand());
        assertThrows(NoSuchElementException.class, input::readCommand);
    }

    @Test
    void messageEndsWithNewline() {
        new ConsoleView().printMessage("Привет");
        assertEquals("Привет" + System.lineSeparator(), output());
    }

    @Test
    void emptyMessagePrintsNewline() {
        new ConsoleView().printMessage("");
        assertEquals(System.lineSeparator(), output());
    }

    @Test
    void printsEmptyHand() {
        new ConsoleView().printHand("Игрок", new Hand());
        assertEquals("Игрок: [] => 0" + System.lineSeparator(), output());
    }

    @Test
    void printsCardsInOrderAndAdjustedAceTotal() {
        new ConsoleView().printHand("Игрок", hand(Rank.ACE, Rank.SIX, Rank.EIGHT));
        assertEquals("Игрок: [ACE SPADES (11), SIX SPADES (6), EIGHT SPADES (8)]"
                + " => 15" + System.lineSeparator(), output());
    }

    @Test
    void handOutputDoesNotDependOnOwnerText() {
        new ConsoleView().printHand("Произвольное имя", hand(Rank.TEN, Rank.NINE));
        assertTrue(output().startsWith("Произвольное имя:"));
        assertTrue(output().contains("=> 19"));
    }

    @Test
    void hidesSecondDealerCardAndTotal() {
        new ConsoleView().printDealerInitialHand(hand(Rank.KING, Rank.FIVE));
        assertEquals("Карты дилера: [KING SPADES (10), <закрытая карта>]"
                + System.lineSeparator(), output());
        assertFalse(output().contains("FIVE"));
        assertFalse(output().contains("=>"));
    }

    @Test
    void printingDoesNotModifyHand() {
        Hand hand = hand(Rank.ACE, Rank.KING);
        new ConsoleView().printHand("Игрок", hand);
        new ConsoleView().printDealerInitialHand(hand);
        assertEquals(2, hand.getCards().size());
        assertTrue(hand.isBlackjack());
    }

    @Test
    void printsDealerLeadingScore() {
        new ConsoleView().printScore(3, 1);
        assertEquals("Счёт 3 : 1 в пользу дилера." + System.lineSeparator(), output());
    }

    @Test
    void printsPlayerLeadingScore() {
        new ConsoleView().printScore(1, 3);
        assertEquals("Счёт 1 : 3 в Вашу пользу." + System.lineSeparator(), output());
    }

    @Test
    void printsEqualNonzeroScore() {
        new ConsoleView().printScore(2, 2);
        assertEquals("Счёт 2 : 2." + System.lineSeparator(), output());
    }

    @Test
    void printsZeroScore() {
        new ConsoleView().printScore(0, 0);
        assertEquals("Счёт 0 : 0." + System.lineSeparator(), output());
    }
}
