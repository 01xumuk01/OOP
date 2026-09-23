package ru.nsu.oop.model;

/**
 * Перечисление возможных достоинств карт.
 */
public enum Rank {
    ACE(11, "Туз"),
    TWO(2, "Двойка"),
    THREE(3, "Тройка"),
    FOUR(4, "Четвёрка"),
    FIVE(5, "Пятёрка"),
    SIX(6, "Шестёрка"),
    SEVEN(7, "Семёрка"),
    EIGHT(8, "Восьмёрка"),
    NINE(9, "Девятка"),
    TEN(10, "Десятка"),
    JACK(10, "Валет"),
    QUEEN(10, "Дама"),
    KING(10, "Король");

    private final int points;
    private final String russianName;

    Rank(int points, String russianName) {
        this.points = points;
        this.russianName = russianName;
    }

    public int getPoints() {
        return points;
    }

    /**
     * Возвращает название для русского интерфейса.
     *
     * @return русское название достоинства
     */
    public String getRussianName() {
        return russianName;
    }
}
