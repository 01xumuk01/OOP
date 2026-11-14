package ru.nsu.oop;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class FinderString {
    private static final ArrayList<Integer> window = new ArrayList<>();
    private static final ArrayList<Integer> pattern = new ArrayList<>();
    private static final ArrayList<Long> answer = new ArrayList<>();
    private static long position;
    private static int m;

    private static void Processing(int symbol) {
        boolean flag = false;

        window.add(symbol);

        if (window.size() > m) {
            window.removeFirst();
        }

        if (window.size() == m) {
            for (int i = 0; i < m; i++) {
                if (!window.get(i).equals(pattern.get(i))) {
                    flag = true;
                    break;
                }
            }

            if (!flag) {
                answer.add(position - m + 1);
            }
        }

        position += 1;
    }

    public static ArrayList<Long> Find(String whatFind, Path whereFindArg)
            throws IOException {
        if (whatFind.isEmpty()) {
            throw new IllegalArgumentException("Подстрока не должна быть пустой");
        }

        window.clear();
        pattern.clear();
        answer.clear();
        position = 0;

        whatFind.codePoints().forEachOrdered(pattern::add);
        m = pattern.size();

        try (BufferedReader whereFind =
                     Files.newBufferedReader(whereFindArg, StandardCharsets.UTF_8)) {
            int symbolCode;

            while ((symbolCode = whereFind.read()) != -1) {
                char currentChar = (char) symbolCode;

                if (Character.isHighSurrogate(currentChar)) {
                    int nextCode = whereFind.read();

                    if (nextCode == -1
                            || !Character.isLowSurrogate((char) nextCode)) {
                        throw new IOException("Некорректный символ Unicode");
                    }

                    symbolCode = Character.toCodePoint(currentChar, (char) nextCode);
                }

                Processing(symbolCode);
            }
        }

        return new ArrayList<>(answer);
    }
}