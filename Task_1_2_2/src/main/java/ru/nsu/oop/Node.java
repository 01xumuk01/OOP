package ru.nsu.oop;

/**
 * Узел цепочки хеш-таблицы, содержащий ключ, значение
 * и ссылку на следующий узел.
 *
 * @param <K> тип ключа
 * @param <V> тип значения
 */
public class Node<K, V> {
    private final K key;
    private V value;
    private Node<K, V> next;

    /**
     * Создаёт узел с указанными ключом, значением и следующим узлом.
     *
     * @param key ключ узла
     * @param value значение узла
     * @param next следующий узел или {@code null}
     */
    public Node(K key, V value, Node<K, V> next) {
        this.key = key;
        this.value = value;
        this.next = next;
    }

    /**
     * Устанавливает ссылку на следующий узел.
     *
     * @param next следующий узел или {@code null}, если цепочка заканчивается
     */
    public void setNext(Node<K, V> next) {
        this.next = next;
    }

    /**
     * Заменяет значение узла.
     *
     * @param value новое значение
     */
    public void setValue(V value) {
        this.value = value;
    }

    /**
     * Возвращает ключ узла.
     *
     * @return ключ узла
     */
    public K getKey() {
        return key;
    }

    /**
     * Возвращает значение узла.
     *
     * @return значение узла
     */
    public V getValue() {
        return value;
    }

    /**
     * Возвращает следующий узел цепочки.
     *
     * @return следующий узел или {@code null}, если текущий узел последний
     */
    public Node<K, V> getNext() {
        return next;
    }

    /**
     * Возвращает строковое представление пары ключ–значение.
     *
     * @return строка вида {@code ключ -> значение}
     */
    @Override
    public String toString() {
        return key + " -> " + value;
    }
}