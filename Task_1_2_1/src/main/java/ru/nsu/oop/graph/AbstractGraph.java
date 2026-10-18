package ru.nsu.oop.graph;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import ru.nsu.oop.graph.io.GraphFileReader;

/**
 * Базовый абстрактный класс ориентированного графа, реализующий общие операции:
 * хранение списка вершин, чтение из файла, топологическую сортировку,
 * сравнение графов и преобразование в строку.
 *
 * @param <T> тип значений вершин графа
 */
public abstract class AbstractGraph<T> implements Graph<T> {
    /**
     * Список вершин графа.
     */
    protected final List<Vertex<T>> list = new ArrayList<>();

    /**
     * Создаёт пустой абстрактный граф.
     */
    protected AbstractGraph() {
    }

    /**
     * Возвращает индекс вершины в списке или выбрасывает исключение, если её нет.
     *
     * @param vertex искомая вершина
     * @return индекс вершины в списке
     * @throws IllegalArgumentException если вершина отсутствует в графе
     */
    protected int vertexIndex(Vertex<T> vertex) {
        int index = list.indexOf(vertex);
        if (index == -1) {
            throw new IllegalArgumentException("Вершина отсутствует в графе");
        }
        return index;
    }

    @Override
    public List<Vertex<T>> topologicalSorting() {
        List<Vertex<T>> sort = new ArrayList<>();
        List<Vertex<T>> vertices = new ArrayList<>(this.vertices());

        while (!vertices.isEmpty()) {
            List<Vertex<T>> zeroDegree = new ArrayList<>();

            for (int i = 0; i < vertices.size(); i += 1) {
                Vertex<T> candidate = vertices.get(i);
                boolean hasIncomingEdge = false;

                for (int j = 0; j < vertices.size(); j += 1) {
                    Vertex<T> from = vertices.get(j);
                    if (neighbors(from).contains(candidate)) {
                        hasIncomingEdge = true;
                        break;
                    }
                }

                if (!hasIncomingEdge) {
                    zeroDegree.add(candidate);
                }
            }

            if (zeroDegree.isEmpty()) {
                throw new IllegalStateException(
                        "Граф содержит цикл, топологическая сортировка невозможна"
                );
            }

            sort.addAll(zeroDegree);
            vertices.removeAll(zeroDegree);
        }

        return sort;
    }

    @Override
    public void readFile(Path path, Function<String, T> valueParser) throws IOException {
        GraphFileReader<T> reader = new GraphFileReader<>();
        reader.read(this, path, valueParser);
    }

    @Override
    public List<Vertex<T>> vertices() {
        return List.copyOf(list);
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Graph<?>)) {
            return false;
        }

        Graph<T> other = (Graph<T>) obj;

        Set<Vertex<T>> thisVertices = new HashSet<>(this.vertices());
        Set<Vertex<T>> otherVertices = new HashSet<>(other.vertices());

        if (!thisVertices.equals(otherVertices)) {
            return false;
        }

        for (Vertex<T> vertex : thisVertices) {
            Set<Vertex<T>> thisNeighbors = new HashSet<>(this.neighbors(vertex));
            Set<Vertex<T>> otherNeighbors = new HashSet<>(other.neighbors(vertex));

            if (!thisNeighbors.equals(otherNeighbors)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = 0;

        for (Vertex<T> vertex : list) {
            result += Objects.hash(vertex, new HashSet<>(neighbors(vertex)));
        }

        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (Vertex<T> vertex : list) {
            sb.append(vertex.getName())
                    .append(" (")
                    .append(vertex.getValue())
                    .append("): ")
                    .append(neighbors(vertex))
                    .append("\n");
        }

        return sb.toString();
    }
}