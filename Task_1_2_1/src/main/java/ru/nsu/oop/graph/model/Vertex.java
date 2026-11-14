package ru.nsu.oop.graph.model;

/**
 * Вершина графа, содержащая уникальное имя и пользовательское значение.
 *
 * @param <T> тип значения вершины
 */
public class Vertex<T> {
    private final String name;
    private final T value;

    /**
     * Создаёт новую вершину с заданным именем и значением.
     *
     * @param name  имя вершины
     * @param value значение вершины
     */
    public Vertex(String name, T value) {
        this.name = name;
        this.value = value;
    }

    /**
     * Возвращает имя вершины.
     *
     * @return имя вершины
     */
    public String getName() {
        return this.name;
    }

    /**
     * Возвращает значение вершины.
     *
     * @return значение вершины
     */
    public T getValue() {
        return this.value;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Vertex<?> vertex)) {
            return false;
        }

        return name.equals(vertex.getName());
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return this.name;
    }
}