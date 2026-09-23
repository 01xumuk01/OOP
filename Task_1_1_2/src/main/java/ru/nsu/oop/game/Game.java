package ru.nsu.oop.game;

import ru.nsu.oop.console.ConsoleInput;
import ru.nsu.oop.console.ConsoleView;
import ru.nsu.oop.model.Deck;

/**
 * Управляет консольным сценарием игры, раундами и общим счётом.
 * Правила раздачи применяет Round, а сообщения формирует ConsoleView.
 */
public class Game {
    private final ConsoleInput input;
    private final ConsoleView view;
    private int playerScore;
    private int dealerScore;
    private int roundNumber = 1;

    /**
     * Создаёт игру со стандартными консольными вводом и выводом.
     */
    public Game() {
        input = new ConsoleInput();
        view = new ConsoleView();
    }

    /**
     * Проводит раунды до команды завершения игры.
     */
    public void play() {
        view.printWelcome();
        while (true) {
            view.printRoundNumber(roundNumber++);
            Round round = createRound();
            conductRound(round);
            updateScore(round.getResult());
            view.printScore(dealerScore, playerScore);
            view.printContinuePrompt();
            if (input.readCommand().equals("0")) {
                return;
            }
        }
    }

    /**
     * Создаёт отдельную раздачу с новой колодой.
     *
     * @return новый раунд
     */
    protected Round createRound() {
        return new Round(new Deck());
    }

    private void conductRound(Round round) {
        round.start();
        view.printCardsDealt();
        view.printDealerInitialHand(round.getDealer());
        view.printPlayerHand(round.getPlayer());

        if (round.getState() == RoundState.FINISHED) {
            view.printDealerHand(round.getDealer());
        } else {
            conductPlayerTurn(round);
            if (round.getState() == RoundState.DEALER_TURN) {
                conductDealerTurn(round);
            }
        }
        view.printResult(round.getResult());
    }

    private void conductPlayerTurn(Round round) {
        view.printPlayerTurn();
        while (round.getState() == RoundState.PLAYER_TURN) {
            view.printPlayerPrompt();
            String command = input.readCommand();
            if (command.equals("1")) {
                round.playerHit();
                view.printPlayerHand(round.getPlayer());
            } else if (command.equals("0")) {
                round.playerStand();
            } else {
                view.printUnknownCommand();
            }
        }
        if (round.getPlayer().isBust()) {
            view.printPlayerBust();
        }
    }

    private void conductDealerTurn(Round round) {
        view.printDealerTurn();
        view.printDealerHand(round.getDealer());
        while (round.getState() == RoundState.DEALER_TURN) {
            if (round.dealerStep()) {
                view.printDealerHand(round.getDealer());
            }
        }
        if (round.getDealer().isBust()) {
            view.printDealerBust();
        } else {
            view.printDealerStand();
        }
    }

    private void updateScore(RoundResult result) {
        if (result == RoundResult.PLAYER_WIN) {
            playerScore++;
        } else if (result == RoundResult.DEALER_WIN) {
            dealerScore++;
        }
    }
}
