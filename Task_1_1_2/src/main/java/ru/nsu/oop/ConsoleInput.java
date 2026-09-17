package ru.nsu.oop;

import java.util.Scanner;

/**
 * Класс, отвечающий за чтение и проверку команд пользователя.
 */
public class ConsoleInput {
    private final Scanner scanner;

    public ConsoleInput() {
        scanner = new Scanner(System.in);
    }

    public String readCommand() {
        return scanner.nextLine().trim();
    }
}
