package ru.nsu.oop.finder;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

/**
 * Ищет вхождения подстроки в UTF-8-файле методом скользящего окна.
 * Файл читается последовательно без загрузки целиком в память.
 */
public class FinderString {

    /**
     * Находит все вхождения заданной подстроки в файле.
     *
     * <p>Индексы отсчитываются с нуля в кодовых точках Unicode.
     * Перекрывающиеся вхождения учитываются. Например, поиск
     * «аа» в строке «ааа» возвращает индексы 0 и 1.
     *
     * @param whatFind непустая искомая подстрока
     * @param whereFindArg путь к файлу в кодировке UTF-8
     * @return индексы начала вхождений в порядке возрастания;
     *         пустой список, если совпадений нет
     * @throws IllegalArgumentException если подстрока пустая
     * @throws IOException если произошла ошибка чтения
     *         или обнаружены некорректные данные Unicode
     */
    public static ArrayList<Long> find(String whatFind, Path whereFindArg)
            throws IOException {
        if (whatFind.isEmpty()) {
            throw new IllegalArgumentException("Подстрока не должна быть пустой");
        }

        final ArrayList<Integer> window = new ArrayList<>();
        final ArrayList<Integer> pattern = new ArrayList<>();
        final ArrayList<Long> answer = new ArrayList<>();
        long position = 0;

        whatFind.codePoints().forEachOrdered(pattern::add);
        int m = pattern.size();

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

                processing(symbolCode,
                        window,
                        pattern,
                        answer,
                        position,
                        m);

                position += 1;
            }
        }

        return answer;
    }

    /**
     * Добавляет очередную кодовую точку в окно и проверяет совпадение.
     *
     * @param symbol очередная кодовая точка файла
     * @param window текущее скользящее окно
     * @param pattern кодовые точки искомой подстроки
     * @param answer список найденных индексов
     * @param position индекс текущей кодовой точки файла
     * @param m длина искомой подстроки в кодовых точках
     */
    private static void processing(
            int symbol,
            ArrayList<Integer> window,
            ArrayList<Integer> pattern,
            ArrayList<Long> answer,
            long position,
            int m
    ) {
        boolean flag = false;

        window.add(symbol);

        if (window.size() > m) {
            window.remove(0);
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
    }
}