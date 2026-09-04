package ru.nsu.oop;

import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class MainTest {

    // 1. Граничные размеры: база индукции
    @Test
    void testEmptyArray() {
        assertArrayEquals(new int[]{}, Main.heapsort(new int[]{}));
    }

    @Test
    void testSingleElement() {
        assertArrayEquals(new int[]{42}, Main.heapsort(new int[]{42}));
    }

    // 2. Двухэлементные массивы: проверяет базовые операции shiftup и shiftdown
    @Test
    void testTwoElementsSorted() {
        assertArrayEquals(new int[]{1, 2}, Main.heapsort(new int[]{1, 2}));
    }

    @Test
    void testTwoElementsReverse() {
        assertArrayEquals(new int[]{1, 2}, Main.heapsort(new int[]{2, 1}));
    }

    // 3. Структура дерева: четные и нечетные размеры
    // В куче четного размера у последнего родителя ровно один (левый) ребенок.
    // В куче нечетного размера у всех родителей по два ребенка.
    @Test
    void testEvenLengthArray() {
        int[] input = {9, 3, 7, 1, 8, 2};
        int[] expected = {1, 2, 3, 7, 8, 9};
        assertArrayEquals(expected, Main.heapsort(input));
    }

    @Test
    void testOddLengthArray() {
        int[] input = {9, 3, 7, 1, 8, 2, 5};
        int[] expected = {1, 2, 3, 5, 7, 8, 9};
        assertArrayEquals(expected, Main.heapsort(input));
    }

    // 4. Порядок исходных данных: лучший, худший и повторяющийся
    @Test
    void testAlreadySorted() {
        int[] input = {1, 2, 3, 4, 5, 6, 7};
        assertArrayEquals(new int[]{1, 2, 3, 4, 5, 6, 7}, Main.heapsort(input));
    }

    @Test
    void testReverseSorted() {
        int[] input = {7, 6, 5, 4, 3, 2, 1};
        assertArrayEquals(new int[]{1, 2, 3, 4, 5, 6, 7}, Main.heapsort(input));
    }

    @Test
    void testAllIdenticalElements() {
        int[] input = {5, 5, 5, 5, 5};
        assertArrayEquals(new int[]{5, 5, 5, 5, 5}, Main.heapsort(input));
    }

    @Test
    void testManyDuplicates() {
        int[] input = {3, 1, 2, 3, 1, 2, 3, 0, 0};
        int[] expected = {0, 0, 1, 1, 2, 2, 3, 3, 3};
        assertArrayEquals(expected, Main.heapsort(input));
    }

    // 5. Граничные значения числовых типов
    @Test
    void testNegativeNumbersAndZero() {
        int[] input = {-10, 0, -50, 4, -1, 12};
        int[] expected = {-50, -10, -1, 0, 4, 12};
        assertArrayEquals(expected, Main.heapsort(input));
    }

    @Test
    void testIntegerLimits() {
        int[] input = {0, Integer.MAX_VALUE, -1, Integer.MIN_VALUE, 100};
        int[] expected = {Integer.MIN_VALUE, -1, 0, 100, Integer.MAX_VALUE};
        assertArrayEquals(expected, Main.heapsort(input));
    }

    // 6. Стресс-тест со случайными данными против библиотечной сортировки
    @Test
    void testRandomLargeArrays() {
        Random rnd = new Random(42);
        for (int iteration = 0; iteration < 10; iteration++) {
            int size = 1000 + rnd.nextInt(5000);
            int[] input = rnd.ints(size, -100_000, 100_000).toArray();
            int[] expected = input.clone();
            Arrays.sort(expected);

            assertArrayEquals(expected, Main.heapsort(input));
        }
    }

    // 7. Покрытие метода main (устраняет просадку JaCoCo)
    @Test
    void testMainMethodCoverage() {
        assertDoesNotThrow(() -> Main.main(new String[]{}));
    }
}