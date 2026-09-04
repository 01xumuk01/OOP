package ru.nsu.oop;

public class Main {

    /**
     * Просеивает вверх.
     *
     * @param list просеиваемая куча
     * @param index индекс просеимаевого числа
     */

    public static void shiftup(int[] list, int index) {

        if (index == 0) {
            return;
        }

        if (list[(index - 1) / 2] > list[index]) {
            int tmp = list[index];
            list[index] = list[(index - 1) / 2];
            list[(index - 1) / 2] = tmp;

            shiftup(list, (index - 1) / 2);

            return;
        }

    }

    /**
     * Просеивает вниз.
     *
     * @param list просеиваемая куча
     * @param index индекс просеимаевого числа
     * @param index_last индекс последнего числа кучи
     */

    public static void shiftdown(int[] list, int index, int index_last) {

        if (index * 2 + 1 > index_last) {
            return;
        }

        if (index * 2 + 2 > index_last) {
            if (list[index] > list[index * 2 + 1]) {
                int tmp = list[index];
                list[index] = list[index * 2 + 1];
                list[index * 2 + 1] = tmp;
                shiftdown(list, index * 2 + 1, index_last);
            }

            return;
        }

        if (list[index] > list[index * 2 + 1]) {
            int tmp = list[index];
            list[index] = list[index * 2 + 1];
            list[index * 2 + 1] = tmp;
            shiftdown(list, index * 2 + 1, index_last);

            if (list[index] > list[index * 2 + 2]) {
                tmp = list[index];
                list[index] = list[index * 2 + 2];
                list[index * 2 + 2] = tmp;
                shiftdown(list, index * 2 + 2, index_last);
            }

        }
        else {
            if (list[index] > list[index * 2 + 2]) {
                int tmp = list[index];
                list[index] = list[index * 2 + 2];
                list[index * 2 + 2] = tmp;
                shiftdown(list, index * 2 + 2, index_last);
            }
        }

        return;

    }

    /**
     * Возвращает минимальное число из кучи, при этом удаляя его и перераспределяя кучу заново.
     *
     * @param list просеиваемая куча
     * @param index_last индекс последнего числа кучи
     * @return минимальное число из кучи
     */

    public static int extractmin(int[] list, int index_last) {

        int min = list[0];

        if (index_last == 0) {
            return min;
        }

        list[0] = list[index_last];
        shiftdown(list, 0, index_last - 1);

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
        shiftup(list, index);

    }

    /**
     * Главная функция пирамидальной сортировки.
     *
     * @param list неотсортированный массив чисел
     */

    public static void heapsort(int[] list) {

        int[] sorted_list = new int[list.length];

        for (int i = 0; i < list.length; i++) {
            insert(sorted_list, i, list[i]);
        }

        System.out.print("[");

        for (int i = 0; i < list.length - 1; i++) {
            System.out.print(extractmin(sorted_list, list.length - i - 1) + ", ");
        }

        if (list.length != 0) {
            System.out.print(sorted_list[0]);
        }

        System.out.print("]\n");

    }

    /**
     * Тестирование функции пирамидальной сортировки.
     *
     */

    public static void main(String[] args) {

        // Граничные размеры (0, 1 и 2 элемента)
        heapsort(new int[]{});
        heapsort(new int[]{42});
        heapsort(new int[]{2, 1});
        heapsort(new int[]{1, 2});

        // Различные варианты исходного порядка
        heapsort(new int[]{1, 2, 3, 4, 5, 6, 7});
        heapsort(new int[]{7, 6, 5, 4, 3, 2, 1});

        // Дубликаты и повторяющиеся элементы
        heapsort(new int[]{5, 5, 5, 5, 5});
        heapsort(new int[]{3, 1, 2, 3, 1, 2, 3});
        heapsort(new int[]{8, 4, 5, 7, 11, 7, 7, 0, -5});

        // Отрицательные значения и ноль
        heapsort(new int[]{-10, -3, -50, -1, 0, -4});
        heapsort(new int[]{-5, 5, -4, 4, -3, 3, -2, 2, -1, 1, 0});

        // Нечетное и четное количество элементов (проверка крайних листьев дерева)
        heapsort(new int[]{9, 3, 7, 1, 8, 2});
        heapsort(new int[]{9, 3, 7, 1, 8, 2, 5});

        // Экстремальные значения диапазона типов
        heapsort(new int[]{0, Integer.MAX_VALUE, -1, Integer.MIN_VALUE, 42});

    }
}