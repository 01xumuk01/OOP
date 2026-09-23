package ru.nsu.oop.game;

import ru.nsu.oop.model.Dealer;
import ru.nsu.oop.model.Deck;
import ru.nsu.oop.model.Player;

/**
 * Хранит состояние раздачи и применяет правила к отдельным действиям.
 * Не читает команды пользователя и ничего не выводит.
 */
public class Round {
    private static final int INITIAL_CARDS_COUNT = 2;

    private final Deck deck;
    private final Player player = new Player();
    private final Dealer dealer = new Dealer();
    private RoundState state = RoundState.NEW;
    private RoundResult result;

    /**
     * Создаёт ещё не начатую раздачу.
     *
     * @param deck колода для этой раздачи
     */
    public Round(Deck deck) {
        this.deck = deck;
    }

    /**
     * Раздаёт начальные карты и проверяет начальные блэкджеки.
     *
     * @throws IllegalStateException если раздача уже началась
     */
    public void start() {
        requireState(RoundState.NEW);
        for (int i = 0; i < INITIAL_CARDS_COUNT; i++) {
            player.receiveCard(deck.dealCard());
            dealer.receiveCard(deck.dealCard());
        }
        if (player.isBlackjack() || dealer.isBlackjack()) {
            finish();
        } else {
            state = RoundState.PLAYER_TURN;
        }
    }

    /**
     * Выдаёт игроку одну карту и завершает раздачу при переборе.
     *
     * @throws IllegalStateException если сейчас не ход игрока
     */
    public void playerHit() {
        requireState(RoundState.PLAYER_TURN);
        player.receiveCard(deck.dealCard());
        if (player.isBust()) {
            finish();
        }
    }

    /**
     * Завершает ход игрока.
     *
     * @throws IllegalStateException если сейчас не ход игрока
     */
    public void playerStand() {
        requireState(RoundState.PLAYER_TURN);
        state = RoundState.DEALER_TURN;
    }

    /**
     * Выполняет один шаг дилера: добор или остановку.
     *
     * @return true, если дилер получил карту
     * @throws IllegalStateException если сейчас не ход дилера
     */
    public boolean dealerStep() {
        requireState(RoundState.DEALER_TURN);
        boolean drawn = dealer.shouldHit();
        if (drawn) {
            dealer.receiveCard(deck.dealCard());
        }
        if (!dealer.shouldHit()) {
            finish();
        }
        return drawn;
    }

    /**
     * Возвращает текущую стадию раздачи.
     *
     * @return стадия раздачи
     */
    public RoundState getState() {
        return state;
    }

    /**
     * Возвращает участника, которым управляет человек.
     *
     * @return игрок
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Возвращает дилера этой раздачи.
     *
     * @return дилер
     */
    public Dealer getDealer() {
        return dealer;
    }

    /**
     * Возвращает итог завершённой раздачи.
     *
     * @return исход раздачи
     * @throws IllegalStateException если раздача ещё не завершилась
     */
    public RoundResult getResult() {
        requireState(RoundState.FINISHED);
        return result;
    }

    private void requireState(RoundState expected) {
        if (state != expected) {
            throw new IllegalStateException("Недопустимое действие на стадии " + state);
        }
    }

    private void finish() {
        result = determineResult();
        state = RoundState.FINISHED;
    }

    private RoundResult determineResult() {
        if (player.isBust()) {
            return RoundResult.DEALER_WIN;
        }
        if (dealer.isBust()) {
            return RoundResult.PLAYER_WIN;
        }
        if (player.isBlackjack() && !dealer.isBlackjack()) {
            return RoundResult.PLAYER_WIN;
        }
        if (dealer.isBlackjack() && !player.isBlackjack()) {
            return RoundResult.DEALER_WIN;
        }
        if (player.getPoints() > dealer.getPoints()) {
            return RoundResult.PLAYER_WIN;
        }
        if (player.getPoints() < dealer.getPoints()) {
            return RoundResult.DEALER_WIN;
        }
        return RoundResult.DRAW;
    }
}
