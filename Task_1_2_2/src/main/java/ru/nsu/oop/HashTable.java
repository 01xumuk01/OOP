package ru.nsu.oop;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * Хеш-таблица, хранящая пары ключ–значение.
 * Коллизии разрешаются методом цепочек. При превышении допустимой
 * заполненности количество корзин увеличивается в полтора раза.
 * Ключи не могут быть {@code null}; значения могут быть {@code null}.
 *
 * @param <K> тип ключей
 * @param <V> тип значений
 */
public class HashTable<K, V> implements Iterable<Node<K, V>> {
    private static final double MAX_LOADFACTOR = 0.75;

    private final ArrayList<Node<K, V>> buckets;
    private int size;
    private int capacity;
    private double loadFactor;
    private int modCount;

    /**
     * Создаёт пустую хеш-таблицу с начальной ёмкостью 16 корзин.
     */
    public HashTable() {
        this.buckets = new ArrayList<>(16);
        this.size = 0;
        this.capacity = 16;
        this.loadFactor = 0;
        this.modCount = 0;
        for (int i = 0; i < capacity; i++) {
            buckets.add(null);
        }
    }

    /**
     * Возвращает количество пар в таблице.
     *
     * @return количество хранимых пар
     */
    public int size() {
        return size;
    }

    /**
     * Возвращает значение, связанное с указанным ключом.
     *
     * @param key искомый ключ
     * @return значение либо {@code null}, если ключ отсутствует
     *         или с ним связано значение {@code null}
     * @throws NullPointerException если ключ равен {@code null}
     */
    public V get(K key) {
        int index = Math.floorMod(key.hashCode(), capacity);
        Node<K, V> current = buckets.get(index);
        while (current != null) {
            if (current.getKey().equals(key)) {
                return current.getValue();
            }
            current = current.getNext();
        }
        return null;
    }

    /**
     * Добавляет пару ключ–значение.
     * Если ключ уже присутствует, заменяет его значение.
     *
     * @param key добавляемый ключ
     * @param value значение, связываемое с ключом
     * @throws NullPointerException если ключ равен {@code null}
     */
    public void put(K key, V value) {
        int index = Math.floorMod(key.hashCode(), capacity);
        Node<K, V> newNode = new Node<>(key, value, null);

        if (buckets.get(index) == null) {
            buckets.set(index, newNode);
        } else {
            Node<K, V> last = buckets.get(index);
            while (last.getNext() != null) {
                if (last.getKey().equals(key)) {
                    last.setValue(value);
                    return;
                }
                last = last.getNext();
            }
            if (last.getKey().equals(key)) {
                last.setValue(value);
                return;
            }
            last.setNext(newNode);
        }

        size += 1;
        modCount += 1;
        loadFactor = (double) size / capacity;
        if (loadFactor > MAX_LOADFACTOR) {
            this.expansion();
        }
    }

    /**
     * Удаляет пару с указанным ключом.
     * Если ключ отсутствует, таблица сохраняет прежнее состояние.
     *
     * @param key удаляемый ключ
     * @throws NullPointerException если ключ равен {@code null}
     */
    public void remove(K key) {
        int index = Math.floorMod(key.hashCode(), capacity);
        Node<K, V> current = buckets.get(index);
        Node<K, V> previous = null;
        while (current != null) {
            if (current.getKey().equals(key)) {
                if (previous == null) {
                    buckets.set(index, current.getNext());
                } else {
                    previous.setNext(current.getNext());
                }
                size -= 1;
                modCount += 1;
                loadFactor = (double) size / capacity;
                return;
            }

            previous = current;
            current = current.getNext();
        }
    }

    /**
     * Проверяет наличие ключа независимо от связанного с ним значения.
     *
     * @param key проверяемый ключ
     * @return {@code true}, если ключ присутствует в таблице
     * @throws NullPointerException если ключ равен {@code null}
     */
    public boolean containsKey(K key) {
        int index = Math.floorMod(key.hashCode(), capacity);
        Node<K, V> current = buckets.get(index);
        while (current != null) {
            if (current.getKey().equals(key)) {
                return true;
            }
            current = current.getNext();
        }
        return false;
    }

    /**
     * Обновляет значение существующего ключа.
     *
     * @param key ключ обновляемой пары
     * @param value новое значение
     * @throws IllegalArgumentException если ключ отсутствует
     * @throws NullPointerException если ключ равен {@code null}
     */
    public void update(K key, V value) {
        if (!containsKey(key)) {
            throw new IllegalArgumentException("Ключ отсутствует");
        }

        put(key, value);
    }

    /**
     * Увеличивает ёмкость таблицы примерно в полтора раза
     * и перераспределяет узлы между корзинами.
     * Количество пар сохраняется. Созданные ранее итераторы
     * после расширения обнаруживают изменение таблицы.
     */
    public void expansion() {
        int newCapacity = capacity + capacity / 2;
        ArrayList<Node<K, V>> newBuckets = new ArrayList<>(newCapacity);
        for (int i = 0; i < newCapacity; i++) {
            newBuckets.add(null);
        }
        for (int i = 0; i < capacity; i++) {
            Node<K, V> current = buckets.get(i);
            while (current != null) {
                Node<K, V> next = current.getNext();
                int index = Math.floorMod(current.getKey().hashCode(), newCapacity);
                current.setNext(newBuckets.get(index));
                newBuckets.set(index, current);
                current = next;
            }
        }
        buckets.clear();
        buckets.addAll(newBuckets);
        capacity = newCapacity;
        loadFactor = (double) size / capacity;
        modCount += 1;
    }

