package ru.nsu.oop.support;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.Charset;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.parallel.ResourceLock;
import org.junit.jupiter.api.parallel.Resources;
import ru.nsu.oop.model.Card;
import ru.nsu.oop.model.Dealer;
import ru.nsu.oop.model.Deck;
import ru.nsu.oop.model.Hand;
import ru.nsu.oop.model.Player;
import ru.nsu.oop.model.Rank;
import ru.nsu.oop.model.Suit;

/**
 * Перехватывает консоль и восстанавливает её после каждого теста.
 */
@ResourceLock("java.lang.System.in")
@ResourceLock(Resources.SYSTEM_OUT)
public abstract class BlackjackTestSupport {
    private InputStream originalInput;
    private PrintStream originalOutput;
    private PrintStream capturedOutput;
    private ByteArrayOutputStream buffer;

    @BeforeEach
    protected void captureConsole() {
        originalInput = System.in;
        originalOutput = System.out;
        buffer = new ByteArrayOutputStream();
        capturedOutput = new PrintStream(buffer, true, Charset.defaultCharset());
        System.setOut(capturedOutput);
        setInput("");
    }

    @AfterEach
    protected void restoreConsole() {
        System.setIn(originalInput);
        System.setOut(originalOutput);
        capturedOutput.close();
    }

    protected void setInput(String text) {
        System.setIn(new ByteArrayInputStream(text.getBytes(Charset.defaultCharset())));
    }

    protected String output() {
        capturedOutput.flush();
        return buffer.toString(Charset.defaultCharset());
    }

    protected void clearOutput() {
        buffer.reset();
    }

    protected Hand hand(Rank... ranks) {
        Hand hand = new Hand();
        for (Rank rank : ranks) {
            hand.addCard(new Card(Suit.SPADES, rank));
        }
        return hand;
    }

    protected Player player(Rank... ranks) {
        Player player = new Player();
        for (Rank rank : ranks) {
            player.receiveCard(new Card(Suit.SPADES, rank));
        }
        return player;
    }

    protected Dealer dealer(Rank... ranks) {
        Dealer dealer = new Dealer();
        for (Rank rank : ranks) {
            dealer.receiveCard(new Card(Suit.SPADES, rank));
        }
        return dealer;
    }

    /**
     * Колода с известным порядком выдачи и счётчиком выданных карт.
     */
    public static class FixedDeck extends Deck {
        private final Rank[] ranks;
        private final int[] rankCounts = new int[Rank.values().length];
        private int dealt;

        public FixedDeck(Rank... ranks) {
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

        public int dealtCount() {
            return dealt;
        }
    }
}
