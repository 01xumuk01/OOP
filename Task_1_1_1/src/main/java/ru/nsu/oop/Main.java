package ru.nsu.oop;

/**
 * Класс, реализующий пирамидальную сортировку (HeapSort).
 */
public class Main {

    /**
     * Приватный конструктор утилитного класса.
     */
    private Main() {
    }

    /**
     * Просеивает вверх.
     *
     * @param list просеиваемая куча
     * @param index индекс просеимаевого числа
     */

    public static void shiftUp(int[] list, int index) {

        if (index == 0) {
            return;
        }

        if (list[(index - 1) / 2] > list[index]) {
            int tmp = list[index];
            list[index] = list[(index - 1) / 2];
            list[(index - 1) / 2] = tmp;

            shiftUp(list, (index - 1) / 2);

            return;
        }

    }

    /**
     * Просеивает вниз.
     *
     * @param list просеиваемая куча
     * @param index индекс просеимаевого числа
     * @param lastIndex индекс последнего числа кучи
     */

    public static void shiftDown(int[] list, int index, int lastIndex) {
        int smallest = index;
        int left = 2 * index + 1;
        int right = 2 * index + 2;

        if (left <= lastIndex && list[left] < list[smallest]) {
            smallest = left;
        }
        if (right <= lastIndex && list[right] < list[smallest]) {
            smallest = right;
        }

        if (smallest != index) {
            int tmp = list[index];
            list[index] = list[smallest];
            list[smallest] = tmp;
            shiftDown(list, smallest, lastIndex);
        }
    }

    /**
     * Возвращает минимальное число из кучи, при этом удаляя его и перераспределяя кучу заново.
     *
     * @param list просеиваемая куча
     * @param indexLast индекс последнего числа кучи
     * @return минимальное число из кучи
     */

    public static int extractMin(int[] list, int indexLast) {

        int min = list[0];

        if (indexLast == 0) {
            return min;
        }

        list[0] = list[indexLast];
        shiftDown(list, 0, indexLast - 1);

        return min;
    }

    /**
     * Добавляем число в кучу.
     *
     * @param list просеиваемая куча
     * @param index индекс для нового числа
     * @param number добавляемое число
     */

    public static void insert(int[] list, int index, int number) {

        list[index] = number;
        shiftUp(list, index);

    }

    /**
     * Главная функция пирамидальной сортировки.
     *
     * @param list неотсортированный массив чисел
     * @return отсортированный массив чисел
     */

    public static int[] heapSort(int[] list) {

        int[] minHeap = new int[list.length];
        int[] result = new int[list.length];

        for (int i = 0; i < list.length; i++) {
            insert(minHeap, i, list[i]);
        }

        for (int i = 0; i < list.length; i++) {
            result[i] = extractMin(minHeap, list.length - i - 1);
        }

        return result;

    }

    /**
     * Тестирование функции пирамидальной сортировки.
     *
     * @param args аргументы командной строки
     */

    public static void main(String[] args) {

    }
}