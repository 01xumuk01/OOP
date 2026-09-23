package ru.nsu.oop.console;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.NoSuchElementException;
import org.junit.jupiter.api.Test;
import ru.nsu.oop.game.RoundResult;
import ru.nsu.oop.model.Dealer;
import ru.nsu.oop.model.Player;
import ru.nsu.oop.model.Rank;
import ru.nsu.oop.support.BlackjackTestSupport;

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
    void welcomeEndsWithNewline() {
        new ConsoleView().printWelcome();
        assertEquals("Добро пожаловать в Блэкджек!" + System.lineSeparator(), output());
    }

    @Test
    void printsRoundNumber() {
        new ConsoleView().printRoundNumber(42);
        assertEquals("\nРаунд 42" + System.lineSeparator(), output());
    }

    @Test
    void printsEmptyHand() {
        new ConsoleView().printPlayerHand(new Player());
        assertEquals("Ваши карты: [] => 0" + System.lineSeparator(), output());
    }

    @Test
    void printsCardsInOrderAndAdjustedAceTotal() {
        new ConsoleView().printPlayerHand(player(Rank.ACE, Rank.SIX, Rank.EIGHT));
        assertEquals("Ваши карты: [Туз пик (11), Шестёрка пик (6), Восьмёрка пик (8)]"
                + " => 15" + System.lineSeparator(), output());
    }

    @Test
    void revealedDealerHandIncludesAllCardsAndTotal() {
        new ConsoleView().printDealerHand(dealer(Rank.KING, Rank.NINE));
        assertEquals("Карты дилера: [Король пик (10), Девятка пик (9)] => 19"
                + System.lineSeparator(), output());
    }

    @Test
    void hidesSecondDealerCardAndTotal() {
        new ConsoleView().printDealerInitialHand(dealer(Rank.KING, Rank.FIVE));
        assertEquals("Карты дилера: [Король пик (10), <закрытая карта>]"
                + System.lineSeparator(), output());
        assertFalse(output().contains("Пятёрка"));
        assertFalse(output().contains("=>"));
    }

    @Test
    void printingDoesNotModifyHand() {
        Dealer dealer = dealer(Rank.ACE, Rank.KING);
        new ConsoleView().printDealerHand(dealer);
        new ConsoleView().printDealerInitialHand(dealer);
        assertEquals(2, dealer.getCards().size());
        assertTrue(dealer.isBlackjack());
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

    @Test
    void printsEveryRoundResult() {
        ConsoleView view = new ConsoleView();
        view.printResult(RoundResult.PLAYER_WIN);
        view.printResult(RoundResult.DEALER_WIN);
        view.printResult(RoundResult.DRAW);
        assertEquals(String.join(System.lineSeparator(),
                "Вы выиграли!", "Вы проиграли!", "Ничья", ""), output());
    }
}
