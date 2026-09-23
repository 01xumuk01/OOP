package ru.nsu.oop.model;

/**
 * Перечисление возможных мастей карты.
 */
public enum Suit {
    SPADES("пик"),
    DIAMONDS("бубен"),
    CLUBS("треф"),
    HEARTS("червей");

    private final String russianName;

    Suit(String russianName) {
        this.russianName = russianName;
    }

    /**
     * Возвращает название масти в родительном падеже.
     *
     * @return название для подписи карты, например «пик»
     */
    public String getRussianName() {
        return russianName;
    }
}
