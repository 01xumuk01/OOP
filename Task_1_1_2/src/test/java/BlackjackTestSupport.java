package ru.nsu.oop;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.Charset;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.parallel.ResourceLock;
import org.junit.jupiter.api.parallel.Resources;

/**
 * Перехватывает консоль и восстанавливает её после каждого теста.
 */
@ResourceLock("java.lang.System.in")
@ResourceLock(Resources.SYSTEM_OUT)
abstract class BlackjackTestSupport {
    private InputStream originalInput;
    private PrintStream originalOutput;
    private PrintStream capturedOutput;
    private ByteArrayOutputStream buffer;

    @BeforeEach
    void captureConsole() {
        originalInput = System.in;
        originalOutput = System.out;
        buffer = new ByteArrayOutputStream();
        capturedOutput = new PrintStream(buffer, true, Charset.defaultCharset());
        System.setOut(capturedOutput);
        setInput("");
    }

    @AfterEach
    void restoreConsole() {
        System.setIn(originalInput);
        System.setOut(originalOutput);
        capturedOutput.close();
    }

    void setInput(String text) {
        System.setIn(new ByteArrayInputStream(text.getBytes(Charset.defaultCharset())));
    }

    String output() {
        capturedOutput.flush();
        return buffer.toString(Charset.defaultCharset());
    }

    void clearOutput() {
        buffer.reset();
    }

    Hand hand(Rank... ranks) {
        Hand hand = new Hand();
        for (Rank rank : ranks) {
            hand.addCard(new Card(Suit.SPADES, rank));
        }
        return hand;
    }

    /**
     * Колода с известным порядком выдачи и счётчиком выданных карт.
     */
    static class FixedDeck extends Deck {
        private final Rank[] ranks;
        private final int[] rankCounts = new int[Rank.values().length];
        private int dealt;

        FixedDeck(Rank... ranks) {
            this.ranks = ranks.clone();
        }

        @Override
        public Card dealCard() {
            if (dealt >= ranks.length) {
                throw new AssertionError("Раунд запросил лишнюю карту: " + (dealt + 1));
            }
            Rank rank = ranks[dealt++];
            int suitIndex = rankCounts[rank.ordinal()]++;
            if (suitIndex >= Suit.values().length) {
                throw new AssertionError("В тестовой раздаче больше четырёх карт одного ранга");
            }
            return new Card(Suit.values()[suitIndex], rank);
        }

        int dealtCount() {
            return dealt;
        }
    }
}