    /**
     * Создаёт независимый итератор по парам таблицы.
     * Итератор возвращает копии узлов без связей с другими узлами.
     * Добавление ключа, удаление пары или расширение таблицы
     * приводит к исключению при последующем обращении к итератору.
     *
     * @return новый итератор по парам ключ–значение
     */
    @Override
    public Iterator<Node<K, V>> iterator() {
        return new HashIterator();
    }

    /**
     * Итератор, последовательно обходящий корзины и цепочки узлов.
     */
    private class HashIterator implements Iterator<Node<K, V>> {
        private final int expectedModCount;
        private int bucketIndex;
        private Node<K, V> nextNode;

        /**
         * Сохраняет счётчик изменений и находит первый узел для выдачи.
         */
        private HashIterator() {
            expectedModCount = modCount;
            bucketIndex = 0;
            nextNode = null;
            findNextBucket();
        }

        /**
         * Находит следующую непустую корзину, если текущая цепочка закончилась.
         */
        private void findNextBucket() {
            while (nextNode == null && bucketIndex < capacity) {
                nextNode = buckets.get(bucketIndex);
                bucketIndex += 1;
            }
        }

        /**
         * Проверяет, сохраняется ли структура таблицы с момента создания итератора.
         *
         * @throws ConcurrentModificationException если структура таблицы изменилась
         */
        private void checkModification() {
            if (expectedModCount != modCount) {
                throw new ConcurrentModificationException(
                        "Во время итерации хеш-таблица была изменена!");
            }
        }

        /**
         * Проверяет наличие следующей пары, сохраняя позицию обхода.
         *
         * @return {@code true}, если остались элементы
         * @throws ConcurrentModificationException если структура таблицы изменилась
         */
        @Override
        public boolean hasNext() {
            checkModification();
            return nextNode != null;
        }

        /**
         * Возвращает следующую пару и продвигает позицию обхода.
         * Ключ и значение передаются в новый узел без копирования самих объектов.
         *
         * @return копия следующего узла со ссылкой {@code next}, равной {@code null}
         * @throws ConcurrentModificationException если структура таблицы изменилась
         * @throws NoSuchElementException если элементы закончились
         */
        @Override
        public Node<K, V> next() {
            checkModification();
            if (nextNode == null) {
                throw new NoSuchElementException("Элементы закончились");
            }

            Node<K, V> current = nextNode;
            nextNode = nextNode.getNext();
            findNextBucket();
            return new Node<>(current.getKey(), current.getValue(), null);
        }
    }

    /**
     * Проверяет наличие пары с указанными ключом и значением.
     *
     * @param key проверяемый ключ
     * @param value проверяемое значение
     * @return {@code true}, если такая пара присутствует
     */
    private boolean containsEntry(Object key, Object value) {
        int index = Math.floorMod(key.hashCode(), capacity);
        Node<K, V> current = buckets.get(index);
        while (current != null) {
            if (current.getKey().equals(key)) {
                return Objects.equals(current.getValue(), value);
            }
            current = current.getNext();
        }
        return false;
    }

    /**
     * Сравнивает таблицы по содержащимся в них парам.
     * Ёмкость и порядок расположения элементов на результат не влияют.
     *
     * @param obj сравниваемый объект
     * @return {@code true}, если объект является таблицей с теми же парами
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof HashTable<?, ?> other) || size != other.size) {
            return false;
        }

        for (Node<K, V> head : buckets) {
            Node<K, V> current = head;
            while (current != null) {
                if (!other.containsEntry(current.getKey(), current.getValue())) {
                    return false;
                }
                current = current.getNext();
            }
        }
        return true;
    }

    /**
     * Вычисляет хеш-код по всем парам таблицы.
     * Результат не зависит от ёмкости и порядка расположения элементов.
     *
     * @return хеш-код таблицы
     */
    @Override
    public int hashCode() {
        int result = 0;
        for (Node<K, V> head : buckets) {
            Node<K, V> current = head;
            while (current != null) {
                result += Objects.hashCode(current.getKey())
                        ^ Objects.hashCode(current.getValue());
                current = current.getNext();
            }
        }
        return result;
    }

    /**
     * Возвращает строку с парами таблицы, разделёнными запятыми.
     * Порядок пар определяется их расположением в корзинах.
     *
     * @return строковое представление таблицы в фигурных скобках
     */
    @Override
    public String toString() {
        StringJoiner result = new StringJoiner(", ", "{", "}");
        for (Node<K, V> head : buckets) {
            Node<K, V> current = head;
            while (current != null) {
                result.add(current.getKey() + "=" + current.getValue());
                current = current.getNext();
            }
        }
        return result.toString();
    }
}