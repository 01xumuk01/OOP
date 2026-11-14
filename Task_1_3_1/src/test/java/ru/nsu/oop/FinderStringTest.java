package ru.nsu.oop;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/** Проверяет поиск подстроки в UTF-8-файлах. */
@Execution(ExecutionMode.SAME_THREAD)
class FinderStringTest {
    @TempDir
    Path tempDir;

    @ParameterizedTest
    @MethodSource("searchCases")
    @DisplayName("Находит все вхождения с индексами Unicode-символов")
    void findsAllOccurrences(String text, String pattern, List<Long> expected)
            throws IOException {
        Path file = writeInput(text);
        assertEquals(expected, FinderString.Find(pattern, file));
    }

    @Test
    @DisplayName("Повторный поиск сбрасывает состояние и сохраняет прошлый результат")
    void repeatedCallsPreservePreviousResult() throws IOException {
        Path firstFile = writeInput("aaaa");
        List<Long> first = FinderString.Find("aa", firstFile);

        Path secondFile = tempDir.resolve("second.txt");
        Files.writeString(secondFile, "абракадабра", StandardCharsets.UTF_8);
        List<Long> second = FinderString.Find("бра", secondFile);

        assertEquals(List.of(0L, 1L, 2L), first);
        assertEquals(List.of(1L, 8L), second);
        assertEquals(List.of(), FinderString.Find("xyz", secondFile));
        assertEquals(List.of(0L, 1L, 2L), first);
    }

    @Test
    @DisplayName("Пустая подстрока отклоняется")
    void rejectsEmptyPattern() throws IOException {
        Path file = writeInput("abc");

        assertThrows(IllegalArgumentException.class,
                () -> FinderString.Find("", file));
    }

    @Test
    @DisplayName("Отсутствующий файл вызывает исключение")
    void rejectsMissingFile() {
        Path file = tempDir.resolve("missing.txt");

        assertThrows(NoSuchFileException.class,
                () -> FinderString.Find("abc", file));
    }

    @Test
    @DisplayName("Поддерживает кириллицу и пробелы в пути")
    void supportsUnicodeFilePath() throws IOException {
        Path directory = tempDir.resolve("папка с пробелами");
        Files.createDirectories(directory);

        Path file = directory.resolve("текст.txt");
        Files.writeString(file, "абракадабра", StandardCharsets.UTF_8);

        assertEquals(List.of(1L, 8L), FinderString.Find("бра", file));
    }

    @Test
    @DisplayName("Находит составные UTF-16 символы после длинного префикса")
    void findsSupplementaryCharactersAfterLongPrefix() throws IOException {
        Path file = writeInput("x".repeat(8191) + "𐐀abc😀z");

        assertEquals(List.of(8191L), FinderString.Find("𐐀a", file));
        assertEquals(List.of(8195L), FinderString.Find("😀z", file));
    }

    @Test
    @DisplayName("Большой файл генерируется во время теста")
    void searchesGeneratedLargeFile() throws IOException {
        Path file = tempDir.resolve("large.txt");
        String block = "ж".repeat(1000);

        try (BufferedWriter writer =
                     Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
            for (int i = 0; i < 2000; i++) {
                writer.write(block);
            }

            writer.write("бра");
            writer.write(block);
            writer.write("бра");
        }

        assertEquals(
                List.of(2000000L, 2001003L),
                FinderString.Find("бра", file)
        );
    }

    private Path writeInput(String text) throws IOException {
        Path file = tempDir.resolve("input.txt");
        Files.writeString(file, text, StandardCharsets.UTF_8);
        return file;
    }

    private static Stream<Arguments> searchCases() {
        return Stream.of(
                // Пример из условия.
                Arguments.of("абракадабра", "бра", List.of(1L, 8L)),

                // Односимвольная подстрока.
                Arguments.of("banana", "a", List.of(1L, 3L, 5L)),

                // Полное совпадение, начало и конец файла.
                Arguments.of("abc", "abc", List.of(0L)),
                Arguments.of("abcxxx", "abc", List.of(0L)),
                Arguments.of("xxxabc", "abc", List.of(3L)),

                // Отсутствие совпадений.
                Arguments.of("abcdef", "xyz", List.of()),
                Arguments.of("ab", "abcd", List.of()),
                Arguments.of("", "abc", List.of()),

                // Перекрывающиеся вхождения.
                Arguments.of("aaaaaa", "aa", List.of(0L, 1L, 2L, 3L, 4L)),
                Arguments.of("abababa", "aba", List.of(0L, 2L, 4L)),

                // Проверка отсутствия ложного повторного совпадения.
                Arguments.of("абб", "аб", List.of(0L)),

                // Регистр и пробелы.
                Arguments.of("АаАа", "Аа", List.of(0L, 2L)),
                Arguments.of(" a  a ", "a ", List.of(1L, 4L)),

                // Переносы строк входят в текст и учитываются в индексах.
                Arguments.of("a\nb\na\nb", "a\nb", List.of(0L, 4L)),
                Arguments.of("a\r\nb\r\nc", "\r\n", List.of(1L, 4L)),

                // Разные алфавиты.
                Arguments.of("猫犬猫犬猫", "猫犬", List.of(0L, 2L)),
                Arguments.of("αβγαβγ", "αβ", List.of(0L, 3L)),

                // Unicode-символы, занимающие два char.
                Arguments.of("😀аб😀аб", "аб", List.of(1L, 4L)),
                Arguments.of("😀😀😀", "😀😀", List.of(0L, 1L)),
                Arguments.of("𐐀аб𐐀аб", "𐐀а", List.of(0L, 3L)),

                // Нулевой символ не должен восприниматься как конец файла.
                Arguments.of("\u0000a\u0000a", "\u0000a", List.of(0L, 2L))
        );
    }
}