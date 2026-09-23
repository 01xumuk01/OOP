package ru.nsu.oop.model;

/**
 * Участник с фиксированным правилом добора карт.
 */
public class Dealer extends Player {
    private static final int STAND_POINTS = 17;

    /**
     * Определяет необходимость добора по текущей сумме.
     *
     * @return true, пока у дилера меньше 17 очков
     */
    public boolean shouldHit() {
        return getPoints() < STAND_POINTS;
    }
}
