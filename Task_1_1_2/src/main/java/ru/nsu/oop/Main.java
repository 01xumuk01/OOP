package ru.nsu.oop;

/**
 * Класс, отвечающий за создание объектов и за старт игры.
 */
public class Main {
    public static void main(String[] args) {
        Game game = new Game();
        game.play();
    }
}