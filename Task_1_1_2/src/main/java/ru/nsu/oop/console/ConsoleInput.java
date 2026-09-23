package ru.nsu.oop.console;

import java.util.Scanner;

/**
 * Читает строку команды и удаляет пробелы по её краям.
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
